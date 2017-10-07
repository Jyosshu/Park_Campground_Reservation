package com.techelevator.model;

import java.util.Date;
import java.util.List;

import com.techelevator.pojo.Site;

public interface SiteDAO {

	public List<Site> getActiveSitesByCampground(int campgroundId, Date beginDate, Date endDate);
	
}
