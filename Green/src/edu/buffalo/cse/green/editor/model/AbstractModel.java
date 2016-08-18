/* This file is part of Green.
 *
 * Copyright (C) 2005 The Research Foundation of State University of New York
 * All Rights Under Copyright Reserved, The Research Foundation of S.U.N.Y.
 * 
 * Green is free software, licensed under the terms of the Eclipse
 * Public License, version 1.0.  The license is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package edu.buffalo.cse.green.editor.model;

import static edu.buffalo.cse.green.editor.controller.PropertyChange.Children;
import static edu.buffalo.cse.green.editor.controller.PropertyChange.Location;
import static edu.buffalo.cse.green.editor.controller.PropertyChange.Refresh;
import static edu.buffalo.cse.green.editor.controller.PropertyChange.Size;
import static edu.buffalo.cse.green.editor.controller.PropertyChange.Visibility;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.palette.ToolEntry;
import org.eclipse.jdt.core.IJavaElement;

import edu.buffalo.cse.green.GreenException;
import edu.buffalo.cse.green.editor.DiagramEditor;
import edu.buffalo.cse.green.editor.action.ContextAction;
import edu.buffalo.cse.green.editor.controller.PropertyChange;
import edu.buffalo.cse.green.editor.controller.PropertyListener;
import edu.buffalo.cse.green.editor.model.commands.DeleteCommand;
import edu.buffalo.cse.green.xml.XMLConverter;

/**
 * Model upon which all other models are based. This class provides the basic
 * functionality that all models in the editor share.
 * 
 * @author bcmartin
 * 
 * C - Child
 * P - Parent
 * E - Element
 */
public abstract class AbstractModel<C extends AbstractModel, P extends AbstractModel, E extends IJavaElement> {
	/**
	 * Holds a mapping of listeners. The appropriate listener will receive
	 * property change event information fired by this model.
	 */
	private Map<PropertyChange, PropertyListener> _listeners;

	/**
	 * Holds a list of all the children of this model.
	 */
	private List<C> _children;

	/**
	 * Holds value that indicates whether or not the model is visible.
	 */
	private boolean _isVisible = true;

	/**
	 * Holds this model's location.
	 */
	private Point _location = new Point(1, 1);

	/**
	 * Holds a reference to this model's parent.
	 */
	private P _parent;

	/**
	 * Holds this model's size.
	 */
	private Dimension _size = new Dimension(1, 1);

	private Dimension _drawnSize;

	protected AbstractModel() {
		_children = new ArrayList<C>();
		_listeners = new HashMap<PropertyChange, PropertyListener>();
	}

	/**
	 * @return The <code>Class</code> representing the <code>AbstractPart</code>
	 * that corresponds to this <code>AbstractModel</code> instance.
	 */
	public abstract Class getPartClass();

	/**
	 * Sets a global property held in the <code>UMLRootModel</code>.
	 * 
	 * @param property - The property to set.
	 * @param value - The value to assign to the given property.
	 */
	public void setProperty(String property, Object value) {
		getRootModel().setProperty(property, value);
	}

	/**
	 * Retrieves the value assigned to the given property.
	 * 
	 * @param property - The property.
	 * @return The value held by the given property.
	 */
	public Object getProperty(String property) {
		return getRootModel().getProperty(property);
	}

	/**
	 * Toggles the value of a property between true and false.
	 * 
	 * @param property - The property.
	 */
	public void toggleProperty(String property) {
		getRootModel().toggleProperty(property);
	}

	/**
	 * Gets the root model.
	 */
	public RootModel getRootModel() {
		return _parent.getRootModel();
	}

	/**
	 * Sets the location of the model.
	 */
	public void setLocation(Point iLocation) {
		_location = iLocation;
		firePropertyChange(Location, null, iLocation);
	}

	/**
	 * Sets the location of the model.
	 * 
	 * @param x - The x location of the model.
	 * @param y - The y location of the model.
	 */
	public void setLocation(int x, int y) {
		setLocation(new Point(x, y));
	}

	/**
	 * @return The location of the model.
	 */
	public Point getLocation() {
		return _location;
	}

