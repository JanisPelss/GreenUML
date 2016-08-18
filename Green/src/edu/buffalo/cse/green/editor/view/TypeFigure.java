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

import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.ToolbarLayout;

/**
 * The figure that corresponds to a <code>TypePart</code> in the editor.
 * 
 * @author bcmartin
 */
public class TypeFigure extends Figure implements ITypeFigure {
	private IIconHolder _nameLabel;
	private IIconHolder _relLabel;
	
	public TypeFigure() {
		ToolbarLayout layout = new ToolbarLayout();
		setOpaque(true);
		setLayoutManager(layout);
		
		_nameLabel = new MemberFigure();
		_nameLabel.setOpaque(true);

		_relLabel = new MemberFigure();
		_relLabel.setOpaque(true);
	}

	/**
	 * @see edu.buffalo.cse.green.editor.view.ITypeFigure#getNameLabel()
	 */
	public IIconHolder getNameLabel() {
		return _nameLabel;
	}
	
	/**
	 * @see edu.buffalo.cse.green.editor.view.ITypeFigure#shouldDisposeFont()
	 */
	public boolean shouldDisposeFont() {
		return true;
	}

	/**
	 * @see edu.buffalo.cse.green.editor.view.ITypeFigure#getRelLabel()
	 */
	public IIconHolder getRelLabel() {
		return _relLabel;
	}
}
