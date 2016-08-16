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

import static edu.buffalo.cse.green.editor.controller.PropertyChange.IncomingRelationship;
import static edu.buffalo.cse.green.editor.controller.PropertyChange.OutgoingRelationship;
import static edu.buffalo.cse.green.preferences.PreferenceInitializer.P_COLOR_SELECTED;
import static edu.buffalo.cse.green.preferences.PreferenceInitializer.P_COLOR_TYPE_BORDER;
import static edu.buffalo.cse.green.preferences.PreferenceInitializer.P_COLOR_TYPE_BORDER_HIDDENR;
import static edu.buffalo.cse.green.preferences.PreferenceInitializer.P_COLOR_TYPE_TEXT;
import static edu.buffalo.cse.green.preferences.PreferenceInitializer.P_COLOR_UML;
import static edu.buffalo.cse.green.preferences.PreferenceInitializer.P_FONT;

import org.eclipse.draw2d.ChopboxAnchor;
import org.eclipse.draw2d.ConnectionAnchor;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.LineBorder;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.ConnectionEditPart;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gef.NodeEditPart;
import org.eclipse.gef.Request;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.AbstractTypeDeclaration;
import org.eclipse.jdt.core.dom.EnumDeclaration;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;

import edu.buffalo.cse.green.PlugIn;
import edu.buffalo.cse.green.editor.controller.policies.GreenSelectionEditPolicy;
import edu.buffalo.cse.green.editor.controller.policies.RelationshipSourceAndTargetCreationEditPolicy;
import edu.buffalo.cse.green.editor.model.TypeModel;
import edu.buffalo.cse.green.editor.model.commands.DeleteCommand;
import edu.buffalo.cse.green.editor.model.commands.DeleteTypeCommand;
import edu.buffalo.cse.green.editor.view.IIconHolder;
import edu.buffalo.cse.green.editor.view.ITypeFigure;

/**
 * @author bcmartin
 * 
 * Controller part for a UML type box. It acts as a node for relationship
 * connections by providing anchors as appropriate. It also handles the updating
 * of information regarding the type.
 */
public class TypePart extends MemberPart implements NodeEditPart {
	/**
	 * @see edu.buffalo.cse.green.editor.controller.AbstractPart#doCreateFigure()
	 */
	protected IFigure doCreateFigure() {
		ITypeFigure f = (ITypeFigure) generateFigure();
		f.setLocation(model().getLocation());
		f.setSize(model().getSize());
		f.getNameLabel().setFont(
				PlugIn.getFontPreference(P_FONT, false));
		
		return f;
	}
	
	/**
	 * @see org.eclipse.gef.editparts.AbstractEditPart#refreshVisuals()
	 */
	protected void refreshVisuals() {
		super.refreshVisuals(getNameLabel());
		GraphicalEditPart parent = (GraphicalEditPart) getParent();
		
		parent.setLayoutConstraint(this, figure(),
				new Rectangle(model().getLocation(), model().getSize()));
		figure().validate();
	}

	/**
	 * @see edu.buffalo.cse.green.editor.controller.MemberPart#updateIcon()
	 */
	public void updateIcon() {
		super.updateIcon(getNameLabel());
	}
	
