/* This file is part of Green.
 *
 * Copyright (C) 2005 The Research Foundation of State University of New York
 * All Rights Under Copyright Reserved, The Research Foundation of S.U.N.Y.
 * 
 * Green is free software, licensed under the terms of the Eclipse
 * Public License, version 1.0.  The license is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package edu.buffalo.cse.green;

import org.eclipse.jface.dialogs.MessageDialog;

import edu.buffalo.cse.green.types.ITypeProperties;

/**
 * Displays errors that occur in Green; this prevents us from having to use
 * System.err directly in our code and facilitates abstraction by moving the
 * error message Strings here.
 * 
 * @author bcmartin
 * @author rjtruban
 */
public class GreenException extends RuntimeException {
	protected static final long serialVersionUID = -760398655607581288L;

	/**
	 * Indicates that a situation occured in the <code>JavaModelListener</code>
	 * class that was unhandled.
	 */
	public static final String GRERR_JVM_CHANGE_UNHANDLED = "Unhandled JavaModel change.";

	/**
	 * Indicates that an illegal index was used as an argument.
	 */
	public static final String GRERR_INVALID_INDEX = "Invalid index.";

	/**
	 * Indicates that the file being read by Green had an invalid format.
	 */
	public static final String GRERR_FILE_FORMAT = "Invalid file format.";

	/**
	 * Indicates that the file being loaded by Green could not be found.
	 */
	public static final String GRERR_FILE_NOT_FOUND = "Unable to find specified file.";

	/**
	 * Indicates that the relationship model has an invalid source or target
	 * node.
	 */
	public static final String GRERR_INVALID_RELATIONSHIP_NODES = "Invalid source or target node for relationship.";
	
	/**
	 * Indicates that the model being created by the palette in the editor is
	 * not yet supported by the code there.
	 */
	public static final String GRERR_PALETTE_CREATION = "Unsupported creation.";

	/**
	 * Indicates that a relationship cannot be created because no methods exist
	 * in which to create the relationship.
	 */
	public static final String GRERR_RELATIONSHIP_NO_METHODS = "No methods exist in the given source class.";
	
	/**
	 * Indicates that more than one save handler is being passed in to handle a
	 * single save format.
	 */
	public static final String GRERR_DUPLICATE_EXTENSION =
		"Duplicate Green file extension detected. Check you plugin.xml files.";
	
	/**
	 * Indicates that the user selected an empty project, which is invalid.
	 */
	public static final String GRERR_EMPTY_PROJECT = "Cannot use an empty project.";

	/**
	 * Indicates that handling of types is unsupported for the given type.
	 */
	public static final String GRERR_UNHANDLED_JAVA_TYPE = "The given type is unsupported for the current operation.";
	
	/**
	 * Indicates that the user tried to load code in from the default package.
	 */
	public static final String GRERR_USING_DEFAULT_PACKAGE = "Cannot use default package in editor for type.";

	/**
	 * Warns the user the a model was added to the wrong editor.
	 */
	public static final String GRWARN_ELEMENT_IN_WRONG_EDITOR = "Element added to wrong editor.";

	/**
	 * Warns the user that the project represented by the editor was overridden.
	 */
	public static final String GRERR_WRONG_SOURCE_PROJECT = "The editor whose project was reset did not have the specified old project.";

	/**
	 * Warns the user that the project represented by the editor was overridden.
	 */
	public static final String GRERR_INVALID_XML_STRUCTURE = "The structure of the diagram file cannot be interpretted by the plugin.";

	/**
	 * Warns the user that the file's version number is newer than the plugin's
	 * version number.
	 */
	public static final String GRERR_FILE_VERSION_TITLE = "File newer than plugin.";
	
	/**
	 * Tells the programmer that "null" was illegally used as an argument.
	 */
	public static final String GRERR_NULL = "Argument is null.";

	/**
	 * Indicates that the desired Java type is not supported.
	 */
	public static final String GRERR_TYPE_UNSUPPORTED = "Type is Unsupported.";

	/**
	 * Figures generated using the figure factory must belong to a class
	 * that has 1 constructor with no parameters.
	 */
	public static final String GRERR_FIGURE_CONSTRUCTOR = "Wrong number of constructors or parameters. The class must have one constructor with no parameters.";

	/**
	 * Tells the programmer that the plugin is at least as new as the file, but
	 * file support is not implemented. The programmer should look at the
	 * <code>DiagramEditorFilePolicies</code> class to determine what should be
	 * done.
	 */
	public static final String UNSUPPORTED_FILE_VERSION = "The version of the file you are trying to load in is unsupported: ";

