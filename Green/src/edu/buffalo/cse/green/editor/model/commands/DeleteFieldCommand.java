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

import org.eclipse.jdt.core.JavaModelException;

import edu.buffalo.cse.green.editor.model.FieldModel;

/**
 * Deletes the selected field.
 * 
 * @author bcmartin
 */
public class DeleteFieldCommand extends DeleteCommand {
	private FieldModel _fieldModel;

	public DeleteFieldCommand(FieldModel model) {
		super();
		_fieldModel = model;
	}

	/**
	 * @see edu.buffalo.cse.green.editor.model.commands.DeleteCommand#doDelete()
	 */
	public void doDelete() {
		try {
			_fieldModel.getMember().delete(true, null);
		} catch (JavaModelException jex) {
			jex.printStackTrace();
		}
	}

	/**
	 * @see edu.buffalo.cse.green.editor.model.commands.DeleteCommand#getDeleteMessage()
	 */
	public String getDeleteMessage() {
		if (_fieldModel.getMember().isBinary()) { return null; }

		return "Are you sure you want to delete "
				+ _fieldModel.getMember().getElementName() + "?";
	}
}