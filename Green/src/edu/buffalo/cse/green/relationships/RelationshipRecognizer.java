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
import static org.eclipse.jdt.core.IJavaElement.TYPE;
import static org.eclipse.jdt.core.IJavaElement.TYPE_PARAMETER;
import static org.eclipse.jdt.core.dom.ASTNode.BLOCK;
import static org.eclipse.jdt.core.dom.ASTNode.EXPRESSION_STATEMENT;
import static org.eclipse.jdt.core.dom.ASTNode.METHOD_INVOCATION;

import java.util.AbstractList;
import java.util.Collection;
import java.util.List;

import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.ExpressionStatement;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.Name;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.Statement;
import org.eclipse.jdt.internal.core.TypeParameter;

import edu.buffalo.cse.green.GreenException;
import edu.buffalo.cse.green.editor.model.RelationshipKind;

/**
 * Provides support for the recognition of relationships and the caching of
 * added and removed relationships
 * 
 * @author bcmartin
 */
public abstract class RelationshipRecognizer extends RelationshipVisitor {
	/**
	 * @see edu.buffalo.cse.relationship.RelationshipCache
	 */
	private RelationshipCache _cache;

	/**
	 * Runs the relationship recognizer on the given compilation unit; When the
	 * method completes, the relationship information in the cache will contain
	 * everything necessary to update the diagram.
	 *
	 * @param cu - The <code>CompilationUnit</code> to run the recognizer on. 
	 * @param cache - The data structure containing information about the
	 * relationships contained in the editor.
	 */
	public final void run(CompilationUnit cu, RelationshipCache cache) {
		_cache = cache;
		
		// run the recognizer
		try {
			cu.accept(this);
		} catch (RuntimeException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Handles the recognition of a relationship.
	 *
	 * @param sourceType - The <code>IType</code> containing the source code
	 * that triggered the relationship.
	 * @param targetType - The <code>IType</code> referenced by the source code
	 * in the source type.
	 * @param partClass - The <code>Class</code> representing the controller
	 * part of this relationship.
	 * @param features - The unique <code>ASTNode</code>s that identify this
	 * relationship .
	 * 
	 * @author Gene Wang
	 */
	protected final void fireFoundRelationship(
			IType sourceType,
			ITypeBinding target,
			Class partClass,
			AbstractList<ASTNode> features) {
		
		IType targetType = null;

		if(target.getJavaElement() instanceof TypeParameter) {
			//Type is parameterized type (such as <E>), true target type
			//is the declaring element
			//LOOKINTO TypeParameter is a JDT internal class, there may
			//be a way of avoiding using this.
			targetType = (IType) target.getDeclaringClass().getJavaElement();
		}
		else {
			targetType = (IType) target.getJavaElement();
		}
		
		// ensure the relationship contains the necessary information
		if ((sourceType == null) || (target == null)) {
			GreenException.illegalOperation(
					"Cannot add a relationship that has a missing source/"
					+ "target type");
		}
		
		if (target.isParameterizedType()) {
			for (ITypeBinding interfaceBinding : target.getInterfaces()) {
				IType interfType = (IType) interfaceBinding.getJavaElement();
				if (interfType.getFullyQualifiedName().equals(
						Collection.class.getName())) {
					IJavaElement element = (target.getTypeArguments()[0].getJavaElement());
					
					if (element.getElementType() == TYPE) {
						targetType = (IType) element;
					} else if (element.getElementType() == TYPE_PARAMETER) {
						targetType = (IType) target.getJavaElement();
					}
				}
			}
		}
		
		_cache.add(sourceType, targetType, partClass,
				new Relationship(features));
	}

	/**
	 * Processes calls to the add() method if they are called on a parameterized
	 * variable.
	 * 
	 * @param features - The features of the relationship.
	 * @param variable - The name of the variable.
	 * @param node - The block node to search.
	 */
	protected void processAddInvocations(List<ASTNode> features,
			Name variable, ASTNode node) {
		if (!(variable instanceof SimpleName)) {
			return;
		}
		
		Block block = null;
		
		while (node != null) {
			if (node.getNodeType() == BLOCK) {
				block = (Block) node;
				break;
			}
			
			node = node.getParent();
		}
		
		for (Statement stmt
				: (AbstractList<Statement>) (List) block.statements()) {
			if (stmt.getNodeType() == EXPRESSION_STATEMENT) {
				ExpressionStatement eStmt = (ExpressionStatement) stmt;
				Expression e = eStmt.getExpression();
				
				if (e.getNodeType() == METHOD_INVOCATION) {
					MethodInvocation m = (MethodInvocation) e;
					
					if (m.getExpression() instanceof SimpleName) {
						SimpleName name = (SimpleName) m.getExpression();
						SimpleName var = (SimpleName) variable;
						if (name.getIdentifier().equals(var.getIdentifier())) {
							if (m.getName().getIdentifier().equals("add")) {
								features.add(stmt);
							}
						}
					}
				}
			}
		}
	}

	/**
	 * @return The kind of relationship.
	 */
	public abstract RelationshipKind getFlags();
}