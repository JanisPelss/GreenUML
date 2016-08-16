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

import org.eclipse.draw2d.AbstractBorder;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Panel;
import org.eclipse.draw2d.ToolbarLayout;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Insets;

/**
 * A figure that holds a list of labels.
 * 
 * @author hk47
 */
public class CompartmentFigure extends Panel {
	private int _height = 0;
	
	public CompartmentFigure() {
		ToolbarLayout layout = new ToolbarLayout();
		layout.setMinorAlignment(ToolbarLayout.ALIGN_CENTER);
		layout.setStretchMinorAxis(false);
		layout.setVertical(true);
		layout.setSpacing(2);
		setLayoutManager(layout);
		setMinimumSize(new Dimension(0, 16));
	}

	/**
	 * @see org.eclipse.draw2d.IFigure#getPreferredSize(int, int)
	 */
	public Dimension getPreferredSize(int wHint, int hHint) {
		Dimension cPrefSize = super.getPreferredSize(wHint, hHint);
		Dimension cMinSize = getMinimumSize(wHint, hHint);
		int height = _height;
		
		if (height == 0) {
			height = Math.max(cPrefSize.height, cMinSize.height);
		}
		
		return new Dimension(Math.max(cPrefSize.width, cMinSize.width), height);
	}

	/**
	 * A special figure border for compartments.
	 * 
	 * @author bcmartin
	 */
	public class CompartmentFigureBorder extends AbstractBorder {
		/**
		 * @see org.eclipse.draw2d.Border#getInsets(org.eclipse.draw2d.IFigure)
		 */
		public Insets getInsets(IFigure figure) {
			return new Insets(1, 0, 0, 0);
		}

		/**
		 * @see org.eclipse.draw2d.Border#paint(org.eclipse.draw2d.IFigure, org.eclipse.draw2d.Graphics, org.eclipse.draw2d.geometry.Insets)
		 */
		public void paint(IFigure figure, Graphics graphics, Insets insets) {

			graphics.drawLine(getPaintRectangle(figure, insets).getTopLeft(),
					tempRect.getTopRight());
		}

	}

	/**
	 * Sets the preferred height of the compartment.
	 * 
	 * @param prefHeight - The height.
	 */
	public void setPreferredHeight(int prefHeight) {
		_height = prefHeight;
	}
}