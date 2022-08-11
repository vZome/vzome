/*
 * Created on Jan 2, 2004
 */
package com.vzome.core.exporters2d;

import java.awt.Color;
import java.awt.geom.GeneralPath;
import java.awt.geom.PathIterator;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.Writer;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;


/**
 * Exports a Java2dSnapshot to a character Writer.
 * Subclassed by SVGExporter and PDFExporter.
 * 
 * @author Scott Vorthmann
 *
 */
public abstract class SnapshotExporter {

	protected transient CountingPrintWriter output;
	
	protected transient float height, width;

	protected final NumberFormat RGB_FORMAT = NumberFormat .getNumberInstance( Locale .US );

	protected final NumberFormat XY_FORMAT = NumberFormat .getNumberInstance( Locale .US );
    

	public static class CountingPrintWriter extends PrintWriter
	{
		private int mTotal = 0;
		
		public CountingPrintWriter( Writer writer )
		{
			super( writer );
		}
		
        @Override
		public void write( char[] buf, int offset, int len )
		{
			super .write( buf, offset, len );
			mTotal += len;
		}
		
        @Override
		public void write( String str )
		{
			super .write( str );
			mTotal += str .length();
		}
		
		public int getBytesTotal()
		{
			return mTotal;
		}
	}
	
	public void includeFile( String rsrcName )
	{
		InputStream input = getClass() .getClassLoader()
									.getResourceAsStream( rsrcName );
		byte[] buf = new byte[1024];
		int num;
		try {
			while ( ( num = input .read( buf, 0, 1024 )) > 0 )
					output .write( new String( buf, 0, num ) );
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void export( Java2dSnapshot snapshot, Writer writer, boolean doOutlines, boolean monochrome, boolean showBackground )
	{
        XY_FORMAT .setGroupingUsed( false );
        XY_FORMAT .setMaximumFractionDigits( 2 );
        RGB_FORMAT .setMaximumFractionDigits( 3 );

		output = new CountingPrintWriter( writer );
		Rectangle2D rect = snapshot .getRect();
		this .height = (float) rect .getHeight();
		this .width = (float) rect .getWidth();

        List<Java2dSnapshot.LineSegment> lines = snapshot .getLines();

		float strokeWidth = snapshot .getStrokeWidth();
		if ( ! snapshot .isLineDrawing() && ! doOutlines )
		    strokeWidth = -1f;
		outputPrologue( snapshot .getRect(), strokeWidth );
		
		Color bgColor = snapshot .getBackgroundColor();
		if ( bgColor != null && showBackground )
			outputBackground( bgColor );
		
        if ( ! lines .isEmpty() )
            for ( Java2dSnapshot.LineSegment line : lines ) {
                outputLine( line, monochrome );
            }
        else
            for ( Java2dSnapshot.Polygon polygon : snapshot .getPolygons() ) {
                outputPolygon( polygon, strokeWidth > 0 );
            }

		outputPostlogue();
		
		output .flush();
		output .close();
	}

	/**
	 * @param height2
	 * @param width
	 * @param bgColor
	 */
	protected abstract void outputBackground( Color bgColor );

	protected abstract void outputPrologue( Rectangle2D rect, float strokeWidth );
	
	protected abstract void outputPostlogue();
	
    
    protected void outputLine( Java2dSnapshot.LineSegment line, boolean monochrome )
    {
        renderPath( line .getPath() );
        float[] rgb = line .getColor() .getRGBColorComponents( null );
        if ( ! monochrome )
            setRGBStrokeColor( rgb[0], rgb[1], rgb[2] );
        strokePath();
    }

	protected void outputPolygon( Java2dSnapshot.Polygon polygon, boolean doOutline )
	{
		renderPath( polygon .getPath() );
		float[] rgb = polygon .getColor() .getRGBColorComponents( null );
		setRGBFillColor( rgb[0], rgb[1], rgb[2] );
		fillPath();
		if ( doOutline ) {
	        renderPath( polygon .getPath() );
		    setBlackStrokeColor();
		    strokePath();
		}
	}
	
    protected void renderPath( GeneralPath path )
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

    protected void setBlackStrokeColor() {} // no-op for PDF and SVG

    protected abstract void setRGBStrokeColor( float r, float g, float b );

    protected abstract void setRGBFillColor( float r, float g, float b );

	protected abstract void beginPath();

	protected abstract void moveToPoint( float x, float y );

	protected abstract void addLineToPoint( float x, float y );

	protected abstract void closePath();

	protected abstract void fillPath();

	protected abstract void strokePath();
	
}
