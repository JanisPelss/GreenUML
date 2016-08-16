/* This file is part of Green.
 *
 * Copyright (C) 2005 The Research Foundation of State University of New York
 * All Rights Under Copyright Reserved, The Research Foundation of S.U.N.Y.
 * 
 * Green is free software, licensed under the terms of the Eclipse
 * Public License, version 1.0.  The license is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package edu.buffalo.cse.green;

import static edu.buffalo.cse.green.GreenException.GRERR_DUPLICATE_EXTENSION;
import static edu.buffalo.cse.green.GreenException.GRERR_INVALID_CONTEXT_ACTION;
import static edu.buffalo.cse.green.preferences.PreferenceInitializer.P_FILTERS_MEMBER;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.help.IWorkbenchHelpSystem;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;

import edu.buffalo.cse.green.constants.PluginConstants;
import edu.buffalo.cse.green.designpattern.DesignPatternGroup;
import edu.buffalo.cse.green.editor.DiagramEditor;
import edu.buffalo.cse.green.editor.action.AlterRelationshipVisibilityAction;
import edu.buffalo.cse.green.editor.action.ContextAction;
import edu.buffalo.cse.green.editor.action.IncrementalExploreSingleAction;
import edu.buffalo.cse.green.editor.controller.FieldPart;
import edu.buffalo.cse.green.editor.controller.MethodPart;
import edu.buffalo.cse.green.editor.controller.NotePart;
import edu.buffalo.cse.green.editor.controller.TypePart;
import edu.buffalo.cse.green.editor.model.MemberModel;
import edu.buffalo.cse.green.editor.model.filters.MemberFilter;
import edu.buffalo.cse.green.editor.save.ISaveFormat;
import edu.buffalo.cse.green.relationships.RelationshipGenerator;
import edu.buffalo.cse.green.relationships.RelationshipGroup;
import edu.buffalo.cse.green.relationships.RelationshipRecognizer;
import edu.buffalo.cse.green.relationships.RelationshipRemover;
import edu.buffalo.cse.green.relationships.RelationshipSubtype;
import edu.buffalo.cse.green.types.ITypeProperties;

/**
 * The main plugin class to be used in the desktop. This class loads in all
 * extensions that are relevant to our editor.
 * 
 * @author bcmartin
 * 
 */

public final class PlugIn extends AbstractUIPlugin {
	private static PlugIn PLUGIN;
	private static ResourceBundle BUNDLE;

	/**
	 * The collection of design pattern plugins in Green.
	 * <em>This is not yet implemented.</em>
	 */
	private static List<DesignPatternGroup> _designPatterns = new ArrayList<DesignPatternGroup>();

	private static Map<Class, Class> _partToView = new HashMap<Class, Class>();
	
	private static Map<Class, RelationshipGroup> _relationshipMap = new HashMap<Class, RelationshipGroup>();

	private static List<ContextAction> _actions = new ArrayList<ContextAction>();

	private static final String resourceBundleId = "edu.buffalo.cse.green.PlugInPluginResources";

	private static final String DESIGN_PATTERN_MENU_NAME = "Design Patterns";

	private static final String CONTEXT_ACTION_ID = "edu.buffalo.cse.green.contextAction";
	private static final String DESIGN_PATTERN_ID = "edu.buffalo.cse.green.designPattern";
	private static final String RELATIONSHIP_ID = "edu.buffalo.cse.green.relationships";
	private static final String VIEW_ID = "edu.buffalo.cse.green.editorViews";
	private static final String SAVE_FORMAT_ID = "edu.buffalo.cse.green.saveFormat";
	private static final String JAVA_TYPE_ID = "edu.buffalo.cse.green.javaType";
	
	private static boolean _recognizersEnabled = true;

	private static boolean _isUserMode = true;

	private static Map<String, List<RelationshipSubtype>> _relationships;
	
	private static Map<String, ISaveFormat> SAVE_FORMAT_MAP =
		new HashMap<String, ISaveFormat>();

