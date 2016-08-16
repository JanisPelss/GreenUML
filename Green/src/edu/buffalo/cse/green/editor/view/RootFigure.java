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

import org.eclipse.draw2d.FreeformLayer;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Rectangle;

/**
 * The root figure for the editor. 
 * 
 * @author zgwang
 */
public class RootFigure extends FreeformLayer {

	/**
	 * @see org.eclipse.draw2d.Figure#getClientArea()
	 */
	public Rectangle getClientArea(Rectangle rect) {
		super.getClientArea(rect);
		return rect;
	}

	/**
	 * @see org.eclipse.draw2d.Figure#getPreferredSize(int, int)
	 */
	public Dimension getPreferredSize(int wHint, int hHint) {
		Dimension d = super.getPreferredSize(wHint, hHint);
		return d;
	}
	
	public void paint(Graphics g) {
		//g.setAntialias(ON);
		super.paint( g );
	}

	/**
	 * @see org.eclipse.draw2d.Figure#paintClientArea(Graphics)
	 */
	protected void paintClientArea(Graphics graphics) {
		if (getChildren().isEmpty())
			return;
		paintChildren(graphics);
	}



	/**
	 * @see org.eclipse.draw2d.Figure#useLocalCoordinates()
	 */
	protected boolean useLocalCoordinates() {
		return false;
	}
	
	
	/**
	 * Forces the editor to update.  Mainly to recalculate the scroll bars
	 */
	public void updateEditor() {
		fireFigureMoved();
	}
}
