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

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.JavaModelException;

import edu.buffalo.cse.green.editor.model.MemberModel;

/**
 * An adaptation of refactor -> rename's support.
 * 
 * @author bcmartin
 */
public class RefactorRenameAction extends ContextAction {
	public RefactorRenameAction() {
		setAccelerator(EDITOR_REFACTOR_RENAME);
	}
	
	/**
	 * @see edu.buffalo.cse.green.editor.action.ContextAction#doRun()
	 */
	public void doRun() throws JavaModelException {
		MemberModel model = (MemberModel) _model;

		try {
			model.getRenameSupport().openDialog(getEditor().getSite().getShell());
			getEditor().doSave(null);
			getEditor().refresh();

		} catch (CoreException e) {
			e.printStackTrace();
		}
	}

	/**
	 * @see edu.buffalo.cse.green.editor.action.ContextAction#getLabel()
	 */
	public String getLabel() {
		return "Rename...";
	}

	/**
	 * @see edu.buffalo.cse.green.editor.action.ContextAction#getSupportedModels()
	 */
	protected int getSupportedModels() {
		return CM_FIELD | CM_METHOD | CM_TYPE;
	}

	/**
	 * @see edu.buffalo.cse.green.editor.action.ContextAction#getPath()
	 */
	public Submenu getPath() {
		return Submenu.Refactor;
	}

	/**
	 * @see edu.buffalo.cse.green.editor.action.ContextAction#isEnabled()
	 */
	public boolean isEnabled() {
		return !isBinary();
	}
}