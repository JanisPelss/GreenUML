/* This file is part of Green.
 *
 * Copyright (C) 2005 The Research Foundation of State University of New York
 * All Rights Under Copyright Reserved, The Research Foundation of S.U.N.Y.
 * 
 * Green is free software, licensed under the terms of the Eclipse
 * Public License, version 1.0.  The license is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package edu.buffalo.cse.green.dialogs.wizards;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.wizard.Wizard;

import edu.buffalo.cse.green.GreenException;

/**
 * Wrapper for Wizards in Green; catches exceptions and displays error messages.
 * 
 * @author bcmartin
 */
public abstract class GreenWizard extends Wizard {
	public static final String ERROR_TITLE = "Error";
	private boolean _cancelled = false; 
	
	/**
	 * @see org.eclipse.jface.wizard.IWizard#performFinish()
	 */
	public final boolean performFinish() {
		if (_cancelled) return true;
		
		try {
			return doFinish();
		} catch (GreenException e) {
			handleFinishException(e);
			return false;
		}
	}

	/**
	 * @see org.eclipse.jface.wizard.IWizard#performCancel()
	 */
	public boolean performCancel() {
		_cancelled = true;
		
		return super.performCancel();
	}
	
	/**
	 * Handles exceptions that occur when dialogs are closing.
	 * 
	 * @param e - The exception.
	 */
	protected void handleFinishException(Exception e) {
		if (e instanceof InvocationTargetException) {
			InvocationTargetException ie = (InvocationTargetException) e;
			e = (Exception) ie.getCause();
		}
		
		MessageDialog.openError(getShell(), ERROR_TITLE,
				e.getMessage());
	}
	
	/**
	 * Called to perform finish events on dialogs.
	 * 
	 * @return True if successful, false otherwise.
	 */
	protected abstract boolean doFinish();
}
