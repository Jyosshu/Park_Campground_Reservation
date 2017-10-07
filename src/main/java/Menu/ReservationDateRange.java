package Menu;

import java.io.InputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

public class ReservationDateRange {
	private static final String MAX_END_DATE="01-01-2100";

	private Scanner in;
	DateFormat sdf;
	private Date startDate;
	private Date endDate;
	
	public ReservationDateRange(InputStream input, String dateFormat) throws ParseException {
		this.in = new Scanner(input);
		sdf=new SimpleDateFormat(dateFormat);
		sdf.setLenient(false);
		makeRdr();
	}
	
	private void makeRdr() throws ParseException{
		while(true){
			setStartAndEndDate();
			if(!correctDateOrder()){
				System.out.println("Incorrect Order");
			} else {
				break;
			}
		} 
	}
	
	public void setStartAndEndDate() throws ParseException{
		while(true){
			startDate=setDate("What is the arrival date? __-__-____", true);
			if(notInStartDateInRange()){
				System.out.println("Start Date Not in Range");
			} else{
				break;
			}
		}
		while(true){
			endDate=setDate("What is the departure date? __-__-____", false);
			if(notInEndDateInRange()){
				System.out.println("End Date Not in Range");
			} else{
				break;
			}
		} 
		
	}
	
	private Date setDate(String message, boolean isStartDate){
		Date newDate=null;
		while(newDate==null){
			try{
				newDate=sdf.parse(getUserInput(message));
			} catch (ParseException e) {
				System.out.println("Bad Date! Try Again.");
			} 
		}
		return newDate;
	}

	
	public boolean correctDateOrder(){
		return !startDate.before(endDate);
	}

	private boolean notInStartDateInRange() throws ParseException{ 
		return !startDate.after(new Date());
	}
	
	private boolean notInEndDateInRange() throws ParseException{ 
		return !endDate.before(sdf.parse(MAX_END_DATE));
	}
	
	
	public Date getStartDate() {
		return startDate;
	}

	public Date getEndDate() {
		return endDate;
	}
	
	private String getUserInput(String prompt) {
		System.out.print(prompt + " >>> ");
		return in.nextLine();
	}

}
