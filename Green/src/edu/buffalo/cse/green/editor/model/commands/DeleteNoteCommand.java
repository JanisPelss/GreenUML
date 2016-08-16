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

import edu.buffalo.cse.green.editor.model.NoteModel;

/**
 * Deletes the selected note.
 * 
 * @author hk47
 */
public class DeleteNoteCommand extends DeleteCommand {
	private NoteModel _noteModel;

	public DeleteNoteCommand(NoteModel model) {
		_noteModel = model;
	}

	/**
	 * @see edu.buffalo.cse.green.editor.model.commands.DeleteCommand#doDelete()
	 */
	public void doDelete() {
		_noteModel.removeFromParent();
	}

	/**
	 * @see edu.buffalo.cse.green.editor.model.commands.DeleteCommand#getDeleteMessage()
	 */
	public String getDeleteMessage() {
		return "Are you sure you want to delete that note?";
	}

	/**
	 * @see org.eclipse.gef.commands.Command#undo()
	 */
	public void undo() {
		_noteModel.getRootModel().addChild(_noteModel);
	}

	/**
	 * @see org.eclipse.gef.commands.Command#redo()
	 */
	public void redo() {
		execute();
	}
	
	/**
	 * @see org.eclipse.gef.commands.Command#canUndo()
	 */
	public boolean canUndo() {
		return true;
	}
}