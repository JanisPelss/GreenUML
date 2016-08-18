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

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import edu.buffalo.cse.green.editor.model.filters.MemberFilter;
import edu.buffalo.cse.green.editor.model.filters.MemberVisibility;

/**
 * Provides the user with the ability to create and enable/disable filters used
 * in Green. Filters can be used to remove members with different visibilities
 * and support pattern matching in the members' names.
 * 
 * Pattern matching is done using the Pattern class as provided by Java.
 * @see <code>java.util.regex.Pattern</code>
 * 
 * @author <a href="mailto:zgwang@sourceforge.net">Gene Wang</a>
 */
public class EditFiltersDialog extends Dialog implements OKCancelListener {
    
	private Combo _conditionCombo;
	
	private Text _patternText;
	
	private Combo _elementTypeCombo;
	
	private ArrayList<FilterableCategory> _filterableCats;
	
	private VisibilityComposite _visHolder;
		
	/**
	 *   Filter to be edited, null state is kept if adding new filter
	 */
	private MemberFilter _editingFilter = null;
	
	/**
	 *  New filter to be added / changed into
	 */
	private MemberFilter _newFilter;
	
	/**
	 * @param shell the shell
	 * @param filter the filter for editing, null if adding a new filter
	 */
	public EditFiltersDialog(Shell shell, MemberFilter filter) {
		super(shell);
        _editingFilter = filter;

        create();
		getShell().setText("Add/edit Filter");
	}
	
	/**
	 * Adds a filter to the list using the values in the dialog.
	 */
	boolean createFilter() {
	    boolean types   = false;
	    boolean fields  = false;
	    boolean methods = false;
	    int selection = _elementTypeCombo.getSelectionIndex();
	    //0: MEMBERS
        //1: FIELDS
        //2: METHODS

		int enabled = 2 + _conditionCombo.getSelectionIndex();

		types = false;
		
		switch (selection) {
		    case 0: //Both selected
	            fields = true;
	            methods = true;
	            break;
		    case 1: //Fields selected
                fields = true;
                methods = false;
		        break;
		    case 2: //Methods selected
                fields = false;
                methods = true;
		        break;
		}
		
		int visibility = _visHolder.getValue().intValue();
		String name = _patternText.getText();
		
		//Determine if the syntax of the supplied regular expression is valid
		try {
		    Pattern.matches(name, "Arbitrary string to compare");
		} catch (PatternSyntaxException pse) {
			MessageDialog.openInformation(getShell(), "Invalid Pattern",
					"The supplied pattern is not a regular expression.");
			return false;
		}

	    _newFilter = new MemberFilter(enabled, types, fields, methods,
                MemberVisibility.makeVisibility(visibility), name);
		return true;
	}

	/**
	 * @see org.eclipse.jface.dialogs.Dialog#createContents(org.eclipse.swt.widgets.Composite)
	 */
	protected Control createContents(Composite parent) {
	    parent.setLayout(new GridLayout(1, false));
	    
		Composite elementTypeComposite = new Composite(parent, 0); //Type of members
        Composite ACMComposite = new Composite(parent, 0); // Access Control Modifiers
        Composite nameComposite = new Composite(parent, 0);
        Composite setFiltersComposite = new Composite(parent, 0);

        elementTypeComposite.setLayout(new GridLayout(4, false));
		ACMComposite.setLayout(new GridLayout(2, false));
		nameComposite.setLayout(new GridLayout(3, false));
		setFiltersComposite.setLayout(new GridLayout(2, true));

		nameComposite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

		Composite buttonComposite = new OKCancelComposite(this, parent, false);
        buttonComposite.setLayout(new GridLayout(3, true));
		
        
		Label filterText = new Label(elementTypeComposite, 0);
		filterText.setText("Filter all");
		_elementTypeCombo = new Combo(elementTypeComposite, SWT.READ_ONLY);
		createFilterableCategories();
		
		_conditionCombo = new Combo(elementTypeComposite, SWT.READ_ONLY);

		Label visText = new Label(ACMComposite, 0);
		visText.setText("Visibility:");
		_visHolder = new VisibilityComposite(ACMComposite, 0, true); 
		
		Label nameText = new Label(nameComposite, 0);
		nameText.setText("Name Pattern:");
		_patternText = new Text(nameComposite, SWT.SINGLE | SWT.BORDER);
		_patternText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		
        if(_editingFilter == null) {
            resetOptions();
        }
        else {
            setFilterData();
        }

		// prepare the view
		parent.pack();
		return parent;
	}
	
	/**
     * Creates the categories that are allowed to be filtered
     */
    private void createFilterableCategories() {
        _filterableCats = new ArrayList<FilterableCategory>();
        FilterableCategory.clearAll();
        _filterableCats.add(new FilterableCategory("both"));
        _filterableCats.add(new FilterableCategory("fields"));
        _filterableCats.add(new FilterableCategory("methods"));
    }

