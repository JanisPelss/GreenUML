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
 * Calls redo() in the editor.
 * 
 * @author bcmartin
 * @author tomhicks
 */
public class RedoAction extends ContextAction {
	/**
	 * @see edu.buffalo.cse.green.editor.action.ContextAction#isAvailableForBinary()
	 */
	public boolean isAvailableForBinary() {
		return true;
	}

	/**
	 * @see org.eclipse.jface.action.IAction#isEnabled()
	 */
	public boolean isEnabled() {
		return getEditor().canRedo();
	}

	/**
	 * @see edu.buffalo.cse.green.editor.action.ContextAction#getLabel()
	 */
	public String getLabel() {
		return "Redo";
	}

	/**
	 * @see edu.buffalo.cse.green.editor.action.ContextAction#doRun()
	 */
	protected void doRun() throws JavaModelException {
		getEditor().redo();
	}

	/**
	 * @see edu.buffalo.cse.green.editor.action.ContextAction#getSupportedModels()
	 */
	protected int getSupportedModels() {
		return CM_ALL;
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
		return ActionFactory.REDO;
	}
}
