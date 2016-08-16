/* This file is part of Green.
 *
 * Copyright (C) 2005 The Research Foundation of State University of New York
 * All Rights Under Copyright Reserved, The Research Foundation of S.U.N.Y.
 * 
 * Green is free software, licensed under the terms of the Eclipse
 * Public License, version 1.0.  The license is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package edu.buffalo.cse.green.editor.action;

import static edu.buffalo.cse.green.constants.DialogStrings.WIZARD_ADD_METHOD_TITLE;

import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.wizard.WizardDialog;

import edu.buffalo.cse.green.GreenException;
import edu.buffalo.cse.green.dialogs.wizards.NewMethodWizard;
import edu.buffalo.cse.green.editor.DiagramEditor;
import edu.buffalo.cse.green.editor.model.TypeModel;
import edu.buffalo.cse.green.editor.model.commands.AddMethodCommand;

/**
 * Adds a new method to the selected <code>TypeModel</code>.
 * 
 * @author bcmartin
 */
public class AddMethodAction extends ContextAction {
	/**
	 * Creates a method with an arbitrary name.
	 */
	public void doRun() throws JavaModelException {
		String typeString = null;
		TypeModel typeModel = _model.getTypeModel();

		// Open wizard for getting method signature
		NewMethodWizard wizard = new NewMethodWizard((TypeModel) typeModel);

		WizardDialog dialog = new WizardDialog(
				getEditor().getSite().getShell(), wizard);
		dialog.setMinimumPageSize(300, 500);
		dialog.create();
		int res = dialog.open();

		if (res == WizardDialog.OK) {
			String methodName = wizard.getMethodName();
			String javadocComment = wizard.getJavaDocComment();
			String modifiers = wizard.getModifiers();
			String returnTypeName = wizard.getReturnTypeName();
			String parameters = wizard.getParameters();
			String body = getBody(returnTypeName);
			boolean forceImports = wizard.forceImports();
			boolean abstr = modifiers.indexOf("abstract") != -1;
			if (typeModel.isClass() && !abstr) {
				typeString = " {\n\t" + body + "\n}";
			} else if (typeModel.isInterface() || abstr) {
				typeString = ";";
			} else {
				GreenException.illegalOperation("Invalid type");
			}
			String commentString = "/**\n";
			for (String line : javadocComment.split("\n")) {
				commentString += " * "+line+"\n";
			}
			commentString += " */\n";
			String methodString = modifiers + " " + returnTypeName + " "
					+ methodName + "(" + parameters + ")" + typeString + '\n';

			DiagramEditor editor = (DiagramEditor) _selectionProvider;
			editor.execute(new AddMethodCommand(methodName, returnTypeName, commentString+methodString, parameters, forceImports,
					typeModel));
		}

		getEditor().autoSave();
	}

	/**
	 * @see edu.buffalo.cse.green.editor.action.ContextAction#getLabel()
	 */
	public String getLabel() {
		return WIZARD_ADD_METHOD_TITLE;
	}

	/**
	 * @see edu.buffalo.cse.green.editor.action.ContextAction#getSupportedModels()
	 */
	protected int getSupportedModels() {
		return CM_METHOD | CM_TYPE;
	}

	/**
	 * @param typeName - The return type of the method.  
	 * @return a string corresponding to the default return value of the method.
	 */
	public String getBody(String typeName) {
		if (typeName.equals("void")) {
			return "";
		} else if (typeName.equals("byte") || typeName.equals("char")
				|| typeName.equals("short") || typeName.equals("long")
				|| typeName.equals("int") || typeName.equals("float")
				|| typeName.equals("double") || typeName.equals("boolean")) {
			return "return 0;";
		} else {
			return "return null;";
		}
	}

	/**
	 * @see edu.buffalo.cse.green.editor.action.ContextAction#getPath()
	 */
	public Submenu getPath() {
		return Submenu.Add;
	}

	/**
	 * @see edu.buffalo.cse.green.editor.action.ContextAction#isEnabled()
	 */
	public boolean isEnabled() {
		return !isBinary();
	}
}