package com.techelevator;

import javax.sql.DataSource;

import org.apache.commons.dbcp2.BasicDataSource;

import java.util.Date;
import java.util.List;
import java.util.Scanner;


public class CampgroundCLI {
	private static final String RETURN_TO_PREVIOUS_SCREEN = "Return to Previous Screen";
	private static final String VIEW_CAMPGROUNDS = "View Campgrounds";
	private static final String SEARCH_FOR_RESERVATION = "Search for Reservation";
	private static final String[] MENU_FOR_RESERVATIONS = {VIEW_CAMPGROUNDS, SEARCH_FOR_RESERVATION, RETURN_TO_PREVIOUS_SCREEN};
//	private static final String MAIN_MENU_OPTION_EMPLOYEES = "Employees";
//	private static final String MAIN_MENU_OPTION_DEPARTMENTS = "Departments";
//	private static final String MAIN_MENU_OPTION_PROJECTS = "Projects";
	private static final String MAIN_MENU_OPTION_EXIT = "Exit";
	private List<Park> parks;
	private String[] parkMenuOptions; 

	private static final String MENU_OPTION_RETURN_TO_MAIN = "Return to main menu";


	private Menu menu;
	private ParkDAO parkDAO;
	
	public static void main(String[] args) {
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
		parkMenuOptions=createParkOptions(parkDAO.getParks()); 
	}
	
	private String[] createParkOptions(List<Park> parkOptionsList) {
		String[] parkOptionsArr=new String[parkOptionsList.size() + 1];
		for(int i=0; i<parkOptionsArr.length - 1; i++){
			parkOptionsArr[i]=parkOptionsList.get(i).getName();
		}
		parkOptionsArr[parkOptionsArr.length - 1] = MAIN_MENU_OPTION_EXIT;
		return parkOptionsArr;
	}

	public void run() {
		while(true) {
			String choice = (String)menu.getChoiceFromOptions(parkMenuOptions);
			if(choice.equals(MAIN_MENU_OPTION_EXIT)) {
				System.exit(0);
			} else {
				//handleParks(Integer.parseInt(choice));
			}
		}
	}
	
	private void listParks(List<Park> parks) {
		System.out.println();
		if(parks.size() > 0) {
			for(Park tempPark : parks) {
				System.out.println(tempPark.getName());
			}
		} else {
			System.out.println("\n*** No results ***");
		}
	}
//	
//	private void handleParks(int choice) {
//
//		String choice = (String)menu.getChoiceFromOptions(PARK_MENU_OPTIONS);
//		if(choice.equals(RETURN_TO_PREVIOUS_SCREEN)) {
//			run();
//		} else if(choice.equals(VIEW_CAMPGROUNDS)) {
//			handleSearchCampgrounds();
//		} else if(choice.equals(SEARCH_FOR_RESERVATION)) {
//			handleSearchForReservation();
//		} 
//	}
//	
//	private void handleSearchForReservation(Date beginDate, Date endDate) {
//		
//	}
//	private void handleListAllDepartments() {
//		List<Park> allParks = parkDAO.getAllPark();
//		listDepartments(allPark);
//	}

	@SuppressWarnings("resource")
	private String getUserInput(String prompt) {
		System.out.print(prompt + " >>> ");
		return new Scanner(System.in).nextLine();
	}

}
