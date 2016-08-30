package com.momskitchen.momskitchen.model;

import android.util.Log;

import com.google.firebase.database.ServerValue;

import java.util.HashMap;
import java.util.List;

public class MealItem{
	public String id;
	public String name;
	public String description;
	public Long pricePerUnit;
	public String thumbnailURL;
	public String posterURL;
	public String discoutId;
	public String catagory;
	public String quantityBought;
	public Double averageRating;
	public Double numberOfRatings;
	public List<String> commentIds;
	public Boolean available;
	public HashMap<String, Object> timeStampCreated;
	public HashMap<String, Object> timeStampLastChanged;

	public MealItem() {
	}

	public MealItem(String id, String name, String description, Long pricePerUnit, String thumbnailURL, String posterURL, String discoutId, String catagory, String quantityBought, Double averageRating, Double numberOfRatings, List<String> commentIds, Boolean available, HashMap<String, Object> timeStampCreated) {
		this.id = id;
		this.name = name;
		this.description = description;
		this.pricePerUnit = pricePerUnit;
		this.thumbnailURL = thumbnailURL;
		this.posterURL = posterURL;
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