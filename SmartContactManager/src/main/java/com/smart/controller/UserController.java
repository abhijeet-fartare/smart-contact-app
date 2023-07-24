package com.smart.controller;

import java.security.Principal;
import java.util.Map;
import java.util.Optional;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.smart.dao.ContactRepository;
import com.smart.dao.UserRepository;
import com.smart.entities.Contact;
import com.smart.entities.User;
import com.razorpay.*;

import jakarta.servlet.http.HttpSession;

//when we want to return any page we weite @controller
@Controller
@RequestMapping("/user")
public class UserController {

	@Autowired
	public UserRepository userRepository;

	@Autowired
	public ContactRepository contactRepository;

	@Autowired
	private BCryptPasswordEncoder passwordEncoder;

	// method for adding common data to response
	@ModelAttribute
	public void addCommonData(Model m, Principal principal) {

		// Principal is for fetch username i.e. unique Id
		String username = principal.getName();

		// get user using username
		User user = userRepository.findByEmail(username);

		m.addAttribute("user", user);
	}

	// dashboard home
	@RequestMapping("/index")
	public String dashBoard(Model m) {
		m.addAttribute("title", "user dashboard");
		return "normal/user_dashBoard";
	}

	// open add form handler
	@GetMapping("/add_contact")
	public String openAddBoard(Model m) {
		m.addAttribute("title", "Add contact");
		System.out.println("on");
		m.addAttribute("contact", new Contact());
		return "normal/add_contact_form";
	}

	// processing add contact form
	@PostMapping("/process_form")
	// use principle to find user so we can save contact in that user
	// sesssion variable is for show success/error message for samll interval of
	// time
	public String processContact(@ModelAttribute("contact") Contact contact, Principal principal, HttpSession session) {

		try {
			String username = principal.getName();
			User user = userRepository.findByEmail(username);

			contact.setUser(user);
			user.getContact().add(contact);

			// save updated user which has contact
			userRepository.save(user);

			// success message
			session.setAttribute("message", "Your contact is added !! Add more contact");

		} catch (Exception e) {
			e.printStackTrace();
			// error message
			session.setAttribute("error", "Something went wrong !! Try again");
		}
		return "normal/add_contact_form";
	}

	// show contact handler
	// per page=3[n]
	// current page=0[page]
	@GetMapping("/show_contacts/{page}")
	public String showContacts(@PathVariable("page") Integer page, Model m, Principal principal) {
		m.addAttribute("title", "Show Contacts");

		String username = principal.getName();
		User user = userRepository.findByEmail(username);

		Pageable pageable = PageRequest.of(page, 3);

		Page<Contact> contacts = contactRepository.findByUser_id(user.getId(), pageable);

		// for show all contacts
		m.addAttribute("contacts", contacts);
		// we are on which current page
		m.addAttribute("currentpage", page);
		// total pages
		m.addAttribute("totalpages", contacts.getTotalPages());

		return "normal/show_contacts";

	}

	// show particular details
	@RequestMapping("/contact/{cId}")
	public String showContactDetail(@PathVariable("cId") Integer cId, Model m, Principal principal) {

		m.addAttribute("title", "Contacts Details");

		String username = principal.getName();
		User user = userRepository.findByEmail(username);

		Optional<Contact> contactoptional = contactRepository.findById(cId);
		Contact contact = contactoptional.get();

		// to avoid accessing other user's contact by id in url
		if (user.getId() == contact.getUser().getId()) {
			m.addAttribute("contact", contact);
		}
		return "normal/contact_detail";

	}

	// delete contact handler
	@GetMapping("/delete_contact/{cId}")
	public String deleteContactHandler(@PathVariable("cId") Integer cId, Model m, Principal principal,
			HttpSession session) {

		m.addAttribute("title", "Delete Contacts ");

		String username = principal.getName();
		User user = userRepository.findByEmail(username);

		Optional<Contact> contactoptional = contactRepository.findById(cId);
		Contact contact = contactoptional.get();

		// to avoid delete other user's contact by id in url
		if (user.getId() == contact.getUser().getId()) {
			// unlink user so we can delete contact without user
			contact.setUser(null);
			contactRepository.delete(contact);
			session.setAttribute("error", "Your contact is Deleted !!");
		}
		return "redirect:/user/show_contacts/0";

	}

	// update contact handler
	@GetMapping("/update_contact/{cId}")
	public String updateContactHandler(@PathVariable("cId") Integer cId, Model m, Principal principal) {

		m.addAttribute("title", "update Contacts ");

		String username = principal.getName();
		User user = userRepository.findByEmail(username);

		Optional<Contact> contactoptional = contactRepository.findById(cId);
		Contact contact = contactoptional.get();

		if (user.getId() == contact.getUser().getId()) {
			m.addAttribute("contact", contact);
		}
		return "normal/update_form";

	}

	// update contact handler
	@PostMapping("/process_update")
	public String updateHandler(@ModelAttribute("contact") Contact contact, Model m, Principal principal,
			HttpSession session) {

		m.addAttribute("title", "update Contacts");

		String username = principal.getName();
		User user = userRepository.findByEmail(username);

		contact.setUser(user);
		contactRepository.save(contact);
		session.setAttribute("message", "Your contact is Updated !!");

		return "redirect:/user/show_contacts/0";

	}

	@GetMapping("/profile")
	public String yourProfile(Model m) {
		m.addAttribute("title", "Profile page");
		return "normal/profile";

	}

	@GetMapping("/setting")
	public String settingHandler(Model m) {
		m.addAttribute("title", "Setting page");
		return "normal/setting";
	}

	@PostMapping("/change_password")
	// data getting in form (if we get in url we use @pathvarible)
	public String changePassword(@RequestParam("newPassword") String newPassword,
			@RequestParam("oldPassword") String oldPassword, Principal principal, HttpSession session) {

		String username = principal.getName();
		User user = userRepository.findByEmail(username);
		System.out.println(user.getPassword());

		if (passwordEncoder.matches(oldPassword, user.getPassword())) {

			user.setPassword(passwordEncoder.encode(newPassword));
			userRepository.save(user);
			session.setAttribute("message", "Your password is change !!");
		} else {
			session.setAttribute("error", "Please enter correct old password !!");
		}
		return "normal/setting";

	}

	// creating order for payment
	@PostMapping("/create_order")
	@ResponseBody
	public String createOrder(@RequestBody Map<String, Object> data) throws Exception {
		
		System.out.println(data);
		int price = Integer.parseInt(data.get("amount").toString());
		System.out.println(price);
	
			RazorpayClient client = new RazorpayClient("rzp_test_7rRM1YcnaKEjKh", "JiF6wbmnbw7DZzkQ9X67Vr52");
			
			JSONObject obj = new JSONObject();
			obj.put("amount", price*100);
			obj.put("currency", "INR");
			obj.put("receipt", "txn_123456");
			
			//creating new order
			Order order = client.Orders.create(obj);
			System.out.println(order);
			return order.toString();
		}
	
}


