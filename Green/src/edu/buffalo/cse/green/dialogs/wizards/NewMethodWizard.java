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

import static edu.buffalo.cse.green.constants.DialogStrings.WIZARD_ADD_METHOD_MESSAGE;
import static edu.buffalo.cse.green.constants.DialogStrings.WIZARD_ADD_METHOD_TITLE;
import static edu.buffalo.cse.green.constants.DialogStrings.WIZARD_ADD_METHOD_TYPE;

import static edu.buffalo.cse.green.dialogs.wizards.NewElementWizardSettings.ClassMethodSettings;
import static edu.buffalo.cse.green.dialogs.wizards.NewElementWizardSettings.InterfaceMethodSettings;

import java.util.List;

import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.swt.widgets.Composite;

import edu.buffalo.cse.green.PlugIn;
import edu.buffalo.cse.green.editor.model.TypeModel;

/**
 * Wizard used for creating methods
 * 
 * @author kfjacobs
 * @author zgwang
 */
public class NewMethodWizard extends NewElementWizard {
	private NewMethodWizardPage _page;
	private String _methodName;
	private String _javadocComment;
	private TypeModel _parent;
	private List<String> _modifiers;
	private String _returnTypeName;
	private String _parameters;
	private boolean _forceImports;

	public NewMethodWizard(TypeModel parent) {
		super();
		_parent = parent;
		_methodName = "";
		_javadocComment = "";
		_returnTypeName = "";
		_parameters = "";
		_forceImports = false;
		setDialogSettings(PlugIn.getDefault().getDialogSettings());
		setWindowTitle(WIZARD_ADD_METHOD_TITLE);
	}

	/**
	 * @see org.eclipse.jface.wizard.IWizard#addPages()
	 */
	public void addPages() {
		super.addPages();
		_page = new NewMethodWizardPage(_parent.getType().getJavaProject());
		try {
			_page.setInterface(_parent.isInterface());
		} catch (JavaModelException e) {
			e.printStackTrace();
		}
		addPage(_page);
	}

	public boolean canFinish() {
		return _page.isCompleted();
	}
	
	/**
	 * @see edu.buffalo.cse.green.dialogs.wizards.GreenWizard#doFinish()
	 */
	public boolean doFinish() {
		boolean result = super.doFinish();
		if (_page.getErrorMessage() != null) {
			result = false;
		}
		
		if (result) {
			_methodName = _page.getName();
			_javadocComment = _page.getComment();
			_modifiers = _page.getModifiers();
			_returnTypeName = _page.getTypeName();
			_parameters = _page.getParameters();
			_forceImports = _page.forceImports();
		}
		
		return result;
	}

	/**
	 * @return The name of the method.
	 */
	public String getMethodName() {
		return _methodName;
	}
	
	/**
	 * @return the JavaDoc comment of the field.
	 */
	public String getJavaDocComment() {
		return _javadocComment;
	}

	/**
	 * @return The modifiers.
	 */
	public String getModifiers() {
		String modifiers = _modifiers.toString();
		modifiers = modifiers.replaceAll(",", "");
		return modifiers.substring(1, modifiers.length() - 1); 
	}

	/**
	 * @return The name of the return type.
	 */
	public String getReturnTypeName() {
		return _returnTypeName;
	}

	/**
	 * @return The parameters in the method signature
	 */
	public String getParameters() {
		return _parameters;
	}
	
	public boolean forceImports() {
		return _forceImports;
	}
	
	/**
	 * The page for <code>NewMethodWizard</code>
	 * 
	 * @author bcmartin
	 */
	class NewMethodWizardPage extends NewMemberSignatureWizardPage {
		public NewMethodWizardPage(IJavaProject project) {
			super("NewMethodWizardPage");

			setTitle(WIZARD_ADD_METHOD_TITLE);
			setDescription(WIZARD_ADD_METHOD_MESSAGE);

		}

		/**
		 * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
		 */
		public void createControl(Composite parent) {
			super.createControl(parent);
			createMethodParameterControls();
			setTypeLabel(WIZARD_ADD_METHOD_TYPE);
			setErrorMessage("Invalid name");
//
			getWizard().getContainer().updateMessage();
			getWizard().getContainer().updateButtons();
		}

		/**
		 * @see edu.buffalo.cse.green.dialogs.NewMemberSignatureWizardPage#getSettings()
		 */
		protected NewElementWizardSettings getSettings() {
			return isInterface() ? InterfaceMethodSettings
					: ClassMethodSettings;
		}

		/**
		 * @see edu.buffalo.cse.green.dialogs.NewMemberSignatureWizardPage#allowVoidType()
		 */
		protected boolean allowVoidType() {
			return true;
		}
		
		/**
		 * @see edu.buffalo.cse.green.dialogs.NewMemberSignatureWizardPage#isField()
		 */
		protected boolean isField() {
			//Used in determining if default veriable name will use preferred
			//field prefix.
			return false;
		}
	}

	/**
	 * @see edu.buffalo.cse.green.dialogs.wizards.NewElementWizard#canRunForked()
	 */
	protected boolean canRunForked() {
		return true;
	}
}
