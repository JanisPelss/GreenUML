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

import static org.eclipse.jdt.core.dom.ASTNode.VARIABLE_DECLARATION_STATEMENT;
import static org.eclipse.jdt.core.dom.ParameterizedType.TYPE_ARGUMENTS_PROPERTY;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTMatcher;
import org.eclipse.jdt.core.dom.Assignment;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.BodyDeclaration;
import org.eclipse.jdt.core.dom.ClassInstanceCreation;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.ExpressionStatement;
import org.eclipse.jdt.core.dom.FieldAccess;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.ImportDeclaration;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.Modifier;
import org.eclipse.jdt.core.dom.Name;
import org.eclipse.jdt.core.dom.ParameterizedType;
import org.eclipse.jdt.core.dom.QualifiedName;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.Statement;
import org.eclipse.jdt.core.dom.ThisExpression;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jdt.core.dom.VariableDeclarationStatement;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.text.edits.TextEdit;

import edu.buffalo.cse.green.GreenException;
import edu.buffalo.cse.green.PlugIn;
import edu.buffalo.cse.green.dialogs.wizards.ChooseTypeWizard;
import edu.buffalo.cse.green.editor.model.RelationshipModel;
import edu.buffalo.cse.green.util.IModifiableBuffer;

/**
 * Contains code common to all relationship generators
 * 
 * @author bcmartin
 */
public abstract class RelationshipGenerator extends RelationshipVisitor {
	private IType _sourceType;

	private IType _actualTargetType;
	
	private IType _declaredTargetType;

	protected int _cardinality = 1;

	public static final String LIST = "java.util.List";
	public static final String ARRAYLIST = "java.util.ArrayList"; 

	/**
	 * Adds the desired import, if necessary.
	 * 
	 * @param type - The element to import.
	 * @return false if the element cannot be imported because the element's
	 * simple name is already used; true otherwise.
	 */
	protected boolean addImport(IType type) {
		if (getCurrentType().getParent().equals(type.getParent())) {
			return true;
		}

		// import not necessary if both are children of CUs within the same
		// package
		if (getSourceType().getCompilationUnit().equals(
				getSourceType().getParent())
				&& getTargetType().getOpenable().equals(
						getTargetType().getParent())
				&& getSourceType().getPackageFragment().equals(
						getTargetType().getPackageFragment())) {
			return true;
		}

		
		
		return addImport(createQualifiedName(
				type.getFullyQualifiedName().replace('$', '.')));
	}

	/**
	 * Adds the desired import, if possible.
	 * 
	 * @param qualifiedName - The desired <code>QualifiedName</code>.
	 * @return false if the element cannot be imported because the element's
	 * simple name is already used; true otherwise.
	 */
	private boolean addImport(QualifiedName qualifiedName) {
		List<ImportDeclaration> imports = (AbstractList<ImportDeclaration>) getCompilationUnit().imports();

		// ensure the import is necessary and non-conflicting
		for (ImportDeclaration declaration : imports) {
			// do not contend with on-demand imports
			if (declaration.isOnDemand()) {
				continue;
			}

			// see if the import has already been added
			if (new ASTMatcher().match(
					(QualifiedName) declaration.getName(), qualifiedName)) {
				return true;
			}

			/* see if the element referred to by the declaration has the same
			 * simple name as the import we are trying to create (problem)
			 */
			
			if (declaration.resolveBinding() != null) { // not recently added
				String elementName =
					declaration.resolveBinding().getJavaElement().getElementName();
			
				if (elementName.equals(qualifiedName.getName().toString())) {
					return false;
				}
			}
		}

		// create the import declaration
		ImportDeclaration i = getAST().newImportDeclaration();
		i.setName(qualifiedName);

		// add the import declaration
		imports.add(i);

		return true;
	}
	
