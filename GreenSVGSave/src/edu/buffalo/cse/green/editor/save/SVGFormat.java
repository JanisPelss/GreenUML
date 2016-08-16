package edu.buffalo.cse.green.editor.save;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;

import org.apache.batik.svggen.SVGGraphics2DIOException;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gmf.runtime.draw2d.ui.render.awt.internal.svg.export.GraphicsSVG;

import edu.buffalo.cse.green.editor.DiagramEditor;

/**
 * @author dan
 *
 */
public class SVGFormat implements ISaveFormat {

	/* (non-Javadoc)
	 * @see edu.buffalo.cse.green.editor.save.ISaveFormat#getDescription()
	 */
	public String getDescription ( ) {
		return "SVG Vector Graphic";
	}

	/* (non-Javadoc)
	 * @see edu.buffalo.cse.green.editor.save.ISaveFormat#getExtension()
	 */
	public String getExtension ( ) {
		return "svg";
	}

	/* (non-Javadoc)
	 * @see edu.buffalo.cse.green.editor.save.ISaveFormat#saveInformation(edu.buffalo.cse.green.editor.DiagramEditor, java.lang.String, org.eclipse.draw2d.IFigure)
	 */
	public void saveInformation ( DiagramEditor editor, String fileName,
			IFigure figure ) {
		Rectangle bounds = figure.getBounds( );
		GraphicsSVG g = GraphicsSVG.getInstance( bounds );
		figure.paint( g );
		try {
			g.getSVGGraphics2D().stream(new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileName))));
		} catch ( SVGGraphics2DIOException e ) {
			e.printStackTrace();
		} catch ( FileNotFoundException e ) {
			e.printStackTrace();
		} finally {
			g.dispose();
		}
	}

}
