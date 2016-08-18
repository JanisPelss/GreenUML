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

import static org.eclipse.jdt.core.dom.ASTNode.FIELD_DECLARATION;
import static org.eclipse.jdt.core.dom.ASTNode.METHOD_DECLARATION;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.dom.AbstractTypeDeclaration;
import org.eclipse.jdt.core.dom.BodyDeclaration;
import org.eclipse.jdt.core.dom.EnumDeclaration;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;

import edu.buffalo.cse.green.GreenException;

/**
 * Provides a uniform way to access information contained within an
 * <code>AbstractTypeDeclaration</code> subclass. This is necessary because the
 * nodes that represent enum instances are different from the ones that
 * represent classes and interfaces.
 * 
 * @author bcmartin
 */
public abstract class DeclarationInfoProvider {
	private AbstractTypeDeclaration _node;
	
	/**
	 * @return A list of <code>FieldDeclaration</code> nodes belonging to the
	 * given <code>AbstractTypeDeclaration</code>.
	 */
	public abstract List<FieldDeclaration> getFields();
	
	/**
	 * @return A list of <code>MethodDeclaration</code> nodes belonging to the
	 * given <code>AbstractTypeDeclaration</code>.
	 */
	public abstract List<MethodDeclaration> getMethods();
	
	/**
	 * @return The superclass <code>Type</code> of the given
	 * <code>AbstractTypeDeclaration</code>. 
	 */
	public abstract Type getSuperclassType();
	
	/**
	 * @return true if the given <code>AbstractTypeDeclaration</code> represents
	 * an interface, false otherwise.
	 */
	public abstract boolean isInterface();
	
	/**
	 * Sets the given <code>AbstractTypeDeclaration</code>'s superclass to the
	 * given type.
	 * 
	 * @param type - The given <code>Type</code>
	 */
	public abstract void setSuperclassType(Type type);
	
	/**
	 * @return A list of <code>Type</code> nodes. For a class or enum, returns
	 * a list of implemented interfaces; for an interface, returns a list of
	 * extended interfaces. 
	 */
	public abstract List<Type> getSuperInterfaceTypes();
	
	protected DeclarationInfoProvider(AbstractTypeDeclaration node) {
		_node = node;
	}

	/**
	 * @param node - The <code>EnumDeclaration</code> to represent
	 * @return An info provider for the given enum
	 */
	public static DeclarationInfoProvider getInfoProvider(EnumDeclaration node) {
		return new EnumDeclarationInfoProvider(node);
	}
	
	/**
	 * @param node - The <code>TypeDeclaration</code> to represent
	 * @return An info provider for the given type
	 */
	public static DeclarationInfoProvider getInfoProvider(TypeDeclaration node) {
		return new TypeDeclarationInfoProvider(node);
	}
	
	/**
	 * @param fields - A list of <code>FieldDeclaration</code> to get the names
	 * of
	 * @return The names of the given fields
	 */
	public static List<String> getFieldNames(List<FieldDeclaration> fields) {
		List<String> fieldNames = new ArrayList<String>();
		
		for (FieldDeclaration field : fields) {
			fieldNames.addAll(getFieldNamesFromFragments(field));
		}
		
		return fieldNames;
	}
	

	/**
	 * @param field - The given <code>FieldDeclaration</code>
	 * @return The list of names of fields in the given declaration
	 */
	private static List<String> getFieldNamesFromFragments(
			FieldDeclaration field) {
		List<String> fields = new ArrayList<String>();
		List<VariableDeclarationFragment> fragments =
			(AbstractList<VariableDeclarationFragment>) field.fragments();

		for (VariableDeclarationFragment fragment : fragments) {
			String name = fragment.getName().getIdentifier();

			if (!name.equals("")) {
				fields.add(name);
				break;
			}
		}
		
		return fields;
	}

	/**
	 * @return The node represented by this instance
	 */
	public AbstractTypeDeclaration getDeclaration() {
		return _node;
	}

	/**
	 * @return All the body declarations of the represented node
	 */
	public List bodyDeclarations() {
		return _node.bodyDeclarations();
	}
}

/**
 * An implementation of a <code>DeclarationInfoProvider</code> for
 * <code>EnumDeclaration</code> nodes.
 * 
 * @author bcmartin
 */
