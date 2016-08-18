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

import edu.buffalo.cse.green.editor.model.NoteModel;

/**
 * Changes the text of a note
 * 
 * @author bcmartin
 * @author rjtruban
 */
public class EditNoteCommand extends Command {
	private NoteModel _nModel;

	private String _newText;

	private String _oldText;

	public EditNoteCommand(NoteModel nModel, String text) {
		_nModel = nModel;
		_newText = text;
	}

	/**
	 * @see org.eclipse.gef.commands.Command#execute()
	 */
	public void execute() {
		_oldText = _nModel.getLabel();
		_nModel.setLabel(_newText);
	}

	/**
	 * @see org.eclipse.gef.commands.Command#canUndo()
	 */
	public boolean canUndo() {
		return true;
	}

	/**
	 * @see org.eclipse.gef.commands.Command#undo()
	 */
	public void undo() {
		_nModel.setLabel(_oldText);
	}

	/**
	 * @see org.eclipse.gef.commands.Command#redo()
	 */
	public void redo() {
		_nModel.setLabel(_newText);
	}
}
