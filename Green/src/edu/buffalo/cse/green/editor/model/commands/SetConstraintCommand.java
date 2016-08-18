/* This file is part of Green.
 *
 * Copyright (C) 2005 The Research Foundation of State University of New York
 * All Rights Under Copyright Reserved, The Research Foundation of S.U.N.Y.
 * 
 * Green is free software, licensed under the terms of the Eclipse
 * Public License, version 1.0.  The license is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package edu.buffalo.cse.green.editor.model.commands;

import static edu.buffalo.cse.green.preferences.PreferenceInitializer.P_GRID_SIZE;

import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.commands.Command;

import edu.buffalo.cse.green.GreenException;
import edu.buffalo.cse.green.PlugIn;
import edu.buffalo.cse.green.editor.model.AbstractModel;

/**
 * Sets the bounds on an <code>AbstractModel</code>.
 * 
 * @author hk47
 */
public class SetConstraintCommand extends Command {
	private Point _oldPos;

	private Point _newPos;

	private Dimension _oldSize;

	private Dimension _newSize;

	private AbstractModel _model;

	public SetConstraintCommand(AbstractModel model) {
		_model = model;
	}
	
	/**
	 * @see org.eclipse.gef.commands.Command#execute()
	 */
	public void execute() {
		_oldSize = _model.getSize();
		_oldPos = _model.getLocation();
		_model.setLocation(_newPos);
		_model.setSize(_newSize);
	}

	/**
	 * @see org.eclipse.gef.commands.Command#redo()
	 */
	public void redo() {
		execute();
	}

	/**
	 * Sets the constraint of the model.
	 * 
	 * @param r - The constraint.
	 */
	public void setBounds(Rectangle r) {
		String sGridSize = PlugIn.getPreference(P_GRID_SIZE);
		int gridSize = 1;
		
		try {
			gridSize = Integer.parseInt(sGridSize);
			if (gridSize < 1) gridSize = 1;
		} catch (NumberFormatException e) {
			GreenException.warn("Cannot parse grid size: " + sGridSize);
		}
		
		if (gridSize != 1) {
			int x = r.getLocation().x + gridSize / 2;
			x /= gridSize;

			int y = r.getLocation().y + gridSize / 2;
			y /= gridSize;
			
			if (!r.getSize().equals(new Dimension(-1, -1))) {
				int width = r.getSize().width + gridSize / 2;
				width /= gridSize;
				
				int height = r.getSize().height + gridSize / 2;
				height /= gridSize;
				_newSize = new Dimension(width * gridSize, height * gridSize);
			} else {
				_newSize = r.getSize();
			}
				
			_newPos = new Point(x * gridSize, y * gridSize);
		} else {
			_newPos = r.getLocation();
			_newSize = r.getSize();
		}
	}

	/**
	 * @see org.eclipse.gef.commands.Command#undo()
	 */
	public void undo() {
		_model.setSize(_oldSize);
		_model.setLocation(_oldPos);
	}
	
	/**
	 * @see org.eclipse.gef.commands.Command#canUndo()
	 */
	public boolean canUndo() {
		return true;
	}
}