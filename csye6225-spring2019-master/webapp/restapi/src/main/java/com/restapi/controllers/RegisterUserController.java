package com.restapi.controllers;



import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.restapi.json.Credentials;
import com.restapi.metrics.StatMetric;
import com.restapi.model.User;
import com.restapi.services.RegisterService;

@RestController
public class RegisterUserController {

	@Autowired
	private RegisterService registerService;

	@Autowired
	StatMetric statMetric;
	
	private static final Logger logger = LoggerFactory.getLogger(RegisterUserController.class);

	@RequestMapping(value = "/user/register", method = RequestMethod.POST)
	public ResponseEntity<Object> registerUser(@Valid @RequestBody  Credentials credentials) {
		
		logger.info("Registering New user");
		statMetric.increementStat("POST /user/register");
		
		return this.registerService.registerUser(credentials);
	}
}
