/* This file is part of Green.
 *
 * Copyright (C) 2005 The Research Foundation of State University of New York
 * All Rights Under Copyright Reserved, The Research Foundation of S.U.N.Y.
 * 
 * Green is free software, licensed under the terms of the Eclipse
 * Public License, version 1.0.  The license is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

/**
 * 
 */
package edu.buffalo.cse.green.util;

import java.util.ArrayList;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;

import static edu.buffalo.cse.green.constants.PluginConstants.GREEN_EXTENSION;

/**
 * Utility class with static members for manipulation of resources.
 * 
 * @author <a href="mailto:zgwang@sourceforge.net">Gene Wang</a>
 * @since 3.1
 */
public class ResourceUtil {

	public static final String HANDLE_PREFIXES = "=/<{[";
	
	/**
	 * Return an array of Green's XML files within the given project,
	 * Returns null if the project does not exist or is closed.
	 * @param project the project to search in
	 * @return ArrayList of Green files
	 */
	public static ArrayList<IFile> getGreenFiles(IProject project) {
		ArrayList<IFile> files = new ArrayList<IFile>();
		
		try {
			IResource[] resources = project.members();
			for(IResource resrc : resources) {
				if(resrc.getType() == IResource.FILE) {
					String ext = resrc.getFileExtension();
					if(ext != null && ext.toLowerCase().equals(GREEN_EXTENSION)) {
						files.add((IFile) resrc);
					}
				}
				else if(resrc.getType() == IResource.FOLDER && !resrc.getName().equals("bin")) {
					//Skips the bin folder.
					//Recursively search for more files
					files.addAll(getFiles((IFolder) resrc));
				}
			}
			
		} catch (CoreException e) {
			System.err.println("Project (" + project.getName() + ") search failed: project is closed or does not exist.");
			e.printStackTrace();
		}
		return files;
	}
	
	/**
	 * Recursively search a folder for Green files.
	 * @param f
	 * @return an ArrayList of IFile whose contents refer to Green's XML files.
	 */
	private static ArrayList<IFile> getFiles(IFolder f) {
		ArrayList<IFile> files = new ArrayList<IFile>();
		try {
			IResource[] resources = f.members();
			for(IResource resrc : resources) {
				if(resrc.getType() == IResource.FILE) {
					String ext = resrc.getFileExtension();
					if(ext != null && ext.toLowerCase().equals(GREEN_EXTENSION)) {
						files.add((IFile) resrc);
					}
				}
				else if(resrc.getType() == IResource.FOLDER) {
					//Recursively search for more files
					files.addAll(getFiles((IFolder) resrc));
				}
			}
		} catch (CoreException e) {
			System.err.println("Folder (" + f.getName() + ") does not exist.");
			e.printStackTrace();
		}
		return files;
	}
	
	
	/**
	 * Returns an array of integers listing the beginning indices of each
	 * occurrence of the substring in the given string.  A zero length array is returned
	 * if the specified substring is not found.
	 *  
	 * @param str the string to search
	 * @param substr the substring to search for
	 * @return
	 */
	public static int[] findAllOccurrences(String str, String substr) {
		if(str.indexOf(substr) == -1) {
			return new int[]{};
		}

		ArrayList<Integer> indexList = new ArrayList<Integer>();
		String tempStr = str;
		int prevAbsoluteIndex = 0;
		while(tempStr.indexOf(substr) != -1) {
			int index = tempStr.indexOf(substr);
			indexList.add(new Integer(prevAbsoluteIndex + index));
			tempStr = tempStr.substring(index + 1);
			prevAbsoluteIndex += index + 1;
		}
		int[] indices = new int[indexList.size()];
		for(int i = 0; i < indexList.size(); i++){
			indices[i] = indexList.get(i);
		}
		return indices;
	}
}
