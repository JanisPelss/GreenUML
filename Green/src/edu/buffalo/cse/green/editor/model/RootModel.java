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

import static edu.buffalo.cse.green.GreenException.GRERR_NULL;
import static edu.buffalo.cse.green.GreenException.GRERR_USING_DEFAULT_PACKAGE;
import static edu.buffalo.cse.green.GreenException.GRERR_WRONG_SOURCE_PROJECT;
import static edu.buffalo.cse.green.GreenException.GRWARN_ELEMENT_IN_WRONG_EDITOR;
import static edu.buffalo.cse.green.constants.XMLConstants.XML_GREEN_VERSION;
import static edu.buffalo.cse.green.constants.XMLConstants.XML_UML;
import static edu.buffalo.cse.green.editor.controller.PropertyChange.GenerateRelationship;
import static edu.buffalo.cse.green.editor.controller.PropertyChange.UpdateRelationships;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.draw2d.geometry.Point;
import org.eclipse.jdt.core.IClassFile;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;

import edu.buffalo.cse.green.GreenException;
import edu.buffalo.cse.green.PlugIn;
import edu.buffalo.cse.green.editor.DiagramEditor;
import edu.buffalo.cse.green.editor.controller.RelationshipPart;
import edu.buffalo.cse.green.editor.controller.RootPart;
import edu.buffalo.cse.green.editor.model.commands.DeleteCommand;
import edu.buffalo.cse.green.relationships.RelationshipCache;
import edu.buffalo.cse.green.xml.XMLConverter;

import static edu.buffalo.cse.green.constants.CodeConstants.Model.*;

/**
 * The top level model in the diagram. Contains notes, compilation units, types,
 * and relationships. Also contains mappings from models to elements and from
 * elements to models.
 * 
 * @author bcmartin
 */
public class RootModel extends AbstractModel<AbstractModel, AbstractModel, IJavaElement> {
	private IJavaProject _project;
	private RootModelCache _cache;
	private List<RelationshipModel> _relationshipModels;
	private RelationshipCache _relationships;

	public RootModel() {
		super();
		_cache = new RootModelCache();
		_relationships = new RelationshipCache();
		_relationshipModels = new ArrayList<RelationshipModel>();
	}

	/**
	 * @return The cache of relationships.
	 */
	public RelationshipCache getRelationshipCache() {
		return _relationships;
	}
	
	/**
	 * Returns the root model (this). This method is necessary because the
	 * children call it recursively on their parents. Overriding it here
	 * prevents a NPE (this model has no parent).
	 */
	@Override
	public RootModel getRootModel() {
		return this;
	}

	/**
	 * Handles removal of the given <code>TypeModel</code> from the root.
	 * 
	 * @param model - The <code>TypeModel</code> to remove.
	 */
	protected void removeChildModel(TypeModel model) {
		List<RelationshipModel> edges = new ArrayList<RelationshipModel>();
		edges.addAll(model.getIncomingEdges());
		edges.addAll(model.getOutgoingEdges());
		
		for (RelationshipModel rModel : edges) {
			rModel.removeFromParent();
		}
	}

	/**
	 * Handles removal of the given <code>RelationshipModel</code> from the
	 * root.
	 * 
	 * @param model - The <code>RelationshipModel</code> to remove.
	 */
	protected void removeChildModel(RelationshipModel model) {
		getRelationshipCache().removeRelationshipModel(model);
		_relationshipModels.remove((RelationshipModel) model);
		removeChild(model);
	}
	
	/**
	 * Fires the property that generates the code for a relationship.
	 * 
	 * @param rModel - The relationship to generate code for.
	 */
	public void generateRelationshipCode(RelationshipModel rModel) {
		firePropertyChange(GenerateRelationship, null, rModel);
	}

	/**
	 * Gets a list of all the relationships in the diagram, even the hidden ones
	 */
	public List<RelationshipModel> getRelationships() {
		return _relationshipModels;
	}

