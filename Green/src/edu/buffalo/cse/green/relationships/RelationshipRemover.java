/* This file is part of Green.
 *
 * Copyright (C) 2005 The Research Foundation of State University of New York
 * All Rights Under Copyright Reserved, The Research Foundation of S.U.N.Y.
 * 
 * Green is free software, licensed under the terms of the Eclipse
 * Public License, version 1.0.  The license is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package edu.buffalo.cse.green.relationships;

import static org.eclipse.jdt.core.dom.ASTNode.EXPRESSION_STATEMENT;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.ASTMatcher;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.ExpressionStatement;
import org.eclipse.jdt.core.dom.Statement;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.text.edits.TextEdit;
import org.eclipse.ui.IWorkbenchPage;

import edu.buffalo.cse.green.GreenException;
import edu.buffalo.cse.green.PlugIn;
import edu.buffalo.cse.green.editor.DiagramEditor;
import edu.buffalo.cse.green.editor.model.RelationshipModel;
import edu.buffalo.cse.green.util.IModifiableBuffer;

/**
 * Provides support common to all relationship removers.
 * 
 * @author bcmartin
 * @author rjtruban
 */
public abstract class RelationshipRemover extends RelationshipVisitor {
	private RelationshipModel _rModel;

	private Relationship _relationship;

	/**
	 * @return The source type of the relationship.
	 */
	protected IType getSourceType() {
		return _rModel.getSourceType();
	}

	/**
	 * @return The target type of the relationship.
	 */
	protected IType getTargetType() {
		return _rModel.getTargetType();
	}

	/**
	 * @return The relationship being removed.
	 */
	protected Relationship getRelationship() {
		return _relationship;
	}

	/**
	 * Sets the relationship model to remove.
	 * 
	 * @param rModel - The model.
	 */
	public void setRelationship(RelationshipModel rModel) {
		_rModel = rModel;
	}

	/**
	 * @see edu.buffalo.cse.green.relationships.RelationshipVisitor#run(org.eclipse.jdt.core.dom.CompilationUnit, edu.buffalo.cse.green.relationships.RelationshipCache)
	 */
	public final void run(CompilationUnit cu, RelationshipCache cache) {
		if (_rModel == null) { throw new IllegalStateException(
				"The relationship to remove hasn't been set"); }

		try {
			ICompilationUnit iCU = (ICompilationUnit) getSourceType()
					.getAncestor(IJavaElement.COMPILATION_UNIT);

			cu.recordModifications();
			
			for (Relationship relationship : _rModel.getRelationships()) {
				_relationship = relationship;
				init();
				cu.accept(this);
				finish();
			}

			IDocument sourceDoc = new IModifiableBuffer(iCU.getBuffer());
			TextEdit textEdit = cu.rewrite(sourceDoc, null);
			textEdit.apply(sourceDoc);
			
//			// put this outside of if block
//			iCU.save(PlugIn.getEmptyProgressMonitor(), false);
//			
//			if (!iCU.isConsistent()) {
//				iCU.discardWorkingCopy();
//				iCU.save(PlugIn.getEmptyProgressMonitor(), false);
//			}
//			
			iCU.save(PlugIn.getEmptyProgressMonitor(), true);
			//find active workbench and save it
			IWorkbenchPage page = DiagramEditor.getActiveEditor().getSite().getPage(); 

			//Iterates through the editor references and finds the source editor and saves it
			String sourceCUName = getSourceType().getCompilationUnit().getResource().getName();
			for (int i = 0; i < page.getEditorReferences().length; i++)
			{
				if( sourceCUName.equals(page.getEditorReferences()[i].getEditor(true).getEditorInput().getName()))
				{
					page.saveEditor(page.getEditorReferences()[i].getEditor(false), false);
				}
			}
			organizeImports(getSourceType());
			DiagramEditor.getActiveEditor().refresh();
			_rModel.getSourceModel().forceRefesh();
		} catch (BadLocationException e) {
			e.printStackTrace();
		} catch (JavaModelException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Called after the remover is run.
	 */
	protected abstract void finish();

	/**
	 * Called before the remover is run.
	 */
	protected abstract void init();

	/**
	 * Throws an exception if relationship removal was unsuccessful.
	 */
	protected void relationshipRemovalError() {
		GreenException.illegalOperation("Couldn't remove relationship");
	}
	
	/**
	 * Handles removal of add invocations involved in a relationship.
	 * 
	 * @param block - The block to process.
	 */
	protected void processAddInvocations(Block block) {
		List<Statement> stmts =
			(AbstractList<Statement>) (List) block.statements();
		List<Statement> toRemove = new ArrayList<Statement>();
		
		for (int x = 1; x < _relationship.getFeatures().size(); x++) {
			stmts.removeAll(toRemove);
			toRemove.clear();
			
			for (Statement statement : stmts) {
				if (statement.getNodeType() == EXPRESSION_STATEMENT) {
					ExpressionStatement e = (ExpressionStatement) statement;

					if (new ASTMatcher().match(e, _relationship.getFeatures().get(x))) {
						toRemove.add(e);
						break;
					}
				}
			}
		}
		
		stmts.removeAll(toRemove);
	}
}