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

import static edu.buffalo.cse.green.constants.XMLConstants.XML_BENDPOINT;
import static edu.buffalo.cse.green.constants.XMLConstants.XML_BENDPOINTS;
import static edu.buffalo.cse.green.constants.XMLConstants.XML_BENDPOINT_X;
import static edu.buffalo.cse.green.constants.XMLConstants.XML_BENDPOINT_Y;
import static edu.buffalo.cse.green.constants.XMLConstants.XML_RELATIONSHIP;
import static edu.buffalo.cse.green.constants.XMLConstants.XML_RELATIONSHIP_CLASS;
import static edu.buffalo.cse.green.constants.XMLConstants.XML_RELATIONSHIP_SOURCE_TYPE;
import static edu.buffalo.cse.green.constants.XMLConstants.XML_RELATIONSHIP_TARGET_TYPE;
import static edu.buffalo.cse.green.editor.controller.PropertyChange.RelationshipCardinality;
import static edu.buffalo.cse.green.editor.controller.PropertyChange.RelationshipSource;
import static edu.buffalo.cse.green.editor.controller.PropertyChange.RelationshipTarget;
import static edu.buffalo.cse.green.editor.model.RelationshipKind.Cumulative;
import static edu.buffalo.cse.green.editor.model.RelationshipKind.Single;
import static org.eclipse.jdt.core.dom.ASTNode.METHOD_DECLARATION;
import static org.eclipse.jdt.core.dom.ASTNode.INITIALIZER;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.commands.Command;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.Initializer;

import edu.buffalo.cse.green.GreenException;
import edu.buffalo.cse.green.PlugIn;
import edu.buffalo.cse.green.editor.DiagramEditor;
import edu.buffalo.cse.green.editor.action.ContextAction;
import edu.buffalo.cse.green.editor.controller.AbstractPart;
import edu.buffalo.cse.green.editor.model.commands.DeleteCommand;
import edu.buffalo.cse.green.editor.model.commands.HideRelationshipCommand;
import edu.buffalo.cse.green.editor.view.GreenBendpoint;
import edu.buffalo.cse.green.relationships.Relationship;
import edu.buffalo.cse.green.relationships.RelationshipGroup;
import edu.buffalo.cse.green.relationships.RelationshipRemover;
import edu.buffalo.cse.green.xml.XMLConverter;

/**
 * Models a relationship. Holds a source, target, and list of bendpoints.
 * Relationships can be shown implicitly or explicitly (default). When a
 * relationship is implicit, if the source or target is invisible, the border
 * color changes

 * @author bcmartin
 */
public class RelationshipModel extends AbstractModel<AbstractModel, RootModel, IJavaElement> implements Cloneable {
	/**
	 * The source and target types (in the diagram) that this relationship
	 * is between
	 */
	private IType _sourceType, _targetType;

	/**
	 * @return A representation of the cardinality. 
	 * 
	 * @throws JavaModelException
	 * 
	 * @author Gene Wang
	 */
	@SuppressWarnings("boxing")
	public String getCardinality() throws JavaModelException {
		final String NPE = "Node is not a child of a MethodDeclaration or Initializer block.";
		final int INF = 999999;
		boolean isGeneric = false;
		int min = INF;
		int max = 0;
		int constructors = 0;
		int uConstructors = 0;
		
		Map<ASTNode, Integer> cardinality = new HashMap<ASTNode, Integer>();
		
		RelationshipKind flags =
			PlugIn.getRelationshipGroup(getPartClass()).getFlags();
		
		if (flags.equals(Single)) {
			return "1";
		}
		if (flags.equals(Cumulative)) {
			for (Relationship relationship : _relationships) {
				ASTNode locator = relationship.getFeatures().get(0);
				
				while ((!(locator instanceof MethodDeclaration) &&
						!(locator instanceof Initializer)) && locator != null) {
					locator = locator.getParent();
				}
				if(locator == null) {
					//If locator is not within an Initializer block or Method Declaration, it
					//will be null.  This is highly unlikely, but I can't guarantee it won't 
					//ever happen, so throw an NPE here just in case
					throw new NullPointerException(NPE);
				}

				if(locator.getNodeType() == METHOD_DECLARATION) {
					isGeneric = !((MethodDeclaration) locator).isConstructor();
				}
				else if (locator.getNodeType() == INITIALIZER) {
					//This might be an erroneous assumption that Initializers
					//cannot have higher cardinality
					return "1";
				}
			}
		}
		
		for (IMethod method : _sourceType.getMethods()) {
			if (method.isConstructor()) {
				constructors++;
			}
		}
		
		for (Relationship relationship : _relationships) {
			ASTNode locator = relationship.getFeatures().get(0);

			int card;
			
			while ((!(locator instanceof MethodDeclaration) &&
					!(locator instanceof Initializer)) && locator != null) {
				locator = locator.getParent();
			}
			if(locator == null) {
				throw new NullPointerException(NPE);
			}
			
			if (cardinality.get(locator) == null) {
				card = 0;
				uConstructors++;
			} else {
				card = cardinality.get(locator);
			}
			
			if((locator.getNodeType() == METHOD_DECLARATION) &&
				((MethodDeclaration)locator).isConstructor()) {
				if (relationship.isGeneric()) {
					isGeneric = true;
					cardinality.put(locator, card + relationship.getFeatures().size() - 2); 
				} else {
					cardinality.put(locator, card + 1);
				}
			}
		}

		for (Integer card : cardinality.values()) {
			if (card < min) {
				min = card;
			}
			
			if (card > max) {
				max = card;
			}
		}

		if (uConstructors < constructors) {
			min = 0;
		}
		
		if (min == INF) {
			min = 0;
		}

		if (isGeneric) {
			return min + "..*";
		} else if (min == max) {
			return "" + min;
		} else {
			return min + ".." + max;
		}
	}
	
