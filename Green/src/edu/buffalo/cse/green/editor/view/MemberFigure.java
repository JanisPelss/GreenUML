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

import static edu.buffalo.cse.green.preferences.PreferenceInitializer.P_FONT;

import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.swt.graphics.Image;

import edu.buffalo.cse.green.PlugIn;

/**
 * The parent class of all figures that correspond to <code>MemberPart</code>
 * instances.
 * 
 * @author bcmartin
 */
public class MemberFigure extends Label implements IIconHolder {
	public MemberFigure() {
		PlugIn.getFontPreference(P_FONT, false);
	}
	
	/**
	 * @see edu.buffalo.cse.green.editor.view.IIconHolder#shouldDisposeFont()
	 */
	public boolean shouldDisposeFont() {
		return false;
	}
	
	/**
	 * @see org.eclipse.draw2d.Label#setIcon(org.eclipse.swt.graphics.Image)
	 */
	public void setIcon(Image icon) {
		Image oldIcon = getIcon();
		
		if (oldIcon != null) {
			oldIcon.dispose();
		}
		
		super.setIcon(icon);
	}
	
	/**
	 * @see edu.buffalo.cse.green.editor.view.IIconHolder#getActualSize()
	 */
	public Dimension getActualSize() {
		return getPreferredSize();
	}
}
