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

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.CompositeChange;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.ltk.core.refactoring.participants.CheckConditionsContext;
import org.eclipse.ltk.core.refactoring.participants.RenameParticipant;


//TODO This is unimplemented
//Most of the code here is copy/pasted from RefactorRenameParticipant

/**
 * Allows Green to participate in the refactoring of Java elements.
 * 
 * @author <a href="mailto:zgwang@sourceforge.net">Gene Wang</a>
 *
 */
public class RefactorMoveParticipant extends RenameParticipant {
//	
//	/**
//	 * 
//	 */
//	public RefactorMoveParticipant() {
//	}
//
	/* (non-Javadoc)
	 * @see org.eclipse.ltk.core.refactoring.participants.RefactoringParticipant#checkConditions(org.eclipse.core.runtime.IProgressMonitor, org.eclipse.ltk.core.refactoring.participants.CheckConditionsContext)
	 */
	@Override
	public RefactoringStatus checkConditions(IProgressMonitor pm, CheckConditionsContext context) throws OperationCanceledException {
		//Does not require condition checking in addition to that which is provided by the JDT refactoring.
		return new RefactoringStatus();
	}
//	
//	/* (non-Javadoc)
//	 * @see org.eclipse.ltk.core.refactoring.participants.RefactoringParticipant#createChange(org.eclipse.core.runtime.IProgressMonitor)
//	 */
//	@Override
	public Change createChange(IProgressMonitor pm) throws CoreException, OperationCanceledException {
		CompositeChange change = null; //Allows the return of a null change if no applicable changes are found.
//		IJavaElement element = ((IJavaElement)(getProcessor()).getElements()[0]); 
//		String oldName = element.getElementName();
//		String handle = element.getHandleIdentifier();
//		String newName = ((JavaRenameProcessor) getProcessor()).getNewElementName();
//		if(newName.indexOf('.') != -1) { //To handle type names that include .java
//			newName = newName.substring(0, newName.indexOf('.'));
//		}
//
//		//Assumes project is open and exists
//		IProject project = element.getResource().getProject();
//		ArrayList<IFile> greenFiles = ResourceUtil.greenFiles(project);
//				
//		for(IFile file : greenFiles) {
//			BufferedReader br = new BufferedReader(new InputStreamReader(file.getContents()));
//			String fileText = "";
//			String line = null;
//		    try {
//				while ((line = br.readLine()) != null) {
//					fileText += line + '\n';
//				}
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
//			
//			//Starting index of element handle
//			int[] handleIndices = ResourceUtil.findAllOccurrences(fileText, handle);
//			ArrayList<Integer> offsets = new ArrayList<Integer>();
//
//			for(int i = 0; i < handleIndices.length; i++) {
////				if(!(element instanceof SourceType)) {
//					//Remove type names in case of: e.g. type name same as package name
////					handle = handle.substring(0, handle.indexOf('['));
////				}
//				int[] tempOffsets = ResourceUtil.findAllOccurrences(handle, oldName);
//				for(int j = 0; j < tempOffsets.length; j++) {
//					offsets.add(new Integer(handleIndices[i] + tempOffsets[j]));
//				}
//			}
//			
//			if(!offsets.isEmpty()) { //Changes exist
//				if(change == null) { //Creates a new Change object if necessary
//					change = new CompositeChange("Change reference '" + oldName + "' to '" + newName + "'.");
//				}
//				
//				TextFileChange result = new TextFileChange( file.getName(), file );
//				MultiTextEdit fileChangeRootEdit = new MultiTextEdit();
//				result.setEdit(fileChangeRootEdit);
//
//				for(Integer offset : offsets) {
//					ReplaceEdit edit = new ReplaceEdit(offset.intValue(), oldName.length(), newName);
//					fileChangeRootEdit.addChild(edit);
//				}
//				change.add(result);
//			}
//		}
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
}
