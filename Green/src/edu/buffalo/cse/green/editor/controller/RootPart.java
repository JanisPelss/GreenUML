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

import static edu.buffalo.cse.green.editor.controller.PropertyChange.Children;
import static edu.buffalo.cse.green.editor.controller.PropertyChange.GenerateRelationship;
import static edu.buffalo.cse.green.editor.controller.PropertyChange.UpdateRelationships;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.FreeformLayout;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.XYLayout;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.gef.DragTracker;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPartViewer;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.Request;
import org.eclipse.gef.tools.MarqueeDragTracker;
import org.eclipse.jface.viewers.StructuredSelection;

import edu.buffalo.cse.green.PlugIn;
import edu.buffalo.cse.green.editor.DiagramEditor;
import edu.buffalo.cse.green.editor.controller.policies.DiagramLayoutEditPolicy;
import edu.buffalo.cse.green.editor.controller.policies.GreenSelectionEditPolicy;
import edu.buffalo.cse.green.editor.model.AbstractModel;
import edu.buffalo.cse.green.editor.model.RelationshipModel;
import edu.buffalo.cse.green.editor.model.commands.DeleteCommand;
import edu.buffalo.cse.green.editor.view.RootFigure;
import edu.buffalo.cse.green.relationships.RelationshipGenerator;

/**
 * Root controller part for our editor.
 * 
 * @author bcmartin
 */
public class RootPart extends AbstractPart {
	/**
	 * A mapping from each model to its corresponding part
	 */
	private HashMap<AbstractModel, AbstractPart> _modelToEditPartMap;

	/**
	 * The editor that contains this part
	 */
	private DiagramEditor _editor;

	public RootPart() {
		_modelToEditPartMap = new HashMap<AbstractModel, AbstractPart>();
	}

	/**
	 * @see org.eclipse.gef.editparts.AbstractEditPart#removeChild(org.eclipse.gef.EditPart)
	 */
	protected void removeChild(EditPart child) {
		if (child instanceof AbstractPart) {
			AbstractModel model = (AbstractModel) child.getModel();
			if (getPartFromModel(model) == child) {
				unmapModelFromEditPart(model);
			}
		}
		
		super.removeChild(child);
	}

	/**
	 * @see edu.buffalo.cse.green.editor.controller.AbstractPart#doCreateFigure()
	 */
	protected IFigure doCreateFigure() {
		Figure f = new RootFigure();
		f.setLayoutManager(new FreeformLayout());
		f.setOpaque(true);
		f.setSize(new Dimension(1000, 1000));
		return f;
	}

	/**
	 * @see org.eclipse.gef.editparts.AbstractEditPart#createEditPolicies()
	 */
	protected void createEditPolicies() {
		super.createEditPolicies();
		installEditPolicy(EditPolicy.LAYOUT_ROLE, new DiagramLayoutEditPolicy(
				(XYLayout) getContentPane().getLayoutManager()));
		installEditPolicy(EditPolicy.SELECTION_FEEDBACK_ROLE,
				new GreenSelectionEditPolicy());
	}

	/**
	 * @see org.eclipse.gef.EditPart#getDragTracker(org.eclipse.gef.Request)
	 */
	public DragTracker getDragTracker(Request req) {
		return new RootPartDragTracker(this);
	}

	/**
	 * @see edu.buffalo.cse.green.editor.controller.AbstractPart#getRootPart()
	 */
	public RootPart getRootPart() {
		return this;
	}

	/**
	 * Maps a model to its corresponding part
	 * 
	 * @param model - The model to be mapped
	 * @param part - The edit part to be mapped to
	 */
	public void mapModelToEditPart(AbstractModel model, AbstractPart part) {
		_modelToEditPartMap.put(model, part);
	}

	/**
	 * Unmaps a model from its corresponding part
	 * 
	 * @param model - The model to be unmapped
	 */
	public void unmapModelFromEditPart(AbstractModel model) {
		_modelToEditPartMap.remove(model);
	}

	/**
	 * @see edu.buffalo.cse.green.editor.controller.AbstractPart#getPartFromModel(edu.buffalo.cse.green.editor.model.AbstractModel)
	 */
	public AbstractPart getPartFromModel(AbstractModel model) {
		return (AbstractPart) _modelToEditPartMap.get(model);
	}

	/**
	 * @see edu.buffalo.cse.green.editor.controller.AbstractPart#getDeleteCommand()
	 */
	public DeleteCommand getDeleteCommand() {
		return null;
	}

