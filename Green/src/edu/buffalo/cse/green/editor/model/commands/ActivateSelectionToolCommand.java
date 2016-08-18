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
import org.eclipse.gef.palette.ToolEntry;
import org.eclipse.gef.ui.palette.PaletteViewer;

import edu.buffalo.cse.green.editor.DiagramEditor;

/**
 * Activates the selection tool, wrapping this command around the other command
 * that will be executed.
 * 
 * @author cgullans
 */
public class ActivateSelectionToolCommand extends Command {
	private DiagramEditor _editor;

	/**
	 * The command to chain along with this command.
	 */
	private Command _command;

	/**
	 * The tool that was previously active.
	 */
	private ToolEntry _oldActiveTool;

	public ActivateSelectionToolCommand(DiagramEditor editor, Command command) {
		_editor = editor;
		_command = command;
	}

	/**
	 * @see org.eclipse.gef.commands.Command#canExecute()
	 */
	public boolean canExecute() {
		return _command.canExecute();
	}

	/**
	 * @see org.eclipse.gef.commands.Command#execute()
	 */
	public void execute() {
		_editor.execute(_command);
		makeSelectionToolActive();
	}

	/**
	 * @see org.eclipse.gef.commands.Command#canUndo()
	 */
	public boolean canUndo() {
		return _command.canUndo();
	}

	/**
	 * @see org.eclipse.gef.commands.Command#undo()
	 */
	public void undo() {
		_editor.getRootPart().getViewer().getEditDomain().getPaletteViewer()
				.setActiveTool(_oldActiveTool);
		_editor.undo();
		_oldActiveTool = null;
	}

	/**
	 * @see org.eclipse.gef.commands.Command#redo()
	 */
	public void redo() {
		_editor.redo();
		makeSelectionToolActive();
	}

	/**
	 * Helper method to make the selection tool active.
	 */
	private void makeSelectionToolActive() {
		PaletteViewer palette = _editor.getRootPart().getViewer()
				.getEditDomain().getPaletteViewer();
		_oldActiveTool = palette.getActiveTool();
		palette.setActiveTool(DiagramEditor.getSelectionTool());
	}
}
