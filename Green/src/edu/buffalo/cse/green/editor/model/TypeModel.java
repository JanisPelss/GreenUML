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


import static edu.buffalo.cse.green.GreenException.GRERR_TYPE_UNSUPPORTED;
import static edu.buffalo.cse.green.constants.CodeConstants.Model.DEFAULT_X_LOCATION;
import static edu.buffalo.cse.green.constants.CodeConstants.Model.DEFAULT_Y_LOCATION;
import static edu.buffalo.cse.green.constants.XMLConstants.XML_TYPE;
import static edu.buffalo.cse.green.constants.XMLConstants.XML_TYPE_HEIGHT;
import static edu.buffalo.cse.green.constants.XMLConstants.XML_TYPE_NAME;
import static edu.buffalo.cse.green.constants.XMLConstants.XML_TYPE_WIDTH;
import static edu.buffalo.cse.green.constants.XMLConstants.XML_TYPE_X;
import static edu.buffalo.cse.green.constants.XMLConstants.XML_TYPE_Y;
import static edu.buffalo.cse.green.editor.controller.PropertyChange.IncomingRelationship;
import static edu.buffalo.cse.green.editor.controller.PropertyChange.OutgoingRelationship;
import static edu.buffalo.cse.green.editor.controller.PropertyChange.Refresh;
import static edu.buffalo.cse.green.preferences.PreferenceInitializer.P_DISPLAY_FQN_TYPE_NAMES;
import static org.eclipse.jdt.ui.refactoring.RenameSupport.UPDATE_REFERENCES;

import java.lang.reflect.Constructor;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.palette.ToolEntry;
import org.eclipse.jdt.core.Flags;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jdt.ui.refactoring.RenameSupport;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchWindow;

import edu.buffalo.cse.green.GreenException;
import edu.buffalo.cse.green.PlugIn;
import edu.buffalo.cse.green.dialogs.wizards.NewElementWizard;
import edu.buffalo.cse.green.editor.DiagramEditor;
import edu.buffalo.cse.green.editor.action.ContextAction;
import edu.buffalo.cse.green.editor.controller.TypePart;
import edu.buffalo.cse.green.editor.model.commands.DeleteCommand;
import edu.buffalo.cse.green.editor.model.commands.DeleteTypeCommand;
import edu.buffalo.cse.green.editor.model.commands.HideTypeCommand;
import edu.buffalo.cse.green.types.ITypeProperties;
import edu.buffalo.cse.green.xml.XMLConverter;

//Compartment visibility stuff
//import org.eclipse.draw2d.geometry.Rectangle;
//import edu.buffalo.cse.green.editor.DiagramEditor;
//import static edu.buffalo.cse.green.editor.controller.PropertyChange.Size;
//import edu.buffalo.cse.green.editor.controller.AbstractPart;
//import edu.buffalo.cse.green.editor.controller.CompartmentPart;
//import edu.buffalo.cse.green.editor.model.commands.SetConstraintCommand;

/**
 * Represents a type in the model hierarchy.
 * 
 * @author bcmartin
 */
public class TypeModel extends MemberModel<CompartmentModel, RootModel, IType> {
	private HashSet<RelationshipModel> _incomingEdges;

	private HashSet<RelationshipModel> _outgoingEdges;

	private HashSet<RelationshipModel> _implicitRelationships;

	private CompartmentModel _nameCompartment;

	private CompartmentModel _fieldCompartment;

	private CompartmentModel _methodCompartment;
	
	
	//Visibility of individual compartments was an unsuccessful attempt
	//at showing portions of the diagram.  Zero-sized compartments
	//created much more problems than they're worth, so the solution
	//for smaller class boxes was made with filters
//	private boolean _fieldsAreVisible;
//	
//	private boolean _methodsAreVisible;

	public TypeModel() {
		this(null);
	}

