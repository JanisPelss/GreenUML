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

import static org.eclipse.jdt.core.IJavaElement.FIELD;
import static org.eclipse.jdt.core.IJavaElement.METHOD;
import static org.eclipse.jdt.core.IJavaElement.TYPE;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.IType;

import edu.buffalo.cse.green.GreenException;

//TODO _enabled is coded with >2, <2, %2 operations.
//This should be changed.  Also see: ManageFiltersDialog

public class MemberFilter {
	private Set<Integer> _type;
	private MemberVisibility _visibility;
	private String _nameMatcher;
	private int _enabled;
	
	public MemberFilter(int enabled, boolean types, boolean fields,
			boolean methods, MemberVisibility visibility, String nameMatcher) {
		_type = new HashSet<Integer>();

		if (types) _type.add(TYPE);
		if (fields) _type.add(FIELD);
		if (methods) _type.add(METHOD);
		
		_enabled = enabled;
		_visibility = visibility;
		_nameMatcher = nameMatcher;
	}
	
	public MemberFilter(String genKey) {
		this(Integer.parseInt(genKey.substring(0, 1)),
				genKey.substring(1, 2).equals("1"),
				genKey.substring(2, 3).equals("1"),
				genKey.substring(3, 4).equals("1"),
				MemberVisibility.makeVisibility(
						Integer.parseInt(genKey.substring(4, 5))),
						genKey.substring(5));
	}

	/**
	 * @param member - The member to check the filter against.
	 * @return true if the kind of element matches, the visibility matches, and
	 * the name matches; false otherwise.
	 */
	public boolean isFiltered(IMember member) {
		try {
			if (_enabled < 2) { // not on
				return false;
			}
			
			IType type;
			
			String memberName = member.getElementName();
			
			if (member.getElementType() == IJavaElement.TYPE) {
				type = (IType) member;
			} else {
				type = member.getDeclaringType();
				memberName = type.getElementName() + "." + memberName;
			}
			
			String packageName = type.getPackageFragment().getElementName();
			String qualifiedName = packageName + "."
			+ type.getCompilationUnit().getElementName() + "." + memberName;
			
            boolean condition = (_type.contains(member.getElementType())
                    && _visibility.match(member)
                    && Pattern.matches(_nameMatcher, qualifiedName));
			
			if (_enabled % 2 == 0) {
				return condition;
			} else {
				return !condition;
			}
		} catch (Exception e) {
			GreenException.warn("Problem with filter: " + this);
		}
		
		return false;
	}
	
	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return _enabled
		+ (_type.contains(TYPE) ? "1" : "0")
		+ (_type.contains(FIELD) ? "1" : "0")
		+ (_type.contains(METHOD) ? "1" : "0")
		+ _visibility.toString() + _nameMatcher;
	}
	
	/**
	 * @return true if this filter is enabled, false otherwise.
	 */
	public boolean isEnabled() {
		return _enabled >= 2;
	}

	/**
	 * @return The value representing whether or not this filter is enabled.
	 */
	public int getEnabledValue() {
		return _enabled;
	}
	
	/**
	 * @return A <code>String</code> representation of the information stored in
	 * this filter.
	 */
	public String getDescription() {
		StringBuffer buf = new StringBuffer();
		String[] members = new String[] {"type, ", "field, ", "method, "};

		buf.append(_enabled % 2 == 0 ? "" : "not: ");
		buf.append(_type.contains(TYPE) ? members[0] : ""); 
		buf.append(_type.contains(FIELD) ? members[1] : ""); 
		buf.append(_type.contains(METHOD) ? members[2] : ""); 
		buf.append("with " + _visibility.getCodeText() + " visibility ");
		buf.append("that matches pattern: " + _nameMatcher);
		return buf.toString();
	}
	
	/**
	 * Enables or disables the filter.
	 * 
	 * @param enabled - The enabled value. A value of 2 or greater indicates
	 * that the match condition is enabled. A (value % 2) of 1 indiates that the
	 * match condition is negated.
	 */
	public void setEnabled(int enabled) {
		_enabled = enabled;
	}
}
