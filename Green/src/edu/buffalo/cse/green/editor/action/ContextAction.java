/* This file is part of Green.
 *
 * Copyright (C) 2005 The Research Foundation of State University of New York
 * All Rights Under Copyright Reserved, The Research Foundation of S.U.N.Y.
 * 
 * Green is free software, licensed under the terms of the Eclipse
 * Public License, version 1.0.  The license is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package edu.buffalo.cse.green.editor.action;

import static edu.buffalo.cse.green.GreenException.GRERR_UNAVAILABLE_ACTION;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.actions.ActionFactory;

import edu.buffalo.cse.green.GreenException;
import edu.buffalo.cse.green.PlugIn;
import edu.buffalo.cse.green.editor.DiagramEditor;
import edu.buffalo.cse.green.editor.model.AbstractModel;

/**
 * Abstract parent of all context actions. These actions are displayed in a
 * menu when the user right-clicks the editor. The menu displayed is based on
 * the selection in the editor
 * 
 * @author bcmartin
 * @author rjtruban
 */
public abstract class ContextAction extends Action {
	/**
	 * The provider of the selection.
	 */
	protected ISelectionProvider _selectionProvider;

	/**
	 * The selected <code>IJavaElement</code>.
	 */
	protected IJavaElement _element;

	/**
	 * The selected <code>AbstractModel</code>.
	 */
	protected AbstractModel _model;

	public static final char EDITOR_CHANGE_SIZE = 's';
	
	public static final char EDITOR_INCREMENTAL_EXPLORATION = 'i';

	public static final char EDITOR_REFACTOR_MOVE = 'v';

	public static final char EDITOR_REFACTOR_RENAME = 'r';
	
	public static final char EDITOR_REFRESH_ACTION = 'f';

	public static final char EDITOR_UNLOAD_TYPE = SWT.BS;
	
	/**
	 * Context menu flag for <code>TypeModel</code>.
	 */
	public static final int CM_TYPE = 1;

	/**
	 * Context menu flag for <code>FieldModel</code>.
	 */
	public static final int CM_FIELD = 2;

	/**
	 * Context menu flag for <code>MethodModel</code>.
	 */
	public static final int CM_METHOD = 4;

	/**
	 * Context menu flag for <code>NoteModel</code>.
	 */
	public static final int CM_NOTE = 8;

	/**
	 * Context menu flag for <code>RelationshipModel</code>.
	 */
	public static final int CM_RELATIONSHIP = 16;

	/**
	 * Context menu flag for <code>UMLRootModel</code>.
	 */
	public static final int CM_EDITOR = 32;

	/**
	 * Context menu flag for <code>MemberModel</code>.
	 */
	public static final int CM_MEMBER = CM_FIELD | CM_METHOD
			| CM_TYPE;

	/**
	 * Context menu flag for <code>AbstractModel</code>.
	 */
	public static final int CM_ALL = CM_MEMBER | CM_NOTE | CM_RELATIONSHIP | CM_EDITOR;

	/**
	 * @return The editor containing the selection.
	 */
	protected final DiagramEditor getEditor() {
		if (_selectionProvider == null) {
			if (DiagramEditor.getActiveEditor() == null) {
			    return DiagramEditor.getEditors().get(0);
			} else {
				return DiagramEditor.getActiveEditor();
			}
		} else {
			return (DiagramEditor) _selectionProvider;
		}
	}

	public ContextAction() {
		setText(getLabel());
		setId(getClass().getName());
		setImageDescriptor(getImageDescriptor());
	}

	// work-around for problems with initial text-setting problem
	public ContextAction(Object obj) {
		setId(getClass().getName());
	}

	/**
	 * Sets the <code>ISelectionProvider</code> used by this context action.
	 * 
	 * @param selectionProvider - The <code>ISelectionProvider</code> to use.
	 */
	public void setSelectionProvider(ISelectionProvider selectionProvider) {
		_selectionProvider = selectionProvider;
	}

