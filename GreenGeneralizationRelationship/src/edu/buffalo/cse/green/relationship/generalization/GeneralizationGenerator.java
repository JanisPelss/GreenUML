/* This file is part of Green.
 *
 * Copyright (C) 2005 The Research Foundation of State University of New York
 * All Rights Under Copyright Reserved, The Research Foundation of S.U.N.Y.
 * 
 * Green is free software, licensed under the terms of the Eclipse
 * Public License, version 1.0.  The license is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

//Created 9/1/2004
package edu.buffalo.cse.green.relationship.generalization;

import java.util.AbstractList;
import java.util.List;

import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.Type;

import edu.buffalo.cse.green.GreenException;
import edu.buffalo.cse.green.relationships.RelationshipGenerator;
import edu.buffalo.cse.green.relationships.DeclarationInfoProvider;

/**
 * @see edu.buffalo.cse.green.relationship.RelationshipGenerator
 * 
 * @author bcmartin
 */
public class GeneralizationGenerator extends RelationshipGenerator {
	private static final String CLASS_MULTIPLE_GENERALIZATION =
		"A class may not generalize multiple classes";

	/**
	 * @see edu.buffalo.cse.green.relationships.RelationshipVisitor#process(org.eclipse.jdt.core.dom.TypeDeclaration)
	 */
	public boolean process(DeclarationInfoProvider node) {
		if (!correctTypeToGenerate()) return true;

		try {
			if (getSourceType().isInterface()) {
				// handle interface
				Type type = createTypeReference(getTargetType());
				
				// retrieve the live list of types
				List<Type> interfaceList =
					(AbstractList<Type>) node.getSuperInterfaceTypes();
				
				// ensure that the type is not already generalized
				for (Type eType : interfaceList) {
					if (getType(eType).equals(getTargetType())) {
						GreenException.illegalOperation(
								"Duplicate generalization of target interface");
					}
				}
				
				interfaceList.add(type);
			} else {
				// handle class
				if (node.getSuperclassType() != null) {
					GreenException.illegalOperation(
							CLASS_MULTIPLE_GENERALIZATION);
				}
				
				node.setSuperclassType(createTypeReference(getTargetType()));
			}
			
			return false;
		} catch (JavaModelException e) {
			e.printStackTrace();
			return false;
		}
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