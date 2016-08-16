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

import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.ui.actions.OpenSuperImplementationAction;
import org.eclipse.ui.IWorkbenchSite;

/**
 * An action to invoke the JDT's own Open Super Implementation.  This will
 * open a Java editor to the method which the selected method overrides.
 * 
 * @author <a href="mailto:zgwang@sourceforge.net">Gene Wang</a>
 */
public class OpenMethodSuperImplementationAction extends ContextAction {
	/**
	 * @see edu.buffalo.cse.green.editor.action.ContextAction#doRun()
	 */
	public void doRun() throws JavaModelException {
		IWorkbenchSite site = getEditor().getSite();
		OpenSuperImplementationAction action = new OpenSuperImplementationAction(site);
		action.run((IMethod) _element);
	}

	/**
	 * @see edu.buffalo.cse.green.editor.action.ContextAction#getLabel()
	 */
	public String getLabel() {
		return "Open Super Implementation";
	}

	/**
	 * @see edu.buffalo.cse.green.editor.action.ContextAction#getPath()
	 */
	public Submenu getPath() {
		return Submenu.None;
	}

	/**
	 * @see org.eclipse.jface.action.IAction#isEnabled()
	 */
	public boolean isEnabled() {
		return true;
	}

	/**
	 * @see edu.buffalo.cse.green.editor.action.ContextAction#getSupportedModels()
	 */
	protected int getSupportedModels() {
		return CM_METHOD;
	}
}