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

import edu.buffalo.cse.green.editor.DiagramEditor;

/**
 * Zooms in on the diagram and makes the figures proportionately bigger
 * 
 * @author zgwang
 */
public class ZoomInAction extends ContextAction {

	/**
	 * Constructor
	 */
	public ZoomInAction() {
		setAccelerator('x');
	}
	
	/**
	 * @see edu.buffalo.cse.green.editor.action.ContextAction#doRun()
	 */
	@Override
	protected void doRun() throws JavaModelException {
		DiagramEditor.getActiveEditor().getZoomManager().zoomIn();
	}

	/**
	 * @see edu.buffalo.cse.green.editor.action.ContextAction#getLabel()
	 */
	@Override
	public String getLabel() {
		return "Zoom In";
	}

	/**
	 * @see edu.buffalo.cse.green.editor.action.ContextAction#getPath()
	 */
	@Override
	public Submenu getPath() {
		return Submenu.Zoom;
	}

	/**
	 * @see edu.buffalo.cse.green.editor.action.ContextAction#getSupportedModels()
	 */
	@Override
	protected int getSupportedModels() {
		return CM_EDITOR;
	}

	/**
	 * @see edu.buffalo.cse.green.editor.action.ContextAction#isEnabled()
	 */
	@Override
	public boolean isEnabled() {
		return DiagramEditor.getActiveEditor().getZoomManager().canZoomIn();
	}

}