	/**
	 * A list of bendpoints belonging to the relationship. This should be
	 * updated whenever a bendpoint is added or removed 
	 */
	private List<GreenBendpoint> _bendpoints =
		new ArrayList<GreenBendpoint>();

	/**
	 * The class that represents the controller part for this particular kind of
	 * relationship
	 */
	private Class _partClass = null;

	/**
	 * A list of all relationships that have this particular source, target, and
	 * kind
	 */
	private Set<Relationship> _relationships;

	/**
	 * Error message indicating the desired relationship is invalid.
	 */
	private String REL_NOT_SUPPORTED =
		"The desired relationship is not supported";

	public RelationshipModel() {
		_relationships = new HashSet<Relationship>();
	}

	public RelationshipModel(IType sourceType, IType targetType,
			Class partClass) {
		this();

		_partClass = partClass;
		_sourceType = sourceType;
		_targetType = targetType;
	}

	/**
	 * Adds a relationship to this model.
	 * 
	 * @param relationship - The relationship.
	 * @return True if the relationship was added, false otherwise.
	 */
	public boolean addRelationship(Relationship relationship) {
		return _relationships.add(relationship);
	}

	/**
	 * @return The <code>IType</code> representing the source of this
	 * relationship.
	 */
	public IType getSourceType() {
		return _sourceType;
	}
	
	/**
	 * @return The <code>IType</code> representing the target of this
	 * relationship.
	 */
	public IType getTargetType() {
		return _targetType;
	}
	
	/**
	 * @see edu.buffalo.cse.green.editor.model.AbstractModel#getPartClass()
	 */
	public Class getPartClass() {
		if (_partClass == null) {
			GreenException.illegalOperation("Part class is null");
		}

		return _partClass;
	}

	/**
	 * Sets the <code>Class</code> representing this kind of relationship.
	 * 
	 * @param partClass - The class.
	 */
	public void setPartClass(Class partClass) {
		_partClass = partClass;
	}

	/**
	 * Returns the source model.
	 */
	public TypeModel getSourceModel() {
		if (getSourceType() == null) {
			return null;
		}
		
		return getRootModel().getModelFromType(_sourceType);
	}

	/**
	 * Returns the target model.
	 */
	public TypeModel getTargetModel() {
		if (getTargetType() == null) {
			return null;
		}
		
		return getRootModel().getModelFromType(_targetType);
	}

	/**
	 * Sets a new source model.
	 */
	public void setSourceModel(TypeModel newSource) {
		// update the source value
		_sourceType = newSource.getType();
		
		firePropertyChange(RelationshipSource, null, newSource);
	}

	/**
	 * Sets a new target model.
	 */
	public void setTargetModel(TypeModel newTarget) {
		// update the target value
		_targetType = newTarget.getType();
		
		firePropertyChange(RelationshipTarget, null, newTarget);
	}

	/**
	 * Sets a new list of bendpoints.
	 * 
	 * @param list - The list.
	 */
	public void setBendpointList(List<GreenBendpoint> list) {
		_bendpoints = list;
	}

	/**
	 * @return The list of bendpoints in this relationship model.
	 */
	public List<GreenBendpoint> getBendpointList() {
		return _bendpoints;
	}

