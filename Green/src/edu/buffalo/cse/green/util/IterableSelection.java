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

import java.util.AbstractList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.viewers.IStructuredSelection;

/**
 * A wrapper class that supports iteration over structured selections.
 * 
 * @author bcmartin
 */
public class IterableSelection<T> implements Iterable<T> {
	private IStructuredSelection _selection;

	public IterableSelection(IStructuredSelection selection) {
		_selection = selection;
	}

	/**
	 * @see java.lang.Iterable#iterator()
	 */
	public Iterator<T> iterator() {
		List<T> list = (AbstractList<T>) (List) _selection.toList();
		return list.iterator();
	}
}
