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

import static org.eclipse.jdt.core.IJavaElement.FIELD;
import static org.eclipse.jdt.core.IJavaElement.LOCAL_VARIABLE;
import static org.eclipse.jdt.core.dom.Modifier.STATIC;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import org.eclipse.jdt.core.IClassFile;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.ILocalVariable;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTMatcher;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.AbstractTypeDeclaration;
import org.eclipse.jdt.core.dom.BodyDeclaration;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.EnumDeclaration;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.IBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.Initializer;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.Name;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.ui.actions.OrganizeImportsAction;

import edu.buffalo.cse.green.editor.DiagramEditor;

/**
 * A parent for generators/recognizer/removers.
 * 
 * The functionality of this class is as follows:
 * -It must allow subclasses visit only appropriate nodes. For example, nodes
 *     that represent type declarations and methods.
 * -It must keep an up-to-the-minute listing of accessible fields and methods
 *     for the purposes of preventing duplicate field/method names in code
 *     generation and removing pieces of code, as well as helping the
 *     relationship recognizers extract pieces of code that uniquely identify a
 *     relationship's constituents.
 * 
 * @author bcmartin
 * @author dk29
 */
public abstract class RelationshipVisitor extends ASTVisitor {
	private ASTMatcher _matcher = new ASTMatcher();
	private MethodDeclaration _methodDeclaration;
	private AST _ast;
	private CompilationUnit _cu;
	private boolean _inConstructor = false;
	private List<String> _locals;
	private List<String> _parameters;
	private List<ILocalVariable> _parameterVars;
	private Stack<DeclarationInfoProvider> _typeStack;
	
	/**
	 * @param element - The member element.
	 * @return A <code>CompilationUnit</code> representing the structure of the
	 * source code of the element.
	 */
	public static CompilationUnit getCompilationUnit(IMember element) {
		ASTParser parser = ASTParser.newParser(AST.JLS3);
		parser.setResolveBindings(true);
		
		if (element.isBinary()) {
			parser.setSource((IClassFile) element
					.getAncestor(IJavaElement.CLASS_FILE));
		} else {
			parser.setSource((ICompilationUnit) element
					.getAncestor(IJavaElement.COMPILATION_UNIT));
		}

		return (CompilationUnit) parser.createAST(null);
	}

	protected RelationshipVisitor() {
		_typeStack = new Stack<DeclarationInfoProvider>();
		_locals = new ArrayList<String>();
		_parameters = new ArrayList<String>();
		_parameterVars = new ArrayList<ILocalVariable>();
	}

	/**
	 * Runs the visitor on the given <code>CompilationUnit</code>.
	 * 
	 * @param cu - The compilation unit.
	 */
	public void accept(CompilationUnit cu) {
		_ast = cu.getAST();
		_cu = cu;

		preVisit();
		
		run(cu, null);
	}

	/**
	 * This method is called before visiting occurs to allow a visitor to
	 * collect any information necessary to perform the desired behavior.
	 */
	protected void preVisit() {}

	/**
	 * @see org.eclipse.jdt.core.dom.ASTVisitor#endVisit(org.eclipse.jdt.core.dom.MethodDeclaration)
	 */
	public final void endVisit(MethodDeclaration node) {
		_methodDeclaration = null;
		_parameters.clear();
		_parameterVars.clear();
		_inConstructor = false;
	}

	/**
	 * @see org.eclipse.jdt.core.dom.ASTVisitor#endVisit(org.eclipse.jdt.core.dom.TypeDeclaration)
	 */
	public final void endVisit(EnumDeclaration node) {
		endVisit((TypeDeclaration) null);
	}

	/**
	 * @see org.eclipse.jdt.core.dom.ASTVisitor#endVisit(org.eclipse.jdt.core.dom.TypeDeclaration)
	 */
	public /*final*/ void endVisit(TypeDeclaration node) {
		_typeStack.pop();
	}

	/**
	 * @return The AST for the compilation unit.
	 */
	protected AST getAST() {
		return _ast;
	}

	/**
	 * @return The compilation unit.
	 */
	protected CompilationUnit getCompilationUnit() {
		return _cu;
	}

	/**
	 * @param element - The element.
	 * @return The <code>CompilationUnit</code> representing the given element.
	 */
	public CompilationUnit getCompilationUnit(IClassFile element) {
		ASTParser parser = ASTParser.newParser(AST.JLS3);
		parser.setResolveBindings(true);
		parser.setSource(element);
		return (CompilationUnit) parser.createAST(null);
	}

	/**
	 * @param element - The element.
	 * @return The <code>CompilationUnit</code> representing the given element.
	 */
	public CompilationUnit getCompilationUnit(ICompilationUnit element) {
		ASTParser parser = ASTParser.newParser(AST.JLS3);
		parser.setResolveBindings(true);
		parser.setSource(element);
		return (CompilationUnit) parser.createAST(null);
	}

