package com.ecom.controller;

import java.security.Principal;
import java.util.List;

import org.hibernate.engine.jdbc.Size;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.ecom.model.Cart;
import com.ecom.model.Category;
import com.ecom.model.UserDetails1;
import com.ecom.service.CartService;
import com.ecom.service.CategoryService;
import com.ecom.service.UserService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/user")
public class UserController {

	@Autowired
	private UserService userService;

	@Autowired
	private CategoryService categoryService;

	@Autowired
	private CartService cartService;

	@ModelAttribute
	public void getUserDetails(Principal p, Model m) {
		if (p != null) {
			String email = p.getName();
			UserDetails1 userDetail = userService.getUserByEmail(email);
			m.addAttribute("user", userDetail);
			Integer countCart = cartService.getCountCart(userDetail.getId());
			m.addAttribute("cartCount", countCart);
		}
		List<Category> category = categoryService.getAllActiveCategory();
		m.addAttribute("category", category);
	}

	// add to cart
	@GetMapping("/addCart")
	public String addCart(@RequestParam int pid, @RequestParam int uid, HttpSession session) {
		Cart saveCart = cartService.saveCart(pid, uid);

		if (ObjectUtils.isEmpty(saveCart)) {
			session.setAttribute("errormsg", "product add to cart failed!!!");
		} else {
			session.setAttribute("success", "product added to cart!!");
		}
		return "redirect:/viewproduct/" + pid;
	}

	@GetMapping("/")
	public String home() {
		return "user/home";
	}

	@GetMapping("/cart")
	public String loadCartPage(Principal p, Model m) {
		UserDetails1 user = getLoggedInDetails(p);
		List<Cart> carts = cartService.getCartByUser(user.getId());
		m.addAttribute("carts", carts);
		if(carts.size()>0) {
		double totalOrderPrice = carts.get(carts.size()-1).getTotalOrderPrice();
		m.addAttribute("totalOrderPrice1",totalOrderPrice);
		}
		return "user/cart";
	}

	
	private UserDetails1 getLoggedInDetails(Principal p) {
		String email = p.getName();
		UserDetails1 userDtl = userService.getUserByEmail(email);
		return userDtl;
	} 
	
	@GetMapping("/cartQuantityUpdate")
	public String updateCartQuantity(@RequestParam String sy,@RequestParam int cid) 
	{
		cartService.updateQuantity(sy, cid);
		return "redirect:/user/cart";
	} 
	
}
