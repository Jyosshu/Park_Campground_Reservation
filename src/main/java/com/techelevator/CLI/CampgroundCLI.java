package com.techelevator.CLI;

import java.math.BigDecimal;
import java.text.DateFormatSymbols;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

import javax.sql.DataSource;

import org.apache.commons.dbcp2.BasicDataSource;

import com.techelevator.FormatLongText;
import com.techelevator.JDBC.JDBCCampgroundDAO;
import com.techelevator.JDBC.JDBCParkDAO;
import com.techelevator.JDBC.JDBCReservationDAO;
import com.techelevator.model.CampgroundDAO;
import com.techelevator.model.JDBCSiteDAO;
import com.techelevator.model.ParkDAO;
import com.techelevator.model.ReservationDAO;
import com.techelevator.model.SiteDAO;
import com.techelevator.pojo.Campground;
import com.techelevator.pojo.Park;
import com.techelevator.pojo.Site;

import Menu.Menu;
import Menu.ReservationDateRange;


public class CampgroundCLI {
	private static final String RETURN_TO_PREVIOUS_SCREEN = "Return to Previous Screen";
	private static final String VIEW_CAMPGROUNDS = "View Campgrounds";
	private static final String SEARCH_FOR_RESERVATION = "Search for Reservation";
	private static final String[] MENU_FOR_OPTIONS_CAMPGROUND = {VIEW_CAMPGROUNDS, SEARCH_FOR_RESERVATION, RETURN_TO_PREVIOUS_SCREEN};
	private static final String MAIN_MENU_OPTION_EXIT = "Exit";
	private static final String SEARCH_FOR_AVAILIBLE_RESERVATIONS = "Search For Availible Reservations";
	private static final String[] SUB_MENU_AFTER_CAMPGROUND_VIEW={SEARCH_FOR_AVAILIBLE_RESERVATIONS, RETURN_TO_PREVIOUS_SCREEN};
	
	private int selectedParkId;
	List<Campground> campgroundsInPark;
	
	String[] months=(new DateFormatSymbols()).getMonths();

	private Menu menu;
	
	private ParkDAO parkDAO;
	private CampgroundDAO campgroundDAO;
	private SiteDAO siteDAO;
	private ReservationDAO reservationDAO;

	
	public static void main(String[] args) throws ParseException {
		BasicDataSource dataSource = new BasicDataSource();
		dataSource.setUrl("jdbc:postgresql://localhost:5432/national_park_reservation");
		dataSource.setUsername("postgres");
		dataSource.setPassword("postgres1");
		
		CampgroundCLI application = new CampgroundCLI(dataSource);
		application.run();
	}

	public CampgroundCLI(DataSource datasource) {
		this.menu = new Menu(System.in, System.out);

		parkDAO = new JDBCParkDAO(datasource);
		campgroundDAO=new JDBCCampgroundDAO(datasource);
		siteDAO=new JDBCSiteDAO(datasource);
		reservationDAO=new JDBCReservationDAO(datasource);
	}

	public void run() throws ParseException {
		while(true) {
			List<Object> parks = new ArrayList<>(parkDAO.getParks()); // private instance List of Parks.
			parks.add(MAIN_MENU_OPTION_EXIT);
			Object choice = menu.getChoiceFromParkOptions(parks.toArray());
			if(choice.equals(MAIN_MENU_OPTION_EXIT)) {
				System.exit(0);
			} else {
				selectedParkId=((Park)choice).getParkId();
				campgroundsInPark= campgroundDAO.getCampgroundsByParkId(selectedParkId);
				handleParkSelection();
			}
		}
	}
	
	private void handleParkSelection() throws ParseException {
		displayPark();
		String choice = (String)menu.getChoiceFromOptions(MENU_FOR_OPTIONS_CAMPGROUND);
		if(choice.equals(RETURN_TO_PREVIOUS_SCREEN)) {
			run();
		} else if(choice.equals(VIEW_CAMPGROUNDS)) {
			handleViewCampgrounds();
		} else if(choice.equals(SEARCH_FOR_RESERVATION)) {
			handleSearchForReservation();
		} 
	}

