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

import static edu.buffalo.cse.green.GreenException.GRERR_RELATIONSHIP_NO_METHODS;
import static edu.buffalo.cse.green.preferences.VariableAffix.*;
import edu.buffalo.cse.green.preferences.VariableAffix;

import java.util.AbstractList;
import java.util.List;

import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.Statement;
import org.eclipse.jdt.core.dom.VariableDeclarationStatement;

import edu.buffalo.cse.green.GreenException;
import edu.buffalo.cse.green.PlugIn;
import edu.buffalo.cse.green.dialogs.ChooseMethodsDialog;
import edu.buffalo.cse.green.dialogs.MethodDialogListener;
import edu.buffalo.cse.green.relationships.CardinalRelationshipGenerator;

/**
 * @see edu.buffalo.cse.green.relationship.CardinalRelationshipGenerator
 * 
 * @author bcmartin
 */
public class DependencyLGenerator extends CardinalRelationshipGenerator {
	private List<IMethod> _selectedMethods;
	
	/**
	 * @see edu.buffalo.cse.green.relationships.RelationshipVisitor#preVisit()
	 * @author zgwang
	 */
	protected void preVisit() {
		_selectedMethods = null; //Ensures that previously used methods do not carry over
		try {
			if (getSourceType().getMethods().length == 0)
				GreenException.errorDialog(GRERR_RELATIONSHIP_NO_METHODS);
			else {
				ChooseMethodsDialog dialog = new ChooseMethodsDialog(
						PlugIn.getDefaultShell(), getSourceType());
				dialog.addMethodDialogListener(new MethodDialogListener() {
					public void okPressed(List<IMethod> selectedMethods) {
						_selectedMethods = selectedMethods;
					}
				});
				dialog.open();
			}
		}
		catch (JavaModelException jme) {
			jme.printStackTrace();
		}
	}
	
	/**
	 * @see edu.buffalo.cse.green.relationships.RelationshipGenerator#process(org.eclipse.jdt.core.dom.Block)
	 */
	protected boolean process(Block block) {
		if (_selectedMethods == null) return true;
		if (!correctTypeToGenerate()) return true;
		IMethod method =
			(IMethod) getMethodDeclaration().resolveBinding().getJavaElement();
		if (!_selectedMethods.contains(method)) return true;

		List<Statement> statements =
			(AbstractList<Statement>) (List) block.statements();

		for (int x = 0; x < _cardinality; x++) {
			String localName = generateVariableName(
					VariableAffix.getAffixString(LocalPrefix) + getBaseVariableName()
					+ VariableAffix.getAffixString(LocalSuffix));
			VariableDeclarationStatement varDec =
				createNormalVariableDeclaration(getTargetType(), localName,
						createInvocation(createTypeReference(
								getActualTargetType())));
			statements.add(0, varDec);

			// add the variable name(s) to the appropriate list(s)
			getLocalDeclarations().add(localName);
		}
		
		return true;
	}

	/* (non-Javadoc)
	 * @see edu.buffalo.cse.green.relationships.RelationshipGenerator#needConstructor()
	 */
	@Override
	protected boolean needConstructor() {
		return false;
	}
}