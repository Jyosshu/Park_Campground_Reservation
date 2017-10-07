package com.techelevator;

import static org.junit.Assert.*;

import java.math.BigDecimal;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import com.techelevator.JDBC.JDBCCampgroundDAO;
import com.techelevator.pojo.Campground;

public class JDBCCampgroundDAOTEST extends DAOIntegrationTest {
	private JDBCCampgroundDAO dao;
	private JdbcTemplate jdbcTemplate;
	
	private int yosemiteId;
	
	@Before
	public void setup() {
		
		jdbcTemplate = new JdbcTemplate(super.getDataSource());
		jdbcTemplate.update("DELETE FROM reservation");
		jdbcTemplate.update("DELETE FROM site");
		jdbcTemplate.update("DELETE FROM campground");
		jdbcTemplate.update("DELETE FROM park");
		
		dao = new JDBCCampgroundDAO(super.getDataSource());
		yosemiteId = jdbcTemplate.queryForObject("INSERT INTO park (name, location, establish_date, area, visitors, description) "
				+ "VALUES ('Yosemite', 'California', '1890-10-01', 1069, 2000000, 'A park named Yosemite') RETURNING park_id", Integer.class);
		jdbcTemplate.update("INSERT INTO park (name, location, establish_date, area, visitors, description) "
				+ "VALUES ('Yellowstone', 'Colorado','1872-03-01', 3471, 2500000, 'A park name Yellow Stone')");
		
		jdbcTemplate.update("INSERT INTO campground (park_id, name, open_from_mm, open_to_mm, daily_fee) "
				+ "VALUES (?, 'No Loitering', '01', '02', '$2.50' )", yosemiteId);
		jdbcTemplate.update("INSERT INTO campground (park_id, name, open_from_mm, open_to_mm, daily_fee) "
				+ "VALUES (?, 'Big Saloon', '05', '07', '$3.50' )", yosemiteId);
			
	}

	@Test
	public void testGetCampgrounds() {
		List<Campground> allCampgrounds = dao.getCampgroundsByParkId(yosemiteId);
		
		String sqlSelectCamp = "SELECT * FROM campground WHERE park_id =? ORDER BY name";
		
		SqlRowSet campRowSet =jdbcTemplate.queryForRowSet(sqlSelectCamp, yosemiteId);
		
		assertNotNull(allCampgrounds);
		assertEquals(2, allCampgrounds.size());
		campRowSet.next();
		assertEquals(allCampgrounds.get(0).getName(), campRowSet.getString("name"));
		assertEquals(allCampgrounds.get(0).getParkId(), campRowSet.getInt("park_id"));
		assertEquals(allCampgrounds.get(0).getOpenFromMm(), campRowSet.getString("open_from_mm"));
		assertEquals(allCampgrounds.get(0).getOpenToMm(), campRowSet.getString("open_to_mm"));
		assertEquals(allCampgrounds.get(0).getDailyFee(), campRowSet.getBigDecimal("daily_fee"));

		campRowSet.next();
		assertEquals(allCampgrounds.get(1).getName(), campRowSet.getString("name"));
		assertEquals(allCampgrounds.get(1).getParkId(), campRowSet.getInt("park_id"));
		assertEquals(allCampgrounds.get(1).getOpenFromMm(), campRowSet.getString("open_from_mm"));
		assertEquals(allCampgrounds.get(1).getOpenToMm(), campRowSet.getString("open_to_mm"));
		assertEquals(allCampgrounds.get(1).getDailyFee(), campRowSet.getBigDecimal("daily_fee"));
	}

}
