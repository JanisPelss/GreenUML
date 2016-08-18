/* This file is part of Green.
 *
 * Copyright (C) 2005 The Research Foundation of State University of New York
 * All Rights Under Copyright Reserved, The Research Foundation of S.U.N.Y.
 * 
 * Green is free software, licensed under the terms of the Eclipse
 * Public License, version 1.0.  The license is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package edu.buffalo.cse.green.dialogs.wizards;

import static edu.buffalo.cse.green.constants.DialogStrings.WIZARD_ADD_CLASS_TITLE;
import static edu.buffalo.cse.green.preferences.PreferenceInitializer.P_AUTOGEN_ABST_METHOD;
import static edu.buffalo.cse.green.preferences.PreferenceInitializer.P_AUTOGEN_MAIN;
import static edu.buffalo.cse.green.preferences.PreferenceInitializer.P_AUTOGEN_SUPER_CONSTR;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.internal.ui.JavaPluginImages;
import org.eclipse.jdt.ui.wizards.NewClassWizardPage;
import org.eclipse.jface.dialogs.IDialogSettings;

import edu.buffalo.cse.green.PlugIn;

/**
 * Opens a dialog box that prompts the user for a new class that will be
 * displayed in the diagram.
 * 
 * @author hk47
 */
public class NewClassWizard extends NewElementWizard {
	private static final String PAGE_NAME = "NewClassWizardPage";
//	private NewJavaDocPage _jPage;
	
	public NewClassWizard() {
		setDefaultPageImageDescriptor(JavaPluginImages.DESC_WIZBAN_NEWCLASS);
		setDialogSettings(PlugIn.getDefault().getDialogSettings());
		setWindowTitle(WIZARD_ADD_CLASS_TITLE);
	}

	/**
	 * @see org.eclipse.jface.wizard.IWizard#addPages()
	 */
	public void addPages() {
		super.addPages();
		_fPage = new NewClassWizardPage() {
			
//TODO This is also part of the JavaDoc code
//			@Override
//			protected String getTypeComment( ICompilationUnit parentCU, String lineDelimiter ) {
//				String result = "/**" + lineDelimiter;
//				for( String s : _jPage.getLines( ) )
//					result += " * " + s + lineDelimiter;
//				result += " */";
//				return result;
//			}
			
		};
		addPage(_fPage);
		_fPage.getWizard().getDialogSettings().addNewSection(PAGE_NAME);

		IDialogSettings settings =
			_fPage.getWizard().getDialogSettings().getSection(PAGE_NAME);
		
		if (settings != null) {
			settings.put("create_main", PlugIn.getBooleanPreference(P_AUTOGEN_MAIN));
			settings.put("create_constructor", PlugIn.getBooleanPreference(
					P_AUTOGEN_SUPER_CONSTR));
			settings.put("create_unimplemented", PlugIn.getBooleanPreference(
					P_AUTOGEN_ABST_METHOD));
		}
		((NewClassWizardPage) _fPage).init(getSelection());
		
		_fPage.setAddComments( true, false );
		
//We don't want this in the current release.
//		_jPage = new NewJavaDocPage( );
//		addPage( _jPage );
	}

	/**
	 * @see edu.buffalo.cse.green.dialogs.wizards.NewElementWizard#canRunForked()
	 */
	protected boolean canRunForked() {
		return !_fPage.isEnclosingTypeSelected();
	}

	/**
	 * @see edu.buffalo.cse.green.dialogs.wizards.NewElementWizard#finishPage(org.eclipse.core.runtime.IProgressMonitor)
	 */
	protected void finishPage(IProgressMonitor monitor)
			throws InterruptedException, CoreException {
		super.finishPage(monitor);
		_fPage.createType(monitor); // use the full progress monitor
	}

	/**
	 * @see edu.buffalo.cse.green.dialogs.wizards.GreenWizard#doFinish()
	 */
	public boolean doFinish() {
		boolean res = super.doFinish();
		
		if (res) {
			IResource resource = _fPage.getModifiedResource();
			
			if (resource != null) {
				selectAndReveal(resource);
				
				if (resource.getType() == IResource.FILE) {
					IFile file = (IFile) resource;
					ICompilationUnit cu = JavaCore
							.createCompilationUnitFrom(file);
					getModel().setMember(cu.findPrimaryType());
				}
			}
		}
		
		return res;
	}
}