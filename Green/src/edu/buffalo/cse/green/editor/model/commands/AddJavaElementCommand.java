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

import static edu.buffalo.cse.green.preferences.PreferenceInitializer.P_DISPLAY_FQN_TYPE_NAMES;
import static edu.buffalo.cse.green.preferences.PreferenceInitializer.P_AUTOARRANGE;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.eclipse.gef.commands.Command;
import org.eclipse.jdt.core.IClassFile;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.dialogs.MessageDialog;

import edu.buffalo.cse.green.GreenException;
import edu.buffalo.cse.green.PlugIn;
import edu.buffalo.cse.green.editor.DiagramEditor;
import edu.buffalo.cse.green.editor.model.AbstractModel;
import edu.buffalo.cse.green.editor.model.RootModel;
import edu.buffalo.cse.green.editor.model.TypeModel;

/**
 * Adds an <code>IJavaElement</code> to a <code>DiagramEditor</code>.
 * 
 * @author bcmartin
 * @author zgwang
 */
public class AddJavaElementCommand extends Command {
	private DiagramEditor _editor;
	private IJavaElement _element;
	private List<Command> _commands;
	
	private ArrayList<IJavaElement> _dupElement;
	
	public AddJavaElementCommand(
			DiagramEditor editor,
			IJavaElement element) {
		_editor = editor;
		_element = element;
		_commands = new ArrayList<Command>();
		_dupElement = new ArrayList<IJavaElement>();
	}

	/**
	 * @see org.eclipse.gef.commands.Command#execute()
	 * 
	 */
	public void execute() {
		RootModel root = _editor.getRootModel();
		_dupElement.clear();
		
		// Determine the type of <code>IJavaElement</code> and take the
		// appropriate action.
		try {
			if (_element instanceof IJavaProject) {
				IJavaProject project = (IJavaProject) _element;
				
				for (IPackageFragment packFrag
						: project.getPackageFragments()) {
					if (!packFrag.isReadOnly()) {
						openPackage(root, packFrag);
					}
				}
			} else if (_element instanceof IPackageFragment) {
				IPackageFragment packFrag = (IPackageFragment) _element;
				openPackage(root, packFrag);
			} else if (_element instanceof ICompilationUnit) {
				openCU(root, (ICompilationUnit) _element);
			} else if (_element instanceof IClassFile) {
				openClass(root, (IClassFile) _element);
			} else if (_element instanceof IType) {
				createType(root, (IType) _element);
			} else if (_element instanceof IMember) {
				_element = _element.getAncestor(IJavaElement.TYPE);
				createType(root, (IType) _element);
			} else {
				GreenException.illegalOperation(
						"Cannot open this kind of Java Element: " + _element);
			}

			if(_dupElement.size() > 0) {
				boolean fqn = PlugIn.getBooleanPreference(P_DISPLAY_FQN_TYPE_NAMES);
				ArrayList<String> eleNames = new ArrayList<String>();
				String prompt = "Selected type is already in the diagram:\n\n";
				if(_dupElement.size() > 1) {
					prompt = prompt.replace("type", "types");
					prompt = prompt.replace("is", "are");
				}
				Iterator itr = _dupElement.iterator();
				
				while(itr.hasNext()) {
					IType e = (IType) itr.next();
					eleNames.add(fqn ? e.getFullyQualifiedName() : e.getElementName());
				}
				
				Collections.sort(eleNames);
				Iterator itr2 = eleNames.iterator();
				String namePrompt = "";
				
				while(itr2.hasNext()) {
					namePrompt += "     " + (String)itr2.next() + "\n";
				}
				
				MessageDialog.openInformation(DiagramEditor.getActiveEditor().getSite().getShell(),
						"Information", prompt + namePrompt);
			}
			
			if(PlugIn.getBooleanPreference(P_AUTOARRANGE))
			{
				_editor.execute(new AutoArrangeCommand());
				_editor.execute(new AutoArrangeCommand());
			}
			else
			{
				_editor.refresh();
				_editor.checkDirty();
			}
		} catch (JavaModelException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Opens all the compilation units in the given
	 * <code>IPackageFragment</code>.
	 */
	private void openPackage(RootModel model, IPackageFragment packFrag)
	throws JavaModelException {
		ICompilationUnit[] cus = packFrag.getCompilationUnits();
		IClassFile[] classFiles = packFrag.getClassFiles();
		
		for (ICompilationUnit cu : cus) {
			openCU(model, cu);
		}
		
		for (IClassFile classFile : classFiles) {
			openClass(model, classFile);
		}
	}

	/**
	 * Opens a compilation unit and all the types in it.
	 */
	private void openCU(RootModel root, ICompilationUnit cu)
	throws JavaModelException {
		IType[] types = cu.getAllTypes();

		for (int i = 0; i < types.length; i++) {
			createType(root, types[i]);
		}
	}

	/**
	 * Loads a class into the editor.
	 */
	private void openClass(RootModel root, IClassFile classFile) {
		IType type = classFile.getType();
		if(type.exists()) {
			createType(root, type);
		}
	}

	/**
	 * @see org.eclipse.gef.commands.Command#canUndo()
	 */
	public boolean canUndo() {
		return false;
	}
	
	/**
	 * Creates a <code>TypeModel</code> in the given <code>UMLRootModel</code>. 
	 * 
	 * @param root - The root model.
	 * @param type - The type's model.
	 * 
	 * @author jeg34
	 */
	private void createType(RootModel root, IType type) {
		boolean isInRoot = false;
		List<AbstractModel> children = root.getChildren();
		Iterator itr = children.iterator();
		
		while(itr.hasNext()) {
			AbstractModel currModel = (AbstractModel)itr.next();
			if((currModel).getJavaElement() != null) {
				if((currModel).getJavaElement().equals(type)) {
					_dupElement.add(type);
					isInRoot = true;
				}
			}
		}
		
		if(!isInRoot) {
			Command command = new AddTypeCommand(root, type);
			_commands.add(command);
			command.execute();
		}
	}
	
	/**
	 * @see org.eclipse.gef.commands.Command#undo()
	 */
	public void undo() {
		for (Command command : _commands) {
			command.undo();
		}
		
		_commands.clear();
	}
	
	/**
	 * @see org.eclipse.gef.commands.Command#redo()
	 */
	public void redo() {
		execute();
	}
	
	/**
	 * Adds a single type to <code>UMLRootModel</code>.
	 * 
	 * @author bcmartin
	 */
	class AddTypeCommand extends Command {
		/**
		 * The root model.
		 */
		private RootModel _root;
		
		/**
		 * The type being added.
		 */
		private IType _type;
		
		/**
		 * The <code>TypeModel</code> representing the added type.
		 */
		private TypeModel _model;

		public AddTypeCommand(RootModel root, IType type) {
			_root = root;
			_type = type;
		}
		
		/**
		 * @see org.eclipse.gef.commands.Command#execute()
		 */
		public void execute() {
			_model = _root.createTypeModel(_type);

			if (_model != null) {
				_root.placeUMLBox(_model);
			}
		}
		
		/**
		 * @see org.eclipse.gef.commands.Command#undo()
		 */
		public void undo() {
			_model.removeFromParent();
		}
		
		/**
		 * @see org.eclipse.gef.commands.Command#redo()
		 */
		public void redo() {
			execute();
		}
	}
}