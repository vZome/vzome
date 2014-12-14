

package com.vzome.core.render;

import java.util.Arrays;

import com.vzome.core.math.Polyhedron;
import com.vzome.core.math.RealVector;
import com.vzome.core.math.symmetry.Direction;
import com.vzome.core.model.Manifestation;

/**
 * @author Scott Vorthmann
 *
 */
public class RenderedManifestation
{
    private static int hashCode( int[] array )
    {
        int prime = 31;
        if ( array == null )
            return 0;
        int result = 1;
        for ( int index = 0; index < array.length; index++ ) {
            result = prime * result + array[index];
        }
        return result;
    }

    private Manifestation mManifestation;
    
    private Polyhedron mShape;
    
    private String mColorName;
    
    private int[][] mOrientation;
    
    private float mGlow = 0.0f, mTransparency = 0.0f;
    
    private Object mGraphicsObject;
    
    private boolean mPickable = true, mMirrored = false;

    private int[] location;

    private int strutZone = -1;

    private int[] strutLength = null;

	private Direction strutOrbit = null;
    
//    private transient Axis mAxis = null;

    public RenderedManifestation( Manifestation m )
    {
        mManifestation = m;
        if ( m != null )
            location = m .getLocation();
//        AlgebraicField factory = (AlgebraicField) m .getLocation() .getFactory();
        mOrientation = null; // factory .identity();
    }

    public void setGraphicsObject( Object go )
    {
        mGraphicsObject = go;
    }
    
    public Object getGraphicsObject()
    {
        return mGraphicsObject;
    }


    public void setGlow( float glow )
    {
        mGlow = glow;
    }


    public float getGlow()
    {
        return mGlow;
    }


    public void setTransparency( float trans )
    {
        mTransparency = trans;
    }


    public float getTransparency()
    {
        return mTransparency;
    }


    /**
     * Shape can be null if the strut length is too short (see resetAttributes),
     * or if the panel has no normal (though the latter should now be an error
     * before we get to this point).
     * @param shape
     */
    public void setShape( Polyhedron shape )
    {
        mShape = shape;
    }
    
    public Polyhedron getShape()
    {
        return mShape;
    }
    
    public void setPickable( boolean value )
    {
        mPickable = value;
    }
    
    public boolean isPickable()
    {
        return mPickable;
    }

    public Manifestation getManifestation()
    {
        return mManifestation;
    }

    public String getColorName()
    {
        return mColorName;
    }

    public void setColorName( String name )
    {
        mColorName = name;
    }
    
    public void setOrientation( int[][] m, boolean mirrored )
    {
        mOrientation = m;
        mMirrored = mirrored;
    }

    public int[][] getOrientation()
    {
        return mOrientation;
    }

    /**
     * @return
     */
    public boolean reverseOrder()
    {
        return mMirrored;
    }
//
//    /**
//     * Useful only for struts.  This is for optimization.
//     * @param axis
//     */
//    public void setAxis( Axis axis )
//    {
//        mAxis = axis;
//    }
//    
//    public Axis getAxis()
//    {
//        return mAxis;
//    }

    public RealVector getLocation()
    {
        if ( location == null )
            return new RealVector( 0d, 0d, 0d );
        else
            return mShape .getField() .getRealVector( location );
    }

    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + RenderedManifestation.hashCode( location );
        result = prime * result + (mMirrored ? 1231 : 1237);
        result = prime * result + ((mShape == null) ? 0 : mShape.hashCode());
        result = prime * result + ((mOrientation == null) ? 0 : mOrientation.hashCode());
        return result;
    }

    public boolean equals( Object obj )
    {
        if ( this == obj )
            return true;
        if ( obj == null )
            return false;
        if ( !(obj instanceof RenderedManifestation) )
            return false;
        RenderedManifestation other = (RenderedManifestation) obj;
        if ( !Arrays.equals( location, other.location ) )
            return false;
        if ( mMirrored != other.mMirrored )
            return false;
        if ( !Arrays.equals( mOrientation, other.mOrientation ) )
            return false;
        if ( mShape == null ) {
            if ( other.mShape != null )
                return false;
        } else if ( !mShape.equals( other.mShape ) )
            return false;
        return true;
    }

    public RenderedManifestation copy()
    {
        RenderedManifestation copy = new RenderedManifestation( null );
        copy .location = this .location;
        copy .mColorName = this .mColorName;
        copy .mGlow = this .mGlow;
        copy .mMirrored = this .mMirrored;
        copy .mOrientation = this .mOrientation;
        copy .mShape = this .mShape;
        copy .mTransparency = this .mTransparency;
        copy .strutLength = this .strutLength;
        copy .strutZone = this .strutZone;
        return copy;
    }

    public void setStrut( Direction orbit, int zone, int[] length )
    {
		this .strutOrbit = orbit;
        this .strutZone = zone;
        this .strutLength = length;
    }
    
    public int getStrutZone()
    {
        return this .strutZone;
    }

    public int[] getStrutLength()
    {
        return this .strutLength;
    }

	public Direction getStrutOrbit()
	{
		return this .strutOrbit;
	}
}
