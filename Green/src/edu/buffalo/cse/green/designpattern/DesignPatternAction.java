/* This file is part of Green.
 *
 * Copyright (C) 2005 The Research Foundation of State University of New York
 * All Rights Under Copyright Reserved, The Research Foundation of S.U.N.Y.
 * 
 * Green is free software, licensed under the terms of the Eclipse
 * Public License, version 1.0.  The license is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package edu.buffalo.cse.green.designpattern;

import edu.buffalo.cse.green.editor.action.ContextAction;

/**
 * Parent class of all actions that belong to design patterns. This abstract
 * parent ensures that if any functionality is added to the run() method, it
 * will not be overridden by subclasses. The doRun() method is a convention of
 * actions in Green.
 * 
 * @author bcmartin
 * @author rjtruban
 */
public abstract class DesignPatternAction extends ContextAction {
	/**
	 * @see edu.buffalo.cse.green.editor.action.ContextAction#doRun()
	 */
	protected abstract void doRun();
}