class EnumDeclarationInfoProvider extends DeclarationInfoProvider {
	private EnumDeclaration _node;

	EnumDeclarationInfoProvider(EnumDeclaration node) {
		super(node);
		_node = node;
	}
	
	/**
	 * @see edu.buffalo.cse.green.relationships.DeclarationInfoProvider#getFields()
	 */
	public List<FieldDeclaration> getFields() {
		List<FieldDeclaration> fields = new ArrayList<FieldDeclaration>();
		
		for (BodyDeclaration dec : (AbstractList<BodyDeclaration>) (List)
				_node.bodyDeclarations()) {
			if (dec.getNodeType() == FIELD_DECLARATION) {
				fields.add((FieldDeclaration) dec);
			}
		}
		
		return fields;
	}
	
	/**
	 * @see edu.buffalo.cse.green.relationships.DeclarationInfoProvider#getMethods()
	 */
	public List<MethodDeclaration> getMethods() {
		List<MethodDeclaration> methods = new ArrayList<MethodDeclaration>();
		
		for (BodyDeclaration dec : (AbstractList<BodyDeclaration>) (List)
				_node.bodyDeclarations()) {
			if (dec.getNodeType() == METHOD_DECLARATION) {
				methods.add((MethodDeclaration) dec);
			}
		}
		
		return methods;
	}

	/**
	 * @see edu.buffalo.cse.green.relationships.DeclarationInfoProvider#getSuperclassType()
	 */
	public Type getSuperclassType() {
		return null;
	}

	/**
	 * @see edu.buffalo.cse.green.relationships.DeclarationInfoProvider#getSuperInterfaceTypes()
	 */
	public List<Type> getSuperInterfaceTypes() {
		return (AbstractList<Type>) _node.superInterfaceTypes();
	}

	/**
	 * @see edu.buffalo.cse.green.relationships.DeclarationInfoProvider#isInterface()
	 */
	public boolean isInterface() {
		return false;
	}

	/**
	 * @see edu.buffalo.cse.green.relationships.DeclarationInfoProvider#setSuperclassType(org.eclipse.jdt.core.dom.Type)
	 */
	public void setSuperclassType(Type type) {
		GreenException.illegalOperation("Enums do not have a superclass");
	}
}

/**
 * An implementation of a <code>DeclarationInfoProvider</code> for
 * <code>TypeDeclaration</code> nodes.
 * 
 * @author bcmartin
 */
class TypeDeclarationInfoProvider extends DeclarationInfoProvider {
	private TypeDeclaration _node;

	TypeDeclarationInfoProvider(TypeDeclaration node) {
		super(node);
		_node = node;
	}
	
	/**
	 * @see edu.buffalo.cse.green.relationships.DeclarationInfoProvider#getFields()
	 */
	public List<FieldDeclaration> getFields() {
		List<FieldDeclaration> fields = new ArrayList<FieldDeclaration>();
		
		for (FieldDeclaration field : _node.getFields()) {
			fields.add(field);
		}
		
		return fields;
	}

	/**
	 * @see edu.buffalo.cse.green.relationships.DeclarationInfoProvider#getMethods()
	 */
	public List<MethodDeclaration> getMethods() {
		List<MethodDeclaration> fields = new ArrayList<MethodDeclaration>();
		
		for (MethodDeclaration field : _node.getMethods()) {
			fields.add(field);
		}
		
		return fields;
	}

	/**
	 * @see edu.buffalo.cse.green.relationships.DeclarationInfoProvider#getSuperclassType()
	 */
	public Type getSuperclassType() {
		return _node.getSuperclassType();
	}

	/**
	 * @see edu.buffalo.cse.green.relationships.DeclarationInfoProvider#getSuperInterfaceTypes()
	 */
	public List<Type> getSuperInterfaceTypes() {
		return (AbstractList<Type>) _node.superInterfaceTypes();
	}

	/**
	 * @see edu.buffalo.cse.green.relationships.DeclarationInfoProvider#isInterface()
	 */
	public boolean isInterface() {
		return _node.isInterface();
	}
	
	/**
	 * @see edu.buffalo.cse.green.relationships.DeclarationInfoProvider#setSuperclassType(org.eclipse.jdt.core.dom.Type)
	 */
	public void setSuperclassType(Type type) {
		_node.setSuperclassType(type);
	}
}