	private void handleViewCampgrounds() throws ParseException {
		displayCampgrounds(campgroundsInPark);
		
		String choice = (String)menu.getChoiceFromOptions(SUB_MENU_AFTER_CAMPGROUND_VIEW);
		if(choice.equals(RETURN_TO_PREVIOUS_SCREEN)) {
			handleParkSelection();
		} else if(choice.equals(SEARCH_FOR_AVAILIBLE_RESERVATIONS)) {
			handleSearchForReservation();
		}
	}
	
	private void handleSearchForReservation() throws ParseException  {
		Object choice = menu.getChoiceFromCampgroundOptions(campgroundsInPark.toArray());
		if(choice instanceof Integer && (Integer)choice==0){
			handleViewCampgrounds();
		} else{
			ReservationDateRange rdr=new ReservationDateRange(System.in, "MM-dd-yyyy");
			Date arrivalDate=rdr.getStartDate();
			Date departureDate=rdr.getEndDate();
			
			List<Site> availibleSites=siteDAO.getActiveSitesByCampground(((Campground)choice).getCampgroundId(), arrivalDate, departureDate);
		
			if(availibleSites.size()==0){
				System.out.println("No sites availible");
			} else {
				int days=getDayDifference(departureDate, arrivalDate);
				handleMakeReservation(availibleSites, arrivalDate, departureDate, ((Campground)choice).getDailyFee().multiply(new BigDecimal(days)));
			}
		}
		
	}

	
	private void handleMakeReservation(List<Site> availibleSites, Date arrivalDate, Date departureDate, BigDecimal price) throws ParseException {
		Object choice = menu.getChoiceFromSiteOptions(availibleSites.toArray(), price);
		if(choice instanceof Integer && (Integer)choice==0){
			handleViewCampgrounds();
		} else{
			String name=getUserInput("What name should the reservation be made under? __");
			int reservationId= reservationDAO.reserveSite(((Site)choice).getSiteId(), name ,arrivalDate, departureDate);
			System.out.println("\nThe reservation has been made and the confirmation id is {"+ reservationId+"}");
		}
	}
	
	
	
	private void displayPark(){
		Park currentPark=parkDAO.getParkById(selectedParkId);
		System.out.println("\nPark Information Screen");
		System.out.println(currentPark.getName());
		System.out.println(String.format("%-20s%-20s", "Location:", currentPark.getLocation()));
		System.out.println(String.format("%-20s%-20s", "Established:", currentPark.getEstablishDate()));
		System.out.println(String.format("%-20s%-20s", "Area:", NumberFormat.getIntegerInstance().format(currentPark.getArea()) + " sq mi"));
		System.out.println(String.format("%-20s%-20s", "Annual Visitors:", NumberFormat.getIntegerInstance().format(currentPark.getVisitors())));
		System.out.println("\n"+FormatLongText.formatLongText(currentPark.getDescription(), 60));
	}
	
	private void displayCampgrounds(List<Campground> campgrounds){
		System.out.println(String.format("\n%-35s %-15s %-15s %-15s", "Name", "Open", "Close", "DailyFee"));
		for(Campground c: campgrounds){
			System.out.println(String.format("%-35s %-15s %-15s %-15s", c.getName(), months[Integer.parseInt(c.getOpenFromMm())-1], months[Integer.parseInt(c.getOpenToMm())-1], "$"+ c.getDailyFee()));
		}
	}
	
//	private void displaySites(List<Site> sites, BigDecimal price){
//		System.out.println(String.format("\n%-10s %-15s %-15s %-15s %-10s %-10s","Site No.", "Max Occup.", "Accessible?", "Max RV Length", "Utility", "Cost"));
//		for(Site s: sites){
//			System.out.println(String.format("%-10d %-15d %-15s %-15d %-10s %-10s", s.getSiteNumber(), s.getMaxOccupancy(), s.isAccessible(), s.getMaxOccupancy(), s.isUtilities(), "$"+price));
//		}
//		
//	}
	
	
	
	public static int getDayDifference(Date date1, Date date2) {
	    long differenceInMillis = date1.getTime() - date2.getTime();
	    int differenceInDays = (int) (differenceInMillis / (24 * 60 * 60 * 1000));
	    return differenceInDays;
	}
	
	@SuppressWarnings("resource")
	private String getUserInput(String prompt) {
		System.out.print(prompt + " >>> ");
		return new Scanner(System.in).nextLine();
	}
	
	

}
