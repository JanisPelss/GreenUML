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

import static org.eclipse.jdt.core.dom.ASTNode.CLASS_INSTANCE_CREATION;
import static org.eclipse.jdt.core.dom.ASTNode.EXPRESSION_STATEMENT;
import static org.eclipse.jdt.core.dom.ASTNode.METHOD_DECLARATION;
import static org.eclipse.jdt.core.dom.ASTNode.PARAMETERIZED_TYPE;
import static org.eclipse.jdt.core.dom.ASTNode.SIMPLE_TYPE;
import static org.eclipse.jdt.core.dom.ASTNode.VARIABLE_DECLARATION_STATEMENT;

import java.util.AbstractList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jdt.core.dom.ASTMatcher;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.ExpressionStatement;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.ParameterizedType;
import org.eclipse.jdt.core.dom.SimpleType;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.Statement;
import org.eclipse.jdt.core.dom.VariableDeclarationStatement;

import edu.buffalo.cse.green.GreenException;

/**
 * Represents a single instance of a relationship; contains the relationship's
 * features.
 * 
 * @author bcmartin
 */
public class Relationship {
	private AbstractList<ASTNode> _features;
	
	private boolean _retain;

	public Relationship(AbstractList<ASTNode> features) {
		_features = features;
	}
	
	/**
	 * @return True if the relationship is generic, false otherwise.
	 * 
	 * @author Gene Wang
	 */
	public boolean isGeneric() {
		for (ASTNode feature : _features) {
			ITypeBinding[] interfaces = null;
			if( feature == null ) {
			} else
			if (feature.getNodeType() == EXPRESSION_STATEMENT) {
				ExpressionStatement node = (ExpressionStatement) feature;
				interfaces = node.getExpression().resolveTypeBinding().getInterfaces();
			} else if (feature.getNodeType() == VARIABLE_DECLARATION_STATEMENT) {
				VariableDeclarationStatement node = (VariableDeclarationStatement) feature;
				interfaces = node.getType().resolveBinding().getInterfaces();
			}

			if(interfaces != null) {
				for(int i = 0; i < interfaces.length; i++) {
					if (interfaces[i].getName().contains("Collection")) {
						return true;
					}
				}
			}
		}
		return false;
	}
	
	/**
	 * @return The features of the relationship.
	 */
	public AbstractList<ASTNode> getFeatures() {
		return _features;
	}
	
	/**
	 * Causes the relationship to be kept or removed from the editor.
	 * 
	 * @param value - If true, the relationship will be kept; otherwise, the
	 * relationship will be removed. 
	 */
	public void setRetained(boolean value) {
		_retain = value;
	}
	
	/**
	 * @return The value indicating whether the relationship should be removed
	 * or not.
	 */
	public boolean isRetained() {
		return _retain;
	}
	
	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object o) {
		ASTMatcher matcher = new ASTMatcher();
		
		if (o instanceof Relationship) {
			Relationship relationship = (Relationship) o;
			Iterator<ASTNode> iter = relationship.getFeatures().iterator();
			
			for (ASTNode node1 : getFeatures()) {
				if (!iter.hasNext()) {
					return false;
				}
				
				ASTNode node2 = iter.next();

				if( node1 == null ) {
				} else
				if (node1.getNodeType() == EXPRESSION_STATEMENT) {
					ExpressionStatement stmt = (ExpressionStatement) node1;
					if (stmt.getExpression().getNodeType()
							== CLASS_INSTANCE_CREATION) {
						return false;
					}
					
					if (!matcher.match((ExpressionStatement) node1, node2)) {
						return false;
					}
				} else if (node1.getNodeType() == METHOD_DECLARATION) {
					if (!matcher.match((MethodDeclaration) node1, node2)) {
						return false;
					}
				} else if (node1.getNodeType() == SIMPLE_TYPE) {
					if (!matcher.match((SimpleType) node1, node2)) {
						return false;
					}
				} else if (node1.getNodeType()
						== VARIABLE_DECLARATION_STATEMENT) {
					if (!matcher.match((VariableDeclarationStatement) node1,
							node2)) {
						return false;
					}
					
					MethodDeclaration md1, md2;
					
					while (!(node1.getNodeType() == METHOD_DECLARATION)) {
						node1 = node1.getParent();
					}

					while (!(node2.getNodeType() == METHOD_DECLARATION)) {
						node2 = node2.getParent();
					}
					
					md1 = (MethodDeclaration) node1;
					md2 = (MethodDeclaration) node2;
					Iterator<SingleVariableDeclaration> pi1 =
						((AbstractList<SingleVariableDeclaration>) (List) md1.parameters()).iterator();
					Iterator pi2 = md2.parameters().iterator();
					
					while (pi1.hasNext()) {
						if (!pi2.hasNext()) return false;
						SingleVariableDeclaration d1 = pi1.next();
						Object d2 = pi2.next();
						
						if (!matcher.match(d1, d2)) return false;
					}

					if (pi2.hasNext()) return false;
				} else if (node1.getNodeType() == PARAMETERIZED_TYPE) {
					if (!matcher.match((ParameterizedType) node1,
							node2)) {
						return false;
					}
				} else if (node1.getNodeType() == CLASS_INSTANCE_CREATION) {
					return false;
				} else if (node1 instanceof Statement) {
					return false;
				} else if (node1 instanceof Expression) {
					return false;
				} else {
					GreenException.illegalOperation(
							node1.getClass() + "=" + node2.getClass());
				}
			}
			
			return !iter.hasNext();
		} else {
			return false;
		}
	}
}
