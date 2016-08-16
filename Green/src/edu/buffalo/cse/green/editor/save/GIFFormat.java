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

import static edu.buffalo.cse.green.GreenException.GRERR_FILE_NOT_FOUND;

import java.io.File;
import java.io.IOException;

import org.eclipse.draw2d.IFigure;

import edu.buffalo.cse.green.GreenException;
import edu.buffalo.cse.green.editor.DiagramEditor;
import edu.buffalo.cse.green.util.ImageWriterFile;

/**
 * A saver for the GIF format.
 * 
 * @author bcmartin
 */
public class GIFFormat implements ISaveFormat {
	/**
	 * @see edu.buffalo.cse.green.editor.save.ISaveFormat#saveInformation(edu.buffalo.cse.green.editor.DiagramEditor, java.lang.String, org.eclipse.draw2d.IFigure)
	 */
	public void saveInformation(DiagramEditor editor, String fileName,
			IFigure figure) {
		try {
			ImageWriterFile writer = new ImageWriterFile(
					new File(fileName).getCanonicalPath(), 2);
			writer.saveFigure(figure);
		} catch (IOException iOE) {
			GreenException.fileException(
					GRERR_FILE_NOT_FOUND);
		}
	}
	
	/**
	 * @see edu.buffalo.cse.green.editor.save.ISaveFormat#getExtension()
	 */
	public String getExtension() {
		return "gif";
	}

	/**
	 * @see edu.buffalo.cse.green.editor.save.ISaveFormat#getDescription()
	 */
	public String getDescription() {
		return "GIF Image";
	}
}
