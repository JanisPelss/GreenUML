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

import static org.eclipse.jdt.internal.ui.JavaPluginImages.IMG_CORRECTION_REMOVE;

import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.ui.JavaPluginImages;
import org.eclipse.jface.resource.ImageDescriptor;

import edu.buffalo.cse.green.editor.model.TypeModel;
import edu.buffalo.cse.green.editor.model.commands.RemoveTypeCommand;

/**
 * Removes the selected <code>TypeModel</code> from the diagram
 * 
 * @author bcmartin
 */
public class RemoveTypeAction extends ContextAction {
	public RemoveTypeAction() {
		setAccelerator(EDITOR_UNLOAD_TYPE);
	}
	
	/**
	 * @see edu.buffalo.cse.green.editor.action.ContextAction#doRun()
	 */
	public void doRun() throws JavaModelException {
		TypeModel typeModel = _model.getTypeModel();
		
		getEditor().execute(new RemoveTypeCommand(typeModel));
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.action.Action#getImageDescriptor()
	 */
	public ImageDescriptor getImageDescriptor() {
		return JavaPluginImages.getDescriptor(IMG_CORRECTION_REMOVE);
	}

	/**
	 * @see edu.buffalo.cse.green.editor.action.ContextAction#getLabel()
	 */
	public String getLabel() {
		return "Remove From Diagram";
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
		return true;
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