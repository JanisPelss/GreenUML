/* This file is part of Green.
 *
 * Copyright (C) 2005 The Research Foundation of State University of New York
 * All Rights Under Copyright Reserved, The Research Foundation of S.U.N.Y.
 * 
 * Green is free software, licensed under the terms of the Eclipse
 * Public License, version 1.0.  The license is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package edu.buffalo.cse.green.editor.model.commands;

import org.eclipse.gef.commands.Command;

import edu.buffalo.cse.green.PlugIn;
import edu.buffalo.cse.green.editor.DiagramEditor;
import edu.buffalo.cse.green.editor.model.RootModel;
import edu.buffalo.cse.green.relationships.RelationshipGroup;

/**
 * Alters the visibility of the given type of relationship. If the given type is
 * visible it will become hidden and vice versa.
 * 
 * @author cgullans
 */
public class AlterRelationshipVisibilityCommand extends Command {
	/**
	 * The class representing the kind of relationship. 
	 */
	private Class _partClass;

	/**
	 * The root model.
	 */
	private RootModel _root;

	public AlterRelationshipVisibilityCommand(DiagramEditor editor,
			Class partClass) {
		_root = editor.getRootModel();
		_partClass = partClass;
	}

	/**
	 * @see org.eclipse.gef.commands.Command#execute()
	 */
	public void execute() throws IllegalArgumentException {
		RelationshipGroup rGroup = PlugIn.getRelationshipGroup(_partClass);

		if (rGroup.isVisible()) {
			_root.hideRelationshipsOfType(_partClass);
			rGroup.setVisible(false);
		} else {
			_root.showRelationshipsOfType(_partClass);
			rGroup.setVisible(true);
		}
	}

	/**
	 * @see org.eclipse.gef.commands.Command#undo()
	 */
	public void undo() {
		execute();
	}

	/**
	 * @see org.eclipse.gef.commands.Command#redo()
	 */
	public void redo() {
		execute();
	}
}