	/**
	 * @return The name of the relationship.
	 */
	public String getRelationshipName() {
		return PlugIn.getRelationshipName(getPartClass());
	}

	/**
	 * @return The <code>RelationshipGroup</code> that represents this kind of
	 * relationship
	 */
	public RelationshipGroup getRelationshipGroup() {
		return PlugIn.getRelationshipGroup(getPartClass());
	}

	/**
	 * Hides/shows the relationship.
	 * 
	 * @param isVisible - If true, shows the relationship; if false, hides it.
	 */
	public void setVisible(boolean isVisible) {
		boolean modelVisible = isVisible;
		
		if (!getRelationshipGroup().isVisible()) {
			isVisible = false;
		}

		TypeModel sModel = getSourceModel();
		TypeModel tModel = getTargetModel();
		
		if (isVisible) {
			// check to ensure that both source/target are visible
			if (sModel == null || tModel == null) return;
			if (!sModel.isVisible() || !tModel.isVisible()) return;
			
			if (sModel.getImplicitRelationships().contains(this)) {
				sModel.removeImplicitRelationship(this);
			}
			
			if (tModel.getImplicitRelationships().contains(this)) {
				tModel.removeImplicitRelationship(this);
			}
		}
		
		super.setVisible(modelVisible);
	}

	/**
	 * @see edu.buffalo.cse.green.editor.model.AbstractModel#toXML(edu.buffalo.cse.green.xml.XMLConverter)
	 */
	public void toXML(XMLConverter converter) {
		converter.pushHeader(XML_RELATIONSHIP);
		converter.writeKey(XML_RELATIONSHIP_CLASS, getPartClass().getName());
		converter.writeKey(XML_RELATIONSHIP_SOURCE_TYPE,
				_sourceType.getHandleIdentifier());
		converter.writeKey(XML_RELATIONSHIP_TARGET_TYPE,
				_targetType.getHandleIdentifier());

		converter.pushHeader(XML_BENDPOINTS);
		
		for (GreenBendpoint bendpoint : getBendpointList()) {
			converter.pushHeader(XML_BENDPOINT);
			converter.writeKey(XML_BENDPOINT_X,
					bendpoint.getAbsoluteLocation().x);
			converter.writeKey(XML_BENDPOINT_Y,
					bendpoint.getAbsoluteLocation().y);
			converter.popHeader();
		}
		
		converter.popHeader();

		super.toXML(converter);

		converter.popHeader();
	}

	/**
	 * Hides/shows the relationship and modifies the source and target type
	 * appropriately.
	 * 
	 * @param show - If true, shows the relationship explicitly; if false, shows
	 * it implicitly.
	 */
	public void showRelationshipExplicitly(boolean show) {
		if (isVisible() == show || !getRelationshipGroup().isVisible()) { return; }

		if (show) { // show relationship explicitly
			getSourceModel().removeImplicitRelationship(this);
			getTargetModel().removeImplicitRelationship(this);

			if (getSourceModel().isVisible() && getTargetModel().isVisible()) {
				setVisible(true);
			}
		} else if (!show) { // show relationship implicitly
			getSourceModel().addImplicitRelationship(this);
			getTargetModel().addImplicitRelationship(this);
			setVisible(false);
		}
	}

	/**
	 * @see edu.buffalo.cse.green.editor.model.AbstractModel#getContextMenuFlag()
	 */
	public int getContextMenuFlag() {
		return ContextAction.CM_RELATIONSHIP;
	}

	/**
	 * @see edu.buffalo.cse.green.editor.model.AbstractModel#getDeleteCommand(edu.buffalo.cse.green.editor.DiagramEditor)
	 */
	public DeleteCommand getDeleteCommand(DiagramEditor editor) {
		return new DeleteRelationshipCommand(editor, this);
	}

	/**
	 * @param editor - The <code>DiagramEditor</code> containing this model.
	 * 
	 * @return A command to hide this model.
	 */
	public Command getHideCommand(DiagramEditor editor) {
		return new HideRelationshipCommand(this);
	}

	/**
	 * @see edu.buffalo.cse.green.editor.model.AbstractModel#getJavaElement()
	 */
	public IJavaElement getJavaElement() {
		return null;
	}
	
	/**
	 * @return The set of relationships represented by this model.
	 */
	public Set<Relationship> getRelationships() {
		return _relationships;
	}
	
	/**
	 * A command for deleting relationships.
	 * 
	 * @author bcmartin
	 */
	class DeleteRelationshipCommand extends DeleteCommand {
		private RelationshipModel _rModel;

		private DiagramEditor _editor;

