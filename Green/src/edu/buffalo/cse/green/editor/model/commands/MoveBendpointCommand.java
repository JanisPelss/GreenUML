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
import org.eclipse.gef.requests.BendpointRequest;

import edu.buffalo.cse.green.editor.view.RelationshipFigure;

/**
 * Moves a bendpoint.
 * 
 * @author Blake
 */
public class MoveBendpointCommand extends Command {
	private BendpointRequest _request;
	private DeleteBendpointCommand _deleteCommand;
	private CreateBendpointCommand _createCommand;
	
	public MoveBendpointCommand(BendpointRequest request) {
		_request = request;
	}
	
	/**
	 * @see org.eclipse.gef.commands.Command#execute()
	 */
	public void execute() {
		_deleteCommand = new DeleteBendpointCommand(_request);
		_createCommand = new CreateBendpointCommand(
				(RelationshipFigure) _request.getSource().getFigure(),
				_request);
		
		_deleteCommand.execute();
		_createCommand.execute();
	}
	
	/**
	 * @see org.eclipse.gef.commands.Command#redo()
	 */
	public void redo() {
		_deleteCommand.redo();
		_createCommand.redo();
	}
	
	/**
	 * @see org.eclipse.gef.commands.Command#undo()
	 */
	public void undo() {
		_createCommand.undo();
		_deleteCommand.undo();
	}
}
