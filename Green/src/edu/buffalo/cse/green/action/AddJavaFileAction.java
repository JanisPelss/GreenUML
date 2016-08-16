/* This file is part of Green.
 *
 * Copyright (C) 2005 The Research Foundation of State University of New York
 * All Rights Under Copyright Reserved, The Research Foundation of S.U.N.Y.
 * 
 * Green is free software, licensed under the terms of the Eclipse
 * Public License, version 1.0.  The license is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package edu.buffalo.cse.green.action;

import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.IActionDelegate;

import edu.buffalo.cse.green.GreenException;
import edu.buffalo.cse.green.editor.DiagramEditor;
import edu.buffalo.cse.green.editor.model.commands.AddJavaElementCommand;
import edu.buffalo.cse.green.util.IterableSelection;

/**
 * Handles context clicking in the Package Explorer and provides support for
 * adding elements to an open editor.
 * 
 * @author bcmartin
 */
public class AddJavaFileAction implements IActionDelegate {
	ISelection _selection;

	/**
	 * @see org.eclipse.ui.IActionDelegate#run(org.eclipse.jface.action.IAction)
	 */
	public void run(IAction action) {
		StructuredSelection ss = (StructuredSelection) _selection;
		DiagramEditor editor = DiagramEditor.getActiveEditor();
		
		// ensure an editor is open
		if (editor == null) {
			try {
				editor = DiagramEditor.createEditor(ss);
			} catch (JavaModelException e) {
				e.printStackTrace();
			}
		}

		for (IJavaElement element
				: new IterableSelection<IJavaElement>(ss)) {
			// If current editor's project is not set, add to it.
			if (editor.getProject() != null) {
				if (!editor.getProject().getHandleIdentifier().equals(
						element.getJavaProject().getHandleIdentifier())) {
					// if the editor we found can't hold the current element,
					// see if an editor exists that can hold the element
					editor = DiagramEditor.findProjectEditor(element
							.getJavaProject());
					// if no such editor exists...
					if (editor == null) {
						// create one
						try {
							editor =
								DiagramEditor.createEditor(
										new StructuredSelection(element));
						} catch (JavaModelException e) {
							e.printStackTrace();
						}
					}
				}
			}

			try {
				editor.execute(new AddJavaElementCommand(
						editor, element));
			} catch (GreenException e) {
				MessageDialog.openError(editor.getSite().getShell(),
						"Error", e.getLocalizedMessage());
			}
		}

		editor.refresh();
	}

	/**
	 * @see org.eclipse.ui.IActionDelegate#selectionChanged(org.eclipse.jface.action.IAction, org.eclipse.jface.viewers.ISelection)
	 */
	public void selectionChanged(IAction action, ISelection selection) {
		if (selection instanceof StructuredSelection) {
			_selection = selection;
		}
	}
}