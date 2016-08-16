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
import static org.eclipse.jface.window.Window.OK;

import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.palette.ToolEntry;

import edu.buffalo.cse.green.PlugIn;
import edu.buffalo.cse.green.editor.DiagramEditor;
import edu.buffalo.cse.green.editor.model.AbstractModel;
import edu.buffalo.cse.green.editor.model.NoteModel;
import edu.buffalo.cse.green.editor.model.RootModel;

/**
 * Creates a model using the selected palette tool.
 * 
 * @author bcmartin
 */
public class CreateCommand extends Command {
	/**
	 * The child model.
	 */
	private AbstractModel<?, RootModel, ?> _model;

	/**
	 * The location of the created model.
	 */
	private Point _location;

	/**
	 * The size of the created model.
	 */
	private Dimension _size;

	/**
	 * The parent of the created model.
	 */
	private RootModel _root;

	public CreateCommand(RootModel root) {
		super();

		_model = null;
		_location = null;
		_size = null;
		_root = root;
	}

	/**
	 * Sets the model to be created.
	 */
	public void setChild(AbstractModel<?, RootModel, ?> model) {
		_model = model;
	}

	/**
	 * Sets the location of the created model.
	 * 
	 * @param location - The location.
	 */
	public void setLocation(Point location) {
		_location = location;
	}

	/**
	 * Sets the size of the created model.
	 * 
	 * @param size - The size.
	 */
	public void setSize(Dimension size) {
		_size = size;
	}

	/**
	 * @see org.eclipse.gef.commands.Command#execute()
	 */
	public void execute() {
		_model.setLocation(_location);
		_model.setSize(_size);
		_model.setParent(_root);
		
		ToolEntry tool = DiagramEditor.getActiveEditor().getActiveTool();
		
		if (_model.invokeCreationDialog(tool) == OK) {
			_model.createNewInstance(_model);
		}
		
		if(PlugIn.getBooleanPreference(P_AUTOARRANGE))
		{
			DiagramEditor.getActiveEditor().execute(new AutoArrangeCommand());
		}
	}

	/**
	 * @see org.eclipse.gef.commands.Command#canUndo()
	 */
	public boolean canUndo() {
		return (_model instanceof NoteModel);
	}

	/**
	 * @see org.eclipse.gef.commands.Command#undo()
	 */
	public void undo() {
		_model.removeFromParent();
	}

	/**
	 * @see org.eclipse.gef.commands.Command#redo()
	 */
	public void redo() {
		execute();
	}
}