	/**
	 * @param element - The element.
	 * @return The <code>CompilationUnit</code> representing the given element.
	 */
	public CompilationUnit getCompilationUnit(IType element) {
		if (element.isBinary()) {
			return getCompilationUnit((IClassFile) element
					.getAncestor(IJavaElement.CLASS_FILE));
		} else {
			return getCompilationUnit((ICompilationUnit) element
					.getAncestor(IJavaElement.COMPILATION_UNIT));
		}
	}

	/**
	 * @param provider - The type information provider.
	 * @return the Java element representing the given type.
	 */
	public IType getType(DeclarationInfoProvider provider) {
		return getType(provider.getDeclaration().resolveBinding());
	}

	/**
	 * @param type - The given <code>Type</code>.
	 * @return The <code>IType</code> bound to the given type.
	 */
	public IType getType(Type type) {
		return getType(type.resolveBinding());
	}

	/**
	 * @param binding - The given <code>ITypeBinding</code>. 
	 * @return The <code>IType</code> bound to the given type.
	 */
	public IType getType(ITypeBinding binding) {
		return (IType) binding.getJavaElement();
	}

	/**
	 * Handles visiting the given node in subclasses.
	 * 
	 * @param node - The node.
	 * @return True if further processing should occur, false otherwise.
	 */
	protected abstract boolean process(DeclarationInfoProvider node);

	/**
	 * Called to run the visitor.
	 * 
	 * @param cu - The compilation unit to visit.
	 * @param cache - The cache of relationships.
	 */
	protected abstract void run(CompilationUnit cu, RelationshipCache cache);

	/**
	 * @see org.eclipse.jdt.core.dom.ASTVisitor#visit(org.eclipse.jdt.core.dom.MethodDeclaration)
	 * 
	 * @author Gene Wang
	 */
	public final boolean visit(MethodDeclaration node) {
		//Ignore all static children
		if ((node.getModifiers() & STATIC) != 0) return false;  
		
		_inConstructor = node.isConstructor();
		_methodDeclaration = node;
		
		// retrieve the simple names of local variables and the elements they
		// represent
		for (SingleVariableDeclaration varDec : (AbstractList<SingleVariableDeclaration>) node
				.parameters()) {
			SimpleName parameter = varDec.getName();
			_parameters.add(parameter.getIdentifier());
			_parameterVars.add((ILocalVariable) parameter.resolveBinding()
					.getJavaElement());
		}

		return true;
	}
	
    /**
     * Ensures no recognizers attempt to use initializer blocks for relationships
     * 
     * @author Gene Wang
     */
	public boolean visit(Initializer node) {
        return false;
	}
	
	/**
	 * Ensures no recognizers attempt to use static fields for relationships
	 * 
	 * @author Gene Wang
	 */
	public boolean visit(SingleVariableDeclaration svd) {
		if(isField(svd.getName()) &&
		   (svd.getModifiers() & STATIC) != 0) return false;
		
		return true;
	}

	/**
	 * @see org.eclipse.jdt.core.dom.ASTVisitor#visit(org.eclipse.jdt.core.dom.EnumDeclaration)
	 */
	public final boolean visit(EnumDeclaration node) {
		return visit(DeclarationInfoProvider.getInfoProvider(node));
	}
	
	/**
	 * @see org.eclipse.jdt.core.dom.ASTVisitor#visit(org.eclipse.jdt.core.dom.TypeDeclaration)
	 */
	public final boolean visit(TypeDeclaration node) {
		return visit(DeclarationInfoProvider.getInfoProvider(node));
	}
	
	/**
	 * Processes type info as an <code>AbstractTypeDeclaration</code> node is
	 * visited. This is necessary to get around the problem caused by enums
	 * having a different node from classes and interfaces. 
	 * 
	 * @param node - The <code>DeclarationInfoProvider</code> being visited.
	 * @return <code>true</code> if the children of this node should be visited,
	 * and <code>false</code> if the children of this node should be skipped.
	 */
	public final boolean visit(DeclarationInfoProvider node) {
		_typeStack.push(node);
		return process(node);
	}
	
	/**
	 * @param var - The <code>ILocalVariable</code> to check
	 * @return True if the name is bound to a local variable, false otherwise
	 */
	private boolean isLocalVariable(ILocalVariable var) {
		return !isParameter(var);
	}

	/**
	 * @param var - The <code>ILocalVariable</code> to check
	 * @return True if the name is bound to a parameter, false otherwise
	 */
	private boolean isParameter(ILocalVariable var) {
		for (ILocalVariable param : _parameterVars) {
			if (param.equals(var)) { return true; }
		}

		return false;
	}

