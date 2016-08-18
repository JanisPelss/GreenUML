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

import edu.buffalo.cse.green.editor.model.RelationshipModel;

/**
 * Hides a relationship.
 * 
 * @author bcmartin
 */
public class HideRelationshipCommand extends Command {
	private RelationshipModel _rModel;

	public HideRelationshipCommand(RelationshipModel rModel) {
		_rModel = rModel;
	}

	/**
	 * @see org.eclipse.gef.commands.Command#execute()
	 */
	public void execute() {
		super.execute();

		// hide the relationship
		_rModel.showRelationshipExplicitly(false);
	}

	/**
	 * @see org.eclipse.gef.commands.Command#undo()
	 */
	public void undo() {
		_rModel.showRelationshipExplicitly(true);
	}

	/**
	 * @see org.eclipse.gef.commands.Command#redo()
	 */
	public void redo() {
		execute();
	}
}
