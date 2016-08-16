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
import org.eclipse.jdt.core.JavaModelException;

import edu.buffalo.cse.green.PlugIn;
import edu.buffalo.cse.green.editor.model.AbstractModel;
import edu.buffalo.cse.green.editor.model.MethodModel;
import edu.buffalo.cse.green.editor.model.TypeModel;

/**
 * Command for adding a method to an existing type
 * 
 * @author cgullans
 */
public class AddMethodCommand extends Command {
	/**
	 * The name of the method.
	 */
	private String _methodName;

	/**
	 * The return type for the method
	 */
	private String _returnType;
	
	/**
	 * The parent of the model being created.
	 */
	private TypeModel _model;

	/**
	 * The representation of the method declaration.
	 */
	private String _methodString;
	
	/**
	 * String of the method's parameters.
	 */
	private String _parameters;
	
	/**
	 * Whether or not imports will be automatically created
	 */
	private boolean _forcingImports;
	
	public AddMethodCommand(
			String methodName,
			String returnTypeName,
			String methodString,
			String methodParameters,
			boolean forcingImports,
			TypeModel model) {
		_model = model;
		_methodName = methodName;
		_returnType = returnTypeName;
		_methodString = methodString;
		_parameters = methodParameters;
		_forcingImports = forcingImports;
	}

	/**
	 * @see org.eclipse.gef.commands.Command#execute()
	 */
	public void execute() {
		if (!_model.getType().getMethod(_methodName, new String[] {}).exists()) {
			try {
				if(_forcingImports) {
					String methodString = _methodString;
					
					if(!isSimple(_returnType)) {
						_model.getType().getCompilationUnit()
							.createImport(_returnType, null, PlugIn.getEmptyProgressMonitor());
						methodString = methodString.replace(_returnType, simplify(_returnType));
					}

					methodString = methodString.replace(_parameters, simplify(_parameters));

					if(_parameters.length() > 0) {	//if parameters exist
						ArrayList<String> types = getTypes(_parameters);
						for(String typeName : types) {
							if(!isSimple(typeName)) {
								_model.getType().getCompilationUnit()
									.createImport(typeName, null, PlugIn.getEmptyProgressMonitor());
							}
						}
					}
					_model.getType().createMethod(methodString, null, false,
							PlugIn.getEmptyProgressMonitor());
				}
				else {
					_model.getType().createMethod(_methodString, null, false,
							PlugIn.getEmptyProgressMonitor());
				}
			} catch (JavaModelException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * @see org.eclipse.gef.commands.Command#undo()
	 */
	public void undo() {
		List<AbstractModel> list = _model.getMethodCompartmentModel()
				.getChildren();
		boolean continue0 = true;
		int index = 0;
		MethodModel mModel = null;

		while (continue0 && index < list.size()) {
			MethodModel tempModel = (MethodModel) list.get(index);
			if (tempModel.getMember().getElementName().equals(_methodName)) {
				mModel = tempModel;
				continue0 = false;
			}
			index++;
		}
		
		try {
			mModel.getMember().delete(true, PlugIn.getEmptyProgressMonitor());
		} catch (JavaModelException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 
	 * @return an array of types from a parameter list
	 */
	private ArrayList<String> getTypes(String s) {
		String temp = s;
		ArrayList<String> types = new ArrayList<String>();
		while(temp.indexOf(' ') != -1) {
			int firstSpace = temp.indexOf(' ');
			types.add(new String(temp.substring(0, firstSpace)));
			if(temp.indexOf(' ', firstSpace + 1) == -1) {
				temp = "";
			}
			else {
				temp = temp.substring(temp.indexOf(' ', firstSpace + 1) + 1);
			}
			//Remove the scanned type and name
		}
		return types;
	}
	
	private boolean isSimple(String typeName) {
		return typeName.indexOf('.') == -1;
	}
	
	/**
	 * 
	 * @param parameters a list of type names [and identifiers]
	 * @return simple names of types
	 */
	private String simplify(String s) {
		if(s.indexOf('.') == -1) { //Already simple
			return s;
		}
		else if(s.indexOf(' ') == -1) { //No spaces, i.e. type only
			return s.substring(s.lastIndexOf('.') + 1);
		}
		else {
			ArrayList<String> typeFQN = getTypes(s);
			for(String typeName : typeFQN) {
				String simpleName = typeName.substring(typeName.lastIndexOf('.') + 1);
				s = s.replace(typeName, simpleName);
			}
		}
		return s;
	}
}
