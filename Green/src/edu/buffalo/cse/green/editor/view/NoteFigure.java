/* This file is part of Green.
 *
 * Copyright (C) 2005 The Research Foundation of State University of New York
 * All Rights Under Copyright Reserved, The Research Foundation of S.U.N.Y.
 * 
 * Green is free software, licensed under the terms of the Eclipse
 * Public License, version 1.0.  The license is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package edu.buffalo.cse.green.editor.view;

import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.StackLayout;
import org.eclipse.draw2d.text.FlowPage;
import org.eclipse.draw2d.text.ParagraphTextLayout;
import org.eclipse.draw2d.text.TextFlow;

import static org.eclipse.draw2d.text.ParagraphTextLayout.WORD_WRAP_SOFT;


/**
 * A figure that holds note information.
 * 
 * @author zgwang
 */
public class NoteFigure extends Figure implements INoteFigure {
	
	/** The inner TextFlow **/
	private TextFlow _flow;

	/**
	 *  Creates a new NoteFigure.
	 */
	public NoteFigure() {
		setOpaque(true);
		FlowPage flowPage = new FlowPage();
		_flow = new TextFlow();
		_flow.setLayoutManager(new ParagraphTextLayout(_flow, WORD_WRAP_SOFT));
		flowPage.add(_flow);
		setLayoutManager(new StackLayout());
		add(flowPage);
	}

	/**
	 * Returns the text inside the TextFlow.
	 * 
	 * @return the text flow inside the text.
	 */
	public String getText() {
		return _flow.getText();
	}

	/**
	 * Sets the text of the TextFlow to the given text.
	 * 
	 * @param newText the new text.
	 */
	public void setText(String newText) {
		_flow.setText(newText);
	}
}