package com.techelevator.JDBC;

import java.util.Date;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;

import com.techelevator.model.ReservationDAO;

public class JDBCReservationDAO implements ReservationDAO {
	private JdbcTemplate jdbcTemplate;

	public JDBCReservationDAO(DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}
	
	@Override
	public int reserveSite(int siteToBeReserved, String reservationName, Date fromDate, Date toDate) {
		String sqlEnterReservation = "INSERT INTO reservation (site_id, name, from_date, to_date, create_date) VALUES (?, ?, ?, ?, CURRENT_DATE) RETURNING reservation_id";
		
		return jdbcTemplate.queryForObject(sqlEnterReservation, Integer.class, siteToBeReserved, reservationName, fromDate, toDate);
	}
}
