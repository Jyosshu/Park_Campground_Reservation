package com.techelevator;

import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;

public class JDBCCampgroundDAO implements CampgroundDAO {
	private JdbcTemplate jdbcTemplate;
	
	public JDBCCampgroundDAO(DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}
	
	@Override
	public List<Campground> getCampgroundsByParkId(int parkId) {
		List<Campground> newCampList = new ArrayList<>();
		String sqlSelectCamp = "SELECT * FROM campground WHERE park_id =? ORDER BY name";
		
		SqlRowSet campRowSet =jdbcTemplate.queryForRowSet(sqlSelectCamp, parkId);
		
		while (campRowSet.next()) {
			newCampList.add(mapRowToCampground(campRowSet));
		}
		
		return newCampList;
	}
	
	public Campground mapRowToCampground(SqlRowSet campRowSet) {
		Campground newCamp = new Campground(campRowSet.getInt("campground_id"),campRowSet.getInt("park_id"), 
								campRowSet.getString("name"), campRowSet.getString("open_from_mm"), campRowSet.getString("open_to_mm"),
								campRowSet.getBigDecimal("daily_fee"));
		
		return newCamp;
	}
}
