package Menu;



import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.text.DateFormatSymbols;
import java.util.Scanner;

import com.techelevator.pojo.Campground;
import com.techelevator.pojo.Park;
import com.techelevator.pojo.Site;

public class Menu {

	private PrintWriter out;
	private Scanner in;
	String[] months=(new DateFormatSymbols()).getMonths();
	
	public Menu(InputStream input, OutputStream output) {
		this.out = new PrintWriter(output);
		this.in = new Scanner(input);
	}
	
	public Object getChoiceFromSiteOptions(Object[] options, BigDecimal price) {
		Object choice = null;
		while(choice == null && options.length!=0) {
			displayMenuSiteOptions(options, price);
			choice = getChoiceFromUserInput(options, true);
		}
		return choice;
	}
	
	public Object getChoiceFromParkOptions(Object[] options) {
		Object choice = null;
		while(choice == null && options.length!=0) {
			displayMenuParkOptions(options);
			choice = getChoiceFromUserInput(options, false);
		}
		return choice;
	}
	
	public Object getChoiceFromCampgroundOptions(Object[] options) {
		Object choice = null;
		while(choice == null && options.length!=0) {
			displayMenuCampgroundOptions(options);
			choice = getChoiceFromUserInput(options , true);
		}
		return choice;
	}

	public Object getChoiceFromOptions(Object[] options) {
		Object choice = null;
		while(choice == null) {
			displayMenuOptions(options);
			choice = getChoiceFromUserInput(options, false);
		}
		return choice;
	}

	private Object getChoiceFromUserInput(Object[] options, boolean zeroToExit) {
		Object choice = null;
		String userInput = in.nextLine();
		try {
			int selectedOption = Integer.valueOf(userInput);
			if(zeroToExit && selectedOption==0){
				choice=0; 
			}else if(selectedOption <= options.length && selectedOption>0) {
					choice = options[selectedOption - 1];
			}
		} catch(NumberFormatException e) {
			// eat the exception, an error message will be displayed below since choice will be null
		}
		if(choice == null) {
			out.println("\n*** "+userInput+" is not a valid option ***\n");
		}
		return choice;
	}
	
	private void displayMenuOptions(Object[] options) {
		out.println();
		for(int i = 0; i < options.length; i++) {
			int optionNum = i+1;
			out.println(optionNum+") "+options[i]);
		}
		out.print("\nPlease choose an option >>> ");
		out.flush();
	}
	

	private void displayMenuParkOptions(Object[] options) {
		out.println();
		int i=0;
		for(; i < options.length; i++) {
			int optionNum = i+1;
			if(options[i] instanceof Park){
				out.println(optionNum+") "+((Park)options[i]).getName());
			}
			else{
				out.println(optionNum+") "+options[i]);
			}
		}
		out.print("\nPlease choose an option >>> ");
		out.flush();
	}
	
	private void displayMenuCampgroundOptions(Object[] options) {
		out.println(String.format("\n%-35s %-15s %-15s %-15s", "Name", "Open", "Close", "DailyFee"));
		
		for(int i = 0; i < options.length; i++) {
			int optionNum = i+1;
			Campground c=(Campground)options[i];
			out.println(String.format("%-35s %-15s %-15s %-15s", optionNum+") "+c.getName(), months[Integer.parseInt(c.getOpenFromMm())-1], months[Integer.parseInt(c.getOpenToMm())-1], "$"+ c.getDailyFee()));
		}
		out.print("\nWhich campground (enter 0 to cancel)? __ ");
		out.flush();
	}
	
	private void displayMenuSiteOptions(Object[] options, BigDecimal price) {
		out.println(String.format("\n%-10s %-15s %-15s %-15s %-10s %-10s","Site No.", "Max Occup.", "Accessible?", "Max RV Length", "Utility", "Cost"));
		
		for(int i = 0; i < options.length; i++) {
			int optionNum = i+1;
			Site s=(Site)options[i];
			out.println(String.format("%-10s %-15d %-15s %-15d %-10s %-10s", optionNum+") "+s.getSiteNumber(), s.getMaxOccupancy(), s.isAccessible(), s.getMaxOccupancy(), s.isUtilities(), "$"+price));
		}
		out.print("\nWhich site should be reserved (enter 0 to cancel)? __ ");
		out.flush();
	}
}
