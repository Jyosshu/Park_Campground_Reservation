package com.techelevator;

import static org.junit.Assert.assertEquals;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;

public class JDBCSiteDAOTest extends DAOIntegrationTest {
	private JDBCSiteDAO dao;
	private JDBCReservationDAO resDao;
	private JdbcTemplate jdbcTemplate;
	private int yosemiteId;
	private int noLoiteringCampId;
	private int site1Id;
	private int site2Id;
	DateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
	
	@Before
	public void setup() throws ParseException {
		
		jdbcTemplate = new JdbcTemplate(super.getDataSource());
		jdbcTemplate.update("DELETE FROM reservation");
		jdbcTemplate.update("DELETE FROM site");
		jdbcTemplate.update("DELETE FROM campground");
		jdbcTemplate.update("DELETE FROM park");
		
		dao = new JDBCSiteDAO(super.getDataSource());
		resDao = new JDBCReservationDAO(super.getDataSource());
		
		yosemiteId = jdbcTemplate.queryForObject("INSERT INTO park (name, location, establish_date, area, visitors, description) "
				+ "VALUES ('Yosemite', 'California', '1890-10-01', 1069, 2000000, 'A park named Yosemite') RETURNING park_id", Integer.class);
		jdbcTemplate.update("INSERT INTO park (name, location, establish_date, area, visitors, description) "
				+ "VALUES ('Yellowstone', 'Colorado','1872-03-01', 3471, 2500000, 'A park name Yellow Stone')");
		
		noLoiteringCampId=jdbcTemplate.queryForObject("INSERT INTO campground (park_id, name, open_from_mm, open_to_mm, daily_fee) "
				+ "VALUES (?, 'No Loitering', '01', '10', '$2.50' ) RETURNING campground_id", Integer.class, yosemiteId);
		jdbcTemplate.update("INSERT INTO campground (park_id, name, open_from_mm, open_to_mm, daily_fee) "
				+ "VALUES (?, 'Big Saloon', '05', '07', '$3.50' )", yosemiteId);
		
		site1Id=jdbcTemplate.queryForObject("INSERT INTO site (campground_id, site_number, max_occupancy, accessible, max_rv_length, utilities) "
				+ "VALUES (?,1, 10, true, 100, true) RETURNING site_id", Integer.class, noLoiteringCampId); 
		site2Id=jdbcTemplate.queryForObject("INSERT INTO site (campground_id, site_number, max_occupancy, accessible, max_rv_length, utilities) "
				+ "VALUES (?,2, 10, true, 100, true) RETURNING site_id", Integer.class, noLoiteringCampId); 
		
		resDao.reserveSite(site1Id, "Cool Joe", sdf.parse("2015-02-05"), sdf.parse("2015-03-15")); 
	}

	@Test
	public void testGetActiveSitesByCampgroundLeftApproachWithOverlap() throws ParseException {
		List<Site> activeSitesLeftWithOverlap = dao.getActiveSitesByCampground(noLoiteringCampId, 
				sdf.parse("2015-1-1"), sdf.parse("2015-2-10"));//Left Intersection with overlap
		
		String sqlSiteLeftWithOverlap = "SELECT * FROM site WHERE site_id=?";
		SqlRowSet siteRowSet =jdbcTemplate.queryForRowSet(sqlSiteLeftWithOverlap, site2Id);
		
		assertEquals(1, activeSitesLeftWithOverlap.size());
		siteRowSet.next();
		assertEquals(activeSitesLeftWithOverlap.get(0).getSiteId(), siteRowSet.getInt("site_id"));
		assertEquals(activeSitesLeftWithOverlap.get(0).getSiteNumber(), siteRowSet.getInt("site_number"));
		assertEquals(activeSitesLeftWithOverlap.get(0).getCampgroundId(), siteRowSet.getInt("campground_id"));
		assertEquals(activeSitesLeftWithOverlap.get(0).getMaxOccupancy(), siteRowSet.getInt("max_occupancy"));
		assertEquals(activeSitesLeftWithOverlap.get(0).getMaxRvLength(), siteRowSet.getInt("max_rv_length"));
		assertEquals(activeSitesLeftWithOverlap.get(0).isAccessible(), siteRowSet.getBoolean("accessible"));
		assertEquals(activeSitesLeftWithOverlap.get(0).isAccessible(), siteRowSet.getBoolean("utilities"));
	}
	
