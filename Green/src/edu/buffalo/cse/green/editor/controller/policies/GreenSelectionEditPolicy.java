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

import org.eclipse.gef.editpolicies.SelectionEditPolicy;
import org.eclipse.jface.viewers.StructuredSelection;

import edu.buffalo.cse.green.editor.controller.AbstractPart;

/**
 * Provides selection information to the context menu actions in the editor for
 * <code>IMember</code> selection.
 * 
 * @author bcmartin
 */
public class GreenSelectionEditPolicy extends SelectionEditPolicy {
	/**
	 * @see org.eclipse.gef.editpolicies.SelectionEditPolicy#hideSelection()
	 */
	protected void hideSelection() {
		// do nothing
	}

	/**
	 * @see org.eclipse.gef.editpolicies.SelectionEditPolicy#showSelection()
	 */
	protected void showSelection() {
		AbstractPart editPart = (AbstractPart) getHost();
		editPart.getEditor().setSelection(new StructuredSelection(editPart));
	}
}