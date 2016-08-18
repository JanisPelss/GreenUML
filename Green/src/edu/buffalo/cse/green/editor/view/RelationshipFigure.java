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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.draw2d.ConnectionAnchor;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.PolylineConnection;

/**
 * A figure that represents a relationship.
 * 
 * @author hk47
 */
public class RelationshipFigure extends PolylineConnection {
	private List<RelationshipFigureListener> _listeners;
	private IFigure _anchor;
	
	public RelationshipFigure() {
		_listeners = new ArrayList<RelationshipFigureListener>();
	}
	
	public void paint(Graphics g) {
		g.setAntialias( org.eclipse.swt.SWT.ON );
		if( !g.getClass( ).getPackage( ).getName( ).equals( "org.eclipse.draw2d" ) && getLineWidth( ) == 0 ) {
			setLineWidth( 1 );
			super.paint( g );
			setLineWidth( 0 );
		} else super.paint( g );
	}

	/**
	 * @see org.eclipse.draw2d.IFigure#addNotify()
	 */
	public void addNotify() {
		super.addNotify();

		for (RelationshipFigureListener listener : _listeners) {
			listener.relationshipFigureWasAdded(this);
		}
	}

	/**
	 * @see org.eclipse.draw2d.IFigure#removeNotify()
	 */
	public void removeNotify() {
		super.removeNotify();

		for (RelationshipFigureListener listener : _listeners) {
			listener.relationshipFigureWasRemoved(this);
		}
	}

	/**
	 * Adds a listener to the relationship figure.
	 * 
	 * @param listener - The listener.
	 */
	public void addRelationshipFigureListener(
			RelationshipFigureListener listener) {
		if (listener != null) {
			_listeners.add(listener);
		}
	}

	/**
	 * Removes a listener from the relationship figure.
	 * 
	 * @param listener - The listener.
	 */
	public void removeRelationshipFigureListener(
			RelationshipFigureListener listener) {
		_listeners.remove(listener);
	}

	/**
	 * @see org.eclipse.draw2d.AnchorListener#anchorMoved(org.eclipse.draw2d.ConnectionAnchor)
	 */
	public void anchorMoved(ConnectionAnchor anchor) {
		super.anchorMoved(anchor);

		for (RelationshipFigureListener listener : _listeners) {
			listener.relationshipFigureMoved(this);
		}
	}

	/**
	 * @return True if the figure represents a recursive relationship, false
	 * otherwise.
	 */
	public boolean isRecursive() {
		return (_anchor != null);
	}
	
	/**
	 * Sets the anchor for a recursive relationship.
	 * 
	 * @param recursiveAnchor - The anchor.
	 */
	public void setRecursive(IFigure recursiveAnchor) {
		_anchor = recursiveAnchor;
	}
	
	/**
	 * @return The anchor for a recursive realtionship.
	 */
	public IFigure getRecursiveAnchor() {
		return _anchor;
	}
	
	/**
	 * An interface for all relationship figure listeners
	 */
	public interface RelationshipFigureListener {
		/**
		 * Called when the figure moves.
		 * 
		 * @param movedFigure - The figure.
		 */
		public void relationshipFigureMoved(RelationshipFigure movedFigure);

		/**
		 * Called when the figure is added.
		 * 
		 * @param addedFigure - The figure.
		 */
		public void relationshipFigureWasAdded(RelationshipFigure addedFigure);

		/**
		 * Called when the figure is removed.
		 * 
		 * @param removedFigure - The figure.
		 */
		public void relationshipFigureWasRemoved(
				RelationshipFigure removedFigure);
	}
}