package com.restapi.services;

import org.junit.Test;

import com.restapi.util.ValidatorUtil;

import org.junit.Assert;


public class ValidatorUtilTest {
	
		
	ValidatorUtil validUtil = new ValidatorUtil();

	@Test
	public void testVerifyPassword() {
		boolean validPassword = validUtil.verifyPassword("Password@123");
	   Assert.assertTrue(validPassword);
	}
	
	@Test
	public void testIncorrectPassword() {
		boolean invalidPassword = validUtil.verifyPassword("password");
		Assert.assertFalse(invalidPassword);
	}
}