	/**
	 * Hides all relationships of the given type.
	 * 
	 * @param partClass
	 *            The type of relationship to hide ie. CompositionModel. klass
	 *            must be either RelationshipModel or a descendent of it.
	 *            
	 * @author zgwang
	 */
	public void hideRelationshipsOfType(Class partClass) {
		if (RelationshipPart.class.isAssignableFrom(partClass)) {
			for (RelationshipModel rModel : getRelationshipsOfType(partClass)) {
				rModel.setVisible(false);
				rModel.getSourceModel().addImplicitRelationship(rModel);
				rModel.getTargetModel().addImplicitRelationship(rModel);
			}
		} else {
			GreenException.illegalOperation("Wrong type of object specified");
		}
	}

	/**
	 * @param partClass - The class of the edit part representing the
	 * relationship type
	 * @return all relationships the have the given part class
	 */
	public List<RelationshipModel> getRelationshipsOfType(Class partClass) {
		List<RelationshipModel> relationships =
			new ArrayList<RelationshipModel>();
		
		for (RelationshipModel rModel : getRelationships()) {
			if (rModel.getPartClass().equals(partClass)) {
				relationships.add(rModel);
			}
		}
		
		return relationships;
	}
	
	/**
	 * Shows all relationships of the given type.
	 * 
	 * @param partClass
	 *            The type of relationship to show ie. CompositionPart. klass
	 *            must be either RelationshipPart or a decendent of it.
	 *            
	 * @author zgwang
	 */
	public void showRelationshipsOfType(Class partClass) {
		if (RelationshipPart.class.isAssignableFrom(partClass)) {
			for (RelationshipModel rModel : getRelationshipsOfType(partClass)) {
				rModel.setVisible(true);
				rModel.getSourceModel().removeImplicitRelationship(rModel);
				rModel.getTargetModel().removeImplicitRelationship(rModel);
			}
		} else {
			GreenException.illegalOperation("Wrong type of object specified");
		}
	}

	// -Type-------------------------------------------------------------------------
	/**
	 * Gets the <code>TypeModel</code> that represents the given
	 * <code>IType</code>
	 */
	public TypeModel getModelFromType(IType type) {
		return (TypeModel) _cache.getModel(type);
	}

	/**
	 * Creates a model representing the given <code>IType</code>.
	 * 
	 * @param type
	 *            The <code>IType</code> to model
	 * @return A <code>TypeModel</code> representing the given
	 *         <code>IType</code>
	 */
	public TypeModel createTypeModel(IType type) {
		TypeModel typeModel = (TypeModel) getModelFromElement(type);
		
		// create the type if it doesn't exist
		if (typeModel == null) {
			// create the type model
			typeModel = new TypeModel(type);

			if (PlugIn.filterMember(typeModel)) {
				removeChild(typeModel);
				return typeModel;
			}
			
			typeModel.setParent(this);

			GreenException.illegalOperation(isValidTypeModel(typeModel));

			// update the fields and models
			typeModel.updateFields();
			typeModel.updateMethods();

			// LOOKINTO Answer why is this needed, since 4 statements up it appears to already have been done...
			// Answer: Null pointer thrown without this statement...possibly a more elegant solution is available
			if (typeModel.getType().isBinary()) {
				// set its parent to the root
				typeModel.setParent(this);
			}

			addChild(typeModel, typeModel.getMember());

			if (_project == null) {
				if (!type.isBinary()) {
					setProject((IJavaProject) type.getAncestor(
							IJavaElement.JAVA_PROJECT));
				}
			} else if (!type.getAncestor(IJavaElement.JAVA_PROJECT).equals(
					_project)) {
				GreenException.warn(GRWARN_ELEMENT_IN_WRONG_EDITOR);
			}
		} else {
			if (PlugIn.filterMember(typeModel)) {
				removeChild(typeModel);
				return typeModel;
			}

			typeModel.setVisible(true);
		}

		// show all relationships attached to this type
		for (RelationshipModel rModel : _relationshipModels) {
			TypeModel rsModel = rModel.getSourceModel();
			TypeModel rtModel = rModel.getTargetModel();
			
			if (rsModel != null && rtModel != null) {
				if (typeModel.equals(rsModel) || typeModel.equals(rtModel)) {
					if (rsModel.isVisible() && rtModel.isVisible()) {
						rModel.setSourceModel(rsModel);
						rModel.setTargetModel(rtModel);
						rModel.setVisible(true);
					}
				}
			}
		}
		
		return typeModel;
	}

