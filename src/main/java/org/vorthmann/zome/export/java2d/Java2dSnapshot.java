package org.vorthmann.zome.export.java2d;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.io.FileWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.vecmath.Vector3f;

import org.vorthmann.ui.DefaultController;

/**
 * Renders out to PDF, Postscript, or SVG.
 * 
 * TODO extract model from controller, to support headless export.
 * 
 * @author vorth
 */
public class Java2dSnapshot extends DefaultController
{
    public Java2dSnapshot( Java2dExporter exporter )
    {
        this .mLineDrawing = false;
        this .monochrome = true;
        this .doLighting = true;
        this .doOutlines = true;
        this.setExporter( exporter );
    }

    private final static String[] DRAW_STYLES = new String[]{ "outlined shapes", "shaded shapes", "shaded, outlined shapes", "colored lines", "black lines" };
    
    @Override
    public String[] getCommandList( String listName )
    {
        if ( "draw.styles" .equals( listName ) )
        {
            return DRAW_STYLES;
        }
        else
            return super .getCommandList( listName );
    }

    public String getProperty( String propName )
    {
        if ( "showBackground" .equals( propName ) )
            return Boolean .toString( mShowBackground );
        if ( "lineDrawing" .equals( propName ) )
            return Boolean .toString( mLineDrawing );
        if ( "monochrome" .equals( propName ) )
            return Boolean .toString( monochrome );
        if ( "drawStyle" .equals( propName ) )
        {
            if ( this .mLineDrawing )
                if ( this .monochrome )
                    return "black lines";
                else
                    return "colored lines";
            else if ( this .doLighting )
                if ( this .doOutlines )
                    return "shaded, outlined shapes";
                else
                    return "shaded shapes";
            else
                return "outlined shapes";
        }
        return super.getProperty( propName );
    }

    public void actionPerformed( ActionEvent e )
    {
        String action = e .getActionCommand();

        if ( action .equals( "refresh" ) )
        {
            current = false;
        }
        else if ( action .equals( "toggleBackground" ) )
        {
            current = false;
            mShowBackground = !mShowBackground;
        }
        else if ( action .equals( "toggleLineDrawing" ) )
        {
            current = false;
            mLineDrawing = !mLineDrawing;
        }
        else if ( action .equals( "toggleMonochrome" ) )
        {
            monochrome = !monochrome;
            if ( mLineDrawing )
                current = false;
        }
        else if ( action .startsWith( "setDrawStyle." ) )
        {
            String drawStyle = action .substring( "setDrawStyle." .length() );
            if ( "black lines" .equals(  drawStyle ) ) {
                this .monochrome = true;
                this .mLineDrawing = true;
            }
            else if ( "colored lines" .equals(  drawStyle ) ) {
                this .monochrome = false;
                this .mLineDrawing = true;
            }
            else if ( "outlined shapes" .equals(  drawStyle ) ) {
                this .mLineDrawing = false;
                this .doLighting = false;
                this .doOutlines = true;
            }
            else if ( "shaded shapes" .equals(  drawStyle ) ) {
                this .mLineDrawing = false;
                this .doLighting = true;
                this .doOutlines = false;
            }
            else if ( "shaded, outlined shapes" .equals(  drawStyle ) ) {
                this .mLineDrawing = false;
                this .doLighting = true;
                this .doOutlines = true;
            }
            current = false;
        }
        else
            super.actionPerformed( e );
    }
    
//    private void refresh()
//    {
//
//        mSnapshot .setBackgroundColor( new java.awt.Color( colors .getColor( Colors.BACKGROUND ) .getRGB() ) );    
//    }

    public void doFileAction( String command, File file )
    {
        try {
            SnapshotExporter exporter = null;
            if ( command .equals( "export.2d.pdf" ) )
                exporter = new PDFExporter();
            else if ( command .equals( "export.2d.ps" ) )
                exporter = new PostScriptExporter();
            else if ( command .equals( "export.2d.svg" ) )
                exporter = new SVGExporter();
            if ( exporter == null )
                return;
            Writer out = new FileWriter( file );
            exporter .export( this, out );
        } catch ( Exception e ) {
            mErrors .reportError( UNKNOWN_ERROR_CODE, new Object[]{ e } );
        }
    }

    public void repaintGraphics( String panelName, Graphics graphics, Dimension size )
    {
        if ( ! current )
            try {
                mPolygons .clear();
                mLines .clear();
                this .exporter .doExport( new File(""), null, size.height, size.width );
                current = true;
            } catch ( Exception e1 ) {
                mErrors .reportError( UNKNOWN_ERROR_CODE, new Object[]{ e1 } );
            }
        
        Graphics2D g2d = (Graphics2D) graphics;

        g2d .setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
        g2d .clearRect( 0, 0, (int) size .getWidth(), (int) size .getHeight() );

        if ( mShowBackground ) {
            g2d .setPaint( this .getBackgroundColor() );
            Rectangle2D rect = new Rectangle2D.Float();
            rect .setFrame( new Point2D.Float(), size );
            g2d .fill( rect );
        }

        if ( mLineDrawing ) {
            g2d .setStroke( new BasicStroke( 3*mStrokeWidth, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND ) );
            g2d .setPaint( java.awt.Color.BLACK );
            
            for (LineSegment line : mLines) {
                if ( monochrome )
                    if ( mShowBackground )
                        g2d .setPaint( java.awt.Color.WHITE );
                    else
                        g2d .setPaint( java.awt.Color.BLACK );
                else
                    g2d .setPaint( line .getColor() );
                g2d .draw( line .getPath() );
            }
        }
        else {
            g2d .setStroke( new BasicStroke( mStrokeWidth, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND ) );
            for ( Java2dSnapshot.Polygon poly : mPolygons ){
                g2d .setPaint( poly .getColor() );
                g2d .fill( poly .getPath() );
                if ( this .doOutlines ) {
                    g2d .setPaint( java.awt.Color.BLACK );
                    g2d .draw( poly .getPath() );
                }
            }
        }

        g2d.dispose(); //clean up
    }
    
