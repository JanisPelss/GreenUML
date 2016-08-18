/* This file is part of Green.
 *
 * Copyright (C) 2005 The Research Foundation of State University of New York
 * All Rights Under Copyright Reserved, The Research Foundation of S.U.N.Y.
 * 
 * Green is free software, licensed under the terms of the Eclipse
 * Public License, version 1.0.  The license is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package edu.buffalo.cse.green.relationship.realization;

import java.util.AbstractList;
import java.util.List;

import org.eclipse.jdt.core.dom.Type;

import edu.buffalo.cse.green.relationships.RelationshipRemover;
import edu.buffalo.cse.green.relationships.DeclarationInfoProvider;

/**
 * @see edu.buffalo.cse.green.relationship.RelationshipRemover
 * 
 * @author bcmartin
 */
public class RealizationRemover extends RelationshipRemover {
	/**
	 * @see edu.buffalo.cse.green.relationships.RelationshipVisitor#process(org.eclipse.jdt.core.dom.TypeDeclaration)
	 */
	public boolean process(DeclarationInfoProvider node) {
		if (node.isInterface()) { return true; }

		List superInterfaces = node.getSuperInterfaceTypes();

		for (Type type : (AbstractList<Type>) (List) superInterfaces) {
			if (getType(type).equals(getTargetType())) {
				type.delete();
				break;
			}
		}

		return true;
	}

	/**
	 * @see edu.buffalo.cse.green.relationships.RelationshipRemover#finish()
	 */
	protected void finish() {
		// removal complete
	}

	/**
	 * @see edu.buffalo.cse.green.relationships.RelationshipRemover#init()
	 */
	protected void init() {
		// no action necessary
	}
}