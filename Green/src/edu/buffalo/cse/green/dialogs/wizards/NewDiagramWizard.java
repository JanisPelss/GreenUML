/* This file is part of Green.
 *
 * Copyright (C) 2005 The Research Foundation of State University of New York
 * All Rights Under Copyright Reserved, The Research Foundation of S.U.N.Y.
 * 
 * Green is free software, licensed under the terms of the Eclipse
 * Public License, version 1.0.  The license is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

/**
 * 
 */
package edu.buffalo.cse.green.dialogs.wizards;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.internal.ide.DialogUtil;
import org.eclipse.ui.internal.wizards.newresource.ResourceMessages;
import org.eclipse.ui.wizards.newresource.BasicNewFileResourceWizard;

import edu.buffalo.cse.green.constants.PluginConstants;

/**
 * @author Gene Wang
 *
 */
public class NewDiagramWizard extends BasicNewFileResourceWizard implements INewWizard
{
    private NewDiagramPage _page;

    // workbench selection when the wizard was started
    protected IStructuredSelection _selection;
    // the workbench instance
    protected IWorkbench _workbench;
    
    public NewDiagramWizard()
    {
	super();
	this.setWindowTitle("New Green UML Diagram");
    }
    
    public void addPages()
    {
	_page = new NewDiagramPage("New Diagram", _selection);
	addPage(_page);
    }
    
    public boolean canFinish()
    {
	return this.getContainer().getCurrentPage().isPageComplete();
    }
    
    /**
     * @see org.eclipse.jface.wizard.Wizard#performCancel()
     */
    @Override
    public boolean performCancel() 
    {
    	return true;
    }
    

    /**
     * @see org.eclipse.jface.wizard.Wizard#performFinish()
     */
    @Override
    public boolean performFinish() 
    {
	_page.setFileName(_page.getFileName() + "." + PluginConstants.GREEN_EXTENSION);
	IFile file = _page.createNewFile();
	

	
    selectAndReveal(file);
    
    IWorkbenchWindow dw = getWorkbench().getActiveWorkbenchWindow();
    try {
        if (dw != null) {
            IWorkbenchPage page = dw.getActivePage();
            if (page != null) {
                IDE.openEditor(page, file, true);
            }
        }
    } catch (PartInitException e) {
        DialogUtil.openError(dw.getShell(), ResourceMessages.FileResource_errorMessage, 
                e.getMessage(), e);
    }
    	return true;
    }

    /**
     * @see org.eclipse.ui.IWorkbenchWizard#init(org.eclipse.ui.IWorkbench, org.eclipse.jface.viewers.IStructuredSelection)
     */
    public void init(IWorkbench workbench, IStructuredSelection selection) 
    {
    	super.init(workbench, selection);
    	_workbench = workbench;
    	_selection = selection;
    }
}
