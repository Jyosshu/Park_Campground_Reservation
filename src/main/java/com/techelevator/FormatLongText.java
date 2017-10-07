package com.techelevator;

public class FormatLongText {
	
	public static String formatLongText(String text, int lineWidth){
		String s="";
		int rightScan=0;
		while(rightScan<text.length()){
			rightScan++;
			if(rightScan==lineWidth){
				while(!text.substring(rightScan, rightScan+1).equals(" ")){
					rightScan++;
				}
				s+=text.substring(0, rightScan)+"\n";
				text=text.substring(rightScan+1);
				rightScan=0;
			}
		}
		
		return s+text;
	}
}
