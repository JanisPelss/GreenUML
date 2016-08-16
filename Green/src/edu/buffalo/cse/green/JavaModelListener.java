/* This file is part of Green.
 *
 * Copyright (C) 2005 The Research Foundation of State University of New York
 * All Rights Under Copyright Reserved, The Research Foundation of S.U.N.Y.
 * 
 * Green is free software, licensed under the terms of the Eclipse
 * Public License, version 1.0.  The license is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package edu.buffalo.cse.green;

import static org.eclipse.jdt.core.IJavaElement.COMPILATION_UNIT;
import static org.eclipse.jdt.core.IJavaElement.JAVA_PROJECT;
import static org.eclipse.jdt.core.IJavaElement.PACKAGE_FRAGMENT;
import static org.eclipse.jdt.core.IJavaElement.TYPE;
import static org.eclipse.jdt.core.IJavaElementDelta.ADDED;
import static org.eclipse.jdt.core.IJavaElementDelta.REMOVED;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jdt.core.ElementChangedEvent;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IElementChangedListener;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaElementDelta;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.core.CompilationUnit;
import org.eclipse.jdt.internal.core.JavaProject;
import org.eclipse.jdt.internal.core.PackageFragment;
import org.eclipse.jdt.internal.core.SourceField;
import org.eclipse.jdt.internal.core.SourceMethod;
import org.eclipse.jdt.internal.core.SourceType;

import edu.buffalo.cse.green.editor.DiagramEditor;
import edu.buffalo.cse.green.editor.controller.FieldPart;
import edu.buffalo.cse.green.editor.controller.MethodPart;
import edu.buffalo.cse.green.editor.controller.RootPart;
import edu.buffalo.cse.green.editor.model.AbstractModel;
import edu.buffalo.cse.green.editor.model.FieldModel;
import edu.buffalo.cse.green.editor.model.MethodModel;
import edu.buffalo.cse.green.editor.model.RootModel;
import edu.buffalo.cse.green.editor.model.TypeModel;

/**
 * Handles changes to the <code>JavaModel</code>, reflecting them in the
 * affected editors.
 * 
 * The elementChanged() method is passed in a <code>JavaElementDelta</code>
 * containing a tree structure with information about the change that occurred
 * in the <code>JavaModel</code>.
 * 
 * Helper methods are called that traverse the delta structure and find changes
 * that correspond to the addition, removal, or movement of certain element
 * types. Once these changes are detected, each editor is checked to see if the
 * changes affect it. If they do, the editor's model's references to the changed
 * elements are updated. Refresh methods are then called on the appropriate edit
 * parts to update the editor's display.
 * 
 * @author bcmartin
 */
