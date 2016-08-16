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

import static org.eclipse.jdt.core.dom.ASTNode.ASSIGNMENT;
import static org.eclipse.jdt.core.dom.ASTNode.VARIABLE_DECLARATION_FRAGMENT;

import java.util.AbstractList;
import java.util.ArrayList;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ClassInstanceCreation;
import org.eclipse.jdt.core.dom.Initializer;
import org.eclipse.jdt.core.dom.ITypeBinding;

import edu.buffalo.cse.green.editor.model.RelationshipKind;
import edu.buffalo.cse.green.relationships.RelationshipRecognizer;
import edu.buffalo.cse.green.relationships.DeclarationInfoProvider;

/**
 * @see edu.buffalo.cse.green.relationship.RelationshipRecognizer
 * 
 * @author bcmartin
 */
public class DependencyIRecognizer extends RelationshipRecognizer {
	/**
	 * @see edu.buffalo.cse.green.relationships.RelationshipVisitor#process(org.eclipse.jdt.core.dom.TypeDeclaration)
	 */
	protected boolean process(DeclarationInfoProvider node) {
		return true;
	}

	/**
	 * Stop the visitor from going inside any initializer block
	 */
	public boolean visit(Initializer node) {
		return false;
	}
	
	/**
	 * @see org.eclipse.jdt.core.dom.ASTVisitor#visit(org.eclipse.jdt.core.dom.ClassInstanceCreation)
	 */
	public boolean visit(ClassInstanceCreation node) {
		AbstractList<ASTNode> features = new ArrayList<ASTNode>();
		
		if (!(node.getParent().getNodeType() == ASSIGNMENT) &&
				!(node.getParent().getNodeType() ==
					VARIABLE_DECLARATION_FRAGMENT)) {
			features.add(node);
			
			ITypeBinding typeBinding = node.resolveTypeBinding();
			if (typeBinding == null) return true;
			
			fireFoundRelationship(getCurrentType(), typeBinding,
					DependencyIPart.class, features);
		}
		
		return true;
	}

	/**
	 * @see edu.buffalo.cse.green.relationships.RelationshipRecognizer#getFlags()
	 */
	public RelationshipKind getFlags() {
		return RelationshipKind.Cumulative;
	}
}