	@Test
	public void testGetActiveSitesByCampgroundLeftApproachWithNoOverlap() throws ParseException {
		List<Site> activeSitesLeftWithNoOverlap= dao.getActiveSitesByCampground(noLoiteringCampId, 
				sdf.parse("2015-1-1"), sdf.parse("2015-1-10"));//Left Intersection no overlap
		
		String sqlSite = "SELECT * FROM site ORDER BY site_number";
		SqlRowSet siteRowSet =jdbcTemplate.queryForRowSet(sqlSite);
		
		assertEquals(2, activeSitesLeftWithNoOverlap.size());
		siteRowSet.next();
		assertEquals(activeSitesLeftWithNoOverlap.get(0).getSiteId(), siteRowSet.getInt("site_id"));
		assertEquals(activeSitesLeftWithNoOverlap.get(0).getSiteNumber(), siteRowSet.getInt("site_number"));
		assertEquals(activeSitesLeftWithNoOverlap.get(0).getCampgroundId(), siteRowSet.getInt("campground_id"));
		assertEquals(activeSitesLeftWithNoOverlap.get(0).getMaxOccupancy(), siteRowSet.getInt("max_occupancy"));
		assertEquals(activeSitesLeftWithNoOverlap.get(0).getMaxRvLength(), siteRowSet.getInt("max_rv_length"));
		assertEquals(activeSitesLeftWithNoOverlap.get(0).isAccessible(), siteRowSet.getBoolean("accessible"));
		assertEquals(activeSitesLeftWithNoOverlap.get(0).isAccessible(), siteRowSet.getBoolean("utilities"));
	
		siteRowSet.next();
		assertEquals(activeSitesLeftWithNoOverlap.get(1).getSiteId(), siteRowSet.getInt("site_id"));
		assertEquals(activeSitesLeftWithNoOverlap.get(1).getSiteNumber(), siteRowSet.getInt("site_number"));
		assertEquals(activeSitesLeftWithNoOverlap.get(1).getCampgroundId(), siteRowSet.getInt("campground_id"));
		assertEquals(activeSitesLeftWithNoOverlap.get(1).getMaxOccupancy(), siteRowSet.getInt("max_occupancy"));
		assertEquals(activeSitesLeftWithNoOverlap.get(1).getMaxRvLength(), siteRowSet.getInt("max_rv_length"));
		assertEquals(activeSitesLeftWithNoOverlap.get(1).isAccessible(), siteRowSet.getBoolean("accessible"));
		assertEquals(activeSitesLeftWithNoOverlap.get(1).isAccessible(), siteRowSet.getBoolean("utilities"));
		
	}
	
	
	@Test
	public void testGetActiveSitesByCampgroundLeftApproachInclusive() throws ParseException {
		List<Site> activeSitesLeftInclusive= dao.getActiveSitesByCampground(noLoiteringCampId, 
				sdf.parse("2015-1-1"), sdf.parse("2015-2-5"));//Left Intersection ending at start of old reservation
		String sqlSiteLeftInclusive = "SELECT * FROM site ORDER BY site_number";
		SqlRowSet siteRowSet =jdbcTemplate.queryForRowSet(sqlSiteLeftInclusive);
		
		assertEquals(2, activeSitesLeftInclusive.size());
		siteRowSet.next();
		assertEquals(activeSitesLeftInclusive.get(0).getSiteId(), siteRowSet.getInt("site_id"));
		assertEquals(activeSitesLeftInclusive.get(0).getSiteNumber(), siteRowSet.getInt("site_number"));
		assertEquals(activeSitesLeftInclusive.get(0).getCampgroundId(), siteRowSet.getInt("campground_id"));
		assertEquals(activeSitesLeftInclusive.get(0).getMaxOccupancy(), siteRowSet.getInt("max_occupancy"));
		assertEquals(activeSitesLeftInclusive.get(0).getMaxRvLength(), siteRowSet.getInt("max_rv_length"));
		assertEquals(activeSitesLeftInclusive.get(0).isAccessible(), siteRowSet.getBoolean("accessible"));
		assertEquals(activeSitesLeftInclusive.get(0).isAccessible(), siteRowSet.getBoolean("utilities"));
	
		siteRowSet.next();
		assertEquals(activeSitesLeftInclusive.get(1).getSiteId(), siteRowSet.getInt("site_id"));
		assertEquals(activeSitesLeftInclusive.get(1).getSiteNumber(), siteRowSet.getInt("site_number"));
		assertEquals(activeSitesLeftInclusive.get(1).getCampgroundId(), siteRowSet.getInt("campground_id"));
		assertEquals(activeSitesLeftInclusive.get(1).getMaxOccupancy(), siteRowSet.getInt("max_occupancy"));
		assertEquals(activeSitesLeftInclusive.get(1).getMaxRvLength(), siteRowSet.getInt("max_rv_length"));
		assertEquals(activeSitesLeftInclusive.get(1).isAccessible(), siteRowSet.getBoolean("accessible"));
		assertEquals(activeSitesLeftInclusive.get(1).isAccessible(), siteRowSet.getBoolean("utilities"));
	}
	
