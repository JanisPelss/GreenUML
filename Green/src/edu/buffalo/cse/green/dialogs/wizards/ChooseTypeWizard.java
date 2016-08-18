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

import java.util.MissingResourceException;

import org.eclipse.jdt.core.Flags;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.ITypeHierarchy;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.ui.JavaElementLabelProvider;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.IColorProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

import edu.buffalo.cse.green.GreenException;
import edu.buffalo.cse.green.PlugIn;

/**
 * A wizard that allows the user to choose a type from a list of types.
 * 
 * @author rjtruban
 */
public class ChooseTypeWizard extends GreenWizard {
	private static final String ERROR_CONCRETE_CLASS =
		"The selected type must be a concrete class";

	private static final String ERROR_SELECT_TYPE =
		"You must select a concrete type to instantiate";

	protected ChooseTypeLabelProvider _labelProvider;

	protected IType _originalType;

	protected ITypeHierarchy _originalTypeHierarchy;

	protected IType _selectedType;

	public ChooseTypeWizard(IType originalType) {
		_originalType = originalType;
		try {
			_originalTypeHierarchy = _originalType.newTypeHierarchy(PlugIn
					.getEmptyProgressMonitor());
		} catch (JavaModelException e) {
			e.printStackTrace();
			throw new MissingResourceException("Can't load type hierarchy.",
					_originalType.getFullyQualifiedName(), "TypeHierarchy");
		}
	}

	/**
	 * @see edu.buffalo.cse.green.dialogs.wizards.GreenWizard#doFinish()
	 */
	public boolean doFinish() {
		try {
			if (_selectedType != null) {
				int flags = _selectedType.getFlags();
				
				if (Flags.isAbstract(flags) || Flags.isInterface(flags)) {
					GreenException.illegalOperation(ERROR_CONCRETE_CLASS);
				} else {
					return true;
				}
			} else {
				GreenException.illegalOperation(ERROR_SELECT_TYPE);
			}
		} catch (JavaModelException e) {
			e.printStackTrace();
		}
		
		return false;
	}

	/**
	 * @see org.eclipse.jface.wizard.IWizard#addPages()
	 */
	public void addPages() {
		super.addPages();
		ChooseTypeWizardPage page = new ChooseTypeWizardPage();
		page.setMessage("The type \"" + _originalType.getElementName()
				+ "\" cannot be used directly.");
		addPage(page);
	}

	/**
	 * The page for the <code>ChooseTypeWizard</code>.
	 * 
	 * @author bcmartin
	 */
	protected class ChooseTypeWizardPage extends WizardPage {
		private static final String DIALOG_TITLE = "Choose Concrete Type";

		protected ChooseTypeWizardPage() {
			super(DIALOG_TITLE);
			setTitle(DIALOG_TITLE);
			setWindowTitle(DIALOG_TITLE);
			setMessage("The type cannot be used directly.");
		}

		/**
		 * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
		 */
		public void createControl(Composite parent) {
			Composite composite = new Composite(parent, SWT.NONE);
			setControl(composite);
			composite.setLayout(new GridLayout());
			composite.setLayoutData(new GridData());

			addTreeComponent(composite);
			Dialog.applyDialogFont(composite);
		}

