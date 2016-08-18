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

import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.wizard.WizardDialog;

import edu.buffalo.cse.green.dialogs.wizards.NewFieldWizard;
import edu.buffalo.cse.green.editor.DiagramEditor;
import edu.buffalo.cse.green.editor.model.TypeModel;
import edu.buffalo.cse.green.editor.model.commands.AddFieldCommand;

/**
 * Adds a new field to the selected <code>TypeModel</code>.
 * 
 * @author bcmartin
 * @author zgwang
 */
public class AddFieldAction extends ContextAction {
	/**
	 * Creates a field with an arbitrary name
	 */
	public void doRun() throws JavaModelException {
		TypeModel typeModel = _model.getTypeModel();

		NewFieldWizard wizard = new NewFieldWizard(typeModel);
		WizardDialog dialog = new WizardDialog(
				getEditor().getSite().getShell(), wizard);
		dialog.setMinimumPageSize(300, 500);
		dialog.create();
		int res = dialog.open();

		if (res == WizardDialog.OK) {
			String fieldName = wizard.getFieldName();
			String javadocComment = wizard.getJavaDocComment();
			String modifiers = wizard.getModifiers();
			String typeName = wizard.getTypeName();
			boolean forceImport = wizard.forceImports();
			String commentString = "/**\n";
			for (String line : javadocComment.split("\n")) {
				commentString += " * "+line+"\n";
			}
			commentString += " */\n";
			String fieldString = modifiers + " " + typeName + " "
			+ fieldName + ";\n";

			DiagramEditor editor = (DiagramEditor) _selectionProvider;
			editor.execute(new AddFieldCommand(commentString+fieldString, fieldName,
					typeName, forceImport, typeModel));
		}
		
		getEditor().autoSave();
	}

	/**
	 * @see edu.buffalo.cse.green.editor.action.ContextAction#getLabel()
	 */
	public String getLabel() {
		return "Add Field";
	}

	/**
	 * @see edu.buffalo.cse.green.editor.action.ContextAction#getSupportedModels()
	 */
	protected int getSupportedModels() {
		return CM_FIELD | CM_TYPE;
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