package edu.buffalo.cse.green.logging;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;


public class OutputStreamSplitter extends OutputStream {

	private ArrayList<OutputStream> _outs = new ArrayList<OutputStream>();


	public OutputStreamSplitter() {}
	
	
	public void add(OutputStream s) {
		_outs.add(s);
	}


	// I/O exceptions are suppressed to eliminate the possibility of infinite recursion. (E.g.: if
	// printStackTrace() prints to stderr that was redirected to this OutputStreamSplitter which in
	// turn throws an exception, its handler can call printStackTrace() again, etc.)
	
	@Override
	public void write(byte[] b) {
		for (OutputStream s : _outs) {
			try {
				s.write(b);
			} catch (Exception e) {}
		}
	}

	@Override
	public void write(byte[] b, int off, int len) {
		for (OutputStream s : _outs) {
			try {
				s.write(b, off, len);
			} catch (Exception e) {}
		}
	}

	@Override
	public void write(int b) {
		for (OutputStream s : _outs) {
			try {
				s.write(b);
			} catch (Exception e) {}
		}
	}

	@Override
	public void flush() {
		for (OutputStream s : _outs) {
			try {
				s.flush();
			} catch (Exception e) {}
		}
	}

	@Override
	public void close() {
		for (OutputStream s : _outs) {
			try {
				s.close();
			} catch (Exception e) {}
		}
	}

}