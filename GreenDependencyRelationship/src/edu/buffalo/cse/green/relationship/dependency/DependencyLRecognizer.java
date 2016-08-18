/* This file is part of Green.
 *
 * Copyright (C) 2005 The Research Foundation of State University of New York
 * All Rights Under Copyright Reserved, The Research Foundation of S.U.N.Y.
 * 
 * Green is free software, licensed under the terms of the Eclipse
 * Public License, version 1.0.  The license is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package edu.buffalo.cse.green.relationship.dependency;

import static org.eclipse.jdt.core.dom.ASTNode.CLASS_INSTANCE_CREATION;
import static org.eclipse.jdt.core.dom.ASTNode.STRING_LITERAL;

import java.util.AbstractList;
import java.util.ArrayList;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.Assignment;
import org.eclipse.jdt.core.dom.FieldAccess;
import org.eclipse.jdt.core.dom.Name;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;

import edu.buffalo.cse.green.editor.model.RelationshipKind;
import edu.buffalo.cse.green.relationships.RelationshipRecognizer;
import edu.buffalo.cse.green.relationships.DeclarationInfoProvider;

/**
 * @see edu.buffalo.cse.green.relationship.RelationshipRecognizer
 * 
 * @author bcmartin
 */
public class DependencyLRecognizer extends RelationshipRecognizer {
	/**
	 * @see edu.buffalo.cse.green.relationships.RelationshipVisitor#process(org.eclipse.jdt.core.dom.TypeDeclaration)
	 */
	protected boolean process(DeclarationInfoProvider node) {
		return true;
	}

	/**
	 * @see org.eclipse.jdt.core.dom.ASTVisitor#visit(org.eclipse.jdt.core.dom.VariableDeclarationFragment)
	 */
	public boolean visit(VariableDeclarationFragment node) {
		checkForDependency(node.getParent(), node.getName(), node
				.getInitializer());
		return true;
	}

	/**
	 * @see org.eclipse.jdt.core.dom.ASTVisitor#visit(org.eclipse.jdt.core.dom.Assignment)
	 */
	public boolean visit(Assignment node) {
		checkForDependency(node.getParent(), node.getLeftHandSide(), node
				.getRightHandSide());
		return true;
	}

	/**
	 * Checks if the current node is a dependency.
	 * 
	 * @param parent - The parent of the current node.
	 * @param lhs - The left side of the assignment.
	 * @param rhs - The right side of the assignment.
	 */
	private void checkForDependency(ASTNode parent, ASTNode lhs, ASTNode rhs) {
		
		if( lhs instanceof FieldAccess ) lhs = ( (FieldAccess) lhs ).getName();
		
		if (lhs instanceof Name) {
			Name LHS = (Name) lhs;

			if (isLocalVariable(LHS)) {
				if (rhs == null) return;
				
				if (rhs.getNodeType() == CLASS_INSTANCE_CREATION) {
					AbstractList<ASTNode> features = new ArrayList<ASTNode>();
					features.add(parent);
					features.add(getMethodDeclaration());
					
					processAddInvocations(features, LHS, parent);
					fireFoundRelationship(getCurrentType(),
							LHS.resolveTypeBinding(), DependencyLPart.class,
							features);
				} else if (rhs.getNodeType() == STRING_LITERAL) {
					AbstractList<ASTNode> features = new ArrayList<ASTNode>();
					features.add(parent);
					features.add(getMethodDeclaration());
					
					fireFoundRelationship(getCurrentType(),
							LHS.resolveTypeBinding(), DependencyLPart.class,
							features);
				}
			}
		}
	}

	/**
	 * @see edu.buffalo.cse.green.relationships.RelationshipRecognizer#getFlags()
	 */
	public RelationshipKind getFlags() {
		return RelationshipKind.Cumulative;
	}
}