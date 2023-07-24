package com.smart.controller;

import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.smart.dao.UserRepository;
import com.smart.entities.User;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
public class HomeController {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private BCryptPasswordEncoder passwordEncoder;

	/*
	 * --------to check tables created in Database using entities-------------
	 * 
	 * @Autowired private UserRepository userRepo;
	 * 
	 * @GetMapping("/test")
	 * 
	 * @ResponseBody public String test() { User user = new User();
	 * user.setName("Abhijeet"); user.setEmail("abhi@gmail.com");
	 * userRepo.save(user); return "working";
	 * 
	 * }
	 */
	@RequestMapping("/")
	public String HomeHandler(Model m) {
		m.addAttribute("title", "Home - Smart contact Manager");
		return "home";
	}

	@RequestMapping("/about")
	public String AboutHandler(Model m) {
		m.addAttribute("title", "About - Smart contact Manager");
		return "about";
	}

	@RequestMapping("/registration")
	public String registrationHandler(Model m) {
		m.addAttribute("user", new User());
		m.addAttribute("title", "Register - Smart contact Manager");
		return "registration";
	}

	// Handler for register user
	@PostMapping("/do_register")
	public String registerHandler(@Valid @ModelAttribute("user") User user, BindingResult validationResult, Model m) {

		if (validationResult.hasErrors()) {
			m.addAttribute("user", user);
			return "registration";
		}

		user.setRole("ROLE_USER");
		user.setEnabled(true);
		user.setPassword(passwordEncoder.encode(user.getPassword()));
		userRepository.save(user);
		m.addAttribute("user", new User());
		return "registration";
	}

	@GetMapping("/signin")
	public String customLogin(Model m) {
		m.addAttribute("title", "Login - Smart contact Manager");
		return "login";
	}

	@GetMapping("/forgot")
	public String openMail() {
		return "forgot_email";

	}

	@PostMapping("/send_otp")
	public String sendOtp(@RequestParam("email") String email, HttpSession session) {

		Random random = new Random(1000);
		int otp = random.nextInt(999999);
		
		User user = userRepository.findByEmail(email);
		
		if(user==null) {
			System.out.println("not found user");
			session.setAttribute("error", "Email not registerd");
			return "forgot_email";
		}
		
		System.out.println("email = " + email + " OTP =" + otp);
		
		return "verify_otp";

	}

}
