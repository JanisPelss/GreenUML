/* This file is part of Green.
 *
 * Copyright (C) 2005 The Research Foundation of State University of New York
 * All Rights Under Copyright Reserved, The Research Foundation of S.U.N.Y.
 * 
 * Green is free software, licensed under the terms of the Eclipse
 * Public License, version 1.0.  The license is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package edu.buffalo.cse.green.dialogs;

/**
 * Information for types used in Green's dialogs.
 * 
 * @author rjtruban
 */
public class TypeNameInfo {
	private String _packageName;

	private String _simpleName;

	private String[] _enclosingTypeNames;

	public TypeNameInfo(
			String packageName,
			String simpleName,
			String[] enclosingTypeNames) {
		_packageName = packageName;
		_simpleName = simpleName;
		_enclosingTypeNames = enclosingTypeNames;
	}

	public TypeNameInfo(
			char[] packageName,
			char[] simpleName,
			char[][] enclosingTypeNames) {
		this(new String(packageName), new String(simpleName),
				convertCharDoubleArrayToStringArray(enclosingTypeNames));
	}

	/**
	 * Converts type information from an eclipse-standard format to a more
	 * usable array of Strings.
	 * 
	 * @param charDoubleArray - The character array to convert.
	 * @return The converted array.
	 */
	private static String[] convertCharDoubleArrayToStringArray(
			char[][] charDoubleArray) {
		String[] result = new String[charDoubleArray.length];
		for (int i = 0; i < charDoubleArray.length; i++) {
			result[i] = new String(charDoubleArray[i]);
		}
		return result;
	}

	/**
	 * @return The enclosing type names.
	 */
	public String[] getEnclosingTypeNames() {
		return _enclosingTypeNames;
	}

	/**
	 * @return The package name.
	 */
	public String getPackageName() {
		return _packageName;
	}

	/**
	 * @return The simple name.
	 */
	public String getSimpleName() {
		return _simpleName;
	}
}
