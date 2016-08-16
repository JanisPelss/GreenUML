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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.gef.Request;
import org.eclipse.gef.RequestConstants;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.views.contentoutline.ContentOutlinePage;

import edu.buffalo.cse.green.editor.controller.AbstractPart;
import edu.buffalo.cse.green.editor.controller.MemberPart;
import edu.buffalo.cse.green.editor.controller.TypePart;
import edu.buffalo.cse.green.editor.model.AbstractModel;
import edu.buffalo.cse.green.editor.model.MemberModel;
import edu.buffalo.cse.green.editor.model.TypeModel;


/**
 * Customized Content Outline View for use with the Green
 * diagram editor.
 * 
 * @author bcmartin
 */
public class OutlinePage extends ContentOutlinePage {
	private OutlineContentProvider _contentProvider;
	private TreeViewer _viewer;
	private IStructuredSelection _sel;
	private static final OutlinePage SINGLETON = new OutlinePage();
	
	/**
     * The constructor.  Maps this OutlinePage to the DiagramEditor
     */
    private OutlinePage() {
        super();
        DiagramEditor.setOutlinePage(this);
    }  
    
    /**
     * @return the static singleton for the OutlinePage
     */
    public static OutlinePage getInstance() {
    	return SINGLETON;
    }
    
    /**
     * Updates the outline with contents from the given editor
     * @param editor the editor whose contents are to be put in the outline
     */
    public void update(DiagramEditor editor) {
    	if (_viewer.getContentProvider() == null) {
    		// don't update
    		return;
    	}
    	_viewer.setInput(editor);
	}

	/**
     * Creates the control i.e. creates all the stuff that matters and
     * is visible in the outline. 
     * 
     * Actions must be created before menus and toolbars.
     * 
     * @param parent
     */
    public void createControl(Composite parent) {
        super.createControl(parent);
        _contentProvider = new OutlineContentProvider();
        
        // create the context actions
        createActions();
        
        // initialize the tree viewer
        _viewer = getTreeViewer();		
        _viewer.setContentProvider(_contentProvider);
        _viewer.setLabelProvider(new OutlineLabelProvider());
        
        // get and apply the preferences
        this.getOutlinePreferences();
        
        // set the selection listener
        _viewer.addSelectionChangedListener(this);
        
        // attach actions to the viewer (double-click, context, etc.)
        _viewer.addDoubleClickListener(new IDoubleClickListener() {
			public void doubleClick(DoubleClickEvent event) {
				AbstractPart part = DiagramEditor.getActiveEditor().getContext().getPart();
				System.err.println(part);
		        part.performRequest(new Request(RequestConstants.REQ_OPEN));
			}
        });
        
        // enable copy-paste
        initCopyPaste(_viewer);
        
        // create the menu bar and the context menu
        createToolbar();
        resetToolbarButtons();
        createContextMenu();
    }
    
    /**
     * @see org.eclipse.ui.views.contentoutline.ContentOutlinePage#setSelection(org.eclipse.jface.viewers.ISelection)
     */
    public void setSelection(ISelection selection) {
        super.setSelection(selection);

        // if the selection hasn't changed, abort
        if (selection.equals(_sel)) return;
        
        _sel = (IStructuredSelection) selection;
        AbstractPart part = (AbstractPart) _sel.getFirstElement();
        DiagramEditor.getActiveEditor().selectionChanged(
        		DiagramEditor.getActiveEditor(), new StructuredSelection(part));
    }
    
    /**
     * @see org.eclipse.ui.part.Page#dispose()
     */
    public void dispose() {
        super.dispose();
    }
    
    /**
     * Creates the actions associated with the outline. 
     */
    private void createActions() {
    }
    
    /**
     * Initialize copy paste by getting the clipboard and hooking 
     * the actions to global edit menu.
     * 
     * @param viewer
     */
    private void initCopyPaste(TreeViewer viewer) {
    }
    
    /**
     * Get the preferences.
     * 
     */
    private void getOutlinePreferences()  {
    }
    
    private void resetToolbarButtons() {
    }
    
    /**
     * Create the toolbar.
     */
    private void createToolbar() {
        // add actions to the toolbar
        // IToolBarManager toolbarManager = getSite().getActionBars().getToolBarManager();
    }
    
