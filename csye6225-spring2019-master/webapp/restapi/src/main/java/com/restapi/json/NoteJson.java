package com.restapi.json;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.restapi.model.Attachment;
import com.restapi.model.Note;

public class NoteJson {
	private String id;
	private String title;
	private String content;
	private Date createdOn;
	private Date lastUpdatedOn;
	private List<AttachmentJSON> attachments; 
//	private String createdById;

	public NoteJson() {

	}

	public NoteJson(Note note) {
		this.id = note.getId();
		this.title = note.getTitle();
		this.setContent(note.getContent());
		this.createdOn = note.getCreatedOn();
		this.lastUpdatedOn = note.getLastUpdatedOn();
		this.attachments = new ArrayList<AttachmentJSON>();
		for(Attachment attach : note.getAttachments())
			attachments.add(new AttachmentJSON(attach));
			
//		this.createdById = note.getCreatedBy().getUsername();
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getCreatedOn() {
		return new SimpleDateFormat("MM/dd/yyyy HH:mm:ss").format(this.createdOn);
	}

	public void setCreatedOn(Date createdOn) {
		this.createdOn = createdOn;
	}

	public String getLastUpdatedOn() {
		return new SimpleDateFormat("MM/dd/yyyy HH:mm:ss").format(this.lastUpdatedOn);
	}

	public void setLastUpdatedOn(Date lastUpdatedOn) {
		this.lastUpdatedOn = lastUpdatedOn;
	}

//	public String getCreatedById() {
//		return createdById;
//	}
//
//	public void setCreatedById(String createdBy) {
//		this.createdById = createdBy;
//	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public List<AttachmentJSON> getAttachments() {
		return attachments;
	}

	public void setAttachments(List<AttachmentJSON> attachments) {
		this.attachments = attachments;
	}

	
}
