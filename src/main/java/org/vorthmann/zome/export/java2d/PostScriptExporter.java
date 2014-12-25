/*
 * Created on Jan 2, 2004
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package org.vorthmann.zome.export.java2d;

import java.awt.Color;
import java.awt.geom.Rectangle2D;


public class PostScriptExporter extends SnapshotExporter {
	
	// ---------- MIMIC NATIVE QUARTZ METHODS 

    protected void setBlackStrokeColor()
    {
        output .print( " 0 setgray" );
    }
    
    protected void setRGBStrokeColor( float r, float g, float b )
    {
        output .print( " " + RGB_FORMAT.format( r ) + " " + RGB_FORMAT.format( g ) + " " + RGB_FORMAT.format( b ) + " setrgbcolor" );
    }

    protected void setRGBFillColor( float r, float g, float b )
    {
        output .print( " " + RGB_FORMAT.format( r ) + " " + RGB_FORMAT.format( g ) + " " + RGB_FORMAT.format( b ) + " setrgbcolor" );
    }

	protected void beginPath()
	{
        output .print( " newpath" );
    }

	protected void moveToPoint( float x, float y )
	{
		output .print( " " + XY_FORMAT.format( x ) + " " + XY_FORMAT.format( y ) + " moveto" );
	}

	protected void addLineToPoint( float x, float y )
	{
		output .print( " " + XY_FORMAT.format( x ) + " " + XY_FORMAT.format( y ) + " lineto" );
	}

	protected void closePath()
	{
		output .print( " closepath" );
	}

	protected void fillPath()
	{
		output .print( " fill" );
	}

	protected void strokePath()
	{
		output .print( " stroke\n" );
	}
	
	/* (non-Javadoc)
	 * @see org.vorthmann.zome.export.java2d.SnapshotExporter#outputPrologue(java.awt.geom.Rectangle2D, float, java.util.Iterator, java.awt.Color)
	 */
	protected void outputPrologue( Rectangle2D rect, float strokeWidth )
	{
	    if ( strokeWidth > 0 )
            output .print( strokeWidth + " setlinewidth 1 setlinejoin\n" );
        RGB_FORMAT .setMaximumFractionDigits( 3 );
        XY_FORMAT .setMaximumFractionDigits( 2 );
	}
	
	protected void outputBackground( Color bgColor )
	{
        float[] rgb = bgColor .getRGBColorComponents( null );
        setRGBFillColor( rgb[0], rgb[1], rgb[2] );
        beginPath();
        moveToPoint( 0f, 0f );
        addLineToPoint( 0f, height );
        addLineToPoint( width, height );
        addLineToPoint( width, 0f );
        addLineToPoint( 0f, 0f );
        closePath();
        fillPath();
    }

	/* (non-Javadoc)
	 * @see org.vorthmann.zome.export.java2d.SnapshotExporter#outputPostlogue()
	 */
	protected void outputPostlogue()
	{}
	
}