	private static Map<String, ITypeProperties> _mTypeProperties =
		new HashMap<String, ITypeProperties>();
	private static List<RelationshipGroup> _relationshipGroups;
	
	public PlugIn() {
		_relationships = new HashMap<String, List<RelationshipSubtype>>();
		_relationshipGroups = new ArrayList<RelationshipGroup>();
		
		PLUGIN = this;

		try {
			BUNDLE = ResourceBundle.getBundle(resourceBundleId);
		} catch (MissingResourceException e) {
			BUNDLE = null;
		}
	}
	
	/**
	 * Prevents dialogs that would obstruct tests from appearing
	 */
	public static void setTestMode() {
		_isUserMode = false;
	}
	
	/**
	 * @return The value of whether Green is being run in user mode or test
	 * mode. In test mode, error dialogs do not appear and cardinalites are set
	 * before relationships are drawn. 
	 */
	public static boolean isUserMode() {
		return _isUserMode;
	}

	/**
	 * @return The <code>IWorkbench</code>'s help system.
	 */
	public static IWorkbenchHelpSystem getWorkbenchHelp() {
		return PlugIn.getDefault().getWorkbench().getHelpSystem();
	}

	/**
	 * @param klass -
	 *            The Class of the relationship's model
	 * @return the relationship group corresponding to the given name.
	 */
	public static RelationshipGroup getRelationshipGroup(Class klass) {
		return (RelationshipGroup) _relationshipMap.get(klass);
	}

	/**
	 * Adds a <code>RelationshipGroup</code> to Green's relationship types.
	 * 
	 * @param group - The <code>RelationshipGroup</code> to add.
	 */
	private static void addRelationshipGroup(RelationshipGroup group) {
		// map the relationship's part to the group
		_relationshipMap.put(group.getPartClass(), group);
		_relationshipGroups.add(group);
		
		List<RelationshipSubtype> rels = _relationships.get(group.getName());
		RelationshipSubtype relSubtype =
			new RelationshipSubtype(group, group.getSubtype());
		
		if (rels == null) {
			rels = new ArrayList<RelationshipSubtype>();
			rels.add(relSubtype);
			
			_relationships.put(group.getName(), rels);
		} else {
			rels.add(relSubtype);
		}
	}

	/**
	 * @param name - The relationship's name.
	 * @return A list of all the relationship subtypes of the given
	 * relationship.
	 */
	public static List<RelationshipSubtype> getRelationshipSubtypes(
			String name) {
		return _relationships.get(name);
	}
	
	/**
	 * @param extensionPointId - The given id.
	 * @return A list of <code>IConfigurationElement</code>s corresponding to
	 * the given extension point id.
	 */
	public List<IConfigurationElement> getConfigElements(String extensionPointId) {
		List<IConfigurationElement> configurationElements = new ArrayList<IConfigurationElement>();
		IExtensionRegistry registry = Platform.getExtensionRegistry();
		IExtension[] extensionPoints = registry.getExtensionPoint(
				extensionPointId).getExtensions();

		for (IExtension extension : extensionPoints) {
			for (IConfigurationElement element : extension
					.getConfigurationElements()) {
				configurationElements.add(element);
			}
		}

		return configurationElements;
	}