	/**
	 * Sets the size of the model.
	 * 
	 * @param iSize - The size.
	 */
	public void setSize(Dimension iSize) {
		if (iSize == null) return;
		
		Dimension oldSize = _size;
		_size = iSize;
		firePropertyChange(Size, oldSize, iSize);
	}

	/**
	 * Sets the size of the model.
	 * 
	 * @param width - The width of the model.
	 * @param height - The height of the model.
	 */
	public void setSize(int width, int height) {
		setSize(new Dimension(width, height));
	}

	/**
	 * Gets the size of the model.
	 */
	public Dimension getSize() {
		return _size;
	}

	/**
	 * Adds a child to this model.
	 * 
	 * @param model - The model to add.
	 * @param setAsParent - If true, sets the child's parent value to this
	 * model.
	 * @param element - The element to map to the child model, or null if no
	 * element should be mapped.
	 */
	@SuppressWarnings("unchecked")
	protected final void addChild(C model, IJavaElement element) {
		if (!equals(model.getParent())) {
			// don't allow a model to have more than one parent
			if (model.getParent() != null) {
				GreenException.illegalOperation(
				"Model already has a parent");
			}
			
			model.setParent(this);
		}

		if (element != null) {
			getRootModel().mapElementToModel(element, model);
		}
		
		// add the child
		_children.add(model);
		firePropertyChange(Children, null, model);
	}

	/**
	 * Removes the specified child model.
	 * 
	 * @param model - The child model.
	 * @param element - The element that should be unmapped, or null if no
	 * element should be unmapped.
	 * @return The removed child model.
	 */
	protected final boolean removeChild(AbstractModel model) {
		boolean removed = _children.remove(model);
		firePropertyChange(Children, model, null);
		dispose();
		if (getJavaElement() != null) {
			getRootModel().unmapElement(getJavaElement());
		}
		
		return removed;
	}

	/**
	 * Gets all child models.
	 */
	public List<C> getChildren() {
		return _children;
	}

	/**
	 * Adds a <code>PropertyChangeListener</code> to this model.
	 * 
	 * @param listener - The listener to add.
	 */
	public void addListener(PropertyChange type, PropertyListener listener) {
		boolean exists = _listeners.containsKey(type);
		
		if (exists) {
			// combine the listeners
			_listeners.put(type, new CombinedListener(
					_listeners.get(type), listener));
		} else {
			// simply add the listeners
			_listeners.put(type, listener);
		}
	}

	/**
	 * Fires a property change. Used to communicate with the controller.
	 * 
	 * @param prop - The property that changed.
	 * @param old - The old value, or null if it is insignificant.
	 * @param newValue - The new value, or null if it is insignificant.
	 */
	public void firePropertyChange(PropertyChange type, Object oValue, Object nValue) {
		PropertyListener listener = _listeners.get(type);

		if (listener != null) {
			listener.notify(oValue, nValue);
		}
	}

	protected final void firePropertyChange(PropertyChange type) {
		firePropertyChange(type, false, true);
	}
	
	/**
	 * Gets the GUI parent of this model (in the EditPart tree).
	 * 
	 * @return The parent of this model, or <code>null</code> if none.
	 */
	public P getParent() {
		return _parent;
	}

	/**
	 * Sets the GUI parent of this model (in the EditPart tree).
	 * 
	 * @param parent - The parent of this model in the GUI tree.
	 */
	public void setParent(P parent) {
		_parent = parent;
	}

	/**
	 * Recursively calls this method on all children. Useful for writing XML to
	 * the converter. The converter processes the XML; after this method is
	 * called from the <code>DiagramEditor</code>, it is stored in a file for
	 * loading at a later point.
	 * 
	 * @param converter - The converter to use to translate the contents of the
	 * <code>AbstractModel</code>s contained in the editor into XML.
	 */
	public void toXML(XMLConverter converter) {
		for (C model : getChildren()) {
			model.toXML(converter);
		}
	}

	/**
	 * Shows/hides the model.
	 * 
	 * @param show - If true, the visibility is set to true; if false, the
	 * visibility is set to false.
	 */
	public void setVisible(boolean show) {
		_isVisible = show;

		if (show) {
			firePropertyChange(Visibility, null, true);
		} else {
			firePropertyChange(Visibility, null, false);
		}
	}