	protected TypeModel(IType type) {
		super(type);
		
		// set defaults
//		_fieldsAreVisible = true;
//		_methodsAreVisible = true;
		_incomingEdges = new HashSet<RelationshipModel>();
		_outgoingEdges = new HashSet<RelationshipModel>();
		_implicitRelationships = new HashSet<RelationshipModel>();
		_nameCompartment = CompartmentModel.newTypeCompartment();
		setLocation(new Point(DEFAULT_X_LOCATION, DEFAULT_Y_LOCATION));
		setSize(new Dimension(-1, -1));

		// add the box for the name
		addChild(_nameCompartment);

		if (type != null) {
			ITypeProperties properties = getTypeProperties(type);

			// add the box for fields
			if (properties.hasFieldCompartment()) {
				_fieldCompartment = CompartmentModel.newFieldCompartment();
				addChild(_fieldCompartment);
			}

			// add the box for methods
			if (properties.hasMethodCompartment()) {
				_methodCompartment = CompartmentModel.newMethodCompartment();
				addChild(_methodCompartment);
			}
		}
	}

//	/**
//	 * Sets the visibility of individual compartments
//	 * 
//	 * @param field true to show fields
//	 * @param method true to show methods
//	 */
//	public void setCompartmentVisibility(boolean field, boolean method) {
////		_fieldsAreVisible = field;
////		_methodsAreVisible = method;
//		if(_fieldCompartment != null) {
////			SetConstraintCommand command = new SetConstraintCommand(_fieldCompartment);
//			if(field) {
//				System.out.println("=================================================");
//				System.out.println("Size: " + _fieldCompartment.getSize());
//				System.out.println("Bounds:" + _fieldCompartment.getBounds());
//				System.out.println("Drawn Size: " + _fieldCompartment.getDrawnSize());
//				_fieldCompartment.setSize(new Dimension(0, 0));
//				this.forceRefesh();
////				this.setSize(width, height)
////				this.
////				_fieldCompartment.setVisible(true);
////				_fieldCompartment.setDrawnSize(new Dimension(0, 0));
//				CompartmentPart p = (CompartmentPart) DiagramEditor.getActiveEditor().getRootPart().getPartFromModel(_fieldCompartment);
//				p.getFigure().setSize(new Dimension(0, 0));
//				
//				
////				_fieldCompartment.removeChildren();
//				System.out.println("-------------------------------------------------");
//				System.out.println("Size: " + _fieldCompartment.getSize());
//				System.out.println("Bounds:" + _fieldCompartment.getBounds());
//				System.out.println("Drawn Size: " + _fieldCompartment.getDrawnSize());
//				_fieldCompartment.refresh();
////				_fieldCompartment.firePropertyChange(Size, _fieldCompartment.getSize(), new Dimension( -1, -1));
////				updateFields();
////				command.setBounds(new Rectangle(_fieldCompartment.getLocation(), new Dimension(-1, -1)));
//				this.refresh();
//			}
//			else {
//				_fieldCompartment.setVisible(false);
////				_fieldCompartment.setDrawnSize(new Dimension(0, 0));
////				_fieldCompartment.se
//				_fieldCompartment.setSize(new Dimension(0, 0));
////				_fieldCompartment.firePropertyChange(Size, _fieldCompartment.getSize(), new Dimension( 0, 0));
////				updateFields();
////				command.setBounds(new Rectangle(_fieldCompartment.getLocation(), new Dimension(0, 0)));
//				this.refresh();
//			}
////			DiagramEditor.getActiveEditor().execute(command);
//				
//		}
//		if(_methodCompartment != null) {
//			SetConstraintCommand command = new SetConstraintCommand(_methodCompartment);
//			if(method) {
////				_methodCompartment.setSize(new Dimension(-1, -1));
//				_methodCompartment.setVisible(true);
////				command.setBounds(new Rectangle(_methodCompartment.getLocation(), new Dimension(-1, -1)));
//			}
//			else {
////				_methodCompartment.setSize(new Dimension(0, 0));
//				_methodCompartment.setVisible(false);
////				command.setBounds(new Rectangle(_methodCompartment.getLocation(), new Dimension(0, 0)));
//			}
////			DiagramEditor.getActiveEditor().execute(command);
//		}
//	}
	
	/**
	 * Adds a <code>CompartmentModel</code> child.
	 * 
	 * @param compartment - The child.
	 */
	private void addChild(CompartmentModel compartment) {
		addChild(compartment, null);
	}

	/**
	 * Adds a <code>FieldModel</code> child.
	 * 
	 * @param model - The child.
	 */
	public void addChild(FieldModel model) {
		CompartmentModel cm = getFieldCompartmentModel();
		if (cm == null) return;
		
		if (!PlugIn.filterMember(model)) {
			cm.addChild(model);
		} else {
			cm.removeChild(model);
		}
	}
	
	/**
	 * Adds a <code>MethodModel</code> child.
	 * 
	 * @param model - The child.
	 */
	public void addChild(MethodModel model) {
		if (!PlugIn.filterMember(model)) {
			getMethodCompartmentModel().addChild(model);
		} else {
			getMethodCompartmentModel().removeChild(model);
		}
	}
	