	/**
	 * This method is called upon plug-in activation
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		GreenException.warn("GreenUML Plugin activated");

		try {
			// load plugins: save types
			for (IConfigurationElement element
					: getConfigElements(SAVE_FORMAT_ID)) {
				ISaveFormat format =
					(ISaveFormat) element.createExecutableExtension("class");
				
				if (SAVE_FORMAT_MAP.containsKey(format.getExtension())) {
					GreenException.illegalOperation(GRERR_DUPLICATE_EXTENSION);
				}
				
				String ext = format.getExtension();
				Pattern valid = Pattern.compile("[a-z0-9]+");
				Matcher matcher = valid.matcher(ext);
				if (!matcher.matches() || ext.length() > 4) {
					GreenException.illegalOperation(
							GreenException.GRERR_INVALID_EXTENSION);
				}
				
				SAVE_FORMAT_MAP.put(ext, format);
			}			
			
			// load plugins: editor view plugin
			for (IConfigurationElement element : getConfigElements(VIEW_ID)) {
				// add new view
				String memberFigure = element.getAttribute("memberClass");
				String noteFigure = element.getAttribute("noteClass");
				String typeFigure = element.getAttribute("typeClass");
				
				_partToView.put(FieldPart.class, Class.forName(memberFigure));
				_partToView.put(MethodPart.class, Class.forName(memberFigure));
				_partToView.put(NotePart.class, Class.forName(noteFigure));
				_partToView.put(TypePart.class, Class.forName(typeFigure));
			}
			
			// load plugins: context actions
			for (IConfigurationElement element
					: getConfigElements(CONTEXT_ACTION_ID)) {
				Object action = element.createExecutableExtension("class");
				
				if (!(action instanceof ContextAction)) {
					GreenException.illegalOperation(
							GRERR_INVALID_CONTEXT_ACTION);
				}
				
				// Get the list that the action belongs to and add it.
				ContextAction contextAction = (ContextAction) action;
				_actions.add(contextAction);
			}
			
			// load plugins: relationships
			List<IConfigurationElement> elements =
				getConfigElements(RELATIONSHIP_ID);
			for (int x = 0; x < elements.size(); x += 5) {
				RelationshipGenerator gen = (RelationshipGenerator) elements.get(x + 1).createExecutableExtension("class");
				RelationshipRecognizer rec = (RelationshipRecognizer) elements.get(x + 2).createExecutableExtension("class");
				RelationshipRemover rem = (RelationshipRemover) elements.get(x + 3).createExecutableExtension("class");
				boolean classToClass = !(elements.get(
						x + 4).getAttribute("classToClass").equals(""));
				boolean classToEnum = !(elements.get(
						x + 4).getAttribute("classToEnum").equals(""));
				boolean classToInterface = !(elements.get(
						x + 4).getAttribute("classToInterface").equals(""));
				boolean enumToClass = !(elements.get(
						x + 4).getAttribute("enumToClass").equals(""));
				boolean enumToEnum = !(elements.get(
						x + 4).getAttribute("enumToEnum").equals(""));
				boolean enumToInterface = !(elements.get(
						x + 4).getAttribute("enumToInterface").equals(""));
				boolean interfaceToClass = !(elements.get(
						x + 4).getAttribute("interfaceToClass").equals(""));
				boolean interfaceToEnum = !(elements.get(
						x + 4).getAttribute("interfaceToEnum").equals(""));
				boolean interfaceToInterface = !(elements.get(
						x + 4).getAttribute("interfaceToInterface").equals(""));
				
				IConfigurationElement dec = elements.get(x);
				
				RelationshipGroup rGroup = new RelationshipGroup(
						dec.getDeclaringExtension().getLabel(),
						dec.getAttribute("label"),
						dec.createExecutableExtension("class").getClass(),
						gen, rec, rem,
						classToClass, classToEnum, classToInterface,
						enumToClass, enumToEnum, enumToInterface,
						interfaceToClass, interfaceToEnum,
						interfaceToInterface);
				
				addRelationshipGroup(rGroup);
			}
			
			// load dynamic context actions
			List<Class> c = PlugIn.getRelationships( );
			List<Class> sorted = new ArrayList<Class>( );
			for( Class cl : c ) sorted.add( cl );
			for( int i = 0; i < sorted.size( ); i++ ) {
				int smallest = i;
				RelationshipGroup group = PlugIn.getRelationshipGroup( sorted.get( i ) );
				String smallestString = ( group.getSubtype() != null ? group.getSubtype() + " " : "" ) + group.getName();
				for( int j = i + 1; j < sorted.size( ); j++ ) {
					group = PlugIn.getRelationshipGroup( sorted.get( j ) );
					String cur = ( group.getSubtype() != null ? group.getSubtype() + " " : "" ) + group.getName();
					if( cur.compareTo( smallestString ) < 0 ) {
						smallest = j;
						smallestString = cur;
					}
				}
				sorted.add( i, sorted.remove( smallest ) );
			}
			for ( Class partClass : sorted ) {
				ContextAction action =
					new AlterRelationshipVisibilityAction(partClass);
				_actions.add(action);
				action =
					new IncrementalExploreSingleAction(partClass);
				_actions.add(action);
			}
			
			// load plugins: design patterns
			for (IConfigurationElement element
					: getConfigElements(DESIGN_PATTERN_ID)) {
				Object designPattern =
					element.createExecutableExtension("class");
				
				if (designPattern instanceof DesignPatternGroup) {
					_designPatterns.add((DesignPatternGroup) designPattern);
				}
			}
			
			// load plugins: Java types
			for (IConfigurationElement element
					: getConfigElements(JAVA_TYPE_ID)) {
				Object javaType = element.createExecutableExtension("class");
				
				if (javaType instanceof ITypeProperties) {
					ITypeProperties prop = (ITypeProperties) javaType;
					_mTypeProperties.put(prop.getLabel(), prop);
				} else {
					GreenException.illegalExtensionClass(javaType.getClass(),
							ITypeProperties.class);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		// add a listener to check for changes to the java model
		JavaCore.addElementChangedListener(JavaModelListener.getListener());
	}
	
	/**
	 * @return A list of all the types available in the workspace. This
	 * list can be found by looking in the "Open Type" dialog.
	 */
	public static Collection<ITypeProperties> getAvailableTypes() {
		return _mTypeProperties.values();
	}

