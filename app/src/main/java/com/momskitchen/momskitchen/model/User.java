package com.momskitchen.momskitchen.model;

import java.util.List;

public class User{
	public String id;
	public String name;
	public String email;
	public String phone;
	public String address;
	public String nearestLandmark;
	public List<String> orderIds;
	public List<String> commentIds;

	public User() {
	}

	public User(String id, String name, String email, String phone, String address, String nearestLandmark, List<String> orderIds, List<String> commentIds) {
		this.id = id;
		this.name = name;
		this.email = email;
		this.phone = phone;
		this.address = address;
		this.nearestLandmark = nearestLandmark;
		this.orderIds = orderIds;
		this.commentIds = commentIds;
	}
}