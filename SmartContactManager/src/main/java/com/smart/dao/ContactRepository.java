package com.smart.dao;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.smart.entities.Contact;
import com.smart.entities.User;

public interface ContactRepository extends JpaRepository<Contact, Integer> {

	// page is sublist of object
	// pageble have information regarding pagination like page per [n], current page
	public Page<Contact> findByUser_id(int userId, Pageable pagable);

	//search
	public List<Contact> findByFirstNameContainingAndUser(String keywords,User user);

}
