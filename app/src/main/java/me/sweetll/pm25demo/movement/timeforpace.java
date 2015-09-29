package me.sweetll.pm25demo.movement;

import java.util.Calendar;

public class timeforpace {

	private boolean flag=false;
	public boolean isminute(){
		Calendar c=Calendar.getInstance();
		if(c.get(Calendar.SECOND)==0) 
		flag=true;
		
		return flag;
	}
	
}
