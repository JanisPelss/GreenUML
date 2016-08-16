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

import static edu.buffalo.cse.green.constants.DialogStrings.WIZARD_ADD_ENUM_TITLE;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.internal.ui.JavaPluginImages;
import org.eclipse.jdt.ui.wizards.NewEnumWizardPage;

import edu.buffalo.cse.green.PlugIn;

/**
 * Opens a dialog box that prompts the user for a new interface that will be
 * displayed in the diagram.
 * 
 * @author hk47
 */
public class NewEnumWizard extends NewElementWizard {
	public NewEnumWizard() {
		setDefaultPageImageDescriptor(JavaPluginImages.DESC_WIZBAN_NEWENUM);
		setDialogSettings(PlugIn.getDefault().getDialogSettings());
		setWindowTitle(WIZARD_ADD_ENUM_TITLE);
		// setWindowTitle(NewWizardMessages.getString("NewInterfaceCreationWizard.title"));
		// //$NON-NLS-1$
	}

	/**
	 * @see org.eclipse.jface.wizard.IWizard#addPages()
	 */
	public void addPages() {
		super.addPages();
		_fPage = new NewEnumWizardPage();
		addPage(_fPage);
		((NewEnumWizardPage) _fPage).init(getSelection());
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
	 * @see org.eclipse.jface.wizard.IWizard#performFinish()
	 */
	public boolean doFinish() {
		boolean res = super.doFinish();
		
		if (res) {
			IResource resource = _fPage.getModifiedResource();
			
			if (resource != null) {
				selectAndReveal(resource);
				
				if (resource.getType() == IResource.FILE) {
					IFile file = (IFile) resource;
					ICompilationUnit cu =
						JavaCore.createCompilationUnitFrom(file);
					getModel().setMember(cu.findPrimaryType());
				}
			}
		}
		
		return res;
	}

	/**
	 * @return The modified resource.
	 */
	public IResource getModifiedResource() {
		return _fPage.getModifiedResource();
	}
}