    /**
     * Sets the controls in this dialog to values in the given filter.
     */
    private void setFilterData() {
        String filterData = _editingFilter.toString();
        /*
         *[Char index] [Meaning]
         *   0th         0 for Inclusion (and off) 
         *              +2 for on
         *              +1 for Exclusion
         *
         *   1st        Types filtered    - 1 for yes, 0 for no
         *   2nd        Fields filtered   - 1 for yes, 0 for no
         *   3rd        Methods filtered  - 1 for yes, 0 for no
         *   4th        Visibility 
         *                  3 - any
         *                  0 - default
         *                  1 - public
         *                  2 - private
         *                  4 - protected
         *  Rest        Name pattern 
         */
        
        _conditionCombo.removeAll();
        _conditionCombo.add("that meet the following conditions: ");
        _conditionCombo.add("that do not meet the following conditions: ");
        int condition = Integer.parseInt(filterData.substring(0,1));
        if(condition > 2 ) {
            _conditionCombo.select(condition - 2);
        }
        else {
            _conditionCombo.select(0);
        }
        
        _elementTypeCombo.removeAll();
        
        for(FilterableCategory fc : _filterableCats) {
            _elementTypeCombo.add(fc.getName(), fc.getIndex());
        }
        
        int fields = Integer.parseInt(filterData.substring(2,3));
        int methods = Integer.parseInt(filterData.substring(3,4));

        if(fields == 1) {
            if(methods == 1) {
                _elementTypeCombo.select(0);
            }
            else {
                _elementTypeCombo.select(1);
            }
        }
        else {
            if(methods == 1) {
                _elementTypeCombo.select(2);
            }
            else {
                _elementTypeCombo.select(0);
            }
        }
        
        int vis = Integer.parseInt(filterData.substring(4,5));
        _visHolder.setDefaultSelected(false);
        _visHolder.setPublicSelected(false);
        _visHolder.setPrivateSelected(false);
        _visHolder.setProtectedSelected(false);

        switch (vis) {
            case 0:
                _visHolder.setDefaultSelected(true);
                break;
            case 1:
                _visHolder.setPublicSelected(true);
                break;
            case 2:
                _visHolder.setPrivateSelected(true);
                break;
            case 3:
                //Can't assign to any.
                break;
            case 4:
                _visHolder.setProtectedSelected(true);
                break;
        }
        
        _patternText.setText(filterData.substring(5));
    }
    
    /**
     * @return the changed filter if it exists, null otherwise
     */
    public MemberFilter getFilter() {
        return _newFilter;
    }

    /**
	 * Resets the controls in this dialog to their defaults.
	 */
	private void resetOptions() {
	    _conditionCombo.removeAll();
		_conditionCombo.add("that meet the following conditions: ");
		_conditionCombo.add("that do not meet the following conditions: ");
		_conditionCombo.select(0);
		
		_elementTypeCombo.removeAll();
		
		for(FilterableCategory fc : _filterableCats) {
		    _elementTypeCombo.add(fc.getName(), fc.getIndex());
		}
        _elementTypeCombo.select(0);
		
		_patternText.setText(".*");
		
		_visHolder.reset();
	}

	/**
	 * @see org.eclipse.jface.dialogs.Dialog#okPressed()
	 */
	public void okPressed() {
		if(createFilter()) {
    		setReturnCode(OK);
            close();
		}
	}
	
	/**
	 * @see org.eclipse.jface.dialogs.Dialog#cancelPressed()
	 */
	public void cancelPressed() {
		super.cancelPressed();
	}

    /* (non-Javadoc)
     * @see edu.buffalo.cse.green.dialogs.OKCancelListener#applyPressed()
     */
    public void applyPressed() {
        //No apply button present.
    }
	
	
	
}

/**
 * A pseudo-maplike class for matching types of filters with the combobox
 * in the Filters Dialog
 * 
 * @author <a href="mailto:zgwang@sourceforge.net">Gene Wang</a>
 *
 */
class FilterableCategory {

    /**
     * Contains the next index for the combobox, singleton'd to make sure
     * that no two types use the same index. 
     */
    public static int INDEX_SINGLETON = 0;
    
    private String _name;
    
    private int _absoluteIndex;
    
    /**
     * @param name name of this type
     */
    public FilterableCategory(String name) {
        _name = name;
        _absoluteIndex = INDEX_SINGLETON;
        INDEX_SINGLETON++;
    }
    
    
    /**
     * @return the name of the category
     */
    public String getName() {
        return _name;
    }
    
    /**
     * @return the index to be used in the table
     */
    public int getIndex() {
        return _absoluteIndex;
    }
    
    public static void clearAll() {
        INDEX_SINGLETON = 0;
    }
}

/**
 * Holds common controls for selecting from the different visibilities.
 *
 * @author bcmartin
 */
class VisibilityComposite extends Composite {
	MemberVisibility _value = MemberVisibility.PUBLIC;
	private Button _publicBut;
	private Button _defaultBut;
	private Button _privateBut;
	private Button _protectedBut;
	private List<IVisibilityChangedListener> _listeners;
	