	/**
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		super.stop(context);
	}

	/**
	 * Returns the shared instance.
	 */
	public static PlugIn getDefault() {
		return PLUGIN;
	}

	/**
	 * @return the string from the plugin's resource bundle, or 'key' if not
	 * found.
	 */
	public static String getResourceString(String key) {
		ResourceBundle bundle = PlugIn.getDefault().getResourceBundle();
		try {
			return (bundle != null) ? bundle.getString(key) : key;
		} catch (MissingResourceException e) {
			return key;
		}
	}

	/**
	 * @return the plugin's resource bundle,
	 */
	public ResourceBundle getResourceBundle() {
		return BUNDLE;
	}

	/**
	 * Returns a default progress monitor for methods that require it
	 * 
	 * @return a default progress monitor (handlers do nothing).
	 */
	public static IProgressMonitor getEmptyProgressMonitor() {
		return new NullProgressMonitor();
	}

	/**
	 * Returns a relationship's name.
	 * 
	 * @param klass - The model's class.
	 * @return The name of the relationship.
	 */
	public static String getRelationshipName(Class klass) {
		return (String) _relationshipMap.get(klass).getName();
	}

	/**
	 * @return A list of all relationships currently loaded.
	 */
	public static List<Class> getRelationships() {
		return new ArrayList<Class>(_relationshipMap.keySet());
	}

	/**
	 * @return A list of all context actions.
	 */
	public static List<ContextAction> getActions() {
		return _actions;
	}

	/**
	 * @return The workbench's active window's shell.
	 */
	public static Shell getDefaultShell() {
		return getDefault().getWorkbench().getActiveWorkbenchWindow()
				.getShell();
	}

	/**
	 * Adds a design pattern.
	 * 
	 * @param editor - The editor to use as the selection provider.
	 * @param manager - The menu manager to add the design pattern menu to.
	 */
	public static void addDesignPatternMenu(
			DiagramEditor editor,
			IMenuManager manager) {
		MenuManager designPatternMM = new MenuManager(DESIGN_PATTERN_MENU_NAME,
				null);

		for (DesignPatternGroup group : _designPatterns) {
			ContextAction cAction = group.getAction();
			cAction.setSelectionProvider(editor);
			cAction.setContents();
			designPatternMM.add(cAction);
		}

		manager.add(designPatternMM);
	}

	/**
	 * @return True if relationship recognizers are enabled, false otherwise.
	 */
	public static boolean isRecognizersEnabled() {
		return _recognizersEnabled;
	}

