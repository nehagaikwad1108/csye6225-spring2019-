package com.restapi.daos;

import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.restapi.model.Attachment;
import com.restapi.model.Note;
import com.restapi.services.NoteService;

@Service
public class NoteDAO {

	@PersistenceContext
	private EntityManager entityManager;
	
	@Value("${cloud.bucketName}")
	private String bucketName;
	
	@Value("${cloud.islocal}")
	private boolean islocal;
	
	private static final Logger logger = LoggerFactory.getLogger(NoteDAO.class);

	public Note getNote(String id) 
	{
		logger.info("Getting note from ID : " + id);
		TypedQuery<Note> query = this.entityManager.createQuery("SELECT n from Note n where n.id = ?1",
				Note.class);
		query.setParameter(1, id);
		return query.getSingleResult();
	}
	
	@Transactional
	public Note saveNote(Note note) 
	{
		logger.info("Saving note into Database ");
		this.entityManager.persist(note);
		return note;
	}
	
	@Transactional
	public void deleteNote(String id) 
	{
		logger.info("Deleting note from Database");
		Note noteToBeDeleted = this.entityManager.find(Note.class, id);
		
		//System.out.println("Deleting notes attached to note");
		List<Attachment> attachments = getAttachmentFromNote(noteToBeDeleted);
		
		for (Attachment attachment : attachments)
		{
			deleteAttachment(attachment.getId());
		}
		//System.out.println("DONE deleting all attachments");
		
		//System.out.println("Finally Deleting note");
		this.entityManager.remove(noteToBeDeleted);
		flushAndClear();
	}
	
	public List<Attachment> getAttachmentFromNote(Note note) 
	{
		logger.info("Getting list of attachment from note");
		TypedQuery<Attachment> query = this.entityManager.createQuery("SELECT a from Attachment a where a.note = ?1",
				Attachment.class);
		query.setParameter(1, note);
		return query.getResultList();
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
			//flushAndClear();
		}
	}
	
	public boolean deleteFromMemory(Attachment attachmentToBeDeleted) 
	{
		logger.debug("Deleting attachment from memory");
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
		logger.debug("Deleting from DB");
		Attachment attachmentToBeDeleted = this.entityManager.find(Attachment.class, id);
		try {
			this.entityManager.remove(attachmentToBeDeleted);
			//flushAndClear();
		} catch (Exception e) {
			//e.printStackTrace();
			logger.error(e.toString());
		}

	}
	
	public Note getNoteFromId(String id) 
	{
		logger.info("Getting note from ID : " + id);
		Note noteToBeDeleted = this.entityManager.find(Note.class, id);
		return noteToBeDeleted;
	}
	
	private void flushAndClear() {
	    this.entityManager.flush();
	    this.entityManager.clear();
	}
	
	@Transactional
	public Note updateNote(Note note,String id)
	{
		logger.info("Updating note using new note object");
		  Note noteToBeUpdated = this.entityManager.find(Note.class, id);
		  //Write code to update the note object here and then merge changes
		  noteToBeUpdated.setTitle(note.getTitle());
		  noteToBeUpdated.setContent(note.getContent());
		  Date currentDate = new Date();
		  noteToBeUpdated.setLastUpdatedOn(currentDate);
		  flushAndClear();
		  return noteToBeUpdated;
					
	}
	
	
}
