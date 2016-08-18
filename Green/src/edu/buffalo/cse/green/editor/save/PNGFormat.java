/**
 * 
 */
package edu.buffalo.cse.green.editor.save;

import static edu.buffalo.cse.green.GreenException.GRERR_FILE_NOT_FOUND;

import java.io.File;
import java.io.IOException;

import org.eclipse.draw2d.IFigure;

import edu.buffalo.cse.green.GreenException;
import edu.buffalo.cse.green.editor.DiagramEditor;
import edu.buffalo.cse.green.util.ImageWriterFile;
import edu.buffalo.cse.green.util.ImageWriterUtil;

/**
 * @author dan
 *
 */
public class PNGFormat implements ISaveFormat {

	/* (non-Javadoc)
	 * @see edu.buffalo.cse.green.editor.save.ISaveFormat#getDescription()
	 */
	public String getDescription ( ) {
		return "PNG Image";
	}

	/* (non-Javadoc)
	 * @see edu.buffalo.cse.green.editor.save.ISaveFormat#getExtension()
	 */
	public String getExtension ( ) {
		return "png";
	}

	/* (non-Javadoc)
	 * @see edu.buffalo.cse.green.editor.save.ISaveFormat#saveInformation(edu.buffalo.cse.green.editor.DiagramEditor, java.lang.String, org.eclipse.draw2d.IFigure)
	 */
	public void saveInformation ( DiagramEditor editor, String fileName,
			IFigure figure ) {
		try {
			ImageWriterFile writer = new ImageWriterFile(
					new File(fileName).getCanonicalPath(), ImageWriterUtil.FORMAT_PNG);
			writer.saveFigure(figure);
		} catch (IOException iOE) {
			GreenException.fileException(
					GRERR_FILE_NOT_FOUND);
		}
	}

}
