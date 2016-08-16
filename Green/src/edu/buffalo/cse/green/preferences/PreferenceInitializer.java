/* This file is part of Green.
 *
 * Copyright (C) 2005 The Research Foundation of State University of New York
 * All Rights Under Copyright Reserved, The Research Foundation of S.U.N.Y.
 * 
 * Green is free software, licensed under the terms of the Eclipse
 * Public License, version 1.0.  The license is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package edu.buffalo.cse.green.preferences;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

import edu.buffalo.cse.green.PlugIn;

/**
 * Class used to initialize default preference values.
 */
public class PreferenceInitializer extends AbstractPreferenceInitializer {
	public static final String P_AUTOGEN_SUPER_CONSTR = "autogen.super.constr";
	public static final String P_AUTOGEN_ABST_METHOD = "autogen.abst.meth";
	public static final String P_AUTOGEN_MAIN = "autogen.main";
	public static final String P_AUTOSAVE = "autoSave";
	public static final String P_FIXED_HEIGHT = "collapse.height";
	public static final String P_FIXED_WIDTH = "collapse.width";
	public static final String P_COLOR_COMPARTMENT_BORDER = "color.compartment.border";
	public static final String P_COLOR_NOTE = "color.note";
	public static final String P_COLOR_NOTE_BORDER = "color.note.border"; 
	public static final String P_COLOR_NOTE_TEXT = "color.note.text";
	public static final String P_COLOR_REL_ARROW_FILL = "color.rel.arrow.fill";
	public static final String P_COLOR_REL_LINE = "color.rel.line";
	public static final String P_COLOR_REL_TEXT = "color.rel.text";
	public static final String P_COLOR_UML = "color.uml";
	public static final String P_COLOR_TYPE_BORDER = "color.type.border";
	public static final String P_COLOR_TYPE_BORDER_HIDDENR = "color.type.border.hiddenr";
	public static final String P_COLOR_TYPE_TEXT = "color.type.text";
	public static final String P_COLOR_SELECTED = "color.selected";
	public static final String P_DISPLAY_ELEMENT_TOOLTIPS = "display.element.tooltips";
	public static final String P_DISPLAY_FQN_TYPE_NAMES = "display.fqn.type.names";
	public static final String P_DISPLAY_METHOD_PARAMETERS = "display.method.parameters";
	public static final String P_DISPLAY_RELATIONSHIP_SUBTYPES = "display.relationship.subtypes";
	public static final String P_DRAW_LINE_WIDTH = "draw.line.width";
	public static final String P_FONT = "font";
	public static final String P_GRID_SIZE = "grid.size";
	public static final String P_JM_ADD_CU_IF_PACKAGE_IN_EDITOR = "add.cu.if.package.in.editor";
	public static final String P_MANHATTAN_ROUTING = "manhattan.routing";
	public static final String P_FILTERS_MEMBER = "filters.member";
	public static final String P_FORCE_DIA_IN_PROJECT = "dia.in.project";
	public static final String P_DISPLAY_INCREMENTAL_EXPLORER_DIA = "display.inc.explr.dia";
	public static final String P_AUTOARRANGE = "auto.arrange";
	
	/**
	 * @see org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer#initializeDefaultPreferences()
	 */
	public void initializeDefaultPreferences() {
		IPreferenceStore store = PlugIn.getDefault().getPreferenceStore();
		store.setDefault(P_AUTOGEN_SUPER_CONSTR, true);
		store.setDefault(P_AUTOGEN_ABST_METHOD, true);
		store.setDefault(P_AUTOGEN_MAIN, false);
		store.setDefault(P_AUTOSAVE, true);
		store.setDefault(P_FIXED_HEIGHT, "200");
		store.setDefault(P_FIXED_WIDTH, "100");
		store.setDefault(P_COLOR_NOTE, "233,233,255");
		store.setDefault(P_COLOR_UML, "255,255,206");
		store.setDefault(P_COLOR_SELECTED, "96,255,96");
		store.setDefault(P_COLOR_TYPE_BORDER, "0,0,0");
		store.setDefault(P_COLOR_TYPE_BORDER_HIDDENR, "255,0,0");
		store.setDefault(P_COLOR_COMPARTMENT_BORDER, "128,128,128");
		store.setDefault(P_COLOR_TYPE_TEXT, "0,0,0");
		store.setDefault(P_COLOR_NOTE_BORDER, "255,196,0");
		store.setDefault(P_COLOR_NOTE_TEXT, "0,0,0");
		store.setDefault(P_COLOR_REL_ARROW_FILL, "255,255,255");
		store.setDefault(P_COLOR_REL_LINE, "0,0,0");
		store.setDefault(P_COLOR_REL_TEXT, "0,0,0");
		store.setDefault(P_DISPLAY_ELEMENT_TOOLTIPS, false);
		store.setDefault(P_DISPLAY_FQN_TYPE_NAMES, true);
		store.setDefault(P_DISPLAY_METHOD_PARAMETERS, true);
		store.setDefault(P_DISPLAY_RELATIONSHIP_SUBTYPES, true);
		store.setDefault(P_DRAW_LINE_WIDTH, 0);
		store.setDefault(P_FORCE_DIA_IN_PROJECT, true);
		store.setDefault(P_GRID_SIZE, "1");
		store.setDefault(P_JM_ADD_CU_IF_PACKAGE_IN_EDITOR, false);
		store.setDefault(P_MANHATTAN_ROUTING, false);
		store.setDefault(P_DISPLAY_INCREMENTAL_EXPLORER_DIA, true);
		store.setDefault(P_AUTOARRANGE, false);
		
		
		//FIXME Font errors
		//The following two errors occur when Green is being initialized without generating
		//an editor. (e.g. through a RefactorParticipant invocation)
//1		FontData font = Display.getDefault().getSystemFont().getFontData()[0];
//		store.setDefault(P_FONT, "|" + font.getName() + "|" + font.getHeight());
		//Results in an Invalid Thread Access error due to preference initializing being
		//done in a non-UI thread.
//2		FontData font = new FontRegistry().defaultFont().getFontData()[0];
//		store.setDefault(P_FONT, "|" + font.getName() + "|" + font.getHeight());
		//Results in null pointer error due to Display not initialized prior to creating
		//a new FontRegistry.
		
		//This seems to bypass the issues, rather than fixing them. 
		store.setDefault(P_FONT, "|Tahoma|8");
	}
}
