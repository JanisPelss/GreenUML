/* This file is part of Green.
 *
 * Copyright (C) 2005 The Research Foundation of State University of New York
 * All Rights Under Copyright Reserved, The Research Foundation of S.U.N.Y.
 * 
 * Green is free software, licensed under the terms of the Eclipse
 * Public License, version 1.0.  The license is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package edu.buffalo.cse.green.editor.action;

import java.util.List;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.swt.graphics.Rectangle;

import edu.buffalo.cse.green.editor.DiagramEditor;
import edu.buffalo.cse.green.editor.model.AbstractModel;
import edu.buffalo.cse.green.editor.model.RelationshipModel;

/**
 * Calculates and sets the zoom of the editor such that the 
 * diagram is zoomed in as far as possible while still
 * entirely in view.
 * 
 * @author zgwang
 */
public class ZoomFitAction extends ContextAction {
	
	/**
	 * Constructor
	 */
	public ZoomFitAction()
	{
		setAccelerator('a');
	}

	/**
	 * @see edu.buffalo.cse.green.editor.action.ContextAction#doRun()
	 */
	@Override
	protected void doRun() throws JavaModelException {
		DiagramEditor activeEditor = DiagramEditor.getActiveEditor();
		List<AbstractModel> allModels = activeEditor.getRootModel().getChildren();
		List<RelationshipModel> allRels = activeEditor.getRootModel().getRelationships();
		Rectangle viewSize = activeEditor.getSize();
		
		int hMin = 9999;
		int hMax = 0;
		int vMin = 9999;
		int vMax = 0;
		
		//Find the min/max bound coordinates for all element boxes
		for(AbstractModel m : allModels) {
			IFigure f = activeEditor.getRootPart().getPartFromModel(m).getFigure();

			if(allRels.contains(m)) {
				//Necessary because this method of obtaining the figure's dimension
				//is inaccurate for relationships.
				continue;
			}

			Dimension dim = m.getSize();
			if(dim.height == -1 && dim.width == -1) {
				//Box is default size, need to get real size instead.
				dim = f.getLayoutManager().getPreferredSize(f.getParent(), -1, -1);
			}
			
			int mLeft = m.getLocation().x;
			int mRight = mLeft + dim.width;
			int mTop = m.getLocation().y;
			int mBottom = mTop + dim.height;
			
			if(mLeft < hMin) hMin = mLeft;
			if(mRight > hMax) hMax = mRight;
			if(mTop < vMin) vMin = mTop;
			if(mBottom > vMax) vMax = mBottom;
		}
		
		//Find the min/max bound coordinates for all relationships
		for(RelationshipModel rm : allRels) {
			int rmLeft = rm.getLocation().x;
			int rmRight = rmLeft + rm.getSize().width;
			int rmTop = rm.getLocation().y;
			int rmBottom = rmTop + rm.getSize().height;
			
			if(rmLeft < hMin) hMin = rmLeft;
			if(rmRight > hMax) hMax = rmRight;
			if(rmTop < vMin) vMin = rmTop;
			if(rmBottom > vMax) vMax = rmBottom;
		}
		
		if(hMin > 0 || vMin > 0) {
			for(AbstractModel m : allModels) {
				int mLeft = m.getLocation().x;
				int mTop = m.getLocation().y;
				m.setLocation(mLeft - hMin, mTop - vMin);
			}
		}
		
		//viewSize.width/height is the editor window size + scroll bars (17 pixels wide)
		double widthScale = (double)(viewSize.width - 17) / (hMax - hMin);
		double heightScale = (double)(viewSize.height - 17) / (vMax - vMin); 

		DiagramEditor.getActiveEditor().getZoomManager().setZoom(Math.min(widthScale, heightScale));
	}

	/**
	 * @see edu.buffalo.cse.green.editor.action.ContextAction#getLabel()
	 */
	@Override
	public String getLabel() {
		return "Zoom Fit";
	}

	/**
	 * @see edu.buffalo.cse.green.editor.action.ContextAction#getPath()
	 */
	@Override
	public Submenu getPath() {
		return Submenu.Zoom;
	}

	/**
	 * @see edu.buffalo.cse.green.editor.action.ContextAction#getSupportedModels()
	 */
	@Override
	protected int getSupportedModels() {
		return CM_EDITOR;
	}

	/**
	 * @see edu.buffalo.cse.green.editor.action.ContextAction#isEnabled()
	 */
	@Override
	public boolean isEnabled() {
		return true;
	}
}