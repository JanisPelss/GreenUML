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
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;

/**
 * An interface to hold properties of a label with a settable font, icon, and
 * text.
 * 
 * @author bcmartin
 */
public interface IIconHolder extends IFigure {
	/**
	 * @return The figure's icon.
	 */
	public Image getIcon();
	
	/**
	 * @return The figure's text.
	 */
	public String getText();
	
	/**
	 * Sets the figure's icon.
	 * 
	 * @param icon - The given icon.
	 */
	public void setIcon(Image icon);
	
	/**
	 * Sets the figure's text.
	 * 
	 * @param displayName - The text to use.
	 */
	public void setText(String displayName);
	
	/**
	 * @return The true size of this figure.
	 */
	public Dimension getActualSize();
	
	/**
	 * @return true if the font should be disposed; false otherwise.
	 */
	public boolean shouldDisposeFont();
	
	/**
	 * @see org.eclipse.draw2d.IFigure#getFont()
	 */
	public Font getFont();
	
	/**
	 * @see org.eclipse.draw2d.IFigure#setFont(org.eclipse.swt.graphics.Font)
	 */
	public void setFont(Font font);
}
