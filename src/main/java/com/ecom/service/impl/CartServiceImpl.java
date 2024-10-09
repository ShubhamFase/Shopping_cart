package com.ecom.service.impl;

import java.util.ArrayList;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import com.ecom.model.Cart;
import com.ecom.model.Product;
import com.ecom.model.UserDetails1;
import com.ecom.repository.CartRepository;
import com.ecom.repository.ProductRepository;
import com.ecom.repository.UserRepository;
import com.ecom.service.CartService;

@Service
public class CartServiceImpl implements CartService {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private ProductRepository productRepository;

	@Autowired
	private CartRepository cartRepository;

	// logic used for add number of product by using add to cart to user account
	public Cart saveCart(int pid, int uid) {
		Product product = productRepository.findById(pid).get();
		UserDetails1 user = userRepository.findById(uid).get();

		Cart cartStatus = cartRepository.findByProductIdAndUserId(pid, uid);
		Cart cart = null;

		if (ObjectUtils.isEmpty(cartStatus)) {
			cart = new Cart();
			cart.setProduct(product);
			cart.setUser(user);
			cart.setQuantity(1);
			cart.setTotalPrice(1 * product.getDiscountPrice());
		} else {
			cart = cartStatus;
			cart.setQuantity(cart.getQuantity() + 1);
			cart.setTotalPrice(cart.getQuantity() * cart.getProduct().getDiscountPrice());
		}
		Cart saveCart = cartRepository.save(cart);
		return saveCart;
	}

	@Override
	public List<Cart> getCartByUser(int userId) {

		List<Cart> carts = cartRepository.findByUserId(userId);
		Double totalOrderPrice = 0.0;
		List<Cart> updateCart = new ArrayList<>();
		for (Cart c : carts) {
			Double totalPrice = (c.getProduct().getDiscountPrice() * c.getQuantity());
			c.setTotalPrice(totalPrice);
			totalOrderPrice += totalPrice;
			c.setTotalOrderPrice(totalOrderPrice);
			updateCart.add(c);
		}

		return updateCart;
	}

	public Integer getCountCart(int userID) {
		Integer countByUserId = cartRepository.countByUserId(userID);
		return countByUserId;
	}

	@Override
	public void updateQuantity(String sy, int cid) {

		Cart cart = cartRepository.findById(cid).get();
		int updateQuantity;

		if (sy.equalsIgnoreCase("de")) {
			updateQuantity = cart.getQuantity() - 1;

			if (updateQuantity <= 0) {
				cartRepository.delete(cart);
			} else {
				cart.setQuantity(updateQuantity);
				cartRepository.save(cart);
			}
		} else {
			updateQuantity = cart.getQuantity() + 1;
			cart.setQuantity(updateQuantity);
			cartRepository.save(cart);
		}

	}

}
