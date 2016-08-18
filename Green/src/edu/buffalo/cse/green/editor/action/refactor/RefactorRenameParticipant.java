/* This file is part of Green.
 *
 * Copyright (C) 2005 The Research Foundation of State University of New York
 * All Rights Under Copyright Reserved, The Research Foundation of S.U.N.Y.
 * 
 * Green is free software, licensed under the terms of the Eclipse
 * Public License, version 1.0.  The license is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package edu.buffalo.cse.green.editor.action.refactor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
//import org.eclipse.core.resources.mapping.IResourceChangeDescriptionFactory;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.internal.corext.refactoring.rename.JavaRenameProcessor;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.CompositeChange;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.ltk.core.refactoring.TextFileChange;
import org.eclipse.ltk.core.refactoring.participants.CheckConditionsContext;
import org.eclipse.ltk.core.refactoring.participants.RenameParticipant;
import org.eclipse.ltk.core.refactoring.participants.RenameProcessor;
//import org.eclipse.ltk.core.refactoring.participants.ResourceChangeChecker;
import org.eclipse.text.edits.MultiTextEdit;
import org.eclipse.text.edits.ReplaceEdit;

import edu.buffalo.cse.green.util.ResourceUtil;
import static edu.buffalo.cse.green.util.ResourceUtil.HANDLE_PREFIXES;


/**
 * Allows Green to participate in the refactoring of Java elements.
 * 
 * @author <a href="mailto:zgwang@sourceforge.net">Gene Wang</a>
 *
 */
public class RefactorRenameParticipant extends RenameParticipant {
	
