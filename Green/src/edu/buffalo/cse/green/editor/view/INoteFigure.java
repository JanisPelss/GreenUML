/* This file is part of Green.
 *
 * Copyright (C) 2005 The Research Foundation of State University of New York
 * All Rights Under Copyright Reserved, The Research Foundation of S.U.N.Y.
 * 
 * Green is free software, licensed under the terms of the Eclipse
 * Public License, version 1.0.  The license is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package edu.buffalo.cse.green.editor.view;

import org.eclipse.draw2d.IFigure;

/**
 * The interface a note figure must implement to be used in the view.
 * 
 * @author bcmartin
 */
public interface INoteFigure extends IFigure {
	/**
	 * Sets the note's text to the given text.
	 * 
	 * @param text - The given text.
	 */
	public void setText(String text);
}
