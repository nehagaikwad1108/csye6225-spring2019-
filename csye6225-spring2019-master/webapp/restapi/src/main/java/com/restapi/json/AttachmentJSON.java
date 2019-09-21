package com.restapi.json;

import com.restapi.model.Attachment;

public class AttachmentJSON {

	private String id;
	private String fileName;
	private String fileType;

	public AttachmentJSON(Attachment attachment) {
		this.id = attachment.getId();
		this.fileName = attachment.getFileName();
		this.fileType = attachment.getFileType();
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getFileType() {
		return fileType;
	}

	public void setFileType(String fileType) {
		this.fileType = fileType;
	}

}
