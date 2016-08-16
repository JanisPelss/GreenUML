/* This file is part of Green.
 *
 * Copyright (C) 2005 The Research Foundation of State University of New York
 * All Rights Under Copyright Reserved, The Research Foundation of S.U.N.Y.
 * 
 * Green is free software, licensed under the terms of the Eclipse
 * Public License, version 1.0.  The license is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package edu.buffalo.cse.green.dialogs;

import static edu.buffalo.cse.green.GreenException.GRERR_RELATIONSHIP_NO_METHODS;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.ui.JavaElementLabelProvider;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;

import edu.buffalo.cse.green.GreenException;
import edu.buffalo.cse.green.editor.model.MethodModel;

public class ChooseMethodsDialog extends Dialog {
	private IType _type;
	private List<MethodDialogListener> _listeners =
		new ArrayList<MethodDialogListener>();
	
	public ChooseMethodsDialog(Shell shell, IType type) {
		super(shell);
		_type = type;
		create();
		
		getShell().setText("Choose Methods");
	}
	
	
	/**
	 * Adds the given listener.
	 * 
	 * @param listener - The given <code>MethodDialogListener</code>.
	 */
	public void addMethodDialogListener(MethodDialogListener listener) {
		_listeners.add(listener);
	}
	
	/**
	 * Removes the given listener.
	 * 
	 * @param listener - The given <code>MethodDialogListener</code>.
	 * @return true if successful, false otherwise.
	 */
	public boolean removeMethodDialogListener(MethodDialogListener listener) {
		return _listeners.remove(listener);
	}
	
	/**
	 * @see org.eclipse.jface.dialogs.Dialog#createContents(org.eclipse.swt.widgets.Composite)
	 */
	protected Control createContents(final Composite parent) {
		try {
			
			if (_type.getMethods().length == 0) {
//				for (MethodDialogListener listener : _listeners) {
//					listener.okPressed(new ArrayList<IMethod>());
					GreenException.errorDialog(GRERR_RELATIONSHIP_NO_METHODS);
//				}
			}

			GridLayout grid = new GridLayout(1, true);
			parent.setLayout(grid);
			
			Label label = new Label(parent, 0);
			label.setText("Select methods to create the relationship in:");
			//label.setEnabled(false);
			
			final Table displayedMethods = new Table(parent, SWT.CHECK | SWT.VIRTUAL | SWT.BORDER);
			displayedMethods.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.GRAB_HORIZONTAL));
			JavaElementLabelProvider icon = new JavaElementLabelProvider();
			
			for (IMethod method : _type.getMethods()) {
				TableItem methodItem =
					new TableItem(displayedMethods, SWT.CHECK);
				methodItem.setText(MethodModel.getMethodSignature(method));
				methodItem.setImage(icon.getImage(method));
				methodItem.setData(method);
			}
			
			Composite buttonHolder = new Composite(parent, SWT.NONE);
			buttonHolder.setLayout(new GridLayout(2, true));
			buttonHolder.setLayoutData(new GridData(GridData.END
					| GridData.HORIZONTAL_ALIGN_END));
			
			Button okButton = new Button(buttonHolder, 0);
			okButton.setText("        OK        ");
			okButton.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			okButton.addSelectionListener(new SelectionListener() {
				public void widgetSelected(SelectionEvent e) {
					List<IMethod> selectedMethods = new ArrayList<IMethod>();

					for (TableItem item : displayedMethods.getItems()) {
						if (item.getChecked()) {
							selectedMethods.add((IMethod) item.getData());
						}
					}

					for (MethodDialogListener listener : _listeners) {
						listener.okPressed(selectedMethods);
					}
					
					getShell().dispose();
				}

				public void widgetDefaultSelected(SelectionEvent e) {}
			});
			
			Button cancelButton = new Button(buttonHolder, 0);
			cancelButton.setText("Cancel");
			cancelButton.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			cancelButton.addSelectionListener(new SelectionListener() {
				public void widgetSelected(SelectionEvent e) {
					getShell().dispose();
				}
				
				public void widgetDefaultSelected(SelectionEvent e) {}
			});
			
			return parent;
		} catch (JavaModelException e) {
			e.printStackTrace();
		}
		
		return parent;
	}
}