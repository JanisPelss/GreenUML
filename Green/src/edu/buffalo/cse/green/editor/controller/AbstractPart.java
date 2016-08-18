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

import static edu.buffalo.cse.green.GreenException.GRERR_FIGURE_CONSTRUCTOR;
import static edu.buffalo.cse.green.editor.controller.PropertyChange.Children;
import static edu.buffalo.cse.green.editor.controller.PropertyChange.Location;
import static edu.buffalo.cse.green.editor.controller.PropertyChange.Refresh;
import static edu.buffalo.cse.green.editor.controller.PropertyChange.Size;
import static edu.buffalo.cse.green.editor.controller.PropertyChange.Visibility;
import static edu.buffalo.cse.green.preferences.PreferenceInitializer.P_COLOR_SELECTED;

import java.beans.PropertyChangeEvent;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.LayoutListener;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.Request;
import org.eclipse.gef.RequestConstants;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;
import org.eclipse.gef.editpolicies.ResizableEditPolicy;
import org.eclipse.jface.action.Action;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;

import edu.buffalo.cse.green.GreenException;
import edu.buffalo.cse.green.PlugIn;
import edu.buffalo.cse.green.editor.DiagramEditor;
import edu.buffalo.cse.green.editor.action.OpenElementAction;
import edu.buffalo.cse.green.editor.controller.policies.DeleteEditPolicy;
import edu.buffalo.cse.green.editor.model.AbstractModel;
import edu.buffalo.cse.green.editor.model.RootModel;
import edu.buffalo.cse.green.editor.model.commands.DeleteCommand;

/**
 * Represents the corresponding controller part for a model part
 * 
 * @author bcmartin
 * @author hk47
 */
public abstract class AbstractPart extends AbstractGraphicalEditPart {
	/**
	 * @see org.eclipse.gef.editparts.AbstractGraphicalEditPart#createFigure()
	 */
	protected final IFigure createFigure() {
		IFigure f = doCreateFigure();
		setFigure(f);
		f.setVisible(model().isVisible());
		updateColors(f);
		initialize();
		f.addLayoutListener(new LayoutListener() {
			/**
			 * @see org.eclipse.draw2d.LayoutListener#invalidate(org.eclipse.draw2d.IFigure)
			 */
			public void invalidate(IFigure container) {}
			
			/**
			 * @see org.eclipse.draw2d.LayoutListener#layout(org.eclipse.draw2d.IFigure)
			 */
			public boolean layout(IFigure container) {
				return false;
			}
			
			/**
			 * @see org.eclipse.draw2d.LayoutListener#postLayout(org.eclipse.draw2d.IFigure)
			 */
			public void postLayout(IFigure container) {}

			/**
			 * @see org.eclipse.draw2d.LayoutListener#remove(org.eclipse.draw2d.IFigure)
			 */
			public void remove(IFigure child) {}

			/**
			 * @see org.eclipse.draw2d.LayoutListener#setConstraint(org.eclipse.draw2d.IFigure, java.lang.Object)
			 */
			public void setConstraint(IFigure child, Object constraint) {
				model().setDrawnSize(child.getPreferredSize());
			}
		});
		return f;
	}
	
