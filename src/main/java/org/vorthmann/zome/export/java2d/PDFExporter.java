/*
 * Created on Jan 2, 2004
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package org.vorthmann.zome.export.java2d;

import java.awt.Color;
import java.awt.geom.Rectangle2D;


public class PDFExporter extends SnapshotExporter {
	
	// ---------- MIMIC NATIVE QUARTZ METHODS 

    protected void setRGBStrokeColor( float r, float g, float b )
    {
        output .print( " " + RGB_FORMAT.format( r ) + " " + RGB_FORMAT.format( g ) + " " + RGB_FORMAT.format( b ) + " RG" );
    }

    protected void setRGBFillColor( float r, float g, float b )
    {
        output .print( " " + RGB_FORMAT.format( r ) + " " + RGB_FORMAT.format( g ) + " " + RGB_FORMAT.format( b ) + " rg" );
    }

	protected void beginPath()
	{
	}

	protected void moveToPoint( float x, float y )
	{
		output .print( " " + XY_FORMAT.format( x ) + " " + XY_FORMAT.format( y ) + " m" );
	}

	protected void addLineToPoint( float x, float y )
	{
		output .print( " " + XY_FORMAT.format( x ) + " " + XY_FORMAT.format( y ) + " l" );
	}

	protected void closePath()
	{
		output .print( " h" );
	}

	protected void fillPath()
	{
		output .print( " f" );
	}

	protected void strokePath()
	{
		output .print( " S\n" );
	}

	private int streamStart;
	
	/* (non-Javadoc)
	 * @see org.vorthmann.zome.export.java2d.SnapshotExporter#outputPrologue(java.awt.geom.Rectangle2D, float, java.util.Iterator, java.awt.Color)
	 */
	protected void outputPrologue( Rectangle2D rect, float strokeWidth )
	{
		includeFile( "org/vorthmann/zome/export/java2d/prologue.pdf" );
		streamStart = output .getBytesTotal();
		if ( strokeWidth > 0 )
		    output .print( strokeWidth + " w 1 j\n" );
        RGB_FORMAT .setMaximumFractionDigits( 3 );
        XY_FORMAT .setMaximumFractionDigits( 2 );
	}
	
	protected void outputBackground( Color bgColor )
	{
		float[] rgb = bgColor .getRGBColorComponents( null );
		setRGBFillColor( rgb[0], rgb[1], rgb[2] );
		beginPath();
		output .print( " 0 0 " + width + " " + height + " re\n" );
		closePath();
		fillPath();
	}

	/* (non-Javadoc)
	 * @see org.vorthmann.zome.export.java2d.SnapshotExporter#outputPostlogue()
	 */
	protected void outputPostlogue()
	{
		// TODO Auto-generated method stub
		int streamLen = output .getBytesTotal() - streamStart;
		output .print( "endstream\n" );
		output .print( "endobj\n" );
		
		int sizeOffset = output .getBytesTotal();
		
		output .print( "5 0 obj " + streamLen + " endobj\n" );
		
		int boxOffset = output .getBytesTotal();
		
		output .print( "6 0 obj [0 0 " + width + " " + height + "] endobj\n" );
		
		int startXref = output .getBytesTotal();
		
		includeFile( "org/vorthmann/zome/export/java2d/postlogue.pdf" );
		
		String num = Integer .toString( sizeOffset );
		for ( int i = 0; i + num.length() < 10; i++ )
			output .print( "0" );
		output .print( num + " 00000 n \n" );
		
		num = Integer .toString( boxOffset );
		for ( int i = 0; i + num.length() < 10; i++ )
			output .print( "0" );
		output .print( num + " 00000 n \n" );

		output .print( "trailer\n" );
		output .print( "<< /Size 7 /Root 1 0 R >>\n" );
		output .print( "startxref\n" );
		output .print( startXref + "\n" );
		output .print( "%%EOF\n" );	
	}
	
}
