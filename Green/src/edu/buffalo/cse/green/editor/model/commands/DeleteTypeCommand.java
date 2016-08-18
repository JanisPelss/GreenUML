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

import java.util.List;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;

import edu.buffalo.cse.green.PlugIn;
import edu.buffalo.cse.green.editor.DiagramEditor;
import edu.buffalo.cse.green.editor.model.RelationshipModel;
import edu.buffalo.cse.green.editor.model.RootModel;
import edu.buffalo.cse.green.editor.model.TypeModel;

/**
 * Delete the selected <code>IType</code>.
 * 
 * @author bcmartin
 * @author <a href="mailto:zgwang@cse.buffalo.edu">Gene Wang</a>
 */
public class DeleteTypeCommand extends DeleteCommand {
	private TypeModel _typeModel;

	public DeleteTypeCommand(TypeModel umlTypeModel) {
		super();
		_typeModel = umlTypeModel;
	}

	/**
	 * @see edu.buffalo.cse.green.editor.model.commands.DeleteCommand#doDelete()
	 */
	public void doDelete() {
		RootModel root = _typeModel.getRootModel();

		//Remove relationships first
		List<RelationshipModel> rels = root.getRelationships();
		
		//No iterators here due to CME's (ConcurrentModificationException)
		//Removal of relationships causes modifications to the rels list.
		for(int i = 0; i < rels.size(); i++) {
			IType t = _typeModel.getType();
			RelationshipModel r = rels.get(i);
			if(r.getSourceType() == t || r.getTargetType() == t) {
				DeleteCommand drc = r.getDeleteCommand(DiagramEditor.findProjectEditor(root.getProject()));
				drc.suppressMessage(true);
				drc.execute();
			}
		}

		
		_typeModel.removeChildren(); // remove fields/methods
		_typeModel.removeFromParent();
		try {
			IType type = _typeModel.getType();
			ICompilationUnit cu = (ICompilationUnit) type
					.getAncestor(IJavaElement.COMPILATION_UNIT);
			
			if (type.equals(cu.findPrimaryType())) {
				cu.delete(true, PlugIn.getEmptyProgressMonitor());
			} else {
				type.delete(true, PlugIn.getEmptyProgressMonitor());
			}
		} catch (JavaModelException e) {
			e.printStackTrace();
		}
		root.updateRelationships();
	}

	/**
	 * @see edu.buffalo.cse.green.editor.model.commands.DeleteCommand#getDeleteMessage()
	 */
	public String getDeleteMessage() {
		if (_typeModel.getType().isBinary()) { return null; }
		String typeName = _typeModel.getMember().getElementName();
		String warning = "\nAll relationships which reference " + typeName + 
			" will also be removed.";
		return "Are you sure you want to delete " + typeName + "?" + warning
			+ "\nThis will delete the class both in the diagram and in your code!";
	}
}