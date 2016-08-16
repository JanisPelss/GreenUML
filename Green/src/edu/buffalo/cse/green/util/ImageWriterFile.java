/* This file is part of Green.
 *
 * Copyright (C) 2005 The Research Foundation of State University of New York
 * All Rights Under Copyright Reserved, The Research Foundation of S.U.N.Y.
 * 
 * Green is free software, licensed under the terms of the Eclipse
 * Public License, version 1.0.  The license is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package edu.buffalo.cse.green.util;

/**
 * Creates graphics files containing <code>IFigure</code>s.
 * 
 * @author evertwoo
 */
public class ImageWriterFile extends ImageWriter {
	private String _filename;

	/**
	 * Writes an image to the specified file in the specified format.
	 * 
	 * @param filename
	 *            The name of the file to save to.
	 * @param imageLoaderFormat
	 *            The format to save in. See ImageLoader. This value is
	 *            available from {@link ImageWriterUtil}.
	 */
	public ImageWriterFile(String filename, int imageLoaderFormat) {
		super(imageLoaderFormat);
		_filename = filename;
	}

	/**
	 * @see edu.buffalo.cse.green.util.ImageWriter#saveImageToStream()
	 */
	protected void saveImageToStream() {
		_imageIO.save(_filename, _format);
	}
}