	/**
	 * Auxiliary method; makes reading easier. 
	 */
	private AbstractModel<?, ?, ?> model() {
		return (AbstractModel) getModel();
	}
	
	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object o) {
		if (o instanceof AbstractPart) {
			AbstractPart part = (AbstractPart) o;
			return model().equals(part.getModel());
		}
		
		return false;
	}
	
	/**
	 * @see org.eclipse.gef.EditPart#getChildren()
	 */
	public List<AbstractPart> getChildren() {
		List pChildren = super.getChildren();
		if (pChildren == null || pChildren.isEmpty()) return new ArrayList<AbstractPart>();

		return (ArrayList<AbstractPart>) pChildren;
	}
	
	/**
	 * Retrieves the <code>AbstractPart</code> that corresponds to the given
	 * <code>AbstractModel</code>
	 * 
	 * @param model - The <code>AbstractModel</code>
	 * @return The part corresponding to the given model
	 */
	public AbstractPart getPartFromModel(AbstractModel model) {
		return getRootPart().getPartFromModel(model);
	}

	/**
	 * @see org.eclipse.gef.editparts.AbstractEditPart#getModelChildren()
	 */
	protected List getModelChildren() {
		return model().getChildren();
	}

	public final void propertyChange(PropertyChangeEvent evt) {
	}

	/**
	 * @see org.eclipse.gef.EditPart#activate()
	 */
	public void activate() {
		if (getParent() != null && getParent().isActive() && !this.isActive()) {
			super.activate();

			addListener(Children, new ChildAndVisualsUpdater());
			addListener(Location, new VisualsUpdater());
			addListener(Refresh, new Refresher());
			addListener(Size, new VisualsUpdater());
			addListener(Visibility, new VisibilityUpdater());
			
			addPropertyListeners();
		}
	}

	/**
	 * Adds the appropriate <code>PropertyChange</code> listeners.
	 */
	protected abstract void addPropertyListeners();
	
	protected void addListener(PropertyChange kind, PropertyListener listener) {
		model().addListener(kind, listener);
	}
	
	/**
	 * @see org.eclipse.gef.EditPart#deactivate()
	 */
	public void deactivate() {
		if (isActive()) {
			super.deactivate();
		}
	}

	/**
	 * Updates the visual components of this part
	 */
	protected final void updateVisuals() {
		if (Display.getCurrent() != null) {
			refreshVisuals();
			updateColors(getFigure());
		} else {
			Display.getDefault().asyncExec(new Runnable() {
				/**
				 * @see java.lang.Runnable#run()
				 */
				public void run() {
					refreshVisuals();
					updateColors(getFigure());
				}
			});
		}
	}

	/**
	 * Updates the children of this part
	 */
	protected final void updateChildren() {
		if (Display.getCurrent() != null) {
			refreshChildren();
		} else {
			Display.getDefault().asyncExec(new Runnable() {
				/**
				 * @see java.lang.Runnable#run()
				 */
				public void run() {
					refreshChildren();
				}
			});
		}
	}

	/**
	 * @see org.eclipse.gef.editparts.AbstractEditPart#createEditPolicies()
	 */
	protected void createEditPolicies() {
		installEditPolicy(EditPolicy.COMPONENT_ROLE, new DeleteEditPolicy(this));
	}

	/**
	 * @return A <code>Command</code> for deleting this object
	 */
	public abstract DeleteCommand getDeleteCommand();

	/**
	 * @return The diagram's root part
	 */
	public RootPart getRootPart() {
		return ((AbstractPart) getParent()).getRootPart();
	}

	/**
	 * @return The diagram's root model
	 */
	public RootModel getRootModel() {
		return (RootModel) getRootPart().getModel();
	}

	/**
	 * @see org.eclipse.gef.EditPart#performRequest(org.eclipse.gef.Request)
	 */
	public void performRequest(Request req) {
		if (req.getType().equals(RequestConstants.REQ_OPEN)) {
			onDoubleClick();
		}
	}

	/**
	 * Method implemented to handle double-click events.
	 */
	protected abstract void onDoubleClick();

	/**
	 * @return The editor that contains this part
	 */
	public DiagramEditor getEditor() {
		return getRootPart().getEditor();
	}

	/**
	 * @return a new <code>OpenElementAction</code> that refers to this editor
	 */
	protected Action getOpenElementAction() {
		OpenElementAction action = new OpenElementAction();
		action.setSelectionProvider(getEditor());
		return action;
	}
	
	/**
	 * @return The figure that will represent this part.
	 */
	protected final IFigure generateFigure() {
		Constructor[] constructor =
			PlugIn.getViewPart(getClass()).getConstructors();
		
		if (constructor.length != 1 ||
				constructor[0].getParameterTypes().length != 0) {
			GreenException.illegalOperation(GRERR_FIGURE_CONSTRUCTOR
					+ "\nClass: " + PlugIn.getViewPart(getClass()));
		}
		
		try {
			return (IFigure) constructor[0].newInstance();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
			return null;
		} catch (InstantiationException e) {
			e.printStackTrace();
			return null;
		} catch (InvocationTargetException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * Updates the colors belonging to the figure.
	 */
	protected abstract void updateColors(IFigure f);
	
	/**
	 * Creates the figure corresponding to the part.
	 */
	protected abstract IFigure doCreateFigure();

	/**
	 * @return An <code>EditPolicy</code> appropriate to this part.
	 */
	public EditPolicy generateResizableEditPolicy() {
		return new ResizableEditPolicy();
	}

	/**
	 * Sets the original background color of the figure.
	 */
	public abstract void setInitialBackgroundColor();

	/**
	 * Sets the background color of the figure when it is selected.
	 */
	public void setSelectedBackgroundColor() {
		Color selectedColor = PlugIn.getColorPreference(P_COLOR_SELECTED);
		getFigure().setBackgroundColor(selectedColor);
	}

	/**
	 * Called after the constructor 
	 */
	public void initialize() {}

	class ChildAndVisualsUpdater implements PropertyListener {
		public void notify(Object oValue, Object nValue) {
			updateChildren();
			updateVisuals();
		}
	}

	class Refresher implements PropertyListener {
		public void notify(Object oValue, Object nValue) {
			getFigure().validate();
			updateVisuals();
		}
	}
	
	class VisibilityUpdater implements PropertyListener {
		public void notify(Object oValue, Object nValue) {
			getFigure().setVisible(nValue.equals(true));
			updateChildren();
			updateVisuals();
		}
	}
	
	class VisualsUpdater implements PropertyListener {
		public void notify(Object oValue, Object nValue) {
			updateVisuals();
		}
	}
}