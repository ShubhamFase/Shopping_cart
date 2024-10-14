package com.ecom.config;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import com.ecom.model.UserDetails1;
import com.ecom.repository.UserRepository;
import com.ecom.service.UserService;
import com.ecom.util.AppConstant;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class AuthFailureHandlerImpl extends SimpleUrlAuthenticationFailureHandler 
{
	@Autowired
	private UserRepository userRepository;

	@Autowired
	private UserService userService;
	@Override
	public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException exception) throws IOException, ServletException {
	
		String email= request.getParameter("username");
		UserDetails1 userDetail1=userRepository.findByEmail(email);
		if(userDetail1!=null) {
		if(userDetail1.isEnable()) 
		{
			if(userDetail1.isAccountNonLocked()) 
			{
				if(userDetail1.getFailedAttempt()<AppConstant.ATTEMPT_TIME) 
				{
					userService.increaseFailedAttempt(userDetail1);
				}else 
				{
					userService.userAccountLocked(userDetail1);
					exception=new LockedException("You are account is Locked !! failed 3 attempt ");
				}
				  
			}else
			{
				if(userService.unlockAccountTimeExpired(userDetail1)) 
				{
					exception=new LockedException("You are account is Unlocked !! Plz try to login ");
				}
				else 
				{
					exception=new LockedException("You are account is Locked !! Please try after sometimes");
				}
				
			} 
		}else 
		{  
			exception=new LockedException("You are account is Inactive");
		}
		}else 
		{
			exception=new LockedException("Invalid email & password!!!");
		}
		super.setDefaultFailureUrl("/signin?error");
		super.onAuthenticationFailure(request, response, exception);
	}

	
}
