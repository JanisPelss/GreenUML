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

import static edu.buffalo.cse.green.GreenException.GRERR_FILE_FORMAT;
import static edu.buffalo.cse.green.constants.PaletteStrings.CREATE_RELATIONSHIP_PREFIX_DESCRIPTION;
import static edu.buffalo.cse.green.constants.PaletteStrings.CREATE_RELATIONSHIP_SUFFIX_DESCRIPTION;
import static edu.buffalo.cse.green.constants.PaletteStrings.GROUP_CREATE_RELATIONSHIPS_LABEL;
import static edu.buffalo.cse.green.constants.PaletteStrings.GROUP_CREATE_TYPE_LABEL;
import static edu.buffalo.cse.green.constants.PaletteStrings.GROUP_MAIN_LABEL;
import static edu.buffalo.cse.green.constants.PaletteStrings.GROUP_VISIBILITY_LABEL;
import static edu.buffalo.cse.green.constants.PaletteStrings.NOTE_DESCRIPTION;
import static edu.buffalo.cse.green.constants.PaletteStrings.NOTE_LABEL;
import static edu.buffalo.cse.green.constants.PaletteStrings.SELECTION_DESCRIPTION;
import static edu.buffalo.cse.green.constants.PaletteStrings.SELECTION_LABEL;
import static edu.buffalo.cse.green.constants.PaletteStrings.TOGGLEFISH_DESCRIPTION;
import static edu.buffalo.cse.green.constants.PaletteStrings.TOGGLEFISH_LABEL;
import static edu.buffalo.cse.green.constants.XMLConstants.XML_BENDPOINT;
import static edu.buffalo.cse.green.constants.XMLConstants.XML_BENDPOINTS;
import static edu.buffalo.cse.green.constants.XMLConstants.XML_BENDPOINT_X;
import static edu.buffalo.cse.green.constants.XMLConstants.XML_BENDPOINT_Y;
import static edu.buffalo.cse.green.constants.XMLConstants.XML_GREEN_VERSION;
import static edu.buffalo.cse.green.constants.XMLConstants.XML_NOTE;
import static edu.buffalo.cse.green.constants.XMLConstants.XML_NOTE_HEIGHT;
import static edu.buffalo.cse.green.constants.XMLConstants.XML_NOTE_TEXT;
import static edu.buffalo.cse.green.constants.XMLConstants.XML_NOTE_WIDTH;
import static edu.buffalo.cse.green.constants.XMLConstants.XML_NOTE_X;
import static edu.buffalo.cse.green.constants.XMLConstants.XML_NOTE_Y;
import static edu.buffalo.cse.green.constants.XMLConstants.XML_RELATIONSHIP;
import static edu.buffalo.cse.green.constants.XMLConstants.XML_RELATIONSHIP_CLASS;
import static edu.buffalo.cse.green.constants.XMLConstants.XML_RELATIONSHIP_SOURCE_PROJECT;
import static edu.buffalo.cse.green.constants.XMLConstants.XML_RELATIONSHIP_SOURCE_TYPE;
import static edu.buffalo.cse.green.constants.XMLConstants.XML_RELATIONSHIP_TARGET_PROJECT;
import static edu.buffalo.cse.green.constants.XMLConstants.XML_RELATIONSHIP_TARGET_TYPE;
import static edu.buffalo.cse.green.constants.XMLConstants.XML_TYPE;
import static edu.buffalo.cse.green.constants.XMLConstants.XML_TYPE_HEIGHT;
import static edu.buffalo.cse.green.constants.XMLConstants.XML_TYPE_NAME;
import static edu.buffalo.cse.green.constants.XMLConstants.XML_TYPE_PROJECT;
import static edu.buffalo.cse.green.constants.XMLConstants.XML_TYPE_WIDTH;
import static edu.buffalo.cse.green.constants.XMLConstants.XML_TYPE_X;
import static edu.buffalo.cse.green.constants.XMLConstants.XML_TYPE_Y;
import static edu.buffalo.cse.green.constants.XMLConstants.XML_UML;
import static edu.buffalo.cse.green.preferences.PreferenceInitializer.P_AUTOSAVE;
import static edu.buffalo.cse.green.preferences.PreferenceInitializer.P_DISPLAY_INCREMENTAL_EXPLORER_DIA;
import static edu.buffalo.cse.green.preferences.PreferenceInitializer.P_FORCE_DIA_IN_PROJECT;
import static edu.buffalo.cse.green.preferences.PreferenceInitializer.P_MANHATTAN_ROUTING;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.lang.reflect.Constructor;
import java.net.URI;
import java.util.ArrayList;
import java.util.EventObject;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.regex.Pattern;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.ListenerList;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.draw2d.BendpointConnectionRouter;
import org.eclipse.draw2d.ConnectionRouter;
import org.eclipse.draw2d.ManhattanConnectionRouter;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.gef.DefaultEditDomain;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPartFactory;
import org.eclipse.gef.KeyHandler;
import org.eclipse.gef.KeyStroke;
import org.eclipse.gef.MouseWheelHandler;
import org.eclipse.gef.MouseWheelZoomHandler;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.commands.CommandStackListener;
import org.eclipse.gef.editparts.ScalableFreeformRootEditPart;
import org.eclipse.gef.editparts.ZoomManager;
import org.eclipse.gef.palette.CombinedTemplateCreationEntry;
import org.eclipse.gef.palette.CreationToolEntry;
import org.eclipse.gef.palette.PaletteEntry;
import org.eclipse.gef.palette.PaletteGroup;
import org.eclipse.gef.palette.PaletteListener;
import org.eclipse.gef.palette.PaletteRoot;
import org.eclipse.gef.palette.PaletteStack;
import org.eclipse.gef.palette.PanningSelectionToolEntry;
import org.eclipse.gef.palette.ToolEntry;
import org.eclipse.gef.requests.BendpointRequest;
import org.eclipse.gef.requests.SimpleFactory;
import org.eclipse.gef.tools.ConnectionCreationTool;
import org.eclipse.gef.ui.actions.ActionRegistry;
import org.eclipse.gef.ui.actions.ZoomInAction;
import org.eclipse.gef.ui.actions.ZoomOutAction;
import org.eclipse.gef.ui.palette.FlyoutPaletteComposite;
import org.eclipse.gef.ui.palette.PaletteViewer;
import org.eclipse.gef.ui.parts.GraphicalEditorWithFlyoutPalette;
import org.eclipse.gef.ui.parts.GraphicalViewerKeyHandler;
import org.eclipse.gef.ui.parts.ScrollingGraphicalViewer;
import org.eclipse.jdt.core.IClassFile;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaModel;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.internal.core.JarPackageFragmentRoot;
import org.eclipse.jdt.internal.ui.JavaPluginImages;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IPathEditorInput;
import org.eclipse.ui.IPersistableElement;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.editors.text.ILocationProvider;
import org.eclipse.ui.ide.FileStoreEditorInput;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.model.IWorkbenchAdapter;
import org.eclipse.ui.model.WorkbenchAdapter;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.views.contentoutline.IContentOutlinePage;

import edu.buffalo.cse.green.GreenException;
import edu.buffalo.cse.green.PlugIn;
import edu.buffalo.cse.green.constants.PluginConstants;
import edu.buffalo.cse.green.editor.action.ContextAction;
import edu.buffalo.cse.green.editor.action.Submenu;
import edu.buffalo.cse.green.editor.controller.AbstractPart;
import edu.buffalo.cse.green.editor.controller.RelationshipPart;
import edu.buffalo.cse.green.editor.controller.RootPart;
import edu.buffalo.cse.green.editor.model.AbstractModel;
import edu.buffalo.cse.green.editor.model.MemberModel;
import edu.buffalo.cse.green.editor.model.NoteModel;
import edu.buffalo.cse.green.editor.model.RelationshipModel;
import edu.buffalo.cse.green.editor.model.RootModel;
import edu.buffalo.cse.green.editor.model.TypeModel;
import edu.buffalo.cse.green.editor.model.commands.CreateBendpointCommand;
import edu.buffalo.cse.green.editor.save.ISaveFormat;
import edu.buffalo.cse.green.editor.view.RelationshipFigure;
import edu.buffalo.cse.green.editor.view.RootFigure;
import edu.buffalo.cse.green.relationships.RelationshipCache;
import edu.buffalo.cse.green.relationships.RelationshipGroup;
import edu.buffalo.cse.green.relationships.RelationshipRecognizer;
import edu.buffalo.cse.green.relationships.RelationshipSubtype;
import edu.buffalo.cse.green.types.ITypeProperties;
import edu.buffalo.cse.green.util.JavaProjectUtil;
import edu.buffalo.cse.green.xml.XMLConverter;
import edu.buffalo.cse.green.xml.XMLNode;

/**
 * The editor. Displays a UML diagram that represents all the parts of the
 * <code>JavaModel</code> that have been loaded into it. New projects,
 * packages, compilation units, and types can be loaded in. Methods and fields,
 * however, cannot be added in without their parent <code>IType</code>.
 * 
 * @author bcmartin
 * @author hk47
 * @author zgwang
 */

