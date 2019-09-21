package com.restapi.daos;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.AmazonSNSClientBuilder;
import com.amazonaws.services.sns.model.PublishRequest;
import com.amazonaws.services.sns.model.PublishResult;

@Service
public class PasswordResetDAO 
{
	@PersistenceContext
	private EntityManager entityManager;
	
	@Value("${cloud.snsTopic}")
	private String snsTopic;
	
	private static String topicArn = "";
	
	private static final Logger logger = LoggerFactory.getLogger(PasswordResetDAO.class);
	
	public void sendEmailToUser (String email)
	{
		logger.info("Sending mail to user using topic arn:::"+snsTopic);
		
		topicArn = snsTopic;
		
		AmazonSNS snsClient = AmazonSNSClientBuilder.defaultClient();
		
		
		final String msg = email;
		final PublishRequest publishRequest = new PublishRequest(topicArn, msg);
		final PublishResult publishResponse = snsClient.publish(publishRequest);
		
		logger.info("MessageId: " + publishResponse.getMessageId());

	}
	
	public int checkIfUserExists(String email) 
	{
		logger.info("Checking if user exists::"+email);
		int result = 0;
		try {
			Query query = this.entityManager.createQuery("SELECT COUNT(u) FROM User u WHERE u.username = ?1");
			query.setParameter(1, email);
			Long resultInLong = (Long) query.getSingleResult();
			result = Math.toIntExact(resultInLong);
		} catch (Exception e) {
			logger.error(e.toString());
			result = 0;
		}

		return result;
	}

}
