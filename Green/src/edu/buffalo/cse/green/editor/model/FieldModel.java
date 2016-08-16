/* This file is part of Green.
 *
 * Copyright (C) 2005 The Research Foundation of State University of New York
 * All Rights Under Copyright Reserved, The Research Foundation of S.U.N.Y.
 * 
 * Green is free software, licensed under the terms of the Eclipse
 * Public License, version 1.0.  The license is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package edu.buffalo.cse.green.editor.model;

import static edu.buffalo.cse.green.preferences.PreferenceInitializer.P_DISPLAY_FQN_TYPE_NAMES;
import static org.eclipse.jdt.ui.refactoring.RenameSupport.UPDATE_REFERENCES;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.gef.commands.Command;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.ui.refactoring.RenameSupport;

import edu.buffalo.cse.green.PlugIn;
import edu.buffalo.cse.green.editor.DiagramEditor;
import edu.buffalo.cse.green.editor.action.ContextAction;
import edu.buffalo.cse.green.editor.controller.FieldPart;
import edu.buffalo.cse.green.editor.model.commands.DeleteCommand;
import edu.buffalo.cse.green.editor.model.commands.DeleteFieldCommand;

/**
 * The model that represents <code>IField</code> elements.
 * 
 * @author hk47
 */
public class FieldModel extends MemberModel<AbstractModel, CompartmentModel, IField> {
	public FieldModel(IField field) {
		super(field);
	}

	/**
	 * @see edu.buffalo.cse.green.editor.model.MemberModel#getDisplayName()
	 */
	public String getDisplayName() {
		try {
			boolean fqn = PlugIn.getBooleanPreference(P_DISPLAY_FQN_TYPE_NAMES);
			return getSignatureName(getMember().getTypeSignature(),
					fqn) + " " + getMember().getElementName();
		} catch (JavaModelException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * @see edu.buffalo.cse.green.editor.model.AbstractModel#getContextMenuFlag()
	 */
	public int getContextMenuFlag() {
		return ContextAction.CM_FIELD;
	}

	/**
	 * @see edu.buffalo.cse.green.editor.model.AbstractModel#getDeleteCommand(edu.buffalo.cse.green.editor.DiagramEditor)
	 */
	public DeleteCommand getDeleteCommand(DiagramEditor editor) {
		return new DeleteFieldCommand(this);
	}

	/**
	 * @param editor - The <code>DiagramEditor</code> containing this model.
	 * 
	 * @return A command to hide this model.
	 */
	public Command getHideCommand(DiagramEditor editor) {
		return null;
	}

	/**
	 * @see edu.buffalo.cse.green.editor.model.AbstractModel#getPartClass()
	 */
	public Class getPartClass() {
		return FieldPart.class;
	}

	/**
	 * @see edu.buffalo.cse.green.editor.model.MemberModel#getRenameSupport()
	 */
	public RenameSupport getRenameSupport() throws CoreException {
		return RenameSupport.create(getMember(), "", UPDATE_REFERENCES);
	}
	
	/**
	 * @return The <code>IField</code> modeled by this model.
	 */
	public IField getField() {
		return (IField) getMember();
	}
}