	/**
	 * @see edu.buffalo.cse.green.editor.controller.MemberPart#updateLabel()
	 */
	public void updateLabel() {
		repaintLabel(getNameLabel());
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
	 * Disposes the current font and creates a new one that's up to date.
	 */
	private void updateFontHelper() {
		try {
			// get rid of the old font
			Font font = getNameLabel().getFont();
			font.dispose();
			
			boolean italic = model().isAbstract() || model().isInterface();
			
			// create the new font
			getNameLabel().setFont(PlugIn.getFontPreference(P_FONT, italic));
		} catch (JavaModelException e) {
			e.printStackTrace();
		}
	}

	/**
	 * @see edu.buffalo.cse.green.editor.controller.AbstractPart#getDeleteCommand()
	 */
	public DeleteCommand getDeleteCommand() {
		return new DeleteTypeCommand(model());
	}

	protected void addPropertyListeners() {
		super.addPropertyListeners();
		
		addListener(IncomingRelationship, new ChildAndVisualsUpdater());
		addListener(OutgoingRelationship, new ChildAndVisualsUpdater());
	}

	/**
	 * @see edu.buffalo.cse.green.editor.controller.AbstractPart#onDoubleClick()
	 */
	protected void onDoubleClick() {
		getOpenElementAction().run();
	}

	/**
	 * @see org.eclipse.gef.editparts.AbstractEditPart#createEditPolicies()
	 */
	protected void createEditPolicies() {
		super.createEditPolicies();
		installEditPolicy(EditPolicy.GRAPHICAL_NODE_ROLE,
				new RelationshipSourceAndTargetCreationEditPolicy());
		installEditPolicy(EditPolicy.SELECTION_FEEDBACK_ROLE,
				new GreenSelectionEditPolicy());
	}

	/**
	 * @return true if this part has a field compartment, false otherwise
	 */
	protected boolean hasFieldCompartment() {
		try {
			return model().getType().isClass();
		} catch (JavaModelException e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * @see org.eclipse.gef.NodeEditPart#getSourceConnectionAnchor(org.eclipse.gef.ConnectionEditPart)
	 */
	public ConnectionAnchor getSourceConnectionAnchor(
			ConnectionEditPart connection) {
		return new ChopboxAnchor(figure());
	}

	/**
	 * @see org.eclipse.gef.NodeEditPart#getTargetConnectionAnchor(org.eclipse.gef.ConnectionEditPart)
	 */
	public ConnectionAnchor getTargetConnectionAnchor(
			ConnectionEditPart connection) {
		return new ChopboxAnchor(figure());
	}

	/**
	 * @see org.eclipse.gef.NodeEditPart#getSourceConnectionAnchor(org.eclipse.gef.Request)
	 */
	public ConnectionAnchor getSourceConnectionAnchor(Request request) {
		return new ChopboxAnchor(figure());
	}

	/**
	 * @see org.eclipse.gef.NodeEditPart#getTargetConnectionAnchor(org.eclipse.gef.Request)
	 */
	public ConnectionAnchor getTargetConnectionAnchor(Request request) {
		return new ChopboxAnchor(figure());
	}

	/**
	 * @return the label containing the name of this part
	 */
	public IIconHolder getNameLabel() {
		return figure().getNameLabel();
	}
	
	/**
	 * Auxiliary method; makes reading easier. 
	 */
	private TypeModel model() {
		return (TypeModel) getModel();
	}
	
	/**
	 * @see org.eclipse.gef.EditPart#deactivate()
	 */
	public void deactivate() {
		// free up memory
		Image image = getNameLabel().getIcon();
		Font font = getNameLabel().getFont();
		
		if (image != null) image.dispose();
		if (font != null && figure().shouldDisposeFont()) font.dispose();
		
		super.deactivate(false);
	}
	
	/**
	 * Auxiliary method; makes reading easier. 
	 */
	private ITypeFigure figure() {
		return (ITypeFigure) getFigure();
	}
	
	/**
	 * @see edu.buffalo.cse.green.editor.controller.AbstractPart#updateColors(org.eclipse.draw2d.IFigure)
	 */
	protected void updateColors(IFigure f) {
		ITypeFigure tf = (ITypeFigure) f;
		
		f.setBorder(new LineBorder(
				PlugIn.getColorPreference(P_COLOR_TYPE_BORDER), 2));
		f.setBackgroundColor(PlugIn.getColorPreference(P_COLOR_UML));
		tf.getNameLabel().setForegroundColor(PlugIn.getColorPreference(
				P_COLOR_TYPE_TEXT));

		if (model().getImplicitRelationships().size() == 0) {
			f.setBorder(new LineBorder(
					PlugIn.getColorPreference(P_COLOR_TYPE_BORDER), 2));
		} else {
			f.setBorder(new LineBorder(PlugIn.getColorPreference(
					P_COLOR_TYPE_BORDER_HIDDENR), 2));
		}
	}

	/**
	 * @see edu.buffalo.cse.green.editor.controller.AbstractPart#setInitialBackgroundColor()
	 */
	public void setInitialBackgroundColor() {
		getNameLabel().getParent().setBackgroundColor(
				PlugIn.getColorPreference(P_COLOR_UML));
		getNameLabel().setOpaque(false);
	}
	
	/**
	 * @see edu.buffalo.cse.green.editor.controller.AbstractPart#setSelectedBackgroundColor()
	 */
	public void setSelectedBackgroundColor() {
		getNameLabel().getParent().setBackgroundColor(
				PlugIn.getColorPreference(P_COLOR_SELECTED));
		getNameLabel().setOpaque(false);
	}

	/**
	 * @see edu.buffalo.cse.green.editor.controller.MemberPart#getJavadocGrabber()
	 */
	protected JavadocGrabber getJavadocGrabber() {
		return new JavadocGrabber() {
			private boolean _alive = true;

			/**
			 * @see org.eclipse.jdt.core.dom.ASTVisitor#visit(org.eclipse.jdt.core.dom.EnumDeclaration)
			 */
			public boolean visit(EnumDeclaration node) {
				return visitNode(node);
			}
			
			/**
			 * @see org.eclipse.jdt.core.dom.ASTVisitor#visit(org.eclipse.jdt.core.dom.TypeDeclaration)
			 */
			public boolean visit(TypeDeclaration node) {
				return visitNode(node);
			}
			
			/**
			 * @param node - The node to visit.
			 * @return true if child nodes should be visited, false otherwise 
			 */
			private boolean visitNode(AbstractTypeDeclaration node) {
				if (!_alive) return false;
				IType type = (IType) node.resolveBinding().getJavaElement();
				if (checkDoc(type)) _doc = getDoc(node.getJavadoc());
				return _alive;
			}
			
			private boolean checkDoc(IType type) {
				if (compareElements(type)) {
					_alive = false;
					return true;
				}
				
				return false;
			}
		};
	}

	/**
	 * @return The label that holds the icons used for incremental exploration.
	 */
	public IFigure getRelLabel() {
		return figure().getRelLabel();
	}
}