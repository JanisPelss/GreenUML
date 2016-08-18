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

import org.eclipse.jdt.core.IClassFile;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.JavaModelException;

import edu.buffalo.cse.green.editor.model.TypeModel;
import edu.buffalo.cse.green.editor.model.commands.IncrementalExploreCommand;

/**
 * Starts incremental exploration from the given <code>TypeModel</code>.
 * 
 * @author bcmartin
 * @author dk29
 */
public class IncrementalExploreAction extends ContextAction {
	public IncrementalExploreAction() {
		setAccelerator(EDITOR_INCREMENTAL_EXPLORATION);
	}
	
	/**
	 * @see edu.buffalo.cse.green.editor.action.ContextAction#doRun()
	 */
	public void doRun() throws JavaModelException {
		getEditor().execute(new IncrementalExploreCommand(getEditor(),
				(TypeModel) _model, false));
	}

	/**
	 * @see edu.buffalo.cse.green.editor.action.ContextAction#getLabel()
	 */
	public String getLabel() {
		return "All";
	}

	/**
	 * @see edu.buffalo.cse.green.editor.action.ContextAction#getSupportedModels()
	 */
	protected int getSupportedModels() {
		return CM_TYPE;
	}

	/**
	 * @see edu.buffalo.cse.green.editor.action.ContextAction#isAvailableForBinary()
	 */
	public boolean isAvailableForBinary() {
		IClassFile classFile = (IClassFile) _element
				.getAncestor(IJavaElement.CLASS_FILE);

		try {
			return (classFile.getSourceRange() != null);
		} catch (JavaModelException e) {
			// no source code attached
			return false;
		}
	}

	/**
	 * @see edu.buffalo.cse.green.editor.action.ContextAction#getPath()
	 */
	public Submenu getPath() {
		return Submenu.IncrExplore;
	}

	/**
	 * @see edu.buffalo.cse.green.editor.action.ContextAction#isEnabled()
	 */
	public boolean isEnabled() {
		return true;
	}
}