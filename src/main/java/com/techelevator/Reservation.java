package com.techelevator;

import java.util.Date;

public class Reservation {
	private int reservation_id;
	private int site_id;
	private String name;
	private Date fromDate;
	private Date toDate;
	private Date createDate;
	
	public Reservation(){
		
	}
	
	public Reservation(int reservation_id, int site_id, String name, Date fromDate, Date toDate, Date createDate) {
		super();
		this.reservation_id = reservation_id;
		this.site_id = site_id;
		this.name = name;
		this.fromDate = fromDate;
		this.toDate = toDate;
		this.createDate = createDate;
	}

	public int getReservation_id() {
		return reservation_id;
	}

	public void setReservation_id(int reservation_id) {
		this.reservation_id = reservation_id;
	}

	public int getSite_id() {
		return site_id;
	}

	public void setSite_id(int site_id) {
		this.site_id = site_id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Date getFromDate() {
		return fromDate;
	}

	public void setFromDate(Date fromDate) {
		this.fromDate = fromDate;
	}

	public Date getToDate() {
		return toDate;
	}

	public void setToDate(Date toDate) {
		this.toDate = toDate;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}
}
