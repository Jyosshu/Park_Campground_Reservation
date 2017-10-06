package com.techelevator;

import java.util.List;

public interface CampgroundDAO {
	
	public List<Campground> getCampgroundsByParkId(int parkId);
}
