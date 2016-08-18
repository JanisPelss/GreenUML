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
import org.eclipse.gef.editpolicies.ComponentEditPolicy;
import org.eclipse.gef.requests.GroupRequest;

import edu.buffalo.cse.green.editor.controller.AbstractPart;

/**
 * Provides deletion for parts.
 * 
 * @author hk47
 */
public class DeleteEditPolicy extends ComponentEditPolicy {
	/**
	 * Holds the part that will be deleted.
	 */
	private AbstractPart _deleteable;

	public DeleteEditPolicy(AbstractPart deleteable) {
		_deleteable = deleteable;
	}

	/**
	 * @see org.eclipse.gef.editpolicies.ComponentEditPolicy#createDeleteCommand(org.eclipse.gef.requests.GroupRequest)
	 */
	protected Command createDeleteCommand(GroupRequest deleteRequest) {
		return _deleteable.getDeleteCommand();
	}
}