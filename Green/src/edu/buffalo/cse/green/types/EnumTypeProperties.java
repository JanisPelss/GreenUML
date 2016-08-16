/* This file is part of Green.
 *
 * Copyright (C) 2005 The Research Foundation of State University of New York
 * All Rights Under Copyright Reserved, The Research Foundation of S.U.N.Y.
 * 
 * Green is free software, licensed under the terms of the Eclipse
 * Public License, version 1.0.  The license is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package edu.buffalo.cse.green.types;

import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.ui.JavaPluginImages;
import org.eclipse.jface.resource.ImageDescriptor;

import edu.buffalo.cse.green.dialogs.wizards.NewEnumWizard;


/**
 * A plugin to add enums to Green's editor.
 * 
 * @author bcmartin
 */
public class EnumTypeProperties implements ITypeProperties {
	/**
	 * @see edu.buffalo.cse.green.types.ITypeProperties#getDescription()
	 */
	public String getDescription() {
		return "Create Enum";
	}

	/**
	 * @see edu.buffalo.cse.green.types.ITypeProperties#getDialogClass()
	 */
	public Class getDialogClass() {
		return NewEnumWizard.class;
	}
	
	/**
	 * @see edu.buffalo.cse.green.types.ITypeProperties#getIconDescriptor()
	 */
	public ImageDescriptor getIconDescriptor() {
		return JavaPluginImages.DESC_OBJS_ENUM;
	}

	/**
	 * @see edu.buffalo.cse.green.types.ITypeProperties#getLabel()
	 */
	public String getLabel() {
		return "Enum";
	}

	/**
	 * @see edu.buffalo.cse.green.types.ITypeProperties#hasFieldCompartment()
	 */
	public boolean hasFieldCompartment() {
		return true;
	}

	/**
	 * @see edu.buffalo.cse.green.types.ITypeProperties#hasMethodCompartment()
	 */
	public boolean hasMethodCompartment() {
		return true;
	}

	/**
	 * @see edu.buffalo.cse.green.types.ITypeProperties#supportsType(org.eclipse.jdt.core.IType)
	 */
	public boolean supportsType(IType type) {
		try {
			return type.isEnum();
		} catch (JavaModelException e) {
			e.printStackTrace();
			return false;
		}
	}
}
