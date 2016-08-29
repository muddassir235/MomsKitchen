package com.momskitchen.momskitchen.model;

import com.google.firebase.database.ServerValue;

import java.util.HashMap;
import java.util.List;

public class MealItem{
	public String id;
	public String name;
	public String description;
	public long pricePerUnit;
	public String thumbNailURL;
	public List<String> imageURLs;
	public String discoutId;
	public String catagory;
	public String quantityBought;
	public double averageRating;
	public double numberOfRatings;
	public List<String> commentIds;
	public Boolean available;
	public HashMap<String, Object> timeStampCreated;
	private HashMap<String, Object> timeStampLastChanged;

	public MealItem() {
	}

	public MealItem(String id, String name, String description, long pricePerUnit, String thumbNailURL, List<String> imageURLs, String discoutId, String catagory, String quantityBought, double averageRating, double numberOfRatings, List<String> commentIds, Boolean available, HashMap<String, Object> timeStampCreated) {
		this.id = id;
		this.name = name;
		this.description = description;
		this.pricePerUnit = pricePerUnit;
		this.thumbNailURL = thumbNailURL;
		this.imageURLs = imageURLs;
		this.discoutId = discoutId;
		this.catagory = catagory;
		this.quantityBought = quantityBought;
		this.averageRating = averageRating;
		this.numberOfRatings = numberOfRatings;
		this.commentIds = commentIds;
		this.available = available;
		this.timeStampCreated = timeStampCreated;

		//Date last changed will always be set to ServerValue.TIMESTAMP
		HashMap<String, Object> dateLastChangedObj = new HashMap<String, Object>();
		dateLastChangedObj.put("date", ServerValue.TIMESTAMP);
		this.timeStampLastChanged = dateLastChangedObj;

	}

	public HashMap<String, Object> getTimeStampLastChanged() {
		return timeStampLastChanged;
	}

	public HashMap<String, Object> getTimeStampCreated() {
		//If there is a timeStampCreated object already, then return that
		if (timeStampCreated != null) {
			return timeStampCreated;
		}
		//Otherwise make a new object set to ServerValue.TIMESTAMP
		HashMap<String, Object> dateCreatedObj = new HashMap<String, Object>();
		dateCreatedObj.put("date", ServerValue.TIMESTAMP);
		return dateCreatedObj;
	}


}