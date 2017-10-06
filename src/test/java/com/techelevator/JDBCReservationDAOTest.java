package com.techelevator;

import static org.junit.Assert.*;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.junit.Before;
import org.junit.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;

public class JDBCReservationDAOTest extends DAOIntegrationTest{
	private JDBCReservationDAO resDao;
	private JdbcTemplate jdbcTemplate;
	private int yosemiteId;
	private int reservationId;
	private int noLoiteringCampId;
	private int site1Id;

	DateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
	
	@Before
	public void setup() throws ParseException {
		
		jdbcTemplate = new JdbcTemplate(super.getDataSource());
		jdbcTemplate.update("DELETE FROM reservation");
		jdbcTemplate.update("DELETE FROM site");
		jdbcTemplate.update("DELETE FROM campground");
		jdbcTemplate.update("DELETE FROM park");
		
		
		resDao = new JDBCReservationDAO(super.getDataSource());
		yosemiteId = jdbcTemplate.queryForObject("INSERT INTO park (name, location, establish_date, area, visitors, description) "
				+ "VALUES ('Yosemite', 'California', '1890-10-01', 1069, 2000000, 'A park named Yosemite') RETURNING park_id", Integer.class);
		jdbcTemplate.update("INSERT INTO park (name, location, establish_date, area, visitors, description) "
				+ "VALUES ('Yellowstone', 'Colorado','1872-03-01', 3471, 2500000, 'A park name Yellow Stone')");
		
		noLoiteringCampId=jdbcTemplate.queryForObject("INSERT INTO campground (park_id, name, open_from_mm, open_to_mm, daily_fee) "
				+ "VALUES (?, 'No Loitering', '01', '10', '$2.50' ) RETURNING campground_id", Integer.class, yosemiteId);
		jdbcTemplate.update("INSERT INTO campground (park_id, name, open_from_mm, open_to_mm, daily_fee) "
				+ "VALUES (?, 'Big Saloon', '05', '07', '$3.50' )", yosemiteId);
		
				
		site1Id = jdbcTemplate.queryForObject("INSERT INTO site (campground_id, site_number, max_occupancy, accessible, max_rv_length, utilities) "
				+ "VALUES (?,1, 10, true, 100, true) RETURNING site_id", Integer.class, noLoiteringCampId); 
		
	}
	
	@Test
	public void testReserveSite() throws ParseException {
		String name="Cool Joe";
		Date startDate=sdf.parse("2015-02-05");
		Date endDate=sdf.parse("2015-03-15");
		reservationId = resDao.reserveSite(site1Id, name, startDate, endDate);
	
		String sqlReservationSelect = "SELECT * FROM reservation WHERE reservation_id=?";
		SqlRowSet reserveRowSet = jdbcTemplate.queryForRowSet(sqlReservationSelect, reservationId);
		
		
		reserveRowSet.next();
		assertEquals(reservationId, reserveRowSet.getInt("reservation_id"));
		assertEquals(site1Id, reserveRowSet.getInt("site_id"));
		assertEquals(name, reserveRowSet.getString("name"));
		assertEquals(startDate, reserveRowSet.getDate("from_date"));
		assertEquals(endDate, reserveRowSet.getDate("to_date"));
	}

}