    /**
     * Creates the context menu. 
     */
    private void createContextMenu() {
        // create menu manager
        MenuManager menuMgr = new MenuManager();
        menuMgr.setRemoveAllWhenShown(true);
        menuMgr.addMenuListener(new IMenuListener() {
            public void menuAboutToShow(IMenuManager mgr) {
                DiagramEditor.getActiveEditor().createContextMenu(mgr);
            }
        });
        
        // create the menu

        Menu menu = menuMgr.createContextMenu(getTreeViewer().getControl());
        getTreeViewer().getControl().setMenu(menu);

        //register menu for extensions
        //getSite().registerContextMenu(menuMgr, getTreeViewer());
    }
}

class OutlineContentProvider implements ITreeContentProvider {
	/** 
	 * Gets the children of the given parent node of the tree.
	 * 
     * @param parentElement parent node of the tree
	 * @return list of children 
	 */
	public Object[] getChildren(Object element) {
		List<Object> children = new ArrayList<Object>();
		
		if (element instanceof TypePart) {
			TypePart part = (TypePart) element;
			TypeModel model = (TypeModel) part.getModel();
			
			if (model.getFieldCompartmentModel() != null) {
				children.addAll(part.getRootPart().getPartFromModel(
						model.getFieldCompartmentModel()).getChildren());
			}
			
			if (model.getMethodCompartmentModel() != null) {
				children.addAll(part.getRootPart().getPartFromModel(
						model.getMethodCompartmentModel()).getChildren());
			}
		}

		return children.toArray();
	}

	/** 
	 * Gets the parent of the given tree node.
	 * 
     * @param element node of the tree
	 * @return parent node of the element 
	 */
	public Object getParent(Object element) {
		if (element instanceof MemberPart) {
			if (!(element instanceof TypePart)) {
				MemberPart part = (MemberPart) element;
				MemberModel model = (MemberModel) part.getModel();
				part.getRootPart().getPartFromModel(
						model.getParent().getParent());
			}
		}

		return null;
	}

	/** 
	 * Checks if the given tree node has children nodes.
	 * 
     * @param element node of the tree
	 * @return true if element has children, otherwise false
	 */
	public boolean hasChildren(Object element) {
		return getChildren(element).length > 0;
	}

	public Object[] getElements(Object inputElement) {
		DiagramEditor editor = (DiagramEditor) inputElement;
		List<AbstractPart> children = new ArrayList<AbstractPart>();
		
		for (AbstractModel model : editor.getRootModel().getChildren()) {
			if (model instanceof TypeModel) {
				children.add(editor.getRootPart().getPartFromModel(model));
			}
		}
		
		return children.toArray();
	}

	/**
	 * @see org.eclipse.jface.viewers.IContentProvider#dispose()
	 */
	public void dispose() {}

	/**
	 * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer, java.lang.Object, java.lang.Object)
	 */
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		// do nothing
	}
}

class OutlineLabelProvider extends LabelProvider {
	private static Map<String, Image> _mapping =
		new HashMap<String, Image>();
	
	/**
	 * Returns an image for the given element.
     * 
     * @return the image to view at the given element
	 */
	public Image getImage(Object element) {
		// if an image was already created, free it up (so as to not waste mem)
		MemberPart part = (MemberPart) element;
		MemberModel model = (MemberModel) part.getModel();
		Image image = _mapping.get(model.getMember().getHandleIdentifier());
		
		if (image != null) {
			image.dispose();
		}
		
		
		image = model.getIcon();
		_mapping.put(model.getMember().getHandleIdentifier(), image);
		return image;
	}

	/**
     * Returns the text description of the elemet. That is element 
     * name for OutlineNode.
     * 
	 * @return the text to view at the given element
	 */
	public String getText(Object element) {
		MemberPart part = (MemberPart) element;
		MemberModel model = (MemberModel) part.getModel();
		return model.getDisplayName();
	}

	/**
	 * @see org.eclipse.jface.viewers.LabelProvider#isLabelProperty(java.lang.Object, java.lang.String)
	 */
	public boolean isLabelProperty(Object element, String property) {
        return false;
    }
}
