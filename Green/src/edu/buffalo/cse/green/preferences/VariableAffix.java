/* This file is part of Green.
 *
 * Copyright (C) 2005 The Research Foundation of State University of New York
 * All Rights Under Copyright Reserved, The Research Foundation of S.U.N.Y.
 * 
 * Green is free software, licensed under the terms of the Eclipse
 * Public License, version 1.0.  The license is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package edu.buffalo.cse.green.preferences;

import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IPreferencesService;

/**
 * Enum for dealing with the Eclipse prefix/suffix preference of
 * generated variable names.
 *  
 * @author Gene Wang
 *
 */
public enum VariableAffix {
	
	FieldPrefix("org.eclipse.jdt.core.codeComplete.fieldPrefixes"),
	FieldSuffix("org.eclipse.jdt.core.codeComplete.fieldSuffixes"),
	LocalPrefix("org.eclipse.jdt.core.codeComplete.localPrefixes"),
	LocalSuffix("org.eclipse.jdt.core.codeComplete.localSuffixes"),
	ParameterPrefix("org.eclipse.jdt.core.codeComplete.argumentPrefixes"),
	ParameterSuffix("org.eclipse.jdt.core.codeComplete.argumentSuffixes");
	
	private String _id;

	VariableAffix(String id) {
		_id = id;
	}
	
	public String getPreferenceId() {
		return _id;
	}
	
	public static String getAffixString(VariableAffix affix)
	{
		IPreferencesService ips = Platform.getPreferencesService();
		return ips.getString("org.eclipse.jdt.core", affix.getPreferenceId(), "", null);
	}
}