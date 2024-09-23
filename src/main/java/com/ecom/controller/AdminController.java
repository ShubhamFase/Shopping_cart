package com.ecom.controller;

import java.io.File;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
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
import com.ecom.service.CategoryService;
import com.ecom.service.CommonService;
import com.ecom.service.CommonServiceImpl;
import com.ecom.service.ProductService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/admin")
public class AdminController {

	@Autowired
	private CategoryService categoryService;

	@Autowired
	private ProductService productService;

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
				File saveFile = new ClassPathResource("static/img").getFile();
				Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + "category_img" + File.separator
						+ file.getOriginalFilename());
				System.out.println(path);
				Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
				session.setAttribute("success", "Save successfully!!!!!");
			}
		}

		return "redirect:/admin/category";
	}

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

	@GetMapping("/loadEditCategory/{id}")
	public String loadEditCategory(@PathVariable int id, Model m) {
		m.addAttribute("category", categoryService.getCategoryById(id));
		return "admin/edit_category";
	}

	@PostMapping("/updateCategory")
	public String updateCategory(@ModelAttribute Category category, @RequestParam("file") MultipartFile file,
			HttpSession session) throws IOException {
		Category oldCategory = categoryService.getCategoryById(category.getId());
		String imageName = file.isEmpty() ? oldCategory.getImageName() : file.getOriginalFilename();
		if (!ObjectUtils.isEmpty(category)) {
			oldCategory.setName(category.getName());
			oldCategory.setStatus(category.getStatus());
			oldCategory.setImageName(imageName);
		}
		Category updateCategory = categoryService.saveCategory(oldCategory);
		if (!ObjectUtils.isEmpty(updateCategory)) {
			if (!file.isEmpty()) {
				File saveFile = new ClassPathResource("static/img").getFile();
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

	@PostMapping("/saveProduct")
	public String saveCategory(@ModelAttribute Product product, @RequestParam("file") MultipartFile file,
			HttpSession session) throws IOException {

		String imageName = file != null ? file.getOriginalFilename() : "default.jpg";
		product.setImage(imageName);
		Product saveProduct = productService.saveProduct(product);
		if (!ObjectUtils.isEmpty(saveProduct)) {

			File saveFile = new ClassPathResource("static/img").getFile();
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

	@GetMapping("/products")
	public String loadViewProducts(Model m) {
		m.addAttribute("products", productService.getAllProducts());
		return "admin/products";
	}

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

	@GetMapping("/updateProduct/{id}")
	public String updateProduct(@PathVariable int id, Model m) {
		m.addAttribute("product", productService.getProductById(id));
		m.addAttribute("categories", categoryService.getAllCategory());
		return "admin/edit_product";
	}

	@PostMapping("/updateProduct")
	public String updateProduct(@ModelAttribute Product product,@RequestParam ("file") MultipartFile image,HttpSession session) throws IOException 
	{
		Product oldProduct = productService.getProductById(product.getId());
		String imageName=image.isEmpty() ? oldProduct.getImage() : image.getOriginalFilename();
		oldProduct.setImage(imageName);
		if(!ObjectUtils.isEmpty(product)) 
		{
			oldProduct.setTitle(product.getTitle());
			oldProduct.setDescription(product.getDescription());
			oldProduct.setCategory(product.getCategory());
			oldProduct.setPrice(product.getPrice());
			oldProduct.setStock(product.getStock());
			
		}
		Product updateProduct = productService.saveProduct(oldProduct);
		if(!ObjectUtils.isEmpty(updateProduct)) 
		{
			if (!image.isEmpty()) {
				File saveFile = new ClassPathResource("static/img").getFile();
				Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + "category_img" + File.separator
						+ image.getOriginalFilename());
				System.out.println(path);
				Files.copy(image.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
			}
			session.setAttribute("success", "product saved sucessfully!!!");
		}
		else 
		{
			session.setAttribute("errormsg", "something went wrong");
		}
			return "redirect:/admin/updateProduct/"+product.getId();
	}
}
