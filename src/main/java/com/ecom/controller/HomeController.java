package com.ecom.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.ecom.model.Category;
import com.ecom.model.Product;
import com.ecom.service.CategoryService;
import com.ecom.service.ProductService;

@Controller
public class HomeController {

	@Autowired
	private CategoryService categoryService;
	
	@Autowired
	private ProductService productSevice;
	
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
	public String productrPage(Model m) 
	{
		List<Category> categories = categoryService.getAllActiveCategory();
		List<Product> products = productSevice.getAllActiveProduct();
		m.addAttribute("categories", categories);
		m.addAttribute("products", products);
		return"product";
	}
	@GetMapping("/viewproduct")
	public String view_product()
	{
		return "view_product";
	}
}
