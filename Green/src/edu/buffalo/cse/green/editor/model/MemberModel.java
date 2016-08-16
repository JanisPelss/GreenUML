/* This file is part of Green.
 *
 * Copyright (C) 2005 The Research Foundation of State University of New York
 * All Rights Under Copyright Reserved, The Research Foundation of S.U.N.Y.
 * 
 * Green is free software, licensed under the terms of the Eclipse
 * Public License, version 1.0.  The license is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package edu.buffalo.cse.green.editor.model;

import static edu.buffalo.cse.green.editor.controller.PropertyChange.Element;
import static org.eclipse.core.resources.IMarker.SEVERITY_INFO;
import static org.eclipse.core.resources.IMarker.SEVERITY_WARNING;
import static org.eclipse.core.resources.IResource.DEPTH_ZERO;
import static org.eclipse.jdt.core.Flags.AccDefault;
import static org.eclipse.jdt.core.Flags.AccPrivate;
import static org.eclipse.jdt.core.Flags.AccProtected;
import static org.eclipse.jdt.core.Flags.AccPublic;
import static org.eclipse.jdt.core.IJavaModelMarker.JAVA_MODEL_PROBLEM_MARKER;
import static org.eclipse.jdt.ui.JavaElementImageDescriptor.ERROR;
import static org.eclipse.jdt.ui.JavaElementImageDescriptor.WARNING;
//TODO Call to element marked internal by JDT
import static org.eclipse.jdt.internal.ui.JavaPluginImages.DESC_OBJS_QUICK_FIX;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.internal.resources.ResourceException;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IJavaModelMarker;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.Signature;
import org.eclipse.jdt.ui.JavaElementImageDescriptor;
import org.eclipse.jdt.ui.JavaElementLabelProvider;
import org.eclipse.jdt.ui.refactoring.RenameSupport;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.ui.IMarkerResolution;
import org.eclipse.ui.ide.IDE;

import edu.buffalo.cse.green.editor.action.QuickFix;
import edu.buffalo.cse.green.editor.action.QuickFixAction;

/**
 * Superclass that provides functionality that is common to all
 * <code>IMember</code>s.
 * 
 * @author bcmartin
 */