	/**
	 * @return True if the model is visible, false otherwise.
	 */
	public boolean isVisible() {
		return _isVisible;
	}

	/**
	 * @return The value indicating what kind of context menu should be shown
	 * if this model is selected.
	 */
	public int getContextMenuFlag() {
		return ContextAction.CM_EDITOR;
	}

	/**
	 * @param klass - The <code>Class</code>
	 * @return A list of all children of this model that are of the instance
	 * specified by klass.
	 */
	public List<C> getChildren(Class klass) {
		List<C> children = new ArrayList<C>();

		for (C model : _children) {
			if (klass.isInstance(model)) {
				children.add(model);
			}
		}

		return children;
	}

	/**
	 * Refreshes this model and all of its children.
	 */
	protected void refresh() {
		for (C model : getChildren()) {
			model.refresh();
		}

		firePropertyChange(Refresh);
	}

	/**
	 * Forces the refresh() method to be called.
	 */
	public void forceRefesh() {
		firePropertyChange(Refresh);
	}

	/**
	 * @param editor - The editor.
	 * @return A delete command for this model.
	 */
	public abstract DeleteCommand getDeleteCommand(DiagramEditor editor);

	/**
	 * @return The bounds of this model.
	 */
	public Rectangle getBounds() {
		return new Rectangle(getLocation(), getSize());
	}

	/**
	 * @return The <code>IJavaElement</code> that corresponds to this model.
	 */
	public abstract E getJavaElement();
	
	/**
	 * Removes this model from its parent.
	 */
	public abstract void removeFromParent();
	
	/**
	 * @see java.lang.Object#toString()
	 */
	public abstract String toString();
	
	/**
	 * Removes all of this model's children from it.
	 */
	public final void removeChildren() {
		List<C> children = new ArrayList<C>();
		children.addAll(getChildren());

		for (C model : children) {
			model.removeFromParent();
		}

		firePropertyChange(Children);
	}
	
	/**
	 * Called when a model is destroyed.
	 */
	public final void dispose() {
		handleDispose();
		
		for (C model : getChildren()) {
			model.dispose();
		}
		
		//_listeners = new HashMap<PropertyChange, PropertyListener>();
	}
	
	/**
	 * Called recursively by dispose to perform disposal-time events.
	 */
	public abstract void handleDispose();
	
	/**
	 * @return Retrieves the <code>TypeModel</code> that contains this model, or
	 * this if this is a <code>TypeModel</code>.
	 */
	public TypeModel getTypeModel() {
		GreenException.illegalOperation("Invalid operation");
		return null;
	}

	/**
	 * Display a creation dialog to the user.
	 * 
	 * @return true if the invocation should occur, false otherwise.
	 */
	public int invokeCreationDialog(ToolEntry tool) {
		GreenException.illegalOperation("This operation is not supported");
		return 0;
	}
	
	/**
	 * Creates a clone of the given model.
	 * 
	 * @param model - The given model.
	 */
	public void createNewInstance(AbstractModel model) {
		GreenException.illegalOperation("This operation is not supported");
	}

	/**
	 * Called during creation to ensure this model is valid.
	 */
	public void assertValid() {
		// do nothing by default
	}
	
	/**
	 * @return The size the figure corresponding to this model is drawn as.
	 */
	public Dimension getDrawnSize() {
		return _drawnSize;
	}
	
	/**
	 * Sets the drawn size of the model. Used for laying out the diagram.
	 * 
	 * @param size - The actual size of the figure.
	 */
	public void setDrawnSize(Dimension size) {
		_drawnSize = size;
	}
}

class CombinedListener implements PropertyListener {
	private PropertyListener _oldListener;
	private PropertyListener _newListener;

	public CombinedListener(PropertyListener ol, PropertyListener nl) {
		_oldListener = ol;
		_newListener = nl;
	}

	/**
	 * @see edu.buffalo.cse.green.editor.controller.PropertyListener#notify(java.lang.Object, java.lang.Object)
	 */
	public void notify(Object oValue, Object nValue) {
		// call the newer notification earlier
		_newListener.notify(oValue, nValue);
		_oldListener.notify(oValue, nValue);
	}
}