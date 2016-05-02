package tags;


import java.util.TimeZone;

public class ZoneWrapper  {

    public ZoneWrapper(){}
	
	public String[] getAvailableIDs(){
	
	return TimeZone.getAvailableIDs();
	
	}

}