	public VisibilityComposite(Composite parent, int style, boolean showAny) {
		super(parent, SWT.FILL);
		_listeners = new ArrayList<IVisibilityChangedListener>();
		
		setLayout(new GridLayout(showAny ? 8 : 7, false));
		
		if (showAny) {
			Button anyBut = new Button(this, SWT.RADIO);
			anyBut.setText("any");
			anyBut.addSelectionListener(new SelectionListener() {
				public void widgetSelected(SelectionEvent e) {
					_value = MemberVisibility.ANY;
					notifyListeners();
				}

				public void widgetDefaultSelected(SelectionEvent e) {}
			});
		}

		_defaultBut = new Button(this, SWT.RADIO);
        _defaultBut.setText("default");
        _defaultBut.addSelectionListener(new SelectionListener() {
            public void widgetSelected(SelectionEvent e) {
                _value = MemberVisibility.DEFAULT;
                notifyListeners();
            }

            public void widgetDefaultSelected(SelectionEvent e) {}
        });

		_publicBut = new Button(this, SWT.RADIO);
		_publicBut.setText("public");
		_publicBut.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				_value = MemberVisibility.PUBLIC;
				notifyListeners();
			}

			public void widgetDefaultSelected(SelectionEvent e) {}
		});
		
		_privateBut = new Button(this, SWT.RADIO);
		_privateBut.setText("private");
		_privateBut.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				_value = MemberVisibility.PRIVATE;
				notifyListeners();
			}

			public void widgetDefaultSelected(SelectionEvent e) {}
		});
		
		_protectedBut = new Button(this, SWT.RADIO);
		_protectedBut.setText("protected");
		_protectedBut.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				_value = MemberVisibility.PROTECTED;
			}

			public void widgetDefaultSelected(SelectionEvent e) {}
		});
	}
	
	/**
	 * @return The selected <code>MemberVisibility</code>.
	 */
	public MemberVisibility getValue() {
		return _value;
	}
	
	/**
	 * Adds the given listener.
	 *
	 * @param listener - The given <code>IVisibilityChangedListener</code>.
	 */
	public void addListener(IVisibilityChangedListener listener) {
		_listeners.add(listener);
	}
	
	/**
	 * Removes the given listener.
	 *
	 * @param listener - The given <code>IVisibilityChangedListener</code>.
	 */
	public void removeListener(IVisibilityChangedListener listener) {
		_listeners.remove(listener);
	}
	
	/**
	 * Notifies all listeners that the selected visibility has changed.
	 */
	public void notifyListeners() {
		for (IVisibilityChangedListener listener : _listeners) {
			listener.visibilityChanged(_value);
		}
	}
	
	/**
	 * Sets the values of the controls to their defaults.
	 */
	public void reset() {
		_defaultBut.setSelection(false);
		_privateBut.setSelection(false);
		_protectedBut.setSelection(false);
		_publicBut.setSelection(true);
	}

	/**
	 * Sets whether the default button is enabled or not.
	 * 
	 * @param enable - The button is enabled if and only if this is true.
	 */
	public void setDefaultEnabled(boolean enable) {
		_defaultBut.setEnabled(enable);
	}

	/**
	 * Sets whether the private button is enabled or not.
	 * 
	 * @param enable - The button is enabled if and only if this is true.
	 */
	public void setPrivateEnabled(boolean enable) {
		_privateBut.setEnabled(enable);
	}

	/**
	 * Sets whether the protected button is enabled or not.
	 * 
	 * @param enable - The button is enabled if and only if this is true.
	 */
	public void setProtectedEnabled(boolean enable) {
		_protectedBut.setEnabled(enable);
	}

	/**
	 * Sets whether the public button is enabled or not.
	 * 
	 * @param enable - The button is enabled if and only if this is true.
	 */
	public void setPublicEnabled(boolean enable) {
		_publicBut.setEnabled(enable);
	}

	/**
	 * Sets whether the default button is selected or not.
	 * 
	 * @param select - The button is selected if and only if this is true.
	 */
	public void setDefaultSelected(boolean select) {
		_defaultBut.setSelection(select);
		_value = MemberVisibility.DEFAULT;
	}

	/**
	 * Sets whether the private button is selected or not.
	 * 
	 * @param select - The button is selected if and only if this is true.
	 */
	public void setPrivateSelected(boolean select) {
		_privateBut.setSelection(select);
		_value = MemberVisibility.PRIVATE;
	}

	/**
	 * Sets whether the protected button is selected or not.
	 * 
	 * @param select - The button is selected if and only if this is true.
	 */
	public void setProtectedSelected(boolean select) {
		_protectedBut.setSelection(select);
		_value = MemberVisibility.PROTECTED;
	}

	/**
	 * Sets whether the public button is selected or not.
	 * 
	 * @param select - The button is selected if and only if this is true.
	 */
	public void setPublicSelected(boolean select) {
		_publicBut.setSelection(select);
		_value = MemberVisibility.PUBLIC;
	}
}


/**
 * Provides a way to hook into visibility selections.
 * 
 * @author Blake
 */
interface IVisibilityChangedListener {
	void visibilityChanged(MemberVisibility value);
}