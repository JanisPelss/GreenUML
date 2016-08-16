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

import static org.eclipse.gef.RequestConstants.REQ_MOVE_BENDPOINT;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.gef.commands.Command;
import org.eclipse.gef.requests.BendpointRequest;

import edu.buffalo.cse.green.GreenException;
import edu.buffalo.cse.green.editor.model.RelationshipModel;
import edu.buffalo.cse.green.editor.view.GreenBendpoint;
import edu.buffalo.cse.green.editor.view.RelationshipFigure;

/**
 * Creates a bendpoint.
 * 
 * @author bcmartin
 */
public class CreateBendpointCommand extends Command {
	/**
	 * The request to create the bendpoint.
	 */
	private BendpointRequest _request;
	
	/**
	 * The parent of the bendpoint.
	 */
	private RelationshipFigure _figure;
	
	private Command _redoCommand;
	
	public CreateBendpointCommand(RelationshipFigure rFigure,
			BendpointRequest request) {
		_figure = rFigure;
		_request = request;
	}
	
	/**
	 * @param request - The request.
	 * @return A bendpoint representing the given request.
	 */
	public GreenBendpoint getBendpoint(BendpointRequest request) {
		// create a bendpoint and set its initial location
    	GreenBendpoint bendpoint = new GreenBendpoint(_figure);
    	bendpoint.setAbsoluteLocation(request.getLocation());
    	
    	return bendpoint;
	}
	
	/**
	 * @see org.eclipse.gef.commands.Command#execute()
	 */
	public void execute() {
		if (_figure.isRecursive()) {
			return;
		}
		
		// get the bendpoint and connection
		GreenBendpoint bendpoint = getBendpoint(_request);
		RelationshipModel rModel =
			(RelationshipModel) _request.getSource().getModel();
		
		// retrieve the list of bendpoints
    	List constraint =
    		(List) _figure.getConnectionRouter().getConstraint(_figure);
    	List<GreenBendpoint> list = (ArrayList<GreenBendpoint>) constraint;

    	if (list == null) {
    		list = new ArrayList<GreenBendpoint>();

    		// set the model's list of bendpoints so that it can save the
    		// bendpoints for loading
    		rModel.setBendpointList(list);
    	}
    	
    	// add the bendpoint to the figure's constraint
  		if (_request.getIndex() == -1) {
  			_request.setIndex(list.size());
  		}

  		list.add(_request.getIndex(), bendpoint);
  		
  		_figure.getConnectionRouter().setConstraint(_figure, list);
  		_figure.anchorMoved(_figure.getSourceAnchor());
  		_figure.anchorMoved(_figure.getTargetAnchor());
  		_figure.repaint();
	}
	
	/**
	 * @see org.eclipse.gef.commands.Command#redo()
	 */
	public void redo() {
		//execute();
		if( _redoCommand != null )
			_redoCommand.undo( );
		else
			GreenException.warn( "Redo called on a bendpoint that had not been undone!" );
	}
	
	/**
	 * @see org.eclipse.gef.commands.Command#undo()
	 */
	public void undo() {
		BendpointRequest request = new BendpointRequest();
		
		request.setIndex(_request.getIndex());
		request.setLocation(_request.getLocation());
		request.setSource(_request.getSource());
		request.setType(REQ_MOVE_BENDPOINT);
		
		( _redoCommand = new DeleteBendpointCommand(request) ).execute();
	}
}
