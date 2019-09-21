package com.restapi.services;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.NoResultException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.restapi.daos.NoteDAO;
import com.restapi.daos.UserDAO;
import com.restapi.json.NoteJson;
import com.restapi.model.Note;
import com.restapi.model.User;
import com.restapi.response.ApiResponse;

@Service
public class NoteService {
	@Autowired
	UserDAO userDAO;

	@Autowired
	NoteDAO noteDao;

	private static final Logger logger = LoggerFactory.getLogger(AttachmentService.class);

	public ResponseEntity<Object> addNewNote(String username, Note note) {
		User user = this.userDAO.getUser(username);
		Note newNote = new Note();
		newNote.setContent(note.getContent());
		newNote.setTitle(note.getTitle());
		Date currentDate = new Date();
		newNote.setCreatedOn(currentDate);
		newNote.setLastUpdatedOn(currentDate);
		newNote.setCreatedBy(user);
		user.getNotes().add(newNote);
		newNote = this.noteDao.saveNote(newNote);
		return new ResponseEntity<Object>(new NoteJson(newNote), HttpStatus.CREATED);
	}

	public ResponseEntity<Object> updateExistingNote(Note note1, String username, String id) {
		Note n1;
		User user = this.userDAO.getUser(username);
		try {
			n1 = this.noteDao.getNote(id);
		} catch (NoResultException e) {
			ApiResponse response = new ApiResponse(HttpStatus.BAD_REQUEST, "Note does not exist.",
					"Note does not exist.");
			logger.error("No note exists with id : " + id);
			return new ResponseEntity<Object>(response, HttpStatus.BAD_REQUEST);
		}
		if (!n1.getCreatedBy().getUsername().equals(username)) {
			ApiResponse response = new ApiResponse(HttpStatus.UNAUTHORIZED, "User is not authorized to update the note",
					"User is not authorized to update the note");
			logger.info("User " + username + " not authorized to add attachment to note with note ID: " + id);
			return new ResponseEntity<Object>(response, HttpStatus.UNAUTHORIZED);
		}
		/*
		 * for(Note n : user.getNotes()) { if(n.getId() == id) {
		 * n.setTitle(note1.getTitle()); n.setContent(note1.getContent()); } }
		 */
		this.noteDao.updateNote(note1, id);
		return new ResponseEntity<Object>(null, HttpStatus.NO_CONTENT);

	}

	public ResponseEntity<Object> getNotes(String username) {

		User user = this.userDAO.getUser(username);
		List<NoteJson> notes = new ArrayList<NoteJson>();
		for (Note not : user.getNotes())
			notes.add(new NoteJson(not));

		if (notes.isEmpty()) {
			logger.info("No note exists for user  ");
			ApiResponse resp = new ApiResponse(HttpStatus.NOT_FOUND,
					"The requested resource could not be found for the user", "Resource not available");
			return new ResponseEntity<Object>(resp, HttpStatus.NOT_FOUND);
		} else {
			return new ResponseEntity<Object>(notes, HttpStatus.OK);
		}
	}

	public ResponseEntity<Object> getNoteById(String username, String id) {

		Note note;
		try {
			note = this.noteDao.getNote(id);
		} catch (NoResultException e) {
			logger.error("No note exists with id : " + id);
			ApiResponse resp = new ApiResponse(HttpStatus.NOT_FOUND, "The requested resource could not be found",
					"Resource not available");
			return new ResponseEntity<Object>(resp, HttpStatus.NOT_FOUND);
		}
		if (!note.getCreatedBy().getUsername().equals(username)) {
			logger.info("User not authorized to operate on Note");
			ApiResponse resp = new ApiResponse(HttpStatus.UNAUTHORIZED,
					"The requested resource not authorized for the user", "Resource not available");
			return new ResponseEntity<Object>(resp, HttpStatus.UNAUTHORIZED);
		} else {
			return new ResponseEntity<Object>(new NoteJson(note), HttpStatus.OK);
		}
	}

	public ResponseEntity<Object> deleteExistingNote(String username, String id) {
		// check if note exists
		boolean noteExists = this.checkIfNoteExists(id);
		if (!noteExists) {
			logger.info("No note exists with id : " + id);
			ApiResponse apiError = new ApiResponse(HttpStatus.BAD_REQUEST, "Note does not exist",
					"Note does not exist");
			return new ResponseEntity<Object>(apiError, HttpStatus.BAD_REQUEST);
		}

		// get note owner
		long noteOwnerId = this.getNoteOwner(id);
		User user = this.userDAO.getUser(username);

		// check if note owner matches with user
		boolean checkIfNoteBelongsToUserFlag = this.checkIfNoteBelongsToUser(user, noteOwnerId);
		if (!checkIfNoteBelongsToUserFlag) {
			logger.info("User not authorized to operate on Note");
			ApiResponse apiError = new ApiResponse(HttpStatus.UNAUTHORIZED, "Note does not belong to user",
					"Note does not belong to user");
			return new ResponseEntity<Object>(apiError, HttpStatus.UNAUTHORIZED);
		}

		// delete the note
		this.noteDao.deleteNote(id);
		// ApiResponse apiError = new ApiResponse(HttpStatus.NO_CONTENT, "Note Deleted",
		// "Success");
		return new ResponseEntity<Object>(null, HttpStatus.NO_CONTENT);
	}

	public boolean checkIfNoteBelongsToUser(User user, long noteOwnerId) {
		if (Long.compare(noteOwnerId, user.getId()) == 0)
			return true;
		else
			return false;
	}

	public Long getNoteOwner(String id) {
		Note note = this.noteDao.getNoteFromId(id);
		return note.getCreatedBy().getId();
	}

	public boolean checkIfNoteExists(String id) {
		Note note = this.noteDao.getNoteFromId(id);
		if (note == null)
			return false;
		else
			return true;
	}

}
