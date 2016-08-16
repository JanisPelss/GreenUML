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

import org.eclipse.draw2d.IFigure;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageLoader;
import org.eclipse.swt.graphics.RGB;

/**
 * Base class for objects which saves a <code>IFigure</code>'s image.
 * 
 * @author evertwoo
 */
public abstract class ImageWriter {
	protected ImageLoader _imageIO;
	protected int _format;
	protected RGB _backgroundColor;

	/**
	 * Writes an image in the specified format.
	 * 
	 * @param imageLoaderFormat - The format to save in. See ImageLoader. This
	 * value is available from {@link ImageWriterUtil}.
	 */
	public ImageWriter(int imageLoaderFormat) {
		_format = imageLoaderFormat;
		_imageIO = new ImageLoader();
		_imageIO.logicalScreenWidth = 1;
		_imageIO.logicalScreenWidth = 1;
	}

	/**
	 * Saves a figure. Subclasses should not override this method, but should
	 * instead override {@link #saveImageToStream()}
	 * 
	 * @see #saveImageToStream()
	 * 
	 * @param figure - The figure to save the image of.
	 */
	public void saveFigure(IFigure figure) {
		if (_backgroundColor == null) {
			ImageWriterUtil.writeFigureToLoader(figure, _imageIO, _format);
		} else {
			ImageWriterUtil.writeFigureToLoader(figure, _imageIO, _format,
					_backgroundColor);
		}

		saveImageToStream();
	}

	/**
	 * Saves an image.
	 * 
	 * @param image - The image to save.
	 */
	public void saveImage(Image image) {
		ImageWriterUtil.writeImageToLoader(image, _imageIO, _format);
		saveImageToStream();
	}

	/**
	 * Sets the background color for the image.
	 * 
	 * @param backgroundColor - The color to set the background to.
	 */
	public void setBackgroundColor(RGB backgroundColor) {
		_backgroundColor = backgroundColor;
	}

	/**
	 * Saves the image in <code>_imageIO</code> into the destination, in the
	 * format identified by <code>_format</code>.
	 */
	protected abstract void saveImageToStream();
}
