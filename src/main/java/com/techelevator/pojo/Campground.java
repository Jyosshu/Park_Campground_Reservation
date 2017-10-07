package com.techelevator.pojo;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class Campground {
	private int campgroundId;
	private int parkId;
	private String name;
	private String openFromMm;
	private String openToMm;
	private BigDecimal dailyFee;
	
	public Campground(int campgroundId, int parkId, String name, String openFromMm, String openToMm,
			BigDecimal dailyFee) {
		this.campgroundId = campgroundId;
		this.parkId = parkId;
		this.name = name;
		this.openFromMm = openFromMm;
		this.openToMm = openToMm;
		this.dailyFee = dailyFee.setScale(2, RoundingMode.HALF_UP);
	}

	public Campground() {
	}

	public int getCampgroundId() {
		return campgroundId;
	}

	public void setCampgroundId(int campgroundId) {
		this.campgroundId = campgroundId;
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

	public String getOpenFromMm() {
		return openFromMm;
	}

	public void setOpenFromMm(String openFromMm) {
		this.openFromMm = openFromMm;
	}

	public String getOpenToMm() {
		return openToMm;
	}

	public void setOpenToMm(String openToMm) {
		this.openToMm = openToMm;
	}

	public BigDecimal getDailyFee() {
		return dailyFee;
	}

	public void setDailyFee(BigDecimal dailyFee) {
		this.dailyFee= dailyFee;
	}
	
	
}
