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

import edu.buffalo.cse.green.PlugIn;
import edu.buffalo.cse.green.editor.DiagramEditor;
import edu.buffalo.cse.green.editor.model.commands.AlterRelationshipVisibilityCommand;
import edu.buffalo.cse.green.relationships.RelationshipGroup;

/**
 * Alters the visibility of all association models in the diagram.
 * 
 * @author cgullans
 */
public class AlterRelationshipVisibilityAction extends ContextAction {
	private Class _partClass;

	public AlterRelationshipVisibilityAction(Class partClass) {
		super(partClass);
		_partClass = partClass;
	}

	/**
	 * @see edu.buffalo.cse.green.editor.action.ContextAction#getLabel()
	 */
	public String getLabel() {
		String visible;
		RelationshipGroup group = PlugIn.getRelationshipGroup(_partClass);

		// determine whether the action should say
		// "hide" or "show" by checking visibility
		if (group.isVisible()) {
			visible = "Hide";
		} else {
			visible = "Show";
		}

		String prefix = "";
		
		if (group.getSubtype() != null) {
			prefix = group.getSubtype();
		}
		
		return visible + " All " + prefix + " " + group.getName() + " Relationships";
	}

	/**
	 * @see edu.buffalo.cse.green.editor.action.ContextAction#doRun()
	 */
	protected void doRun() throws JavaModelException {
		DiagramEditor editor = getEditor();
		editor.execute(new AlterRelationshipVisibilityCommand(
				editor, _partClass));
	}

	/**
	 * @see edu.buffalo.cse.green.editor.action.ContextAction#getSupportedModels()
	 */
	protected int getSupportedModels() {
		return CM_EDITOR;
	}

	/**
	 * @see edu.buffalo.cse.green.editor.action.ContextAction#getPath()
	 */
	public Submenu getPath() {
		return Submenu.Visibility;
	}

	/**
	 * @see edu.buffalo.cse.green.editor.action.ContextAction#isEnabled()
	 */
	public boolean isEnabled() {
		return true;
	}
}