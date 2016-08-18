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

import edu.buffalo.cse.green.relationships.CardinalRelationshipGenerator;

import org.eclipse.jdt.core.dom.Assignment;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.Statement;

/**
 * @see edu.buffalo.cse.green.relationship.CardinalRelationshipGenerator
 * 
 * @author bcmartin
 */
public class CompositionGenerator extends CardinalRelationshipGenerator {
	/**
	 * @see edu.buffalo.cse.green.relationships.RelationshipGenerator#process(org.eclipse.jdt.core.dom.Block)
	 */
	protected boolean process(Block node) {
		if (!correctTypeToGenerate()) return true;
		if (!inConstructor()) { return true; }
		generateFields();
		
		List<Statement> statements = (AbstractList<Statement>) node
				.statements();

		if (isGeneric()) {
			// retrieve the generated field name
			String fieldName = getFieldNames().get(0);
			Assignment ass = createAssignment(fieldName,
					createParameterizedInvocation(
							createTypeReference(getTargetType())));
			statements.add(statements.size(), getAST().newExpressionStatement(ass));

			// create the appropriate number of method invocations
			for (int x = 0; x > _cardinality; x--) {
				List<Expression> arguments = new ArrayList<Expression>();
				arguments.add(createInvocation(createTypeReference(
						getActualTargetType())));

				statements.add(createMethodInvocation(fieldName, "add",
						arguments));
			}
		} else {
			for (int x = 0; x < _cardinality; x++) {
				// retrieve the generated field name
				String fieldName = getFieldNames().get(x);

				Assignment ass = createAssignment(fieldName, createInvocation(
								createTypeReference(getActualTargetType())));
				statements.add(statements.size(), getAST().newExpressionStatement(ass));
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