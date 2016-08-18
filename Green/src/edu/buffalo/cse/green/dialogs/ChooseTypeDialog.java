/* This file is part of Green.
 *
 * Copyright (C) 2005 The Research Foundation of State University of New York
 * All Rights Under Copyright Reserved, The Research Foundation of S.U.N.Y.
 * 
 * Green is free software, licensed under the terms of the Eclipse
 * Public License, version 1.0.  The license is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package edu.buffalo.cse.green.dialogs;

import static org.eclipse.jdt.ui.IJavaElementSearchConstants.CONSIDER_ALL_TYPES;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.search.SearchEngine;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jface.window.Window;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.SelectionDialog;

import edu.buffalo.cse.green.PlugIn;
import edu.buffalo.cse.green.editor.DiagramEditor;

/**
 * A dialog that allows the user to select a type from a list of types.
 * 
 * @author bcmartin
 * @author zgwang
 */
public class ChooseTypeDialog {
	private SelectionDialog _dialog;

	public ChooseTypeDialog(boolean allowMultiSelect) {
		DiagramEditor editor = DiagramEditor.getActiveEditor();
		IJavaElement[] elements = new IJavaElement[1];
		elements[0] = editor.getProject();
		
		try {
			_dialog = JavaUI.createTypeDialog(PlugIn.getDefaultShell(),
					PlatformUI.getWorkbench().getActiveWorkbenchWindow(),
					SearchEngine.createJavaSearchScope(elements), CONSIDER_ALL_TYPES,
					allowMultiSelect);
			_dialog.setTitle("Select Type");
		} catch (JavaModelException e) {
			e.printStackTrace();
		}
	}
	
	public List<IType> open() {
		List<IType> types = new ArrayList<IType>();
		
		// if OK is pressed, collect the selected types
		if (_dialog.open() == Window.OK) {
			for (Object obj : _dialog.getResult()) {
				types.add((IType) obj);
			}
		}
		
		return types;
	}
}