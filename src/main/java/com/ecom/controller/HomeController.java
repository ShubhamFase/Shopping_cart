package com.ecom.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

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
}
