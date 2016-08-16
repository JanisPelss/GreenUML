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
import static edu.buffalo.cse.green.preferences.PreferenceInitializer.P_DISPLAY_METHOD_PARAMETERS;
import static org.eclipse.jdt.ui.refactoring.RenameSupport.UPDATE_REFERENCES;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.gef.commands.Command;
import org.eclipse.jdt.core.Flags;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.ui.refactoring.RenameSupport;

import edu.buffalo.cse.green.PlugIn;
import edu.buffalo.cse.green.editor.DiagramEditor;
import edu.buffalo.cse.green.editor.action.ContextAction;
import edu.buffalo.cse.green.editor.controller.MethodPart;
import edu.buffalo.cse.green.editor.model.commands.DeleteCommand;
import edu.buffalo.cse.green.editor.model.commands.DeleteMethodCommand;

//import org.eclipse.draw2d.geometry.Dimension;
//import static edu.buffalo.cse.green.editor.controller.PropertyChange.Size;

/**
 * Represents a method in the model's hierarchy.
 * 
 * @author hk47
 */
public class MethodModel extends MemberModel<AbstractModel, CompartmentModel, IMethod> {
	public MethodModel(IMethod method) {
		super(method);
	}

	/**
	 * @see edu.buffalo.cse.green.editor.model.MemberModel#getDisplayName()
	 */
	public String getDisplayName() {
		if (!getMember().exists()) return "";
		return getMethodSignature(getMethod());
	}

	/**
	 * @return The <code>IMethod</code> modeled by this class.
	 */
	public IMethod getMethod() {
		return (IMethod) getMember();
	}

	/**
	 * @see edu.buffalo.cse.green.editor.model.AbstractModel#getContextMenuFlag()
	 */
	public int getContextMenuFlag() {
		return ContextAction.CM_METHOD;
	}

	/**
	 * @return True if the member is abstract, false otherwise.
	 */
	public boolean isAbstract() {
		try {
			return getMember().exists() && Flags.isAbstract(getMember().getFlags());
		} catch (JavaModelException e) {
			// don't display error
			return false;
		}
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
		return MethodPart.class;
	}

	/**
	 * @see edu.buffalo.cse.green.editor.model.AbstractModel#getDeleteCommand(edu.buffalo.cse.green.editor.DiagramEditor)
	 */
	public DeleteCommand getDeleteCommand(DiagramEditor editor) {
		return new DeleteMethodCommand(this);
	}

	/**
	 * @see edu.buffalo.cse.green.editor.model.AbstractModel#removeFromParent()
	 */
	public void removeFromParent() {
		
		super.removeFromParent();  
		
		// This line is no longer necessary due to the firePropertyChange method being commented out.
		
//		TypeModel parent = (TypeModel) getParent().getParent(); 
		
		// This firePropertyChange caused the mystical "decremental deletion" bug which in actuality
		// caused the stack to jump out precisely as this method call was executed.
		
//		parent.firePropertyChange(Size, null, new Dimension(0, 0));
	}

	/**
	 * @see edu.buffalo.cse.green.editor.model.MemberModel#getRenameSupport()
	 */
	public RenameSupport getRenameSupport() throws CoreException {
		return RenameSupport.create(getMethod(), "", UPDATE_REFERENCES);
	}

	/**
	 * @param method - The given <code>IMethod</code>.
	 * @return A string representation of the given method.
	 */
	public static String getMethodSignature(IMethod method) {
		boolean displayParameters =
			PlugIn.getBooleanPreference(P_DISPLAY_METHOD_PARAMETERS);
		boolean fqn =
			PlugIn.getBooleanPreference(P_DISPLAY_FQN_TYPE_NAMES);
		
		try {
			String returnType = "";
			String methodName = method.getElementName();
			String parameters = "(";
			
			// if the method isn't a constructor, get it's return type
			if (!method.isConstructor()) {
				returnType = getSignatureName(method.getReturnType(),
						fqn) + " ";
			}

			String[] types = method.getParameterTypes();
			String[] names;
			
			try {
				names = method.getParameterNames();
			} catch (JavaModelException e) {
				names = null;
			}
			
			StringBuffer param = new StringBuffer();
			
			for (int i = 0; i < method.getParameterTypes().length; i++) {
				param.append(", ");
				param.append(getSignatureName(types[i], fqn));
				
				if (names != null) {
					if (displayParameters) {
						param.append(" " + names[i]);
					}
				}
			}

			if (param.toString().length() > 0) {
				parameters += param.toString().substring(2);
			}
			
			parameters += ")";
			
			return returnType + methodName + parameters;
		} catch (JavaModelException e) {
			e.printStackTrace();
			return "";
		}
	}
}