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

import edu.buffalo.cse.green.editor.model.MethodModel;

/**
 * Deletes the selected method.
 * 
 * @author bcmartin
 */
public class DeleteMethodCommand extends DeleteCommand {
	private MethodModel _methodModel;

	public DeleteMethodCommand(MethodModel model) {
		super();
		_methodModel = model;
	}

	/**
	 * @see edu.buffalo.cse.green.editor.model.commands.DeleteCommand#doDelete()
	 */
	public void doDelete() {
		try {
			_methodModel.getMember().delete(true, null);
		} catch (JavaModelException jex) {
			jex.printStackTrace();
		}
	}

	/**
	 * @see edu.buffalo.cse.green.editor.model.commands.DeleteCommand#getDeleteMessage()
	 */
	public String getDeleteMessage() {
		if (_methodModel.getMember().isBinary()) { return null; }

		return "Are you sure you want to delete "
				+ _methodModel.getMember().getElementName() + "?";
	}
}