	/**
	 * Warns the user that a critical error has occurred. This type of error
	 * normally crashes Green.
	 */
	private static final String GRERR_REPORT_CRITICAL =
		"A critical error has occurred. You should copy this message along "
		+ "with the stack trace below and send it to the contact address "
		+ "provided with this software.";

	/**
	 * Indicates that the extension in the XML file was not a context action.
	 */
	public static final String GRERR_INVALID_CONTEXT_ACTION =
		"Action class must be a subclass of ContextAction";

	public static final String GRERR_INVALID_EXTENSION =
		"Invalid extension. Valid extensions must consist of alphanumeric "
		+ "characters and must be between 1 and 4 characters in length.";

	public static final String GRERR_REL_SOURCE_BINARY =
		"A relationship cannot be generated with a binary type as its source.";

	public static final String GRERR_UNAVAILABLE_ACTION =
		"The desired action is not currently available";

	public GreenException(String description) {
		super(description);
	}
	
	/**
	 * This method should be called when an unexpected behavior is encountered
	 * during Green's execution; no warning messages like these should ever be
	 * seen by the user; the presence of one indicates that there is a flaw in
	 * existing code.
	 * 
	 * @param message - The message to be displayed in the console.
	 */
	public static void warn(String message) {
		System.err.println("GREEN WARNING: " + message);
	}

	/**
	 * Displays a warning that informs the user that the plugin's version is
	 * older than the file's version and hence the file's information may not be
	 * accurately reflected by the diagram.
	 * 
	 * @param pluginVersion - The plugin's version.
	 * @param fileVersion - The file's version.
	 * @return An appropriate error message.
	 */
	public static String generateVersionWarning(int pluginVersion, int fileVersion) {
		return "The file you are trying to load was made with a newer version "
		+ "of Green (" + fileVersion + ") than you are currently running ("
		+ pluginVersion + "). You should install an updated version of Green. "
		+ "The contents of the editor may not accurately reflect the appearance"
		+ " of the diagram when it was last saved. If you save this diagram in "
		+ "the editor, some information from the diagram file may be lost.";
	}

	/**
	 * Throws a critical error.
	 * 
	 * @param t - The throwable that occurred.
	 */
	public static void critical(Throwable t) {
		warn(GRERR_REPORT_CRITICAL);
		t.printStackTrace();
	}
	
	/**
	 * Throws an <code>IllegalOperationException</code>.
	 */
	public static void illegalOperation() {
		throw new IllegalOperationException("no message specified");
	}

	/**
	 * Throws an <code>IllegalOperationException</code>.
	 * 
	 * @param description - The exception's description
	 */
	public static void illegalOperation(String description) {
		if (description == null) return;
		
		throw new IllegalOperationException(description);
	}
	
	/**
	 * Throw a <code>GreenFileException</code>.
	 * 
	 * @param description - The exception's description
	 */
	public static void fileException(String description) {
		throw new GreenFileException(description);
	}

	/**
	 * Displays an error dialog with the specified message.
	 * 
	 * @param error - The message.
	 */
	public static void errorDialog(String error) {
		MessageDialog.openInformation(PlugIn.getDefaultShell(),
				"Error", error);
	}

	/**
	 * Notifies the user that the incorrect class was used in implementing a
	 * plugin to Green.
	 * 
	 * @param aClass - The given class (via the plugin).
	 * @param eClass - The expected class.
	 */
	public static void illegalExtensionClass(Class<? extends Object> aClass,
			Class<ITypeProperties> eClass) {
		illegalOperation("Illegal plugin class: expected " + eClass + ", but "
				+ "was " + aClass);
	}
}

/**
 * Green file error indicating an I/O exception due to Green's behavior.
 * 
 * @author bcmartin
 */
class GreenFileException extends GreenException {
	private static final long serialVersionUID =
		GreenException.serialVersionUID;
	
	public GreenFileException(String description) {
		super(description);
	}
}

/**
 * Generic Green error indicating that an illegal operation was performed.
 * 
 * @author bcmartin
 */
class IllegalOperationException extends GreenException {
	private static final long serialVersionUID =
		GreenException.serialVersionUID;

	public IllegalOperationException(String description) {
		super(description);
	}
	
	public IllegalOperationException(Exception e) {
		super("An exception of type \"" + e.getClass() + "\" has occurred");
	}
}
