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

import static edu.buffalo.cse.green.preferences.VariableAffix.*;
import edu.buffalo.cse.green.preferences.VariableAffix;

import java.util.AbstractList;
import java.util.List;

import org.eclipse.jdt.core.Flags;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.BodyDeclaration;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.Modifier;

/**
 * Provides support for the common functionality of relationship generators that
 * represent relationships with the potential for various cardinalities.
 * 
 * @author bcmartin
 * @author Gene Wang
 */
public abstract class CardinalRelationshipGenerator extends RelationshipGenerator {
	/**
	 * @see edu.buffalo.cse.green.relationships.RelationshipGenerator#doVisitBlocks()
	 */
	protected final boolean doVisitBlocks() {
		return true;
	}

	/**
	 * @see edu.buffalo.cse.green.relationships.RelationshipVisitor#process(org.eclipse.jdt.core.dom.TypeDeclaration)
	 */
	protected final boolean process(DeclarationInfoProvider node) {
		if (!correctTypeToGenerate()) return true;
				
		// make sure there's a constructor
		for(MethodDeclaration method : node.getMethods()) {
			if (method.isConstructor()) return true;
		}
		
		// get the list of body declarations
		List<BodyDeclaration> decs =
			(AbstractList<BodyDeclaration>) node.bodyDeclarations();
		
		// add a default constructor
		if(needConstructor()) {
			MethodDeclaration dec = getAST().newMethodDeclaration();
			dec.setConstructor(true);
			dec.setName(getAST().newSimpleName(getSourceType().getElementName()));
			dec.setBody(getAST().newBlock());
			List<Modifier> modifiers = (AbstractList<Modifier>) dec.modifiers();
			
			try {
				if (getType(node).isClass()) {
					modifiers.add(getAST().newModifier(
							Modifier.ModifierKeyword.PUBLIC_KEYWORD));
				}
			} catch (JavaModelException e) {
				e.printStackTrace();
			}
				
			decs.add(dec);
		}
		return true;
	}
	
	/**
	 * @return True if the relationship is generic, false otherwise.
	 */
	protected final boolean isGeneric() {
		return (_cardinality < 1);
	}
	
	/**
	 * @see edu.buffalo.cse.green.relationships.RelationshipGenerator#needChooseTypeDialog()
	 */
	protected boolean needChooseTypeDialog() {
		try {
			int flags = getTargetType().getFlags();
			return Flags.isAbstract(flags) || Flags.isInterface(flags);
		} catch (JavaModelException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public final boolean supportsCardinality() {
		return true;
	}
	
	protected void generateFields() {
		// generate a generic variable name
		String baseName = getBaseVariableName();

		int numFieldNames = isGeneric() ? 1 : _cardinality;
		IType targetType = getTargetType();

		for (int x = 0; x < numFieldNames; x++) {
			// create a unique variable name for the field
			String fieldName = generateVariableName(
					VariableAffix.getAffixString(FieldPrefix) + baseName
					+ VariableAffix.getAffixString(FieldSuffix));

			if (isGeneric()) { // normal relationships
				addField(createParameterizedTypeReference(LIST,
						createTypeReference(targetType)), fieldName);
			} else { // parameterized collection
				addField(createTypeReference(targetType), fieldName);
			}
		}
	}
}