	// -IJavaElement <->
	// Model-------------------------------------------------------
	/**
	 * Adds an element-to-model mapping.
	 */
	public void mapElementToModel(IJavaElement element, AbstractModel model) {
		_cache.putModel(element, model);
	}

	/**
	 * Removes the mapping from an element to its model. This will prevent
	 * future update attempts to the specified element.
	 * 
	 * @param element
	 *            The element to unmap.
	 */
	public void unmapElement(IJavaElement element) {
		_cache.removeElement(element);
	}

	/**
	 * Adds a model-to-element mapping.
	 */
	public AbstractModel getModelFromElement(IJavaElement element) {
		return (AbstractModel) _cache.getModel(element);
	}

	// -Load /
	// Save------------------------------------------------------------------
	/**
	 * Recursively-called method that writes the XML to the converter.
	 * 
	 * NOTE: this method should be implemented in all models that will store
	 * information in the XML file.
	 */
	@Override
	public void toXML(XMLConverter converter) {
		converter.pushHeader(XML_UML);
		converter.writeKey(XML_GREEN_VERSION, PlugIn.getVersion());
		super.toXML(converter);
		converter.popHeader();
	}

	// -Misc-------------------------------------------------------------------------
	/**
	 * @return The project being modeled.
	 */
	public IJavaProject getProject() {
		return _project;
	}

	/**
	 * Sets the project being displayed in the editor.
	 */
	public void setProject(IJavaProject project) {
		_project = project;
	}

	/**
	 * @param model - The given <code>TypeModel</code>.
	 * @return true if the <code>TypeModel</code> is valid; false otherwise.
	 */
	public String isValidTypeModel(TypeModel model) {
		if (model.getType() == null) return "Type is null";
		
		return model.getType().getPackageFragment().isDefaultPackage() ?
				GRERR_USING_DEFAULT_PACKAGE : null;
	}

	/**
	 * This interface was copied from
	 * edu.buffalo.cse.testbase.util.UtilityUIFinders. It facilitates in
	 * matching all submodels of a model that are a certain kind of model.
	 * 
	 * @author tomhicks, vigilone
	 */
	private interface ModelFilter {
		/**
		 * Determines if subject is acceptable in this inclusion i.e. do we want
		 * it matched to this filter.
		 * 
		 * @param subject -
		 *            An AbstractModel to be tested for matchability.
		 */
		public abstract boolean isAcceptable(AbstractModel subject);
	}

	/**
	 * Gets all the submodels of a model that are assignable to a certain type.
	 * Also gets the model itself if it is assignable to that type.
	 * 
	 * @param root -
	 *            The root model to start searching from.
	 * @param predicate -
	 *            A class implementing ModelFilter that is used to match root
	 *            and it's subnodes for inclusion in the list.
	 */
	private static List<AbstractModel> getModels(
			AbstractModel root,
			ModelFilter predicate) {
		List<AbstractModel> listToAddTo = new ArrayList<AbstractModel>();
		List<AbstractModel> queueToCheck = new ArrayList<AbstractModel>();
		queueToCheck.add(root);
		while (!queueToCheck.isEmpty()) {
			AbstractModel modelToCheck = (AbstractModel) queueToCheck
					.remove(0);
			if (predicate.isAcceptable(modelToCheck)) {
				listToAddTo.add(modelToCheck);
			}
			List<AbstractModel<?, ?, ?>> children = (ArrayList<AbstractModel<?, ?, ?>>) (List) modelToCheck.getChildren();
			queueToCheck.addAll(children);
		}
		return listToAddTo;
	}

	/**
	 * Gets all models that are of class type.
	 * 
	 * @param root -
	 *            The root model to start searching from.
	 * @param type -
	 *            The type to be matched for. Must be an instance of an
	 *            AbstractModel.
	 */
	public static List<AbstractModel> getModels(
			AbstractModel root,
			final Class type) {
		return (List<AbstractModel>) getModels(root, new ModelFilter() {
			/**
			 * @see edu.buffalo.cse.green.editor.model.RootModel.ModelFilter#isAcceptable(edu.buffalo.cse.green.editor.model.AbstractModel)
			 */
			public boolean isAcceptable(AbstractModel modelToCheck) {
				return type.isInstance(modelToCheck);
			}
		});
	}

