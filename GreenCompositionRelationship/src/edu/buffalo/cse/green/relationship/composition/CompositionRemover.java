/* This file is part of Green.
 *
 * Copyright (C) 2005 The Research Foundation of State University of New York
 * All Rights Under Copyright Reserved, The Research Foundation of S.U.N.Y.
 * 
 * Green is free software, licensed under the terms of the Eclipse
 * Public License, version 1.0.  The license is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package edu.buffalo.cse.green.relationship.composition;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.dom.Assignment;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.ExpressionStatement;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.Name;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;

import edu.buffalo.cse.green.relationships.RelationshipRemover;
import edu.buffalo.cse.green.relationships.DeclarationInfoProvider;

/**
 * @see edu.buffalo.cse.green.relationship.RelationshipRemover
 * 
 * @author bcmartin
 */
public class CompositionRemover extends RelationshipRemover {
	private List<ExpressionStatement> lEXP;

	private List<IField> lFIE;

	private DeclarationInfoProvider _typeInfo;

	/**
	 * @see edu.buffalo.cse.green.relationships.RelationshipRemover#init()
	 */
	protected void init() {
		lEXP = new ArrayList<ExpressionStatement>();
		lFIE = new ArrayList<IField>();
	}

	/**
	 * @see edu.buffalo.cse.green.relationships.RelationshipRemover#finish()
	 */
	protected void finish() {
		for (ExpressionStatement exp : lEXP) {
			Block block = (Block) exp.getParent();
			exp.delete();

			processAddInvocations(block);
		}

		for (IField field : lFIE) {
			for (FieldDeclaration fieldDec : _typeInfo.getFields()) {
				List<VariableDeclarationFragment> fragments =
					(AbstractList<VariableDeclarationFragment>)
					fieldDec.fragments();

				for (VariableDeclarationFragment fragment : fragments) {
					IField dField = (IField) fragment.getName()
							.resolveBinding().getJavaElement();
					if (field.equals(dField)) {
						fieldDec.delete();

						break;
					}
				}
			}
		}
	}

	/**
	 * @see edu.buffalo.cse.green.relationships.RelationshipVisitor#process(org.eclipse.jdt.core.dom.TypeDeclaration)
	 */
	protected boolean process(DeclarationInfoProvider node) {
		_typeInfo = node;
		
		return true;
	}

	/**
	 * @see org.eclipse.jdt.core.dom.ASTVisitor#visit(org.eclipse.jdt.core.dom.ExpressionStatement)
	 */
	public boolean visit(ExpressionStatement node) {
		if (getMatcher().match(node, getRelationship().getFeatures().get(0))) {
			lEXP.add(node);
			Assignment ass = (Assignment) node.getExpression();
			Name name = (Name) ass.getLeftHandSide();
			lFIE.add((IField) name.resolveBinding().getJavaElement());
		}

		return true;
	}
}
