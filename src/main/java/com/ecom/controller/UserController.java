package com.ecom.controller;

import java.io.UnsupportedEncodingException;
import java.security.Principal;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.ecom.model.Cart;
import com.ecom.model.Category;
import com.ecom.model.OrderRequest;
import com.ecom.model.ProductOrder;
import com.ecom.model.UserDetails1;
import com.ecom.service.CartService;
import com.ecom.service.CategoryService;
import com.ecom.service.OrderService;
import com.ecom.service.UserService;
import com.ecom.util.CommonUtil;
import com.ecom.util.OrderStatus;

import jakarta.mail.MessagingException;
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

	@Autowired
	private OrderService orderService;

	@Autowired
	private CommonUtil commonUtil;
	
	@Autowired
	private PasswordEncoder passwordEncoder;

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
		if (carts.size() > 0) {
			double totalOrderPrice = carts.get(carts.size() - 1).getTotalOrderPrice();
			m.addAttribute("totalOrderPrice1", totalOrderPrice);
		}
		return "user/cart";
	}

	private UserDetails1 getLoggedInDetails(Principal p) {
		String email = p.getName();
		UserDetails1 userDtl = userService.getUserByEmail(email);
		return userDtl;
	}

	@GetMapping("/cartQuantityUpdate")
	public String updateCartQuantity(@RequestParam String sy, @RequestParam int cid) {
		cartService.updateQuantity(sy, cid);
		return "redirect:/user/cart";
	}

	// get order and totalorderprice to view page
	@GetMapping("/orders")
	public String Orders(Principal p, Model m) {
		UserDetails1 user = getLoggedInDetails(p);
		List<Cart> carts = cartService.getCartByUser(user.getId());
		m.addAttribute("carts", carts);
		if (carts.size() > 0) {
			double orderPrice = carts.get(carts.size() - 1).getTotalOrderPrice();
			double totalOrderPrice = carts.get(carts.size() - 1).getTotalOrderPrice() + 250 + 100;
			m.addAttribute("orderPrice", orderPrice);
			m.addAttribute("totalOrderPrice1", totalOrderPrice);
		}
		return "user/order";
	}

	// save biling address and payment type from user
	@PostMapping("/saveOrder")
	public String saveOrder(@ModelAttribute OrderRequest request, Principal p) throws UnsupportedEncodingException, MessagingException {
		UserDetails1 user = getLoggedInDetails(p);
		orderService.saveOrder(user.getId(), request);
		return "redirect:/user/success1";
	}

	// confirm order
	@GetMapping("/success1")
	public String loadSuccess() {
		return "user/success";
	}

	// view order
	@GetMapping("/viewOrder")
	public String viewOrder(Principal p, Model m) {
		UserDetails1 loginUser = getLoggedInDetails(p);
		List<ProductOrder> orders = orderService.getOrderByUser(loginUser.getId());
		m.addAttribute("order", orders);
		return "user/myorder";
	}

	// cancel product
	@GetMapping("/updateOrderStatus")
	public String updateOrderStatus(@RequestParam Integer id, @RequestParam Integer st, HttpSession session) {

		OrderStatus[] values = OrderStatus.values();
		String status = null;
		for (OrderStatus orderstatus : values) {
			if (orderstatus.getId().equals(st)) {
				status = orderstatus.getName();
			}
		}
		ProductOrder updateOrder = orderService.orderStatus(id, status);

		try {
			commonUtil.sendMailForProductOrder(updateOrder, status);
		} catch (UnsupportedEncodingException | MessagingException e) {

			e.printStackTrace();
		}

		if (!ObjectUtils.isEmpty(updateOrder)) {
			session.setAttribute("success", "status updated!!");
		} else {
			session.setAttribute("errormsg", "status not updated!!");
		}
		return "redirect:/user/viewOrder";
	}
	
	@GetMapping("/profile")
	public String profile() 
	{
		return "user/profile";
	}
	
	@PostMapping("/updateProfile")
	public String userUpdateProfile(UserDetails1 user,@RequestParam MultipartFile img,HttpSession session) 
	{
		UserDetails1 userUpdateProfile = userService.userUpdateProfile(user, img);
		
		if(ObjectUtils.isEmpty(userUpdateProfile)) 
		{
			session.setAttribute("errormsg", "profile not updated!!");
		}else 
		{
			session.setAttribute("success", "profile updated!!");
		}
		return "redirect:/user/profile";
	}
	// change user password from user profile page
	@PostMapping("/changePassword")
	public String changePassword(@RequestParam String newPassword,@RequestParam String currentPassword,Principal p,HttpSession session) 
	{
		UserDetails1 loginUserDetails = getLoggedInDetails(p);
		boolean matches = passwordEncoder.matches(currentPassword, loginUserDetails.getPassword());
		if(matches) 
		{
			String encodePassword = passwordEncoder.encode(newPassword);
			loginUserDetails.setPassword(encodePassword);
		     UserDetails1 updateUser = userService.updatePassword(loginUserDetails);
			if(ObjectUtils.isEmpty(updateUser)) 
			{
				session.setAttribute("errormsg", "Password not updated !! Error in server");
			}else
			{
				session.setAttribute("success", "Password updated succesfully!!");
			}
		}else 
		{
			session.setAttribute("errormsg", "Current Password Incorrect!!");
		}
		return "redirect:/user/profile";
	}
	
}
