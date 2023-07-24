package com.smart.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;

@Entity
public class Contact {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int Cid;
	@Column(unique = true)
	private String email;
	private String firstName;
	private String lastName;
	private String work;
	private String phone;
	private String imgurl;
	@Column(length = 1000)
	private String description;
	@ManyToOne
	@JsonIgnore /*
				 * not serialize the JSON of user (otherwise while search, responseEntity will
				 * serialize Contact,Contact will serialize User & User will again serialize
				 * Contact & it will go in loop
				 */
	private User user;

	public int getCid() {
		return Cid;
	}

	public void setCid(int cid) {
		Cid = cid;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getWork() {
		return work;
	}

	public void setWork(String work) {
		this.work = work;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getImgurl() {
		return imgurl;
	}

	public void setImgurl(String imgurl) {
		this.imgurl = imgurl;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	@Override
	public String toString() {
		return "Contact [Cid=" + Cid + ", email=" + email + ", firstName=" + firstName + ", lastName=" + lastName
				+ ", work=" + work + ", phone=" + phone + ", imgurl=" + imgurl + ", description=" + description + "]";
	}

}
