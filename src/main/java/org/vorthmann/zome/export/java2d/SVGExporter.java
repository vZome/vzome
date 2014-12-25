/*
 * Created on Jul 30, 2004
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package org.vorthmann.zome.export.java2d;

import java.awt.Color;
import java.awt.geom.Rectangle2D;

/**
 * @author scottv
 *
 * To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public class SVGExporter extends SnapshotExporter {

	/* (non-Javadoc)
	 * @see org.vorthmann.zome.export.java2d.SnapshotExporter#outputBackground(java.awt.Color)
	 */
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
	protected void outputPrologue( Rectangle2D rect, float strokeWidth )
	{
		output .println( "<?xml version='1.0'?>" );
		output .println( "<svg version='1.1' xmlns='http://www.w3.org/2000/svg'" );
		if ( strokeWidth > 0 )
		    output .println( "   stroke='black' stroke-linejoin='round' stroke-width='" + strokeWidth + "' " );
        output .println( "   viewBox='0 0 " + width + " " + height + "' >" );

        XY_FORMAT .setMaximumFractionDigits( 2 );
    }
    
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

	protected void outputPostlogue()
	{
		output .println();
		output .println( "</svg>" );
	}


    protected void setRGBStrokeColor(float r, float g, float b) {}

    protected void setRGBFillColor(float r, float g, float b) {}

	protected void beginPath() {}

	protected void moveToPoint(float x, float y)
	{
		output .print( "M " + XY_FORMAT.format( x ) + " " + XY_FORMAT.format(height-y) + " " );
	}

	/* (non-Javadoc)
	 * @see org.vorthmann.zome.export.java2d.SnapshotExporter#addLineToPoint(float, float)
	 */
	protected void addLineToPoint(float x, float y)
	{
		output .print( "L " + XY_FORMAT.format( x ) + " " + XY_FORMAT.format(height-y) + " " );
	}

	protected void closePath()
	{
		output .print( " z" );
	}

	protected void fillPath() {}

	protected void strokePath() {}

}