	@Test
	public void testGetActiveSitesByCampgroundDateBetweenRangeStartDateJnclusive() throws ParseException {
		List<Site> activeSitesStartDateInclusive = dao.getActiveSitesByCampground(noLoiteringCampId, 
				sdf.parse("2015-2-5"), sdf.parse("2015-2-15")); //inbetween no points included
		
		String sqlSiteLeftWithOverlap = "SELECT * FROM site WHERE site_id=?";
		SqlRowSet siteRowSet =jdbcTemplate.queryForRowSet(sqlSiteLeftWithOverlap, site2Id);
		
		assertEquals(1, activeSitesStartDateInclusive.size());
		siteRowSet.next();
		assertEquals(activeSitesStartDateInclusive.get(0).getSiteId(), siteRowSet.getInt("site_id"));
		assertEquals(activeSitesStartDateInclusive.get(0).getSiteNumber(), siteRowSet.getInt("site_number"));
		assertEquals(activeSitesStartDateInclusive.get(0).getCampgroundId(), siteRowSet.getInt("campground_id"));
		assertEquals(activeSitesStartDateInclusive.get(0).getMaxOccupancy(), siteRowSet.getInt("max_occupancy"));
		assertEquals(activeSitesStartDateInclusive.get(0).getMaxRvLength(), siteRowSet.getInt("max_rv_length"));
		assertEquals(activeSitesStartDateInclusive.get(0).isAccessible(), siteRowSet.getBoolean("accessible"));
		assertEquals(activeSitesStartDateInclusive.get(0).isAccessible(), siteRowSet.getBoolean("utilities"));
	}
	
	@Test
	public void testGetActiveSitesByCampgroundDateBetweenRangeEndDateInclusive() throws ParseException {
		List<Site> activeSitesEndDateInclusive= dao.getActiveSitesByCampground(noLoiteringCampId, 
				sdf.parse("2015-2-15"), sdf.parse("2015-3-15"));//inbetween right point included
		
		String sqlSiteLeftWithOverlap = "SELECT * FROM site WHERE site_id=?";
		SqlRowSet siteRowSet =jdbcTemplate.queryForRowSet(sqlSiteLeftWithOverlap, site2Id);
		
		assertEquals(1, activeSitesEndDateInclusive.size());
		siteRowSet.next();
		assertEquals(activeSitesEndDateInclusive.get(0).getSiteId(), siteRowSet.getInt("site_id"));
		assertEquals(activeSitesEndDateInclusive.get(0).getSiteNumber(), siteRowSet.getInt("site_number"));
		assertEquals(activeSitesEndDateInclusive.get(0).getCampgroundId(), siteRowSet.getInt("campground_id"));
		assertEquals(activeSitesEndDateInclusive.get(0).getMaxOccupancy(), siteRowSet.getInt("max_occupancy"));
		assertEquals(activeSitesEndDateInclusive.get(0).getMaxRvLength(), siteRowSet.getInt("max_rv_length"));
		assertEquals(activeSitesEndDateInclusive.get(0).isAccessible(), siteRowSet.getBoolean("accessible"));
		assertEquals(activeSitesEndDateInclusive.get(0).isAccessible(), siteRowSet.getBoolean("utilities"));
	}
	
