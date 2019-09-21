package com.restapi.services;

import org.junit.Test;
import org.mockito.InjectMocks;
import org.springframework.security.crypto.bcrypt.BCrypt;

import com.restapi.util.BCryptUtil;

import org.junit.Assert;

public class BCryptUtilTest {

	
	BCryptUtil bcryptUtil = new BCryptUtil();
	
	@Test
	public void generateEncryptedPasswordTest() {
		String actualEncryptedPass = this.bcryptUtil.generateEncryptedPassword("abc@123");
		Assert.assertTrue(BCrypt.checkpw("abc@123", actualEncryptedPass));
	}
}