public abstract class MemberModel<C extends AbstractModel,
P extends AbstractModel, E extends IMember>
extends AbstractModel<C, P, E> {
	private static JavaElementLabelProvider ICON_PROVIDER =
		new JavaElementLabelProvider();
	private E _member = null;

	protected MemberModel(E element) {
		setMember(element);
	}

	/**
	 * @see edu.buffalo.cse.green.editor.model.AbstractModel#getJavaElement()
	 */
	public final E getJavaElement() {
		return _member;
	}
	
	/**
	 * @return the <code>IMember</code> represented by this model.
	 */
	public final E getMember() {
		return _member;
	}

	/**
	 * Sets the member that is represented by this model.
	 * 
	 * @param member - The member.
	 */
	public final void setMember(E member) {
		E oldMember = _member;
		_member = member;
		firePropertyChange(Element, oldMember, _member);
	}

	/**
	 * @return the visibility of this <code>IMember</code>
	 */
	protected String getVisibility() {
		try {
			int flags = _member.getFlags();

			if ((flags & AccPublic) == AccPublic) {
				return "public";
			} else if ((flags & AccProtected) == AccProtected) {
				return "protected";
			} else if ((flags & AccPrivate) == AccPrivate) {
				return "private";
			} else if ((flags & AccDefault) == AccDefault) { return ""; }
		} catch (JavaModelException e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * @see edu.buffalo.cse.green.editor.model.AbstractModel#removeFromParent()
	 */
	public void removeFromParent() {
		getParent().removeChild(this);
	}

	/**
	 * Gets the appropriate icon to represent the given <code>IMember</code>.
	 * 
	 * @param member - The <code>IMember</code> to represent.
	 * @return The icon.
	 */
	protected Image getImage(MemberModel<?, ?, E> model) {
		E member = model.getMember();
		
		ImageDescriptor id = ImageDescriptor.createFromImage(
				ICON_PROVIDER.getImage(member)); 
		int adornments = 0;
		
		IResource memberResource = member.getResource();
		
		if (memberResource != null && member instanceof IMember
				&& member.exists()) {
			try {
				IMarker[] errorMarkers = memberResource.findMarkers(
						IJavaModelMarker.JAVA_MODEL_PROBLEM_MARKER, false,
						IResource.DEPTH_ZERO);
				
				for (IMarker marker : errorMarkers) {
					// see if the error/warning is inside this member's scope
					int errStart = (Integer) marker.getAttribute("charStart");
					int errEnd = (Integer) marker.getAttribute("charEnd");
					int memStart = member.getSourceRange().getOffset();
					int memEnd = memStart + member.getSourceRange().getLength();
					if (memStart > errStart || memEnd < errEnd) continue;
					
					int severity = marker.getAttribute(
							IMarker.SEVERITY, SEVERITY_INFO);
					
					if (severity == IMarker.SEVERITY_ERROR) {
						adornments = ERROR;
						break;
					} else if (severity == SEVERITY_WARNING) {
						adornments = WARNING;
					}
				}
			} catch (ResourceException e) {
				// TODO Issue: when Java editor is open, stack trace
				// is printed during a refactoring, because we're trying
				// to access old model, not new model.  If Java editor
				// is not open, we're OK.
				// RETHROWING THIS EXCEPTION causes a cascade of THROWS clauses being required...
				// throw e;
				System.err.println("[GREEN] Resource exception was thrown - probably OK");
			}
			catch (CoreException e) {
				e.printStackTrace();
			}
		}
		
		return new JavaElementImageDescriptor(id, adornments,
				new Point(id.getImageData().width,
						id.getImageData().height)).createImage();
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return getDisplayName();
	}
	
	/**
	 * @see edu.buffalo.cse.green.editor.model.AbstractModel#handleDispose()
	 */
	public void handleDispose() {
		// do nothing
	}

	/**
	 * @return The image representing the type.
	 */
	public Image getIcon() {
		return getImage(this);
	}
	
	/**
	 * @return The caption to display on the label in the diagram.
	 */
	public abstract String getDisplayName();
	
	/**
	 * @param signature - The type signature.
	 * @param fqn - If true, fully-qualified names should be used.
	 * @return The string representing the member's signature.
	 */
	protected static final String getSignatureName(
			String signature, boolean fqn) {
		String sig = Signature.toString(signature);
		
		if (!fqn) {
			sig = sig.substring(sig.lastIndexOf('.') + 1);
		}
		
		return sig;
	}
	
	/**
	 * @see edu.buffalo.cse.green.editor.model.AbstractModel#getTypeModel()
	 */
	public TypeModel getTypeModel() {
		AbstractModel<?,?,?> parent = getParent();
		return parent.getTypeModel();
	}

	/**
	 * @return A <code>RenameSupport</code> for use by
	 * <code>RefactorRenameAction</code> 
	 * @throws CoreException
	 */
	public abstract RenameSupport getRenameSupport() throws CoreException;

//	/**
//	 * @see java.lang.Object#equals(java.lang.Object)
//	 */
//	public final boolean equals(Object o) {
//		System.err.println(o + ":" + this);
//		
//		if (!(o instanceof MemberModel)) return false;
//
//		MemberModel model = (MemberModel) o;
//		return getMember().getHandleIdentifier().equals(
//				model.getMember().getHandleIdentifier());
//	}
	
	/**
	 * Appends quick fix actions for this element to the given menu.
	 * 
	 * @param menu - The target menu.
	 */
	public void appendQuickFixActionsToMenu(MenuManager menu) {
		List<QuickFix> fixes = getQuickFixes();
		
		if (fixes.size() > 0) {
			MenuManager qf = new MenuManager("Quick Fixes", DESC_OBJS_QUICK_FIX, null);
			qf.setVisible(true);
			menu.add(new Separator());
			menu.add(qf);
			
			for (QuickFix fix : fixes) {
				qf.add(new QuickFixAction(fix));
			}
		}
	}

	/**
	 * Finds the quick fixes corresponding to this model.
	 */
	private List<QuickFix> getQuickFixes() {
		boolean isBinary = getMember().isBinary();
		
		if (!isBinary) {
			try {
				IResource resource = getMember().getUnderlyingResource();
				IMarker[] errorMarkers = resource.findMarkers(
						JAVA_MODEL_PROBLEM_MARKER, false, DEPTH_ZERO);
				
				for (IMarker marker : errorMarkers) {
					// see if the error/warning is inside this member's scope
					int errStart = (Integer) marker.getAttribute("charStart");
					int errEnd = (Integer) marker.getAttribute("charEnd");
					int memStart = getMember().getSourceRange().getOffset();
					int memEnd =
						memStart + getMember().getSourceRange().getLength();
					if (memStart > errStart || memEnd < errEnd) continue;

					return getQuickFix(marker);
				}
			} catch (CoreException e) {
				// do nothing
			}
		}

		return new ArrayList<QuickFix>();
	}


	/**
	 * Finds the quick fixes corresponding to the given error markers and
	 * displays a dialog for the user to select the desired action to take.
	 * 
	 * @param errorMarkers - The given <code>IMarker</code>s.
	 */
	private List<QuickFix> getQuickFix(IMarker errorMarker) {
		List<QuickFix> fixes = new ArrayList<QuickFix>();
		
		for (IMarkerResolution resolution
				: IDE.getMarkerHelpRegistry().getResolutions(errorMarker)) {
			fixes.add(new QuickFix(errorMarker, resolution));
		}
		
		return fixes;
	}
}
