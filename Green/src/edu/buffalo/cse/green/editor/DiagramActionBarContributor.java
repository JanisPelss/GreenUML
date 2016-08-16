/* This file is part of Green.
 *
 * Copyright (C) 2005 The Research Foundation of State University of New York
 * All Rights Under Copyright Reserved, The Research Foundation of S.U.N.Y.
 * 
 * Green is free software, licensed under the terms of the Eclipse
 * Public License, version 1.0.  The license is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package edu.buffalo.cse.green.editor;

import org.eclipse.gef.ui.actions.ActionBarContributor;
import org.eclipse.gef.ui.actions.DeleteRetargetAction;
import org.eclipse.gef.ui.actions.RedoRetargetAction;
import org.eclipse.gef.ui.actions.UndoRetargetAction;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.actions.RetargetAction;

/**
 * Creates a tool bar right below Eclipse's main menu bar
 * (default location) that contains action buttons
 * appropriate to our editor.
 * 
 * @author bcmartin
 */
public class DiagramActionBarContributor extends ActionBarContributor {
	/**
	 * @see org.eclipse.gef.ui.actions.ActionBarContributor#buildActions()
	 */
    protected void buildActions() {
        addRetargetAction(new DeleteRetargetAction());
        addRetargetAction(new UndoRetargetAction());
        addRetargetAction(new RedoRetargetAction());
        addRetargetAction(new RetargetAction(ActionFactory.PRINT.getId(),""));
        addRetargetAction(new RetargetAction(ActionFactory.REFRESH.getId(),""));
        
    }

    /**
     * @see org.eclipse.gef.ui.actions.ActionBarContributor#declareGlobalActionKeys()
     */
    protected void declareGlobalActionKeys() {
    	// do nothing
    }

    /**
     * @see org.eclipse.ui.part.EditorActionBarContributor#contributeToToolBar(org.eclipse.jface.action.IToolBarManager)
     */
    public void contributeToMenu(IMenuManager menuManager) {
    	//menuManager.add(new DeleteAction());
    	//menuManager.add(new PrintDIAAction());
    	//menuManager.add(new RedoAction());
    	//menuManager.add(new RefreshAction());
    	//menuManager.add(new UndoAction());
    }
}
