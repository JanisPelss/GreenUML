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

import static edu.buffalo.cse.green.preferences.PreferenceInitializer.P_COLOR_TYPE_TEXT;
import static edu.buffalo.cse.green.preferences.PreferenceInitializer.P_FONT;

import org.eclipse.draw2d.IFigure;
import org.eclipse.gef.EditPolicy;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.widgets.Display;

import edu.buffalo.cse.green.PlugIn;
import edu.buffalo.cse.green.editor.controller.policies.GreenSelectionEditPolicy;
import edu.buffalo.cse.green.editor.model.AbstractModel;
import edu.buffalo.cse.green.editor.model.MethodModel;
import edu.buffalo.cse.green.editor.model.TypeModel;
import edu.buffalo.cse.green.editor.model.commands.DeleteCommand;
import edu.buffalo.cse.green.editor.model.commands.DeleteMethodCommand;
import edu.buffalo.cse.green.editor.view.IIconHolder;

/**
 * The controller part that corresponds to a <code>MethodModel</code>.
 * 
 * @author bcmartin
 */
public class MethodPart extends MemberPart {
	/**
	 * @see edu.buffalo.cse.green.editor.controller.AbstractPart#doCreateFigure()
	 */
	protected IFigure doCreateFigure() {
		IIconHolder f = (IIconHolder) generateFigure();
		f.setText(model().getDisplayName());
		f.setIcon(model().getIcon());
		f.setFont(PlugIn.getFontPreference(P_FONT, false));

		// map the model to its appropriate type EditPart
		RootPart root = (RootPart) getRootPart();
		root.mapModelToEditPart((AbstractModel) getModel(), this);
		f.setOpaque(true);

		return f;
	}

	/**
	 * @see edu.buffalo.cse.green.editor.controller.MemberPart#getLabel()
	 */
	protected IIconHolder getNameLabel() {
		return figure();
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
	 * @see org.eclipse.gef.editparts.AbstractEditPart#refreshVisuals()
	 */
	protected void refreshVisuals() {
		super.refreshVisuals(figure());
		if (figure() != null) figure().setText(model().getDisplayName());
	}

	/**
	 * @see edu.buffalo.cse.green.editor.controller.AbstractPart#getDeleteCommand()
	 */
	public DeleteCommand getDeleteCommand() {
		return new DeleteMethodCommand((MethodModel) getModel());
	}

	/**
	 * @see edu.buffalo.cse.green.editor.controller.FieldPart#updateIcon()
	 */
	public void updateIcon() {
		super.updateIcon(figure());
	}

	/**
	 * @see edu.buffalo.cse.green.editor.controller.AbstractPart#onDoubleClick()
	 */
	protected void onDoubleClick() {
		getOpenElementAction().run();
	}

	/**
	 * Updates the font used in the label. 
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
	 * edu.buffalo.cse.green.editor.controller.MethodPart#updateFont(final MethodModel model)
	 */
	private void updateFontHelper() {
		try {
			// get rid of the old font
			Font font = getFigure().getFont();
			font.dispose();
			
			// create the new font
			TypeModel typeModel = (TypeModel) model().getParent().getParent();
			boolean italic = (model().isAbstract() && !typeModel.isInterface());
			getFigure().setFont(PlugIn.getFontPreference(P_FONT, italic));
		} catch (JavaModelException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Auxiliary method; makes reading easier. 
	 */
	private MethodModel model() {
		return (MethodModel) getModel();
	}
	
	/**
	 * Auxiliary method; makes reading easier. 
	 */
	private IIconHolder figure() {
		return (IIconHolder) getFigure();
	}
	
	/**
	 * @see edu.buffalo.cse.green.editor.controller.AbstractPart#updateColors(org.eclipse.draw2d.IFigure)
	 */
	protected void updateColors(IFigure f) {
		f.setForegroundColor(PlugIn.getColorPreference(P_COLOR_TYPE_TEXT));
	}
	
	/**
	 * @see edu.buffalo.cse.green.editor.controller.MemberPart#updateLabel()
	 */
	public void updateLabel() {
		repaintLabel(figure());
	}

	/**
	 * @see edu.buffalo.cse.green.editor.controller.MemberPart#getJavadocGrabber()
	 */
	protected JavadocGrabber getJavadocGrabber() {
		return new JavadocGrabber() {
			private boolean _alive = true;

			/**
			 * @see org.eclipse.jdt.core.dom.ASTVisitor#endVisit(org.eclipse.jdt.core.dom.FieldDeclaration)
			 */
			public boolean visit(MethodDeclaration node) {
				if (!_alive) return false;
				
				if (compareElements(node.resolveBinding().getJavaElement())) {
					_alive = false;
					_doc = getDoc(node.getJavadoc());
				}
				
				return _alive;
			}
		};
	}
}