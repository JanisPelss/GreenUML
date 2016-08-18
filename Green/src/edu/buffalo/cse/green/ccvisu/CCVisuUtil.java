/* This file is part of Green.
 *
 * Copyright (C) 2010 The Research Foundation of State University of New York
 * All Rights Under Copyright Reserved, The Research Foundation of S.U.N.Y.
 * 
 * Green is free software, licensed under the terms of the Eclipse
 * Public License, version 1.0.  The license is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package edu.buffalo.cse.green.ccvisu;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import ccvisu.GraphData;
import ccvisu.Options;
import ccvisu.Position;

/*****************************************************************
 * Utility class.
 * @author   François Rey
 *****************************************************************/
public class CCVisuUtil {

	// Get access to Position private constructor
    static Constructor<Position> positionConstructor;
    static {
    	try {
			positionConstructor = Position.class.getDeclaredConstructor(new Class[0]);
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	positionConstructor.setAccessible(true);
    }
    static final Object[] emtpyObjectArray = new Object[0];
    
	public static Position newPosition() throws IllegalArgumentException, InstantiationException, IllegalAccessException, InvocationTargetException {
		return positionConstructor.newInstance(emtpyObjectArray);
	}
	public static Position newPosition(float x, float y) throws IllegalArgumentException, InstantiationException, IllegalAccessException, InvocationTargetException {
		return newPosition(x,y,0);
	}
	public static Position newPosition(float x, float y, float z) throws IllegalArgumentException, InstantiationException, IllegalAccessException, InvocationTargetException {
		Position pos = newPosition();
		pos.x=x;
		pos.y=y;
		pos.z=z;
		return pos;
	}
	static public Options newOptions(GraphData graph, 
	          int nrIterations, 
	          float attrExponent, 
	          float repuExponent, 
	          boolean vertRepu,
	          boolean noWeight,
	          float gravitation,
	          GraphData initialLayout,
	          boolean fixedInitPos) {
		int n = 10;
		if (vertRepu) n++;
		if (noWeight) n++;
		if (fixedInitPos) n++;
		String[] args = new String[n];
		n = 0;
		args[n++] = "-inFormat";
		args[n++] = "AUX";
		args[n++] = "-iter";
		args[n++] = Integer.toString(nrIterations);
		args[n++] = "-attrExp";
		args[n++] = Float.toString(attrExponent);
		args[n++] = "-repuExp";
		args[n++] = Float.toString(repuExponent);
		args[n++] = "-grav";
		args[n++] = Float.toString(gravitation);
		if (vertRepu) args[n++]= "-vertRepu";
		if (vertRepu) args[n++]= "-noWeight";
		if (vertRepu) args[n++]= "-fixedInitPos";
		Options opt = new Options();
		opt.parseCmdLine(args);
		opt.graph = graph;
		opt.initialLayout = initialLayout;
		return opt;
	}
}