	/**
	 * @param fullyQualifiedString - The String.
	 * @return A qualified name representing the given fully qualified String. 
	 */
	protected QualifiedName createQualifiedName(String fullyQualifiedString) {
		AST ast = getAST();

		if (fullyQualifiedString.indexOf(".") == -1) {
			GreenException.illegalOperation("String must be fully qualified");
		}

		int prevIndex = fullyQualifiedString.indexOf(".");
		String qual = fullyQualifiedString.substring(0, prevIndex);
		Name qualifier = ast.newSimpleName(qual);

		while (true) {
			qual = fullyQualifiedString.substring(prevIndex + 1);
			int index = qual.indexOf(".");

			if (index == -1) {
				break;
			}

			index += prevIndex;
			qual = fullyQualifiedString.substring(prevIndex + 1, index + 1);
			qualifier = ast
					.newQualifiedName(qualifier, ast.newSimpleName(qual));
			prevIndex = index + 1;
		}

		return ast.newQualifiedName(qualifier, ast.newSimpleName(qual));
	}

	/**
	 * @see edu.buffalo.cse.green.relationships.RelationshipVisitor#run(org.eclipse.jdt.core.dom.CompilationUnit, edu.buffalo.cse.green.relationships.RelationshipCache)
	 */
	protected void run(CompilationUnit cu, RelationshipCache cache) {
		try {
			if (_sourceType.isBinary()) {
				GreenException.illegalOperation(
						GreenException.GRERR_REL_SOURCE_BINARY);
			}

			ICompilationUnit iCU = (ICompilationUnit) getSourceType()
					.getAncestor(IJavaElement.COMPILATION_UNIT);

			cu.recordModifications();
			cu.accept(this);

			IDocument sourceDoc = new IModifiableBuffer(iCU.getBuffer());
			TextEdit textEdit = cu.rewrite(sourceDoc, null);
			textEdit.apply(sourceDoc);

			if (!iCU.isConsistent()) {
				iCU.save(PlugIn.getEmptyProgressMonitor(), true);
			}
			
			organizeImports(_sourceType);
		} catch (BadLocationException e) {
			e.printStackTrace();
		} catch (JavaModelException e) {
			e.printStackTrace();
		}
	}

	/**
	 * @return The source type of the generated relationship.
	 */
	protected IType getSourceType() {
		return _sourceType;
	}

	/**
	 * @return The target type of the generated relationship.
	 */
	protected IType getTargetType() {
		return _declaredTargetType;
	}

	/**
	 * Convenience method for removing the last segment of a name.
	 * 
	 * @param name - The name.
	 * @return The name's qualifier (the part other than the simple name).
	 */
	public static Name getQualifier(Name name) {
		if (name.isSimpleName()) { return null; }
		QualifiedName qualName = (QualifiedName) name;
		return qualName.getQualifier();
	}

	/**
	 * Sets the model used for this generator.
	 * 
	 * @param rModel - The model.
	 * @return True if successful, false otherwise.
	 */
	public boolean setRelationship(RelationshipModel rModel) {
		_sourceType = rModel.getSourceType();
		_declaredTargetType = rModel.getTargetType();

		if (needChooseTypeDialog()) {
			ChooseTypeWizard wizard = new ChooseTypeWizard(getTargetType());
			WizardDialog dialog = new WizardDialog(PlugIn.getDefaultShell(),
					wizard);
			dialog.setMinimumPageSize(300, 500);
			dialog.create();
			int res = dialog.open();
			
			if (res == WizardDialog.CANCEL) {
				return false;
			}
			
			setActualTargetType(wizard.getSelectedType());
		}
		
		return true;
	}

	/**
	 * @return true if a choose type dialog is needed, false otherwise.
	 */
	protected abstract boolean needChooseTypeDialog();

	/**
	 * @return true if a default constructor is needed, false otherwise.
	 */	
	protected abstract boolean needConstructor();
	
	/**
	 * @return true if the relationship can potentially be a collection.
	 */
	public boolean supportsCardinality() {
		return false;
	}

