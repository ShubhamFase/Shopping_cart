package com.ecom.controller;

import java.io.File;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.ecom.model.Category;
import com.ecom.model.Product;
import com.ecom.model.UserDetails1;
import com.ecom.service.CategoryService;
import com.ecom.service.ProductService;
import com.ecom.service.UserService;
import com.ecom.util.CommonUtil;

import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Controller
public class HomeController {

	@Autowired
	private CategoryService categoryService;
	
	@Autowired
	private ProductService productSevice;
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private CommonUtil commonUtil;
	
	@Autowired
	private BCryptPasswordEncoder passwordEncoder;
	
	@ModelAttribute
	public void getUserDetails(Principal p,Model m) 
	{
		if(p!=null) {
		String email= p.getName();
		UserDetails1 userDetail = userService.getUserByEmail(email);
		m.addAttribute("user",userDetail);
		}
		List<Category> category = categoryService.getAllActiveCategory();
		m.addAttribute("category", category);
	}
	
	@GetMapping("/")
	public String home() 
	{
		return"index";
	}
	@GetMapping("/signin")
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
	public String productrPage(Model m,@RequestParam (value = "category", defaultValue="") String category) 
	{
		List<Category> categories = categoryService.getAllActiveCategory();
		List<Product> products = productSevice.getAllActiveProduct(category);
		m.addAttribute("categories", categories);
		m.addAttribute("products", products);
		m.addAttribute("paramValue", category);
		return"product";
	}
	@GetMapping("/viewproduct/{id}")
	public String view_product(@PathVariable int id,Model m)
	{
		Product productById = productSevice.getProductById(id);
		m.addAttribute("product", productById);
		return "view_product";
	}
	
	@PostMapping("/saveUser")
	public String saveUser(@ModelAttribute UserDetails1 userDetails,@RequestParam("file") MultipartFile file,HttpSession session) throws IOException 
	{
		String imageName=file.isEmpty() ? "default.jpg": file.getOriginalFilename();
		userDetails.setProfileImage(imageName);
		UserDetails1 saveUser = userService.saveUser(userDetails);
		if(!ObjectUtils.isEmpty(saveUser)) 
		{
			if(!file.isEmpty()) 
			{
				File saveFile = new ClassPathResource("static/img/").getFile();
				Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + "profile_img" + File.separator
						+ file.getOriginalFilename());
//				System.out.println(path);
				Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

				
			}
			session.setAttribute("success", "User saved sucessfully!!!");
		}
		else 
		{
			session.setAttribute("errormsg", "something went wrong");
		}
		return "redirect:/register";
	} 
	
	@GetMapping("/forgotPassword")
	public String  showForgotPassword() 
	{
		return "forgot_password";
	}
	
	@PostMapping("/processPassword")
	public String  processForgotPassword(@RequestParam String email,HttpSession session,HttpServletRequest request) throws UnsupportedEncodingException, MessagingException 
	{
		UserDetails1 userDetail=userService.getUserByEmail(email);
		
		if(ObjectUtils.isEmpty(userDetail)) 
		{
			session.setAttribute("errormsg", "INVALID EMAIL");
		}
		else 
		{
			String resetToken = UUID.randomUUID().toString();
			userService.updateUserResetToken(email,resetToken);

			// Generate URL= http://localhost:8080/resetPassword?token=ygdcbkseygadcjac 	 
			String url=CommonUtil.generateUrl(request)+"/resetPassword?token="+resetToken;
			Boolean sendMail = commonUtil.sendMail(url, email);
			
			if(sendMail) 
			{
				session.setAttribute("success", "Please check your email....Password reset link sent!!");
			}
			else 
			{
				session.setAttribute("errormsg", "Something wrong on server ! Email not sent.");
			}
		}
		return "redirect:/forgotPassword";
	}
	
	@GetMapping("/resetPassword")
	public String showResetPassword(@RequestParam String token,HttpSession session,Model m) 
	{
		UserDetails1 userByToken = userService.getUserByToken(token);
		
		if(userByToken==null) 
		{
			m.addAttribute("msg", "Your link is invalid or expired");
			return "message";
		}
		m.addAttribute("token",token);
		return "reset_password";
	}
	
	@PostMapping("/resetPassword")
	public String resetPassword(@RequestParam String token,@RequestParam String password,Model m,HttpSession session) 
	{
		UserDetails1 userByToken = userService.getUserByToken(token);
		
		if(userByToken==null) 
		{
			m.addAttribute("msg", "Your link is invalid or expired!!!");
			return "message";
		}
		else 
		{
			userByToken.setPassword(passwordEncoder.encode(password));
			userByToken.setResetToken(null);
			userService.updatePassword(userByToken);
			session.setAttribute("success", "password change sucessfully!!!");
			m.addAttribute("msg","password change sucessfully!!!!");
			return "message";
		}
		
	} 
}
