package com.restapi.controllers;

import javax.validation.constraints.NotNull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.restapi.metrics.StatMetric;
import com.restapi.model.Note;
import com.restapi.response.ApiResponse;
import com.restapi.services.NoteService;

@RestController
public class NoteController {

	@Autowired
	NoteService noteService;
	
	@Autowired
	StatMetric statMetric;
	
	private static final Logger logger = LoggerFactory.getLogger(NoteController.class);
	
	@RequestMapping(value = "/note", method = RequestMethod.POST)
	public ResponseEntity<Object> addNote(@RequestBody Note note) 
	{
		
		statMetric.increementStat("POST /note");
		
		String message = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		ApiResponse errorResponse;
		if (message.equals("Username does not exist") || message.equals("Invalid Credentials") || message.equals("Username not entered") || message.equals("Password not entered")) {
			logger.error("Invalid credentials provided for Add New Note");
			errorResponse = new ApiResponse(HttpStatus.UNAUTHORIZED, message, message);
			return new ResponseEntity<Object>(errorResponse, HttpStatus.UNAUTHORIZED);
		}
		if (StringUtils.isEmpty(note.getTitle())) {
			logger.error("Title is missing");
			errorResponse = new ApiResponse(HttpStatus.BAD_REQUEST, "Please Enter Title", "Please Enter Title");
			return new ResponseEntity<Object>(errorResponse, HttpStatus.BAD_REQUEST);
		}
		if (StringUtils.isEmpty(note.getContent())) {
			logger.error("Content is missing");
			errorResponse = new ApiResponse(HttpStatus.BAD_REQUEST, "Please Enter Content", "Please Enter Content");
			return new ResponseEntity<Object>(errorResponse, HttpStatus.BAD_REQUEST);
		}
		return this.noteService.addNewNote(message, note);
	}
	
	@RequestMapping(value = "/note", method = RequestMethod.GET)
	public ResponseEntity<Object> getNote(){
		statMetric.increementStat("GET /note");
		logger.info("Getting all notes");
		String message = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		
		ApiResponse errorResponse;
		if (message.equals("Username does not exist") || message.equals("Invalid Credentials")|| message.equals("Username not entered") || message.equals("Password not entered")) {
			logger.error("Invalid credentials provided for Get Note");
			errorResponse = new ApiResponse(HttpStatus.UNAUTHORIZED, message, message);
			return new ResponseEntity<Object>(errorResponse, HttpStatus.UNAUTHORIZED);
		}
		
		return this.noteService.getNotes(message);
	}
	
	@RequestMapping(value = "/note/{id}", method = RequestMethod.GET)
	public ResponseEntity<Object> getNoteById(@PathVariable @NotNull String id)
	{
		logger.info("Getting note with id:" + id);
		statMetric.increementStat("GET /note/{noteId}");
		String message = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		
		ApiResponse errorResponse;
		if (message.equals("Username does not exist") || message.equals("Invalid Credentials")|| message.equals("Username not entered") || message.equals("Password not entered")) {
			logger.error("Invalid Credentials provided for Get Note");
			errorResponse = new ApiResponse(HttpStatus.UNAUTHORIZED, message, message);
			return new ResponseEntity<Object>(errorResponse, HttpStatus.UNAUTHORIZED);
		}
		
		return this.noteService.getNoteById(message, id);
	}
	
	@RequestMapping(value = "/note/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<Object> deleteNote(@PathVariable("id") @NotNull String id) 
	{
		logger.info("Deleting note with id:" + id);
		statMetric.increementStat("DELETE /note/{noteId}");
		
		String message = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		ApiResponse errorResponse;
		if (message.equals("Username does not exist") || message.equals("Invalid Credentials")|| message.equals("Username not entered") || message.equals("Password not entered")) {
			logger.error("Inavlid Credentials provided for Delete Note");
			errorResponse = new ApiResponse(HttpStatus.UNAUTHORIZED, message, message);
			return new ResponseEntity<Object>(errorResponse, HttpStatus.UNAUTHORIZED);
		}
		return this.noteService.deleteExistingNote(message, id);
	}


    @RequestMapping(value = "/note/{id}", method = RequestMethod.PUT)
    public ResponseEntity<Object> updateNote(@RequestBody Note note,@PathVariable("id") @NotNull String id)
    {
    	logger.info("Updating note with id:" + id);
    	statMetric.increementStat("PUT /note/{noteId}");
	   
    	String message = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    	ApiResponse errorResponse;
    	if (message.equals("Username does not exist") || message.equals("Invalid Credentials")|| message.equals("Username not entered") || message.equals("Password not entered")) {
    		logger.error("Invalid credentials provided for Update Note");
    		errorResponse = new ApiResponse(HttpStatus.UNAUTHORIZED, message, message);
    		return new ResponseEntity<Object>(errorResponse, HttpStatus.UNAUTHORIZED);
	   } 
	   if (StringUtils.isEmpty(note.getTitle())) {
		   	logger.error("Title is missing in New Note Object");
		   	ApiResponse response = new ApiResponse(HttpStatus.BAD_REQUEST, "Please Enter Title", "Please Enter Title");
			return new ResponseEntity<Object>(response, HttpStatus.BAD_REQUEST);
	   }
	   if (StringUtils.isEmpty(note.getContent())) {
		   	logger.error("Content is missing in New Note Object");
		   	ApiResponse response = new ApiResponse(HttpStatus.BAD_REQUEST, "Please Enter Content", "Please Enter Content");
			return new ResponseEntity<Object>(response, HttpStatus.BAD_REQUEST);
	   }
	   return this.noteService.updateExistingNote(note,message, id);
    }
}
