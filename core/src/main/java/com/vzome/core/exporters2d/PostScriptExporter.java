/*
 * Created on Jan 2, 2004
 */
package com.vzome.core.exporters2d;

import java.awt.Color;
import java.awt.geom.Rectangle2D;


public class PostScriptExporter extends SnapshotExporter {
	
	// ---------- MIMIC NATIVE QUARTZ METHODS 

    @Override
    protected void setBlackStrokeColor()
    {
        output .print( " 0 setgray" );
    }
    
    @Override
    protected void setRGBStrokeColor( float r, float g, float b )
    {
        output .print( " " + RGB_FORMAT.format( r ) + " " + RGB_FORMAT.format( g ) + " " + RGB_FORMAT.format( b ) + " setrgbcolor" );
    }

    @Override
    protected void setRGBFillColor( float r, float g, float b )
    {
        output .print( " " + RGB_FORMAT.format( r ) + " " + RGB_FORMAT.format( g ) + " " + RGB_FORMAT.format( b ) + " setrgbcolor" );
    }

    @Override
	protected void beginPath()
	{
        output .print( " newpath" );
    }

    @Override
	protected void moveToPoint( float x, float y )
	{
		output .print( " " + XY_FORMAT.format( x ) + " " + XY_FORMAT.format( y ) + " moveto" );
	}

    @Override
	protected void addLineToPoint( float x, float y )
	{
		output .print( " " + XY_FORMAT.format( x ) + " " + XY_FORMAT.format( y ) + " lineto" );
	}

    @Override
	protected void closePath()
	{
		output .print( " closepath" );
	}

    @Override
	protected void fillPath()
	{
		output .print( " fill" );
	}

    @Override
	protected void strokePath()
	{
		output .print( " stroke\n" );
	}
	
	/* (non-Javadoc)
	 * @see org.vorthmann.zome.export.java2d.SnapshotExporter#outputPrologue(java.awt.geom.Rectangle2D, float, java.util.Iterator, java.awt.Color)
	 */
    @Override
	protected void outputPrologue( Rectangle2D rect, float strokeWidth )
	{
	    if ( strokeWidth > 0 )
            output .print( strokeWidth + " setlinewidth 1 setlinejoin\n" );
        RGB_FORMAT .setMaximumFractionDigits( 3 );
        XY_FORMAT .setMaximumFractionDigits( 3 );
	}
	
    @Override
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
    @Override
	protected void outputPostlogue()
	{}
	
}
