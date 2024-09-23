package com.ecom.service;

import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Service
public class CommonServiceImpl implements CommonService {

	@Override
	public void removeSessionMsg() {
		HttpServletRequest request=((ServletRequestAttributes) (RequestContextHolder.getRequestAttributes())).getRequest();
		HttpSession session=request.getSession();
		session.removeAttribute("success");
		session.removeAttribute("errormsg");
		
		
	}

}
