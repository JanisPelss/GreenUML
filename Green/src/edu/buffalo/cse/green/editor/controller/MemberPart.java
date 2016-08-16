/* This file is part of Green.
 *
 * Copyright (C) 2005 The Research Foundation of State University of New York
 * All Rights Under Copyright Reserved, The Research Foundation of S.U.N.Y.
 * 
 * Green is free software, licensed under the terms of the Eclipse
 * Public License, version 1.0.  The license is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package edu.buffalo.cse.green.editor.controller;

import static edu.buffalo.cse.green.editor.controller.PropertyChange.Element;
import static edu.buffalo.cse.green.preferences.PreferenceInitializer.P_COLOR_UML;
import static edu.buffalo.cse.green.preferences.PreferenceInitializer.P_DISPLAY_ELEMENT_TOOLTIPS;
import static org.eclipse.jdt.core.IJavaElement.CLASS_FILE;
import static org.eclipse.jdt.core.IJavaElement.COMPILATION_UNIT;

//import javax.swing.JToolTip;

import org.eclipse.draw2d.MouseEvent;
import org.eclipse.draw2d.MouseMotionListener;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.Javadoc;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;

import edu.buffalo.cse.green.PlugIn;
import edu.buffalo.cse.green.editor.DiagramEditor;
import edu.buffalo.cse.green.editor.model.AbstractModel;
import edu.buffalo.cse.green.editor.model.MemberModel;
import edu.buffalo.cse.green.editor.view.IIconHolder;

/**
 * The superclass of parts that correspond to <code>MemberModel</code>s.
 * 
 * @author bcmartin
 */
public abstract class MemberPart extends AbstractPart {
	
	/**
	 * @return The label for the part.
	 */
	protected abstract IIconHolder getNameLabel();

	/**
	 * Auxiliary method; makes reading easier. 
	 */
	private IIconHolder figure() {
		return (IIconHolder) getFigure();
	}
	
	/**
	 * @see edu.buffalo.cse.green.editor.controller.AbstractPart#addPropertyListeners()
	 */
	protected void addPropertyListeners() {
		addListener(Element, new VisualsUpdater());
	}
	
	/**
	 * @return The part's corresponding javadoc.
	 */
	private String getJavaDoc() {
		final IMember element = (IMember) model().getJavaElement();
		int type = element.isBinary() ? CLASS_FILE : COMPILATION_UNIT;
		
		CompilationUnit cu = getEditor().getCompilationUnit(element.getAncestor(type));
		
		//If no source code attached for given element
		if(cu == null) return "";
		
		JavadocGrabber grabber = getJavadocGrabber();
		grabber.setElement(model().getMember());
		cu.accept(grabber);
		
		return grabber.getJavadoc();
	}
	
	/**
	 * @return An instance of a <code>JavadocGrabber</code> appropriate to the
	 * current part.
	 */
	protected abstract JavadocGrabber getJavadocGrabber();
	
	/**
	 * @see org.eclipse.gef.EditPart#deactivate()
	 */
	public void deactivate() {
		deactivate(figure().shouldDisposeFont());
		getRootPart().unmapModelFromEditPart((AbstractModel)getModel());
	}
	
	/**
	 * Called when the part is destroyed. Optionally removes the icon.
	 * 
	 * @param removeIcon - If true, the icon will be destroyed; otherwise, it
	 * will not be destroyed.
	 */
	protected void deactivate(boolean removeIcon) {
		if (removeIcon) {
			// free up memory
			figure().getIcon().dispose();
		}
		
		super.deactivate();
	}
	
	/**
	 * @see org.eclipse.gef.editparts.AbstractEditPart#refreshVisuals()
	 */
	protected void refreshVisuals(IIconHolder figure) {
		updateFont();
		updateIcon(figure);
		updateLabel();
		updateVisibility();
	}
	
