package com.restapi.daos;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.restapi.model.User;

@Service
public class UserDAO {
	@PersistenceContext
	private EntityManager entityManager;
	
	private static final Logger logger = LoggerFactory.getLogger(UserDAO.class);

	@Transactional
	public User saveUser(User user) 
	{
		logger.info("Saving user object to database");
		return this.entityManager.merge(user);
	}

	public User getUser(String username) 
	{
		logger.info("Getting user data from database for user ");
		TypedQuery<User> query = this.entityManager.createQuery("SELECT c from User c where c.username = ?1",
				User.class);
		query.setParameter(1, username);
		return query.getSingleResult();
	}

	public String getStoredPasswordFromUser(String email) 
	{
		logger.info("Getting stored password from user");
		String hashed_pw = "";
		try {
			Query query = this.entityManager.createQuery("SELECT u FROM User u WHERE u.username = ?1");
			query.setParameter(1, email);
			List<User> resultList = query.getResultList();
			hashed_pw = resultList.get(0).getPassword();

		} catch (Exception e) {
			// System.out.println("caught exception in hashed_pw::");
			logger.error(e.toString());
			hashed_pw = null;

		}

		// System.out.println("Returning hashed_pw::"+hashed_pw);
		return hashed_pw;
	}

	public int checkIfUserExists(String email) 
	{
		logger.info("Checking if user exists");
		// System.out.println("Email is :"+email);
		int result = 0;
		try {
			Query query = this.entityManager.createQuery("SELECT COUNT(u) FROM User u WHERE u.username = ?1");
			query.setParameter(1, email);
			Long resultInLong = (Long) query.getSingleResult();
			// System.out.println("resultInLong:"+resultInLong);
			result = Math.toIntExact(resultInLong);
		} catch (Exception e) {
			// System.out.println("Exception in checkIfUserExists:"+e.getMessage());
			logger.error(e.toString());
			result = 0;
		}

		// System.out.println("Returning count of user::"+result);
		return result;
	}
}
