/* This file is part of Green.
 *
 * Copyright (C) 2005 The Research Foundation of State University of New York
 * All Rights Under Copyright Reserved, The Research Foundation of S.U.N.Y.
 * 
 * Green is free software, licensed under the terms of the Eclipse
 * Public License, version 1.0.  The license is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package edu.buffalo.cse.green.editor.controller.policies;

import static edu.buffalo.cse.green.constants.DialogStrings.DIALOG_INPUT_CARDINALITY_MESSAGE;
import static edu.buffalo.cse.green.constants.DialogStrings.DIALOG_INPUT_CARDINALITY_TITLE;
import static edu.buffalo.cse.green.constants.DialogStrings.DIALOG_RELATIONSHIP;
import static org.eclipse.jface.window.Window.CANCEL;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.gef.commands.Command;
import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.dialogs.MessageDialog;

import edu.buffalo.cse.green.GreenException;
import edu.buffalo.cse.green.PlugIn;
import edu.buffalo.cse.green.editor.controller.RelationshipPart;
import edu.buffalo.cse.green.editor.model.RelationshipModel;
import edu.buffalo.cse.green.editor.model.RootModel;
import edu.buffalo.cse.green.editor.model.TypeModel;
import edu.buffalo.cse.green.relationships.RelationshipGenerator;
import edu.buffalo.cse.green.relationships.RelationshipGroup;

/**
 * Creates a relationship between two <code>TypeModel</code>s.
 * 
 * @author hk47
 */
public class CreateRelationshipCommand extends Command {
	private static boolean _forceCardinality = false;

	private static int _forcedCardinality;

	private RelationshipPart _relationshipPart;

	private RootModel _root;

	public CreateRelationshipCommand() {
		_relationshipPart = null;
	}

	public CreateRelationshipCommand(RootModel model, RelationshipPart part) {
		setRoot(model);

		_relationshipPart = part;
		getModel().setParent(_root);

		RelationshipGroup group = PlugIn.getRelationshipGroup(part.getClass());
		group.setVisible(true);
		_root.showRelationshipsOfType(part.getClass());
	}

	/**
	 * @return The <code>RelationshipModel</code> that was created.
	 */
	public RelationshipModel getRelationship() {
		return (RelationshipModel) _relationshipPart.getModel();
	}

	/**
	 * Sets the source <code>TypeModel</code>.
	 * 
	 * @param source - The <code>TypeModel</code>.
	 */
	public void setSource(TypeModel source) {
		getModel().setSourceModel(source);
	}

	/**
	 * Sets the target <code>TypeModel</code>.
	 * 
	 * @param target - The <code>TypeModel</code>.
	 */
	public void setTarget(TypeModel target) {
		getModel().setTargetModel(target);
	}

	/**
	 * @see org.eclipse.gef.commands.Command#execute()
	 */
	public void execute() {
		Class klass = _relationshipPart.getClass();
		getModel().setPartClass(klass);

		try {
			getModel().assertValid();
		} catch (GreenException e) {
			if (PlugIn.isUserMode()) {
				MessageDialog.openError(PlugIn.getDefaultShell(),
						DIALOG_RELATIONSHIP, e.getLocalizedMessage());
				return;
			} else {
				throw e;
			}
		}
		
		RelationshipGenerator rGen =
			PlugIn.getRelationshipGroup(klass).getGenerator();
		
		// if the relationship type is not set, abort
		if (!rGen.setRelationship(getModel())) return;

		// if cardinality is supported, ask for it
		if (rGen.supportsCardinality()) {
			InputDialog dialog;
			int cardinality = 1;
			
			if (!_forceCardinality) {
				// Open dialog to obtain cardinality of the relationship
				dialog = new InputDialog(PlugIn.getDefaultShell(),
						DIALOG_INPUT_CARDINALITY_TITLE,
						DIALOG_INPUT_CARDINALITY_MESSAGE, "1",
						new IInputValidator() {
					/**
					 * @see org.eclipse.jface.dialogs.IInputValidator#isValid(java.lang.String)
					 */
					public String isValid(String newText) {
						Pattern p = Pattern.compile("\\*|\\d+|\\d+\\.\\.\\*");
						Matcher m = p.matcher(newText);
						if (m.matches()) { return null; }
						
						return "Cardinality needs to be either a quantity or '#..*' or *";
					}
				});

				if (dialog.open() == CANCEL) {
					return;
				}

				if (dialog.getValue().equals("*")) {
					cardinality = 0;
				} else {
					try {
						cardinality = Integer.parseInt(dialog.getValue());
					} catch (NumberFormatException e) {
						// collection
						int i = dialog.getValue().indexOf(".");
						String card = dialog.getValue().substring(0, i);
						cardinality = -1 * Integer.parseInt(card);
					}
				}
			} else {
				cardinality = _forcedCardinality;
			}
			
			_forceCardinality = false;
			rGen.setCardinality(cardinality);
		}

		_root.generateRelationshipCode(getModel());
	}

	/**
	 * Sets the parent of the <code>RelationshipModel</code> that will be
	 * created.
	 * 
	 * @param root - The <code>UMLRootModel</code>.
	 */
	public void setRoot(RootModel root) {
		_root = root;
	}

	/**
	 * Retrieves the <code>UMLRootModel</code> that the relationship will
	 * be displayed in.
	 * 
	 * @return The <code>UMLRootModel</code>.
	 */
	public RootModel getRoot() {
		return _root;
	}

	/**
	 * @see org.eclipse.gef.commands.Command#canUndo()
	 */
	public boolean canUndo() {
		return false;
	}

	/**
	 * @return The <code>RelationshipModel</code> that corresponds to the
	 * <code>RelationshipPart</code>.
	 */
	private RelationshipModel getModel() {
		return (RelationshipModel) _relationshipPart.getModel();
	}
	
	/**
	 * Used by tests to set the relationship's cardinality before drawing.
	 * 
	 * @param cardinality - The desired cardinality. Non-positive values
	 * represent collections.
	 */
	public static void forceCardinality(int cardinality) {
		_forceCardinality = true;
		_forcedCardinality = cardinality;
	}
}
