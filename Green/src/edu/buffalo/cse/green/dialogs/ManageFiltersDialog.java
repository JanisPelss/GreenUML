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

import static edu.buffalo.cse.green.preferences.PreferenceInitializer.P_FILTERS_MEMBER;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;

import edu.buffalo.cse.green.PlugIn;
import edu.buffalo.cse.green.editor.DiagramEditor;
import edu.buffalo.cse.green.editor.model.filters.MemberFilter;

/**
 * Provides the user with the ability to create and enable/disable filters used
 * in Green. Filters can be used to remove members with different visibilities
 * and support pattern matching in the members' names.
 * 
 * @author bcmartin
 * @author <a href="mailto:zgwang@sourceforge.net">Gene Wang</a>
 *
 */
public class ManageFiltersDialog extends Dialog implements OKCancelListener {
    
    private Composite _buttonComposite;

    Table _filterTable;
    
	Button _addFilterButton;
    
	Button _editFilterButton;
	
	Button _removeFiltersButton;
	
	public ManageFiltersDialog(Shell shell) {
		super(shell);
		create();
		getShell().setText("Manage Filters");
		getShell().setSize(450, 300);
	}
	
	/**
	 * @see org.eclipse.jface.dialogs.Dialog#createContents(org.eclipse.swt.widgets.Composite)
	 */
	protected Control createContents(Composite parent) {
	    parent.setLayout(new GridLayout(1, false));
	    
        Composite setFiltersComposite = new Composite(parent, 0);
		setFiltersComposite.setLayout(new GridLayout(3, true));
		
		_filterTable = new Table(parent, SWT.MULTI | SWT.CHECK | SWT.BORDER);
		_filterTable.setLayout(new GridLayout(1, false));
		_filterTable.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		
		_filterTable.addSelectionListener(new SelectionListener() {

            public void widgetDefaultSelected(SelectionEvent e) {
                //Do nothing
            }

            public void widgetSelected(SelectionEvent e) {
                if(_filterTable.getSelection().length == 1) {
                    _editFilterButton.setEnabled(true);
                }
                else {
                    _editFilterButton.setEnabled(false);
                }
                if(_filterTable.getSelection().length == 0) {
                    _removeFiltersButton.setEnabled(false);
                }
                else {
                    _removeFiltersButton.setEnabled(true);
                }
                tableChanged();
            }
		});
		
		_filterTable.addMouseListener(new MouseListener() {

		    public void mouseDoubleClick(MouseEvent e) {
                editSelectedFilter();
            }

            public void mouseDown(MouseEvent e) {
                //Do nothing
            }

            public void mouseUp(MouseEvent e) {
                //Do nothing
            }
		    
		});
		
		_buttonComposite = new OKCancelComposite(this, parent, true);
        _buttonComposite.setLayout(new GridLayout(3, true));
        _buttonComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
        ((OKCancelComposite) _buttonComposite).setApplyEnabled(false);
        
		_addFilterButton = new Button(setFiltersComposite, 0);
		_addFilterButton.setText("Add Filter");
		_addFilterButton.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		_addFilterButton.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
			    addNewFilter();
			}

			public void widgetDefaultSelected(SelectionEvent e) {
			    //Do nothing
			}
		});
		
        _editFilterButton = new Button(setFiltersComposite, 0);
        _editFilterButton.setText("Edit Filter");
        _editFilterButton.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        _editFilterButton.setEnabled(false);
        _editFilterButton.addSelectionListener(new SelectionListener() {

            public void widgetDefaultSelected(SelectionEvent e) {
                //Do nothing
            }

            public void widgetSelected(SelectionEvent e) {
                editSelectedFilter();
            }
        });

        _removeFiltersButton = new Button(setFiltersComposite, 0);
        _removeFiltersButton.setText("Remove Highlighted Filters");
        _removeFiltersButton.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        _removeFiltersButton.setEnabled(false);
        _removeFiltersButton.addSelectionListener(new SelectionListener() {
            public void widgetSelected(SelectionEvent e) {
                removeFilters();
                _removeFiltersButton.setEnabled(false);
                _editFilterButton.setEnabled(false);
            }

            public void widgetDefaultSelected(SelectionEvent e) {
                //Do nothing
            }
        });
		
		for (MemberFilter filter : PlugIn.getMemberFilters()) {
			addFilter(filter);
		}
		
		return parent;
	}
	
    void addNewFilter() {
        //Assumes this is only called when filter table has a selection
        EditFiltersDialog d = new EditFiltersDialog(getShell(), null);
        d.setBlockOnOpen(true);
        if (d.open() == OK) {
            addFilter(d.getFilter());
        }
    }

    void editSelectedFilter() {
	    //Assumes this is only called when filter table has a SINGLE selection
        int selectionIndex = _filterTable.getSelectionIndex();
	    MemberFilter filter = (MemberFilter) _filterTable.getSelection()[0].getData();
	    EditFiltersDialog d = new EditFiltersDialog(getShell(), filter);
        d.setBlockOnOpen(true);
        if (d.open() == OK) {
    	    TableItem tableItem = _filterTable.getItem(selectionIndex);
    	    MemberFilter newFilter = d.getFilter();
            tableItem.setText(newFilter.getDescription());
            tableItem.setData(newFilter);
            tableItem.setChecked(newFilter.isEnabled());
            tableChanged();	    
        }
	}
	
	
	void tableChanged() {
	    ((OKCancelComposite) _buttonComposite).setApplyEnabled(true);
	}
	
	/**
     * Removes the selected filters from the filter list
     */
    void removeFilters() {
        _filterTable.remove(_filterTable.getSelectionIndices());
        tableChanged();
    }
    
    /**
     * Adds the given filter to the list of filters and resets the dialog's
     * settings to the defaults.
     * 
     * @param filter - The given <code>MemberFilter</code>.
     */
    void addFilter(MemberFilter filter) {
        TableItem tableItem = new TableItem(_filterTable, 0);
        tableItem.setText(filter.getDescription());
        tableItem.setData(filter);
        tableItem.setChecked(filter.isEnabled());
        tableChanged();
    }
	
	/**
	 * @see org.eclipse.jface.dialogs.Dialog#okPressed()
	 */
	public void okPressed() {
		applyPressed();
		close();
	}
	
	/**
	 * @see org.eclipse.jface.dialogs.Dialog#cancelPressed()
	 */
	public void cancelPressed() {
		super.cancelPressed();
	}

	/**
	 * @see edu.buffalo.cse.green.dialogs.OKCancelListener#applyPressed()
	 */
	public void applyPressed() {
	    StringBuffer buffer = new StringBuffer();

	    if (_filterTable.getItems().length > 0) {
	        for (TableItem item : _filterTable.getItems()) {
	            MemberFilter filter = (MemberFilter) item.getData();
	            
	            if (item.getChecked() != filter.isEnabled()) {
	                int val = filter.getEnabledValue();
              
	                if (item.getChecked()) {
	                    filter.setEnabled(val + 2);
	                } else {
	                    filter.setEnabled(val - 2);
	                }
	            }
          
	            buffer.append("|");
	            buffer.append(filter);
	        }
	    } else {
	        buffer.append("|");
	    }
  
	    PlugIn.getDefault().getPreferenceStore().putValue(P_FILTERS_MEMBER, buffer.toString().substring(1));
  
	    // refresh the editor
	    for (DiagramEditor editor : DiagramEditor.getEditors()) {
	        editor.refresh();
	    }
	    
	    ((OKCancelComposite) _buttonComposite).setApplyEnabled(false);
	}
}