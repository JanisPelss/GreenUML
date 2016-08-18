/* This file is part of Green.
 *
 * Copyright (C) 2005 The Research Foundation of State University of New York
 * All Rights Under Copyright Reserved, The Research Foundation of S.U.N.Y.
 * 
 * Green is free software, licensed under the terms of the Eclipse
 * Public License, version 1.0.  The license is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package edu.buffalo.cse.green.relationships;


/**
 * Permits mapping from a relationship group to a subtype so that a relationship
 * may have more than one flavor.
 * 
 * @author bcmartin
 */
public class RelationshipSubtype {
	private RelationshipGroup _group;
	private String _label;
	
	public RelationshipSubtype(RelationshipGroup group,
			String label) {
		_group = group;
		_label = label;
	}
	
	/**
	 * @return The <code>RelationshipGroup</code> represented by this subtype.
	 */
	public RelationshipGroup getGroup() {
		return _group;
	}
	
	/**
	 * @return The label for this subtype.
	 */
	public String getLabel() {
		return _label;
	}
}