	/**
	 * @return the label displayed in the context menu for this action
	 */
	public abstract String getLabel();

	/**
	 * @see org.eclipse.jface.action.IAction#run()
	 */
	public final void run() {
		setContents();
		
		// if the action isn't always available
		if (getSupportedModels() != CM_ALL) {
			// check its validity
			if ((_model == null)
					|| ((_model.getContextMenuFlag() & getSupportedModels())
							== 0)) {
				GreenException.errorDialog(GRERR_UNAVAILABLE_ACTION);
				return;
			}
		}

		BusyIndicator.showWhile(getEditor().getSite().getShell().getDisplay(),
				new Runnable() {
			/**
			 * @see java.lang.Runnable#run()
			 */
			public void run() {
				try {
					doRun();
				} catch (JavaModelException e) {
					e.printStackTrace();
				}
			}
		});
		
		// set global actions
		IActionBars bars = getEditor().getEditorSite().getActionBars();

		for (IContributionItem item : bars.getToolBarManager().getItems()) {
			if (item instanceof ActionContributionItem) {
				ActionContributionItem aItem = (ActionContributionItem) item;
				ContextAction action = (ContextAction) aItem.getAction();
				action.setEnabled(action.isEnabled());
			}
		}
	}

	/**
	 * Sets the variable for accessing the element and model that are currently
	 * selected
	 */
	public void setContents() {
		if (getEditor().getContext() != null) {
			_element = getEditor().getContext().getElement();
			_model = getEditor().getContext().getModel();
		} else {
			_element = null;
			_model = null;
		}
	}

	/**
	 * Called by run; the run() method is intercepted to determine the context
	 * before the action is performed 
	 * 
	 * @throws JavaModelException
	 */
	protected abstract void doRun() throws JavaModelException;

	/**
	 * @return A flag representing the models supported by the context menu
	 * action.
	 * 
	 * @see edu.buffalo.cse.green.editor.action.ContextAction
	 */
	protected abstract int getSupportedModels();

	/**
	 */
	public abstract boolean isEnabled();

	/**
	 * Determines whether this action should be visible
	 * 
	 * @param model - The model currently selected
	 * @return true if the action should be visible, false otherwise
	 */
	public boolean isVisible(AbstractModel model) {
		if ((model.getContextMenuFlag() & getSupportedModels()) != 0) { return true; }

		return false;
	}

	/**
	 * @return the group that this context menu action belongs to
	 */
	public final String getGroup() {
		return getPath().toString();
	}

	/**
	 * @return a list of labels representing the submenus to this action
	 */
	public List<String> getSubMenuLabels() {
		List<String> labels = new ArrayList<String>();
		if (getGroup().equals(Submenu.None.toString())) return labels;
		labels.add(PlugIn.getSubMenuLabel(getGroup()));
		return labels;
	}

	/**
	 * @return true if this function is available for .class files, false
	 * otherwise
	 */
	public boolean isAvailableForBinary() {
		return false;
	}

	/**
	 * @return A string representing the location of this action in the menu
	 */
	public abstract Submenu getPath();
	
	/**
	 * @return The String representing a unique action identifier. The action
	 * will only be valid if the model understands the message.
	 */
	protected final String getMessage() {
		return null;
	}

	/**
	 * @return The corresponding standard workbench action.
	 */
	public ActionFactory getGlobalActionHandler() {
		GreenException.illegalOperation();
		return null;
	}
	
	/**
	 * @return True if the element is binary, false otherwise
	 */
	public boolean isBinary() {
		setContents();
		
		if (_element == null) return true;
		
		if (_element.getElementType() == IJavaElement.TYPE) {
			return ((IType) _element).isBinary();
		}
		
		return ((IType) _element.getAncestor(IJavaElement.TYPE)).isBinary();
	}

	/**
	 * Makes the action enabled or disabled as appropriate.
	 */
	public void calculateEnabled() {
		setContents();
		setEnabled(isEnabled());
	}
}