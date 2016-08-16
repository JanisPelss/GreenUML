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

import static edu.buffalo.cse.green.GreenException.GRERR_INVALID_INDEX;
import static edu.buffalo.cse.green.editor.controller.PropertyChange.RelationshipCardinality;
import static edu.buffalo.cse.green.editor.controller.PropertyChange.RelationshipSource;
import static edu.buffalo.cse.green.editor.controller.PropertyChange.RelationshipTarget;
import static edu.buffalo.cse.green.editor.controller.PropertyChange.Size;
import static edu.buffalo.cse.green.preferences.PreferenceInitializer.P_COLOR_REL_ARROW_FILL;
import static edu.buffalo.cse.green.preferences.PreferenceInitializer.P_COLOR_REL_LINE;
import static edu.buffalo.cse.green.preferences.PreferenceInitializer.P_COLOR_REL_TEXT;
import static edu.buffalo.cse.green.preferences.PreferenceInitializer.P_DISPLAY_RELATIONSHIP_SUBTYPES;
import static edu.buffalo.cse.green.preferences.PreferenceInitializer.P_DRAW_LINE_WIDTH;
import static edu.buffalo.cse.green.preferences.PreferenceInitializer.P_FONT;

import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.draw2d.BendpointConnectionRouter;
import org.eclipse.draw2d.Connection;
import org.eclipse.draw2d.ConnectionEndpointLocator;
import org.eclipse.draw2d.ConnectionRouter;
import org.eclipse.draw2d.FigureListener;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.LineBorder;
import org.eclipse.draw2d.PolylineConnection;
import org.eclipse.draw2d.RotatableDecoration;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.gef.ConnectionEditPart;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.NodeEditPart;
import org.eclipse.gef.editpolicies.ConnectionEndpointEditPolicy;
import org.eclipse.gef.editpolicies.NonResizableEditPolicy;
import org.eclipse.gef.requests.BendpointRequest;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.widgets.Display;

import edu.buffalo.cse.green.GreenException;
import edu.buffalo.cse.green.PlugIn;
import edu.buffalo.cse.green.editor.DiagramEditor;
import edu.buffalo.cse.green.editor.controller.policies.BendableRelationshipEditPolicy;
import edu.buffalo.cse.green.editor.controller.policies.RelationshipLayoutEditPolicy;
import edu.buffalo.cse.green.editor.controller.policies.RelationshipSelectionEditPolicy;
import edu.buffalo.cse.green.editor.model.RelationshipModel;
import edu.buffalo.cse.green.editor.model.TypeModel;
import edu.buffalo.cse.green.editor.model.commands.CreateBendpointCommand;
import edu.buffalo.cse.green.editor.model.commands.DeleteCommand;
import edu.buffalo.cse.green.editor.view.GreenBendpoint;
import edu.buffalo.cse.green.editor.view.RelationshipFigure;
import edu.buffalo.cse.green.relationships.RelationshipGroup;

/**
 * @author bcmartin
 * 
 * The controller part corresponding to a <code>RelationshipModel</code>
 */
