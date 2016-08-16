/* This file is part of Green.
 *
 * Copyright (C) 2005 The Research Foundation of State University of New York
 * All Rights Under Copyright Reserved, The Research Foundation of S.U.N.Y.
 * 
 * Green is free software, licensed under the terms of the Eclipse
 * Public License, version 1.0.  The license is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package edu.buffalo.cse.green.editor.view;

import org.eclipse.draw2d.Bendpoint;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;

/**
 * A bendpoint with the ability to toggle between the behaviors of an
 * <code>AbsoluteBendpoint</code> and a <code>RelativeBendpoint</code>.
 * 
 * @author bcmartin
 */
public class GreenBendpoint implements Bendpoint {
	private RelationshipFigure _rFigure;
	private Point _location = new Point(0, 0);
	private Point _oldSource, _oldTarget;
	
	private Point fixPoint( Point p ) {
		Point p2 = p.getCopy( );
		_rFigure.translateToRelative( p2 );
		return p2;
	}
	
	public GreenBendpoint(RelationshipFigure rFigure) {
		setConnection(rFigure);
	}

	/**
	 * @return the figure representing the parent of this bendpoint
	 */
	protected RelationshipFigure getConnection() {
		return _rFigure;
	}
	
	/**
	 * Sets the bendpoint's initial location in the editor 
	 * 
	 * @param x - The x-coordinate of the location
	 * @param y - The y-coordinate of the location
	 */
	public void setAbsoluteLocation(int x, int y) {
		setAbsoluteLocation(new Point(x, y));
	}
	
	/**
	 * Sets the bendpoint's initial location in the editor
	 * 
	 * @param p - The location
	 */
	public void setAbsoluteLocation(Point p) {
		_location = fixPoint( p );
	}
	
	/**
	 * @return The absolute location of the bendpoint. This should be called if
	 * the location should be retrieved without regard to movement of the
	 * anchors.
	 */
	public Point getAbsoluteLocation() {
		return _location;
	}
	
	/**
	 * @see org.eclipse.draw2d.Bendpoint#getLocation()
	 */
	public Point getLocation() {
		/* if the relationship is recursive, ensure the points are at a constant
		 * distance from the recursive anchor (set in RelationshipFigure)
		 */
		if (_rFigure.isRecursive()) {
			Point topRight =
				_rFigure.getRecursiveAnchor().getBounds().getTopRight();
			
			return new Point(
					(int) _location.x + topRight.x,
					(int) _location.y + topRight.y);
		}
		
		/* if this is the first time the location is retrieved, use the initial
		 * location set by CreateBendpointCommand; otherwise, calculate the
		 * location according to the rules below
		 */
		if (_oldSource == null) {
			// get the current location of the anchors
			_oldSource = getSourceLocation();
			_oldTarget = getTargetLocation();
			return _location;
		} else {
			/* get the difference in location from the new anchor positions to
			 * the old anchor positions
			 */ 
			Dimension ds = getSourceLocation().getDifference(_oldSource);
			Dimension dt = getTargetLocation().getDifference(_oldTarget);
			// get the current location of the anchors
			_oldSource = getSourceLocation();
			_oldTarget = getTargetLocation();
			/* the new location is calculated as follows:
			 * -the points are calculated as following the source by a weight
			 * of 0.5 and the target by a weight of 0.5
			 */
			_location = new Point(
					(int) _location.x + (ds.width + dt.width) / 2,
					(int) _location.y + (ds.height + dt.height) / 2);
			return _location;
		}
	}
		
	/**
	 * Sets the figure representing the parent of this bendpoint
	 * 
	 * @param rFigure - The figure
	 */
	private void setConnection(RelationshipFigure rFigure) {
		_rFigure = rFigure;
	}
		
	/**
	 * @return the source anchor of the relationship figure
	 */
	private Point getSourceLocation() {
		return fixPoint( getConnection().getSourceAnchor().getReferencePoint() );
	}

	/**
	 * @return the target anchor of the relationship figure
	 */
	private Point getTargetLocation() {
		return fixPoint( getConnection().getTargetAnchor().getReferencePoint() );
	}
}
