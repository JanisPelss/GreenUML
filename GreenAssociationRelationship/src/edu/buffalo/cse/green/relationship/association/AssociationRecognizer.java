/* This file is part of Green.
 *
 * Copyright (C) 2005 The Research Foundation of State University of New York
 * All Rights Under Copyright Reserved, The Research Foundation of S.U.N.Y.
 * 
 * Green is free software, licensed under the terms of the Eclipse
 * Public License, version 1.0.  The license is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package edu.buffalo.cse.green.relationship.association;

import java.util.AbstractList;
import java.util.ArrayList;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.Assignment;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.FieldAccess;
import org.eclipse.jdt.core.dom.Name;

import edu.buffalo.cse.green.editor.model.RelationshipKind;
import edu.buffalo.cse.green.relationships.RelationshipRecognizer;
import edu.buffalo.cse.green.relationships.DeclarationInfoProvider;

/**
 * @see edu.buffalo.cse.green.relationship.RelationshipRecognizer
 * 
 * @author bcmartin
 */
public class AssociationRecognizer extends RelationshipRecognizer {
	/**
	 * @see org.eclipse.jdt.core.dom.ASTVisitor#visit(org.eclipse.jdt.core.dom.Assignment)
	 */
	public boolean visit(Assignment node) {
		if (!inConstructor()) return true;

		Expression eLHS = node.getLeftHandSide();
		Expression eRHS = node.getRightHandSide();
		
		if( eLHS instanceof FieldAccess ) eLHS = ( (FieldAccess) eLHS ).getName();
		if( eRHS instanceof FieldAccess ) eRHS = ( (FieldAccess) eRHS ).getName();

		if (eLHS instanceof Name && eRHS instanceof Name) {
			Name LHS = (Name) eLHS;
			Name RHS = (Name) eRHS;

			if (!isField(LHS)) return true;
			if (!isParameter(RHS)) return true;

			AbstractList<ASTNode> features = new ArrayList<ASTNode>();
			features.add(node.getParent());
			features.add(getMethodDeclaration());
			
			processAddInvocations(features, LHS, node);
			fireFoundRelationship(getCurrentType(), LHS.resolveTypeBinding(),
					AssociationPart.class, features);
		}

		return true;
	}

	/**
	 * @see edu.buffalo.cse.green.relationships.RelationshipVisitor#process(org.eclipse.jdt.core.dom.TypeDeclaration)
	 */
	protected boolean process(DeclarationInfoProvider node) {
		return true;
	}

	/**
	 * @see edu.buffalo.cse.green.relationships.RelationshipRecognizer#getFlags()
	 */
	public RelationshipKind getFlags() {
		return RelationshipKind.Cardinal;
	}
}