	@Test
	public void testGetActiveSitesByCampgroundDateBetweenRange() throws ParseException {
		List<Site> activeSitesNeitherDateInclusive = dao.getActiveSitesByCampground(noLoiteringCampId, 
				sdf.parse("2015-2-15"), sdf.parse("2015-3-10")); //inbetween leftpoint included
		
		String sqlSiteLeftWithOverlap = "SELECT * FROM site WHERE site_id=?";
		SqlRowSet siteRowSet =jdbcTemplate.queryForRowSet(sqlSiteLeftWithOverlap, site2Id);
		
		assertEquals(1, activeSitesNeitherDateInclusive.size());
		siteRowSet.next();
		assertEquals(activeSitesNeitherDateInclusive.get(0).getSiteId(), siteRowSet.getInt("site_id"));
		assertEquals(activeSitesNeitherDateInclusive.get(0).getSiteNumber(), siteRowSet.getInt("site_number"));
		assertEquals(activeSitesNeitherDateInclusive.get(0).getCampgroundId(), siteRowSet.getInt("campground_id"));
		assertEquals(activeSitesNeitherDateInclusive.get(0).getMaxOccupancy(), siteRowSet.getInt("max_occupancy"));
		assertEquals(activeSitesNeitherDateInclusive.get(0).getMaxRvLength(), siteRowSet.getInt("max_rv_length"));
		assertEquals(activeSitesNeitherDateInclusive.get(0).isAccessible(), siteRowSet.getBoolean("accessible"));
		assertEquals(activeSitesNeitherDateInclusive.get(0).isAccessible(), siteRowSet.getBoolean("utilities"));
	}
	
	@Test
	public void testGetActiveSitesByCampgroundRightApproachNoOverlap() throws ParseException {
		List<Site> activeSitesRightWithNoOverlap= dao.getActiveSitesByCampground(noLoiteringCampId, 
				sdf.parse("2015-4-1"), sdf.parse("2015-5-10"));//Right Intersection no overlap
		String sqlSiteLeftInclusive = "SELECT * FROM site ORDER BY site_number";
		SqlRowSet siteRowSet =jdbcTemplate.queryForRowSet(sqlSiteLeftInclusive);
		
		assertEquals(2, activeSitesRightWithNoOverlap.size());
		siteRowSet.next();
		assertEquals(activeSitesRightWithNoOverlap.get(0).getSiteId(), siteRowSet.getInt("site_id"));
		assertEquals(activeSitesRightWithNoOverlap.get(0).getSiteNumber(), siteRowSet.getInt("site_number"));
		assertEquals(activeSitesRightWithNoOverlap.get(0).getCampgroundId(), siteRowSet.getInt("campground_id"));
		assertEquals(activeSitesRightWithNoOverlap.get(0).getMaxOccupancy(), siteRowSet.getInt("max_occupancy"));
		assertEquals(activeSitesRightWithNoOverlap.get(0).getMaxRvLength(), siteRowSet.getInt("max_rv_length"));
		assertEquals(activeSitesRightWithNoOverlap.get(0).isAccessible(), siteRowSet.getBoolean("accessible"));
		assertEquals(activeSitesRightWithNoOverlap.get(0).isAccessible(), siteRowSet.getBoolean("utilities"));
	
		siteRowSet.next();
		assertEquals(activeSitesRightWithNoOverlap.get(1).getSiteId(), siteRowSet.getInt("site_id"));
		assertEquals(activeSitesRightWithNoOverlap.get(1).getSiteNumber(), siteRowSet.getInt("site_number"));
		assertEquals(activeSitesRightWithNoOverlap.get(1).getCampgroundId(), siteRowSet.getInt("campground_id"));
		assertEquals(activeSitesRightWithNoOverlap.get(1).getMaxOccupancy(), siteRowSet.getInt("max_occupancy"));
		assertEquals(activeSitesRightWithNoOverlap.get(1).getMaxRvLength(), siteRowSet.getInt("max_rv_length"));
		assertEquals(activeSitesRightWithNoOverlap.get(1).isAccessible(), siteRowSet.getBoolean("accessible"));
		assertEquals(activeSitesRightWithNoOverlap.get(1).isAccessible(), siteRowSet.getBoolean("utilities"));
		
	}
	
