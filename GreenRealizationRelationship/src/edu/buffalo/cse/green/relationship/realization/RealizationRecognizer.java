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
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.Type;

import edu.buffalo.cse.green.editor.model.RelationshipKind;
import edu.buffalo.cse.green.relationships.RelationshipRecognizer;
import edu.buffalo.cse.green.relationships.DeclarationInfoProvider;

/**
 * @see edu.buffalo.cse.green.relationship.RelationshipRecognizer
 * 
 * @author bcmartin
 */
public class RealizationRecognizer extends RelationshipRecognizer {
	/**
	 * @see edu.buffalo.cse.green.relationships.RelationshipVisitor#process(org.eclipse.jdt.core.dom.TypeDeclaration)
	 */
	public boolean process(DeclarationInfoProvider node) {
		if (!node.isInterface()) { // it's a class
			if (getCurrentType() != null) {
				List<Type> interfaces = (AbstractList<Type>) node
						.getSuperInterfaceTypes();

				for (Type type : interfaces) {
					if (type.resolveBinding() == null) return true;
					IType targetType = getType(type);

					if (targetType != null) {
						AbstractList<ASTNode> features =
							new ArrayList<ASTNode>();
						features.add(type);

						fireFoundRelationship(getCurrentType(),
								type.resolveBinding(), RealizationPart.class,
								features);
					}
				}
			}
		}

		return true;
	}

	/**
	 * @see edu.buffalo.cse.green.relationships.RelationshipRecognizer#getFlags()
	 */
	public RelationshipKind getFlags() {
		return RelationshipKind.Single;
	}
}