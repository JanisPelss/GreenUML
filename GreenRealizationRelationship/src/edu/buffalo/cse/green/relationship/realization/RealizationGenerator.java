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

import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.Type;

import edu.buffalo.cse.green.relationships.RelationshipGenerator;
import edu.buffalo.cse.green.relationships.DeclarationInfoProvider;

/**
 * @see edu.buffalo.cse.green.relationship.RelationshipGenerator
 * 
 * @author bcmartin
 */
public class RealizationGenerator extends RelationshipGenerator {
	/**
	 * @see edu.buffalo.cse.green.relationships.RelationshipVisitor#process(org.eclipse.jdt.core.dom.TypeDeclaration)
	 */
	public boolean process(DeclarationInfoProvider node) {
		if (!correctTypeToGenerate()) return true;
		List<Type> superInterfaces = (AbstractList<Type>) node.getSuperInterfaceTypes();
		
		// If it's already in there, don't add it again
		for (Type type : superInterfaces) {
			if (getType(type).equals(getTargetType())) { return false; }
		}
		
		superInterfaces.add(createTypeReference(getTargetType()));
		return false;
	}

	/**
	 * @see edu.buffalo.cse.green.relationships.RelationshipGenerator#process(org.eclipse.jdt.core.dom.Block)
	 */
	protected boolean process(Block node) {
		return false;
	}

	/**
	 * @see edu.buffalo.cse.green.relationships.RelationshipGenerator#doVisitBlocks()
	 */
	protected boolean doVisitBlocks() {
		return false;
	}

	/**
	 * @see edu.buffalo.cse.green.relationships.RelationshipGenerator#needChooseTypeDialog()
	 */
	protected boolean needChooseTypeDialog() {
		return false;
	}

	/* (non-Javadoc)
	 * @see edu.buffalo.cse.green.relationships.RelationshipGenerator#needConstructor()
	 */
	@Override
	protected boolean needConstructor() {
		return false;
	}
}