	@Test
	public void testGetActiveSitesByCampgroundRightApproachWithOverlap() throws ParseException {
		List<Site> activeSitesRighttWithOverlap = dao.getActiveSitesByCampground(noLoiteringCampId, 
				sdf.parse("2015-2-10"), sdf.parse("2015-3-20"));//Right Intersection with overlap
		String sqlSiteLeftWithOverlap = "SELECT * FROM site WHERE site_id=?";
		SqlRowSet siteRowSet =jdbcTemplate.queryForRowSet(sqlSiteLeftWithOverlap, site2Id);
		
		assertEquals(1, activeSitesRighttWithOverlap.size());
		siteRowSet.next();
		assertEquals(activeSitesRighttWithOverlap.get(0).getSiteId(), siteRowSet.getInt("site_id"));
		assertEquals(activeSitesRighttWithOverlap.get(0).getSiteNumber(), siteRowSet.getInt("site_number"));
		assertEquals(activeSitesRighttWithOverlap.get(0).getCampgroundId(), siteRowSet.getInt("campground_id"));
		assertEquals(activeSitesRighttWithOverlap.get(0).getMaxOccupancy(), siteRowSet.getInt("max_occupancy"));
		assertEquals(activeSitesRighttWithOverlap.get(0).getMaxRvLength(), siteRowSet.getInt("max_rv_length"));
		assertEquals(activeSitesRighttWithOverlap.get(0).isAccessible(), siteRowSet.getBoolean("accessible"));
		assertEquals(activeSitesRighttWithOverlap.get(0).isAccessible(), siteRowSet.getBoolean("utilities"));
	
	}
	
	@Test
	public void testGetActiveSitesByCampgroundRightApproachRightInclusive() throws ParseException {
		List<Site> activeSitesRightInclusive= dao.getActiveSitesByCampground(noLoiteringCampId, 
				sdf.parse("2015-3-15"), sdf.parse("2015-4-15"));//Right Intersection ending at start of old reservation
		String sqlSiteLeftInclusive = "SELECT * FROM site ORDER BY site_number";
		SqlRowSet siteRowSet =jdbcTemplate.queryForRowSet(sqlSiteLeftInclusive);
		
		assertEquals(2, activeSitesRightInclusive.size());
		siteRowSet.next();
		assertEquals(activeSitesRightInclusive.get(0).getSiteId(), siteRowSet.getInt("site_id"));
		assertEquals(activeSitesRightInclusive.get(0).getSiteNumber(), siteRowSet.getInt("site_number"));
		assertEquals(activeSitesRightInclusive.get(0).getCampgroundId(), siteRowSet.getInt("campground_id"));
		assertEquals(activeSitesRightInclusive.get(0).getMaxOccupancy(), siteRowSet.getInt("max_occupancy"));
		assertEquals(activeSitesRightInclusive.get(0).getMaxRvLength(), siteRowSet.getInt("max_rv_length"));
		assertEquals(activeSitesRightInclusive.get(0).isAccessible(), siteRowSet.getBoolean("accessible"));
		assertEquals(activeSitesRightInclusive.get(0).isAccessible(), siteRowSet.getBoolean("utilities"));
	
		siteRowSet.next();
		assertEquals(activeSitesRightInclusive.get(1).getSiteId(), siteRowSet.getInt("site_id"));
		assertEquals(activeSitesRightInclusive.get(1).getSiteNumber(), siteRowSet.getInt("site_number"));
		assertEquals(activeSitesRightInclusive.get(1).getCampgroundId(), siteRowSet.getInt("campground_id"));
		assertEquals(activeSitesRightInclusive.get(1).getMaxOccupancy(), siteRowSet.getInt("max_occupancy"));
		assertEquals(activeSitesRightInclusive.get(1).getMaxRvLength(), siteRowSet.getInt("max_rv_length"));
		assertEquals(activeSitesRightInclusive.get(1).isAccessible(), siteRowSet.getBoolean("accessible"));
		assertEquals(activeSitesRightInclusive.get(1).isAccessible(), siteRowSet.getBoolean("utilities"));
	}
}
