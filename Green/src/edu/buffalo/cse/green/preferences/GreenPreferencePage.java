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

import static edu.buffalo.cse.green.preferences.PreferenceInitializer.P_AUTOGEN_MAIN;
import static edu.buffalo.cse.green.preferences.PreferenceInitializer.P_AUTOGEN_SUPER_CONSTR;
import static edu.buffalo.cse.green.preferences.PreferenceInitializer.P_AUTOGEN_ABST_METHOD;
import static edu.buffalo.cse.green.preferences.PreferenceInitializer.P_AUTOSAVE;
import static edu.buffalo.cse.green.preferences.PreferenceInitializer.P_DISPLAY_RELATIONSHIP_SUBTYPES;
import static edu.buffalo.cse.green.preferences.PreferenceInitializer.P_DRAW_LINE_WIDTH;
import static edu.buffalo.cse.green.preferences.PreferenceInitializer.P_FORCE_DIA_IN_PROJECT;
import static edu.buffalo.cse.green.preferences.PreferenceInitializer.P_GRID_SIZE;
import static edu.buffalo.cse.green.preferences.PreferenceInitializer.P_MANHATTAN_ROUTING;
import static edu.buffalo.cse.green.preferences.PreferenceInitializer.P_DISPLAY_INCREMENTAL_EXPLORER_DIA;
import static edu.buffalo.cse.green.preferences.PreferenceInitializer.P_AUTOARRANGE;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.ScaleFieldEditor;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import edu.buffalo.cse.green.PlugIn;
import edu.buffalo.cse.green.editor.DiagramEditor;

/**
 * A preferences page tailored specifically to Green.
 * 
 * @author bcmartin
 */
public class GreenPreferencePage extends FieldEditorPreferencePage
implements IWorkbenchPreferencePage {
	public GreenPreferencePage() {
		super(GRID);
		setPreferenceStore(PlugIn.getDefault().getPreferenceStore());
		//setDescription("A demonstration of a preference page implementation");
	}
	
	/**
	 * @see org.eclipse.jface.preference.FieldEditorPreferencePage#createFieldEditors()
	 */
	public void createFieldEditors() {
		addField(new BooleanFieldEditor(P_FORCE_DIA_IN_PROJECT,
				"Create all diagram files in project root",
				0, getFieldEditorParent()));
		addField(new BooleanFieldEditor(P_AUTOGEN_MAIN,
				"Generate stubs for public static void main(String[] args)",
				0, getFieldEditorParent()));
		addField(new BooleanFieldEditor(P_AUTOGEN_SUPER_CONSTR,
				"Generate stubs for constructors from superclass",
				0, getFieldEditorParent()));
		addField(new BooleanFieldEditor(P_AUTOGEN_ABST_METHOD,
				"Generate stubs for inherited abstract methods",
				0, getFieldEditorParent()));
		addField(new StringFieldEditor(P_GRID_SIZE,
				"Grid Size",
				5, getFieldEditorParent()));
		addField(new BooleanFieldEditor(
				P_AUTOSAVE,
				"Save compilation units automatically after code modification",
				0, getFieldEditorParent()));
		addField(new BooleanFieldEditor(P_DISPLAY_RELATIONSHIP_SUBTYPES,
				"Show subtype names on relationship arcs",
				0, getFieldEditorParent()));
		addField(new BooleanFieldEditor(P_MANHATTAN_ROUTING,
				"Use Manhattan routing",
				0, getFieldEditorParent()));
		addField(new BooleanFieldEditor(P_DISPLAY_INCREMENTAL_EXPLORER_DIA,
				"Display Incremental Explorer Icons in Diagram Editor",
				0, getFieldEditorParent()));
		addField(new BooleanFieldEditor(P_AUTOARRANGE,
				"Automatically Arrange Diagrams",
				0, getFieldEditorParent()));
		addField(new ScaleFieldEditor(P_DRAW_LINE_WIDTH,
				"Relationship Line Width", getFieldEditorParent(),
				0, 3, 1, 1));
		
		adjustGridLayout();
	}

	/**
	 * @see org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
	 */
	public void init(IWorkbench workbench) {}

	/**
	 * @see org.eclipse.jface.preference.IPreferencePage#performOk()
	 */
	public boolean performOk() {
		boolean ok = super.performOk();
		
		for (DiagramEditor editor : DiagramEditor.getEditors()) {
			editor.refresh();
		}
		
		return ok;
	}
}