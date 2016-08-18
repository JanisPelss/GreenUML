/* This file is part of Green.
 *
 * Copyright (C) 2005 The Research Foundation of State University of New York
 * All Rights Under Copyright Reserved, The Research Foundation of S.U.N.Y.
 * 
 * Green is free software, licensed under the terms of the Eclipse
 * Public License, version 1.0.  The license is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package edu.buffalo.cse.green.editor.model.filters;

import static org.eclipse.jdt.core.Flags.AccDefault;
import static org.eclipse.jdt.core.Flags.AccPrivate;
import static org.eclipse.jdt.core.Flags.AccProtected;
import static org.eclipse.jdt.core.Flags.AccPublic;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;

/**
 * An enumeration over possible visibilities used by Green's filters to
 * determine if the visibility of a filter matches the visibility of a member.
 * 
 * @author bcmartin
 */
public enum MemberVisibility {
	ANY("any", 3), DEFAULT("default", null, AccDefault),
	PRIVATE("private", AccPrivate), PROTECTED("protected", AccProtected),
	PUBLIC("public", AccPublic);
	
	private int _visibility;

	private String _codeText;
	
	private String _name;

	private static Map<Integer, MemberVisibility> _visMap;
	
	MemberVisibility(String name, int visibility) {
		this(name, name, visibility);
	}

	MemberVisibility(String name, String codeText, int visibility) {
		_codeText = codeText;
		_name = name;
		_visibility = visibility;
		addVisibilityMapping(visibility, this);
	}
	
	/**
	 * Maps a visibility value to a <code>MemberVisibility</code> instance.
	 * 
	 * @param visibilityNum - The given visibility value.
	 * @param visibility - The <code>MemberVisibility</code> instance to map the
	 * given value to.
	 */
	private void addVisibilityMapping(int visibilityNum,
			MemberVisibility visibility) {
		if (_visMap == null) {
			_visMap = new HashMap<Integer, MemberVisibility>();
		}
		
		_visMap.put(visibilityNum, visibility);
	}

	/**
	 * @param member - The member to check the filter against.
	 * @return true if the filter applied, false otherwise.
	 */
	public boolean match(IMember member) {
		if (_visibility == ANY._visibility) return true;
		
		try {
			// handle interfaces: public-only 
			if (member.getParent() instanceof IType) {
				IType parent = (IType) member.getParent();
				if (parent.isInterface()) {
					return (_visibility == AccPublic);
				}
			}
			
			return (member.getFlags() & _visibility) == _visibility;
		} catch (JavaModelException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	/**
	 * @param visibility - The given visibility value.
	 * @return The <code>MemberVisibility</code> instance corresponding to the
	 * given visibility value. 
	 */
	public static MemberVisibility makeVisibility(int visibility) {
		return _visMap.get(visibility);
	}

	/**
	 * @see java.lang.Enum#toString()
	 */
	public String toString() {
		return "" + _visibility;
	}
	
	/**
	 * @return The integer value representing this visibility instance.
	 */
	public int intValue() {
		return _visibility;
	}
	
	/**
	 * @return A <code>String</code> representation of this visibility.
	 */
	public String getName() {
		return _name;
	}

	public String getCodeText() {
		return _codeText;
	}
}
