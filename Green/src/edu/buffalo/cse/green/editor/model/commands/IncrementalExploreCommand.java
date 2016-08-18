/* This file is part of Green.
 *
 * Copyright (C) 2005 The Research Foundation of State University of New York
 * All Rights Under Copyright Reserved, The Research Foundation of S.U.N.Y.
 * 
 * Green is free software, licensed under the terms of the Eclipse
 * Public License, version 1.0.  The license is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package edu.buffalo.cse.green.editor.model.commands;

import static edu.buffalo.cse.green.constants.DialogStrings.DIALOG_INCREMENTAL_EXPLORATION_NO_MORE_RELATIONSHIPS;
import static edu.buffalo.cse.green.constants.DialogStrings.DIALOG_INCREMENTAL_EXPLORATION_NO_RELATIONSHIPS_TITLE;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.eclipse.gef.commands.Command;
import org.eclipse.jdt.core.IType;
import org.eclipse.jface.dialogs.MessageDialog;

import edu.buffalo.cse.green.PlugIn;
import edu.buffalo.cse.green.editor.DiagramEditor;
import edu.buffalo.cse.green.editor.model.RelationshipModel;
import edu.buffalo.cse.green.editor.model.RootModel;
import edu.buffalo.cse.green.editor.model.TypeModel;
import edu.buffalo.cse.green.relationships.RelationshipGroup;

/**
 * Finds all relationships that have this type as their source, then pulls in
 * all of the targets of those relationships that are not in the editor. 
 * 
 * @author bcmartin
 */
public class IncrementalExploreCommand extends Command {
	private TypeModel _model;
	private DiagramEditor _editor;
	private List<Command> _commands;
	private List<RelationshipGroup> _relTypes;
	private boolean _suppressWarning;
	
	public IncrementalExploreCommand(DiagramEditor editor, TypeModel model, boolean suppressWarning) {
		this(editor, model, PlugIn.getRelationshipGroups(), suppressWarning);
	}
	
	public IncrementalExploreCommand(DiagramEditor editor, TypeModel model,
			List<RelationshipGroup> relationshipGroups, boolean suppressWarning) {
		_model = model;
		_editor = editor;
		_commands = new ArrayList<Command>();
		_relTypes = relationshipGroups;
		_suppressWarning = suppressWarning;
	}

	/**
	 * @see org.eclipse.gef.commands.Command#execute()
	 */
	public void execute() {
		_editor.refresh(true);
		_commands.clear();
		
		IType type = _model.getType();
		RootModel root = _editor.getRootModel();
		Set<RelationshipModel> relationships =
			root.getRelationshipCache().getRelationships(type);
		
		for (RelationshipModel rModel : relationships) {
			// if the relationship type is hidden, abort
			if (!rModel.getRelationshipGroup().isVisible()) {
				continue;
			}
				
			IType tType = rModel.getTargetType();
			
			// if the type is not in the editor, load it in
			if (_relTypes.contains(rModel.getRelationshipGroup())) {
				if (root.getModelFromType(tType) == null) {
					Command command = new AddJavaElementCommand(_editor, tType);
					_commands.add(command);
					command.execute();
				}
			}
		}

		// refresh the diagram if exploration found something
		// otherwise, display a dialog to inform the user
		if (_commands.size() > 0) {
			_editor.refresh();
		}
		else if(!_suppressWarning){
			MessageDialog.openInformation(_editor.getSite().getShell(),
					DIALOG_INCREMENTAL_EXPLORATION_NO_RELATIONSHIPS_TITLE,
					DIALOG_INCREMENTAL_EXPLORATION_NO_MORE_RELATIONSHIPS +
					": " + _model.getDisplayName());
		}
	}
	
	/**
	 * @see org.eclipse.gef.commands.Command#undo()
	 */
	public void undo() {
		for (Command command : _commands) {
			command.undo();
		}
	}
	
	/**
	 * @see org.eclipse.gef.commands.Command#redo()
	 */
	public void redo() {
		execute();
	}
}
