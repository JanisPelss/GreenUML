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
import org.eclipse.gef.editpolicies.GraphicalNodeEditPolicy;
import org.eclipse.gef.requests.CreateConnectionRequest;
import org.eclipse.gef.requests.ReconnectRequest;

import edu.buffalo.cse.green.editor.controller.AbstractPart;
import edu.buffalo.cse.green.editor.controller.RelationshipPart;
import edu.buffalo.cse.green.editor.model.RelationshipModel;
import edu.buffalo.cse.green.editor.model.TypeModel;
import edu.buffalo.cse.green.editor.model.commands.ActivateSelectionToolCommand;

/**
 * Provides connections to the source and target <code>TypeModel</code>.
 * 
 * @author hk47
 */
public class RelationshipSourceAndTargetCreationEditPolicy extends
		GraphicalNodeEditPolicy {
	public RelationshipSourceAndTargetCreationEditPolicy() {
		super();
	}

	/**
	 * @see org.eclipse.gef.editpolicies.GraphicalNodeEditPolicy#getConnectionCreateCommand(org.eclipse.gef.requests.CreateConnectionRequest)
	 */
	protected Command getConnectionCreateCommand(CreateConnectionRequest request) {
		AbstractPart host = (AbstractPart) getHost();
		TypeModel typeModel = (TypeModel) host.getModel();

		RelationshipPart part = (RelationshipPart) request.getNewObject();
		part.setModel(new RelationshipModel());

		CreateRelationshipCommand cmd = new CreateRelationshipCommand(typeModel
				.getRootModel(), part);
		cmd.setRoot(typeModel.getRootModel());
		cmd.setSource(typeModel);
		request.setStartCommand(cmd);
		return cmd;
	}

	/**
	 * @see org.eclipse.gef.editpolicies.GraphicalNodeEditPolicy#getConnectionCompleteCommand(org.eclipse.gef.requests.CreateConnectionRequest)
	 */
	protected Command getConnectionCompleteCommand(
			CreateConnectionRequest request) {
		AbstractPart host = (AbstractPart) getHost();
		CreateRelationshipCommand cmd = (CreateRelationshipCommand) request
				.getStartCommand();
		cmd.setTarget((TypeModel) host.getModel());
		return new ActivateSelectionToolCommand(host.getRootPart().getEditor(),
				cmd);
	}

	/**
	 * @see org.eclipse.gef.editpolicies.GraphicalNodeEditPolicy#getReconnectSourceCommand(org.eclipse.gef.requests.ReconnectRequest)
	 */
	protected Command getReconnectSourceCommand(ReconnectRequest request) {
		return new Command() {};
	}

	/**
	 * @see org.eclipse.gef.editpolicies.GraphicalNodeEditPolicy#getReconnectTargetCommand(org.eclipse.gef.requests.ReconnectRequest)
	 */
	protected Command getReconnectTargetCommand(ReconnectRequest request) {
		return new Command() {};
	}
}