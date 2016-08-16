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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.gef.commands.Command;

import edu.buffalo.cse.green.editor.model.RelationshipModel;
import edu.buffalo.cse.green.editor.model.TypeModel;

/**
 * Hides a type and all edges connected to it.
 * 
 * @author bcmartin
 * @author tomhicks
 */
public class HideTypeCommand extends Command {
	private TypeModel _typeModel;

	private List<RelationshipModel> _hiddenRelationships;

	public HideTypeCommand(TypeModel typeModel) {
		_typeModel = typeModel;
		_hiddenRelationships = new ArrayList<RelationshipModel>();
	}

	/**
	 * @see org.eclipse.gef.commands.Command#execute()
	 */
	public void execute() {
		super.execute();

		// hide the type's box
		_typeModel.setVisible(false);

		for (RelationshipModel rModel : _typeModel.getOutgoingEdges()) {
			if (rModel.isVisible()) {
				_hiddenRelationships.add(rModel);
			}

			rModel.showRelationshipExplicitly(false);
		}

		for (RelationshipModel rModel : _typeModel.getIncomingEdges()) {
			if (rModel.isVisible()) {
				_hiddenRelationships.add(rModel);
			}

			rModel.showRelationshipExplicitly(false);
		}
	}

	/**
	 * @see org.eclipse.gef.commands.Command#undo()
	 */
	public void undo() {
		_typeModel.setVisible(true);

		for (RelationshipModel rModel : _hiddenRelationships) {
			if (_typeModel.getParent().getChildren().contains(rModel)) {
				if (rModel.getSourceModel().isVisible()
						&& rModel.getTargetModel().isVisible()) {
					rModel.showRelationshipExplicitly(true);
				}
			}
		}
	}

	/**
	 * @see org.eclipse.gef.commands.Command#redo()
	 */
	public void redo() {
		execute();
	}
}