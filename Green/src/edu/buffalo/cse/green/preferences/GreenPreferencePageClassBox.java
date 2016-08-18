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

import static edu.buffalo.cse.green.preferences.PreferenceInitializer.P_DISPLAY_ELEMENT_TOOLTIPS;
import static edu.buffalo.cse.green.preferences.PreferenceInitializer.P_DISPLAY_FQN_TYPE_NAMES;
import static edu.buffalo.cse.green.preferences.PreferenceInitializer.P_DISPLAY_METHOD_PARAMETERS;
import static edu.buffalo.cse.green.preferences.PreferenceInitializer.P_FIXED_HEIGHT;
import static edu.buffalo.cse.green.preferences.PreferenceInitializer.P_FIXED_WIDTH;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import edu.buffalo.cse.green.PlugIn;
import edu.buffalo.cse.green.editor.DiagramEditor;

/**
 * The preferences page for class box settings.
 * 
 * @author bcmartin
 */
public class GreenPreferencePageClassBox extends FieldEditorPreferencePage
implements IWorkbenchPreferencePage {
	public GreenPreferencePageClassBox() {
		super(GRID);
		setPreferenceStore(PlugIn.getDefault().getPreferenceStore());
	}
	
	/**
	 * @see org.eclipse.jface.preference.FieldEditorPreferencePage#createFieldEditors()
	 */
	public void createFieldEditors() {
		addField(new StringFieldEditor(P_FIXED_HEIGHT,
				"Fixed height",
				5, getFieldEditorParent()));
		addField(new StringFieldEditor(P_FIXED_WIDTH,
				"Fixed width",
				5, getFieldEditorParent()));
		addField(new BooleanFieldEditor(P_DISPLAY_FQN_TYPE_NAMES,
				"Show fully-qualified type names",
				0, getFieldEditorParent()));
		addField(new BooleanFieldEditor(P_DISPLAY_METHOD_PARAMETERS,
				"Show method parameter names",
				0, getFieldEditorParent()));
		addField(new BooleanFieldEditor(P_DISPLAY_ELEMENT_TOOLTIPS,
				"Show tooltips while hovering over elements",
				0, getFieldEditorParent()));
		
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