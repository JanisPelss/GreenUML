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
//
//import java.util.List;
//
//import org.eclipse.jface.dialogs.Dialog;
//import org.eclipse.swt.SWT;
//import org.eclipse.swt.layout.GridData;
//import org.eclipse.swt.layout.GridLayout;
//import org.eclipse.swt.widgets.Button;
//import org.eclipse.swt.widgets.Composite;
//import org.eclipse.swt.widgets.Control;
//import org.eclipse.swt.widgets.Group;
//import org.eclipse.swt.widgets.Shell;
//
//import edu.buffalo.cse.green.editor.DiagramEditor;
//import edu.buffalo.cse.green.editor.model.AbstractModel;
//import edu.buffalo.cse.green.editor.model.TypeModel;
//
//
///**
// * Dialog box for choosing the visibility of fields and methods
// * 
// * @author <a href="mailto:zgwang@sourceforge.net">Gene Wang</a>
// *
// */
//public class CompartmentVisibilityDialog extends Dialog implements OKCancelListener {
//
//	/**
//	 * Checkbox for showing of fields within a class box
//	 */
//	private Button _fieldButton;
//	
//	/**
//	 * Checkbox for showing of methods within a class box
//	 */
//	private Button _methodButton;
//	
//	/**
//	 * True if fields are shown in the open editors
//	 */
//	private static boolean _showFields;
//	
//	/**
//	 * True if methods are shown in the open editors
//	 */
//	private static boolean _showMethods;
//	
//	static {
//		_showFields = true;
//		_showMethods = true;
//	}
//	
//	
//	/**
//	 * The constructor.
//	 * @param shell - The "window" of the dialog.
//	 * @param title - Title of the dialog box.
//	 */
//	public CompartmentVisibilityDialog(Shell shell) {
//		super(shell);
//		create();
//		getShell().setText("Change Field and Method Visibility");
//		getShell().setSize(300, 150);
//	}
//
//	/**
//	 * @see org.eclipse.jface.dialogs.Dialog#createContents(org.eclipse.swt.widgets.Composite)
//	 */
//	@Override
//	protected Control createContents(Composite parent) {
//		Composite selectionComposite = new Composite(parent, 0);
//		selectionComposite.setLayout(new GridLayout(1, false));
//		selectionComposite.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL | 
//												 GridData.VERTICAL_ALIGN_FILL| 
//												 GridData.GRAB_HORIZONTAL | 
//												 GridData.GRAB_VERTICAL));
//		
//		Group buttonGroup = new Group(parent,SWT.SHADOW_ETCHED_IN);
//		buttonGroup.setLayout(new GridLayout(1, false));
//		buttonGroup.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL | 
//				 GridData.VERTICAL_ALIGN_FILL| 
//				 GridData.GRAB_HORIZONTAL | 
//				 GridData.GRAB_VERTICAL));
//		
//		_fieldButton = new Button(buttonGroup, SWT.CHECK);
//		_fieldButton.setText("Show fields in classboxes");
//		_fieldButton.setSelection(_showFields);
//		_methodButton = new Button(buttonGroup, SWT.CHECK);
//		_methodButton.setText("Show methods in classboxes");
//		_methodButton.setSelection(_showMethods);
//		
//		Composite buttonComposite = new OKCancelComposite(this, parent, true);
//		buttonComposite.setLayout(new GridLayout(3, true));
//		
//		// prepare the view
//		parent.pack();
//		return parent;
//	}
//
//	/**
//	 * Updates the text and closes the dialog with an OK return code.
//	 * Called when the OK button is pressed. 
//	 */
//	@Override
//	public void okPressed() {
//		applyPressed();
//		setReturnCode(OK);
//		close();
//	}
//	
//	/**
//	 * @see org.eclipse.jface.dialogs.Dialog#cancelPressed()
//	 */
//	@Override
//	public void cancelPressed() {
//		super.cancelPressed();
//	}
//	
//	/**
//	 * Updates the visibility as specified in the checkboxes
//	 * 
//	 * @see edu.buffalo.cse.green.dialogs.OKApplyCancelListener#applyPressed()
//	 */
//	public void applyPressed() {
//		_showFields = _fieldButton.getSelection();
//		_showMethods = _methodButton.getSelection();
//		
//		for(DiagramEditor editor : DiagramEditor.getEditors()) {
//			List<AbstractModel> allModels = editor.getRootModel().getChildren();
//			for(AbstractModel m : allModels) {
//				if(m instanceof TypeModel) {
//					((TypeModel) m).setCompartmentVisibility(_showFields, _showMethods);
//				}
//			}
//		}
//	}
//	
//	public boolean showFields() { return _showFields; }
//
//	public boolean showMethods() { return _showMethods; }
//
//}
