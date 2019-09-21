package com.restapi.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.stereotype.Service;

@Service
public class ValidatorUtil {
	public boolean verifyEmail(String email) {
		boolean isEmail = false;
		
		//Email pattern (test@test.com , test@test.co.in)
		Pattern emailPattern = Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]+$");
		
		//Match input email string with email pattern and return boolean
		Matcher matcher = emailPattern.matcher(email);
		isEmail = matcher.find();
		
		return isEmail;
	}
	public boolean verifyPassword(String password) {
		boolean isPassword = false;
		
		//Checks if password contains minimum eight characters, at least one uppercase letter, one lowercase letter, one number and one special character
		Pattern passwordPattern = Pattern.compile("^(?=.*?[A-Z])(?=(.*[a-z]){1,})(?=(.*[\\d]){1,})(?=(.*[\\W_]){1,})(?!.*\\s).{8,}$");
		
		Matcher m = passwordPattern.matcher(password);
		isPassword = m.find();
		
		return isPassword;
	}
	
}
