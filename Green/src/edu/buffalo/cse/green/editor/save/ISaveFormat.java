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

import org.eclipse.draw2d.IFigure;

import edu.buffalo.cse.green.editor.DiagramEditor;

/**
 * Represents a kind of save format. This format can be accessed by Green's save
 * dialog, allowing the editor's contents to be saved as many different kinds of
 * files.
 * 
 * @author bcmartin
 */
public interface ISaveFormat {
	/**
	 * This method is called by the editor when a save needs to be performed.
	 *
	 * @param editor - The <code>DiagramEditor</code> holding the diagram's
	 * contents.
	 * @param fileName - The full path to the file.
	 * @param figure - The <code>IFigure</code> at the root of the editor.
	 */
	public void saveInformation(DiagramEditor editor, String fileName,
			IFigure figure);
	
	/**
	 * @return A description of this file type.
	 */
	public String getDescription();
	
	/**
	 * @return The file extension this implementation represents. The extension
	 * should consist of alphanumeric characters only.
	 */
	public String getExtension();
}
