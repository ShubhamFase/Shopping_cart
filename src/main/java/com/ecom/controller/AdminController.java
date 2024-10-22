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

import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.ecom.model.Category;
import com.ecom.model.Product;
import com.ecom.model.ProductOrder;
import com.ecom.model.UserDetails1;
import com.ecom.service.CartService;
import com.ecom.service.CategoryService;
import com.ecom.service.CommonService;
import com.ecom.service.CommonServiceImpl;
import com.ecom.service.OrderService;
import com.ecom.service.ProductService;
import com.ecom.service.UserService;
import com.ecom.util.CommonUtil;
import com.ecom.util.OrderStatus;

import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/admin")
public class AdminController {

	@Autowired
	private CategoryService categoryService;

	@Autowired
	private ProductService productService;

	@Autowired
	private UserService userService;
	
	@Autowired
	private CartService cartService;
	
	@Autowired
	private OrderService orderService;
	
	@Autowired
	private CommonUtil commonUtil;

	// we can used for show category and category wise product to home page
	@ModelAttribute
	public void getUserDetails(Principal p, Model m) {
		if (p != null) {
			String email = p.getName();
			UserDetails1 userDetail = userService.getUserByEmail(email);
			m.addAttribute("user", userDetail);
			Integer countCart = cartService.getCountCart(userDetail.getId());
			m.addAttribute("cartCount",countCart);
		}
		List<Category> category = categoryService.getAllActiveCategory();
		m.addAttribute("category", category);
	}

	@GetMapping("/")
	public String index() {
		return "admin/index";
	}

	@GetMapping("/addproduct")
	public String addproduct(Model m) {
		List<Category> allCategories = categoryService.getAllCategory();
		m.addAttribute("categories", allCategories);
		return "admin/add_product";
	}

	@GetMapping("/category")
	public String category(Model m) {
		m.addAttribute("categorys", categoryService.getAllCategory());
		return "admin/category";
	}

	// we can used for save category from admin to database
	@PostMapping("/saveCategory")
	public String saveCategory(@ModelAttribute Category category, @RequestParam("file") MultipartFile file,
			HttpSession session) throws IOException {
		// This is code use for store default image in database.
		String imageName = file != null ? file.getOriginalFilename() : "default.jpg";
		category.setImageName(imageName);

//		 // This code we can use for store default status value in table.
//		if(category.getStatus()==null || category.getStatus().isEmpty()) 
//		{
//			category.setStatus("Inactive");
//		}

		// This is code use for category name present in database then send msg to user.
		boolean existCategory = categoryService.existCategory(category.getName());
		if (existCategory) {
			session.setAttribute("errormsg", "category name already exist!!!!");
		} else {
			Category saveCategory = categoryService.saveCategory(category);
			if (ObjectUtils.isEmpty(saveCategory)) {
				session.setAttribute("errormsg", "Not saved: Internal server error");
			} else {

				// We can used for store image path in our folder
				File saveFile = new ClassPathResource("static/img/").getFile();
				Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + "category_img" + File.separator
						+ file.getOriginalFilename());
				System.out.println(path);
				Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
				session.setAttribute("success", "Save successfully!!!!!");
			}
		}