public class JavaModelListener
implements IElementChangedListener {
	private static JavaModelListener _listener = new JavaModelListener();
	private static Map<Class, RefactorHandler> map;
	
	static {
		// add the elements to consider changes for to a list
		map = new HashMap<Class, RefactorHandler>();
		map.put(JavaProject.class, ProjectRefactorHandler.instance());
		map.put(PackageFragment.class, PackageRefactorHandler.instance());
		map.put(CompilationUnit.class,
				CompilationUnitRefactorHandler.instance());
		map.put(SourceType.class, TypeRefactorHandler.instance());
		map.put(SourceField.class, FieldRefactorHandler.instance());
		map.put(SourceMethod.class, MethodRefactorHandler.instance());
	}
	
	// singleton
	private JavaModelListener() {}
	
	/**
	 * @return The singleton instance of the listener.
	 */
	public static JavaModelListener getListener() {
		return _listener;
	}
	
	/**
	 * Called when an IJavaElement changes. Used to reflect changes in the
	 * editor, which has a view that is based on the <code>JavaModel</code>.
	 * 
	 * @param event - The change that occurred to the <code>JavaModel</code>.
	 */
	@SuppressWarnings("unchecked")
	public void elementChanged(ElementChangedEvent event) {
		try {
			/* Goes through these classes looking for any that are added, moved
			 * or removed. Calls methods that updates the editor to reflect any
			 * changes found.
			 */
			for (Class type : map.keySet()) {
				List<IJavaElementDelta> added =
					findAddedElements(event.getDelta(), type);
				List<IJavaElementDelta> removed =
					findRemovedElements(event.getDelta(), type);
				List<IJavaElementDelta> changed =
					findChangedElements(event.getDelta(), type);
				HashMap<IJavaElement, IJavaElement> moved =
					extractMovedElements(added, removed);
				
				// ignore updating the editors if no changes occurred
				if (added.size() == 0 && removed.size() == 0
						&& moved.size() == 0 && changed.size() == 0) {
					continue;
				}
				
				List<DiagramEditor> editors =
					new ArrayList<DiagramEditor>(DiagramEditor.getEditors());
				
				// handle changes
				for (DiagramEditor editor : editors) {
					RootModel root = editor.getRootModel();
					
					// handle moves
					for (IJavaElement sourceElement : moved.keySet()) {
						IJavaElement targetElement = moved.get(sourceElement);
						map.get(sourceElement.getClass()).handleMove(
								root, sourceElement, targetElement);
					}

					// handle removes
					for (IJavaElementDelta removedElement : removed) {
						map.get(removedElement.getElement().getClass())
						.handleRemove(root, removedElement.getElement());
					}
					
					// handle adds
					for (IJavaElementDelta addedElement : added) {
						map.get(addedElement.getElement().getClass()).handleAdd(
								root, addedElement.getElement());
					}

					// handle changes (to modifiers, etc.)
					for (IJavaElementDelta changedElement : changed) {
						handleElementChange(changedElement);
					}
					
					editor.forceRefreshRelationships();
				}

			}
		}
		catch (Throwable t) {
			//TODO Incremental exploration throws Null Pointer.  Virtually unreproduceable.
			GreenException.critical(t);
		} finally {
			TypeRefactorHandler.REMOVED_TYPE = null;
		}
	}

	/**
	 * Finds pairs of element changes that triggered "move" events. The types of
	 * elements that are considered eligible for such an event are:
	 * <code>IJavaProject</code>
	 * <code>IPackageFragment</code>
	 * <code>ICompilationUnit</code>
	 * 
	 * This is checked using the getMovedFromElement() and getMovedToElement()
	 * methods in the delta.
	 * 
	 * There are other <code>IJavaElement</code> subclasses that may trigger
	 * move events; however, in that context, move events do not pertain to the
	 * <code>DiagramEditor</code>.
	 * 
	 * @param added - A list of elements that were added.
	 * @param removed - A list of elements that were removed.
	 * @return A mapping of all elements that triggered "move" events in the
	 *         <code>IJavaElementDelta</code> hierarchy.
	 */
	private HashMap<IJavaElement, IJavaElement> extractMovedElements(
			List<IJavaElementDelta> added,
			List<IJavaElementDelta> removed) {
		HashMap<IJavaElement, IJavaElement> moved =
			new HashMap<IJavaElement, IJavaElement>();
		List<IJavaElementDelta> notAdded = new ArrayList<IJavaElementDelta>();
		List<IJavaElementDelta> notRemoved = new ArrayList<IJavaElementDelta>();
		
		for (IJavaElementDelta toDelta : added) {
			// ignore this element if it wasn't moved to somewhere
			if (toDelta.getMovedFromElement() != null) {
				IJavaElement newElement = toDelta.getElement();

				// see if there's an element that has
				// the "newElement" returned by
				// toDelta.getMovedToElement()
				for (IJavaElementDelta fromDelta : removed) {
					// if this element was moved from "newElement"...
					if (sameElements(
							fromDelta.getMovedToElement(), newElement)) {
						IJavaElement oldElement = fromDelta.getElement();

						// and this element was moved from "oldElement"
						if (sameElements(
								toDelta.getMovedFromElement(), oldElement)) {
							/*
							 * remove both elements from their respective lists
							 * and put them in the move map
							 */
							moved.put(oldElement, newElement);
							notAdded.add(toDelta);
							notRemoved.add(fromDelta);
						}
					}
				}
			}
		}
		
		added.removeAll(notAdded);
		removed.removeAll(notRemoved);
		
		return moved;
	}

	/**
	 * Iterates over all subbranches of the <code>IJavaElementDelta</code> and
	 * determines what elements have been added.
	 * 
	 * @param parentDelta - The tree (or subtree) of changes.
	 * @param type - The class of elements to check for.
	 * @return A list of added element deltas.
	 */
	private List<IJavaElementDelta> findAddedElements(
			IJavaElementDelta parentDelta,
			Class type) {
		List<IJavaElementDelta> changes = new ArrayList<IJavaElementDelta>();

		// check for added element
		if (parentDelta.getKind() == ADDED) {
			if (type.isInstance(parentDelta.getElement())) {
				changes.add(parentDelta);
			}
		}

		// traverse all changed branches
		// this code shouldn't need altering
		for (IJavaElementDelta delta : parentDelta.getAffectedChildren()) {
			changes.addAll(findAddedElements(delta, type));
		}

		return changes;
	}

	/**
	 * Iterates over all subbranches of the <code>IJavaElementDelta</code> and
	 * determines what elements have changed that are present in the
	 * <code>DiagramEditor</code>.
	 * 
	 * @param parentDelta - The delta to look through.
	 * @param type - The class to match.
	 * @return A mapping of moved elements.
	 */
	private List<IJavaElementDelta> findChangedElements(
			IJavaElementDelta parentDelta,
			Class type) {
		IJavaElementDelta delta;
		List<IJavaElementDelta> changes = new ArrayList<IJavaElementDelta>();

		// adds deltas representing the removed elements of
		// the specified type to the list of changes
		for (int i = 0; i < parentDelta.getChangedChildren().length; i++) {
			delta = parentDelta.getChangedChildren()[i];

			if (type.isInstance(delta.getElement())) {
				if (delta.getChangedChildren().length == 0) {
					changes.add(delta);
				}
			}
		}

		// traverse all changed branches
		// this code shouldn't need altering
		for (int i = 0; i < parentDelta.getChangedChildren().length; i++) {
			delta = parentDelta.getChangedChildren()[i];
			changes.addAll(findChangedElements(delta, type));
		}

		return changes;
	}

	/**
	 * Iterates over all subbranches of the <code>IJavaElementDelta</code> and
	 * determines what elements have been removed that are present in the
	 * <code>DiagramEditor</code>.
	 * 
	 * @param parentDelta - The tree (or subtree) of changes.
	 * @param type - The class of elements to check for.
	 * @return A list of removed element deltas.
	 */
	private List<IJavaElementDelta> findRemovedElements(
			IJavaElementDelta parentDelta,
			Class type) {
		List<IJavaElementDelta> changes = new ArrayList<IJavaElementDelta>();

		// check for removed element
		if (parentDelta.getKind() == REMOVED) {
			if (type.isInstance(parentDelta.getElement())) {
				changes.add(parentDelta);
			}
		}

		// traverse all changed branches
		// this code shouldn't need altering
		for (IJavaElementDelta delta : parentDelta.getAffectedChildren()) {
			changes.addAll(findRemovedElements(delta, type));
		}

		return changes;
	}

	/**
	 * Updates the display when some change occurs to a Java element that
	 * doesn't involve removal, addition, or movement.
	 * 
	 * @param elementDelta - The delta of the element that changed.
	 */
	private void handleElementChange(IJavaElementDelta elementDelta) {
		IJavaElement element = elementDelta.getElement();

		// update the modifiers of the element (if they changed)
		if ((elementDelta.getFlags() & IJavaElementDelta.F_MODIFIERS) != 0) {
			for (DiagramEditor editor : DiagramEditor.getEditors()) {
				RootPart rootEditPart = editor.getRootPart();
				RootModel root = (RootModel) rootEditPart.getModel();
				AbstractModel abstractModel = root.getModelFromElement(element);

				if (abstractModel != null) {
					if (abstractModel instanceof FieldModel) {
						FieldModel fModel = (FieldModel) abstractModel;
						FieldPart fEditPart = (FieldPart) rootEditPart
								.getPartFromModel(fModel);
						fEditPart.updateIcon();
					} else if (abstractModel instanceof MethodModel) {
						MethodModel mModel = (MethodModel) abstractModel;
						MethodPart mEditPart = (MethodPart) rootEditPart
								.getPartFromModel(mModel);
						mEditPart.updateIcon();
					}
				}
			}
		}
	}

	/**
	 * Compares two elements by their handle identifiers.
	 * 
	 * @param e1 - An element to compare.
	 * @param e2 - An element to compare.
	 * @return True if the elements have the same handles, false otherwise. 
	 */
	public static boolean sameElements(IJavaElement e1, IJavaElement e2) {
		if (e1 == null || e2 == null) return false;
		
		return (e1.getHandleIdentifier().equals(e2.getHandleIdentifier()));
	}
}