	/**
	 * @param name - The <code>Name</code> to check
	 * @return True if the name is bound to a field, false otherwise
	 */
	protected boolean isField(Name name) {
		IBinding binding = name.resolveBinding();
		if (binding == null) return false;
		
		return binding.getJavaElement().getElementType() == FIELD;
	}

	/**
	 * @param name - The <code>Name</code> to check
	 * @return True if the name is bound to a parameter, false otherwise
	 */
	protected boolean isParameter(Name name) {
		IBinding binding = name.resolveBinding();
		if (binding == null) return false;
		
		IJavaElement element = binding.getJavaElement();

		//element is null when binding is for a primitive or void,
		//in which case, we would not have any relationships.
		if(element == null) return false;
		
		if (!(element.getElementType() == LOCAL_VARIABLE)) return false;
		return isParameter((ILocalVariable) element);
	}

	/**
	 * @param name - The <code>Name</code> to check
	 * @return True if the name is bound to a local variable, false otherwise
	 */
	protected boolean isLocalVariable(Name name) {
		IBinding binding = name.resolveBinding();
		if (binding == null) return false;

		IJavaElement element = binding.getJavaElement();
		if (element == null) return false;
		
		if (!(element.getElementType() == LOCAL_VARIABLE)) return false;
		return isLocalVariable((ILocalVariable) element);
	}
	
	/**
	 * @param name - The name of the field
	 * @return True if the field is already declared, false otherwise
	 */
	protected boolean isFieldDeclared(String name) {
		return getFieldNames().contains(name);
	}
	
	/**
	 * Performs import organization on the given <code>ICompilationUnit</code>.
	 * 
	 * @param cu - The <code>ICompilationUnit</code>.
	 */
	protected void organizeImports(ICompilationUnit cu) {
		new OrganizeImportsAction(
				DiagramEditor.getActiveEditor().getSite()).runOnMultiple(
						new ICompilationUnit[] { cu });
	}
	
	/**
	 * Performs import organization on the given <code>IType</code>'s
	 * compilation unit.
	 * 
	 * @param type - The <code>IType</code>.
	 */
	protected void organizeImports(IType type) {
		organizeImports(type.getCompilationUnit());
	}
	
	/**
	 * @return true if we're in a constructor's node, false otherwise.
	 */
	protected boolean inConstructor() {
		return _inConstructor;
	}

	/**
	 * @return The <code>IType</code> corresponding to the current type being
	 * visited.
	 */
	protected IType getCurrentType() {
		AbstractTypeDeclaration typeDec = getCurrentTypeDeclaration();
		ITypeBinding type = typeDec.resolveBinding(); 
		return type!=null?(IType)type.getJavaElement():null;
	}
	
	/**
	 * @return A list of <code>String</code>s representing the names of
	 * local variables in the scope of the current method. 
	 */
	protected List<String> getLocalDeclarations() {
		return _locals;
	}
	
	/**
	 * @return A list of <code>String</code>s representing the names of
	 * parameters in the scope of the current method. 
	 */
	protected List<String> getParameterDeclarations() {
		return _parameters;
	}

	/**
	 * @return The <code>MethodDeclaration</code> of the method most recently
	 * visited.
	 */
	protected MethodDeclaration getMethodDeclaration() {
		return _methodDeclaration;
	}
	
	/**
	 * @return The shared AST subtree matcher instance.
	 */
	protected ASTMatcher getMatcher() {
		return _matcher;
	}
	
	/**
	 * @return The type info of the currently visited type.
	 */
	protected DeclarationInfoProvider getCurrentTypeInfo() {
		return _typeStack.peek();
	}
	
	/**
	 * @return The body declarations of the currently visited type.
	 */
	protected List<BodyDeclaration> getBodyDeclarations() {
		return (AbstractList<BodyDeclaration>)
		getCurrentTypeInfo().bodyDeclarations();
	}

	/**
	 * @return The <code>AbstractTypeDeclaration</code> corresponding to the
	 * current type.
	 */
	protected AbstractTypeDeclaration getCurrentTypeDeclaration() {
		return getCurrentTypeInfo().getDeclaration();
	}
	
	/**
	 * @return A list of names of the fields in the compilation unit as String
	 * objects.
	 */
	protected List<String> getFieldNames() {
		List<String> fieldNames = new ArrayList<String>();
		
		for (DeclarationInfoProvider typeInfo : _typeStack) {
			fieldNames.addAll(
					DeclarationInfoProvider.getFieldNames(typeInfo.getFields()));
		}

		return fieldNames;
	}

	/**
	 * @return A list of all the <code>FieldDeclaration</code> nodes in the
	 * current type. 
	 */
	protected List<FieldDeclaration> getFields() {
		return getCurrentTypeInfo().getFields();
	}
}
