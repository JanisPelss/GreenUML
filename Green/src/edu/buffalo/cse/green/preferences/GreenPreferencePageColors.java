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

import static edu.buffalo.cse.green.preferences.PreferenceInitializer.P_COLOR_COMPARTMENT_BORDER;
import static edu.buffalo.cse.green.preferences.PreferenceInitializer.P_COLOR_NOTE;
import static edu.buffalo.cse.green.preferences.PreferenceInitializer.P_COLOR_NOTE_BORDER;
import static edu.buffalo.cse.green.preferences.PreferenceInitializer.P_COLOR_NOTE_TEXT;
import static edu.buffalo.cse.green.preferences.PreferenceInitializer.P_COLOR_REL_ARROW_FILL;
import static edu.buffalo.cse.green.preferences.PreferenceInitializer.P_COLOR_REL_LINE;
import static edu.buffalo.cse.green.preferences.PreferenceInitializer.P_COLOR_REL_TEXT;
import static edu.buffalo.cse.green.preferences.PreferenceInitializer.P_COLOR_SELECTED;
import static edu.buffalo.cse.green.preferences.PreferenceInitializer.P_COLOR_TYPE_BORDER;
import static edu.buffalo.cse.green.preferences.PreferenceInitializer.P_COLOR_TYPE_BORDER_HIDDENR;
import static edu.buffalo.cse.green.preferences.PreferenceInitializer.P_COLOR_TYPE_TEXT;
import static edu.buffalo.cse.green.preferences.PreferenceInitializer.P_COLOR_UML;
import static edu.buffalo.cse.green.preferences.PreferenceInitializer.P_FONT;

import org.eclipse.jface.preference.ColorFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.FontFieldEditor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import edu.buffalo.cse.green.PlugIn;
import edu.buffalo.cse.green.editor.DiagramEditor;

/**
 * The preference page for Green's colors. 
 * 
 * @author bcmartin
 */
public class GreenPreferencePageColors extends FieldEditorPreferencePage
implements IWorkbenchPreferencePage {
	public GreenPreferencePageColors() {
		super(GRID);
		setPreferenceStore(PlugIn.getDefault().getPreferenceStore());
	}
	
	/**
	 * @see org.eclipse.jface.preference.FieldEditorPreferencePage#createFieldEditors()
	 */
	public void createFieldEditors() {
		addField(new FontFieldEditor(P_FONT, "Font", getFieldEditorParent()));
		addField(new ColorFieldEditor(P_COLOR_SELECTED,
				"Selected Item", getFieldEditorParent()));
		addField(new ColorFieldEditor(P_COLOR_UML,
				"UML Boxes", getFieldEditorParent()));
		addField(new ColorFieldEditor(P_COLOR_TYPE_BORDER,
				"Type Borders", getFieldEditorParent()));
		addField(new ColorFieldEditor(P_COLOR_TYPE_BORDER_HIDDENR,
				"Type Borders (with hidden relationships)", getFieldEditorParent()));
		addField(new ColorFieldEditor(P_COLOR_TYPE_TEXT,
				"Type Text", getFieldEditorParent()));
		addField(new ColorFieldEditor(P_COLOR_COMPARTMENT_BORDER,
				"Compartment Borders", getFieldEditorParent()));
		addField(new ColorFieldEditor(P_COLOR_NOTE,
				"Notes", getFieldEditorParent()));
		addField(new ColorFieldEditor(P_COLOR_NOTE_BORDER,
				"Note Borders", getFieldEditorParent())); 
		addField(new ColorFieldEditor(P_COLOR_NOTE_TEXT,
				"Note Text", getFieldEditorParent()));
		addField(new ColorFieldEditor(P_COLOR_REL_ARROW_FILL,
				"Relationship Arrow Heads", getFieldEditorParent()));
		addField(new ColorFieldEditor(P_COLOR_REL_LINE,
				"Relationships", getFieldEditorParent()));
		addField(new ColorFieldEditor(P_COLOR_REL_TEXT,
				"Relationship Text", getFieldEditorParent()));
		
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