/**
 * Provides the listener with a polymorphic way to update Green's editor by
 * mapping different <code>IJavaElement</code> instances onto the classes that
 * handle their refactoring.
 * 
 * @author bcmartin
 * @param <E> - The element handled by the given implementor.
 */
interface RefactorHandler<E extends IJavaElement> {
	/**
	 * Handles the addition of an <code>IJavaElement</code> to the workspace.
	 * 
	 * @param root
	 * @param element
	 */
	public void handleAdd(RootModel root, E element);
	
	/**
	 * Handles movement of an <code>IJavaElement</code> within the workspace.
	 * 
	 * @param root
	 * @param sourceElement
	 * @param targetElement
	 */
	public void handleMove(RootModel root, E sourceElement, E targetElement);
	
	/**
	 * Handles the removal of an <code>IJavaElement</code> from the workspace.
	 * 
	 * @param root
	 * @param element
	 */
	public void handleRemove(RootModel root, E element);
}

/**
 * Handles project refactoring.
 * 
 * @author bcmartin
 * @param <E> - The <code>IJavaProject</code> implementation to use.
 */
class ProjectRefactorHandler<E extends IJavaProject>
implements RefactorHandler<E> {
	private static final ProjectRefactorHandler<IJavaProject> INSTANCE =
		new ProjectRefactorHandler<IJavaProject>();

	private ProjectRefactorHandler() {}
	
	/**
	 * @see edu.buffalo.cse.green.RefactorHandler#handleAdd(edu.buffalo.cse.green.editor.model.RootModel, org.eclipse.jdt.core.IJavaElement)
	 */
	public void handleAdd(RootModel root, E element) {
		// do nothing
	}
	
	/**
	 * @return The singleton instance of this <code>RefactorHandler</code>.
	 */
	public static RefactorHandler<IJavaProject> instance() {
		return INSTANCE;
	}

	/**
	 * @see edu.buffalo.cse.green.RefactorHandler#handleMove(edu.buffalo.cse.green.editor.model.RootModel, org.eclipse.jdt.core.IJavaElement, org.eclipse.jdt.core.IJavaElement)
	 */
	public void handleMove(RootModel root, E sourceElement, E targetElement) {
		root.changeProjectElement(sourceElement, targetElement);

		try {
			List<IJavaElement> packages = root.getElementsOfKind(
					PACKAGE_FRAGMENT);

			// look through packages to match up source and destination elements
			for (IJavaElement packElement : packages) {
				IPackageFragment packFrag = (IPackageFragment) packElement;

				if (JavaModelListener.sameElements(packFrag.getAncestor(
						JAVA_PROJECT),
						sourceElement.getPrimaryElement())) {
					for (IPackageFragment packFrags
							: targetElement.getPackageFragments()) {
						// check for the same name of the two packages
						if (packFrag.getElementName().equals(
								packFrags.getElementName())) {
							PackageRefactorHandler.instance().handleMove(
									root, packFrag, packFrags);
						}
					}
				}
			}

			root.dispose();
		} catch (JavaModelException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * @see edu.buffalo.cse.green.RefactorHandler#handleRemove(edu.buffalo.cse.green.editor.model.RootModel, org.eclipse.jdt.core.IJavaElement)
	 */
	public void handleRemove(RootModel root, E element) {
		List<IJavaElement> packages = root.getElementsOfKind(PACKAGE_FRAGMENT);
		
		for (IJavaElement pack : packages) {
			// get the project that contains the package
			IJavaElement proj = pack.getAncestor(JAVA_PROJECT);

			// if the package belongs to the project, remove it
			if (JavaModelListener.sameElements(proj, element)) {
				PackageRefactorHandler.instance().handleRemove(root,
						(IPackageFragment) pack);
			}
		}
	}
}

/**
 * Handles package refactoring.
 * 
 * @author bcmartin
 * @param <E> - The <code>IPackageFragment</code> implementation to use.
 */
class PackageRefactorHandler<E extends IPackageFragment>
implements RefactorHandler<E> {
	private static final PackageRefactorHandler<IPackageFragment> INSTANCE =
		new PackageRefactorHandler<IPackageFragment>();
	
	private PackageRefactorHandler() {}
	
	/**
	 * @see edu.buffalo.cse.green.RefactorHandler#handleAdd(edu.buffalo.cse.green.editor.model.RootModel, org.eclipse.jdt.core.IJavaElement)
	 */
	public void handleAdd(RootModel root, E element) {
		// do nothing
	}
	
	/**
	 * @return The singleton instance of this <code>RefactorHandler</code>.
	 */
	public static RefactorHandler<IPackageFragment> instance() {
		return INSTANCE;
	}

	/**
	 * @see edu.buffalo.cse.green.RefactorHandler#handleMove(edu.buffalo.cse.green.editor.model.RootModel, org.eclipse.jdt.core.IJavaElement, org.eclipse.jdt.core.IJavaElement)
	 */
	public void handleMove(RootModel root, E sourceElement, E targetElement) {
		try {
			List<IJavaElement> cus =
				root.getElementsOfKind(COMPILATION_UNIT);
			ICompilationUnit[] newCUs = targetElement.getCompilationUnits();

			for (IJavaElement cuElement : cus) {
				ICompilationUnit iCU = (ICompilationUnit) cuElement;
				if (JavaModelListener.sameElements(
						iCU.getAncestor(PACKAGE_FRAGMENT), sourceElement)) {
					for (ICompilationUnit cu : newCUs) {
						if (cu.getElementName().equals(
								iCU.getElementName())) {
							CompilationUnitRefactorHandler.instance(
									).handleMove(root, iCU, cu);
						}
					}
				}
			}
		} catch (JavaModelException e) {
			e.printStackTrace();
		}
	}

	/**
	 * @see edu.buffalo.cse.green.RefactorHandler#handleRemove(edu.buffalo.cse.green.editor.model.RootModel, org.eclipse.jdt.core.IJavaElement)
	 */
	public void handleRemove(RootModel root, E element) {
		List<IJavaElement> cus = root.getElementsOfKind(COMPILATION_UNIT); 
		
		for (IJavaElement cu : cus) {
			if (JavaModelListener.sameElements(
					cu.getAncestor(PACKAGE_FRAGMENT), element)) {
				CompilationUnitRefactorHandler.instance().handleRemove(
						root, (ICompilationUnit) cu);
			}
		}
	}
}

/**
 * Handles compilation unit refactoring.
 * 
 * @author bcmartin
 * @param <E> - The <code>ICompilationUnit</code> implementation to use.
 */
class CompilationUnitRefactorHandler<E extends ICompilationUnit>
implements RefactorHandler<E> {
	private static final CompilationUnitRefactorHandler<ICompilationUnit>
	INSTANCE = new CompilationUnitRefactorHandler<ICompilationUnit>();
	
	/**
	 * @return The singleton instance of this <code>RefactorHandler</code>.
	 */
	public static CompilationUnitRefactorHandler<ICompilationUnit> instance() {
		return INSTANCE;
	}

	/**
	 * @see edu.buffalo.cse.green.RefactorHandler#handleAdd(edu.buffalo.cse.green.editor.model.RootModel, org.eclipse.jdt.core.IJavaElement)
	 */
	public void handleAdd(RootModel root, E element) {
		// do nothing
	}
	
	/**
	 * @see edu.buffalo.cse.green.RefactorHandler#handleMove(edu.buffalo.cse.green.editor.model.RootModel, org.eclipse.jdt.core.IJavaElement, org.eclipse.jdt.core.IJavaElement)
	 */
	public void handleMove(RootModel root, E sourceElement, E targetElement) {
		// get all types represented in the diagram
		String scuId = sourceElement.getHandleIdentifier();
		String tcuId = targetElement.getHandleIdentifier();
		
		String scuName =
			scuId.substring(scuId.indexOf('{') + 1, scuId.indexOf(".java"));
		String tcuName =
			tcuId.substring(tcuId.indexOf('{') + 1, tcuId.indexOf(".java"));
		
		List<IJavaElement> cuTypes = root.getElementsOfKind(TYPE);
		
		// see if any types belong to the compilation unit
		// that is undergoing the move event
		for (IJavaElement oType : cuTypes) {
			if (JavaModelListener.sameElements(sourceElement,
					oType.getAncestor(COMPILATION_UNIT))) {
				String oId = oType.getHandleIdentifier();
				String oName = oId.substring(oId.indexOf('['));
				
				oName = oName.replaceAll("\\[" + scuName, "[" + tcuName);
				
				IJavaElement nType = JavaCore.create(tcuId + oName);
				TypeModel oModel = (TypeModel) root.getModelFromElement(oType);

				TypeModel nModel = root.createTypeModel((IType) nType);
				if (oModel != null) {
					// TODO We tried to catch a ResourceException,
					// but it is caught in MemberModel
					oModel.removeFromParent();
					nModel.setLocation(oModel.getLocation());
					nModel.setSize(oModel.getSize());
				}
			}
		}
	}

	/**
	 * @see edu.buffalo.cse.green.RefactorHandler#handleRemove(edu.buffalo.cse.green.editor.model.RootModel, org.eclipse.jdt.core.IJavaElement)
	 */
	public void handleRemove(RootModel root, E element) {
		// ignore this if the CU removed is a working copy (not actual removal)
		if (element.isWorkingCopy()) return;
		
		List<IJavaElement> types = new ArrayList<IJavaElement>(
				root.getElementsOfKind(TYPE));
		
		// remove any types that belong to the compilation unit
		for (IJavaElement type : types) {
			if (JavaModelListener.sameElements(element,
					type.getAncestor(COMPILATION_UNIT))) {
				if (root.getModelFromElement(type) != null) {
					root.getModelFromElement(type).removeFromParent();
				}
			}
		}
	}
}

/**
 * Handles type refactoring.
 * 
 * @author bcmartin
 * @param <E> - The <code>IType</code> implementation to use.
 */
class TypeRefactorHandler<E extends IType>
implements RefactorHandler<E> {
	private static final TypeRefactorHandler<IType> INSTANCE =
		new TypeRefactorHandler<IType>();
	public static TypeModel REMOVED_TYPE;
	
	/**
	 * @see edu.buffalo.cse.green.RefactorHandler#handleAdd(edu.buffalo.cse.green.editor.model.RootModel, org.eclipse.jdt.core.IJavaElement)
	 */
	public void handleAdd(RootModel root, E element) {
		// if the compilation unit exists in the editor, add the type
		if (root.ancestorInEditor(element.getAncestor(COMPILATION_UNIT))) {
			TypeModel addedType = root.createTypeModel(element);
			
			if (REMOVED_TYPE != null) {
				addedType.setLocation(REMOVED_TYPE.getLocation());
				addedType.setSize(REMOVED_TYPE.getSize());
			}
		}
	}
	
	/**
	 * @return The singleton instance of this <code>RefactorHandler</code>.
	 */
	public static TypeRefactorHandler<IType> instance() {
		return INSTANCE;
	}

	/**
	 * @see edu.buffalo.cse.green.RefactorHandler#handleMove(edu.buffalo.cse.green.editor.model.RootModel, org.eclipse.jdt.core.IJavaElement, org.eclipse.jdt.core.IJavaElement)
	 */
	public void handleMove(RootModel root, E sourceElement, E targetElement) {
		GreenException.illegalOperation("Unhandled element change");
	}

	/**
	 * @see edu.buffalo.cse.green.RefactorHandler#handleRemove(edu.buffalo.cse.green.editor.model.RootModel, org.eclipse.jdt.core.IJavaElement)
	 */
	public void handleRemove(RootModel root, E element) {
		REMOVED_TYPE = (TypeModel) root.getModelFromElement(element);
	}
}

/**
 * Handles field refactoring.
 * 
 * @author bcmartin
 * @param <E> - The <code>IField</code> implementation to use.
 */
class FieldRefactorHandler<E extends IField>
implements RefactorHandler<E> {
	private static final FieldRefactorHandler<IField> INSTANCE =
		new FieldRefactorHandler<IField>();

	/**
	 * @see edu.buffalo.cse.green.RefactorHandler#handleAdd(edu.buffalo.cse.green.editor.model.RootModel, org.eclipse.jdt.core.IJavaElement)
	 */
	public void handleAdd(RootModel root, E element) {
		if (element.equals(element.getPrimaryElement())) {
			TypeModel type = (TypeModel) root.getModelFromElement(
					element.getAncestor(TYPE));
			
			if (type != null) {
				type.addChild(new FieldModel(element));
			}
		}
	}
	
	/**
	 * @return The singleton instance of this <code>RefactorHandler</code>.
	 */
	public static FieldRefactorHandler<IField> instance() {
		return INSTANCE;
	}

	/**
	 * @see edu.buffalo.cse.green.RefactorHandler#handleMove(edu.buffalo.cse.green.editor.model.RootModel, org.eclipse.jdt.core.IJavaElement, org.eclipse.jdt.core.IJavaElement)
	 */
	public void handleMove(RootModel root, E sourceElement, E targetElement) {
		GreenException.illegalOperation("Unhandled element change");
	}

	/**
	 * @see edu.buffalo.cse.green.RefactorHandler#handleRemove(edu.buffalo.cse.green.editor.model.RootModel, org.eclipse.jdt.core.IJavaElement)
	 */
	public void handleRemove(RootModel root, E element) {
		FieldModel fieldModel =
			(FieldModel) root.getModelFromElement(element);
		
		// remove only stored copy
		if (fieldModel != null) {
			fieldModel.removeFromParent();
		}
	}
}

/**
 * Handles method refactoring.
 * 
 * @author bcmartin
 * @param <E> - The <code>IMethod</code> implementation to use.
 */
class MethodRefactorHandler<E extends IMethod>
implements RefactorHandler<E> {
	private static final MethodRefactorHandler<IMethod> INSTANCE =
		new MethodRefactorHandler<IMethod>();
	
	/**
	 * @see edu.buffalo.cse.green.RefactorHandler#handleAdd(edu.buffalo.cse.green.editor.model.RootModel, org.eclipse.jdt.core.IJavaElement)
	 */
	public void handleAdd(RootModel root, E element) {
		TypeModel type = (TypeModel) root.getModelFromElement(
				element.getAncestor(TYPE));
		
		if (type != null) {
			type.addChild(new MethodModel((IMethod) element));
		}
	}
	
	/**
	 * @return The singleton instance of this <code>RefactorHandler</code>.
	 */
	public static MethodRefactorHandler<IMethod> instance() {
		return INSTANCE;
	}

	/**
	 * @see edu.buffalo.cse.green.RefactorHandler#handleMove(edu.buffalo.cse.green.editor.model.RootModel, org.eclipse.jdt.core.IJavaElement, org.eclipse.jdt.core.IJavaElement)
	 */
	public void handleMove(RootModel root, E sourceElement, E targetElement) {
		GreenException.illegalOperation("Unhandled element change");
	}

	/**
	 * @see edu.buffalo.cse.green.RefactorHandler#handleRemove(edu.buffalo.cse.green.editor.model.RootModel, org.eclipse.jdt.core.IJavaElement)
	 */
	public void handleRemove(RootModel root, E element) {
		MethodModel methodModel =
			(MethodModel) root.getModelFromElement(element);
		
		// remove only stored copy
		if (methodModel != null) {
			methodModel.removeFromParent();
		}
	}
}