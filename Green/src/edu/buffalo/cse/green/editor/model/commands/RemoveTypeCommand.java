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

import static edu.buffalo.cse.green.preferences.PreferenceInitializer.P_AUTOARRANGE;

import org.eclipse.gef.commands.Command;

import edu.buffalo.cse.green.PlugIn;
import edu.buffalo.cse.green.editor.DiagramEditor;
import edu.buffalo.cse.green.editor.model.RootModel;
import edu.buffalo.cse.green.editor.model.TypeModel;

/**
 * Removes a <code>TypeModel</code> from the editor without deleting it.
 * 
 * @author bcmartin
 */
public class RemoveTypeCommand extends Command {
	private TypeModel _typeModel;

	public RemoveTypeCommand(TypeModel model) {
		_typeModel = model;
	}

	/**
	 * @see org.eclipse.gef.commands.Command#execute()
	 */
	public void execute() {
		_typeModel.removeFromParent();
		
		if(PlugIn.getBooleanPreference(P_AUTOARRANGE))
		{
			DiagramEditor.getActiveEditor().execute(new AutoArrangeCommand());
		}
	}

	/**
	 * @see org.eclipse.gef.commands.Command#undo()
	 */
	public void undo() {
		RootModel root = (RootModel) _typeModel.getParent();
		TypeModel newTypeModel = root.createTypeModel(_typeModel.getType());
		newTypeModel.setLocation(_typeModel.getLocation());
		_typeModel = newTypeModel;
		root.updateRelationships();
	}
	
	/**
	 * @see org.eclipse.gef.commands.Command#redo()
	 */
	public void redo() {
		execute();
	}
}