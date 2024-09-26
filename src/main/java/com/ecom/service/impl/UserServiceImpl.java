package com.ecom.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ecom.model.UserDetails;
import com.ecom.repository.UserRepository;
import com.ecom.service.UserService;

@Service
public class UserServiceImpl implements UserService {

	@Autowired
	private UserRepository userRepository;
	@Override
	public UserDetails saveUser(UserDetails userDetails) {
		UserDetails saveUser = userRepository.save(userDetails);
		return saveUser;
	}

}
