package edu.buffalo.cse.green;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class UmlLog {
	   
	public static int i = 0;
	
	public static void addToLog(String message) {
	    Logger logger = Logger.getLogger("MyLog");  
	    FileHandler fh;  
	    logger.setUseParentHandlers(false);
	    if (i == 0) {
	    	try {  
	    		// This block configures the logger with handler and formatter  
	    		fh = new FileHandler(System.getProperty("user.dir") + "GreenUmlLog.log");  
	    		logger.addHandler(fh);
	    		SimpleFormatter formatter = new SimpleFormatter();  
	    		fh.setFormatter(formatter);  

	    		// The following statement is used to log any messages  
	    		logger.info("GreenUML debug log");  
	    	} 
	    	
	    	catch (SecurityException e) {  
	    		e.printStackTrace();  
	    	} 
	    	
	    	catch (IOException e) {  
	    		e.printStackTrace();  
	    	}  
	    	
	    	i++;
	    }
	    
	    logger.info(message);	
	}
}