public abstract class RelationshipPart extends AbstractPart
implements ConnectionEditPart, PropertyChangeListener,
           RelationshipFigure.RelationshipFigureListener {
	private int _loopSize = 40;
	
	private RootPart _root;
	
	private Label _sourceMultiplicityLabel;
	
	private RotatableDecoration _sourceDecoration;
	
	private RotatableDecoration _targetDecoration;
	
	private boolean _ignoreNextUpdateRequest;

	public RelationshipPart() {
		_sourceMultiplicityLabel = new Label();
		_sourceMultiplicityLabel.setText("");
		_sourceMultiplicityLabel.setFont(
				PlugIn.getFontPreference(P_FONT, false));
	}
	
	/**
	 * @see edu.buffalo.cse.green.editor.controller.AbstractPart#doCreateFigure()
	 */
	protected final IFigure doCreateFigure() {
		PolylineConnection rFigure = createConnection();
		model().getSourceModel().getOutgoingEdges().add(model());
		model().getTargetModel().getIncomingEdges().add(model());
		rFigure.setConnectionRouter(DiagramEditor.getConnectionRouter());
		
		TypeModel modelSource = model().getSourceModel();
		TypeModel modelTarget = model().getTargetModel();
		
		if (modelSource != null && modelTarget != null) {
			_sourceDecoration = createSourceArrow();
			_targetDecoration = createTargetArrow();
			rFigure.setSourceDecoration(_sourceDecoration);
			rFigure.setTargetDecoration(_targetDecoration);
			
			ConnectionEndpointLocator sourceEndpointLocator = new ConnectionEndpointLocator(
					rFigure, false);
			sourceEndpointLocator.setVDistance(15);
			_sourceMultiplicityLabel = new Label();
			_sourceMultiplicityLabel.setFont(
					PlugIn.getFontPreference(P_FONT, false));
			rFigure.add(_sourceMultiplicityLabel, sourceEndpointLocator);
		}
		
		rFigure.setLineWidth(PlugIn.getIntegerPreference(P_DRAW_LINE_WIDTH));
		rFigure.addFigureListener(new FigureListener() {
			/**
			 * @see org.eclipse.draw2d.FigureListener#figureMoved(org.eclipse.draw2d.IFigure)
			 */
			public void figureMoved(IFigure source) {
				if (!model().getBounds().equals(source.getBounds())) {
					model().setBounds(source.getBounds());
				}
			}
		});
		
		return rFigure;
	}
	
	/**
	 * @see org.eclipse.gef.editparts.AbstractEditPart#createEditPolicies()
	 */
	protected void createEditPolicies() {
		super.createEditPolicies();
		installEditPolicy(EditPolicy.SELECTION_FEEDBACK_ROLE,
				new RelationshipSelectionEditPolicy());
		installEditPolicy(EditPolicy.CONNECTION_ENDPOINTS_ROLE,
				new ConnectionEndpointEditPolicy());
		installEditPolicy(EditPolicy.CONNECTION_BENDPOINTS_ROLE,
				new BendableRelationshipEditPolicy());
		installEditPolicy(EditPolicy.LAYOUT_ROLE,
				new RelationshipLayoutEditPolicy());
	}
	
	/**
	 * @see org.eclipse.gef.EditPart#activate()
	 */
	public void activate() {
		if (getParent() != null) {
			super.activate();
			_root = (RootPart) getRootPart();
			updateSourceAnchor();
			updateTargetAnchor();
			
			if (model().getSourceModel().equals(model().getTargetModel())) {
				createLoop();
			}
		}
	}
	
	/**
	 * @return the connection created by this part
	 */
	public abstract RelationshipFigure createConnection();
	
	/**
	 * @return the arrow on the source end of this connection
	 */
	public abstract RotatableDecoration createSourceArrow();
	
	/**
	 * @return the arrow on the target end of this connection
	 */
	public abstract RotatableDecoration createTargetArrow();
	
	/**
	 * @see edu.buffalo.cse.green.editor.controller.AbstractPart#getDeleteCommand()
	 */
	public DeleteCommand getDeleteCommand() {
		return model().getDeleteCommand(getEditor());
	}
	
	/**
	 * @see org.eclipse.gef.ConnectionEditPart#getSource()
	 */
	public EditPart getSource() {
		return _root.getPartFromModel(model().getSourceModel());
	}
	
	/**
	 * @see org.eclipse.gef.ConnectionEditPart#getTarget()
	 */
	public EditPart getTarget() {
		return _root.getPartFromModel(model().getTargetModel());
	}
	
	/**
	 * @see org.eclipse.gef.ConnectionEditPart#setSource(org.eclipse.gef.EditPart)
	 */
	public void setSource(EditPart source) {
		updateSourceAnchor();
	}
	
	/**
	 * @see org.eclipse.gef.ConnectionEditPart#setTarget(org.eclipse.gef.EditPart)
	 */
	public void setTarget(EditPart target) {
		updateTargetAnchor();
	}
	
	/**
	 * Sets the source anchor to the appropriate part
	 */
	protected void updateSourceAnchor() {
		NodeEditPart part = (NodeEditPart) getSource();
		
		if (isActive()) {
			figure().setSourceAnchor(part.getSourceConnectionAnchor(this));
		}
	}
	
	/**
	 * Sets the target anchor to the appropriate part
	 */
	protected void updateTargetAnchor() {
		NodeEditPart part = (NodeEditPart) getTarget();
		
		if (isActive()) {
			figure().setTargetAnchor(part.getTargetConnectionAnchor(this));
		}
	}
	
	/**
	 * @see edu.buffalo.cse.green.editor.view.RelationshipFigure.RelationshipFigureListener#relationshipFigureMoved(edu.buffalo.cse.green.editor.view.RelationshipFigure)
	 */
	public void relationshipFigureMoved(RelationshipFigure movedFigure) {
		updateChildren();
	}
	
	/**
	 * @see edu.buffalo.cse.green.editor.view.RelationshipFigure.RelationshipFigureListener#relationshipFigureWasAdded(edu.buffalo.cse.green.editor.view.RelationshipFigure)
	 */
	public void relationshipFigureWasAdded(RelationshipFigure addedFigure) {
		List<GreenBendpoint> bendpoints = model().getBendpointList();
		
		if (bendpoints == null) {
			model().setBendpointList(
					bendpoints = new ArrayList<GreenBendpoint>());
		}
		
		DiagramEditor.getConnectionRouter().setConstraint(
				(Connection) getFigure(), bendpoints);
	}
	
	/**
	 * @see edu.buffalo.cse.green.editor.view.RelationshipFigure.RelationshipFigureListener#relationshipFigureWasRemoved(edu.buffalo.cse.green.editor.view.RelationshipFigure)
	 */
	public void relationshipFigureWasRemoved(RelationshipFigure removedFigure) {
	}
	
	/**
	 * Creates a loop from a model to itself. Used for recursive relationships.
	 */
	public void createLoop() {
		final RelationshipModel rModel = (RelationshipModel) getModel();
		RelationshipFigure rFigure = (RelationshipFigure) getFigure();
		rFigure.setConnectionRouter(new BendpointConnectionRouter());
		BendpointRequest request;
		
		for (int x = 0; x < 3; x++) {
			request = new BendpointRequest();
			request.setIndex(x);
			request.setSource(this);
			
			switch(x) {
			case 0:
				request.setLocation(new Point(_loopSize, 0));
				break;
			case 1:
				request.setLocation(new Point(_loopSize, -_loopSize));
				break;
			case 2:
				request.setLocation(new Point(0, -_loopSize));
				break;
			default:
				GreenException.illegalOperation(GRERR_INVALID_INDEX);
			}
			
			new CreateBendpointCommand(rFigure, request).execute();
		}
		
		rFigure.setRecursive(
				getPartFromModel(rModel.getSourceModel()).getFigure());
		
		rModel.getSourceModel().addListener(Size, new PropertyListener() {
			private Dimension _oldSize = rModel.getSourceModel().getSize();
					
			public void notify(Object oValue, Object nValue) {
				Dimension newSize = rModel.getSourceModel().getSize();
				Dimension delta = newSize.getExpanded(
						_oldSize.getNegated()).getScaled(.5);
				delta.height = -delta.height;
				_oldSize = newSize;
			}
		});
	}
	
	/**
	 * @see edu.buffalo.cse.green.editor.controller.AbstractPart#addPropertyListeners()
	 */
	protected void addPropertyListeners() {
		addListener(RelationshipSource, new RelationshipSourceHandler());
		addListener(RelationshipTarget, new RelationshipTargetHandler());
		addListener(RelationshipCardinality,
				new RelationshipCardinalityHandler());
	}
	
	/**
	 * @see edu.buffalo.cse.green.editor.controller.AbstractPart#onDoubleClick()
	 */
	protected void onDoubleClick() {
		// do nothing
	}
	
	/**
	 * @see edu.buffalo.cse.green.editor.controller.AbstractPart#updateColors(org.eclipse.draw2d.IFigure)
	 */
	protected void updateColors(IFigure f) {
		Color fillColor = PlugIn.getColorPreference(P_COLOR_REL_ARROW_FILL);
		Color lineColor = PlugIn.getColorPreference(P_COLOR_REL_LINE);
		_sourceMultiplicityLabel.setForegroundColor(
				PlugIn.getColorPreference(P_COLOR_REL_TEXT));
		
		f.setForegroundColor(lineColor);
		
		// Is it possible for _sourceDecoration to be null in the case that a relationship
		// arc does not have a decoration at the source?  If not, why are we ignoring the 
		// null pointer here?
		if (_sourceDecoration != null) {
			_sourceDecoration.setBackgroundColor(fillColor);
			_sourceDecoration.setForegroundColor(lineColor);
		}
		
		// See comment above.
		if (_targetDecoration != null) {
			_targetDecoration.setBackgroundColor(fillColor);
			_targetDecoration.setForegroundColor(lineColor);
		}
	}
	
	/**
	 * Updates the relationship's cardinality label.
	 */
	private void updateCardinalityLabel() {
		try {
			String cardinality = model().getCardinality();
			
			if (cardinality.equals("1")) {
				cardinality = "";
			}
			
			_sourceMultiplicityLabel.setText(cardinality + subtypeLabel());
		} catch (JavaModelException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * @see org.eclipse.gef.editparts.AbstractEditPart#refreshVisuals()
	 */
	protected void refreshVisuals() {
		updateCardinalityLabel();
		updateFont();
		
		ConnectionRouter desiredRouter = DiagramEditor.getConnectionRouter();
		
		if (!figure().isRecursive()) {
			if (!figure().getConnectionRouter().getClass().equals(desiredRouter.getClass())) {
				figure().setConnectionRouter(desiredRouter);
			}
		}
		
		//figure().getConnectionRouter().route(figure());
		figure().setLineWidth(PlugIn.getIntegerPreference(
				P_DRAW_LINE_WIDTH));
	}
	
	/**
	 * Updates the font used in the label 
	 */
	private void updateFont() {
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
		if (_ignoreNextUpdateRequest == true) {
			_ignoreNextUpdateRequest = false;
			return;
		}
		
		_ignoreNextUpdateRequest = true;
		
		// get rid of the old font
		Font font = _sourceMultiplicityLabel.getFont();
		font.dispose();
		
		// create the new font
		_sourceMultiplicityLabel.setFont(PlugIn.getFontPreference(P_FONT, false));
	}
	
	/**
	 * Auxiliary method; makes reading easier. 
	 */
	private RelationshipFigure figure() {
		return (RelationshipFigure) getFigure();
	}
	
	/**
	 * @see edu.buffalo.cse.green.editor.controller.AbstractPart#generateResizableEditPolicy()
	 */
	public EditPolicy generateResizableEditPolicy() {
		NonResizableEditPolicy dragPolicy = new NonResizableEditPolicy();
		dragPolicy.setDragAllowed(false);
		return dragPolicy;
	}
	
	/**
	 * @see edu.buffalo.cse.green.editor.controller.AbstractPart#setSelectedBackgroundColor()
	 */
	public void setSelectedBackgroundColor() {
		getFigure().setBorder(
				new LineBorder(PlugIn.getColorPreference(P_COLOR_REL_LINE), 1));
	}
	
	/**
	 * @see edu.buffalo.cse.green.editor.controller.AbstractPart#setInitialBackgroundColor()
	 */
	public void setInitialBackgroundColor() {
		// not necessary
	}
	
	/**
	 * @return The subtype label, if one is desired.
	 */
	private String subtypeLabel() {
		if (!PlugIn.getBooleanPreference(P_DISPLAY_RELATIONSHIP_SUBTYPES))
			return "";
		
		RelationshipGroup group = PlugIn.getRelationshipGroup(getClass());
		if (group.getSubtype() == null)
			return ""; 
		
		return " <<" + group.getSubtype() + ">>";
	}
	
	/**
	 * Auxiliary method; makes reading easier. 
	 */
	private RelationshipModel model() {
		return (RelationshipModel) getModel();
	}

	class RelationshipCardinalityHandler implements PropertyListener {
		public void notify(Object oValue, Object nValue) {
			updateCardinalityLabel();
			updateChildren();
		}
	}
	
	class RelationshipSourceHandler implements PropertyListener {
		public void notify(Object oValue, Object nValue) {
			RootPart rootPart = getRootPart();
			TypeModel oldModel = (TypeModel) oValue;
			TypeModel newModel = (TypeModel) nValue;
			
			if (oldModel != null) {
				oldModel.getOutgoingEdges().remove(getModel());
			}
			
			newModel.getOutgoingEdges().add(model());
			setSource(rootPart.getPartFromModel(newModel));
			
			updateChildren();
		}
	}

    class RelationshipTargetHandler implements PropertyListener {
		public void notify(Object oValue, Object nValue) {
			RootPart rootPart = getRootPart();
			TypeModel oldModel = (TypeModel) oValue;
			TypeModel newModel = (TypeModel) nValue;
			
			if (oldModel != null) {
				oldModel.getIncomingEdges().remove(getModel());
			}
			
			newModel.getIncomingEdges().add(model());
			setTarget(rootPart.getPartFromModel(newModel));
			
			updateChildren();
		}
    }
}