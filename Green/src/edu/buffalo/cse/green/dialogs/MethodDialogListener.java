/* This file is part of Green.
 *
 * Copyright (C) 2005 The Research Foundation of State University of New York
 * All Rights Under Copyright Reserved, The Research Foundation of S.U.N.Y.
 * 
 * Green is free software, licensed under the terms of the Eclipse
 * Public License, version 1.0.  The license is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package edu.buffalo.cse.green.dialogs;

import java.util.List;

import org.eclipse.jdt.core.IMethod;

/**
 * Notifies the user when the OK button is pressed in the
 * <code>ChooseMethodsDialog</code>.
 * 
 * @author bcmartin
 */
public interface MethodDialogListener {
	/**
	 * Called when the OK button is pressed.
	 * 
	 * @param methods - The list of methods selected in the dialog.
	 */
	void okPressed(List<IMethod> methods);
}
