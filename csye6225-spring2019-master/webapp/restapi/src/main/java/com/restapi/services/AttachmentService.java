package com.restapi.services;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.restapi.controllers.HomePageController;
import com.restapi.daos.AttachmentDAO;
import com.restapi.daos.NoteDAO;
import com.restapi.json.AttachmentJSON;
import com.restapi.model.Attachment;
import com.restapi.model.Note;
import com.restapi.response.ApiResponse;

@Service
public class AttachmentService {

	@Autowired
	NoteDAO noteDAO;

	@Autowired
	AttachmentDAO attachmentDAO;

	private static final Logger logger = LoggerFactory.getLogger(AttachmentService.class);

	public ResponseEntity<Object> addAttachmenttoNote(String username, String noteId, MultipartFile file) {
		AttachmentJSON attachmentJSON = null;
		ApiResponse apiResponse = null;
		try {
			Note note = this.noteDAO.getNoteFromId(noteId);
			if (note == null) {
				apiResponse = new ApiResponse(HttpStatus.NOT_FOUND, "Note not found", "Note not found");
				logger.error("No note found for note id " + noteId);
				return new ResponseEntity<Object>(apiResponse, HttpStatus.NOT_FOUND);
			} else if (!note.getCreatedBy().getUsername().equals(username)) {
				apiResponse = new ApiResponse(HttpStatus.UNAUTHORIZED, "Resource not owned by user",
						"Resource not owned by user");
				logger.error("User " + username + " not authorized to add attachment to note with note ID: " + noteId);
				return new ResponseEntity<Object>(apiResponse, HttpStatus.UNAUTHORIZED);
			} else {
				attachmentJSON = new AttachmentJSON(this.attachmentDAO.saveAttachment(file, note));
				logger.info("Attachment created");
			}

		} catch (Exception e) {
			logger.error(e.toString());
		}

		return new ResponseEntity<Object>(attachmentJSON, HttpStatus.CREATED);
	}

	public ResponseEntity<Object> getAttachmenttoNote(String username, String noteId) {
		List<AttachmentJSON> attachmentJSON = new ArrayList<AttachmentJSON>();
		ApiResponse apiResponse = null;
		try {
			Note note = this.noteDAO.getNoteFromId(noteId);
			if (note == null) {
				apiResponse = new ApiResponse(HttpStatus.NOT_FOUND, "Note not found", "Note not found");
				logger.error("No note found for id " + noteId);
				return new ResponseEntity<Object>(apiResponse, HttpStatus.NOT_FOUND);
			} else if (!note.getCreatedBy().getUsername().equals(username)) {
				apiResponse = new ApiResponse(HttpStatus.UNAUTHORIZED, "Resource not owned by user",
						"Resource not owned by user");
				logger.error("User " + username + " not authorized to get attachment for note with note ID: " + noteId);
				return new ResponseEntity<Object>(apiResponse, HttpStatus.UNAUTHORIZED);
			} else {
				for (Attachment at : this.attachmentDAO.getAttachmentFromNote(note))
					attachmentJSON.add(new AttachmentJSON(at));
			}

		} catch (Exception e) {
			logger.error(e.toString());
		}

		return new ResponseEntity<Object>(attachmentJSON, HttpStatus.OK);
	}

	public ResponseEntity<Object> deleteAttachmentToNote(String username, String noteId, String attachmentId) {
		ApiResponse apiResponse = null;
		try {
			Note note = this.noteDAO.getNoteFromId(noteId);
			if (note == null) {
				apiResponse = new ApiResponse(HttpStatus.NOT_FOUND, "Note not found", "Note not found");
				logger.error("No note found for id " + noteId);
				return new ResponseEntity<Object>(apiResponse, HttpStatus.NOT_FOUND);
			} else if (!note.getCreatedBy().getUsername().equals(username)) {
				apiResponse = new ApiResponse(HttpStatus.UNAUTHORIZED, "Resource not owned by user",
						"Resource not owned by user");
				logger.error(
						"User " + username + " not authorized to delete attachment for note with note ID: " + noteId);
				return new ResponseEntity<Object>(apiResponse, HttpStatus.UNAUTHORIZED);
			} else {
				Attachment attachmentToBeDeleted = this.attachmentDAO.getAttachmentFromId(attachmentId);
				if (attachmentToBeDeleted == null) {
					apiResponse = new ApiResponse(HttpStatus.NOT_FOUND, "Attachment not found", "Attachment not found");
					logger.error("No attachment found for id " + attachmentId);
					return new ResponseEntity<Object>(apiResponse, HttpStatus.NOT_FOUND);
				} else {
					// delete actual file from local/S3 bucket
					logger.info("Attachment with id " + attachmentId + " deleted");
					this.attachmentDAO.deleteAttachment(attachmentId);
				}

			}

		} catch (Exception e) {
			logger.error(e.toString());
		}

		return new ResponseEntity<Object>(null, HttpStatus.NO_CONTENT);

	}

	public ResponseEntity<Object> updateAttachmentToNote(String username, String noteId, String attachmentId,
			MultipartFile file) {
		ApiResponse apiResponse = null;
		try {
			Note note = this.noteDAO.getNoteFromId(noteId);
			if (note == null) {
				apiResponse = new ApiResponse(HttpStatus.NOT_FOUND, "Note not found", "Note not found");
				logger.error("No note found for id " + noteId);
				return new ResponseEntity<Object>(apiResponse, HttpStatus.NOT_FOUND);
			} else if (!note.getCreatedBy().getUsername().equals(username)) {
				apiResponse = new ApiResponse(HttpStatus.UNAUTHORIZED, "Resource not owned by user",
						"Resource not owned by user");
				logger.error(
						"User " + username + " not authorized to delete attachment for note with note ID: " + noteId);
				return new ResponseEntity<Object>(apiResponse, HttpStatus.UNAUTHORIZED);
			} else {
				Attachment attachmentToBeUpdated = this.attachmentDAO.getAttachmentFromId(attachmentId);
				if (attachmentToBeUpdated == null) {
					apiResponse = new ApiResponse(HttpStatus.NOT_FOUND, "Attachment not found", "Attachment not found");
					logger.error("No attachment found for id " + attachmentId);
					return new ResponseEntity<Object>(apiResponse, HttpStatus.NOT_FOUND);
				} else {
					this.attachmentDAO.updateAttachment(attachmentId, attachmentToBeUpdated, file, note);
				}

			}

		} catch (Exception e) {
			logger.error(e.toString());
		}

		return new ResponseEntity<Object>(null, HttpStatus.NO_CONTENT);

	}
}
