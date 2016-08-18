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

import org.eclipse.jdt.core.IBuffer;
import org.eclipse.jface.text.AbstractDocument;
import org.eclipse.jface.text.DefaultLineTracker;
import org.eclipse.jface.text.ILineTracker;
import org.eclipse.jface.text.ITextStore;

/**
 * Basic {@link org.eclipse.jface.text.IDocument IDocument} support for source
 * code compilation units. Adapted from
 * {@link org.eclipse.jface.text.Document Document}.
 * 
 * @author evertwoo
 */
public class IModifiableBuffer extends AbstractDocument {
	public IModifiableBuffer(IBuffer buffer) {
		setTextStore(new BufferModifierStore(buffer));
		ILineTracker lineTracker = new DefaultLineTracker();
		lineTracker.set(buffer.getContents());
		setLineTracker(lineTracker);
		completeInitialization();
	}

	/**
	 * Stores the modified contents of the buffer.
	 * 
	 * @author evertwoo
	 */
	class BufferModifierStore implements ITextStore {
		private IBuffer _buffer;

		public BufferModifierStore(IBuffer buffer) {
			_buffer = buffer;
		}

		/**
		 * @see org.eclipse.jface.text.ITextStore#get(int)
		 */
		public char get(int offset) {
			return _buffer.getChar(offset);
		}

		/**
		 * @see org.eclipse.jface.text.ITextStore#get(int, int)
		 */
		public String get(int offset, int length) {
			return _buffer.getText(offset, length);
		}

		/**
		 * @see org.eclipse.jface.text.ITextStore#getLength()
		 */
		public int getLength() {
			return _buffer.getLength();
		}

		/**
		 * @see org.eclipse.jface.text.ITextStore#replace(int, int, java.lang.String)
		 */
		public void replace(int offset, int length, String text) {
			_buffer.replace(offset, length, text);
		}

		/**
		 * @see org.eclipse.jface.text.ITextStore#set(java.lang.String)
		 */
		public void set(String text) {
			_buffer.setContents(text);
		}
	}
}
