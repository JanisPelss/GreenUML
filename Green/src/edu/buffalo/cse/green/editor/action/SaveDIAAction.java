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
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.IActionDelegate;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.ActionFactory;

import edu.buffalo.cse.green.editor.DiagramEditor;

/**
 * Prints diagram.
 * 
 * @author bmm6
 */
public class SaveDIAAction extends ContextAction implements IActionDelegate{
	ISelection _selection;
	
	/**
	 * @see edu.buffalo.cse.green.editor.action.ContextAction#doRun()
	 */
	public void doRun() throws JavaModelException {
		DiagramEditor.getActiveEditor().doSaveAs();
		
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.action.Action#getImageDescriptor()
	 */
	public ImageDescriptor getImageDescriptor() {
		Image img = PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_ETOOL_SAVEAS_EDIT);
		return ImageDescriptor.createFromImage(img);
	}

	/**
	 * @see edu.buffalo.cse.green.editor.action.ContextAction#getLabel()
	 */
	public String getLabel() {
		return "Save As...";
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
		return Submenu.None;
	}

	/**
	 * @see edu.buffalo.cse.green.editor.action.ContextAction#getGlobalActionHandler()
	 */
	public ActionFactory getGlobalActionHandler() {
		return ActionFactory.SAVE_AS;
	}

	/**
	 * @see edu.buffalo.cse.green.editor.action.ContextAction#isEnabled()
	 */
	public boolean isEnabled() {
		return true;
	}

	public void run(IAction action) {
			DiagramEditor.getActiveEditor().doSaveAs();
	}

	public void selectionChanged(IAction action, ISelection selection) {
		if (selection instanceof StructuredSelection) {
			_selection = selection;
		}
		
	}
}