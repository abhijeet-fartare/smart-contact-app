package com.smart.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import com.smart.entities.User;

public interface UserRepository extends JpaRepository<User, Integer> {

	User findByEmail(String username);

	//to fetch user from database for UserDetailsServiceImpl
//	@Query("select u from User where u.email = : email")
//	public User getUserByUserName(@Param("email") String email);

}