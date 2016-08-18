/* This file is part of Green.
 *
 * Copyright (C) 2005 The Research Foundation of State University of New York
 * All Rights Under Copyright Reserved, The Research Foundation of S.U.N.Y.
 * 
 * Green is free software, licensed under the terms of the Eclipse
 * Public License, version 1.0.  The license is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package edu.buffalo.cse.green.dialogs.wizards;

public enum NewElementWizardSettings {
	ClassFieldSettings(
			false, true, true, false, true, true, false, false, false),
	ClassMethodSettings(
			true, true, true, true, true, true, false, false, false),
	InterfaceFieldSettings(
			false, true, true, false, false, false, false, true, true),
	InterfaceMethodSettings(
			true, true, true, false, false, false, false, false, false);
	
	private boolean _enableAbstract;
	private boolean _enableFinal;
	private boolean _enableStatic;
	private boolean _selectAbstract;
	private boolean _selectFinal;
	private boolean _selectStatic;
	private boolean _useAbstract;
	private boolean _useFinal;
	private boolean _useStatic;

	NewElementWizardSettings(boolean useAbstract, boolean useFinal,
			boolean useStatic, boolean enableAbstract, boolean enableFinal,
			boolean enableStatic, boolean selectAbstract, boolean selectFinal,
			boolean selectStatic) {
		_useAbstract = useAbstract;
		_useFinal = useFinal;
		_useStatic = useStatic;
		_enableAbstract = enableAbstract;
		_enableFinal = enableFinal;
		_enableStatic = enableStatic;
		_selectAbstract = selectAbstract;
		_selectFinal = selectFinal;
		_selectStatic = selectStatic;
	}
	
	public boolean isAbstractAvailable() {
		return _useAbstract;
	}

	public boolean isAbstractEnabled() {
		return _enableAbstract;
	}

	public boolean isAbstractSelected() {
		return _selectAbstract;
	}

	public boolean isFinalAvailable() {
		return _useFinal;
	}

	public boolean isFinalEnabled() {
		return _enableFinal;
	}

	public boolean isFinalSelected() {
		return _selectFinal;
	}

	public boolean isStaticAvailable() {
		return _useStatic;
	}

	public boolean isStaticEnabled() {
		return _enableStatic;
	}

	public boolean isStaticSelected() {
		return _selectStatic;
	}
}