		public DeleteRelationshipCommand(
				DiagramEditor editor,
				RelationshipModel relationship) {
			_editor = editor;
			_rModel = relationship;
		}

		/**
		 * @see edu.buffalo.cse.green.editor.model.commands.DeleteCommand#doDelete()
		 */
		public void doDelete() {
			AbstractPart part = _editor.getRootPart().getPartFromModel(_rModel);
			RelationshipGroup group = PlugIn.getRelationshipGroup(part.getClass());
			RelationshipRemover remover = group.getRemover();
			remover.setRelationship(_rModel);
			remover.run(remover.getCompilationUnit(_rModel.getSourceType()), null);
			remover.setRelationship(null);
			getSourceModel().updateFields();
			getSourceModel().updateMethods();
		}

		/**
		 * @see edu.buffalo.cse.green.editor.model.commands.DeleteCommand#getDeleteMessage()
		 */
		public String getDeleteMessage() {
			if (getSourceType().isBinary()) {
				return null;
			}
			
			return "Are you sure you want to delete that relationship?";
		}
	}
	
	/**
	 * @param relationship - The relationship.
	 * 
	 * @return An equivalent relationship if one is found inside this model,
	 * null otherwise. 
	 */
	public Relationship contains(Relationship relationship) {
		for (Relationship rel : _relationships) {
			if (rel.equals(relationship)) {
				return rel;
			}
		}
		
		return null;
	}

	/**
	 * @see edu.buffalo.cse.green.editor.model.AbstractModel#removeFromParent()
	 */
	public void removeFromParent() {
		RootModel root = getRootModel();

		if (root.getChildren().contains(this)) {
			if (getSourceModel() != null) {
				getSourceModel().removeOutgoingEdge(this);
			}
			
			if (getTargetModel() != null) {
				getTargetModel().removeIncomingEdge(this);
			}
			
			root.removeChildModel(this);
		}
	}

	/**
	 * Updates the cardinality label of this model.
	 */
	public void updateCardinality() {
		String cardinality;
		
		try {
			cardinality = getCardinality();
		} catch (JavaModelException e) {
			cardinality = "?";
		}
		
		firePropertyChange(RelationshipCardinality, null, cardinality);
	}
	
	/**
	 * @see edu.buffalo.cse.green.editor.model.AbstractModel#refresh()
	 */
	protected void refresh() {
		updateCardinality();
		setVisible(isVisible());
//
//		super.refresh();
	}

	/**
	 * Sets the bounds of this model.
	 * 
	 * @param bounds - The bounds to set.
	 */
	public void setBounds(Rectangle bounds) {
		setLocation(bounds.getLocation());
		setSize(bounds.getSize());
	}

	/**
	 * Ensures that the relationship being drawn is valid.
	 */
	public void assertValid() {
		RelationshipGroup group = PlugIn.getRelationshipGroup(getPartClass());
		
		try {
			if (getSourceType().isClass()) {
				if (getTargetType().isClass()) {
					if (group.isValidClassToClass()) return;
				} else if (getTargetType().isEnum()) {
					if (group.isValidClassToEnum()) return;
				} else if (getTargetType().isInterface()) {
					if (group.isValidClassToInterface()) return;
				}
			} else if (getSourceType().isEnum()) {
				if (getTargetType().isClass()) {
					if (group.isValidEnumToClass()) return;
				} else if (getTargetType().isEnum()) {
					if (group.isValidEnumToEnum()) return;
				} else if (getTargetType().isInterface()) {
					if (group.isValidEnumToInterface()) return;
				}
			} else if (getSourceType().isInterface()) {
				if (getTargetType().isClass()) {
					if (group.isValidInterfaceToClass()) return;
				} else if (getTargetType().isEnum()) {
					if (group.isValidInterfaceToEnum()) return;
				} else if (getTargetType().isInterface()) {
					if (group.isValidInterfaceToInterface()) return;
				}
			}
			
			GreenException.illegalOperation(REL_NOT_SUPPORTED);
		} catch (JavaModelException e) {
			GreenException.illegalOperation(e.getLocalizedMessage());
		}
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return getSourceModel() + "," + getTargetModel() + "," + getPartClass();
	}

	/**
	 * @see edu.buffalo.cse.green.editor.model.AbstractModel#handleDispose()
	 */
	public void handleDispose() {
		// do nothing
	}
	
	/**
	 * @see edu.buffalo.cse.green.editor.model.AbstractModel#createNewInstance(edu.buffalo.cse.green.editor.model.AbstractModel)
	 */
	public void createNewInstance(AbstractModel model) {
		getRootModel().addChild((RelationshipModel) model);
	}
}