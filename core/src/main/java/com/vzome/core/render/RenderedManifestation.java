

package com.vzome.core.render;

import com.vzome.core.algebra.AlgebraicMatrix;
import com.vzome.core.algebra.AlgebraicNumber;
import com.vzome.core.algebra.AlgebraicVector;
import com.vzome.core.math.Polyhedron;
import com.vzome.core.math.RealVector;
import com.vzome.core.math.symmetry.Direction;
import com.vzome.core.math.symmetry.Embedding;
import com.vzome.core.model.Manifestation;
import com.vzome.core.model.Strut;

/**
 * @author Scott Vorthmann
 *
 */
public class RenderedManifestation
{
	private final Manifestation mManifestation;
    
    private Polyhedron mShape;
    
    private RenderedModel model;

	private String mColorName;
    
    private Color color = null;
    
    private AlgebraicMatrix mOrientation;
    
    private float mGlow = 0.0f, mTransparency = 0.0f;
    
    private Object mGraphicsObject;
    
    private boolean mPickable = true;
    
    private boolean isOffset = false;
    private AlgebraicVector location;
    private AlgebraicVector fixedLocation;

    private int strutZone = -1;

    private AlgebraicNumber strutLength = null;

	private Direction strutOrbit = null;

	private int strutSense;
    
//    private transient Axis mAxis = null;

    public RenderedManifestation( Manifestation m )
    {
        mManifestation = m;
        if ( m != null )
            location = m .getLocation();
        this .fixedLocation = location;
//        AlgebraicField factory = (AlgebraicField) m .getLocation() .getFactory();
        mOrientation = null; // factory .identity();
    }
    
    @Override
	public String toString()
    {
		return this .mManifestation .toString();
	}

	public void setModel( RenderedModel model )
	{
		this .model = model;
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
    
    public Color getColor()
    {
		return this.color;
	}

	public void setColor( Color color )
	{
		this.color = color;
	}

	public void setOrientation( AlgebraicMatrix m )
    {
        mOrientation = m;
    }

    public AlgebraicMatrix getOrientation()
    {
        return mOrientation;
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
    	return this .model .renderVector( this .location );
    }
    
    public AlgebraicVector getLocationAV()
    {
    	return this .location;
    }

	public Embedding getEmbedding()
	{
		return this .model .getEmbedding();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((fixedLocation == null) ? 0 : fixedLocation.hashCode());
		result = prime * result + (isOffset ? 1231 : 1237);
		result = prime * result
				+ ((mOrientation == null) ? 0 : mOrientation.hashCode());
		result = prime * result + ((mShape == null) ? 0 : mShape.hashCode());
		result = prime * result + strutSense;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		RenderedManifestation other = (RenderedManifestation) obj;
		if (fixedLocation == null) {
			if (other.fixedLocation != null) {
				return false;
			}
		} else if (!fixedLocation.equals(other.fixedLocation)) {
			return false;
		}
		if (isOffset != other.isOffset) {
			return false;
		}
		if (mOrientation == null) {
			if (other.mOrientation != null) {
				return false;
			}
		} else if (!mOrientation.equals(other.mOrientation)) {
			return false;
		}
		if (mShape == null) {
			if (other.mShape != null) {
				return false;
			}
		} else if (!mShape.equals(other.mShape)) {
			return false;
		}
		if (strutSense != other.strutSense) {
			return false;
		}
		return true;
	}

    public RenderedManifestation copy()
    {
        RenderedManifestation copy = new RenderedManifestation( null );
        copy .location = this .location;
        copy .fixedLocation = this .fixedLocation;
        copy .mColorName = this .mColorName;
        copy .color = this .color;
        copy .mGlow = this .mGlow;
        copy .mOrientation = this .mOrientation;
        copy .mShape = this .mShape;
        copy .mTransparency = this .mTransparency;
        copy .strutLength = this .strutLength;
        copy .strutZone = this .strutZone;
        return copy;
    }

    public void setStrut( Direction orbit, int zone, int sense, AlgebraicNumber length )
    {
		this .strutOrbit = orbit;
        this .strutZone = zone;
		this .strutSense = sense;
        this .strutLength = length;
    }
    
    public int getStrutZone()
    {
        return this .strutZone;
    }

    public int getStrutSense()
    {
        return this .strutSense;
    }

    public AlgebraicNumber getStrutLength()
    {
        return this .strutLength;
    }

	public Direction getStrutOrbit()
	{
		return this .strutOrbit;
	}
	
	void offsetLocation()
	{
		if ( this .mManifestation != null ) {
			Strut strut = (Strut) this .mManifestation;
			this .location = strut .getEnd();
			this .isOffset = true;
		}
	}
	
	void resetLocation()
	{
		if ( this .mManifestation != null ) {
			location = this .mManifestation .getLocation();
			this .isOffset = false;
		}
	}
}
