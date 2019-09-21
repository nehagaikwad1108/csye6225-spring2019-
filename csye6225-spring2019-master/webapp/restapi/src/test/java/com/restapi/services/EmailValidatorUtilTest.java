package com.restapi.services;

import org.junit.Assert;
import org.junit.Test;

import com.restapi.util.ValidatorUtil;



public class EmailValidatorUtilTest {
	
	ValidatorUtil emailValidator = new ValidatorUtil();
	
	@Test
	
	public void testVerifyEmail()
	{
		boolean validEmail = emailValidator.verifyEmail("Test@gmail.com");
		 Assert.assertTrue(validEmail);
	}
	

}
