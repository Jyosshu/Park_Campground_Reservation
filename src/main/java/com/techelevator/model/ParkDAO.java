package com.techelevator.model;

import java.util.List;

import com.techelevator.pojo.Park;

public interface ParkDAO {
	public Park getParkById(int id);
	public List<Park> getParks();
}