	/**
	 * Runs code with the relationship recognizers disabled. If any exception
	 * occurs, the relationship recognizers are enabled again and the exception
	 * is rethrown.
	 * 
	 * @param runnable - The <code>Runnable</code> to run.
	 * @throws Exception
	 */
	public static void runWithoutRecognizers(Runnable runnable) {
		try {
			_recognizersEnabled = false;
			runnable.run();
		} finally {
			_recognizersEnabled = true;
		}
	}

	/**
	 * @param menuGroup - The group to generate the label for
	 * @return a submenu label based on the identifier string
	 */
	public static String getSubMenuLabel(String menuGroup) {
		String menuLabel = menuGroup.substring(menuGroup.lastIndexOf('.') + 1)
				.toLowerCase();
		return menuLabel.substring(0, 1).toUpperCase() + menuLabel.substring(1);
	}

	/**
	 * @return The workspace's root.
	 */
	public static IWorkspaceRoot getWorkspaceRoot() {
		return ResourcesPlugin.getWorkspace().getRoot(); 
	}
	
	/**
	 * Generates a numerical version number for this plugin.
	 * 
	 * @return A mathematical representation of the version number. The digits
	 * are in the form "mmnnss", where m is the major segment, n is the minor
	 * segment, and s is the service segment; the qualifier segment is ignored
	 * for the time being. 
	 * @author zgwang
	 */
	public static int getVersion() {
		//TODO Versioning, this should get cleaned up a bit.
		//Modified to adhere to Eclipse's versioning system as of 10/10/06
		//Reminder: in eclipse, version numbers are composed of 
		//four (4) segments: 3 integers and a string respectively 
		//named major.minor.service.qualifier.
		//the qualifier is usually in the form vYYMMDD
		String version = (String) getDefault().getBundle().getHeaders().get(
				Constants.BUNDLE_VERSION);
		int firstDot = version.indexOf('.');
		int secondDot = firstDot + 1 + (version.substring(firstDot + 1)).indexOf('.');
		int thirdDot = secondDot + 1 + (version.substring(secondDot + 1)).indexOf('.');
		int major = Integer.parseInt(version.substring(0, firstDot));
		int minor = Integer.parseInt(version.substring(firstDot + 1, secondDot));
		int service;
		if(thirdDot == secondDot) //no qualifier
			service = 0;
		else
			service = Integer.parseInt(version.substring(secondDot + 1, thirdDot));
		
		return major * 10000 + minor * 100 + service;
	}

	/**
	 * @param klass - The <code>AbstractPart</code>'s class
	 * @return The corresponding view part class.
	 */
	public static Class getViewPart(Class<?> klass) {
		return _partToView.get(klass);
	}
	
	/**
	 * @return The plugin's preference information.
	 */
	private static IPreferenceStore getPreferences() {
		return getDefault().getPreferenceStore();
	}
	
	/**
	 * @param key - The String key
	 * @return The preference from the preference system for the given key.
	 */
	public static boolean getBooleanPreference(String key) {
		return getPreferences().getBoolean(key);
	}
	
	/**
	 * @param key The String key
	 * @param value The value to assign
	 */
	public static void setBooleanPreference(String key, boolean value) {
		getPreferences().setValue( key, value );
		for( DiagramEditor e : DiagramEditor.getEditors( ) )
			e.refresh( );
	}

	/**
	 * @param key - The String key
	 * @return The preference from the preference system for the given key.
	 */
	public static String getPreference(String key) {
		return getPreferences().getString(key);
	}

	/**
	 * Gets the Font object based on the preferences settings
	 * @param key - The String key
	 * @return The preference from the preference system for the given key.
	 * 
	 * @author zgwang
	 */
	public static Font getFontPreference(String key, boolean forceItalics) {
		int fontSize = 10;
		int style = 0;
		int italics = forceItalics ? 2 : 0;
		String fontData = getPreference(key);
		String fontName = "";
		
		StringTokenizer tokens = new StringTokenizer("0" + fontData, "|");
		tokens.nextToken();
		fontName = tokens.nextToken();

		//font = <unknown>|<name>|<size>|<style>|<OS>|<don't cares>
		fontSize = (int)(Double.parseDouble(tokens.nextToken()));
		//Get italics setting from parameter, retain bold setting
		style = italics;
		if(tokens.hasMoreTokens()) {
			style += Integer.parseInt(tokens.nextToken()) % 2;
		}
		return new Font(null, fontName, fontSize, style);
	}
	
