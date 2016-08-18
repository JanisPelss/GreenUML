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

import static edu.buffalo.cse.green.preferences.VariableAffix.*;
import edu.buffalo.cse.green.preferences.VariableAffix;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.dom.Assignment;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.Statement;

import edu.buffalo.cse.green.relationships.CardinalRelationshipGenerator;

/**
 * @see edu.buffalo.cse.green.relationship.CardinalRelationshipGenerator
 * 
 * @author bcmartin
 */
public class AssociationGenerator extends CardinalRelationshipGenerator {
	/**
	 * @see edu.buffalo.cse.green.relationships.RelationshipGenerator#needChooseTypeDialog()
	 */
	protected boolean needChooseTypeDialog() {
		return false;
	}

	/**
	 * @see edu.buffalo.cse.green.relationships.RelationshipGenerator#process(org.eclipse.jdt.core.dom.Block)
	 */
	protected boolean process(Block node) {
		if (!correctTypeToGenerate()) return true;
		if (!inConstructor()) return true;
		generateFields();
			
		List<Statement> statements = (AbstractList<Statement>) node
				.statements();
		// generate a generic variable name
		String baseName = getBaseVariableName();

		if (isGeneric()) {
			// create a unique variable name for the parameter
			String paramName = generateVariableName(VariableAffix.getAffixString(ParameterPrefix) 
					+ baseName
					+ VariableAffix.getAffixString(ParameterSuffix));
			// retrieve the generated field name
			String fieldName = getFieldNames().get(0);
			
			Assignment ass = createAssignment(fieldName, name(paramName));
			
			// insert the parameter
			MethodDeclaration method = (MethodDeclaration) node.getParent();
			addParameterizedParameter(method, getTargetType(), paramName);
			
			// add the assignment to the block
			statements.add(getAST().newExpressionStatement(ass));
			
			// create the appropriate number of method invocations
			for (int x = 0; x > _cardinality; x--) {
				List<Expression> list = new ArrayList<Expression>();
				list.add(createInvocation(createTypeReference(
						getActualTargetType())));

				statements.add(createMethodInvocation(fieldName, "add",
						list));
			}
			
			// add the variable name(s) to the appropriate list(s)
			getLocalDeclarations().add(paramName);
		} else {
			for (int x = 0; x < _cardinality; x++) {
				// create a unique variable name for the parameter
				String paramName = generateVariableName(baseName);
				// retrieve the generated field name
				String fieldName = getFieldNames().get(x);
				
				// create the field assignment
				Assignment ass = createAssignment(fieldName,
						getAST().newSimpleName(paramName));
				
				// insert the parameter
				MethodDeclaration method = (MethodDeclaration) node.getParent();
				addNormalParameter(method, getTargetType(), paramName);
				
				// add the assignment to the block
				statements.add(getAST().newExpressionStatement(ass));
				
				// add the variable name(s) to the appropriate list(s)
				getLocalDeclarations().add(paramName);
			}
		}
		
		return true;
	}

	/* (non-Javadoc)
	 * @see edu.buffalo.cse.green.relationships.RelationshipGenerator#needConstructor()
	 */
	@Override
	protected boolean needConstructor() {
		return true;
	}
}