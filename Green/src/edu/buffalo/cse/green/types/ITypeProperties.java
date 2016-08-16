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
import org.eclipse.jface.resource.ImageDescriptor;

/**
 * The interface used for plugins that add a type to the palette. This also
 * allows the given type to be loaded into the editor. For example, if you don't
 * have a plugin to support "enum" types, you can't load them into the editor.
 * 
 * @author bcmartin
 */
public interface ITypeProperties {
	/**
	 * @return The palette item's icon's <code>ImageDescriptor</code>
	 */
	ImageDescriptor getIconDescriptor();

	/**
	 * @return true if this type has a field compartment, false otherwise
	 */
	boolean hasFieldCompartment();

	/**
	 * @return true if this type has a method compartment, false otherwise
	 */
	boolean hasMethodCompartment();

	/**
	 * @return The tooltip when hovering over the palette item
	 */
	String getDescription();

	/**
	 * @return The class of the dialog displayed when a new type is created;
	 * should be a subclass of <code>NewElementWizard</code>
	 */
	Class getDialogClass();

	/**
	 * @return The palette item's label 
	 */
	String getLabel();

	/**
	 * @return true if this plugin supports the given type, false otherwise.
	 * 
	 * @param type - The given <code>Type</code>. 
	 */
	boolean supportsType(IType type);
}
