/* This file is part of Green.
 *
 * Copyright (C) 2005 The Research Foundation of State University of New York
 * All Rights Under Copyright Reserved, The Research Foundation of S.U.N.Y.
 * 
 * Green is free software, licensed under the terms of the Eclipse
 * Public License, version 1.0.  The license is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package edu.buffalo.cse.green.editor;

import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jface.viewers.IStructuredSelection;

import edu.buffalo.cse.green.editor.controller.AbstractPart;
import edu.buffalo.cse.green.editor.model.AbstractModel;

/**
 * A convenient way to store information from the current selection. This
 * class prevents a lot of unnecessary typecasting in other code.
 * 
 * @author bcmartin
 */
public class Context {
	/**
	 * The <code>IJavaElement</code> corresponding to the current selection
	 */
	private IJavaElement _element;

	/**
	 * The <code>AbstractModel</code> corresponding to the current selection
	 */
	private AbstractModel _model;

	/**
	 * The <code>AbstractPart</code> corresponding to the current selection
	 */
	private AbstractPart _part;

	public Context(IStructuredSelection selection) {
		_part = (AbstractPart) selection.getFirstElement();
		_model = (AbstractModel<?, ?, IJavaElement>) _part.getModel();
		_element = _model.getJavaElement();
		}

	/**
	 * @return the currently selected element
	 */
	public IJavaElement getElement() {
		return _element;
	}

	/**
	 * @return the currently selected model
	 */
	public AbstractModel getModel() {
		return _model;
	}

	/**
	 * @return the currently selected part
	 */
	public AbstractPart getPart() {
		return _part;
	}
}
