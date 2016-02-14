/*
 * Created on Jan 2, 2004
 */
package org.vorthmann.zome.export.java2d;

import java.awt.geom.GeneralPath;
import java.awt.geom.PathIterator;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.Iterator;
import org.vorthmann.zome.export.java2d.Java2dSnapshot.Polygon;


public class QuartzXMLExporter {

	private PrintWriter output;
	
	private Java2dSnapshot mSnapshot;
	
	private float height;

	
	public QuartzXMLExporter( Java2dSnapshot snapshot )
	{
		mSnapshot = snapshot;
	}
	
	public void exportQuartzXML( Writer writer )
	{
		output = new PrintWriter( writer );
		height = (float) mSnapshot .getRect() .getHeight();
		float width = (float) mSnapshot .getRect() .getWidth();
		output .println( "<?xml version='1.0'?>" );
		output .println( "<quartz>" );
		
		output .println();
		output .println( "  <beginPage height='" + height
									+ "' width='" + width + "'/>" );

		float[] rgb = mSnapshot .getBackgroundColor() .getRGBColorComponents( null );
		setRGBFillColor( rgb[0], rgb[1], rgb[2] );
		beginPath();
		output .println( "    <addRect x='0' y='0' width='" + width
								+ "' height='" + height + "'/>" );
		closePath();
		fillPath();
		
		output .println();
		output .println( "    <setLineWidth width='" + mSnapshot .getStrokeWidth() + "'/>" );
		
		for ( Iterator<Polygon> paths = mSnapshot .getPolygons(); paths .hasNext(); ){
			Polygon polygon = paths .next();
			renderPath( polygon .getPath() );
			rgb = polygon .getColor() .getRGBColorComponents( null );
			setRGBFillColor( rgb[0], rgb[1], rgb[2] );
			fillPath();
			renderPath( polygon .getPath() );
			strokePath();
		}

		output .println();
		output .println( "  <endPage/>" );
		
		output .println( "</quartz>" );
		output .flush();
		output .close();
	}

	public void renderPath( GeneralPath path )
	{
		beginPath();
		PathIterator segments = path .getPathIterator( null );
		while ( ! segments .isDone() ){
			float[] coords = new float[6];
			int step = segments .currentSegment( coords );
			switch ( step ) {
			
			case PathIterator.SEG_MOVETO :
				moveToPoint( coords[0], height - coords[1] );
				break;
				
			case PathIterator.SEG_LINETO :
				addLineToPoint( coords[0], height - coords[1] );
				break;
				
			case PathIterator.SEG_CLOSE :
				closePath();
				break;
				
			default :
				break;
			}
			segments .next();
		}
	}


	// ---------- MIMIC NATIVE QUARTZ METHODS 

	private void setRGBFillColor( float r, float g, float b )
	{
		output .println();
		output .println();
		output .print( "    <setRGBFillColor r=\"" );
		output .print( r + "\" g=\"" );
		output .print( g + "\" b=\"" );
		output .println( b + "\"/>" );
	}

	private void beginPath()
	{
		output .println( "    <beginPath/>" );
	}

	private void moveToPoint( float x, float y )
	{
		output .print( "        <moveToPoint " );
		output .println( " x=\"" + x + "\" y=\"" + y + "\"/>" );
	}

	private void addLineToPoint( float x, float y )
	{
		output .print( "        <addLineToPoint " );
		output .println( " x=\"" + x + "\" y=\"" + y + "\"/>" );
	}

	private void closePath()
	{
		output .println( "    <closePath/>" );
	}

	private void fillPath()
	{
		output .println( "    <fillPath/>" );
	}

	private void strokePath()
	{
		output .println( "    <strokePath/>" );
	}
	
}
