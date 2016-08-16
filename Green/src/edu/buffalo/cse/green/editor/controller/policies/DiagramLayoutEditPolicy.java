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

import org.eclipse.draw2d.XYLayout;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.Request;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editpolicies.XYLayoutEditPolicy;
import org.eclipse.gef.requests.CreateRequest;

import edu.buffalo.cse.green.editor.controller.AbstractPart;
import edu.buffalo.cse.green.editor.model.AbstractModel;
import edu.buffalo.cse.green.editor.model.RootModel;
import edu.buffalo.cse.green.editor.model.commands.CreateCommand;
import edu.buffalo.cse.green.editor.model.commands.SetConstraintCommand;

/**
 * Handles creation and constraing commands for the editor.
 * 
 * @author hk47
 */
public class DiagramLayoutEditPolicy extends XYLayoutEditPolicy {
	/**
	 * Holds the part that can potentially be deleted.
	 */
	private AbstractPart _deleteListener;

	public DiagramLayoutEditPolicy(XYLayout layout) {
		super();
		setXyLayout(layout);
	}

	/**
	 * @see org.eclipse.gef.editpolicies.ConstrainedLayoutEditPolicy#createAddCommand(org.eclipse.gef.EditPart, java.lang.Object)
	 */
	protected Command createAddCommand(EditPart child, Object constraint) {
		return null;
	}

	/**
	 * @see org.eclipse.gef.editpolicies.ConstrainedLayoutEditPolicy#createChangeConstraintCommand(org.eclipse.gef.EditPart, java.lang.Object)
	 */
	protected Command createChangeConstraintCommand(
			EditPart child,
			Object constraint) {
		SetConstraintCommand locationCommand =
			new SetConstraintCommand((AbstractModel) child.getModel());
		locationCommand.setBounds((Rectangle) constraint);

		return locationCommand;
	}

	/**
	 * @see org.eclipse.gef.editpolicies.LayoutEditPolicy#getCreateCommand(org.eclipse.gef.requests.CreateRequest)
	 */
	protected Command getCreateCommand(CreateRequest request) {
		AbstractModel<?, RootModel, ?> nm =
			(AbstractModel<?, RootModel, ?>) request.getNewObject();
		CreateCommand command = new CreateCommand(
				(RootModel) getHost().getModel());

		command.setChild(nm);

		Rectangle constraint = (Rectangle) getConstraintFor(request);
		command.setLocation(constraint.getLocation());

		if (request.getSize() != null) {
			command.setSize(constraint.getSize());
		}

		return command;
	}

	/**
	 * @see org.eclipse.gef.editpolicies.LayoutEditPolicy#createChildEditPolicy(org.eclipse.gef.EditPart)
	 */
	protected EditPolicy createChildEditPolicy(EditPart child) {
		AbstractPart part = (AbstractPart) child;
		return part.generateResizableEditPolicy();
	}

	/**
	 * @see org.eclipse.gef.editpolicies.LayoutEditPolicy#getDeleteDependantCommand(org.eclipse.gef.Request)
	 */
	protected Command getDeleteDependantCommand(Request request) {
		Command dc = _deleteListener.getDeleteCommand();
		return dc;
	}
}