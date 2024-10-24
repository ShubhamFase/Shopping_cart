package com.ecom.service.impl;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.ecom.model.UserDetails1;
import com.ecom.repository.UserRepository;
import com.ecom.service.UserService;
import com.ecom.util.AppConstant;

import jakarta.servlet.http.HttpSession;

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

	@Override
	public void updateUserResetToken(String email, String resetToken) {
		UserDetails1 findByEmail = userRepository.findByEmail(email);
		findByEmail.setResetToken(resetToken);
		userRepository.save(findByEmail);
	}

	@Override
	public UserDetails1 getUserByToken(String token) {
		
		return userRepository.findByResetToken(token);
	}

	@Override
	public UserDetails1 updatePassword(UserDetails1 user) {
		UserDetails1 savePassword = userRepository.save(user);
		return savePassword;
	}

	@Override
	public UserDetails1 userUpdateProfile(UserDetails1 user,MultipartFile img) {
	    UserDetails1 dbUser = userRepository.findById(user.getId()).get();
	    
	    if(!img.isEmpty()) 
	    {
	    	dbUser.setProfileImage(img.getOriginalFilename());
	    }
	    if(!ObjectUtils.isEmpty(dbUser)) 
	    {
	    	dbUser.setName(user.getName());
	    	dbUser.setMobileNumber(user.getMobileNumber());
	    	dbUser.setEmail(user.getEmail());
	    	dbUser.setAddress(user.getAddress());
	    	dbUser.setCity(user.getCity());
	    	dbUser.setState(user.getState());
	    	dbUser.setPincode(user.getPincode());
	    	userRepository.save(dbUser);   
	    }
	    try {
	    if(!img.isEmpty()) 
		{
			File saveFile = new ClassPathResource("static/img/").getFile();
			Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + "profile_img" + File.separator
					+ img.getOriginalFilename());

			Files.copy(img.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
			
		}
	    }catch(Exception e)
	    {
	    	e.printStackTrace();
	    }
		return dbUser;
	}
    
	
	
	
}