	/**
	 * @return The list of <code>IClassFile</code>s contained in the root.
	 */
	public List<IClassFile> getClassFiles() {
		List<IClassFile> classFiles = new ArrayList<IClassFile>();

		for (AbstractModel model : getModels(this, TypeModel.class)) {
			TypeModel typeModel = (TypeModel) model;

			if (typeModel.getType().isBinary()) {
				classFiles.add((IClassFile) typeModel.getType().getAncestor(
						IJavaElement.CLASS_FILE));
			}
		}

		return classFiles;
	}

	/**
	 * @see edu.buffalo.cse.green.editor.model.AbstractModel#getPartClass()
	 */
	@Override
	public Class getPartClass() {
		return RootPart.class;
	}

	/**
	 * @see edu.buffalo.cse.green.editor.model.AbstractModel#handleDispose()
	 */
	@Override
	public void handleDispose() {
		// do nothing
	}
	
	/**
	 * @param sourceProject -
	 *            The old project
	 * @param targetProject -
	 *            The new project
	 */
	public void changeProjectElement(
			IJavaProject sourceProject,
			IJavaProject targetProject) {
		if (!_project.equals(sourceProject)) {
			GreenException.illegalOperation(GRERR_WRONG_SOURCE_PROJECT);
		}

		_project = targetProject;
	}

	/**
	 * @see edu.buffalo.cse.green.editor.model.AbstractModel#getDeleteCommand(edu.buffalo.cse.green.editor.DiagramEditor)
	 */
	@Override
	public DeleteCommand getDeleteCommand(DiagramEditor editor) {
		return null;
	}

	/**
	 * @see edu.buffalo.cse.green.editor.model.AbstractModel#getJavaElement()
	 */
	@Override
	public IJavaElement getJavaElement() {
		return null;
	}

	/**
	 * Maps <code>IJavaElement</code>s to their corresponding
	 * <code>AbstractModel</code>s.
	 * 
	 * @author bcmartin
	 */
	public class RootModelCache {
		/**
		 * Maps <code>IJavaElement</code>s to their corresponding models.
		 */
		private HashMap<String, AbstractModel> _elementMapToModel;
		
		public RootModelCache() {
			_elementMapToModel = new HashMap<String, AbstractModel>();
		}

		/**
		 * Adds an element and model to the mapping.
		 * 
		 * @param element - The element.
		 * @param model - The model.
		 */
		public void putModel(IJavaElement element, AbstractModel model) {
			if (element == null) {
				GreenException.illegalOperation(GRERR_NULL);
			}
			
			if (model == null) {
				GreenException.illegalOperation(GRERR_NULL);
			}
			
			putModelWithElement(element, model);
		}

		/**
		 * @return A set of all the elements in the editor.
		 */
		public Set<IJavaElement> getElements() {
			Set<String> handles = _elementMapToModel.keySet();
			Set<IJavaElement> elements = new HashSet<IJavaElement>();
			
			for (String handle : handles) {
				elements.add(JavaCore.create(handle));
			}
			
			return elements;
		}

		/**
		 * @param element - The given element.
		 * @return The corresponding model.
		 */
		public AbstractModel getModel(IJavaElement element) {
			if (element == null) {
				GreenException.illegalOperation(GRERR_NULL);
			}
			
			return (AbstractModel) _elementMapToModel.get(
					element.getHandleIdentifier());
		}

		/**
		 * Maps the given element to the given model.
		 * 
		 * @param element - The element.
		 * @param model - The model.
		 */
		private void putModelWithElement(
				IJavaElement element,
				AbstractModel model) {
			_elementMapToModel.put(element.getHandleIdentifier(), model);
		}

		/**
		 * Removes an element from the mapping.
		 * 
		 * @param element - The element.
		 * @return The corresponding model.
		 */
		private AbstractModel removeElement(IJavaElement element) {
			return (AbstractModel) _elementMapToModel.remove(
					element.getHandleIdentifier());
		}
	}

	/**
	 * Adds a <code>NoteModel</code> to the root.
	 * 
	 * @param model - The <code>NoteModel</code>.
	 */
	public void addChild(NoteModel model) {
		addChild(model, null);
	}

