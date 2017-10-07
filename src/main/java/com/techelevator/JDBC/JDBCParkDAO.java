package com.techelevator.JDBC;

import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import com.techelevator.model.ParkDAO;
import com.techelevator.pojo.Park;

public class JDBCParkDAO implements ParkDAO {

	private JdbcTemplate jdbcTemplate;

	public JDBCParkDAO(DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}
	
	//test
	@Override
	public Park getParkById(int id) {
		Park park;
		String sqlSelectPark = "SELECT * FROM park WHERE park_id=? ORDER BY name";
		
		SqlRowSet parkRowSet =jdbcTemplate.queryForRowSet(sqlSelectPark, id);
		
		parkRowSet.next();
		park=mapRowToPark(parkRowSet);
	
		return park;
	}
	
	
	@Override
	public List<Park> getParks() {
		List<Park> newParkList = new ArrayList<>();
		String sqlSelectPark = "SELECT * FROM park ORDER BY name";
		
		SqlRowSet parkRowSet =jdbcTemplate.queryForRowSet(sqlSelectPark);
		
		while (parkRowSet.next()) {
			newParkList.add(mapRowToPark(parkRowSet));
		}
		
		return newParkList;
	}

	
	private Park mapRowToPark(SqlRowSet parkRowSet) {
		Park newPark = new Park(parkRowSet.getInt("park_id"),parkRowSet.getString("name"), 
								parkRowSet.getString("location"), parkRowSet.getDate("establish_date"), parkRowSet.getInt("area"),
								parkRowSet.getInt("visitors"), parkRowSet.getString("description"));
		
		return newPark;
	}
}
