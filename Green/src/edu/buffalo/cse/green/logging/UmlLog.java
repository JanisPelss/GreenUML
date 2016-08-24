package edu.buffalo.cse.green.logging;

import java.io.IOException;
import java.io.PrintStream;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class UmlLog {
	   
	private String _fileName;
	private boolean _firstTime = true;
	
	
	public UmlLog(String fileName) {
		_fileName = fileName;
	}
	
	
	public void addToLog(String message) throws IOException, SecurityException {
		FileHandler fh = new FileHandler(_fileName, true);  // Throws.
		fh.setFormatter(new SimpleFormatter()); 
		
		Logger logger = Logger.getLogger("UmlLogger");
		logger.setUseParentHandlers(false);
		logger.addHandler(fh);
		
		if (_firstTime) {
	    	_firstTime = false;
	    	logger.info("GreenUML Debug Log");
	    }
		logger.info(message);
		
		fh.close();  // Throws.
	}
	
	
	// ----- Global Settings -----
	
	
	private static final PrintStream stdout = System.out;
	private static final PrintStream stderr = System.err;
	
	
	public static void redirectOutput( boolean prefLogToStd, boolean prefLogToFile,
	                                   String prefLogFileName ) {
		if (prefLogToStd && !prefLogToFile) {
			// If logging is enabled to stdout/stderr only, the standard behaviour is in use:
			System.setOut(stdout);
			System.setErr(stderr);
		}
		else {
			OutputStreamSplitter out = new OutputStreamSplitter();
			OutputStreamSplitter err = new OutputStreamSplitter();
			if (prefLogToStd) {
				out.add(stdout);
				err.add(stderr);
			}
			if (prefLogToFile && prefLogFileName.length() > 0) {
				UmlLog umlLog = new UmlLog(prefLogFileName);
				out.add(new UmlLogAdapter(umlLog));
				err.add(new UmlLogAdapter(umlLog));
			}
			
			System.setOut(new PrintStream(out, true));
			System.setErr(new PrintStream(err, true));
		}
	}
}
