package com.restapi.services;

import javax.persistence.PersistenceException;
import javax.validation.ConstraintViolationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.restapi.daos.UserDAO;
import com.restapi.json.Credentials;
import com.restapi.model.User;
import com.restapi.response.ApiResponse;
import com.restapi.util.BCryptUtil;
import com.restapi.util.ValidatorUtil;

@Service
public class RegisterService {

	@Autowired
	private BCryptUtil bCrptUtil;

	@Autowired
	private UserDAO userDAO;
	
	@Autowired
	private ValidatorUtil validUtil;
	
	@Autowired
	private LoginService loginService;
	
	private static final Logger logger = LoggerFactory.getLogger(RegisterService.class);

	public ResponseEntity<Object> registerUser(Credentials credentials) {
		if(!validUtil.verifyEmail(credentials.getUsername())){
			ApiResponse apiError = new ApiResponse(HttpStatus.BAD_REQUEST, "Invalid syntax for this request was provided.", "Not an valid email address");
			logger.error("Email validation failed");
			return new ResponseEntity<Object>(apiError, HttpStatus.BAD_REQUEST);
		}
		else if(loginService.checkIfUserExists(credentials.getUsername())) {
			ApiResponse apiError = new ApiResponse(HttpStatus.BAD_REQUEST, "Invalid syntax for this request was provided.", "User with same email already exists");
			logger.error("User already exists");
			return new ResponseEntity<Object>(apiError,  HttpStatus.BAD_REQUEST);
		}
		else if (!validUtil.verifyPassword(credentials.getPassword())) {
			ApiResponse apiError = new ApiResponse(HttpStatus.BAD_REQUEST, "Invalid syntax for this request was provided.", "Password must contain minimum 8 characters,atleast one uppercase,lowercase,digit and special character and no whitespaces");
			logger.error("Password validation failed");
			return new ResponseEntity<Object>(apiError, HttpStatus.BAD_REQUEST);
		}
		else {
			User user = new User(credentials.getUsername(),	this.bCrptUtil.generateEncryptedPassword(credentials.getPassword()));
			this.userDAO.saveUser(user);
			ApiResponse apiresponse = new ApiResponse(HttpStatus.OK, "User has been successfully registered", "NA");
			logger.info("User successfully registered");
			return new ResponseEntity<Object>(apiresponse,  HttpStatus.OK);
		}
	}
}