	/**
	 * Sets the cardinality of the relationship.
	 * 
	 * @param cardinality - The cardinality.
	 */
	public void setCardinality(int cardinality) {
		if (!supportsCardinality()) {
			GreenException.illegalOperation("Cannot set the cardinality "
					+ "of a non-cardinal relationship type");
		}

		_cardinality = cardinality;
	}

	/**
	 * Sets the source type of the relationship.
	 * 
	 * @param selectedType - The type.
	 */
	public void setSourceType(IType selectedType) {
		_sourceType = selectedType;
	}

	/**
	 * Sets the target type of the relationship.
	 * 
	 * @param selectedType - The type.
	 */
	public void setTargetType(IType selectedType) {
		_declaredTargetType = selectedType;
	}

	/**
	 * @param type - The type.
	 * @return A reference to the given <code>IType</code>. 
	 */
	protected Type createTypeReference(IType type) {
		boolean success = addImport(type);
		
		if (success) {
			return getAST().newSimpleType(
					getAST().newSimpleName(type.getElementName()));
		} else {
			return getAST().newSimpleType(createQualifiedName(
					type.getFullyQualifiedName()));
		}
	}

	/**
	 * @param name - The type's name.
	 * @return A reference to the given name.
	 */
	private Type createTypeReference(QualifiedName name) {
		// create a type (for reference to the target type)
		AST ast = getAST();

		// add an import for the desired Java element if necessary
		boolean success = addImport(name);

		if (success) {
			// type can be referred to by its simple name
			return ast.newSimpleType(
					ast.newSimpleName(name.getName().toString()));
		} else {
			// type must use a qualified name
			return ast.newSimpleType(name);
		}
	}
	
	/**
	 * Called to handle the traversal of a block.
	 * 
	 * @param node - The node to traverse.
	 * @return True if processing should continue, false otherwise.
	 */
	protected abstract boolean process(Block node);

	/**
	 * @return True if blocks should be traversed, false otherwise.
	 */
	protected abstract boolean doVisitBlocks();

	/**
	 * @see org.eclipse.jdt.core.dom.ASTVisitor#visit(org.eclipse.jdt.core.dom.Block)
	 */
	public final boolean visit(Block node) {
		if (doVisitBlocks()) {
			for (Statement stmt : (AbstractList<Statement>) node
					.statements()) {
				if (stmt.getNodeType() == VARIABLE_DECLARATION_STATEMENT) {
					VariableDeclarationStatement vds = (VariableDeclarationStatement) stmt;
					List<VariableDeclarationFragment> vdfs = (AbstractList<VariableDeclarationFragment>) vds
					.fragments();
					
					for (VariableDeclarationFragment vdf : vdfs) {
						getLocalDeclarations().add(vdf.getName().getIdentifier());
					}
				}
			}
			
			process(node);
		}

		return true;
	}

	/**
	 * @see org.eclipse.jdt.core.dom.ASTVisitor#endVisit(org.eclipse.jdt.core.dom.Block)
	 */
	public final void endVisit(Block node) {
		getLocalDeclarations().clear();
	}

	/**
	 * @param targetType - The declared type of the variable.
	 * @param name - The name of the variable.
	 * @param value - The value to assign to the variable.
	 * @return A variable declaration. 
	 */
	protected VariableDeclarationStatement createNormalVariableDeclaration(
			IType targetType,
			String name,
			Expression value) {
		return createVariableDeclaration(targetType, name, value, false);
	}

	/**
	 * @param targetType - The declared type of the variable.
	 * @param name - The name of the variable.
	 * @return A generic variable declaration.
	 */
	protected VariableDeclarationStatement createGenericVariableDeclaration(IType targetType, String name) {
		addImport(createQualifiedName(ARRAYLIST));
		
		ClassInstanceCreation cic = getAST().newClassInstanceCreation();
		cic.setType(createParameterizedTypeReference(ARRAYLIST,
				createTypeReference(targetType)));
		
		return createVariableDeclaration(targetType, name, cic, true);
	}

