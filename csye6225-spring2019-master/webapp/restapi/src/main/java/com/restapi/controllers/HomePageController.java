package com.restapi.controllers;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.restapi.metrics.StatMetric;
import com.restapi.response.ApiResponse;

@RestController
public class HomePageController {
	
	
	private static final Logger logger = LoggerFactory.getLogger(HomePageController.class);
	
	@Autowired
	StatMetric statMetric;
	
	@RequestMapping(value = "/", method = { RequestMethod.GET })
	public ResponseEntity<Object> showDate(/* @RequestHeader("Authorization") String bearerToken */) {

		//logger.info("Get call for Home Page");
		statMetric.increementStat("GET /");
		
		
		String message = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		ApiResponse apiResponse;
		if (message != null && message.contentEquals("User not logged in")) {
			logger.error("The user is not logged in");
			apiResponse = new ApiResponse(HttpStatus.UNAUTHORIZED, "You are unauthorized to access the requested resource.", "User not logged in");
			return new ResponseEntity<Object>(apiResponse, HttpStatus.UNAUTHORIZED);
		} else if (message != null && message.contentEquals("Username not entered")) {
			logger.error("The username is not entered");
			apiResponse = new ApiResponse(HttpStatus.FORBIDDEN, "You are not authorized to access the requested resource.", "Username not entered");
			return new ResponseEntity<Object>(apiResponse, HttpStatus.FORBIDDEN);
		} else if (message != null && message.contentEquals("Password not entered")) {
			logger.error("The password is not entered");
			apiResponse = new ApiResponse(HttpStatus.FORBIDDEN, "You are not authorized to access the requested resource.", "Password not entered");
			return new ResponseEntity<Object>(apiResponse, HttpStatus.FORBIDDEN);
		} else if (message != null && message.contentEquals("Username does not exist")) {
			logger.error("The username does not exist");
			apiResponse = new ApiResponse(HttpStatus.FORBIDDEN, "You are not authorized to access the requested resource.", "Username does not exist");
			return new ResponseEntity<Object>(apiResponse, HttpStatus.FORBIDDEN);
		} else if (message != null && message.contentEquals("Invalid Credentials")) {
			logger.error("The user has entered invalid credentials");
			apiResponse = new ApiResponse(HttpStatus.UNAUTHORIZED, "You are unauthorized to access the requested resource.", "Invalid credentials");
			return new ResponseEntity<Object>(apiResponse, HttpStatus.UNAUTHORIZED);
		} else {
			apiResponse = new ApiResponse(HttpStatus.OK, new Date().toString(), "NA");
			return new ResponseEntity<Object>(apiResponse, new HttpHeaders(), HttpStatus.OK);
		}

	}
}