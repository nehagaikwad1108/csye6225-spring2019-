package com.restapi.daos;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.InstanceProfileCredentialsProvider;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.restapi.json.AttachmentJSON;
import com.restapi.model.Attachment;
import com.restapi.model.Note;
import com.restapi.response.ApiResponse;
import com.restapi.services.AttachmentService;

@Service
public class AttachmentDAO {

	private static String UPLOADED_FOLDER = System.getProperty("user.dir") + "//attachments//";
	private static final Logger logger = LoggerFactory.getLogger(AttachmentDAO.class);

	@PersistenceContext
	private EntityManager entityManager;

	@Value("${cloud.islocal}")
	private boolean islocal;

	@Value("${cloud.bucketName}")
	private String bucketName;

	@Transactional
	public Attachment saveAttachment(MultipartFile file, Note note) {
		//System.out.println(this.islocal);
		
		if (this.islocal) {
			logger.info("Application running on dev environment");
			return this.saveAttachmentToLocal(file, note);
		} else {
			logger.info("Application running on cloud environment");
			return this.saveAttachmentToS3Bucket(file, note);
		}

	}

	public List<Attachment> getAttachmentFromNote(Note note) 
	{
		logger.info("Getting all attachments from note");
		TypedQuery<Attachment> query = this.entityManager.createQuery("SELECT a from Attachment a where a.note = ?1",
				Attachment.class);
		query.setParameter(1, note);
		return query.getResultList();
	}