	/**
	 * @param collection - The qualified name of the collection's type.
	 * @param targetType - The type parameter.
	 * @return A reference to the parameterized type.
	 */
	protected Type createParameterizedTypeReference(String collection,
			Type targetType) {
		// set the parameter
		ParameterizedType pType = getAST().newParameterizedType(
				createTypeReference(createQualifiedName(collection)));
		
		// set the collection type
		pType.setType(createTypeReference(createQualifiedName(collection)));

		List<Type> types = (AbstractList<Type>) (List) pType.getStructuralProperty(TYPE_ARGUMENTS_PROPERTY);
		types.add(targetType);
		
		return pType;
	}

	/**
	 * @param targetType - The variable's declared type.
	 * @param name - The name of the variable.
	 * @param value - The value to assign to the variable.
	 * @param isGeneric - Whether or not the variable represents a generic
	 * variable declaration.
	 * @return A variable declaration.
	 */
	private VariableDeclarationStatement createVariableDeclaration(
			IType targetType,
			String name,
			Expression value,
			boolean isGeneric) {
		VariableDeclarationFragment vdf =
			getAST().newVariableDeclarationFragment();
		vdf.setInitializer(value);
		vdf.setName(getAST().newSimpleName(name));
		
		VariableDeclarationStatement vds = getAST()
		.newVariableDeclarationStatement(vdf);
		
		if (isGeneric) {
			vds.setType(createParameterizedTypeReference(LIST,
					createTypeReference(targetType)));
		} else {
			vds.setType(createTypeReference(targetType));
		}
		
		return vds;
	}
	
	/**
	 * @param type - The type.
	 * @return A representation of an instantiation of the type.
	 */
	protected ClassInstanceCreation createInvocation(Type type) {
		ClassInstanceCreation cic = getAST().newClassInstanceCreation();
		cic.setType(type);

		return cic;
	}
	
	/**
	 * @param type - The type.
	 * @return A representation of a parameterized instnatiation of the type.
	 */
	protected ClassInstanceCreation createParameterizedInvocation(Type type) {
		return createInvocation(createParameterizedTypeReference(
				ARRAYLIST, type));
	}
	

	/**
	 * @return The basic variable name used for this generator.
	 */
	protected String getBaseVariableName() {
		return getTargetType().getElementName().toLowerCase().charAt(0)
				+ getTargetType().getElementName().substring(1);
	}

	/**
	 * @return A list of all the used variable names.
	 */
	private List<String> generateVariableList() {
		List<String> vars = new ArrayList<String>();
		vars.addAll(getParameterDeclarations());
		vars.addAll(getLocalDeclarations());
		vars.addAll(getFieldNames());
		
		return vars;
	}

	/**
	 * @param base - The base variable name.
	 * @return A unique variable name generated using the base.
	 */
	protected String generateVariableName(String base) {
		List<String> variables = generateVariableList();
		String varName = base;
		int x = 2;

		while (variables.contains(varName)) {
			varName = base + x;
			x++;
		}

		return varName;
	}

	/**
	 * Adds a declaration for the given field
	 * 
	 * @param type - The declared type of the field
	 * @param name - The name of the field
	 * @return False if the field already exists, true otherwise
	 */
	protected boolean addField(Type type, String name) {
		if (isFieldDeclared(name)) return false;
		
		List<BodyDeclaration> decs =
			(AbstractList<BodyDeclaration>)
			getCurrentTypeDeclaration().bodyDeclarations();
		VariableDeclarationFragment vdf = getAST()
				.newVariableDeclarationFragment();
		FieldDeclaration dec = getAST().newFieldDeclaration(vdf);
		List<Modifier> modifiers = (AbstractList<Modifier>) dec.modifiers();
		modifiers.add(getAST().newModifier(
				Modifier.ModifierKeyword.PRIVATE_KEYWORD));
		
		dec.setType(type);
		vdf.setName(getAST().newSimpleName(name));

		decs.add(0, dec);
		getFields().add(0, dec);
		
		return true;
	}

