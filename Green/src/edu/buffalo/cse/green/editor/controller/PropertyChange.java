/* This file is part of Green.
 *
 * Copyright (C) 2005 The Research Foundation of State University of New York
 * All Rights Under Copyright Reserved, The Research Foundation of S.U.N.Y.
 * 
 * Green is free software, licensed under the terms of the Eclipse
 * Public License, version 1.0.  The license is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package edu.buffalo.cse.green.editor.controller;

public enum PropertyChange {
	Children(), Element(), Type(), Name(),
	DisposeIcon(), Location(), Redraw(), Refresh(), Size(), Visibility(),
	Note(),
	RelationshipBendpoint(), RelationshipCardinality(),
	RelationshipSource(), RelationshipTarget(),
	IncomingRelationship(), OutgoingRelationship(),
	GenerateRelationship(), RemoveRelationship(), UpdateRelationships();
}
