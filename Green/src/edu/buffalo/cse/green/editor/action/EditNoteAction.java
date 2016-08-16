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

import static edu.buffalo.cse.green.constants.DialogStrings.DIALOG_EDIT_NOTE_TITLE;

import org.eclipse.jdt.core.JavaModelException;

import edu.buffalo.cse.green.dialogs.EditNoteDialog;
import edu.buffalo.cse.green.editor.model.NoteModel;
import edu.buffalo.cse.green.editor.model.commands.EditNoteCommand;

/**
 * Brings up a dialog that allows the editing of the text in a note.
 * 
 * @author bcmartin
 * @author zgwang
 */
public class EditNoteAction extends ContextAction {
	/**
	 * @see edu.buffalo.cse.green.editor.action.ContextAction#doRun()
	 */
	public void doRun() throws JavaModelException {
		NoteModel model = (NoteModel) _model;

		EditNoteDialog dialog = new EditNoteDialog(getEditor().getSite().getShell(), 
				DIALOG_EDIT_NOTE_TITLE, model.getLabel());
		
		dialog.setBlockOnOpen(true);
		dialog.open();
		
		if (dialog.getValue() != null) {
			getEditor().execute(
					new EditNoteCommand(model, dialog.getValue()));
		}
	}

	/**
	 * @see edu.buffalo.cse.green.editor.action.ContextAction#getLabel()
	 */
	public String getLabel() {
		return "Edit";
	}

	/**
	 * @see edu.buffalo.cse.green.editor.action.ContextAction#getSupportedModels()
	 */
	protected int getSupportedModels() {
		return CM_NOTE;
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