		return "redirect:/admin/category";
	}

	// we can used for delete id from table
	@GetMapping("/deleteCategory/{id}")
	public String deleteCategory(@PathVariable long id, HttpSession session) {
		Boolean deleteCategory = categoryService.deleteCategory(id);
		if (deleteCategory) {
			session.setAttribute("success", "category delete successfully");
		} else {
			session.setAttribute("errormsg", "Something went wrong");
		}
		return "redirect:/admin/category";
	}

	// show category details to edit page from database
	@GetMapping("/loadEditCategory/{id}")
	public String loadEditCategory(@PathVariable int id, Model m) {
		m.addAttribute("category", categoryService.getCategoryById(id));
		return "admin/edit_category";
	}

	// update category and then save into database
	@PostMapping("/updateCategory")
	public String updateCategory(@ModelAttribute Category category, @RequestParam("file") MultipartFile file,
			HttpSession session) throws IOException {
		Category oldCategory = categoryService.getCategoryById(category.getId());
		String imageName = file.isEmpty() ? oldCategory.getImageName() : file.getOriginalFilename();
		if (!ObjectUtils.isEmpty(category)) {
			oldCategory.setName(category.getName());
			oldCategory.setIsActive(category.getIsActive());
			oldCategory.setImageName(imageName);
		}
		Category updateCategory = categoryService.saveCategory(oldCategory);
		if (!ObjectUtils.isEmpty(updateCategory)) {
			if (!file.isEmpty()) {
				File saveFile = new ClassPathResource("static/img/").getFile();
				Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + "category_img" + File.separator
						+ file.getOriginalFilename());
				System.out.println(path);
				Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
			}
			session.setAttribute("success", "category update sucessfully!!!");

		} else {
			session.setAttribute("errormsg", "something went wrong");
		}
		return "redirect:/admin/loadEditCategory/" + category.getId();
	}

	// we can used for save product in table
	@PostMapping("/saveProduct")
	public String saveCategory(@ModelAttribute Product product, @RequestParam("file") MultipartFile file,
			HttpSession session) throws IOException {

		String imageName = file != null ? file.getOriginalFilename() : "default.jpg";
		product.setImage(imageName);
		product.setDiscount(0);
		product.setDiscountPrice(product.getPrice());
		Product saveProduct = productService.saveProduct(product);
		if (!ObjectUtils.isEmpty(saveProduct)) {

			File saveFile = new ClassPathResource("static/img/").getFile();
			Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + "product_img" + File.separator
					+ file.getOriginalFilename());
			System.out.println(path);
			Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

			session.setAttribute("success", "product saved sucessfully!!!");
		} else {
			session.setAttribute("errormsg", "something went wrong");
		}
		return "redirect:/admin/addproduct";
	}

	// we can used for view all products to user
	@GetMapping("/products")
	public String loadViewProducts(Model m) {
		m.addAttribute("products", productService.getAllProducts());
		return "admin/products";
	}

	// we can used for delete product from table
	@GetMapping("/deleteProduct/{id}")
	public String deleteProduct(@PathVariable int id, HttpSession session) {
		Boolean deleteProduct = productService.deleteProduct(id);
		if (deleteProduct) {
			session.setAttribute("success", "product delete successfully");
		} else {
			session.setAttribute("errormsg", "something went wrong");
		}
		return "redirect:/admin/products";
	}

	// edit product
	@GetMapping("/updateProduct/{id}")
	public String updateProduct(@PathVariable int id, Model m) {
		m.addAttribute("product", productService.getProductById(id));
		m.addAttribute("categories", categoryService.getAllCategory());
		return "admin/edit_product";
	}

	// update product
	@PostMapping("/updateProduct")
	public String updateProduct(@ModelAttribute Product product, @RequestParam("file") MultipartFile image,
			HttpSession session) throws IOException {
		Product oldProduct = productService.getProductById(product.getId());
		String imageName = image.isEmpty() ? oldProduct.getImage() : image.getOriginalFilename();
		oldProduct.setImage(imageName);
		if (!ObjectUtils.isEmpty(product)) {
			oldProduct.setTitle(product.getTitle());
			oldProduct.setDescription(product.getDescription());
			oldProduct.setCategory(product.getCategory());
			oldProduct.setPrice(product.getPrice());
			oldProduct.setStock(product.getStock());
			oldProduct.setIsActive(product.getIsActive());

			oldProduct.setDiscount(product.getDiscount());
			// This is use for discount on product
			Double discount = product.getPrice() * (product.getDiscount() / 100.0);
			Double discountPrice = product.getPrice() - discount;
			oldProduct.setDiscountPrice(discountPrice);
		}
		if (product.getDiscount() < 0 || product.getDiscount() > 100) {
			session.setAttribute("errormsg", "Invalid discount!!");
		} else {
			Product updateProduct = productService.saveProduct(oldProduct);
			if (!ObjectUtils.isEmpty(updateProduct)) {
				if (!image.isEmpty()) {
					File saveFile = new ClassPathResource("static/img/").getFile();
					Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + "product_img" + File.separator
							+ image.getOriginalFilename());
					System.out.println(path);
					Files.copy(image.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
				}
				session.setAttribute("success", "product updated sucessfully!!!");
			} else {
				session.setAttribute("errormsg", "something went wrong");
			}
		}
		return "redirect:/admin/updateProduct/" + product.getId();
	}

	@GetMapping("/users")
	public String getAllUsers(Model m) {
		List<UserDetails1> users = userService.getUsers("ROLE_USER");

		m.addAttribute("users", users);
		return "admin/users";
	}
	
	@GetMapping("/updateAccountstatus")
	public String accountStatus(@RequestParam boolean status,@RequestParam int id,HttpSession session ) 
	{
		boolean updateAccount=userService.updateAccountStatus(id,status);
		
		if(updateAccount) 
		{
			session.setAttribute("success", "Account Status updated sucessfully!!!");
		}
		else 
		{
			session.setAttribute("errormsg", "something went wrong");
		}
		return "redirect:/admin/users";
	}
	
	@GetMapping("/viewOrders")
	public String viewAllOrders(Model m) 
	{
		List<ProductOrder> allOrders = orderService.getAllOrders();
		m.addAttribute("allOrders",allOrders);
		return "admin/Orders";
	}
	@PostMapping("/adminUpdateOrderStatus")
	public String updateOrderStatus(@RequestParam Integer id, @RequestParam Integer st,HttpSession session) {

		OrderStatus[] values = OrderStatus.values();
		String status = null;
		for (OrderStatus orderstatus : values) 
		{
			if (orderstatus.getId().equals(st)) 
			{
				status = orderstatus.getName();
			}
		}
		ProductOrder updateOrder = orderService.orderStatus(id, status);
		try {
			commonUtil.sendMailForProductOrder(updateOrder, status);
		} catch (UnsupportedEncodingException | MessagingException e) {

			e.printStackTrace();
		}
		
		if(!ObjectUtils.isEmpty(updateOrder)) 
		{
			session.setAttribute("success", "status updated!!");
		}else 
		{
			session.setAttribute("errormsg", "status not updated!!");
		}
		return "redirect:/admin/viewOrders";
	}
}
