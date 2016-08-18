/* This file is part of Green.
 *
 * Copyright (C) 2005 The Research Foundation of State University of New York
 * All Rights Under Copyright Reserved, The Research Foundation of S.U.N.Y.
 * 
 * Green is free software, licensed under the terms of the Eclipse
 * Public License, version 1.0.  The license is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package edu.buffalo.cse.green.editor.controller;

import static edu.buffalo.cse.green.preferences.PreferenceInitializer.P_COLOR_COMPARTMENT_BORDER;
import static edu.buffalo.cse.green.preferences.PreferenceInitializer.P_DISPLAY_INCREMENTAL_EXPLORER_DIA;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.LineBorder;
import org.eclipse.draw2d.ToolbarLayout;
import org.eclipse.gef.DragTracker;
import org.eclipse.gef.Request;

import edu.buffalo.cse.green.PlugIn;
import edu.buffalo.cse.green.editor.model.CompartmentModel;
import edu.buffalo.cse.green.editor.model.commands.DeleteCommand;
import edu.buffalo.cse.green.editor.view.CompartmentFigure;

/**
 * Creates a compartment with a maximum height/width used to store
 * <code>IJavaElement</code>s in a class box.
 * 
 * @author bcmartin
 * @author evertwoo
 * @author rjtruban
 */
public class CompartmentPart extends AbstractPart {
	/**
	 * @see edu.buffalo.cse.green.editor.controller.AbstractPart#doCreateFigure()
	 */
	protected IFigure doCreateFigure() {
		// create the figure and set its layout policy
		CompartmentFigure cFigure = new CompartmentFigure();
		ToolbarLayout layout = (ToolbarLayout) cFigure.getLayoutManager();
		layout.setMinorAlignment(model().getLayout());

		// add in extraneous labels
		model().dispatchLabels(this, cFigure);

		return cFigure;
	}

	/**
	 * Auxiliary method; makes reading easier. 
	 */
	private CompartmentModel model() {
		return (CompartmentModel) getModel();
	}
	
	/**
	 * @see org.eclipse.gef.editparts.AbstractEditPart#createEditPolicies()
	 */
	protected void createEditPolicies() {
		super.createEditPolicies();
	}

	/**
	 * @see edu.buffalo.cse.green.editor.controller.AbstractPart#getDeleteCommand()
	 */
	public DeleteCommand getDeleteCommand() {
		return null;
	}

	/**
	 * @see org.eclipse.gef.EditPart#getDragTracker(org.eclipse.gef.Request)
	 */
	public DragTracker getDragTracker(Request request) {
		return getParent().getDragTracker(request);
	}

	/**
	 * @see edu.buffalo.cse.green.editor.controller.AbstractPart#onDoubleClick()
	 */
	protected void onDoubleClick() {
		AbstractPart parentEditPart = (AbstractPart) getParent();
		parentEditPart.onDoubleClick();
	}
	
	/**
	 * @see edu.buffalo.cse.green.editor.controller.AbstractPart#updateColors(org.eclipse.draw2d.IFigure)
	 */
	protected void updateColors(IFigure f) {
		f.setBorder(new LineBorder(PlugIn.getColorPreference(
				P_COLOR_COMPARTMENT_BORDER), 1));
	}
	
	/**
	 * @see org.eclipse.gef.editparts.AbstractEditPart#refreshVisuals()
	 */
	protected void refreshVisuals() {
		model().updateLabels(PlugIn.getBooleanPreference(P_DISPLAY_INCREMENTAL_EXPLORER_DIA));
	}

	/**
	 * @see edu.buffalo.cse.green.editor.controller.AbstractPart#setInitialBackgroundColor()
	 */
	public void setInitialBackgroundColor() {
		// not necessary
	}

	/**
	 * @see edu.buffalo.cse.green.editor.controller.AbstractPart#addPropertyListeners()
	 */
	protected void addPropertyListeners() {
		// do nothing
	}
}