    private Java2dExporter exporter;

    private boolean mShowBackground, mLineDrawing, monochrome, doLighting, doOutlines;
    
    private boolean current;
    	
	private List<Polygon> mPolygons = new ArrayList<>();
    
    private List<LineSegment> mLines = new ArrayList<>();
	
	private Rectangle2D mRect;
	
	private float mStrokeWidth;


    public boolean isLineDrawing()
    {
        return mLineDrawing;
    }
    
    public boolean isMonochrome()
    {
        return monochrome;
    }

    public boolean hasLighting()
    {
        return doLighting;
    }
    
	public void addPolygon( Polygon polygon )
	{
		mPolygons .add( polygon );
	}
    
    public void addLineSegment( Color color, Vector3f start, Vector3f end )
    {
        mLines .add( new LineSegment( color, start, end ) );
    }
	
	public void depthSort()
	{
        if ( mLineDrawing )
            Collections .sort( mLines );
            // TODO eliminate duplicates
        else
            Collections .sort( mPolygons );
	}

	public void setRect( Rectangle2D rect )
	{
		mRect = rect;
	}
    
	public void setStrokeWidth( float strokeWidth )
	{
		mStrokeWidth = strokeWidth;
	}

	public Rectangle2D getRect()
	{
	    return mRect;
	}

	public float getStrokeWidth()
	{
	    if ( this .mLineDrawing || this .doOutlines )
	        return mStrokeWidth;
	    else
	        return -1f;
	}
	
    public Dimension getDimension()
    {
        return new Dimension( (int) mRect .getWidth(), (int) mRect .getHeight() ) ;
    }

    public static class LineSegment implements Comparable<LineSegment>
    {
        private final GeneralPath mPath;
        private float mDepth;
        private final Color mPolyColor;
        
        public GeneralPath getPath()
        {
            return mPath;
        }
        
        public LineSegment( Color color, Vector3f start, Vector3f end )
        {
            mPolyColor = color;
            mPath = new GeneralPath();
            mPath .moveTo( start.x, start.y );
            mPath .lineTo( end.x, end.y );
            mDepth = ( start.z + end.z ) / 2.0f;
        }
        
        public Color getColor()
        {
            return mPolyColor;
        }

        public int compareTo( LineSegment other )
        {
            double otherZ = other .mDepth;
            if ( mDepth > otherZ )
                return 1;
            if ( mDepth < otherZ )
                return -1;
            return 0;
        }
    }

	public static class Polygon implements Comparable<Polygon>
	{
		private final GeneralPath mPath;
		private float mDepth;
		private int mSize = 0;
		private Color mPolyColor;
		
		public GeneralPath getPath()
		{
			return mPath;
		}

		public int size()
		{
			return mSize;
		}
		
		public void addVertex( Vector3f vertex )
		{
			++ mSize;
			if ( mSize == 1 ) {
				mPath .moveTo( vertex.x, vertex.y );
				mDepth = vertex.z;
			}
			else {
				mPath .lineTo( vertex.x, vertex.y );
				mDepth += vertex.z;
			}
		}
		
		public void close()
		{
			mDepth /= mSize;
			mPath .closePath();
		}
		
		public Polygon( Color color )
		{
			mPolyColor = color;
			mPath = new GeneralPath();
		}
		
		public Color getColor()
		{
			return mPolyColor;
		}
		
		public int compareTo( Polygon other )
		{
			double otherZ = other .mDepth;
			if ( mDepth > otherZ )
				return 1;
			if ( mDepth < otherZ )
				return -1;
			return 0;
		}

        public void applyLighting( Vector3f normal, Vector3f[] lightDirs, Color[] lightColors, Color ambient )
        {
            float redIntensity = ambient .getRed() / 255f;
            float greenIntensity = ambient .getGreen() / 255f;
            float blueIntensity = ambient .getBlue() / 255f;
            for ( int i = 0; i < lightColors.length; i++ ) {
                float intensity = Math .max( normal .dot( lightDirs[ i ] ), 0f );
                redIntensity += intensity * ( lightColors[ i ].getRed() / 255f );
                greenIntensity += intensity * ( lightColors[ i ].getGreen() / 255f );
                blueIntensity += intensity * ( lightColors[ i ].getBlue() / 255f );
            }
            int red = (int) ( mPolyColor.getRed() * Math.min( redIntensity, 1f ) );
            int green = (int) ( mPolyColor.getGreen() * Math.min( greenIntensity, 1f ) );
            int blue = (int) ( mPolyColor.getBlue() * Math.min( blueIntensity, 1f ) );
            mPolyColor = new Color( red, green, blue );
        }
    }

    public Iterator<LineSegment> getLines()
    {
        return mLines .iterator();
    }

    public Iterator<Polygon> getPolygons()
    {
        return mPolygons .iterator();
    }

    public void setExporter( Java2dExporter exporter )
    {
        current = false;
        this.exporter = exporter;
        exporter .setSnapshot( this );
    }

    public Color getBackgroundColor()
    {
        if ( this .mShowBackground )
            return this .exporter .getBackgroundColor();
        else
            return null;
    }
}


