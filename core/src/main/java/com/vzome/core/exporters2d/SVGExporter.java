/*
 * Created on Jul 30, 2004
 */
package com.vzome.core.exporters2d;

import java.awt.Color;
import java.awt.geom.Rectangle2D;

/**
 * @author scottv
 */
public class SVGExporter extends SnapshotExporter {

	/* (non-Javadoc)
	 * @see org.vorthmann.zome.export.java2d.SnapshotExporter#outputBackground(java.awt.Color)
	 */
    @Override
	protected void outputBackground( Color bgColor )
	{
		output .print( "<rect fill='#" );
		int color = bgColor .getRGB() & 0xffffff;
		output .print( Integer .toHexString( color ) );
		output .println( "' x='1' y='1' width='" + width + "' height='" + height + "'/>" );
	}

	/* (non-Javadoc)
	 * @see org.vorthmann.zome.export.java2d.SnapshotExporter#outputPrologue(java.awt.geom.Rectangle2D, float, java.util.Iterator)
	 */
    @Override
	protected void outputPrologue( Rectangle2D rect, float strokeWidth )
	{
		output .println( "<?xml version='1.0'?>" );
		output .println( "<svg version='1.1' xmlns='http://www.w3.org/2000/svg'" );
		if ( strokeWidth > 0 )
		    output .println( "   stroke='black' stroke-linejoin='round' stroke-width='" + strokeWidth + "' " );
        output .println( "   viewBox='0 0 " + width + " " + height + "' >" );

        XY_FORMAT .setMaximumFractionDigits( 2 );
    }
    
    @Override
    protected void outputLine( Java2dSnapshot.LineSegment line, boolean monochrome )
    {
        output .print( "<path stroke='#" );
        int color = line .getColor() .getRGB() & 0xffffff;
        if ( monochrome )
            color = Color.BLACK .getRGB() & 0xffffff;
        String hex = Integer .toHexString( color );
        for ( int i = 0; i < 6-hex.length(); i++ )
            output .print( '0' );
        output .print( hex );
        output .print( "' d='" );
        renderPath( line .getPath() );
        output .println( "'/>" );
    }

    @Override
	protected void outputPolygon( Java2dSnapshot.Polygon polygon, boolean doOutline )
	{
		output .print( "<path fill='#" );
		int color = polygon .getColor() .getRGB() & 0xffffff;
		String hex = Integer .toHexString( color );
		for ( int i = 0; i < 6-hex.length(); i++ )
			output .print( '0' );
		output .print( hex );
		output .print( "' d='" );
		renderPath( polygon .getPath() );
		output .println( "'/>" );
	}

    @Override
	protected void outputPostlogue()
	{
		output .println();
		output .println( "</svg>" );
	}


    @Override
    protected void setRGBStrokeColor(float r, float g, float b) {}

    @Override
    protected void setRGBFillColor(float r, float g, float b) {}

    @Override
	protected void beginPath() {}

    @Override
	protected void moveToPoint(float x, float y)
	{
		output .print( "M " + XY_FORMAT.format( x ) + " " + XY_FORMAT.format(height-y) + " " );
	}

	/* (non-Javadoc)
	 * @see org.vorthmann.zome.export.java2d.SnapshotExporter#addLineToPoint(float, float)
	 */
    @Override
	protected void addLineToPoint(float x, float y)
	{
		output .print( "L " + XY_FORMAT.format( x ) + " " + XY_FORMAT.format(height-y) + " " );
	}

    @Override
	protected void closePath()
	{
		output .print( " z" );
	}

    @Override
	protected void fillPath() {}

    @Override
	protected void strokePath() {}

}
