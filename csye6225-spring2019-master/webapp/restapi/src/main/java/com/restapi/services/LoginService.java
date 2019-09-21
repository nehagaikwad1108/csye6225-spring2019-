package com.restapi.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.restapi.daos.AttachmentDAO;
import com.restapi.daos.UserDAO;
import com.restapi.util.BCryptUtil;

@Service
public class LoginService {
	@Autowired
	private BCryptUtil bCrptUtil;
	
	private static final Logger logger = LoggerFactory.getLogger(LoginService.class);

	@Autowired
	private UserDAO userDAO;
	
	public boolean checkUser(String userName, String password) 
	{
		logger.info("Checking user credentials provided in request");
		// System.out.println("in check user::"+password);
		String hashedPwdFromDB = this.userDAO.getStoredPasswordFromUser(userName); // get stored hash from username from
																					// MYSQL

		if (hashedPwdFromDB != null) {
			if (bCrptUtil.verifyPassword(password, hashedPwdFromDB))
				return true;
			else
				return false;
		} else
			return false;

	}

	public boolean checkIfUserExists(String userName) 
	{
		logger.info("Checking if user exists already exists in database");
		int count = this.userDAO.checkIfUserExists(userName);
		if (count > 0)
			return true;
		else
			return false;

	}

}
