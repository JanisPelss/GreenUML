package edu.buffalo.cse.green.preferences;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import edu.buffalo.cse.green.PlugIn;
import edu.buffalo.cse.green.editor.DiagramEditor;

import static edu.buffalo.cse.green.preferences.PreferenceInitializer.P_LOG_TO_STD;
import static edu.buffalo.cse.green.preferences.PreferenceInitializer.P_LOG_TO_FILE;
import static edu.buffalo.cse.green.preferences.PreferenceInitializer.P_LOG_FILE_NAME;


public class GreenPreferencePageLogging
	extends FieldEditorPreferencePage
	implements IWorkbenchPreferencePage {
	
	private BooleanFieldEditor _editorLogToStd, _editorLogToFile;
	private NewFileFieldEditor _editorFileName;
	
	
	public GreenPreferencePageLogging() {
		super(GRID);
		setPreferenceStore(PlugIn.getDefault().getPreferenceStore());
	}
	
	/**
	 * @see org.eclipse.jface.preference.FieldEditorPreferencePage#createFieldEditors()
	 */
	public void createFieldEditors() {
		_editorLogToStd = new BooleanFieldEditor(P_LOG_TO_STD, "Log to stdout and stderr", getFieldEditorParent());
		_editorLogToFile = new BooleanFieldEditor(P_LOG_TO_FILE, "Log to file", getFieldEditorParent());
		_editorFileName = new NewFileFieldEditor(P_LOG_FILE_NAME, "Log file name", getFieldEditorParent());
		
		_editorLogToFile.setPropertyChangeListener(new IPropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent e) {
				//_editorFileName.setEnabled(_editorLogToFile.getBooleanValue(), getFieldEditorParent());
				String __s__ = "> > >";
				try { java.io.File f = new java.io.File("/home/student/Documents/#####.txt"); java.io.BufferedWriter w = new java.io.BufferedWriter(new java.io.FileWriter(f, true)); w.write(__s__); w.newLine(); w.flush(); w.close(); } catch (java.io.IOException ex) {}
				System.err.println("dfgsdfjkldfjkldfg");
			}
		});
		
		
		addField(_editorLogToStd);
		addField(_editorLogToFile);
		addField(_editorFileName);
		
		
		
		adjustGridLayout();
	}

	/**
	 * @see org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
	 */
	public void init(IWorkbench workbench) {}

	/**
	 * @see org.eclipse.jface.preference.IPreferencePage#performOk()
	 */
	public boolean performOk() {
		boolean ok = super.performOk();
		
		for (DiagramEditor editor : DiagramEditor.getEditors()) {
			editor.refresh();
		}
		
		ok &= checkPrefs();
		return ok;
	}
	
	
	@Override
	public boolean okToLeave() {
		return checkPrefs();
	}
	
	
	// We are intentionally not overriding isValid(). 
	private boolean checkPrefs() {
		if ( _editorLogToFile.getBooleanValue() &&
		     _editorFileName.getStringValue().length() == 0) {
			setErrorMessage("Log file name is required.");
			return false;
		}
		else {
			setErrorMessage(null);
			return true;
		}
	}

}
