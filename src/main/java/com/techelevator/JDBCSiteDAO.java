package com.techelevator;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;

public class JDBCSiteDAO implements SiteDAO {

	private JdbcTemplate jdbcTemplate;

	public JDBCSiteDAO(DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}
	
	@Override
	public List<Site> getActiveSitesByCampground(int campgroundId, Date beginDate, Date endDate) {

		List<Site> newSiteList = new ArrayList<>();
		
		String sqlGetCampground="SELECT * FROM campground WHERE campground_id=?";
		SqlRowSet campRowSet=jdbcTemplate.queryForRowSet(sqlGetCampground, campgroundId);
		campRowSet.next();
		
		JDBCCampgroundDAO campDao=new JDBCCampgroundDAO(jdbcTemplate.getDataSource());
		Campground camp=campDao.mapRowToCampground(campRowSet);
		
		int fromMm=Integer.parseInt(camp.getOpenFromMm());
		int toMm=Integer.parseInt(camp.getOpenToMm());
		int beginDateMonth=Integer.parseInt(String.valueOf(beginDate.getMonth()))+1;
		int endDateMonth=Integer.parseInt(String.valueOf(endDate.getMonth()))+1;
		
		System.out.println(fromMm + " " + toMm + " " + beginDateMonth + " " + endDateMonth);
		
		if(beginDateMonth>=fromMm && endDateMonth<=toMm){
			String sqlSelectSite = "SELECT * FROM site WHERE site_id NOT IN "  
					+ "(SELECT DISTINCT site_id FROM reservation "
					+ "WHERE (? > from_date AND ?<=to_date) "
					+ "OR (? < to_date 	AND ?>=from_date) "
					+ "OR (? <= from_date AND ? >= to_date) "
					+ "OR (?>from_date AND ?<to_date))"
					+ "ORDER BY site_number";
			
	
			SqlRowSet siteRowSet =jdbcTemplate.queryForRowSet(sqlSelectSite, endDate, endDate, beginDate, beginDate, beginDate, endDate, beginDate, endDate);
			
			while (siteRowSet.next()) {
				newSiteList.add(mapRowToSite(siteRowSet));
			}
		}
		return newSiteList;
	}
	
	public Site mapRowToSite(SqlRowSet siteRowSet) {
		Site newSite = new Site(siteRowSet.getInt("site_id"),siteRowSet.getInt("campground_id"), 
								siteRowSet.getInt("site_number"), siteRowSet.getInt("max_occupancy"), 
								siteRowSet.getBoolean("accessible"),
								siteRowSet.getInt("max_rv_length"), siteRowSet.getBoolean("utilities"));
		
		return newSite;
	}
}
