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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.VariableDeclarationStatement;

import edu.buffalo.cse.green.relationships.RelationshipRemover;
import edu.buffalo.cse.green.relationships.DeclarationInfoProvider;

/**
 * @see edu.buffalo.cse.green.relationship.RelationshipRemover
 * 
 * @author bcmartin
 */
public class DependencyLRemover extends RelationshipRemover {
	private List<VariableDeclarationStatement> lVDS;
	
	/**
	 * @see edu.buffalo.cse.green.relationships.RelationshipRemover#init()
	 */
	protected void init() {
		lVDS = new ArrayList<VariableDeclarationStatement>();
	}

	/**
	 * @see edu.buffalo.cse.green.relationships.RelationshipRemover#finish()
	 */
	protected void finish() {
		for (VariableDeclarationStatement vds : lVDS) {
			Block block = (Block) vds.getParent();
			vds.delete();
			processAddInvocations(block);
		}
	}

	/**
	 * @see org.eclipse.jdt.core.dom.ASTVisitor#visit(org.eclipse.jdt.core.dom.VariableDeclarationStatement)
	 */
	public boolean visit(VariableDeclarationStatement node) {
		if (getMatcher().match(node, getRelationship().getFeatures().get(0))) {
			lVDS.add(node);
		}

		return true;
	}

	/**
	 * @see edu.buffalo.cse.green.relationships.RelationshipVisitor#process(org.eclipse.jdt.core.dom.TypeDeclaration)
	 */
	protected boolean process(DeclarationInfoProvider node) {
		return true;
	}
}
