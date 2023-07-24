package com.smart.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.smart.dao.UserRepository;
import com.smart.entities.User;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

	@Autowired
	private UserRepository userRepository;

	@Override
	public UserDetails loadUserByUsername(String username) {
		// fetching user from database
		User user = userRepository.findByEmail(username);
		if(user==null) {
			System.out.println("not found user");
			throw new UsernameNotFoundException("could not found user !!");
		}
		// for constructor of CustomUserDetails
		CustomUserDetails customUserDetails = new CustomUserDetails(user);
		return customUserDetails;
	}
}
