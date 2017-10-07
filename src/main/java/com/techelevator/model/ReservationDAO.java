package com.techelevator.model;

import java.util.Date;

public interface ReservationDAO {
	public int reserveSite(int siteToBeReserved, String reservationName, Date from_date, Date to_date);
}
