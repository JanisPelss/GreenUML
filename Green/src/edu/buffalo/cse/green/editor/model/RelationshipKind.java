/* This file is part of Green.
 *
 * Copyright (C) 2005 The Research Foundation of State University of New York
 * All Rights Under Copyright Reserved, The Research Foundation of S.U.N.Y.
 * 
 * Green is free software, licensed under the terms of the Eclipse
 * Public License, version 1.0.  The license is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package edu.buffalo.cse.green.editor.model;

/**
 * This class is used by a relationship to determine what sort of behavior its
 * generator will exhibit, as well as how its cardinality is calculated.
 * <code>Single</code> relationships can only have a cardinality of one.
 * The cardinality of <code>Cardinal</code> relationships is a range across
 * their various cardinality in each constructor. The cardinality of
 * <code>Cumulative</code> relationships is the total of the occurances of that
 * kind of relationship.
 * 
 * <code>Single</code> is appropriate for Generalization and Realization
 * <code>Cardinal</code> is appropriate for Association and Composition
 * <code>Cumulative</code> is appropriate for Dependency
 * 
 * @author bcmartin
 */
public enum RelationshipKind {
	Single(0), Cardinal(1), Cumulative(2);

	private int _kind;

	RelationshipKind(int kind) {
		_kind = kind;
	}
	
	/**
	 * @return A value representing the kind of relationship this is.
	 */
	public int getKind() {
		return _kind;
	}
}
