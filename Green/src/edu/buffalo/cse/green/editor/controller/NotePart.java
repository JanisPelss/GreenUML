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

import static edu.buffalo.cse.green.editor.controller.PropertyChange.Location;
import static edu.buffalo.cse.green.editor.controller.PropertyChange.Note;
import static edu.buffalo.cse.green.editor.controller.PropertyChange.Size;
import static edu.buffalo.cse.green.preferences.PreferenceInitializer.P_COLOR_NOTE;
import static edu.buffalo.cse.green.preferences.PreferenceInitializer.P_COLOR_NOTE_BORDER;
import static edu.buffalo.cse.green.preferences.PreferenceInitializer.P_COLOR_NOTE_TEXT;
import static edu.buffalo.cse.green.preferences.PreferenceInitializer.P_FONT;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.LineBorder;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.widgets.Display;

import edu.buffalo.cse.green.PlugIn;
import edu.buffalo.cse.green.editor.action.EditNoteAction;
import edu.buffalo.cse.green.editor.controller.policies.GreenSelectionEditPolicy;
import edu.buffalo.cse.green.editor.model.NoteModel;
import edu.buffalo.cse.green.editor.model.commands.DeleteCommand;
import edu.buffalo.cse.green.editor.model.commands.DeleteNoteCommand;
import edu.buffalo.cse.green.editor.view.INoteFigure;

/**
 * @author bcmartin
 * @author zgwang
 * 
 * The controller part corresponding to a <code>NoteModel</code>
 */
public class NotePart extends AbstractPart {
	/**
	 * @see edu.buffalo.cse.green.editor.controller.AbstractPart#doCreateFigure()
	 */
	protected IFigure doCreateFigure() {
		INoteFigure label = (INoteFigure) generateFigure();
		
		label.setLocation(model().getLocation());
		label.setSize(model().getSize());
		label.setText(model().getLabel());
		label.setFont(PlugIn.getFontPreference(P_FONT, false));
		label.setVisible(true);

		return label;
	}

	/**
	 * @see org.eclipse.gef.editparts.AbstractEditPart#refreshVisuals()
	 */
	protected void refreshVisuals() {
		updateFont();
		Point loc = ((NoteModel) getModel()).getLocation();
		Dimension size = ((NoteModel) getModel()).getSize();
		Rectangle r = new Rectangle(loc, size);
		figure().setText(((NoteModel) getModel()).getLabel());
		
		((GraphicalEditPart) getParent()).setLayoutConstraint(this,
				getFigure(), r);
	}

	/**
	 * @see org.eclipse.gef.editparts.AbstractEditPart#createEditPolicies()
	 */
	protected void createEditPolicies() {
		super.createEditPolicies();
		installEditPolicy(EditPolicy.SELECTION_FEEDBACK_ROLE,
				new GreenSelectionEditPolicy());
	}

	/**
	 * @see edu.buffalo.cse.green.editor.controller.AbstractPart#getDeleteCommand()
	 */
	public DeleteCommand getDeleteCommand() {
		return new DeleteNoteCommand((NoteModel) getModel());
	}

	/**
	 * @see edu.buffalo.cse.green.editor.controller.AbstractPart#addPropertyListeners()
	 */
	protected void addPropertyListeners() {
		addListener(Location, new VisualsUpdater());
		addListener(Note, new VisualsUpdater());
		addListener(Size, new VisualsUpdater());
	}
	
	/**
	 * @see edu.buffalo.cse.green.editor.controller.AbstractPart#onDoubleClick()
	 */
	protected void onDoubleClick() {
		EditNoteAction action = new EditNoteAction();
		action.setSelectionProvider(getEditor());
		action.run();
	}
	
	/**
	 * Auxiliary method; makes reading easier. 
	 */
	private INoteFigure figure() {
		return (INoteFigure) getFigure();
	}
	
	/**
	 * Auxiliary method; makes reading easier. 
	 */
	private NoteModel model() {
		return (NoteModel) getModel();
	}
	
	/**
	 * @see edu.buffalo.cse.green.editor.controller.AbstractPart#updateColors(org.eclipse.draw2d.IFigure)
	 */
	protected void updateColors(IFigure f) {
		f.setBorder(new LineBorder(
				PlugIn.getColorPreference(P_COLOR_NOTE_BORDER), 2));
		f.setBackgroundColor(PlugIn.getColorPreference(P_COLOR_NOTE));
		f.setForegroundColor(PlugIn.getColorPreference(P_COLOR_NOTE_TEXT));
	}
	
	/**
	 * @see edu.buffalo.cse.green.editor.controller.MemberPart#updateFont()
	 */
	public void updateFont() {
		if (Display.getCurrent() != null) {
			updateFontHelper();
		} else {
			Display.getDefault().asyncExec(new Runnable() {
				/**
				 * @see java.lang.Runnable#run()
				 */
				public void run() {
					updateFontHelper();
				}
			});
		}
	}
	
	/**
	 * Gets rid of the old font, installing a fresh one.
	 */
	private void updateFontHelper() {
		// get rid of the old font
		Font font = figure().getFont();
		font.dispose();
		
		// create the new font
		figure().setFont(PlugIn.getFontPreference(P_FONT, false));
	}
	
	/**
	 * @see edu.buffalo.cse.green.editor.controller.AbstractPart#setInitialBackgroundColor()
	 */
	public void setInitialBackgroundColor() {
		getFigure().setBackgroundColor(PlugIn.getColorPreference(P_COLOR_NOTE));
	}
}