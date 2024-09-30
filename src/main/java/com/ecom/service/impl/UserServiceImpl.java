package com.ecom.service.impl;

import org.springframework.beans.factory.annotation.Autowired;


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
		String encodePassword=passwordEncoder.encode(userDetails.getPassword());
		userDetails.setPassword(encodePassword);
		UserDetails1 saveUser = userRepository.save(userDetails);
		return saveUser;
	}

}
