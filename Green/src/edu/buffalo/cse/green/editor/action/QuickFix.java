/* This file is part of Green.
 *
 * Copyright (C) 2005 The Research Foundation of State University of New York
 * All Rights Under Copyright Reserved, The Research Foundation of S.U.N.Y.
 * 
 * Green is free software, licensed under the terms of the Eclipse
 * Public License, version 1.0.  The license is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package edu.buffalo.cse.green.editor.action;

import org.eclipse.core.resources.IMarker;
import org.eclipse.ui.IMarkerResolution;

public class QuickFix {
	private IMarker _marker;
	private IMarkerResolution _resolution;

	public QuickFix(IMarker marker, IMarkerResolution resolution) {
		_marker = marker;
		_resolution = resolution;
	}
	
	public IMarker getMarker() {
		return _marker;
	}
	
	public IMarkerResolution getResolution() {
		return _resolution;
	}
}