	/**
	 * @param key - The String key
	 * @return The preference from the preference system for the given key.
	 */
	public static Color getColorPreference(String key) {
		String col = getPreference(key);
		int comma1 = col.indexOf(',');
		int comma2 = col.lastIndexOf(',');
		
		int r = Integer.parseInt(col.substring(0, comma1));
		int g = Integer.parseInt(col.substring(comma1 + 1, comma2));
		int b = Integer.parseInt(col.substring(comma2 + 1));
		
		return new Color(null, r, g, b);
	}

	/**
	 * @param key - The String key
	 * @return The preference from the preference system for the given key.
	 */
	public static int getIntegerPreference(String key) {
		return getPreferences().getInt(key);
	}

	/**
	 * @param extension - The file extension.
	 * @return The save format for the given extension.
	 */
	public static ISaveFormat getSaveFormat(String extension) {
		return SAVE_FORMAT_MAP.get(extension);
	}
	
	/**
	 * @return A list of all available save formats.
	 */
	public static List<String> getSaveFormats() {
		Set<String> formats = new HashSet<String>(SAVE_FORMAT_MAP.keySet());
		List<String> orderedFormats = new ArrayList<String>();
		
		formats.remove(PluginConstants.GREEN_EXTENSION);
		orderedFormats.add(PluginConstants.GREEN_EXTENSION);
		
		while (formats.size() > 0) {
			String last = "{";
			
			for (String string : formats) {
				if (string.compareTo(last) < 0) {
					last = string;
				}
			}
			
			formats.remove(last);
			orderedFormats.add(last);
		}
		
		return orderedFormats;
	}

	/**
	 * @return A list of all the plugins that extend the extension point
	 * edu.buffalo.cse.green.relationships.
	 */
	public static List<RelationshipGroup> getRelationshipGroups() {
		List<RelationshipGroup> groups =
			new ArrayList<RelationshipGroup>();
		groups.addAll(_relationshipMap.values());
		return groups;
	}
	
	/**
	 * Determines whether or not a given model is filtered out from the diagram.
	 * 
	 * @param model - The given model.
	 * @return true if the model is filtered out, false otherwise.
	 */
	public static boolean filterMember(MemberModel model) {
		if (model == null) return false;
		
		for (MemberFilter filter : getMemberFilters()) {
			if (filter.isFiltered(model.getMember())) return true;
		}
		
		return false;
	}

//	public static Set<MemberModel> filterMembers(
//			Collection<MemberModel> models) {
//		Set<MemberModel> filtered = new HashSet<MemberModel>();
//		
//		for (MemberFilter filter : getMemberFilters()) {
//			for (MemberModel model : models) {
//				if (filter.isFiltered(model.getMember())) {
//					filtered.add(model);
//				}
//			}
//		}
//		
//		return filtered;
//	}

	public static List<MemberFilter> getMemberFilters() {
		List<MemberFilter> filters = new ArrayList<MemberFilter>();
		
		try {
			StringTokenizer tokens = new StringTokenizer(
					PlugIn.getPreference(P_FILTERS_MEMBER), "|");
			
			while (tokens.hasMoreTokens()) {
				String token = tokens.nextToken();
				filters.add(new MemberFilter(token));
			}
		} catch (Exception e) {
			GreenException.warn("problem loading filters");
		}

		return filters;
	}
	
	/**
	 * @return A list of all the relationship plugins.
	 */
	public static List<RelationshipGroup> getRelationshipList() {
		return _relationshipGroups;
	}
	
	/**
	 * @return The mapping from <code>String</code> representation of kinds of
	 * types to their respective plugin instances.
	 */
	public static Map<String, ITypeProperties> getTypeProperties() {
		return _mTypeProperties;
	}
}