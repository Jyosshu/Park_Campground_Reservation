package com.techelevator;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;


public class JDBCParkDAOTest extends DAOIntegrationTest{
	private JDBCParkDAO dao;
	private JdbcTemplate jdbcTemplate;
	
	@Before
	public void setup() {
		
		jdbcTemplate = new JdbcTemplate(super.getDataSource());
		jdbcTemplate.update("DELETE FROM reservation");
		jdbcTemplate.update("DELETE FROM site");
		jdbcTemplate.update("DELETE FROM campground");
		jdbcTemplate.update("DELETE FROM park");
		
		
		
		
		dao = new JDBCParkDAO(super.getDataSource());
		jdbcTemplate.update("INSERT INTO park (name, location, establish_date, area, visitors, description) "
				+ "VALUES ('Yosemite', 'California', '1890-10-01', 1069, 2000000, 'A park named Yosemite')");
		jdbcTemplate.update("INSERT INTO park (name, location, establish_date, area, visitors, description) "
				+ "VALUES ('Yellowstone', 'Colorado','1872-03-01', 3471, 2500000, 'A park name Yellow Stone')");
	}

	@Test
	public void testGetParks() {
		List<Park> allParks = dao.getParks();
		
		String sqlSelectPark = "SELECT * FROM park ORDER BY name";
		
		SqlRowSet parkRowSet =jdbcTemplate.queryForRowSet(sqlSelectPark);
		
		assertNotNull(allParks);
		assertEquals(2, allParks.size());
		parkRowSet.next();
		assertEquals(allParks.get(0).getName(), parkRowSet.getString("name"));
		assertEquals(allParks.get(0).getParkId(), parkRowSet.getInt("park_id"));
		assertEquals(allParks.get(0).getLocation(), parkRowSet.getString("location"));
		assertEquals(allParks.get(0).getEstablishDate(), parkRowSet.getDate("establish_date"));
		assertEquals(allParks.get(0).getArea(), parkRowSet.getInt("area"));
		assertEquals(allParks.get(0).getVisitors(), parkRowSet.getInt("visitors"));
		assertEquals(allParks.get(0).getDescription(), parkRowSet.getString("description"));
		parkRowSet.next();
		assertEquals(allParks.get(1).getName(), parkRowSet.getString("name"));
		assertEquals(allParks.get(1).getParkId(), parkRowSet.getInt("park_id"));
		assertEquals(allParks.get(1).getLocation(), parkRowSet.getString("location"));
		assertEquals(allParks.get(1).getEstablishDate(), parkRowSet.getDate("establish_date"));
		assertEquals(allParks.get(1).getArea(), parkRowSet.getInt("area"));
		assertEquals(allParks.get(1).getVisitors(), parkRowSet.getInt("visitors"));
		assertEquals(allParks.get(1).getDescription(), parkRowSet.getString("description"));
	}

}
