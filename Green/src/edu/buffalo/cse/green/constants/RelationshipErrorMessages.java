/* This file is part of Green.
 *
 * Copyright (C) 2005 The Research Foundation of State University of New York
 * All Rights Under Copyright Reserved, The Research Foundation of S.U.N.Y.
 * 
 * Green is free software, licensed under the terms of the Eclipse
 * Public License, version 1.0.  The license is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package edu.buffalo.cse.green.constants;

/**
 * Error message constants for relationships.
 * 
 * @author dk29
 */
public final class RelationshipErrorMessages {
	public static final String ERROR_GENERALIZATION =
		"Source and target types are not of the same kind (class extending "
		+ "class, interface extending interface).";
	public static final String ERROR_INTERNAL =
		"Internal error has occurred.";
	public static final String ERROR_REALIZATION =
		"Source type is not an interface, or the target type is not a class.";
	public static final String ERROR_RECURSIVE =
		"Recursive relationship is disallowed for this type of relationship.";
	public static final String ERROR_SOURCE_IS_INTERFACE =
		"The source type is an interface, which cannot contain code.";
	public static final String ERROR_TARGET_IN_DEFAULT = 
		"The source type is not in the default package, but the target type "
		+ "is.  Java does not allow this kind of reference.";
	public static final String ERROR_UNSPECIFIED_SOURCE =
		"The source type of the relationship is not specified.";
	public static final String ERROR_UNSPECIFIED_TARGET =
		"The target type of the relationship is not specified.";
}
