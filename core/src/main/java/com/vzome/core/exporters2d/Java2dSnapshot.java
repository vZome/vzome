
package com.vzome.core.exporters2d;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.geom.GeneralPath;
import java.awt.geom.Rectangle2D;
import java.util.Collections;
import java.util.List;

import javax.vecmath.Vector3f;

public class Java2dSnapshot
{
    private boolean showBackground = false, monochrome = true, lineDrawing = false, doLighting = true, doOutlines = true;

    private List<Polygon> polygons;

    private List<LineSegment> lines;

    private Rectangle2D mRect;

    private float strokeWidth;

    private Color backgroundColor;

    public Java2dSnapshot( List<Polygon> mPolygons, List<LineSegment> mLines )
    {
        this.polygons = mPolygons;
        this.lines = mLines;
    }

    public Color getBackgroundColor()
    {
        return this .backgroundColor;
    }

    public boolean isMonochrome()
    {
        return this .monochrome;
    }

    public boolean isLineDrawing()
    {
        return this .lineDrawing;
    }

    public void addPolygon( Polygon polygon )
    {
        this .polygons .add( polygon );
    }
    
    public void addLineSegment( Color color, Vector3f start, Vector3f end )
    {
        this .lines .add( new LineSegment( color, start, end ) );
    }
    
    public void depthSort()
    {
        if ( this .lineDrawing )
            Collections .sort( this .lines );
            // TODO eliminate duplicates
        else
            Collections .sort( this .polygons );
    }
    
    public boolean hasLighting()
    {
        return this .doLighting;
    }

    public void setRect( Rectangle2D rect )
    {
        this .mRect = rect;
    }
    
    public void setStrokeWidth( float strokeWidth )
    {
        this .strokeWidth = strokeWidth;
    }

    public Rectangle2D getRect()
    {
        return this .mRect;
    }

    public float getStrokeWidth()
    {
        return this .strokeWidth;
    }
    
    public Dimension getDimension()
    {
        return new Dimension( (int) this .mRect .getWidth(), (int) this .mRect .getHeight() ) ;
    }

    public boolean showBackground()
    {
        return this .showBackground;
    }

    public boolean isDoOutlines()
    {
        return doOutlines;
    }

    public void setDoOutlines( boolean doOutlines )
    {
        this.doOutlines = doOutlines;
    }

    public List<LineSegment> getLines()
    {
        return this .lines;
    }

    public List<Polygon> getPolygons()
    {
        return this .polygons;
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

        @Override
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
        
        @Override
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

    public void toggleBackground()
    {
        this .showBackground = ! this .showBackground;
    }

    public void toggleLineDrawing()
    {
        this.setLineDrawing( ! this .isLineDrawing() );
    }

    public void toggleMonochrome()
    {
        this.setMonochrome( ! this .isMonochrome() );
    }

    public void setMonochrome( boolean monochrome )
    {
        this .monochrome = monochrome;
    }

    public void clear()
    {
        this .lines .clear();
        this .polygons .clear();
    }

    public void setDoLighting( boolean doLighting )
    {
        this .doLighting = doLighting;
    }

    public void setLineDrawing( boolean lineDrawing )
    {
        this.lineDrawing = lineDrawing;
    }

    public void setBackgroundColor( Color backgroundColor )
    {
        this.backgroundColor = backgroundColor;
    }
}