package com.ecom.service.impl;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.ecom.model.UserDetails1;
import com.ecom.repository.UserRepository;
import com.ecom.service.UserService;
import com.ecom.util.AppConstant;

@Service
public class UserServiceImpl implements UserService {

	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private PasswordEncoder passwordEncoder; 

	public UserDetails1 saveUser(UserDetails1 userDetails) {
		userDetails.setRole("ROLE_USER");
		userDetails.setEnable(true);
		userDetails.setAccountNonLocked(true);
		userDetails.setFailedAttempt(0);
		userDetails.setLockTime(null);
		String encodePassword=passwordEncoder.encode(userDetails.getPassword());
		userDetails.setPassword(encodePassword);
		UserDetails1 saveUser = userRepository.save(userDetails);
		return saveUser;
	}

	@Override
	public UserDetails1 getUserByEmail(String email) {
		
		return userRepository.findByEmail(email);
	}

	@Override
	public List<UserDetails1> getUsers(String role) 
	{
	
		return userRepository.findByRole(role);
	}

	@Override
	public boolean updateAccountStatus(int id, boolean status) {
		Optional<UserDetails1> findUsers= userRepository.findById(id);
		
		if(findUsers.isPresent()) 
		{
			UserDetails1 userDetails = findUsers.get();
			userDetails.setEnable(status);
			userRepository.save(userDetails);
			return true;
		}
		return false;
	}

	@Override
	public void increaseFailedAttempt(UserDetails1 user) {
		int attempt = user.getFailedAttempt()+1;
		user.setFailedAttempt(attempt);
		userRepository.save(user);
		
	}

	@Override
	public void userAccountLocked(UserDetails1 user) {
         user.setAccountNonLocked(false);
         user.setLockTime(new Date());
         userRepository.save(user);
	}

	@Override
	public boolean unlockAccountTimeExpired(UserDetails1 user) {
		long lockTime=user.getLockTime().getTime();
		long unlockTime=lockTime+AppConstant.UNLOCK_DURATION_TIME;
		
		long currentTimeMillis = System.currentTimeMillis();
		
		if(unlockTime<currentTimeMillis) 
		{
			user.setAccountNonLocked(true);
			user.setFailedAttempt(0);
			user.setLockTime(null);
			userRepository.save(user);
			return true;
			
		}
		return false;
	}

	
}
