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
 * The interface a type figure must implement to be used in the view.
 * 
 * @author bcmartin
 */
public interface ITypeFigure extends IFigure {
	/**
	 * @return The label that holds the type's name.
	 */
	public IIconHolder getNameLabel();
	
	/**
	 * @return The label that holds relationship icons. Null is fine.
	 */
	public IIconHolder getRelLabel();
	
	/**
	 * @return true if the font should be disposed; false otherwise.
	 */
	public boolean shouldDisposeFont();
}
