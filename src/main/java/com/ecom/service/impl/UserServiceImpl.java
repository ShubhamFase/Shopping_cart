package com.ecom.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.ecom.model.UserDetails1;
import com.ecom.repository.UserRepository;
import com.ecom.service.UserService;

@Service
public class UserServiceImpl implements UserService {

	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private PasswordEncoder passwordEncoder; 

	public UserDetails1 saveUser(UserDetails1 userDetails) {
		userDetails.setRole("ROLE_USER");
		userDetails.setEnable(true);
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

}
