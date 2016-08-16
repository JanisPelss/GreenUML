/* This file is part of Green.
 *
 * Copyright (C) 2005 The Research Foundation of State University of New York
 * All Rights Under Copyright Reserved, The Research Foundation of S.U.N.Y.
 * 
 * Green is free software, licensed under the terms of the Eclipse
 * Public License, version 1.0.  The license is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package edu.buffalo.cse.green.editor.save;

import org.eclipse.core.runtime.Path;
import org.eclipse.draw2d.IFigure;

import edu.buffalo.cse.green.constants.PluginConstants;
import edu.buffalo.cse.green.editor.DiagramEditor;
import edu.buffalo.cse.green.xml.XMLConverter;

/**
 * Green's default file format.
 * 
 * @author bcmartin
 */
public class GreenFormat implements ISaveFormat {
	/**
	 * @see edu.buffalo.cse.green.editor.save.ISaveFormat#saveInformation(edu.buffalo.cse.green.editor.DiagramEditor, java.lang.String, org.eclipse.draw2d.IFigure)
	 */
	public void saveInformation(DiagramEditor editor, String fileName,
			IFigure figure) {
		editor.setPartName(new Path(fileName).lastSegment());
		
		XMLConverter converter = new XMLConverter();
		editor.getRootModel().toXML(converter);
		editor.saveFile(converter.getEncodedXML());
		editor.markAsSaved();
	}

	/**
	 * @see edu.buffalo.cse.green.editor.save.ISaveFormat#getDescription()
	 */
	public String getDescription() {
		return "Green File";
	}

	/**
	 * @see edu.buffalo.cse.green.editor.save.ISaveFormat#getExtension()
	 */
	public String getExtension() {
		return PluginConstants.GREEN_EXTENSION;
	}
}
