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

import static edu.buffalo.cse.green.preferences.PreferenceInitializer.P_FIXED_HEIGHT;
import static edu.buffalo.cse.green.preferences.PreferenceInitializer.P_FIXED_WIDTH;

import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.jdt.core.JavaModelException;

import edu.buffalo.cse.green.GreenException;
import edu.buffalo.cse.green.PlugIn;
import edu.buffalo.cse.green.editor.model.commands.SetConstraintCommand;

/**
 * Sets a <code>TypeModel</code>'s size to the specified dimensions.
 * 
 * @author Blake
 */
public class UseSizeAction extends ContextAction {
	public UseSizeAction() {
		super(null);
		setAccelerator(EDITOR_CHANGE_SIZE);
	}
	
	/**
	 * @see edu.buffalo.cse.green.editor.action.ContextAction#getLabel()
	 */
	public String getLabel() {
		if (isResized()) {
			return "Set to default size";
		} else {
			return "Set to fixed size";
		}
	}

	/**
	 * @see edu.buffalo.cse.green.editor.action.ContextAction#doRun()
	 */
	protected void doRun() throws JavaModelException {
		String sWidth = PlugIn.getPreference(P_FIXED_WIDTH);
		String sHeight = PlugIn.getPreference(P_FIXED_HEIGHT);
		int height = -1;
		int width = -1;
		
		if (isResized()) {
			SetConstraintCommand command = new SetConstraintCommand(_model);
			command.setBounds(new Rectangle(_model.getLocation(),
					new Dimension(-1, -1)));

			getEditor().execute(command);
			_model.forceRefesh();
		} else {
			try {
				width = Integer.parseInt(sWidth);

				if (width < -1) {
					width = -1;
				}
			} catch (NumberFormatException e) {
				GreenException.warn("Cannot parse fixed size: " + sWidth);
			}

			try {
				height = Integer.parseInt(sHeight);

				if (height < -1) {
					height = -1;
				}
			} catch (NumberFormatException e) {
				GreenException.warn("Cannot parse fixed size: " + sWidth);
			}

			SetConstraintCommand command = new SetConstraintCommand(_model);
			command.setBounds(new Rectangle(_model.getLocation(),
					new Dimension(width, height)));
			getEditor().execute(command);
		}
	}

	/**
	 * @see edu.buffalo.cse.green.editor.action.ContextAction#getSupportedModels()
	 */
	protected int getSupportedModels() {
		return CM_TYPE;
	}

	/**
	 * @see edu.buffalo.cse.green.editor.action.ContextAction#getPath()
	 */
	public Submenu getPath() {
		return Submenu.None;
	}

	/**
	 * @see edu.buffalo.cse.green.editor.action.ContextAction#isEnabled()
	 */
	public boolean isEnabled() {
		return true;
	}

	/**
	 * @return true if the model isn't using the default sizing, false otherwise.
	 */
	private boolean isResized() {
		return !_model.getSize().equals(new Dimension(-1, -1));
	}
}