public class DiagramEditor extends GraphicalEditorWithFlyoutPalette implements
		CommandStackListener, ISelectionProvider {
	static {
		_editors = new ArrayList<DiagramEditor>();
	}

//	private boolean _ignoreMenuSelection = false;

	/**
	 * Reference string for the context menu in our editor.
	 */
	private static final String UML_CONTEXT_MENU_ID = "#PopupMenu";

	/**
	 * A list of all editors currently open.
	 */
	private static List<DiagramEditor> _editors;

	/**
	 * A list of listeners to the selections in our editor.
	 */
	private static ListenerList _selectionChangedListeners = new ListenerList();

	/**
	 * The current selection in our editor.
	 */
	private IStructuredSelection _selection = StructuredSelection.EMPTY;

	/**
	 * Handler for shortcut key presses.
	 */
	private KeyHandler _sharedKeyHandler;

	/**
	 * Stores a reference to the active editor.
	 */
	private static DiagramEditor ACTIVE_EDITOR;

	/**
	 * The top-level model displayed in the diagram.
	 */
	private RootModel _root;

	/**
	 * A reference to the context menu's manager.
	 */
	private IMenuManager _menuManager;

	/**
	 * Information contained in the current selection.
	 */
	private Context _context;

	/**
	 * A list of relationships that have changed and have not yet been
	 * processed.
	 */
	private Set<RelationshipModel> _relationshipChanges;

	/**
	 * A list of bendpoints that should be added when loading is complete and
	 * the root part is available.
	 */
	private List<BendpointInformation> _bendpoints;
	private CompilationUnitMap _cuMap;
	private MenuManager _contextMenu;
	private List<Filter> _filters;
	private static ConnectionRouter CONNECTION_ROUTER;
	private static OutlinePage _outlinePage;
	
	/**
	 * GEF's "root part" different from green's RootPart
	 */
	private ScalableFreeformRootEditPart _gefRootPart;


	/**
	 * Constructs an instance of the editor.
	 */
	public DiagramEditor() {
		updateConnectionRouter();
		_editors.add(this);
		_bendpoints = new ArrayList<BendpointInformation>();
		setEditDomain(new DefaultEditDomain(this));
		getCommandStack().addCommandStackListener(this);
		getCommandStack().setUndoLimit(100);
		_root = new RootModel();
		_cuMap = new CompilationUnitMap();
		_filters = new ArrayList<Filter>();

		getPalettePreferences().setPaletteState(FlyoutPaletteComposite.STATE_PINNED_OPEN);	
	}

	public Object getAdapter(Class adapter) {
		if (IContentOutlinePage.class.equals(adapter)) {
			return OutlinePage.getInstance();
		}
		
		return super.getAdapter(adapter);
	}

	/**
	 * Updates the connection router based on the user's preference.
	 */
	private void updateConnectionRouter() {
		if (PlugIn.getBooleanPreference(P_MANHATTAN_ROUTING)) {
			CONNECTION_ROUTER = new ManhattanConnectionRouter();
		} else {
			CONNECTION_ROUTER = new BendpointConnectionRouter();
		}
	}

	/**
	 * @return The selection tool's entry in the palette.
	 */
	public static ToolEntry getSelectionTool() {
		return DiagramPaletteFactory.getSelectionTool();
	}
	
	/**
	 * Creates the editor's context menu.
	 */
	private void buildMenu(IMenuManager menuManager) {
		Map<String, MenuManager> menus =
			new HashMap<String, MenuManager>();
		Map<MenuManager, List<ContextAction>> mActions =
			new HashMap<MenuManager, List<ContextAction>>();
		
		MenuManager inv = new MenuManager("inv");
		inv.setVisible(false);
		menus.put(Submenu.Invisible.toString(), inv);
		
		// get all actions that must be added to the menu
		List<ContextAction> actions = new ArrayList<ContextAction>();

		actions.addAll(PlugIn.getActions());
		
		// moved to PlugIn, after the xml plugins are initialized
		// so that accelerator keys work.
		/*for (Class partClass : PlugIn.getRelationships()) {
			ContextAction action =
				new AlterRelationshipVisibilityAction(partClass);
			actions.add(action);
			action =
				new IncrementalExploreSingleAction(partClass);
			actions.add(action);
		}*/
		
		// add the actions to their appropriate submenus
		List<ContextAction> lastItems = new ArrayList<ContextAction>();
		
		for (ContextAction action : actions) {
			// add in menu group if it doesn't exist
			MenuManager submenu = menus.get(action.getPath());
			
			// initialize the action
			action.calculateEnabled();
			action.setSelectionProvider(this);
			action.setText(action.getLabel());
			
			// if the submenu doesn't exist, create it
			if (submenu == null) {
				submenu = createMenuGroup(menus, action.getPath());

				// add it to the end if it's a menu item
				if (submenu == null) {
					lastItems.add(action);
					continue;
				}
				
				List<ContextAction> actionList = mActions.get(submenu);
				
				if (actionList == null) {
					actionList = new ArrayList<ContextAction>();
					mActions.put(submenu, actionList);
				}
				
				actionList.add(action);
			}
		}
		
		// sort the menu alphabetically
		while (mActions.keySet().size() > 0) {
			String firstAlpha = "zzz";
			MenuManager addMenu = null;
			
			for (MenuManager menu : mActions.keySet()) {
				if (menu.getMenuText().compareTo(firstAlpha) < 0) {
					firstAlpha = menu.getMenuText();
					addMenu = menu;
				}
			}
			
			_contextMenu.add(addMenu);
			List<ContextAction> actionList = mActions.get(addMenu);
			mActions.remove(addMenu);
			
			while (actionList.size() > 0) {
				firstAlpha = "zzz";
				ContextAction addAction = null;
				
				for (ContextAction cAction : actionList) {
					if (cAction.getLabel().compareTo(firstAlpha) < 0) {
						firstAlpha = cAction.getLabel();
						addAction = cAction;
					}
				}
				
				addMenu.add(addAction);
				actionList.remove(addAction);
			}
		}
			
		while (lastItems.size() > 0) {
			String firstAlpha = "zzz";
			ContextAction addAction = null;
			
			for (ContextAction cAction : lastItems) {
				if (cAction.getLabel().compareTo(firstAlpha) < 0) {
					firstAlpha = cAction.getLabel();
					addAction = cAction;
				}
			}
			
			_contextMenu.add(addAction);
			lastItems.remove(addAction);
		}
		
		// add quick fixes, if applicable
		IJavaElement element = getContext().getElement();
		
		if (element instanceof IMember) {
			MemberModel model =	(MemberModel) getRootModel().getModelFromElement(element);
			model.appendQuickFixActionsToMenu(_contextMenu);
		}
	}

	/**
	 * Creates and returns a menu with the given path.
	 * 
	 * @param menus - A map of menu prefixes to their corresponding menus.
	 * @param path - The path to the desired menu.
	 * @return The appropriate menu.
	 */
	private MenuManager createMenuGroup(Map<String, MenuManager> menus,
			Submenu path) {
		StringTokenizer tokens = new StringTokenizer(path.toString(), "/");
		if (!tokens.hasMoreTokens()) return null;
		
		// create the base menu
		String menuName = tokens.nextToken();
		MenuManager menu = createMenuGroupHelper(menus, menuName);
		
		// nest menus using the path
		while (tokens.hasMoreTokens()) {
			menuName += "/" + tokens.nextToken();
			MenuManager submenu = createMenuGroupHelper(menus, menuName);
			menu.add(submenu);
			menu = submenu;
		}
		
		return menu;
	}

	/**
	 * Creates menus if they don't exist until the desired menu path exists.
	 * 
	 * @param menus - The map of menu prefixes to their corresponding menus.
	 * @param menuName - The name of the menu to create.
	 * @return The created menu, or the menu if it already existed.
	 */
	private MenuManager createMenuGroupHelper(Map<String, MenuManager> menus,
			String menuName) {
		MenuManager menu = menus.get(menuName);
		
		if (menu == null) {
			menu = new MenuManager(
					menuName.substring(menuName.lastIndexOf("/") + 1));
			menus.put(menuName, menu);
		}
		
		return menu;
	}

	/**
	 * Updates the context menu's contents, hiding inappropriate contents.
	 * 
	 * @param menu - The menu to update.
	 */
	private void updateMenuDisplay(IMenuManager menu) {
		AbstractModel selectedModel = _context.getModel();

		for (IContributionItem item : menu.getItems()) {
			if (item instanceof IMenuManager) {
				updateMenuDisplay((IMenuManager) item);
			} else if (item instanceof ActionContributionItem) {
				ActionContributionItem aItem = (ActionContributionItem) item;
				ContextAction action = (ContextAction) aItem.getAction();
				aItem.setVisible(action.isVisible(selectedModel));
			}
		}
	}
	
	/**
	 * Finds the editor that holds the given <code>IJavaProject</code>.
	 * 
	 * @param project - The project to find.
	 */
	public static DiagramEditor findProjectEditor(IJavaProject project) {
		for (int x = 0; x < _editors.size(); x++) {
			DiagramEditor editor = (DiagramEditor) _editors.get(x);
			IJavaProject editorProject = editor.getProject();
			if (editorProject == null) continue;
			
			if (project.getHandleIdentifier().equals(
					editorProject.getHandleIdentifier())) {
				return editor; }
		}

		return null;
	}

	/**
	 * Returns the project that the current <code>DiagramEditor</code> is
	 * displaying the contents of.
	 * 
	 * @return The project that's currenly displayed in the editor.
	 */
	public IJavaProject getProject() {
		return getRootModel().getProject();
	}

	/**
	 * @see org.eclipse.ui.IWorkbenchPart#dispose()
	 */
	public void dispose() {
		try {
			markAsSaved();
		} catch (Throwable t) {
			// don't care
		}
		
		_editors.remove(this);
		getRootModel().dispose();
		if (ACTIVE_EDITOR == this) ACTIVE_EDITOR = null;
		
		super.dispose();
	}

	/**
	 * Adds a bendpoint to a relationship at the given location.
	 * 
	 * @param rModel - The <code>RelationshipsModel</code>.
	 * @param location - The location of the bendpoint.
	 */
	public void addBendpoint(RelationshipModel rModel, Point location) {
		_bendpoints.add(new BendpointInformation(rModel, location));
	}
	
	/**
	 * @see org.eclipse.ui.ISaveablePart#doSave(org.eclipse.core.runtime.IProgressMonitor)
	 */
	public void doSave(IProgressMonitor iMonitor) {
		DiagramEditorFilePolicies.save(this, false);
	}

	/**
	 * @see org.eclipse.ui.ISaveablePart#doSaveAs()
	 */
	public void doSaveAs() {
		DiagramEditorFilePolicies.save(this, true);
	}

	/**
	 * @see org.eclipse.ui.IEditorPart#init(org.eclipse.ui.IEditorSite, org.eclipse.ui.IEditorInput)
	 */
	public void init(IEditorSite iSite, IEditorInput iInput)
			throws PartInitException {
		super.init(iSite, iInput);

		if (iInput instanceof FileStoreEditorInput){
			   URI resourceID = ((FileStoreEditorInput)iInput).getURI();
			   setEditorInput(new GreenEditorInput(new File(resourceID)));
		}
		
		setPartName(getEditorInput().getName());

		// set global actions
		IActionBars bars = getEditorSite().getActionBars();

		for (IContributionItem item : bars.getToolBarManager().getItems()) {
			if (item instanceof ActionContributionItem) {
				// set action handlers (shortcut keys) as appropriate
				ActionContributionItem aItem = (ActionContributionItem) item;
				ContextAction action = (ContextAction) aItem.getAction();
				bars.setGlobalActionHandler(
						action.getGlobalActionHandler().getId(), action);
			}
		}

		bars.updateActionBars();
	}

	/**
	 * @see org.eclipse.gef.ui.parts.GraphicalEditor#initializeGraphicalViewer()
	 */
	protected void initializeGraphicalViewer() {
		// associate Green's model and controller with the editor
		getGraphicalViewer().setContents(getRootModel());
		getRootPart().setEditor(this);

		// get the input file to the editor and handle loading from it
		IPathEditorInput input = (IPathEditorInput) getEditorInput();
		DiagramEditorFilePolicies.load(this,
				new File(input.getPath().toOSString()));
		
		createContextMenu();
		
		// create bendpoints (saved) on relationship arcs
		for (BendpointInformation bendpoint : _bendpoints) {
			BendpointRequest request = bendpoint.getBendpointRequest(this);
			if (request == null) continue;
			
			RelationshipFigure rFigure =
				(RelationshipFigure) request.getSource().getFigure();
			
			execute(new CreateBendpointCommand(rFigure, request));
		}
		
		// pretend the editor is saved
		markAsSaved();
		
		
		//This code have been replaced with the addition of the flyout
		//palette and PaletteStacks
		//@author zgwang
//		
//		getPaletteViewer().addPaletteListener(new PaletteListener() {
//			/**
//			 * @see org.eclipse.gef.palette.PaletteListener#activeToolChanged(org.eclipse.gef.ui.palette.PaletteViewer, org.eclipse.gef.palette.ToolEntry)
//			 */
//			public void activeToolChanged(final PaletteViewer palette,
//					final ToolEntry tool) {
//				if (!PlugIn.isUserMode()) return;
//				
//				final List<RelationshipSubtype> subtypes =
//					PlugIn.getRelationshipSubtypes(tool.getLabel());
//				
//				if (subtypes == null) return;
//				
//				if (subtypes.size() < 2) return;
//				
//				if (_ignoreMenuSelection) {
//					_ignoreMenuSelection = false;
//					return;
//				}
//				
//				Menu menu = new Menu(palette.getControl());
//				
//				/* If a relationship with more than one subtype exists, we want
//				 * to display a context menu when the palette tool is clicked so
//				 * that the user can select which subtype to create.
//				 */
//				for (final RelationshipSubtype subtype : subtypes) {
//					MenuItem item = new MenuItem(menu, 0);
//					item.setText(subtype.getLabel());
//					item.addSelectionListener(new SelectionListener() {
//						/**
//						 * @see org.eclipse.swt.events.SelectionListener#widgetSelected(org.eclipse.swt.events.SelectionEvent)
//						 */
//						public void widgetSelected(SelectionEvent e) {
//							tool.setToolProperty(
//									CreationTool.PROPERTY_CREATION_FACTORY,
//									new SimpleFactory(
//											subtype.getGroup().getPartClass()));
//							_ignoreMenuSelection = true;
//							palette.setActiveTool(tool);
//						}
//
//						/**
//						 * @see org.eclipse.swt.events.SelectionListener#widgetDefaultSelected(org.eclipse.swt.events.SelectionEvent)
//						 */
//						public void widgetDefaultSelected(SelectionEvent e) {}
//					});
//				}
//				
//				// display the menu
//				menu.setLocation(getSite().getShell().getDisplay().getCursorLocation());
//				menu.setVisible(true);
//			}
//		});
		
		// save the editor
		doSave(null);
		
		// makes the palette buttons work
		final PaletteViewer v = getGraphicalViewer().getEditDomain().getPaletteViewer(); 
		getGraphicalViewer().getEditDomain().getPaletteViewer().addPaletteListener( new PaletteListener() {
			
			private ToolEntry _lastEntry = v.getActiveTool( ); 
			
			public void activeToolChanged ( PaletteViewer palette,
					ToolEntry tool ) {
				if( tool instanceof PaletteButton ) {
					((PaletteButton)tool).act( );
					v.setActiveTool( _lastEntry );
				} else _lastEntry = tool;
			}
			
		});
		
	}

	/**
	 * @return The <code>RootPart</code>
	 */
	public RootPart getRootPart() {
		// return the graphical viewer, if it exists
		return getGraphicalViewer() == null ? null
				: (RootPart) getGraphicalViewer().getContents();
	}

	/**
	 * @return The <code>UMLRootModel</code>.
	 */
	public RootModel getRootModel() {
		return _root;
	}

	public void createContextMenu(IMenuManager m) {
		buildMenu(m);
		updateMenuDisplay(m);
		_menuManager = m;
		m.setVisible(true);
	}

	/**
	 * Returns the list of currently open editors.
	 */
	public static List<DiagramEditor> getEditors() {
		return _editors;
	}

	/**
	 * @see org.eclipse.ui.ISaveablePart#isSaveAsAllowed()
	 */
	public boolean isSaveAsAllowed() {
		return true;
	}

	/**
	 * Creates the pop-up menu for right-click events.
	 */
	private void createContextMenu() {
		_contextMenu = new MenuManager(UML_CONTEXT_MENU_ID);
		_contextMenu.setRemoveAllWhenShown(true);
		_contextMenu.addMenuListener(new IMenuListener() {
			/**
			 * @see org.eclipse.jface.action.IMenuListener#menuAboutToShow(org.eclipse.jface.action.IMenuManager)
			 */
			public void menuAboutToShow(IMenuManager m) {
				createContextMenu(m);
			}
		});

		// associate the control with the menu
		Control control = getGraphicalViewer().getControl();
		Menu menu = _contextMenu.createContextMenu(control);
		control.setMenu(menu);
	}
	
	/**
	 * Gets the ZoomManager for this.
	 * @return zoom manager
	 */
	public ZoomManager getZoomManager() { return _gefRootPart.getZoomManager(); }
	
	/**
	 * @see org.eclipse.gef.ui.parts.GraphicalEditor#configureGraphicalViewer()
	 */
	protected void configureGraphicalViewer() {
		super.configureGraphicalViewer();
		ScrollingGraphicalViewer viewer = (ScrollingGraphicalViewer)getGraphicalViewer();

		// Scroll-wheel Zoom
		getGraphicalViewer().setProperty(MouseWheelHandler.KeyGenerator.getKey(SWT.MOD1), 
				MouseWheelZoomHandler.SINGLETON);		
		
		// associate appropriate handlers with the viewer
		_gefRootPart = new ScalableFreeformRootEditPart();
		ZoomManager zoom = _gefRootPart.getZoomManager();
		List<String> zoomLevels = new ArrayList<String>(3);
		zoomLevels.add(ZoomManager.FIT_ALL);
		zoomLevels.add(ZoomManager.FIT_WIDTH);
		zoomLevels.add(ZoomManager.FIT_HEIGHT);
		zoom.setZoomLevelContributions(zoomLevels);
		IAction zoomIn = new ZoomInAction(_gefRootPart.getZoomManager());
		IAction zoomOut = new ZoomOutAction(_gefRootPart.getZoomManager());
		getActionRegistry().registerAction(zoomIn);
		getActionRegistry().registerAction(zoomOut);
		
		//These lines don't seem to do anything and will
		//remain commented out until an error occurs.
//		getSite().getKeyBindingService().registerAction(zoomIn);
//		getSite().getKeyBindingService().registerAction(zoomOut);
		
		viewer.setRootEditPart(_gefRootPart);
		viewer.setEditPartFactory(new DiagramPartFactory());
		viewer.setKeyHandler(new GraphicalViewerKeyHandler(viewer)
				.setParent(getCommonKeyHandler()));
		
		ActionRegistry registry = getActionRegistry();
		List<ContextAction> actions = PlugIn.getActions();
		
		// register context menu actions
		for (ContextAction action : actions) {
			registry.registerAction(action);
			int accelerator = action.getAccelerator();
			
			// enabled shortcut keys where appropriate
			if (accelerator != 0) {
				addKeyAction((char) accelerator, action);
			}
		}
	}

	/**
	 * Adds a shortcut key mapping to the editor.
	 * 
	 * @param key - The key to map.
	 * @param action - The action to map the key to.
	 */
	public void addKeyAction(char key, ContextAction action) {
		_sharedKeyHandler.put(KeyStroke.getPressed(key, key, 0), action);
	}

	/**
	 * @see org.eclipse.gef.ui.parts.GraphicalEditorWithPalette#getPaletteRoot()
	 */
	protected PaletteRoot getPaletteRoot() {
		PaletteRoot pRoot = DiagramPaletteFactory.createPaletteRoot( );
		return pRoot;
	}

	/**
	 * @see org.eclipse.ui.IWorkbenchPart#setFocus()
	 */
	public void setFocus() {
		ACTIVE_EDITOR = this;

		for (ContextAction action : PlugIn.getActions()) {
			action.setSelectionProvider(this);
		}

		autoSave();
	}

	/**
	 * Creates a new editor using the given selection.
	 * 
	 * @param selection - The selection of elements to add to the editor.
	 * @return The editor that was created.
	 */
	public static DiagramEditor createEditor(
			IStructuredSelection selection) throws JavaModelException {

		for (Object element : selection.toArray()) {
			if (element instanceof IJavaElement) {
				return createEditor((IJavaElement) element);
			}
		}
		
		return null;
	}

	/**
	 * Opens a blank editor.
	 * 
	 * @param element - The element to place the diagram file in.
	 * @return a reference to the opened editor, if successful.
	 */
	private static DiagramEditor createEditor(
			IJavaElement element) throws JavaModelException {
		IWorkbenchWindow dwindow = PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow();
		IWorkbenchPage workbenchPage = dwindow.getActivePage();
		IPackageFragment packFrag = null;
		IPath elementPath;

		// get the project itself, if desired (for creating DIA)
		if (element.isReadOnly() || PlugIn.getBooleanPreference(
				P_FORCE_DIA_IN_PROJECT)) {
			element = element.getAncestor(IJavaElement.JAVA_PROJECT);
		}
		
		if (element instanceof IJavaProject) {
			IJavaProject project = (IJavaProject) element;
			IJavaElement[] defaultPackages = JavaProjectUtil.getDefaultSourcePackages(project, false);
			if (defaultPackages.length>0) {
				IJavaElement javaElement = defaultPackages[0];
				if (javaElement instanceof IPackageFragment) {
					packFrag = (IPackageFragment)javaElement;
				} else {
					packFrag = (IPackageFragment) javaElement.getAncestor(
							IJavaElement.PACKAGE_FRAGMENT);					
				}
			}
		} else if (!(element instanceof IPackageFragment)) {
			packFrag = (IPackageFragment) element.getAncestor(
					IJavaElement.PACKAGE_FRAGMENT);
		} else {
			packFrag = (IPackageFragment) element;
		}

		// create a path to the diagram file with current extension
		elementPath = packFrag.getPath().append(
				packFrag.getJavaProject().getElementName() + "." + PluginConstants.GREEN_EXTENSION);
		
		try {
			IFile diaFile = DiagramEditor.getFileNotExist(
					element.getJavaProject().getProject(), elementPath);
			DiagramEditor editor = (DiagramEditor) IDE.openEditor(
					workbenchPage, diaFile, true);
			ACTIVE_EDITOR = editor;
			return editor;
		} catch (CoreException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}

	// -Selection--------------------------------------------------------------------

	/**
	 * @see org.eclipse.jface.viewers.ISelectionProvider#addSelectionChangedListener(ISelectionChangedListener listener)
	 */
	public void addSelectionChangedListener(ISelectionChangedListener listener) {
		_selectionChangedListeners.add(listener);
	}

	/**
	 * @see org.eclipse.jface.viewers.ISelectionProvider#getSelection()
	 */
	public ISelection getSelection() {
		// return null since Eclipse uses this to add items to our context menu
		return null;
	}

	/**
	 * @return information obtained from the current selection
	 */
	public Context getContext() {
		return _context;
	}

	/**
	 * @see org.eclipse.jface.viewers.ISelectionProvider#removeSelectionChangedListener(org.eclipse.jface.viewers.ISelectionChangedListener)
	 */
	public void removeSelectionChangedListener(
			ISelectionChangedListener listener) {
		_selectionChangedListeners.remove(listener);
	}

	/**
	 * @see org.eclipse.jface.viewers.ISelectionProvider#setSelection(org.eclipse.jface.viewers.ISelection)
	 */
	public void setSelection(ISelection selection) {
		if (!(selection instanceof IStructuredSelection)) return;
		
		// set the formerly selected part to its original color
		if (_context != null) {
			_context.getPart().setInitialBackgroundColor();
		}
		
		
		// set Green's context to the currently selected values
		IStructuredSelection sSelection = (IStructuredSelection) selection;
		if (!(sSelection.getFirstElement() instanceof AbstractPart)) {
			return;
		}
		
		_selection = sSelection;
		_context = new Context(_selection);
		
		AbstractPart part = _context.getPart();
		
		// set the selected part to the selection color
		if ((part != null) && (part.getParent() instanceof AbstractPart)) {
			part.setSelectedBackgroundColor();
		}
	}

	/**
	 * @see org.eclipse.ui.ISelectionListener#selectionChanged(org.eclipse.ui.IWorkbenchPart, org.eclipse.jface.viewers.ISelection)
	 */
	public void selectionChanged(IWorkbenchPart part, ISelection selection) {
		if (selection.isEmpty()) { return; }
		super.selectionChanged(part, selection);
		
		if (part instanceof DiagramEditor) {
			if (_outlinePage != null) {
				_outlinePage.setSelection(selection);
			}
		}
		
		setSelection(selection);
	}

	// ------------------------------------------------------------------------------
	/**
	 * Singleton: gets the key handler for editors. This method is responsible
	 * for all of the keyboard shortcuts handled by our editor.
	 * 
	 * @return The <code>KeyHandler</code>.
	 */
	protected KeyHandler getCommonKeyHandler() {
		if (_sharedKeyHandler == null) {
			_sharedKeyHandler = new KeyHandler();
		}

		return _sharedKeyHandler;
	}

	/**
	 * Executes a command in this editor.
	 */
	public void execute(Command command) {
		getCommandStack().execute(command);
		refresh();
	}

	/**
	 * @see org.eclipse.gef.commands.CommandStackListener#commandStackChanged(java.util.EventObject)
	 */
	public void commandStackChanged(EventObject event) {
		super.commandStackChanged(event);
		checkDirty();
	}

	/**
	 * Checks to see if there were any changes to the editor
	 */
	public void checkDirty() {
		firePropertyChange(IEditorPart.PROP_DIRTY);
	}

	/**
	 * @see org.eclipse.ui.ISaveablePart#isSaveOnCloseNeeded()
	 */
	public boolean isSaveOnCloseNeeded() {
		return isDirty();
	}

	/**
	 * @see org.eclipse.ui.ISaveablePart#isDirty()
	 */
	public boolean isDirty() {
		return getCommandStack().isDirty();
	}

	/**
	 * Creates a file at the specified path and returns it. If the file already
	 * exists, a number will be concatenated to the path to create a new file.
	 * 
	 * @param project
	 *            The Java project to create the file in
	 * @param path
	 *            The path of the file to create
	 * @return The created file
	 * @throws CoreException
	 * @throws IOException
	 */
	public static IFile getFileNotExist(IProject project, IPath path)
			throws CoreException, IOException {
		// create a new path to the file
		IFile file;
		String sPath = path.toOSString();
		int extensionIndex = sPath.lastIndexOf('.');
		String extension = sPath.substring(extensionIndex);
		sPath = sPath.substring(0, extensionIndex);
		
		int x = 1;

		// mutate the path until it is unique 
		do {
			IPath newPath;

			if (x == 1) {
				newPath = new Path(sPath + extension).removeFirstSegments(1);
			} else {
				newPath = new Path(sPath + "." + x + extension).removeFirstSegments(1);
			}
			
			file = project.getFile(newPath);
			x++;
		} while (file.isAccessible());

		// create the empty file
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ObjectOutputStream oos = new ObjectOutputStream(baos);
		ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());

		file.create(bais, true, null);
		oos.close();
		baos.close();
		bais.close();

		// notify the workspace that the file has changed
		file.refreshLocal(IResource.DEPTH_INFINITE, null);
		return file;
	}

	/**
	 * Returns the file that is currently being edited. This will attempt to see
	 * if Eclipse knows about the file. If not, the fallback file will be used
	 * instead.
	 * 
	 * @return The <code>IFile</code> representing the file that is currently
	 *         being edited.
	 */
	public IFile getCurrentFile() {
		IEditorInput input = getEditorInput();

		if (input instanceof FileEditorInput) {
			FileEditorInput fInput = (FileEditorInput) input;
			return fInput.getFile();
		}
		
		return null;
	}

	/**
	 * Sets the editor's input to the given input.
	 * 
	 * @param input - The editor's input.
	 */
	public void setEditorInput(IEditorInput input) {
		setInput(input);
	}
	
	/**
	 * @see org.eclipse.ui.part.WorkbenchPart#setPartName(java.lang.String)
	 */
	public void setPartName(String partName) {
		super.setPartName(partName);
	}

	/**
	 * Saves the given string to the current file 
	 * 
	 * @param contents - The text to save
	 * @return true upon success, false otherwise
	 */
	public boolean saveFile(String contents) {
		return saveFile(getCurrentFile(), contents);
	}

	/**
	 * Refreshes the elements in the current editor
	 */
	public void synchronizeCurrentFile() {
		try {
			if (getCurrentFile() == null) return;
			
			getCurrentFile().refreshLocal(IResource.DEPTH_INFINITE, PlugIn
					.getEmptyProgressMonitor());
		} catch (CoreException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Saves the given contents to the given file.
	 * 
	 * @param iFile - The file to save to.
	 * @param contents - The text to save.
	 * @return true upon success, false otherwise.
	 */
	public boolean saveFile(IFile iFile, String contents) {
		File file = null;
		
		if (iFile != null) {
			// use the given file
			file = PlugIn.getWorkspaceRoot().getFile(
					iFile.getFullPath()).getLocation().toFile();
		} else {
			if (getEditorInput() instanceof GreenEditorInput) {
				// Green file format
				GreenEditorInput input = (GreenEditorInput) getEditorInput();
				file = input.getPath().toFile();
			} else {
				// Invalid format
				GreenException.illegalOperation("Illegal editor input type: " +
						getEditorInput().getClass());
			}
		}
		
		// write the contents into the file (perform the save)
		try {
			FileWriter fWriter = new FileWriter(file);
			PrintWriter pWriter = new PrintWriter(fWriter);
			pWriter.println(contents);
			pWriter.close();
			fWriter.close();
			synchronizeCurrentFile();
			return true;
		} catch (IOException e) {
			e.printStackTrace();
		}

		return false;
	}

	/**
	 * Marks the current editor as being saved (not dirty).
	 */
	public void markAsSaved() {
		getCommandStack().markSaveLocation();
		checkDirty();
	}

	/**
	 * Refreshes the editor
	 */
	public void refresh() {
		refresh(false);
	}

	/**
	 * Refreshes the editor.
	 * 
	 * @param forceUpdateRelationships - Forces the updating of relationships.
	 * @author zgwang
	 */
	public void refresh(final boolean forceUpdateRelationships) {
		BusyIndicator.showWhile(Display.getCurrent(), new Runnable() {
			/**
			 * @see java.lang.Runnable#run()
			 */
			public void run() {
				// use Manhattan or normal routing, as desired
				updateConnectionRouter();

				try {
					// build workspace, if necessary
					if (getProject() != null) {
						IWorkspace w = getProject().getUnderlyingResource().getWorkspace();
						w.build(IncrementalProjectBuilder.INCREMENTAL_BUILD, null);
					}
				} catch (CoreException e) {
					GreenException.critical(e);
				}
				
				// refresh relationships
				refreshRelationships(forceUpdateRelationships);
				// refresh model
				getRootModel().refresh();
				// refresh figures
				List<DiagramEditor> allEditors = getEditors();
				for(DiagramEditor editor : allEditors) {
					((RootFigure)(editor.getRootPart().getFigure())).updateEditor();
				}
			}
		});
		
		if (_outlinePage != null) {
			_outlinePage.update(this);
		}
	}
	
	/**
	 * @return true if an undo can be performed, false otherwise
	 */
	public boolean canUndo() {
		return getCommandStack().canUndo();
	}

	/**
	 * Performs an undo
	 */
	public void undo() {
		if (!canUndo()) {
			GreenException.illegalOperation("Undo was unsuccessful");
		}
		
		getCommandStack().undo();
	}

	/**
	 * @return true if a redo can be performed, false otherwise
	 */
	public boolean canRedo() {
		return getCommandStack().canRedo();
	}

	/**
	 * Performs a redo
	 */
	public void redo() {
		if (!canRedo()) {
			GreenException.illegalOperation("Redo was unsuccessful");
		}
		
		getCommandStack().redo();
	}

	/**
	 * @return a reference to the context menu's manager
	 */
	public IMenuManager getMenuManager() {
		return _menuManager;
	}

	/**
	 * Finds all relationships that have the given element as their source.
	 * 
	 * @param element - The element to find relationships for.
	 */
	private void findRelationships(IJavaElement element) {
		long modified;
		
		// if the element contains errors, quit
		try {
			if (!element.exists() || !element.isStructureKnown()) return;
		} catch (JavaModelException e) {
			e.printStackTrace();
			return;
		}
		
		CompilationUnit cu;
		String id = element.getHandleIdentifier();
		
		// generate AST if necessary - check modification stamp
		Long modifiedStore = _cuMap.getModificationStamp(id);
		IResource resource = element.getResource();

		if (resource == null) {
			if (_cuMap.getCompilationUnit(id) != null) {
				modifiedStore = new Long(0);
			}
			
			modified = 0;
		} else {
			modified = resource.getModificationStamp();
		}
		
		// if there isn't an up-to-date AST, create one
		if ((modifiedStore == null) || (modified != modifiedStore)) {
			ASTParser parser = ASTParser.newParser(AST.JLS3);
			parser.setResolveBindings(true);
			
			if (element instanceof ICompilationUnit) {
				parser.setSource((ICompilationUnit) element);
			} else if (element instanceof IClassFile) {
				// only search through the class if it has source code attached
				IClassFile classFile = (IClassFile) element;
				
				try {
					if (classFile.getSource() == null) {
						return;
					}
				} catch (JavaModelException e) {
					e.printStackTrace();
				}
				
				parser.setSource(classFile);
			} else {
				GreenException.illegalOperation("Illegal element type: "
						+ element.getClass());
			}
			
			cu = (CompilationUnit) parser.createAST(null);
			_cuMap.put(element, cu);
		} else {
			cu = _cuMap.getCompilationUnit(id);
		}
		
		// run the recognizers
		for (Class klass : PlugIn.getRelationships()) {
			RelationshipRecognizer recognizer = PlugIn.getRelationshipGroup(
					klass).getRecognizer();
			
			// run the recognizer
			recognizer.run(cu, getRootModel().getRelationshipCache());
		}
	}
	
	public void forceRefreshRelationships()
	{
		refreshRelationships(true);
	}
	
	/**
	 * Refreshes the relationships in the editor
	 * 
	 * @param force - If true, will run the relationship recognizers. If false,
	 * will run the relationship recognizers only if they are not disabled in
	 * the <code>PlugIn</code> instance. 
	 */
	private void refreshRelationships(boolean force) {
		List<String> visitedElements = new ArrayList<String>();
		List<String> outdated = new ArrayList<String>();
		
		if (!force && !PlugIn.isRecognizersEnabled()) {
			return;
		}
		
		// get a list of all classes and compilation units in the editor
		List<IJavaElement> elements = new ArrayList<IJavaElement>();
		elements.addAll(getRootModel().getElementsOfKind(IJavaElement.COMPILATION_UNIT));
		elements.addAll(getRootModel().getElementsOfKind(IJavaElement.CLASS_FILE));

		// find relationships attached to those elements
		for (IJavaElement element : elements) {
			findRelationships(element);
			visitedElements.add(element.getHandleIdentifier());
		}
		
		// remove outdated CompilationUnit objects from the map 
		for (String cu : _cuMap.keySet()) {
			if (!(visitedElements.contains(cu))) {
				outdated.add(cu);
			}
		}
		
		for (String obsolete : outdated) {
			_cuMap.remove(obsolete);
		}
		
		Set<RelationshipModel> toRemove = new HashSet<RelationshipModel>();
		_relationshipChanges = getRootModel().getRelationshipCache().processChanges();
		
		// update the relationships as appropriate
		for (RelationshipModel rModel : _relationshipChanges) {
			if (rModel.getRelationships().size() == 0) { // removal
				rModel.removeFromParent();
				toRemove.add(rModel);
			} else {
				rModel.setParent(getRootModel());
				
				if (rModel.getSourceModel() != null &&
						rModel.getTargetModel() != null) {
					if (!getRootModel().getRelationships().contains(rModel)) {
						getRootModel().addChild(rModel);
						toRemove.add(rModel);
					}
				}
			}
		}
		
		// update the cardinality labels of all updated relationships 
		for (RelationshipModel model : toRemove) {
			model.updateCardinality();
		}
		
		_relationshipChanges.removeAll(toRemove);
	}

	/**
	 * Preserves consistency between the editor and the code by automatically
	 * saving open <code>CompilationUnit</code>s 
	 */
	public void autoSave() {
		if (!PlugIn.getBooleanPreference(P_AUTOSAVE)) return;
		
		// save all open compilation units
		IDE.saveAllEditors(
				new IResource[] { PlugIn.getWorkspaceRoot() }, false);
	}
	
	/**
	 * Gets an AST <code>CompilationUnit</code> from the mapping using the given
	 * <code>IJavaElement</code>, which should be either an
	 * <code>IClassFile</code> or an <code>ICompilationUnit</code>
	 * 
	 * @param element - The element to find in the mapping.
	 * @return The AST <code>CompilationUnit</code> the represents the structure
	 * of the given element
	 */
	public CompilationUnit getCompilationUnit(IJavaElement element) {
		return _cuMap.getCompilationUnit(element.getHandleIdentifier());
	}
	
	/**
	 * @return The router used in the current routing scheme.
	 */
	public static ConnectionRouter getConnectionRouter() {
		return CONNECTION_ROUTER;
	}

	/**
	 * @return The active editor.
	 */
	public static DiagramEditor getActiveEditor() {
		return ACTIVE_EDITOR;
	}
	
	/**
	 * @return The currently active tool in the palette.
	 */
	public ToolEntry getActiveTool() {
		return getGraphicalViewer().getEditDomain().getPaletteViewer().getActiveTool();
	}

	/**
	 * Determines whether or not the given member is filtered.
	 * 
	 * @param member - the given <code>IMember</code>.
	 * @return true if a filter applies to the member, false otherwise.
	 * @throws JavaModelException
	 */
	public boolean isFiltered(IMember member)
	throws JavaModelException {
		for (Filter filter : _filters) {
			if (filter.accept(member)) return true;
		}
		
		return false;
	}

	public static void setOutlinePage(OutlinePage page) {
		_outlinePage = page;
	}
	
	public Rectangle getSize() {
		return getGraphicalControl().getBounds();
	}
}

/**
 * Creates the <code>AbstractPart</code>s that correspond to the given
 * <code>AbstractModel</code>s. The parts, in turn, create the figures that
 * represent the models in our editor.
 * 
 * @author bcmartin
 */
class DiagramPartFactory implements EditPartFactory {
	/**
	 * @see org.eclipse.gef.EditPartFactory#createEditPart(org.eclipse.gef.EditPart, java.lang.Object)
	 */
	public EditPart createEditPart(EditPart context, Object oModel) {
		try {
			// finds the EditPart associated with the created model
			// and instanciates a new EditPart of that type
			AbstractModel model = (AbstractModel) oModel;
			Class editPartClass = model.getPartClass();
			Constructor<?> instance = null;
			Constructor<?>[] constructors = editPartClass.getConstructors();

			// find the empty constructor
			for (Constructor<?> constructor : constructors) {
				if (constructor.getParameterTypes().length == 0) {
					instance = constructor;
				}
			}

			// throw an error if there is no empty constructor
			if (instance == null) {
				GreenException.illegalOperation(
						"Model has no empty constructor: " + model);
			}

			// connect the model and controller
			AbstractPart part = (AbstractPart) instance.newInstance();
			part.setModel(model);

			// map the model to its corresponding edit part
			RootPart drep;
			
			if (part instanceof RootPart) {
				drep = (RootPart) part;
			} else {
				AbstractPart aEditPart = (AbstractPart) context;
				drep = aEditPart.getRootPart();
			}

			drep.mapModelToEditPart(model, part);

			return part;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}
}

/**
 * Sets up the tools in the palette.
 * @author bcmartin
 */
class DiagramPaletteFactory {
	/**
	 * A generic selection tool.
	 */
	private static ToolEntry SELECTION_TOOL = new PanningSelectionToolEntry(
			SELECTION_LABEL, SELECTION_DESCRIPTION);

	/**
	 * @return The generic selection tool.
	 */
	public static ToolEntry getSelectionTool() {
		return SELECTION_TOOL;
	}

	/**
	 * Creates the bottom-level palette contents.
	 *
	 * @return The bottom-level palette contents.
	 */
	public static PaletteRoot createPaletteRoot( ) {
		ImageDescriptor noteIcon = JavaPluginImages.DESC_TOOL_SHOW_SEGMENTS;
		
		PaletteRoot paletteRoot = new PaletteRoot();

		// create relationship groups
		List<PaletteEntry> categories = new Vector<PaletteEntry>();
		PaletteGroup tools = new PaletteGroup(GROUP_MAIN_LABEL);
		List<ToolEntry> selectEntries = new Vector<ToolEntry>();
		List<ToolEntry> typeEntries = new Vector<ToolEntry>();
		List relEntries = new ArrayList();

		String noteLabel = NOTE_LABEL;
		String noteDesc = NOTE_DESCRIPTION;

		// Selection tool
		selectEntries.add(SELECTION_TOOL);
		paletteRoot.setDefaultEntry(SELECTION_TOOL);
		tools.addAll(selectEntries);

		// Type tools and Note tool
		PaletteGroup typeDrawer = new PaletteGroup(GROUP_CREATE_TYPE_LABEL);
		List<ITypeProperties> properties = new ArrayList<ITypeProperties>();
		
		// put types in alphabetical order
		for (ITypeProperties prop : PlugIn.getAvailableTypes()) {
			boolean added = false;
			int x = 0;
						
			for (ITypeProperties cProp : properties) {
				if (prop.getLabel().compareToIgnoreCase(cProp.getLabel()) < 0) {
					properties.add(x, prop);
					added = true;
					break;
				}
				
				x++;
			}
			
			if (!added) {
				properties.add(prop);
			}
		}

		// create palette entries for available types
		for (ITypeProperties prop : properties) {
			typeEntries.add(new CombinedTemplateCreationEntry(prop.getLabel(),
					prop.getDescription(), TypeModel.class,
					new SimpleFactory(TypeModel.class),
					prop.getIconDescriptor(), prop.getIconDescriptor()));
		}

		typeEntries.add(new CombinedTemplateCreationEntry(noteLabel, noteDesc,
				NoteModel.class, new SimpleFactory(NoteModel.class), noteIcon,
				noteIcon));

		typeDrawer.addAll(typeEntries);

		// create container for relationship tools
		PaletteGroup relDrawer =
			new PaletteGroup(GROUP_CREATE_RELATIONSHIPS_LABEL);
		List<Class> relClasses = new ArrayList<Class>();
		
		/* Ensure the relationships are added to the palette in the correct
		 * order by comparing their names to one another beforehand
		 */

		for (Class klass : PlugIn.getRelationships()) {
			boolean added = false;
			int x = 0;
			
			for (Class comp : relClasses) {
				if (PlugIn.getRelationshipName(klass).compareToIgnoreCase(
						PlugIn.getRelationshipName(comp)) < 0) {
					relClasses.add(x, klass);
					added = true;
					break;
				}
				
				x++;
			}
			
			if (!added) {
				relClasses.add(klass);
			}
		}
		
		String oldName = null;
		
		/* Map all relationships to their supertypes (e.g. dependency has more
		 * than one flavor.
		 */ 
		for (Class klass : relClasses) {
			RelationshipGroup group = PlugIn.getRelationshipGroup(klass);
			
			List<RelationshipSubtype> subtypes =
				PlugIn.getRelationshipSubtypes(group.getName());

			if(group.getName().equals(oldName)) continue;
			oldName = group.getName();
			
			if (subtypes == null || subtypes.size() < 2) {
				RelationshipCreationToolEntry entry = new RelationshipCreationToolEntry(group, null);
				relEntries.add(entry);
			}
			else {
				PaletteStack pstack = new PaletteStack(null, null, null);
				
				for(RelationshipSubtype type : subtypes) {
					RelationshipCreationToolEntry entry =
						new RelationshipCreationToolEntry(type.getGroup(), null);
					entry.setLabel(type.getLabel() + " " + type.getGroup().getName());
					pstack.add(entry);
				}
				relEntries.add(pstack);
				
			}
		}
		
		relDrawer.addAll(relEntries);
		
		PaletteGroup visDrawer =
			new PaletteGroup(GROUP_VISIBILITY_LABEL);
		List<PaletteEntry> visEntries = new ArrayList<PaletteEntry>();
		ImageDescriptor fishIcon = ImageDescriptor.createFromFile( DiagramEditor.class, "fish.gif" );
		visEntries.add( new PaletteButton(TOGGLEFISH_LABEL, TOGGLEFISH_DESCRIPTION, fishIcon, fishIcon ) {
			{
				setUserModificationPermission(PERMISSION_NO_MODIFICATION);
			}
			
			public void act( ) {
				PlugIn.setBooleanPreference(P_DISPLAY_INCREMENTAL_EXPLORER_DIA,
						!PlugIn.getBooleanPreference(P_DISPLAY_INCREMENTAL_EXPLORER_DIA));
			}
		});
		visDrawer.addAll( visEntries );

		// add the tools into the palette root
		categories.add(tools);
		categories.add(typeDrawer);
		categories.add(relDrawer);
		categories.add( visDrawer );
		paletteRoot.addAll(categories);
		
		return paletteRoot;
	}
}

/**
 * Our palette tool for creating a relationship.
 * 
 * @author bcmartin
 */
class RelationshipCreationToolEntry extends CreationToolEntry {
	public RelationshipCreationToolEntry(final RelationshipGroup group,
			final List<RelationshipSubtype> subtypes) {
		super(group.getName(), CREATE_RELATIONSHIP_PREFIX_DESCRIPTION
				+ group.getName() + CREATE_RELATIONSHIP_SUFFIX_DESCRIPTION,
				new SimpleFactory(group.getPartClass()),
				group.getImageDescriptor(), group.getImageDescriptor());
		
		setToolClass(ConnectionCreationTool.class);
		setUserModificationPermission(PERMISSION_NO_MODIFICATION);
	}
}

/**
 * A class that allows the adding of buttons to the palette instead of tools.
 * 
 * @author dan
 */
abstract class PaletteButton extends ToolEntry {

	/**
	 * @param label
	 * @param shortDesc
	 * @param iconSmall
	 * @param iconLarge
	 */
	public PaletteButton ( String label, String shortDesc,
			ImageDescriptor iconSmall, ImageDescriptor iconLarge ) {
		super( label, shortDesc, iconSmall, iconLarge );
	}
	
	/**
	 * Override to specify what this button will execute when clicked.
	 */
	public abstract void act( );
}

/**
 * A collection of methods for performing save and load in the editor.
 * 
 * @author bcmartin
 * @author zgwang
 */
class DiagramEditorFilePolicies {
	private static boolean _fileModified;

	/**
	 * Indicates to the user that there was an I/O error in Green.
	 * 
	 * @param file - The file with the invalid format.
	 */
	private static void displayInvalidFileFormatError(File file) {
		GreenException.fileException(GRERR_FILE_FORMAT);
	}
	
	/**
	 * Attempts to find the Type in the Java Model
	 * 
	 * @param projectName - the String name of the project
	 * @param path - the IPath of the Compilation Unit containing the type
	 * @return the desired <code>IType</code> that is modeled by our diagram
	 */
	private static IType extractType(String projectName, String fullyQualifiedTypeName) {
		try {
			IJavaModel jm =
				JavaCore.create(ResourcesPlugin.getWorkspace().getRoot());
			return jm.getJavaProject(projectName).findType(
					fullyQualifiedTypeName);
		} catch (JavaModelException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * @param handleId - The handle to the type.
	 * @return The type referred to by this handle.
	 */
	private static IType extractType(String handleId) {
		IType type = (IType) JavaCore.create(handleId);
		if (type.exists()) return type;
		return null;
	}
	
	/**
	 * Loads a UML file into an editor. This method delegates the interpretation
	 * of the file's contents to a private helper method of the same name; thus
	 * this method should never need maintenance.
	 * 
	 * @param file - The file to be loaded into this editor.
	 */
	public static void load(final DiagramEditor editor, final File file) {
		
		//LOOKINTO [Can be removed if refactoring is done through extension point.] Refactoring in DIA files, this needs to parse Eclipse FQN format
		
		final XMLConverter converter = new XMLConverter();
		final XMLNode node, parent;
		
		char[] fileContents = new char[(int) file.length()];
		
		// read the file and parse the contents
		try {
			FileReader fReader = null;
			
			try {
				fReader = new FileReader(file);
			} catch (FileNotFoundException e) {
				// abort;
				GreenException.warn("The file " + file + " was not found.");
				return;
			}
			
			fReader.read(fileContents);
			fReader.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return;
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
		
		if (fileContents.length < 5) {
			// file is "empty" - no error
			return;
		}
		
		node = converter.getDecodedXML(new String(fileContents));
		
		if (!node.getName().equals("!root")) {
			// invalid file format
			displayInvalidFileFormatError(file);
			return;
		}
		
		if (node.getChildren().size() == 0) { return; }
		parent = (XMLNode) node.getChild(XML_UML);
		
		PlugIn.runWithoutRecognizers(new Runnable() {
			/**
			 * @see java.lang.Runnable#run()
			 */
			public void run() {
				load(editor, parent);
			}
		});
	}
	
	/**
	 * Saves the current editor.
	 * 
	 * @param askForName - Will ask for name if <code>true</code>.
	 */
	public static void save(DiagramEditor editor, boolean askForName) {
		IPath filePath = new Path("/noname");
		
		if (editor.getEditorInput() instanceof GreenEditorInput) {
			GreenEditorInput input = (GreenEditorInput) editor.getEditorInput();
			filePath = new Path(input.getPath().toFile().toString());
		}
		
		// in case the file is saved outside of Eclipse
		if (editor.getCurrentFile() != null) {
			filePath = PlugIn.getWorkspaceRoot().getFile(
					editor.getCurrentFile().getFullPath()).getLocation();
		}
		
		String extension;
		
		// Open dialog and ask for a name
		if (askForName || !filePath.toFile().exists()) {
			// display the dialog to get the file location
			String fileName = filePath.toString();
			FileDialog dialog = new FileDialog(
					editor.getSite().getShell(), SWT.SAVE);
			dialog.setFileName(fileName.substring(fileName.lastIndexOf('/') + 1, 
												  fileName.lastIndexOf('.')));

			List<String> fExt = new ArrayList<String>();
			List<String> fDesc = new ArrayList<String>();

			// get file extensions
			for (String ext : PlugIn.getSaveFormats()) {
				String sExt = "*." + ext;
				ISaveFormat format = PlugIn.getSaveFormat(ext);
				
				String desc = format.getDescription();
				if (desc == "null") {
					desc = "";
				}
				
				fExt.add(sExt);
				fDesc.add(format.getDescription() + " (" + sExt + ")");
			}
			
			dialog.setFilterExtensions(fExt.toArray(new String[0]));
			dialog.setFilterNames(fDesc.toArray(new String[0]));
			dialog.setFilterPath(filePath.toOSString());
			fileName = dialog.open();
			
			// abort if the user pressed cancel
			if (fileName == null) return;
			
			// see if the file path is in the workspace
			filePath = new Path(fileName);
			String workspaceString =
				PlugIn.getWorkspaceRoot().getLocation().toPortableString();
			String workspaceDir = workspaceString.substring(
					workspaceString.lastIndexOf('/')) + "/";
			String fileLocation = filePath.toPortableString();

			int wsIndex = fileLocation.indexOf(workspaceDir);
			 
			if (wsIndex == -1) { // not a workspace file path	
				try {
					filePath.toFile().createNewFile();
				} catch (IOException e) {
					GreenException.fileException(
							"There was a problem accessing \""
							+ filePath + "\"");
					e.printStackTrace();
					return;
				}
				
				if (filePath.toString().endsWith("." + PluginConstants.GREEN_EXTENSION) ||
						//Older file type compatibility
						filePath.toString().endsWith(".dia")
						) {
					editor.setEditorInput(new GreenEditorInput(
							new File(filePath.toOSString())));
				}
			} else { // save file in workspace
				String wsFile = fileLocation.substring(
						wsIndex + workspaceDir.length());
				String wsProject = wsFile.substring(0, wsFile.indexOf('/'));
				
				try {
					IProject project =
						PlugIn.getWorkspaceRoot().getProject(wsProject);
					IFile file = DiagramEditor.getFileNotExist(project,
							new Path(wsFile));

					editor.setEditorInput(new FileEditorInput(file));
				
					filePath = PlugIn.getWorkspaceRoot().getFile(
							editor.getCurrentFile().getFullPath()).getLocation();
				} catch (CoreException e) {
					e.printStackTrace();
					return;
				} catch (IOException e) {
					e.printStackTrace();
					return;
				}
			}
		}

		extension = filePath.getFileExtension();
		ISaveFormat format;
		if (extension.equals("dia") || extension.equals("grn"))
			format = PlugIn.getSaveFormat(PluginConstants.GREEN_EXTENSION);
		else
			format = PlugIn.getSaveFormat(extension);
		
		if (format == null) {
			MessageDialog.openError(editor.getSite().getShell(),
					GRERR_FILE_FORMAT, GRERR_FILE_FORMAT + ": " + extension);
			return;
		}
		
		format.saveInformation(editor, filePath.toOSString(),
				editor.getRootPart().getFigure());
	}
	
	/**
	 * Handles loading of the editor's contents by extracting the file and
	 * plugin version. The versions are then compared, warning messages are
	 * displayed as appropriate, and the delegate methods are called to perform
	 * the loading of the editor's contents.
	 * 
	 * @param editor - The editor to load the file's contents into.
	 * @param base - The node with label XML_UML.
	 */
	private static void load(DiagramEditor editor, XMLNode base) {
		final RootModel root = editor.getRootModel();
		
		String version = base.getAttribute(XML_GREEN_VERSION);
		int pluginVersion = PlugIn.getVersion();
		int fileVersion = version == null ? 20000 : Integer.parseInt(version);
		
		if (pluginVersion < fileVersion) {
			MessageDialog.openWarning(editor.getSite().getShell(),
					GreenException.GRERR_FILE_VERSION_TITLE,
					GreenException.generateVersionWarning(pluginVersion, fileVersion));
			fileVersion = pluginVersion;
		}
		
		_fileModified = false;
		loadTypes(root, base, fileVersion);
		loadNotes(root, base, fileVersion);
		loadRelat(editor, root, base, fileVersion);

		if(_fileModified) {warnFileModified();}
	}

	/**
	 * Loads <code>TypeModel</code>s into the diagram.
	 * 
	 * @param root - The root model to use.
	 * @param base - The XML_UML node of the file.
	 * @param ver - The version number of the file.
	 */
	private static void loadTypes(RootModel root, XMLNode base, int ver) {
		//LOOKINTO [Can be removed if refactoring is done through extension point.] Refactoring in DIA files, this should alter JDT handles...
		if (ver == 20000) {
			// 2.0.0
			for (XMLNode child : base.getChildren()) {
				if (child.getName().equals(XML_TYPE)) {
					// load in type
					String proj = child.getAttribute(XML_TYPE_PROJECT);
					String fqn = child.getAttribute(XML_TYPE_NAME);
					int height = child.getIntAttribute(XML_TYPE_HEIGHT);
					int width = child.getIntAttribute(XML_TYPE_WIDTH);
					int x = child.getIntAttribute(XML_TYPE_X);
					int y = child.getIntAttribute(XML_TYPE_Y);
					
					IType type = extractType(proj, fqn);
					if (type == null) {
						_fileModified = true;
						GreenException.warn("Type does not exist: " + fqn);
						continue;
					}
					
					TypeModel model = root.createTypeModel(type);
					model.setLocation(new Point(x, y));
					model.setSize(new Dimension(width, height));
				}
			}
		} else if (ver <= PlugIn.getVersion()) {
			// 2.1.0 - 3.0.0
			for (XMLNode child : base.getChildren()) {
				if (child.getName().equals(XML_TYPE)) {
					// load in type
					String handle = child.getAttribute(XML_TYPE_NAME);
					int height = child.getIntAttribute(XML_TYPE_HEIGHT);
					int width = child.getIntAttribute(XML_TYPE_WIDTH);
					int x = child.getIntAttribute(XML_TYPE_X);
					int y = child.getIntAttribute(XML_TYPE_Y);
					
					IType type = extractType(handle);
					if (type == null) {
						_fileModified = true;
						GreenException.warn("Type does not exist: " + handle);
						continue;
					}
					
					TypeModel model = root.createTypeModel(type);
					model.setLocation(new Point(x, y));
					model.setSize(new Dimension(width, height));
				}
			}
		} else {
			GreenException.warn(
					"loadTypes failed: invalid file version: " + ver);
		}
	}

	/**
	 * Loads <code>RelationshipModel</code>s into the diagram.
	 * 
	 * @param root - The root model to use.
	 * @param base - The XML_UML node of the file.
	 * @param ver - The version number of the file.
	 */
	private static void loadRelat(DiagramEditor editor, RootModel root,
			XMLNode base, int ver) {
		// the editor must be forcibly refreshed so that the relationships
		// will appear; we are running with recognizers disabled
		editor.refresh(true);
		RelationshipCache cache = root.getRelationshipCache();

		if (ver == 20000) {
			// 2.0.0
			for (XMLNode relationshipNode : base.getChildren()) {
				if (relationshipNode.getName().equals(XML_RELATIONSHIP)) {
					String relationshipClass = relationshipNode.getAttribute(
							XML_RELATIONSHIP_CLASS);
					String sourceProj = relationshipNode.getAttribute(
							XML_RELATIONSHIP_SOURCE_PROJECT);
					String sourceName = relationshipNode.getAttribute(
							XML_RELATIONSHIP_SOURCE_TYPE);
					String targetProj = relationshipNode.getAttribute(
							XML_RELATIONSHIP_TARGET_PROJECT);
					String targetName = relationshipNode.getAttribute(
							XML_RELATIONSHIP_TARGET_TYPE);
					
					IType sourcetype = extractType(sourceProj, sourceName);
					IType targettype = extractType(targetProj, targetName);
					RelationshipModel rModel =
						cache.getRelationshipModel(sourcetype,
								targettype, relationshipClass);
					
					if (rModel != null) {
						XMLNode bendpointsNode =
							relationshipNode.getChild(
									XML_BENDPOINTS);
						
						for (XMLNode bendpointNode
								: bendpointsNode.getChildren()) {
							if (bendpointNode.getName().equals(
									XML_BENDPOINT)) {
								
								int x = new Integer(bendpointNode.getAttribute(XML_BENDPOINT_X)).intValue();
								int y = new Integer(bendpointNode.getAttribute(XML_BENDPOINT_Y)).intValue();
								
								editor.addBendpoint(
										rModel, new Point(x, y));
							}
						}
					}
					else {//Relationship changed?
						_fileModified = true;
						
					}
				}
			}
		} else if (ver <= PlugIn.getVersion()) {
			// 2.1.0 - 3.0.0
			for (XMLNode relationshipNode : base.getChildren()) {
				if (relationshipNode.getName().equals(XML_RELATIONSHIP)) {
					String relationshipClass = relationshipNode.getAttribute(
							XML_RELATIONSHIP_CLASS);
					String sourceId = relationshipNode.getAttribute(
							XML_RELATIONSHIP_SOURCE_TYPE);
					String targetId = relationshipNode.getAttribute(
							XML_RELATIONSHIP_TARGET_TYPE);
					
					IType sourcetype = extractType(sourceId);
					IType targettype = extractType(targetId);
					RelationshipModel rModel =
						cache.getRelationshipModel(sourcetype,
								targettype, relationshipClass);
					
					if (rModel != null) {
						XMLNode bendpointsNode =
							relationshipNode.getChild(
									XML_BENDPOINTS);
						
						for (XMLNode bendpointNode
								: bendpointsNode.getChildren()) {
							if (bendpointNode.getName().equals(
									XML_BENDPOINT)) {
								
								int x = new Integer(bendpointNode.getAttribute(XML_BENDPOINT_X)).intValue();
								int y = new Integer(bendpointNode.getAttribute(XML_BENDPOINT_Y)).intValue();
								
								editor.addBendpoint(
										rModel, new Point(x, y));
							}
						}
					}
					else {
						_fileModified = true;
					}
				}
			}
		} else {
			GreenException.warn(
					"loadRelat failed: invalid file version: " + ver);
		}
	}
	
	/**
	 * Loads <code>NoteModel</code>s into the diagram.
	 * 
	 * @param root - The root model to use.
	 * @param base - The XML_UML node of the file.
	 * @param ver - The version number of the file.
	 */
	private static void loadNotes(RootModel root, XMLNode base, int ver) {
		if (ver <= PlugIn.getVersion()) {
			// 2.0.0 - 3.0.0
			for (XMLNode child : base.getChildren()) {
				if (child.getName().equals(XML_NOTE)) {
					String text = child.getAttribute(XML_NOTE_TEXT);
					int height = child.getIntAttribute(XML_NOTE_HEIGHT);
					int width = child.getIntAttribute(XML_NOTE_WIDTH);
					int x = child.getIntAttribute(XML_NOTE_X);
					int y = child.getIntAttribute(XML_NOTE_Y);
					
					NoteModel model = new NoteModel();
					model.setLabel(text);
					model.setLocation(new Point(x, y));
					model.setSize(new Dimension(width, height));
					
					root.addChild(model);
				}
			}
		} else {
			GreenException.warn("loadNotes failed: invalid file version: " + ver);
		}
	}

	/**
	 * Informs the user that the diagram cannot accurately reflect changes made to the code
	 * while it was closed.
	 * 
	 * @author zgwang
	 */
	private static void warnFileModified() {
		String warning = "One or more files this diagram refers to have been modified outside of Green's editor.  The diagram may be unable to accurately reflect these changes.";
		MessageDialog.openInformation(PlugIn.getDefaultShell(),
				"Error", warning);
	}
}

/**
 * A wrapper for a loaded file that exists outside of the
 * <code>IWorkspace</code>.
 * 
 * @author bcmartin
 */
class GreenEditorInput implements IEditorInput, ILocationProvider,
IPathEditorInput {
	private File _file;
	
	public GreenEditorInput(File file) {
		_file = file;
	}

	/**
	 * @see org.eclipse.ui.IEditorInput#exists()
	 */
	public boolean exists() {
		return _file.exists();
	}
	
	/**
	 * @see org.eclipse.ui.IEditorInput#getImageDescriptor()
	 */
	public ImageDescriptor getImageDescriptor() {
		return null;
	}
	
	/**
	 * @see org.eclipse.ui.IEditorInput#getName()
	 */
	public String getName() {
		return _file.getName();
	}
	
	/**
	 * @see org.eclipse.ui.IEditorInput#getPersistable()
	 */
	public IPersistableElement getPersistable() {
		return null;
	}
	
	/**
	 * @see org.eclipse.ui.IEditorInput#getToolTipText()
	 */
	public String getToolTipText() {
		return _file.getAbsolutePath();
	}
	
	/**
	 * @see org.eclipse.core.runtime.IAdaptable#getAdapter(java.lang.Class)
	 */
	public Object getAdapter(Class adapter) {
		if (ILocationProvider.class.equals(adapter)) {
			return this;
		}
		
		if (IWorkbenchAdapter.class.equals(adapter)) {
			return new WorkbenchAdapter() {};
		}

		return Platform.getAdapterManager().getAdapter(this, adapter);
	}

	/**
	 * @see org.eclipse.ui.editors.text.ILocationProvider#getPath(java.lang.Object)
	 */
	public IPath getPath(Object element) {
		if (element instanceof GreenEditorInput) {
			GreenEditorInput input = (GreenEditorInput) element;
			return Path.fromOSString(input._file.getAbsolutePath());
		}
		
		return null;
	}

	/**
	 * @see org.eclipse.ui.IPathEditorInput#getPath()
	 */
	public IPath getPath() {
		return new Path(_file.toString());
	}
}

/**
 * Permits the removal of certain <code>IJavaElement</code> representations from
 * the diagram.
 *
 * @author bcmartin
 */
class Filter {
	private Pattern _pattern;
	private int _flags;
	private int _type;

	public Filter(Pattern pattern, int type, int flags) {
		_pattern = pattern;
		_type = type;
		_flags = flags;
	}
	
	/**
	 * Compares the given member to the filter to see if it applies.
	 * 
	 * @param element - The <code>IMember</code> to compare.
	 * @return true if the filter applies, false otherwise. 
	 */
	public boolean accept(IMember element) throws JavaModelException {
		// see if the element type applies
		if ((_type & element.getElementType()) != _type) {
			return false;
		}
		
		// see if the flags match
		if ((_flags & element.getFlags()) != _flags) {
			return false;
		}
		
		// see if the text matches
		if (!_pattern.matcher(element.getElementName()).matches()) {
			return false;
		}
		
		return false;
	}
}

/**
 * Holds bendpoint information and generates bendpoint requests to permit
 * bendpoints to be accurately recreated in the editor.
 * 
 * @author bcmartin
 */
class BendpointInformation {
	private RelationshipModel _rModel;
	private Point _location;

	public BendpointInformation(RelationshipModel rModel,
			Point absoluteLocation) {
		_rModel = rModel;
		_location = absoluteLocation;
	}
	
	/**
	 * @param editor - The editor.
	 * @return A <code>BendpointRequest</code> corresponding to the
	 * appropriate <code>RelationshipModel</code> and <code>Point</code>.
	 */
	public BendpointRequest getBendpointRequest(DiagramEditor editor) {
		RelationshipPart rPart = 
			(RelationshipPart) editor.getRootPart().getPartFromModel(_rModel);
		if (rPart == null) return null;
		
//		Point location = new Point();
//		Point topLeft = rPart.getFigure().getBounds().getTopLeft();
//		
//		location.x = _location.x - topLeft.x / 2;
//		location.y = _location.y - topLeft.y / 2;
		
		// set the necessary information for the request
		BendpointRequest request = new BendpointRequest();
		request.setIndex(-1); // add to end
		request.setLocation(_location);
		request.setSource(rPart);
		
		return request;
	}
}

/**
 * Holds a mapping from element handles to their respective resource
 * modification times and <code>CompilationUnit</code>s.
 * 
 * @author bcmartin
 */
class CompilationUnitMap {
	private Map<String, Long> _cuModMap;
	private Map<String, CompilationUnit> _map;
	
	public CompilationUnitMap() {
		_cuModMap = new HashMap<String, Long>();
		_map = new HashMap<String, CompilationUnit>();
	}
	
	/**
	 * @param id - The handle of the element.
	 * @return The <code>CompilationUnit</code> corresponding to the given
	 * id.
	 */
	public CompilationUnit getCompilationUnit(String id) {
		return _map.get(id);
	}

	/**
	 * @param id - The element handle.
	 *  
	 * @return The stored modification time corresponding to the given
	 * handle.
	 */
	public Long getModificationStamp(String id) {
		return _cuModMap.get(id);
	}
	
	/**
	 * Removes a handle from the mapping.
	 * 
	 * @param id - The handle.
	 */
	public void remove(String id) {
		_map.remove(id);
		_cuModMap.remove(id);
	}
	
	/**
	 * @return A set of all element handles in the mapping.
	 */
	public Set<String> keySet() {
		return new HashSet<String>(_map.keySet());
	}
	
	/**
	 * Maps an element to its corresponding <code>CompilationUnit</code>.
	 * 
	 * @param element - The element.
	 * @param cu - Its corresponding <code>CompilationUnit</code>.
	 */
	public void put(IJavaElement element, CompilationUnit cu) {
		String id = element.getHandleIdentifier();
		
		_map.put(id, cu);
		
		if (!(element.isReadOnly())) {
			_cuModMap.put(id, element.getResource().getModificationStamp());
		}
	}

    public Object getAdapter(Class required) {
    	return null;
    }
}