		/**
		 * Tree-viewer that shows the allowable types in a tree view. Adapted
		 * from
		 * {@link org.eclipse.jdt.internal.ui.refactoring.ChangeTypeWizard$ChangeTypeInputPage}
		 */
		private void addTreeComponent(Composite parent) {
			TreeViewer fTreeViewer = new TreeViewer(parent, SWT.SINGLE
					| SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
			GridData gd = new GridData(GridData.FILL_BOTH);
			gd.grabExcessHorizontalSpace = true;
			gd.grabExcessVerticalSpace = true;
			GC gc = null;
			try {
				gc = new GC(parent);
				gc.setFont(gc.getFont());
				gd.heightHint = Dialog.convertHeightInCharsToPixels(gc
						.getFontMetrics(), 6); // 6 characters tall
			} finally {
				if (gc != null) {
					gc.dispose();
					gc = null;
				}
			}
			fTreeViewer.getTree().setLayoutData(gd);

			fTreeViewer.setContentProvider(new ChooseTypeContentProvider());
			_labelProvider = new ChooseTypeLabelProvider();
			fTreeViewer.setLabelProvider(_labelProvider);
			ISelectionChangedListener listener = new ISelectionChangedListener() {
				/**
				 * @see org.eclipse.jface.viewers.ISelectionChangedListener#selectionChanged(org.eclipse.jface.viewers.SelectionChangedEvent)
				 */
				public void selectionChanged(SelectionChangedEvent event) {
					IStructuredSelection selection = (IStructuredSelection) event
							.getSelection();
					typeSelected((IType) selection.getFirstElement());
				}
			};
			fTreeViewer.addSelectionChangedListener(listener);
			// Don't use _originalType directly, because the tree will attempt
			// to get its children infinitely if the original type's children
			// includes itself.
			fTreeViewer.setInput(new Object[] {
				_originalType });
			// How deep to get children (to prevent infinite loops)
			fTreeViewer.expandToLevel(10);
		}
	}

	/**
	 * Called when a type is selected.
	 *  
	 * @param selected - The selected type.
	 */
	protected void typeSelected(IType selected) {
		_selectedType = selected;
	}

	/**
	 * @return The selected type.
	 */
	public IType getSelectedType() {
		return _selectedType;
	}

	/**
	 * Content provider for <code>ChooseTypeWizardPage</code>.
	 * 
	 * @author bcmartin
	 */
	class ChooseTypeContentProvider implements ITreeContentProvider {
		/**
		 * @see org.eclipse.jface.viewers.ITreeContentProvider#getChildren(java.lang.Object)
		 */
		public Object[] getChildren(Object parentElement) {
			return parentElement instanceof IType ? _originalTypeHierarchy
					.getSubtypes((IType) parentElement) : null;
		}

		/**
		 * @see org.eclipse.jface.viewers.ITreeContentProvider#getParent(java.lang.Object)
		 */
		public Object getParent(Object element) {
			return element instanceof IType ? ((IType) element).getParent()
					.getAncestor(IJavaElement.TYPE) : null;
		}

		/**
		 * @see org.eclipse.jface.viewers.ITreeContentProvider#hasChildren(java.lang.Object)
		 */
		public boolean hasChildren(Object element) {
			return getChildren(element).length != 0;
		}

		/**
		 * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java.lang.Object)
		 */
		public Object[] getElements(Object inputElement) {
			if (!(inputElement instanceof Object[])) {
				GreenException.illegalOperation(
						"Root element of tree not original element.");
			} else {
				Object[] input = (Object[]) inputElement;
				if (input.length == 0 || !(input[0] == _originalType)) {
					GreenException.illegalOperation(
							"Root element of tree not original element.");
				}
				
				return new Object[] { _originalType };
			}
			
			return null;
		}

		/**
		 * @see org.eclipse.jface.viewers.IContentProvider#dispose()
		 */
		public void dispose() {
		}

		/**
		 * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer, java.lang.Object, java.lang.Object)
		 */
		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		}
	}

	/**
	 * A label provider for <code>ChooseTypeWizardPage</code>.
	 * 
	 * @author bcmartin
	 */
	protected class ChooseTypeLabelProvider extends JavaElementLabelProvider
			implements IColorProvider {
		/**
		 * @see org.eclipse.jface.viewers.IColorProvider#getForeground(java.lang.Object)
		 */
		public Color getForeground(Object element) {
			return null;
		}

		/**
		 * @see org.eclipse.jface.viewers.IColorProvider#getBackground(java.lang.Object)
		 */
		public Color getBackground(Object element) {
			return null;
		}
	}
}