	/**
	 * Adds a <code>RelationshipModel</code> to the root.
	 * 
	 * @param model - The <code>RelationshipModel</code>.
	 */
	public void addChild(RelationshipModel model) {
		TypeModel sourceModel = model.getSourceModel();
		TypeModel targetModel = model.getTargetModel();
		
		_relationshipModels.add(model);
		addChild(model, null);
		
		model.setSourceModel(sourceModel);
		model.setTargetModel(targetModel);
		sourceModel.addOutgoingEdge(model);
		targetModel.addIncomingEdge(model);
	}

	/**
	 * @see edu.buffalo.cse.green.editor.model.AbstractModel#removeFromParent()
	 */
	@Override
	public void removeFromParent() {
		GreenException.illegalOperation("Cannot remove root model");
	}

	/**
	 * Updates the relationships in the editor.
	 */
	public void updateRelationships() {
		firePropertyChange(UpdateRelationships);
	}
	
	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return _project.getElementName();
	}
	
	/**
	 * @param kind - The kind of element to find.
	 * @return All elements in the editor of the specified kind.
	 */
	public List<IJavaElement> getElementsOfKind(int kind) {
		Set<String> ids = new HashSet<String>();
		List<IJavaElement> elements = new ArrayList<IJavaElement>();
		
		for (IJavaElement element : _cache.getElements()) {
			if (element == null) continue;
			
			IJavaElement ancestor = element.getAncestor(kind);
			
			if (ancestor != null) {
				ids.add(ancestor.getHandleIdentifier());
			}
		}
		
		for (String id : ids) {
			elements.add(JavaCore.create(id));
		}
		
		return elements;
	}

	/**
	 * @param ancestor - The given element.
	 * @return true if the given element is an ancestor of another element in
	 * the editor; false otherwise.
	 */
	public boolean ancestorInEditor(IJavaElement ancestor) {
		List<IJavaElement> elements =
			getElementsOfKind(ancestor.getElementType());

		for (IJavaElement element : elements) {
			if (element.getHandleIdentifier().equals(
					ancestor.getHandleIdentifier())) {
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * Our default layout
	 * 
	 * TODO Need better layout. (Auto-arrange)
	 * (@see edu.buffalo.cse.green.editor.model.RootModel#placeUMLBox(edu.buffalo.cse.green.editor.model.TypeModel typeModel))
	 * 
	 * Graph embedding algorithms for optimal layout run in exponential time;
	 * the optimization problem is NP-hard. This layout is subject to change at
	 * any time.
	 */
	public void placeUMLBox(TypeModel model) {

		for (int y = 0; y <= MAX_BOX_HEIGHT; y += DEFAULT_BOX_HEIGHT) {
			for (int x = 0; x <= MAX_BOX_WIDTH; x += DEFAULT_BOX_WIDTH) {
				boolean fail = false;

				for (AbstractModel element : getChildren()) {
					if (element instanceof TypeModel) {
						TypeModel sibling = (TypeModel) element;
						if (!sibling.equals(model)
								&& (sibling.getLocation().x > x
										- (DEFAULT_BOX_WIDTH / 2))
								&& (sibling.getLocation().x < x
										+ (DEFAULT_BOX_WIDTH / 2))
								&& (sibling.getLocation().y > y
										- (DEFAULT_BOX_HEIGHT / 2))
								&& (sibling.getLocation().y < y
										+ (DEFAULT_BOX_HEIGHT / 2))) {
							fail = true;
							break;
						}
					}
				}

				if (!fail) {
					model.setLocation(new Point(x, y));
					return;
				}
			}
		}

		model.setLocation(new Point(DEFAULT_X_LOCATION, DEFAULT_Y_LOCATION));
	}
	
	/**
	 * @see edu.buffalo.cse.green.editor.model.AbstractModel#refresh()
	 */
	@Override
	public void refresh() {
		updateTypes();
		
		super.refresh();
	}

	/**
	 * Updates the visibility of <code>TypeModel</code>s contained in the
	 * editor. The visibility may change because of added or applied filters.
	 */
	private void updateTypes() {
		for (TypeModel typeModel
				: (AbstractList<TypeModel>) (List) getChildren(TypeModel.class)) {
			if (PlugIn.filterMember(typeModel)) {
				typeModel.removeFromParent();
			}
		}
	}
}