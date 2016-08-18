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

import static edu.buffalo.cse.green.constants.XMLConstants.XML_NOTE;
import static edu.buffalo.cse.green.constants.XMLConstants.XML_NOTE_HEIGHT;
import static edu.buffalo.cse.green.constants.XMLConstants.XML_NOTE_TEXT;
import static edu.buffalo.cse.green.constants.XMLConstants.XML_NOTE_WIDTH;
import static edu.buffalo.cse.green.constants.XMLConstants.XML_NOTE_X;
import static edu.buffalo.cse.green.constants.XMLConstants.XML_NOTE_Y;
import static edu.buffalo.cse.green.editor.controller.PropertyChange.Note;
import static org.eclipse.jface.window.Window.OK;

import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.palette.ToolEntry;
import org.eclipse.jdt.core.IJavaElement;

import edu.buffalo.cse.green.editor.DiagramEditor;
import edu.buffalo.cse.green.editor.action.ContextAction;
import edu.buffalo.cse.green.editor.controller.NotePart;
import edu.buffalo.cse.green.editor.model.commands.DeleteCommand;
import edu.buffalo.cse.green.editor.model.commands.DeleteNoteCommand;
import edu.buffalo.cse.green.xml.XMLConverter;

/**
 * Creates a note that displays a message in the diagram.
 * 
 * @author hk47
 * @author zgwang
 */
public class NoteModel extends AbstractModel<AbstractModel, RootModel, IJavaElement> {
	private String _label;

	public NoteModel() {
		_label = "Double Click to Edit";
	}

	public NoteModel(String label) {
		_label = parseHTML(label);
	}
	
	/**
	 * Sets the text displayed in the note.
	 */
	public void setLabel(String mesg) {
		String oldMesg = _label;
		_label = parseHTML(mesg);
		firePropertyChange(Note, oldMesg, _label);
	}

	/**
	 * Returns the text displayed in the note.
	 */
	public String getLabel() {
		return _label;
	}

	/**
	 * Converts certain HTML 4.0 character reference entities to their
	 * corresponding characters to be displayed in the note
	 * 
	 * @return parsed version of the given text
	 * 
	 * @author zgwang
	 */
	private String parseHTML(String text) {
		String converted;
		converted = text;
		converted = converted.replace("&lt;", "<");
		converted = converted.replace("&gt;", ">");
		converted = converted.replace("&quot;", "\"");
		return converted;
	}
	
	/**
	 * Converts special characters in the note text to character entity
	 * references as defined by HTML 4.0
	 * 
	 * @return encoded version of the note text
	 * 
	 * @author zgwang
	 */
	private String toHTML() {
		String converted;
		converted = _label;
		converted = converted.replace("<", "&lt;");
		converted = converted.replace(">", "&gt;");
		converted = converted.replace("\"", "&quot;");
		return converted;
	}
	
	/**
	 * Writes the XML stored for this note to the converter.
	 */
	public void toXML(XMLConverter converter) {
		converter.pushHeader(XML_NOTE);
		converter.writeKey(XML_NOTE_TEXT, toHTML());
		converter.writeKey(XML_NOTE_HEIGHT, "" + getSize().height);
		converter.writeKey(XML_NOTE_WIDTH, "" + getSize().width);
		converter.writeKey(XML_NOTE_X, "" + getLocation().x);
		converter.writeKey(XML_NOTE_Y, "" + getLocation().y);

		super.toXML(converter);
		converter.popHeader();
	}

	/**
	 * @see edu.buffalo.cse.green.editor.model.AbstractModel#getContextMenuFlag()
	 */
	public int getContextMenuFlag() {
		return ContextAction.CM_NOTE;
	}

	/**
	 * @see edu.buffalo.cse.green.editor.model.AbstractModel#getDeleteCommand(edu.buffalo.cse.green.editor.DiagramEditor)
	 */
	public DeleteCommand getDeleteCommand(DiagramEditor editor) {
		return new DeleteNoteCommand(this);
	}

	/**
	 * @param editor - The <code>DiagramEditor</code> containing this model.
	 * 
	 * @return A command to hide this model.
	 */
	public Command getHideCommand(DiagramEditor editor) {
		return null;
	}

	/**
	 * @see edu.buffalo.cse.green.editor.model.AbstractModel#getPartClass()
	 */
	public Class getPartClass() {
		return NotePart.class;
	}

	/**
	 * @see edu.buffalo.cse.green.editor.model.AbstractModel#getJavaElement()
	 */
	public IJavaElement getJavaElement() {
		return null;
	}

	/**
	 * @see edu.buffalo.cse.green.editor.model.AbstractModel#removeFromParent()
	 */
	public void removeFromParent() {
		getParent().removeChild(this);
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return getLabel();
	}

	/**
	 * @see edu.buffalo.cse.green.editor.model.AbstractModel#handleDispose()
	 */
	public void handleDispose() {
		// do nothing
	}

	/**
	 * @see edu.buffalo.cse.green.editor.model.AbstractModel#invokeCreationDialog(org.eclipse.gef.palette.ToolEntry)
	 */
	public int invokeCreationDialog(ToolEntry tool) {
		return OK;
	}
	
	/**
	 * @see edu.buffalo.cse.green.editor.model.AbstractModel#createNewInstance(edu.buffalo.cse.green.editor.model.AbstractModel)
	 */
	public void createNewInstance(AbstractModel model) {
		// make sure we don't have a miniscule model
		if (model.getSize() == null || model.getSize().height < 5
				|| model.getSize().width < 5) {
			model.setSize(new Dimension(150, 50));
		}
		getRootModel().addChild((NoteModel) model);
	}
}