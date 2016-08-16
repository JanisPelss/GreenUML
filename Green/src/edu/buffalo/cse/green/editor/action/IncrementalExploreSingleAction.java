package edu.buffalo.cse.green.editor.action;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.IClassFile;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.JavaModelException;

import edu.buffalo.cse.green.PlugIn;
import edu.buffalo.cse.green.editor.model.TypeModel;
import edu.buffalo.cse.green.editor.model.commands.IncrementalExploreCommand;
import edu.buffalo.cse.green.relationships.RelationshipGroup;

/**
 * mock-up
 * @author dan
 */
public class IncrementalExploreSingleAction extends ContextAction {
	
	private Class _partClass;
	//private static int KEYCODE = 0;
	
	/**
	 * mock-up
	 */
	public IncrementalExploreSingleAction ( Class partClass ) {
		super( partClass );
		_partClass = partClass;
		setText(getLabel());
		//setAccelerator( ('0' + ( ( ++KEYCODE ) % 10 ) ) );
	}
	
	/**
	 * @see edu.buffalo.cse.green.editor.action.ContextAction#getLabel()
	 */
	public String getLabel() {
		RelationshipGroup group = PlugIn.getRelationshipGroup(_partClass);

		return ( group.getSubtype() != null ? group.getSubtype() + " " : "" ) + group.getName();
	}

	/**
	 * @see edu.buffalo.cse.green.editor.action.ContextAction#doRun()
	 */
	@Override
	protected void doRun ( ) throws JavaModelException {
		List<RelationshipGroup> relationships =
			new ArrayList<RelationshipGroup>();
		relationships.add(PlugIn.getRelationshipGroup(_partClass));
		getEditor().execute(new IncrementalExploreCommand(getEditor(),
				(TypeModel) _model, relationships, false));
	}

	/**
	 * @see edu.buffalo.cse.green.editor.action.ContextAction#getPath()
	 */
	public Submenu getPath() {
		return Submenu.IncrExplore;
	}

	/**
	 * @see edu.buffalo.cse.green.editor.action.ContextAction#getSupportedModels()
	 */
	protected int getSupportedModels() {
		return CM_TYPE;
	}
	
	/**
	 * @see edu.buffalo.cse.green.editor.action.ContextAction#isAvailableForBinary()
	 */
	public boolean isAvailableForBinary() {
		IClassFile classFile = (IClassFile) _element
				.getAncestor(IJavaElement.CLASS_FILE);

		try {
			return (classFile.getSourceRange() != null);
		} catch (JavaModelException e) {
			// no source code attached
			return false;
		}
	}

	/**
	 * @see edu.buffalo.cse.green.editor.action.ContextAction#isEnabled()
	 */
	public boolean isEnabled() {
		return true;
	}

}