	/**
	 * @return The caption to display on the label for the type. The display
	 * name will be either the simple name or the fully qualified name, which is
	 * at the user's discretion.
	 */
	public String getDisplayName() {
		boolean fqn = PlugIn.getBooleanPreference(P_DISPLAY_FQN_TYPE_NAMES);

		return fqn ? getType().getFullyQualifiedName() :
			getType().getElementName();
	}

	/**
	 * Updates the methods compartment.
	 */
	public void updateMethods() {
		//FIXME Method filter causes NPE in this method
		//Must look into this to make sure the solution of destroying the
		//old compartment and making new ones doesn't **** up...create
		//grossly undesirable side effects in...our underlying model. 

//		if(!_methodsAreVisible) return;

		CompartmentModel compartment = getMethodCompartmentModel();
		if (compartment == null) return;
		compartment.removeChildren();
//		removeChild(_methodCompartment);
//		_methodCompartment = CompartmentModel.newMethodCompartment();
//		addChild(_methodCompartment);
		
		try {
			IMethod[] methods = getType().getMethods();

			for (int j = 0; j < methods.length; j++) {
				MethodModel mModel = new MethodModel(methods[j]);
				addChild(mModel);
			}
		} catch (JavaModelException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Updates the fields compartment.
	 */
	public void updateFields() {
		//FIXME Field filter causes NPE in this method
		//Must look into this to make sure the solution of destroying the
		//old compartment and making new ones doesn't **** up...create
		//grossly undesirable side effects in...our underlying model. 

//		if(!_fieldsAreVisible) return;

		CompartmentModel compartment = getFieldCompartmentModel();
		if (compartment == null) return;
		compartment.removeChildren();
//		removeChild(_fieldCompartment);
//		_fieldCompartment = CompartmentModel.newFieldCompartment();
//		addChild(_fieldCompartment);
		
		try {
			IField[] fields = getType().getFields();

			for (int j = 0; j < fields.length; j++) {
				FieldModel fModel = new FieldModel(fields[j]);
				addChild(fModel);
			}
		} catch (JavaModelException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Gets a set of incoming edges.
	 */
	public Set<RelationshipModel> getIncomingEdges() {
		return _incomingEdges;
	}

	/**
	 * Gets a set of outgoing edges.
	 */
	public Set<RelationshipModel> getOutgoingEdges() {
		return _outgoingEdges;
	}

	/**
	 * Adds an incoming <code>RelationshipModel</code> edge.
	 * 
	 * @param relationshipModel - The edge to add.
	 */
	public void addIncomingEdge(RelationshipModel relationshipModel) {
		_incomingEdges.add(relationshipModel);
		firePropertyChange(IncomingRelationship, null, relationshipModel);
	}

	/**
	 * Removes an incoming <code>RelationshipModel</code> edge.
	 * 
	 * @param relationshipModel - The edge to add.
	 */
	public void removeIncomingEdge(RelationshipModel relationshipModel) {
		if (_incomingEdges.contains(relationshipModel)) {
			_incomingEdges.remove(relationshipModel);
			firePropertyChange(IncomingRelationship, relationshipModel,
					null);
		}
	}

	/**
	 * Adds an outgoing <code>RelationshipModel</code> edge.
	 * 
	 * This method is used by DiagramRootModel.addRelationship(...) and should
	 * not be called by other methods.
	 */
	public void addOutgoingEdge(RelationshipModel edge) {
		_outgoingEdges.add(edge);
		firePropertyChange(OutgoingRelationship, null, edge);
	}

	/**
	 * Removes an outgoing <code>RelationshipModel</code> edge.
	 * 
	 * This method is used by DiagramRootModel.addRelationship(...) and should
	 * not be called by other methods.
	 */
	public void removeOutgoingEdge(RelationshipModel edge) {
		if (_outgoingEdges.remove(edge)) {
			firePropertyChange(OutgoingRelationship, edge, null);
		}
	}

	/**
	 * @see edu.buffalo.cse.green.editor.model.AbstractModel#toXML(edu.buffalo.cse.green.xml.XMLConverter)
	 */
	public void toXML(XMLConverter converter) {
		//LOOKINTO [Can be removed if refactoring is done through extension point.] Refactoring in DIA files, this writes handle to XML.
		converter.pushHeader(XML_TYPE);
		converter.writeKey(XML_TYPE_NAME, getType().getHandleIdentifier());
		converter.writeKey(XML_TYPE_HEIGHT, "" + getSize().height);
		converter.writeKey(XML_TYPE_WIDTH, "" + getSize().width);
		converter.writeKey(XML_TYPE_X, "" + getLocation().x);
		converter.writeKey(XML_TYPE_Y, "" + getLocation().y);
		super.toXML(converter);
		converter.popHeader();
	}

	/**
	 * @return The list of hidden relationships.
	 */
	public Set<RelationshipModel> getImplicitRelationships() {
		return _implicitRelationships;
	}

	/**
	 * Makes a relationship implicit.
	 * 
	 * @param rModel - The relationship.
	 */
	public void addImplicitRelationship(RelationshipModel rModel) {
		_implicitRelationships.add(rModel);
		firePropertyChange(Refresh);
	}

	/**
	 * Makes a relationship explicit.
	 * 
	 * @param rModel - The relationship.
	 */
	public void removeImplicitRelationship(RelationshipModel rModel) {
		_implicitRelationships.remove(rModel);

		if (_implicitRelationships.size() == 0) {
			firePropertyChange(Refresh);
		}
	}

	/**
	 * @return The <code>IType</code> modeled by this class.
	 */
	public IType getType() {
		return (IType) getMember();
	}

	/**
	 * @return The compartment that contains the label that holds the name of
	 * the type.
	 */
	public CompartmentModel getNameCompartmentModel() {
		return _nameCompartment;
	}

	/**
	 * @return The compartment that contains the label that holds the fields of
	 * the type.
	 */
	public CompartmentModel getFieldCompartmentModel() {
		return _fieldCompartment;
	}

	/**
	 * @return The compartment that contains the label that holds the methods of
	 * the type.
	 */
	public CompartmentModel getMethodCompartmentModel() {
		return _methodCompartment;
	}

	/**
	 * @return 'true' if the modeled type is a class, false otherwise
	 */
	public boolean isClass() {
		if (!getMember().exists()) return false;

		try {
			return getType().isClass();
		} catch (JavaModelException e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * @return True if the modeled type is a class, false otherwise.
	 */
	public boolean isInterface() throws JavaModelException {
		if (!getMember().exists()) return false;

		return getType().isInterface();
	}

	/**
	 * @return True if the modeled type is abstract, false otherwise. 
	 */
	public boolean isAbstract() throws JavaModelException {
		if (!getMember().exists()) return false;
		
		return Flags.isAbstract(getType().getFlags());
	}

	/**
	 * @see edu.buffalo.cse.green.editor.model.AbstractModel#getContextMenuFlag()
	 */
	public int getContextMenuFlag() {
		return ContextAction.CM_TYPE;
	}

	/**
	 * @see edu.buffalo.cse.green.editor.model.AbstractModel#removeFromParent()
	 */
	public void removeFromParent() {
		removeChildren();
		getRootModel().removeChildModel(this);
		
		super.removeFromParent();
	}

	/**
	 * @see edu.buffalo.cse.green.editor.model.AbstractModel#getDeleteCommand(edu.buffalo.cse.green.editor.DiagramEditor)
	 */
	public DeleteCommand getDeleteCommand(DiagramEditor editor) {
		return new DeleteTypeCommand(this);
	}

	/**
	 * @param editor - The <code>DiagramEditor</code> containing this model.
	 * 
	 * @return A command to hide this model.
	 */
	public Command getHideCommand(DiagramEditor editor) {
		return new HideTypeCommand(this);
	}

	/**
	 * @see edu.buffalo.cse.green.editor.model.AbstractModel#getPartClass()
	 */
	public Class getPartClass() {
		return TypePart.class;
	}

	/**
	 * @see edu.buffalo.cse.green.editor.model.AbstractModel#setVisible(boolean)
	 */
	public void setVisible(boolean value) {
		super.setVisible(value);
		
		if (value) {
			for (RelationshipModel rModel : getIncomingEdges()) {
				rModel.setVisible(true);
			}
			
			for (RelationshipModel rModel : getOutgoingEdges()) {
				rModel.setVisible(true);
			}
		}
	}
	
	/**
	 * @see edu.buffalo.cse.green.editor.model.AbstractModel#getTypeModel()
	 */
	public TypeModel getTypeModel() {
		return this;
	}

	/**
	 * @see edu.buffalo.cse.green.editor.model.MemberModel#getRenameSupport()
	 */
	public RenameSupport getRenameSupport() throws CoreException {
		return RenameSupport.create(getType(), "", UPDATE_REFERENCES);
	}

	/**
	 * @see edu.buffalo.cse.green.editor.model.AbstractModel#invokeCreationDialog(org.eclipse.gef.palette.ToolEntry)
	 */
	public int invokeCreationDialog(ToolEntry tool) {
		ITypeProperties properties = getTypeProperties(
				tool.getLabel());
		Class klass = properties.getDialogClass();
		Constructor<?> constructor = klass.getConstructors()[0];

		if (constructor.getParameterTypes().length != 0) {
			GreenException.illegalOperation(
					"Constructor must not have any parameters");
		}

		try {
			NewElementWizard wizard =
				(NewElementWizard) constructor.newInstance(new Object[] {});
			wizard.setModel(this);
			wizard.init(PlugIn.getDefault().getWorkbench(),
					getCurrentSelection());
			WizardDialog dialog =
				new WizardDialog(PlugIn.getDefaultShell(), wizard);
			dialog.setMinimumPageSize(300, 500);
			dialog.create();
			dialog.open();

			return dialog.getReturnCode();
		} catch (Exception e) {
			e.printStackTrace();
			return WizardDialog.CANCEL;
		}
	}
	
	/**
	 * @see edu.buffalo.cse.green.editor.model.AbstractModel#createNewInstance(edu.buffalo.cse.green.editor.model.AbstractModel)
	 */
	public void createNewInstance(AbstractModel model) {
		RootModel root = model.getRootModel();
		TypeModel typeModel = (TypeModel) model;
		typeModel = root.createTypeModel(typeModel.getType());
		typeModel.setLocation(model.getLocation());
		typeModel.setSize(model.getSize());
		root.updateRelationships();
	}
	
	/**
	 * @return The current selection.
	 */
	private IStructuredSelection getCurrentSelection() {
		IWorkbenchWindow[] windows = PlugIn.getDefault().getWorkbench()
				.getWorkbenchWindows();

		for (int i = 0; i < windows.length; i++) {
			IViewPart packExplorer = windows[i].getPages()[0]
					.findView(JavaUI.ID_PACKAGES);

			if (packExplorer != null) {
				ISelection selection = (ISelection) packExplorer.getViewSite()
						.getSelectionProvider().getSelection();

				if (selection instanceof IStructuredSelection) { return (IStructuredSelection) selection; }
			}
		}

		return null;
	}
	
	/**
	 * @see edu.buffalo.cse.green.editor.model.AbstractModel#assertValid()
	 */
	public void assertValid() {
		GreenException.illegalOperation(getRootModel().isValidTypeModel(this));
	}
	
	/**
	 * @see edu.buffalo.cse.green.editor.model.AbstractModel#refresh()
	 */
	public void refresh() {
		if (getMember().exists()) {
			updateFields();
			updateMethods();
		}

		super.refresh();
	}
	
	/**
	 * @param type - The given <code>IType</code>.
	 * @return The plugin class that specifies properties of how the given type
	 * integrates with Green's editor.  
	 */
	private static ITypeProperties getTypeProperties(IType type) {
		return getTypeProperties(getTypeAsString(type));
	}
	
	/**
	 * @param key - The <code>String</code> representation of the kind of type
	 * (e.g. class, enum).
	 * @return The plugin class that specifies properties of how the given type
	 * integrates with Green's editor.  
	 */
	private static ITypeProperties getTypeProperties(String key) {
		ITypeProperties properties = PlugIn.getTypeProperties().get(key);
		
		if (properties == null) {
			GreenException.illegalOperation(GRERR_TYPE_UNSUPPORTED + ":\nkey= " + key);
		}
		
		return properties;
	}

	/**
	 * @param type - The given <code>IType</code>.
	 * @return A string representation of the kind of type (e.g. class, enum).
	 */
	private static String getTypeAsString(IType type) {
		Collection<ITypeProperties> typeP = PlugIn.getAvailableTypes();
		if (typeP.isEmpty()) {
			GreenException.critical(new GreenException("typeP is empty"));
			return null;
		} else {
			GreenException.warn("typeP="+typeP.toString());
		}
		for (ITypeProperties typeProp : PlugIn.getAvailableTypes()) {
			if (typeProp.supportsType(type)) {
				return typeProp.getLabel();
			}
		}
		
		GreenException.illegalOperation(GRERR_TYPE_UNSUPPORTED + ":\nIType= " + type.getFullyQualifiedName());
		return null;
	}
}