	/**
	 * Creates a new field assignment and adds a field declaration if needed
	 * 
	 * @param name - The name of the field
	 * @param value - The value on the RHS of the assignment
	 * @return
	 */
	protected Assignment createAssignment(String name, Expression value) {
		Assignment assignment = getAST().newAssignment();
		Expression lhs;
		
		SimpleName field = getAST().newSimpleName(name);
		
		if (getParameterDeclarations().contains(name)) {
			FieldAccess exp = getAST().newFieldAccess();
			ThisExpression thisExp = getAST().newThisExpression();
			exp.setExpression(thisExp);
			exp.setName(field);
			lhs = exp;
		} else {
			lhs = field;
		}
		
		assignment.setLeftHandSide(lhs);
		assignment.setRightHandSide(value);
		
		return assignment;
	}
	
	/**
	 * @return True if the type declaration matches the source type, false
	 * otherwise.
	 */
	protected boolean correctTypeToGenerate() {
		return getSourceType().equals(getCurrentType());
	}
	
	/**
	 * Adds a parameterized parameter to the given method.
	 * 
	 * @param method - The method.
	 * @param type - The type of the parameter.
	 * @param paramName - The name of the parameter.
	 */
	protected void addParameterizedParameter(MethodDeclaration method,
			IType type, String paramName) {
		addParameter(method, createParameterizedTypeReference(LIST,
				createTypeReference(type)),
				paramName);
	}
	
	/**
	 * Adds a parameter to the given method.
	 * 
	 * @param method - The method.
	 * @param type - The type of the parameter.
	 * @param paramName - The name of the parameter.
	 */
	protected void addNormalParameter(MethodDeclaration method, IType type,
			String paramName) {
		addParameter(method, createTypeReference(type), paramName);
	}
	
	/**
	 * Adds a parameter to the given method.
	 * 
	 * @param method - The method.
	 * @param type - The type of the parameter.
	 * @param paramName - The name of the parameter.
	 */
	private void addParameter(MethodDeclaration method, Type type,
			String paramName) {
		List<SingleVariableDeclaration> params = (AbstractList<SingleVariableDeclaration>) method.parameters();
		SingleVariableDeclaration svd = getAST().newSingleVariableDeclaration();
		svd.setType(type);
		svd.setName(getAST().newSimpleName(paramName));
		params.add(svd);
	}
	
	/**
	 * Sets the actual type of the target of the relationship (as opposed to the
	 * declared type).
	 * 
	 * @param type - The actual type.
	 */
	private void setActualTargetType(IType type) {
		_actualTargetType = type;
	}
	
	/**
	 * @return The actual type of the target of the relationship.
	 */
	protected IType getActualTargetType() {
		if (_actualTargetType == null) {
			return _declaredTargetType;
		} else {
			return _actualTargetType;
		}
	}
	
	/**
	 * @param text - The text.
	 * @return A simple name representing the text.
	 */
	protected SimpleName name(String text) {
		return getAST().newSimpleName(text);
	}
	
	/**
	 * @param variable - The variable to invoke the method on.
	 * @param method - The method to invoke.
	 * @param arguments - The arguments to pass to the method.
	 * @return A method invocation statement.
	 */
	protected ExpressionStatement createMethodInvocation(String variable,
			String method, List<Expression> arguments) {
		MethodInvocation m = getAST().newMethodInvocation();
		m.setExpression(name(variable));
		m.setName(name(method));
		
		List<Expression> args =
			(AbstractList<Expression>) (List) m.arguments();
		args.addAll(arguments);
		
		return getAST().newExpressionStatement(m);
	}
}