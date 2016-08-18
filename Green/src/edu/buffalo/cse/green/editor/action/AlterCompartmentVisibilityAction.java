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
//
//import edu.buffalo.cse.green.dialogs.CompartmentVisibilityDialog;
//
//import org.eclipse.jdt.core.JavaModelException;
//
///**
// * Opens the dialog for showing or hiding fields and/or methods of a type.
// *  
// * @author <a href="mailto:zgwang@sourceforge.net">Gene Wang</a>
// *
// */
////This action does not work as intended and has been disabled for now.
////users are advised to use the Filters to get a similar result to the one
////desired here.
//
//public class AlterCompartmentVisibilityAction extends ContextAction {
//	/**
//	 * @see edu.buffalo.cse.green.editor.action.ContextAction#doRun()
//	 */
//	public void doRun() throws JavaModelException {
//		CompartmentVisibilityDialog cvd = new CompartmentVisibilityDialog(getEditor().getSite().getShell());
//		cvd.setBlockOnOpen(true);
//		cvd.open();
//	}
//
//	/**
//	 * @see edu.buffalo.cse.green.editor.action.ContextAction#getLabel()
//	 */
//	public String getLabel() {
//		return "Show/Hide Classbox Sections...";
//	}
//
//	/**
//	 * @see edu.buffalo.cse.green.editor.action.ContextAction#getSupportedModels()
//	 */
//	protected int getSupportedModels() {
//		return CM_EDITOR;
//	}
//
//	/**
//	 * @see edu.buffalo.cse.green.editor.action.ContextAction#getPath()
//	 */
//	public Submenu getPath() {
//		return Submenu.Invisible;
////		return Submenu.None;
//	}
//
//	/**
//	 * @see edu.buffalo.cse.green.editor.action.ContextAction#isEnabled()
//	 */
//	public boolean isEnabled() {
//		return false;
////		return true;
//	}
//}