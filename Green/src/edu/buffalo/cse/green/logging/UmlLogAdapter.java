package edu.buffalo.cse.green.logging;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import edu.buffalo.cse.green.logging.UmlLog;


/**
 * Should be used as an underlying OutputStream of an auto-flush PrintStream.
 * Whenever the PrintStream invokes flush(), a call to UmlLog.addToLog() is made.
 */
public class UmlLogAdapter extends ByteArrayOutputStream {

	private static String NL = System.getProperty("line.separator");
	
	private UmlLog _umlLog;


	public UmlLogAdapter(UmlLog umlLog) {
		_umlLog = umlLog;
	}


	@Override
	public void flush() throws IOException, SecurityException {
		super.flush();  // Throws.

		String s = this.toString();
		super.reset();

		// Empty lines are not to be logged:
		if (s.length() > 0 && !s.equals(NL))
			_umlLog.addToLog(s);  // Throws.
	}
	
}
