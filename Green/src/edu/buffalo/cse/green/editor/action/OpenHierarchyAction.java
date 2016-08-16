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

import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.ui.actions.OpenTypeHierarchyAction;


/**
 * An adaptation of <code>OpenTypeHierarchyAction</code>.
 * 
 * @author bcmartin
 */
public class OpenHierarchyAction extends ContextAction {
	/**
	 * @see edu.buffalo.cse.green.editor.action.ContextAction#doRun()
	 */
	public void doRun() {
		OpenTypeHierarchyAction actions =
			new OpenTypeHierarchyAction(getEditor().getSite());
		actions.run(new IJavaElement[] {_element});
	}

	/**
	 * @see edu.buffalo.cse.green.editor.action.ContextAction#isAvailableForBinary()
	 */
	public boolean isAvailableForBinary() {
		return true;
	}

	/**
	 * @see edu.buffalo.cse.green.editor.action.ContextAction#getLabel()
	 */
	public String getLabel() {
		return "Open Type Hierarchy";
	}

	/**
	 * @see edu.buffalo.cse.green.editor.action.ContextAction#getSupportedModels()
	 */
	protected int getSupportedModels() {
		return CM_TYPE;
	}

	/**
	 * @see edu.buffalo.cse.green.editor.action.ContextAction#getPath()
	 */
	public Submenu getPath() {
		return Submenu.None;
	}

	/**
	 * @see edu.buffalo.cse.green.editor.action.ContextAction#isEnabled()
	 */
	public boolean isEnabled() {
		return true;
	}
}