	/**
	 * Updates the visibility of this member as appropriate.
	 */
	private void updateVisibility() {
		try {
			// hide the model if it is filtered out
			if (getEditor().isFiltered(model().getMember())) {
				model().setVisible(false);
			}
		} catch (JavaModelException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Called to update the font.
	 */
	public abstract void updateFont();
	
	/**
	 * Called to update the icon.
	 * 
	 * @param figure - The holder within the child part which contains the icon.
	 */
	protected void updateIcon(IIconHolder figure){
		final Image image = model().getIcon();
		final Image oldImage = figure.getIcon();
		final IIconHolder figure1 = figure;
		
		if (Display.getCurrent() !=null){
			figure.setIcon(image);
			if (oldImage !=null){
				oldImage.dispose();
			}
		} else {
			Display.getDefault().asyncExec(new Runnable(){
				/**
				 * @see java.lang.Runnable#run()
				 */
				public void run() {
					figure1.setIcon(image);
					if (oldImage != null){
						oldImage.dispose();
					}
				}
			});
		}
	}
	
	/**
	 * Called to update the label.
	 */
	public abstract void updateLabel();

	/**
	 * Called to repaint the label's contents.
	 * 
	 * @param label - The label to repaint.
	 */
	protected void repaintLabel(final IIconHolder label) {
		if (Display.getCurrent() != null) {
			label.setText(model().getDisplayName());
			label.repaint();
		} else {
			Display.getDefault().asyncExec(new Runnable() {
				/**
				 * @see java.lang.Runnable#run()
				 */
				public void run() {
					label.setText(model().getDisplayName());
					label.repaint();
				}
			});
		}
	}
	
	/**
	 * Auxiliary method; makes reading easier. 
	 */
	private MemberModel model() {
		return (MemberModel) getModel();
	}

	/**
	 * @see edu.buffalo.cse.green.editor.controller.AbstractPart#setInitialBackgroundColor()
	 */
	public void setInitialBackgroundColor() {
		getFigure().setBackgroundColor(PlugIn.getColorPreference(P_COLOR_UML));
	}
	
	/**
	 * @see edu.buffalo.cse.green.editor.controller.AbstractPart#initialize()
	 */
	public void initialize() {
		getNameLabel().addMouseMotionListener(new MouseMotionListener() {
			/**
			 * @see org.eclipse.draw2d.MouseMotionListener#mouseDragged(org.eclipse.draw2d.MouseEvent)
			 */
			public void mouseDragged(MouseEvent me) {}
			
			/**
			 * @see org.eclipse.draw2d.MouseMotionListener#mouseEntered(org.eclipse.draw2d.MouseEvent)
			 */
			public void mouseEntered(MouseEvent me) {
				if (!PlugIn.getBooleanPreference(P_DISPLAY_ELEMENT_TOOLTIPS)) {
					return;
				}
				String doc = getJavaDoc().trim();
				doc = removeCommentMarks(doc);
				getViewer().getControl().setToolTipText(doc);
			}
			
			/**
			 * Removes comments, converts @ tags for JavaDoc, and 
			 * removes all HTML tags
			 * 
			 * @author zgwang
			 * @param doc the JavaDoc string
			 * @return the modified JavaDoc string
			 */
			private String removeCommentMarks(String doc) {
				doc = doc.replace("/**", "");
				doc = doc.replace("*/", "");
				doc = doc.replace("\n * ", "\n");
				doc = doc.replace("\n@author", "\n\nAuthor:\n");
				doc = doc.replace("\n@exception", "\n\nThrows:\n");
				doc = doc.replace("\n@param", "\n\nParameters:\n");
				doc = doc.replace("\n@return", "\n\nReturns:\n");
				doc = doc.replace("\n@see", "\n\nSee:\n");
				doc = doc.replace("\n@since", "\n\nSince:\n");
				doc = doc.replace("\n@throws", "\n\nThrows:\n");
				doc = doc.replace("\n@version", "\n\nVersion:\n");
				
				//Removing HTML tags
				while(doc.contains("<") && doc.contains(">") &&
					  doc.indexOf('<') < doc.indexOf('>') &&
					  !doc.substring(doc.indexOf('<'), doc.indexOf('>')).contains(" ")) {
					int start = 0;
					for(int i = 0; i < doc.length(); i++) {
						if(doc.charAt(i) == '<') start = i;
						if(doc.charAt(i) == '>') {
							doc = doc.substring(0, start) + doc.substring(i + 1);
							start = 0;
						}
					}
				}

				return doc;
			}
			
			/**
			 * @see org.eclipse.draw2d.MouseMotionListener#mouseExited(org.eclipse.draw2d.MouseEvent)
			 */
			public void mouseExited(MouseEvent me) {
				getViewer().getControl().setToolTipText(null);
			}

			/**
			 * @see org.eclipse.draw2d.MouseMotionListener#mouseHover(org.eclipse.draw2d.MouseEvent)
			 */
			public void mouseHover(MouseEvent me) {}
			
			/**
			 * @see org.eclipse.draw2d.MouseMotionListener#mouseMoved(org.eclipse.draw2d.MouseEvent)
			 */
			public void mouseMoved(MouseEvent me) {}
		});
	}

	/**
	 * @param doc - The <code>Javadoc</code>.
	 * @return The javadoc String from the given <code>Javadoc</code>.
	 */
	protected String getDoc(Javadoc doc) {
		if (doc == null) {
			return "";
		}
		
		return doc.toString();
	}
}

/**
 * Retrieves the javadoc string that corresponds to an
 * <code>IJavaElement</code>.
 * 
 * @author bcmartin
 */
class JavadocGrabber extends ASTVisitor {
	private IJavaElement _element;
	protected String _doc;
	
	/**
	 * Sets the element whose doc is being grabbed.
	 * 
	 * @param element - The element.
	 */
	public void setElement(IJavaElement element) {
		_element = element;
	}
	
	/**
	 * Compares two <code>IJavaElement</code>s for equality.
	 * 
	 * @param element - The element to compare the current element to.
	 * @return true if the elements are equal; false otherwise.
	 */
	public boolean compareElements(IJavaElement element) {
		return (_element.getHandleIdentifier().equals(
				element.getHandleIdentifier()));
	}
	
	/**
	 * @return The javadoc, if one was found; false otherwise.
	 */
	public String getJavadoc() {
		return _doc != null ? _doc : "";
	}
}