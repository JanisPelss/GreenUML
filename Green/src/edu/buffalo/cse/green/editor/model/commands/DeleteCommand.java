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
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;

import edu.buffalo.cse.green.PlugIn;
import edu.buffalo.cse.green.editor.DiagramEditor;

/**
 * A generic delete command.
 * 
 * @author bcmartin
 */
public abstract class DeleteCommand extends Command {
	private boolean _suppressMessage = false;

	protected DeleteCommand() {}

	/**
	 * Called to perform deletion.
	 */
	public abstract void doDelete();

	/**
	 * @return The error message, or null if none.
	 */
	public abstract String getDeleteMessage();

	/**
	 * @see org.eclipse.gef.commands.Command#execute()
	 */
	public final void execute() {
		super.execute();
		boolean perform;
		String deleteMessage = getDeleteMessage();
		Shell shell = PlugIn.getDefault().getWorkbench()
				.getActiveWorkbenchWindow().getShell();

		if (deleteMessage == null) {
			MessageDialog.openError(shell, "Delete Error",
					"You cannot delete this");
			perform = false;
		} else {
			if (_suppressMessage) {
				perform = true;
			} else {
				perform = MessageDialog.openConfirm(shell, "Confirm Delete",
						deleteMessage);
			}
		}

		if (perform) {
			doDelete();
			
			if(PlugIn.getBooleanPreference(P_AUTOARRANGE))
			{
				DiagramEditor.getActiveEditor().execute(new AutoArrangeCommand());
			}
		}
	}

	/**
	 * Prevents the error message from being displayed.
	 * 
	 * @param value - Whether error messages should be suppressed. 
	 */
	public final void suppressMessage(boolean value) {
		_suppressMessage = value;
	}
	
	/**
	 * @see org.eclipse.gef.commands.Command#canUndo()
	 */
	public boolean canUndo() {
		return false;
	}
}
