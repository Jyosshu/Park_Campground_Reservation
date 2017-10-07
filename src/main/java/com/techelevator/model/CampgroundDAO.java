package com.techelevator.model;

import java.util.List;

import com.techelevator.pojo.Campground;

public interface CampgroundDAO {
	
	public List<Campground> getCampgroundsByParkId(int parkId);
}
