/* This file is part of Green.
 *
 * Copyright (C) 2005 The Research Foundation of State University of New York
 * All Rights Under Copyright Reserved, The Research Foundation of S.U.N.Y.
 * 
 * Green is free software, licensed under the terms of the Eclipse
 * Public License, version 1.0.  The license is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package edu.buffalo.cse.green.editor.action;

import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.resource.ImageDescriptor;

/**
 * Context action for Quick Fixes.  QFs are put into their own
 * submenu.
 * 
 * @author unknown
 * @author <a href="mailto:zgwang@sourceforge.net">Gene Wang</a>
 *
 */
public class QuickFixAction extends ContextAction {
	private QuickFix _fix;

	public QuickFixAction(QuickFix fix) {
		super(null);
		_fix = fix;
		setText(getLabel());
	}

	/**
	 * @see edu.buffalo.cse.green.editor.action.ContextAction#getLabel()
	 */
	public String getLabel() {
		if (_fix == null) return "";
		
		return trimNonAlphaNumeric(_fix.getResolution().getLabel());
	}

	private String trimNonAlphaNumeric(String input) {
		char[] chars = input.toCharArray();
		StringBuffer buf = new StringBuffer();
		
		for (Character ch : chars) {
			if (Character.isLetterOrDigit(ch) || ch == ' ') {
				buf.append(ch);
			}
		}
		
		return new String(buf.toString());
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.action.Action#getImageDescriptor()
	 */
	public ImageDescriptor getImageDescriptor() {
		//TODO Ideally, this method would return the same image descriptors that the JavaEditor
		//hover menu gives for QuickFixes
//		_fix.getResolution().
		return null; 
	}
	
	/**
	 * @see edu.buffalo.cse.green.editor.action.ContextAction#doRun()
	 */
	protected void doRun() throws JavaModelException {
		_fix.getResolution().run(_fix.getMarker());
	}

	/**
	 * @see edu.buffalo.cse.green.editor.action.ContextAction#getSupportedModels()
	 */
	protected int getSupportedModels() {
		return CM_MEMBER;
	}

	/**
	 * @see edu.buffalo.cse.green.editor.action.ContextAction#isEnabled()
	 */
	public boolean isEnabled() {
		return true;
	}

	/**
	 * @see edu.buffalo.cse.green.editor.action.ContextAction#getPath()
	 */
	public Submenu getPath() {
		return Submenu.QuickFix;
	}
}