	private Attachment saveAttachmentToLocal(MultipartFile file, Note note) 
	{
		logger.debug("Saving attachments to local");
		Attachment attachment = null;
		String filename;
		try {
			Files.createDirectories(Paths.get(UPLOADED_FOLDER));
			String fileNameWithOutExt = FilenameUtils.removeExtension(file.getOriginalFilename());
			filename = fileNameWithOutExt + "_" + new Date().getTime() + "."
					+ FilenameUtils.getExtension(file.getOriginalFilename());
			// System.out.println("filename::"+filename);

			Path path = Paths.get(UPLOADED_FOLDER + filename);
			Files.write(path, file.getBytes());
			attachment = new Attachment(path.toString(), file.getContentType(), note);
			this.entityManager.persist(attachment);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.toString());
		}
		return attachment;
	}

	private Attachment saveAttachmentToS3Bucket(MultipartFile file, Note note) 
	{
		logger.debug("Saving attachments to S3 bucket");
		Attachment attachment = null;
		String filename;
		try {
			String fileNameWithOutExt = FilenameUtils.removeExtension(file.getOriginalFilename());
			filename = fileNameWithOutExt + "_" + new Date().getTime() + "."
					+ FilenameUtils.getExtension(file.getOriginalFilename());
			AmazonS3 s3Client = AmazonS3ClientBuilder.defaultClient();
			File tempFile = this.convert(file);
			s3Client.putObject(new PutObjectRequest(this.bucketName, filename, tempFile));
			String path = s3Client.getUrl(this.bucketName, filename).toString();
//			String path = s3Client
//					.generatePresignedUrl(bucketName, filename, new Date(System.currentTimeMillis() + 5 * 60 * 1000))
//					.toString();
			tempFile.delete();
			logger.debug("File path : " + path);
			logger.debug("Bucket Name : " + bucketName);
			attachment = new Attachment(path, file.getContentType(), note);
			this.entityManager.persist(attachment);
		} catch (Exception e) 
		{
			logger.error(e.toString());
		}
		return attachment;
	}

	public Attachment getAttachmentFromId(String id) 
	{
		logger.info("Getting attachment from attachment ID : " + id);
		Attachment attachmentToBeDeleted = this.entityManager.find(Attachment.class, id);
		return attachmentToBeDeleted;
	}

	@Transactional
	public void deleteAttachment(String id) {
		if (this.islocal) {
			logger.info("Application running on dev environment");
			this.deleteAttachmentFromLocal(id);
		} else {
			logger.info("Application running on cloud environment");
			this.deleteAttachmentFromS3Bucket(id);
		}

	}

	@Transactional
	public void deleteAttachmentFromLocal(String id) 
	{
		logger.debug("Deleting attachment from local");
		Attachment attachmentToBeDeleted = this.entityManager.find(Attachment.class, id);
		boolean successfullyDeleted = deleteFromMemory(attachmentToBeDeleted);
		if (successfullyDeleted) {
			this.entityManager.remove(attachmentToBeDeleted);
			flushAndClear();
		}
	}

	private void flushAndClear() {
		this.entityManager.flush();
		this.entityManager.clear();
	}

	@Transactional
	public void deleteAttachmentFromS3Bucket(String id) 
	{
		logger.debug("Deleting attachment from S3 bucket");
		Attachment attachmentToBeDeleted = this.entityManager.find(Attachment.class, id);
		String entirePath = attachmentToBeDeleted.getFileName();
		String filename = entirePath.substring(entirePath.lastIndexOf("/") + 1);
		try {
			AmazonS3 s3Client = AmazonS3ClientBuilder.defaultClient();
			s3Client.deleteObject(this.bucketName, filename);
		} catch (Exception e) {
			//e.printStackTrace();
			logger.error(e.toString());
		}

		deleteFromDB(id);
	}

	@Transactional
	public void deleteFromDB(String id) 
	{
		logger.info("Deleting attachment from Database");
		Attachment attachmentToBeDeleted = this.entityManager.find(Attachment.class, id);
		try {
			this.entityManager.remove(attachmentToBeDeleted);
			flushAndClear();
		} catch (Exception e) {
			//e.printStackTrace();
			logger.error(e.toString());
		}

	}

	public boolean deleteFromMemory(Attachment attachmentToBeDeleted) 
	{
		logger.debug("Deleting attachment from local");
		String path = attachmentToBeDeleted.getFileName();
		System.out.println(path);
		try {
			java.io.File fileToBeDeleted = new java.io.File((path));
			if (fileToBeDeleted.delete()) {
				return true;
			} else {
				return false;
			}
		} catch (Exception e) {
			//e.printStackTrace();
			logger.error(e.toString());
			return false;
		}

	}

	@Transactional
	public Attachment updateAttachment(String id, Attachment attachment, MultipartFile file, Note note) {
		if (this.islocal) {
			logger.info("Application running on dev environment");
			return this.updateAttachmentFromLocal(id, attachment, file, note);
		} else {
			logger.info("Application running on cloud environment");
			return this.updateAttachmentFromS3Bucket(id, attachment, file, note);
		}
	}

	@Transactional
	public Attachment updateAttachmentFromLocal(String id, Attachment attachment, MultipartFile file, Note note) 
	{
		logger.debug("Updating attachment from local");
		// delete actual file from local
		boolean successfullyDeleted = deleteFromMemory(attachment);

		if (successfullyDeleted) {
			saveAttachmentToLocalMemory(file, note);
		}

		Attachment attachmentToBeUpdated1 = null;
		if (successfullyDeleted) {
			// update entry from DB
			String fileNameWithOutExt = FilenameUtils.removeExtension(file.getOriginalFilename());
			String filename = fileNameWithOutExt + "_" + new Date().getTime() + "."
					+ FilenameUtils.getExtension(file.getOriginalFilename());
			Path path = Paths.get(UPLOADED_FOLDER + filename);
			attachment = new Attachment(path.toString(), file.getContentType(), note);
			attachmentToBeUpdated1 = updateInDB(id, attachment);

		}

		return attachmentToBeUpdated1;
	}

	private void saveAttachmentToLocalMemory(MultipartFile file, Note note) 
	{
		logger.debug("Saving attachment to local");
		String filename = "";
		try {
			Files.createDirectories(Paths.get(UPLOADED_FOLDER));
			String fileNameWithOutExt = FilenameUtils.removeExtension(file.getOriginalFilename());
			filename = fileNameWithOutExt + "_" + new Date().getTime() + "."
					+ FilenameUtils.getExtension(file.getOriginalFilename());
			Path path = Paths.get(UPLOADED_FOLDER + filename);
			Files.write(path, file.getBytes());
		} catch (Exception e) {
			//e.printStackTrace();
			logger.error(e.toString());
		}
	}

	@Transactional
	public Attachment updateAttachmentFromS3Bucket(String id, Attachment attachment, MultipartFile file, Note note) 
	{
		logger.debug("Updating attachment from S3 bucket");
		// delete actual file from S3bucket
		Attachment attachmentToBeUpdated = this.entityManager.find(Attachment.class, id);
		String entirePath = attachmentToBeUpdated.getFileName();
		String filename = entirePath.substring(entirePath.lastIndexOf("/") + 1);
		try {
			AmazonS3 s3Client = AmazonS3ClientBuilder.defaultClient();
			s3Client.deleteObject(this.bucketName, filename);
		} catch (Exception e) {
			//e.printStackTrace();
			logger.error(e.toString());
		}

		// save new file in S3bucket
		try {
			String fileNameWithOutExt = FilenameUtils.removeExtension(file.getOriginalFilename());
			filename = fileNameWithOutExt + "_" + new Date().getTime() + "."
					+ FilenameUtils.getExtension(file.getOriginalFilename());
			AmazonS3 s3Client = AmazonS3ClientBuilder.defaultClient();
			File tempFile = this.convert(file);
			s3Client.putObject(new PutObjectRequest(this.bucketName, filename, tempFile));
			String path = s3Client.getUrl(this.bucketName, filename).toString();
//			String path = s3Client
//					.generatePresignedUrl(bucketName, filename, new Date(System.currentTimeMillis() + 5 * 60 * 1000))
//					.toString();
			tempFile.delete();
			attachment = new Attachment(path, file.getContentType(), note);
		} catch (Exception e) {
			//e.printStackTrace();
			logger.error(e.toString());
		}

		// update entry from DB
		Attachment attachmentToBeUpdated1 = updateInDB(id, attachment);
		return attachmentToBeUpdated1;
	}

	@Transactional
	public Attachment updateInDB(String id, Attachment attachment) 
	{
		logger.info("Updating attachment in DB");
		Attachment attachmentToBeUpdated1 = this.entityManager.find(Attachment.class, id);
		attachmentToBeUpdated1.setFileName(attachment.getFileName());
		attachmentToBeUpdated1.setFileType(attachment.getFileType());
		flushAndClear();
		return attachmentToBeUpdated1;

	}

	private File convert(MultipartFile file) 
	{
		logger.info("Converting a Multipart File");
		File convFile = new File(file.getOriginalFilename());
		try {
			convFile.createNewFile();
			FileOutputStream fos = new FileOutputStream(convFile);
			fos.write(file.getBytes());
			fos.close();
		} catch (IOException e) {
			//e.printStackTrace();
			logger.error(e.toString());
		}
		return convFile;
	}
}
