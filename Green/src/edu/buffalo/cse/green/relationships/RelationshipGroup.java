/* This file is part of Green.
 *
 * Copyright (C) 2005 The Research Foundation of State University of New York
 * All Rights Under Copyright Reserved, The Research Foundation of S.U.N.Y.
 * 
 * Green is free software, licensed under the terms of the Eclipse
 * Public License, version 1.0.  The license is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package edu.buffalo.cse.green.relationships;

import org.eclipse.jface.resource.ImageDescriptor;

import edu.buffalo.cse.green.editor.model.RelationshipKind;

/**
 * A group representing the components of a relationship.
 * 
 * @author bcmartin
 */
public class RelationshipGroup {
	protected String _name;

	private boolean _visibility = true;

	private Class _partClass;

	private RelationshipGenerator _generator;

	private RelationshipRecognizer _recognizer;

	private RelationshipRemover _remover;

	private boolean _classToClass;

	private boolean _classToEnum;

	private boolean _classToInterface;

	private boolean _enumToClass;

	private boolean _enumToEnum;

	private boolean _enumToInterface;

	private boolean _interfaceToClass;

	private boolean _interfaceToEnum;

	private boolean _interfaceToInterface;

	private String _subtype;

	public RelationshipGroup(
			String name,
			String subtype,
			Class partClass,
			RelationshipGenerator generator,
			RelationshipRecognizer recognizer,
			RelationshipRemover remover,
			boolean classToClass,
			boolean classToEnum,
			boolean classToInterface,
			boolean enumToClass,
			boolean enumToEnum,
			boolean enumToInterface,
			boolean interfaceToClass,
			boolean interfaceToEnum,
			boolean interfaceToInterface) {
		_name = name;
		_subtype = subtype;
		_partClass = partClass;
		_generator = generator;
		_recognizer = recognizer;
		_remover = remover;
		_classToClass = classToClass;
		_classToEnum = classToEnum;
		_classToInterface = classToInterface;
		_enumToClass = enumToClass;
		_enumToEnum = enumToEnum;
		_enumToInterface = enumToInterface;
		_interfaceToClass = interfaceToClass;
		_interfaceToEnum = interfaceToEnum;
		_interfaceToInterface = interfaceToInterface;
	}

	/**
	 * @return The generator for the kind of relationship.
	 */
	public RelationshipGenerator getGenerator() {
		return _generator;
	}

	/**
	 * Note: the image file must be the name of the relationship (returned in
	 * getName()) concatenated with .gif
	 * 
	 * For example, if getName() returns the value "Association", then the image
	 * file must be "Association.gif"
	 */
	public ImageDescriptor getImageDescriptor() {
		return ImageDescriptor.createFromFile(getPartClass(), getName()
				+ ".gif");
	}

	/**
	 * @return Whether or not the given relationship type is visible in the
	 * editor.
	 */
	public boolean isVisible() {
		return _visibility;
	}

	/**
	 * Shows/hides all relationships of the given kind.
	 * 
	 * @param show - Whether or not relationships of the given kind should be
	 * shown.
	 */
	public void setVisible(boolean show) {
		_visibility = show;
	}

	/**
	 * @return The name of this kind of relationship.
	 */
	public String getName() {
		return _name;
	}

	/**
	 * @return The recognizer for this kind of relationship.
	 */
	public RelationshipRecognizer getRecognizer() {
		return _recognizer;
	}

	/**
	 * @return The remover for this kind of relationship.
	 */
	public RelationshipRemover getRemover() {
		return _remover;
	}

	/**
	 * @return The part class that represents this kind of relationship.
	 */
	public Class getPartClass() {
		return _partClass;
	}
	
	/**
	 * @return Whether class-to-class relationships are valid.
	 */
	public boolean isValidClassToClass() {
		return _classToClass;
	}
	
	/**
	 * @return Whether class-to-interface relationships are valid.
	 */
	public boolean isValidClassToInterface() {
		return _classToInterface;
	}
	
	/**
	 * @return Whether interface-to-class relationships are valid.
	 */
	public boolean isValidInterfaceToClass() {
		return _interfaceToClass;
	}
	
	/**
	 * @return Whether interface-to-interface relationships are valid.
	 */
	public boolean isValidInterfaceToInterface() {
		return _interfaceToInterface;
	}
	
	/**
	 * @return Flags determining how cardinality is counted in the recognizer.
	 */
	public RelationshipKind getFlags() {
		return _recognizer.getFlags();
	}

	/**
	 * @return The subtype name of the relationship, or null if it isn't a
	 * subtype.
	 */
	public String getSubtype() {
		return _subtype;
	}

	/**
	 * @return Whether class-to-enum relationships are valid.
	 */
	public boolean isValidClassToEnum() {
		return _classToEnum;
	}

	/**
	 * @return Whether enum-to-class relationships are valid.
	 */
	public boolean isValidEnumToClass() {
		return _enumToClass;
	}

	/**
	 * @return Whether enum-to-enum relationships are valid.
	 */
	public boolean isValidEnumToEnum() {
		return _enumToEnum;
	}

	/**
	 * @return Whether enum-to-interface relationships are valid.
	 */
	public boolean isValidEnumToInterface() {
		return _enumToInterface;
	}

	/**
	 * @return Whether interface-to-enum relationships are valid.
	 */
	public boolean isValidInterfaceToEnum() {
		return _interfaceToEnum;
	}
}