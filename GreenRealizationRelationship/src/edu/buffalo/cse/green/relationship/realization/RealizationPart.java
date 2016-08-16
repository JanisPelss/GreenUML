/* This file is part of Green.
 *
 * Copyright (C) 2005 The Research Foundation of State University of New York
 * All Rights Under Copyright Reserved, The Research Foundation of S.U.N.Y.
 * 
 * Green is free software, licensed under the terms of the Eclipse
 * Public License, version 1.0.  The license is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package edu.buffalo.cse.green.relationship.realization;

import edu.buffalo.cse.green.editor.controller.RelationshipPart;
import edu.buffalo.cse.green.editor.view.RelationshipFigure;

import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.PolygonDecoration;
import org.eclipse.draw2d.RotatableDecoration;

/** author bcmartin */
public class RealizationPart extends RelationshipPart {
	/**
	 * @see edu.buffalo.cse.green.editor.controller.RelationshipPart#createConnection()
	 */
	public RelationshipFigure createConnection() {
		RelationshipFigure connection = new RelationshipFigure();
		connection.addRelationshipFigureListener(this);
		connection.setLineStyle(Graphics.LINE_DASH);
		connection.setLineWidth(0);
		return connection;
	}

	/**
	 * @see edu.buffalo.cse.green.editor.controller.RelationshipPart#createSourceArrow()
	 */
	public RotatableDecoration createSourceArrow() {
		return null;
	}

	/**
	 * @see edu.buffalo.cse.green.editor.controller.RelationshipPart#createTargetArrow()
	 */
	public RotatableDecoration createTargetArrow() {
		PolygonDecoration targetArrow = new PolygonDecoration();
		targetArrow.setScale(14, 7);
		return targetArrow;
	}
}