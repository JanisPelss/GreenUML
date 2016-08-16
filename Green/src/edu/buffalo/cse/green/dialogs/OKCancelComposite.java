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

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

/**
 * A reusable composite for selecting either an OK or Cancel button.  
 * An Apply button can be enabled by passing in <code>true</code>
 * for the third parameter in the constructor.
 * 
 * @author bcmartin
 * @author zgwang
 */
public class OKCancelComposite extends Composite {
	
    private Button _ok;
    
    private Button _apply = null;
    
	/**
	 * Constructor
	 * @param listener - Listener for the OK, Cancel, [Apply] buttons.
	 * @param parent - The parent container of this container.
	 * @param showApply - Whether or not the Apply button should be shown.
	 */
	public OKCancelComposite(final OKCancelListener listener, Composite parent,
			boolean showApply) {
		super(parent, SWT.FILL);
		
		setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		
		if(!showApply) {
			//When hiding apply, used to center other two buttons
			Button dummyButton = new Button(this, 0);
			dummyButton.setVisible(false);
		}
		_ok = new Button(this, 0);
		_ok.setText("OK");
		_ok.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		_ok.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				listener.okPressed();
			}

			public void widgetDefaultSelected(SelectionEvent e) {}
		});
		
		 //Apply button is only generated if it is enabled by showApply in constructor
		if(showApply) {
			_apply = new Button(this, 0);
			_apply.setText("Apply");
			_apply.setEnabled(showApply);
			_apply.setVisible(showApply);
			_apply.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			_apply.addSelectionListener(new SelectionListener() {
				public void widgetSelected(SelectionEvent e) {
					listener.applyPressed();
				}
	
				public void widgetDefaultSelected(SelectionEvent e) {}
			});
		}

		Button cancelButton = new Button(this, 0);
		cancelButton.setText("Cancel");
		cancelButton.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		cancelButton.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				listener.cancelPressed();
			}

			public void widgetDefaultSelected(SelectionEvent e) {}
		});
	}
	
	/**
	 * @param enabled
	 */
	public void setOKEnabled(boolean enabled) {
	    _ok.setEnabled(enabled);
	}
	
	/**
	 * @param enabled
	 */
	public void setApplyEnabled(boolean enabled) {
	    if(_apply == null) {
	        return;
	    }
        _apply.setEnabled(enabled);
	}
}

/**
 * Provides a way to hook into the button press events of an
 * <code>OKCancelComposite</code>.
 * 
 * @author bcmartin
 */
interface OKCancelListener {
	/**
	 * Called when the Apply button is pressed.
	 */
	void applyPressed();
	
	/**
	 * Called when the Cancel button is pressed.
	 */
	void cancelPressed();

	/**
	 * Called when the OK button is pressed.
	 */
	void okPressed();
}
