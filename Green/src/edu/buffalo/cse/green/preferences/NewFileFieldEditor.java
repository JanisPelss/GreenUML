package edu.buffalo.cse.green.preferences;

import java.io.File;

import org.eclipse.jface.preference.FileFieldEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;


public class NewFileFieldEditor extends FileFieldEditor {
	
	protected NewFileFieldEditor() {
		super();
    }

    public NewFileFieldEditor(String name, String labelText, Composite parent) {
        super(name, labelText, false, parent);
    }

    public NewFileFieldEditor(String name, String labelText, boolean enforceAbsolute, Composite parent) {
    	super(name, labelText, enforceAbsolute, VALIDATE_ON_FOCUS_LOST, parent);
    }

    public NewFileFieldEditor( String name, String labelText, boolean enforceAbsolute,
                               int validationStrategy, Composite parent ) {
        super(name, labelText, enforceAbsolute, validationStrategy, parent);
    }
    
    
    // Mostly does the same as the overriden method, but calls getNewFile() instead of getFile().    
    @Override
	protected String changePressed() {
        String startingDir = new File(getTextControl().getText()).getPath();
        
        File d = getNewFile(startingDir);
        if (d == null) {
			return null;
		}

        return d.getAbsolutePath();
    }
    
    
    // Mostly copies the functionality of FileFieldEditor.getFile() (cannot be overriden since it is private).
    private File getNewFile(String startingDirectory) {

        FileDialog dialog = new FileDialog(getShell(), SWT.SAVE | SWT.SHEET);
        dialog.setOverwrite(false);
        dialog.setText("Log File");
        dialog.setOverwrite(true);
        dialog.setFileName(startingDirectory);
        
        String file = dialog.open();
        if (file != null) {
            file = file.trim();
            if (file.length() > 0) {
				return new File(file);
			}
        }

        return null;
    }
    
    
    @Override
    public boolean checkState() {
    	return true;
    }

}
