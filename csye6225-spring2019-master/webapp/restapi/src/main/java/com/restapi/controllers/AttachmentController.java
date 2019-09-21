package com.restapi.controllers;

import javax.validation.constraints.NotNull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.restapi.daos.NoteDAO;
import com.restapi.metrics.StatMetric;
import com.restapi.model.Note;
import com.restapi.response.ApiResponse;
import com.restapi.services.AttachmentService;

@RestController
public class AttachmentController {

	@Autowired
	AttachmentService attachmentService;
	
	@Autowired
	StatMetric statMetric;
	
	private static final Logger logger = LoggerFactory.getLogger(AttachmentController.class);

	@RequestMapping(value = "/note/{noteId}/attachments", method = RequestMethod.POST)
	public ResponseEntity<Object> addAttachmentToNote(@PathVariable @NotNull String noteId,
			@RequestParam("file") MultipartFile file) 
	{
		logger.info("Creating attachments for note with id:" +noteId);
		statMetric.increementStat("POST /note/{noteId}/attachments");
		
		String message = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		ApiResponse errorResponse;
		if (message.equals("Username does not exist") || message.equals("Invalid Credentials")|| message.equals("Username not entered") || message.equals("Password not entered")) {
			logger.error("Inavlid credentials provided for Add Note Attachment");
			errorResponse = new ApiResponse(HttpStatus.UNAUTHORIZED, message, message);
			return new ResponseEntity<Object>(errorResponse, HttpStatus.UNAUTHORIZED);
		}
		return this.attachmentService.addAttachmenttoNote(message, noteId, file);
	}
	
	@RequestMapping(value = "/note/{noteId}/attachments", method = RequestMethod.GET	)
	public ResponseEntity<Object> getAttachmentToNote(@PathVariable @NotNull String noteId) 
	{
		logger.info("Getting attachments of note with id:" +noteId);
		statMetric.increementStat("GET /note/{noteId}/attachments");
		
		String message = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		ApiResponse errorResponse;
		if (message.equals("Username does not exist") || message.equals("Invalid Credentials")|| message.equals("Username not entered") || message.equals("Password not entered")) {
			logger.error("Inavlid credentials provided for GET Note Attachment");
			errorResponse = new ApiResponse(HttpStatus.UNAUTHORIZED, message, message);
			return new ResponseEntity<Object>(errorResponse, HttpStatus.UNAUTHORIZED);
		}
		return this.attachmentService.getAttachmenttoNote(message, noteId);
	}
	
	@RequestMapping(value = "/note/{noteId}/attachments/{idAttachments}", method = RequestMethod.DELETE	)
	public ResponseEntity<Object> deleteAttachmentToNote(@PathVariable("noteId") @NotNull String noteId, @PathVariable("idAttachments") @NotNull String attachmentId) {
		
		logger.info("Deleting attachments of note with id:" +noteId);
		statMetric.increementStat("DELETE /note/{noteId}/attachments/{attachmentId}");
		
		String message = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		ApiResponse errorResponse;
		if (message.equals("Username does not exist") || message.equals("Invalid Credentials")|| message.equals("Username not entered") || message.equals("Password not entered")) {
			logger.error("Inavlid credentials provided for Delete Note Attachment");
			errorResponse = new ApiResponse(HttpStatus.UNAUTHORIZED, message, message);
			return new ResponseEntity<Object>(errorResponse, HttpStatus.UNAUTHORIZED);
		}
		return this.attachmentService.deleteAttachmentToNote(message, noteId , attachmentId);
	}
	
	@RequestMapping(value = "/note/{noteId}/attachments/{idAttachments}", method = RequestMethod.PUT	)
	public ResponseEntity<Object> updateAttachmentToNote(@PathVariable("noteId") @NotNull String noteId, @PathVariable("idAttachments") @NotNull String attachmentId, @RequestParam("file") MultipartFile file) {
		
		logger.info("Updating attachments of note with id:" +noteId);
		statMetric.increementStat("PUT /note/{noteId}/attachments/{attachmentId}");
		
		String message = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		ApiResponse errorResponse;
		if (message.equals("Username does not exist") || message.equals("Invalid Credentials")|| message.equals("Username not entered") || message.equals("Password not entered")) {
			logger.error("Inavlid credentials provided for Update Note Attachment");
			errorResponse = new ApiResponse(HttpStatus.UNAUTHORIZED, message, message);
			return new ResponseEntity<Object>(errorResponse, HttpStatus.UNAUTHORIZED);
		}
		return this.attachmentService.updateAttachmentToNote(message, noteId , attachmentId, file);
	}
}