	/**
	 * 
	 */
	public RefactorRenameParticipant() {
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.ltk.core.refactoring.participants.RefactoringParticipant#checkConditions(org.eclipse.core.runtime.IProgressMonitor, org.eclipse.ltk.core.refactoring.participants.CheckConditionsContext)
	 */
	@Override
	public RefactoringStatus checkConditions(IProgressMonitor pm, CheckConditionsContext context) throws OperationCanceledException {
//		ResourceChangeChecker checker =  (ResourceChangeChecker)context.getChecker(ResourceChangeChecker.class);
//		IResourceChangeDescriptionFactory deltaFactory = checker.getDeltaFactory();
		
//		deltaFactory.
		
//		this.getProcessor().
		//Does not require condition checking in addition to that which is provided by the JDT refactoring.
		return new RefactoringStatus();
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.ltk.core.refactoring.participants.RefactoringParticipant#createChange(org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public Change createChange(IProgressMonitor pm) throws CoreException, OperationCanceledException {
		if(!(getProcessor() instanceof RenameProcessor)) {
			return null;
		}

		CompositeChange change = null; //Allows the return of a null change if no applicable changes are found.
		IJavaElement element = ((IJavaElement)(getProcessor()).getElements()[0]); 
		String oldName = element.getElementName();
		oldName = oldName.replace(".java", "");
		String handle = element.getHandleIdentifier();
		String newName = ((JavaRenameProcessor) getProcessor()).getNewElementName();
		newName = newName.replace(".java", "");
		ArrayList<HandleNode> handleList = new ArrayList<HandleNode>();
		
		String handleCopy = handle;
		char prefix = handleCopy.charAt(0);
		handleCopy = handleCopy.substring(1);
		for(int j = 0; j < handleCopy.length(); j++) {
			if(HANDLE_PREFIXES.contains(handleCopy.charAt(j) + "")){
				handleList.add(new HandleNode(prefix + handleCopy.substring(0, j)));
				prefix = handleCopy.charAt(j);
				handleCopy = handleCopy.substring(j + 1);
				j = 0;
			}
		}
		handleList.add(new HandleNode(prefix + handleCopy));
		handleCopy = "";
		
		Iterator<HandleNode> itr = handleList.iterator();
		
		while(itr.hasNext()) {
			HandleNode node = itr.next();
//			if(node.getName().contains(".")) {
//				if((node.getName()).substring(0, node.getName().indexOf('.')) == oldName) {
//					node.setName(newName + ".java");
//				}
//			}
			if(node.getName().equals(oldName)) {
				node.setName(newName);				
			}
		}
		Iterator<HandleNode> itr2 = handleList.iterator();
		String newHandle = "";
		while(itr2.hasNext()) {
			HandleNode node = itr2.next();
			newHandle += node.toString();
		}
		
		
		
//		if(newName.indexOf('.') != -1) { //To handle type names that include .java
//			if(oldName.contains(".java")) {
//				oldName = oldName.substring(0, oldName.indexOf(".java"));
//			}
//			newName = newName.substring(0, newName.indexOf('.'));
//		}

		//Assumes project is open and exists (which should be the case if the element
		//was able to be selected)
		IProject project = element.getResource().getProject();
		ArrayList<IFile> greenFiles = ResourceUtil.getGreenFiles(project);
				
		for(IFile file : greenFiles) {
			BufferedReader br = new BufferedReader(new InputStreamReader(file.getContents()));
			String fileText = "";
			String line = null;
		    try {
				while ((line = br.readLine()) != null) {
					fileText += line + '\n';
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			//Starting index of element handle
			int[] handleIndices = ResourceUtil.findAllOccurrences(fileText, handle);
//			ArrayList<Integer> offsets = new ArrayList<Integer>();

			if(change == null) { //Creates a new Change object if necessary
				change = new CompositeChange("Change reference '" + oldName + "' to '" + newName + "'.");
			}
			TextFileChange result = new TextFileChange(file.getName(), file);
			MultiTextEdit fileChangeRootEdit = new MultiTextEdit();
			result.setEdit(fileChangeRootEdit);
	
			for(int i = 0; i < handleIndices.length; i++) {
				ReplaceEdit edit = new ReplaceEdit(handleIndices[i], handle.length(), newHandle);
				fileChangeRootEdit.addChild(edit);
				change.add(result);
			}

//			for(int i = 0; i < handleIndices.length; i++) {
//				int[] tempOffsets = ResourceUtil.findAllOccurrences(handle, oldName);
//				for(int j = 0; j < tempOffsets.length; j++) {
//					offsets.add(new Integer(handleIndices[i] + tempOffsets[j]));
//				}
//			}
			
//			if(!offsets.isEmpty()) { //Changes exist
//				if(change == null) { //Creates a new Change object if necessary
//					change = new CompositeChange("Change reference '" + oldName + "' to '" + newName + "'.");
//				}
//				
//				TextFileChange result = new TextFileChange(file.getName(), file);
//				MultiTextEdit fileChangeRootEdit = new MultiTextEdit();
//				result.setEdit(fileChangeRootEdit);
//
//				for(Integer offset : offsets) {
//					ReplaceEdit edit = new ReplaceEdit(offset.intValue(), oldName.length(), newName);
//					fileChangeRootEdit.addChild(edit);
//				}
//				change.add(result);
//			}
		}
		return change;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ltk.core.refactoring.participants.RefactoringParticipant#getName()
	 */
	@Override
	public String getName() {
		return "Green XML Rename Participant";
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ltk.core.refactoring.participants.RefactoringParticipant#initialize(java.lang.Object)
	 */
	@Override
	protected boolean initialize(Object element) {
		return element != null;
	}
	
	
	private class HandleNode {
		private char _prefix;
		private String _name;
		private String _suffix = "";
		
		public HandleNode(String fullName) {
			_prefix = fullName.charAt(0);
			if(fullName.contains(".java")) {
				_suffix = ".java";
				_name = (fullName.substring(1)).replace(".java", "");
			}
			else {
				_name = fullName.substring(1);
				
			}
		}
		
		public String getName() {
			return _name;
		}
		
		public void setName(String newName) {
			_name = newName;
		}
		
		public String toString() {
			return _prefix + _name + _suffix;
		}
	}
}
