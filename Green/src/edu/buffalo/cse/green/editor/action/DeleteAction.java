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
import org.eclipse.ui.actions.ActionFactory;

/**
 * Deletes the selected <code>IJavaElement</code>.
 * 
 * @author bcmartin
 */
public class DeleteAction extends ContextAction {
	/**
	 * @see edu.buffalo.cse.green.editor.action.ContextAction#doRun()
	 */
	public void doRun() throws JavaModelException {
		// perform the selected model's delete command
		getEditor().execute(_model.getDeleteCommand(getEditor()));
		// refresh the editor
		getEditor().autoSave();
	}

	/**
	 * @see edu.buffalo.cse.green.editor.action.ContextAction#getLabel()
	 */
	public String getLabel() {
		return "Delete";
	}

	/**
	 * @see edu.buffalo.cse.green.editor.action.ContextAction#getSupportedModels()
	 */
	protected int getSupportedModels() {
		return CM_MEMBER | CM_NOTE | CM_RELATIONSHIP;
	}

	/**
	 * @see edu.buffalo.cse.green.editor.action.ContextAction#getPath()
	 */
	public Submenu getPath() {
		return Submenu.Invisible;
	}

	/**
	 * @see edu.buffalo.cse.green.editor.action.ContextAction#getGlobalActionHandler()
	 */
	public ActionFactory getGlobalActionHandler() {
		return ActionFactory.DELETE;
	}

	/**
	 * @see edu.buffalo.cse.green.editor.action.ContextAction#isEnabled()
	 */
	public boolean isEnabled() {
		return !isBinary();
	}
}