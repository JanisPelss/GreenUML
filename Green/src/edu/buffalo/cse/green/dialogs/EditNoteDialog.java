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

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/**
 * Dialog box for editing notes in the diagram.
 * @author zgwang
 *
 */
public class EditNoteDialog extends Dialog implements OKCancelListener {

	/**
	 * Text field inside the dialog.
	 */
	private Text _noteTextField;
	
	/**
	 * The string representing the text.
	 */
	private String _text;
	
	/**
	 * The constructor.
	 * @param shell - The "window" of the dialog.
	 * @param title - Title of the dialog box.
	 * @param oldText - Previous text in the note.
	 */
	public EditNoteDialog(Shell shell, String title, String oldText) {
		super(shell);
		_text = oldText;
		create();
		getShell().setText(title);
		getShell().setSize(350, 250);
	}

	/**
	 * @see org.eclipse.jface.dialogs.Dialog#createContents(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	protected Control createContents(Composite parent) {
		Composite textComposite = new Composite(parent, 0);
		textComposite.setLayout(new GridLayout(1, false));
		textComposite.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL | 
												 GridData.VERTICAL_ALIGN_FILL| 
												 GridData.GRAB_HORIZONTAL | 
												 GridData.GRAB_VERTICAL));
		_noteTextField = new Text(textComposite, SWT.MULTI|SWT.BORDER|SWT.WRAP|SWT.V_SCROLL);
		_noteTextField.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL | 
												  GridData.VERTICAL_ALIGN_FILL | 
												  GridData.GRAB_HORIZONTAL |
												  GridData.GRAB_VERTICAL));
		_noteTextField.setText(_text);
		_noteTextField.selectAll();
		
		//May be needed in the future
		//_noteTextField.setTextLimit(int);
		
		Composite buttonComposite = new OKCancelComposite(this, parent, false);
		buttonComposite.setLayout(new GridLayout(4, true));
		
		// prepare the view
		parent.pack();
		return parent;
	}

	/**
	 * Returns the String representation of the text in the dialog.
	 * @return The text. 
	 */
	public String getValue() {
		return _text;
	}
	
	/**
	 * Updates the text and closes the dialog with an OK return code.
	 * Called when the OK button is pressed. 
	 */
	@Override
	public void okPressed() {
		_text = _noteTextField.getText();
		setReturnCode(OK);
		close();
	}
	
	/**
	 * @see org.eclipse.jface.dialogs.Dialog#cancelPressed()
	 */
	@Override
	public void cancelPressed() {
		super.cancelPressed();
	}
	
	/**
	 * @see edu.buffalo.cse.green.dialogs.OKApplyCancelListener#applyPressed()
	 */
	public void applyPressed() {
	}
}