	/**
	 * Set the editor that is editing this diagram
	 * 
	 * @param editor - The editor that is editing this diagram
	 */
	public void setEditor(DiagramEditor editor) {
		_editor = editor;
	}

	/**
	 * @see edu.buffalo.cse.green.editor.controller.AbstractPart#getEditor()
	 */
	public DiagramEditor getEditor() {
		return _editor;
	}

	/**
	 * @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
	 */
	public void addPropertyListeners() {
		addListener(Children, new RootChildHandler());
		addListener(GenerateRelationship, new RelationshipGenerationHandler());
		addListener(UpdateRelationships, new RelationshipUpdateHandler());
	}

	/**
	 * @see edu.buffalo.cse.green.editor.controller.AbstractPart#onDoubleClick()
	 */
	protected void onDoubleClick() {
		// do nothing
	}
	
	/**
	 * Custom drag tracker used in our editor.
	 * 
	 * @author evertwoo
	 */
	class RootPartDragTracker extends MarqueeDragTracker {
		protected static final int TOGGLE_MODE = 1;

		protected static final int APPEND_MODE = 2;

		private int _mode;

		private EditPart _startingEditPart;

		public RootPartDragTracker(EditPart startingEditPart) {
			_startingEditPart = startingEditPart;
		}

		/**
		 * @see org.eclipse.gef.tools.AbstractTool#handleButtonDown(int)
		 */
		protected boolean handleButtonDown(int button) {
			if ((getState() & STATE_INITIAL) != 0) {
				if (getCurrentInput().isControlKeyDown()) {
					_mode = TOGGLE_MODE;
				} else if (getCurrentInput().isShiftKeyDown()) {
					_mode = APPEND_MODE;
				}
			}
			return super.handleButtonDown(button);
		}

		/**
		 * @see org.eclipse.gef.tools.AbstractTool#handleButtonUp(int)
		 */
		protected boolean handleButtonUp(int button) {
			EditPartViewer viewer = getCurrentViewer();

			// If we're releasing the button, and it's about to be single-clicking,
			// not dragging
			if ((getState() & STATE_DRAG_IN_PROGRESS) != 0
					&& getStartLocation().equals(getLocation())) {
				// Single select
				if (_mode == APPEND_MODE) {
					viewer.appendSelection(_startingEditPart);
				} else if (_mode == TOGGLE_MODE) {
					List<EditPart> selected = new ArrayList<EditPart>(
							(ArrayList<EditPart>) viewer.getSelectedEditParts());
					if (selected.contains(_startingEditPart)) {
						selected.remove(_startingEditPart);
					} else {
						selected.add(_startingEditPart);
					}
					viewer.setSelection(new StructuredSelection(selected));
				} else {
					viewer.select(_startingEditPart);
				}

			}

			// Handle dragged rectangle selections
			boolean result = super.handleButtonUp(button);

			return result;
		}
	}
	
	/**
	 * @see edu.buffalo.cse.green.editor.controller.AbstractPart#updateColors(org.eclipse.draw2d.IFigure)
	 */
	protected void updateColors(IFigure f) {}

	/**
	 * @see edu.buffalo.cse.green.editor.controller.AbstractPart#setInitialBackgroundColor()
	 */
	public void setInitialBackgroundColor() {
		// not necessary
	}
	
	class RelationshipGenerationHandler implements PropertyListener {
		public void notify(Object oValue, Object nValue) {
			RelationshipModel rModel = (RelationshipModel) nValue;

			// pass the CompilationUnit to the appropriate generator
			RelationshipGenerator rGenerator = PlugIn.getRelationshipGroup(
					rModel.getPartClass()).getGenerator();
			rGenerator.accept(rGenerator.getCompilationUnit(rModel
					.getSourceType()));
			rModel.getSourceModel().updateFields();
			rModel.getSourceModel().updateMethods();

			getEditor().autoSave();
			getEditor().refresh();
		}
	}

	class RelationshipUpdateHandler implements PropertyListener {
		public void notify(Object oValue, Object nValue) {
			getEditor().refresh();
		}
	}
	
	class RootChildHandler implements PropertyListener {
		public void notify(Object oValue, Object nValue) {
			if (oValue == null) {
				AbstractModel model = (AbstractModel) nValue;
				model.assertValid();
			}
		}
	}
}