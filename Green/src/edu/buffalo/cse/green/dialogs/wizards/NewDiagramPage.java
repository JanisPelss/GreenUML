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

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.dialogs.WizardNewFileCreationPage;

/**
 * @author Gene Wang
 *
 */
public class NewDiagramPage extends WizardNewFileCreationPage
{
    /**
     * @param pageName Name of the page.
     */
    public NewDiagramPage(String pageName, IStructuredSelection sel)
    {
	super(pageName, sel);
	setTitle("Green UML Diagram");
	setDescription("Create a new diagram.");
//	Image in create new Wizard
//	setImageDescriptor();
    }
}
