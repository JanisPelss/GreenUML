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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.draw2d.Bendpoint;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.requests.BendpointRequest;

import edu.buffalo.cse.green.editor.view.RelationshipFigure;

/**
 * Deletes a bendpoint.
 * 
 * @author bcmartin
 */
public class DeleteBendpointCommand extends Command {
	private BendpointRequest _request;
	private Bendpoint _bp;

	public DeleteBendpointCommand(BendpointRequest request) {
		_request = request;
	}
	
	/**
	 * @see org.eclipse.gef.commands.Command#execute()
	 */
	public void execute() {
		RelationshipFigure rFigure =
			(RelationshipFigure) _request.getSource().getFigure();
		
		// retrieve the list of bendpoints
    	List constraint =
    		(List) rFigure.getConnectionRouter().getConstraint(rFigure);
    	List<Bendpoint> list = (ArrayList<Bendpoint>) constraint;
    	if (list == null) return;

    	// remove the desired bendpoint
    	_bp = (Bendpoint) list.remove(_request.getIndex());
    	
    	// set the constraint of the relationship
    	rFigure.getConnectionRouter().setConstraint(
    			rFigure, list);
    	rFigure.getConnectionRouter().route(rFigure);
  		rFigure.anchorMoved(rFigure.getSourceAnchor());
  		rFigure.anchorMoved(rFigure.getTargetAnchor());
    	rFigure.repaint();
	}
	
	/**
	 * @see org.eclipse.gef.commands.Command#redo()
	 */
	public void redo() {
		execute();
	}
	
	/**
	 * @see org.eclipse.gef.commands.Command#undo()
	 */
	public void undo() {
		RelationshipFigure rFigure =
			(RelationshipFigure) _request.getSource().getFigure();
		
		// retrieve the list of bendpoints
    	List constraint =
    		(List) rFigure.getConnectionRouter().getConstraint(rFigure);
    	List<Bendpoint> list = (ArrayList<Bendpoint>) constraint;
    	// add in the removed bendpoint
    	list.add(_request.getIndex( ), _bp);
    	
    	// set the constraint of the relationship
    	rFigure.getConnectionRouter().setConstraint(
    			rFigure, list);
    	rFigure.getConnectionRouter().route(rFigure);
  		rFigure.anchorMoved(rFigure.getSourceAnchor());
  		rFigure.anchorMoved(rFigure.getTargetAnchor());
    	rFigure.repaint();
	}
}