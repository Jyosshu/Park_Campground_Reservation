package com.techelevator.pojo;

import java.util.Date;

public class Park {

	private int parkId;
	private String name;
	private String location;
	private Date establishDate;
	private int area;
	private int visitors;
	private String description;
	
	public Park() {
		
	}
	
	public Park(int parkId, String name, String location, Date establishDate, int area, int visitors,
			String description) {
		this.parkId = parkId;
		this.name = name;
		this.location = location;
		this.establishDate = establishDate;
		this.area = area;
		this.visitors = visitors;
		this.description = description;
	}

	public int getParkId() {
		return parkId;
	}
	public void setParkId(int parkId) {
		this.parkId = parkId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
	}
	public Date getEstablishDate() {
		return establishDate;
	}
	public void setEstablishDate(Date establishDate) {
		this.establishDate = establishDate;
	}
	public int getArea() {
		return area;
	}
	public void setArea(int area) {
		this.area = area;
	}
	public int getVisitors() {
		return visitors;
	}
	public void setVisitors(int visitors) {
		this.visitors = visitors;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
	
}
