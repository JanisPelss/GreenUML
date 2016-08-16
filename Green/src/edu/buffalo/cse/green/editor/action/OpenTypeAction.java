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

import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.ui.JavaPluginImages;
import org.eclipse.jface.resource.ImageDescriptor;

import edu.buffalo.cse.green.GreenException;
import edu.buffalo.cse.green.dialogs.ChooseTypeDialog;
import edu.buffalo.cse.green.editor.DiagramEditor;
import edu.buffalo.cse.green.editor.model.commands.AddJavaElementCommand;

/**
 * An adaptation of <code>OpenCallHierarchyAction</code>.
 * 
 * @author bcmartin
 * @author <a href="mailto:zgwang@sourceforge.net">Gene Wang</a>
 */
public class OpenTypeAction extends ContextAction {
	/**
	 * @see edu.buffalo.cse.green.editor.action.ContextAction#doRun()
	 */
	public void doRun() throws JavaModelException {
		// open the selected types in the appropriate editors
		for (IType type : new ChooseTypeDialog(false).open()) {
			DiagramEditor editor = DiagramEditor.findProjectEditor(
					type.getJavaProject());
			
			if (editor == null) {
				editor = DiagramEditor.getActiveEditor();
			}
			
			if (editor == null) {
				GreenException.illegalOperation("No editor available");
			}
			
			editor.execute(new AddJavaElementCommand(editor, type));
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.action.Action#getImageDescriptor()
	 */
	public ImageDescriptor getImageDescriptor() {
		return JavaPluginImages.DESC_TOOL_OPENTYPE;
	}
	
	/**
	 * @see edu.buffalo.cse.green.editor.action.ContextAction#getLabel()
	 */
	public String getLabel() {
		return "Open Type in Editor";
	}

	/**
	 * @see edu.buffalo.cse.green.editor.action.ContextAction#getSupportedModels()
	 */
	protected int getSupportedModels() {
		return CM_EDITOR;
	}

	/**
	 * @see edu.buffalo.cse.green.editor.action.ContextAction#getPath()
	 */
	public Submenu getPath() {
		return Submenu.None;
	}

	/**
	 * @see edu.buffalo.cse.green.editor.action.ContextAction#isEnabled()
	 */
	public boolean isEnabled() {
		return true;
	}
}