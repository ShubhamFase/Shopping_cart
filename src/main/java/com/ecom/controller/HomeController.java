package com.ecom.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

	@GetMapping("/")
	public String home() 
	{
		return"index";
	}
	@GetMapping("/login")
	public String loginPage() 
	{
		return"login";
	}
	@GetMapping("/register")
	public String registerPage() 
	{
		return"register";
	}
	@GetMapping("/product")
	public String productrPage() 
	{
		return"product";
	}
	@GetMapping("/viewproduct")
	public String view_product()
	{
		return "view_product";
	}
}
