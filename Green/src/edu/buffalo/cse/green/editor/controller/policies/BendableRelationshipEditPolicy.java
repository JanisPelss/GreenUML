/* This file is part of Green.
 *
 * Copyright (C) 2005 The Research Foundation of State University of New York
 * All Rights Under Copyright Reserved, The Research Foundation of S.U.N.Y.
 * 
 * Green is free software, licensed under the terms of the Eclipse
 * Public License, version 1.0.  The license is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package edu.buffalo.cse.green.editor.controller.policies;

import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editpolicies.BendpointEditPolicy;
import org.eclipse.gef.requests.BendpointRequest;

import edu.buffalo.cse.green.editor.model.commands.CreateBendpointCommand;
import edu.buffalo.cse.green.editor.model.commands.DeleteBendpointCommand;
import edu.buffalo.cse.green.editor.model.commands.MoveBendpointCommand;
import edu.buffalo.cse.green.editor.view.RelationshipFigure;

/**
 * Provides creation, deletion, and movement for bendpoints.
 * 
 * @author hk47
 */
public class BendableRelationshipEditPolicy extends BendpointEditPolicy {
	/**
	 * @see org.eclipse.gef.editpolicies.BendpointEditPolicy#getCreateBendpointCommand(org.eclipse.gef.requests.BendpointRequest)
	 */
	protected Command getCreateBendpointCommand(final BendpointRequest request) {
		RelationshipFigure rFigure =
			(RelationshipFigure) request.getSource().getFigure();
		return new CreateBendpointCommand(rFigure, request);
	}

	/**
	 * @see org.eclipse.gef.editpolicies.BendpointEditPolicy#getDeleteBendpointCommand(org.eclipse.gef.requests.BendpointRequest)
	 */
	protected Command getDeleteBendpointCommand(BendpointRequest request) {
		return new DeleteBendpointCommand(request);
	}

	/**
	 * @see org.eclipse.gef.editpolicies.BendpointEditPolicy#getMoveBendpointCommand(org.eclipse.gef.requests.BendpointRequest)
	 */
	protected Command getMoveBendpointCommand(BendpointRequest request) {
		return new MoveBendpointCommand(request);
	}
}