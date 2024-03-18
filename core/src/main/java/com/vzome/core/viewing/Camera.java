package com.vzome.core.viewing;

import java.util.Objects;

import javax.vecmath.Matrix3f;
import javax.vecmath.Matrix4f;
import javax.vecmath.Point3f;
import javax.vecmath.Quat4f;
import javax.vecmath.Vector3f;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.vzome.core.math.RealMatrix4;
import com.vzome.core.math.RealVector;
import com.vzome.xml.DomUtils;

public class Camera
{
    /**
     * The original frustum.
     */
    public static final float ORIG_WIDTH = 18f, ORIG_DISTANCE = 40f;

    protected static final float ORIG_NEAR = 0.1f, ORIG_FAR = 2 * ORIG_DISTANCE;

    protected static final Vector3f ORIG_LOOK = new Vector3f(0,0,-1);

    protected static final Vector3f ORIG_UP = new Vector3f(0,1,0);


    private Point3f mLookAtPoint = new Point3f();

    private float mNear = ORIG_NEAR;

    private float mFar = ORIG_FAR;

    private float mWidth = ORIG_WIDTH;

    private float mDistance = ORIG_DISTANCE;

    private Vector3f mUpDirection = new Vector3f( ORIG_UP ), mLookDirection = new Vector3f( ORIG_LOOK );

    private boolean mOrthographic = false;

    private double mStereoAngle = 0d;

    @Override
    public boolean equals( Object object )
    {
        if ( object == null )
            return false;
        if ( object == this )
            return true;
        if ( ! ( object instanceof Camera ) )
            return false;

        Camera that = (Camera) object;

        if ( this.mNear != that.mNear )
            return false;
        if ( this.mFar != that.mFar )
            return false;
        if ( this.mWidth != that.mWidth )
            return false;
        if ( this.mDistance != that.mDistance )
            return false;
        if ( this.mOrthographic != that.mOrthographic )
            return false;
        if ( this.mStereoAngle != that.mStereoAngle )
            return false;
        if ( ! this.mLookAtPoint .equals( that.mLookAtPoint ) )
            return false;
        if ( ! this.mUpDirection .equals( that.mUpDirection ) )
            return false;
        return this.mLookDirection .equals( that.mLookDirection );
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 83 * hash + Objects.hashCode(this.mLookAtPoint);
        hash = 83 * hash + (int) (Double.doubleToLongBits(this.mNear) ^ (Double.doubleToLongBits(this.mNear) >>> 32));
        hash = 83 * hash + (int) (Double.doubleToLongBits(this.mFar) ^ (Double.doubleToLongBits(this.mFar) >>> 32));
        hash = 83 * hash + (int) (Double.doubleToLongBits(this.mWidth) ^ (Double.doubleToLongBits(this.mWidth) >>> 32));
        hash = 83 * hash + (int) (Double.doubleToLongBits(this.mDistance) ^ (Double.doubleToLongBits(this.mDistance) >>> 32));
        hash = 83 * hash + Objects.hashCode(this.mUpDirection);
        hash = 83 * hash + Objects.hashCode(this.mLookDirection);
        hash = 83 * hash + (this.mOrthographic ? 1 : 0);
        hash = 83 * hash + (int) (Double.doubleToLongBits(this.mStereoAngle) ^ (Double.doubleToLongBits(this.mStereoAngle) >>> 32));
        return hash;
    }

    public Camera()
    {
        this .setMagnification( 1f );
    }

    public Camera( Camera prototype )
    {
        this();

        this .mLookAtPoint = new Point3f( prototype .mLookAtPoint );
        this .mNear = prototype .mNear;
        this .mFar = prototype .mFar;
        this .mWidth = prototype .mWidth;
        this .mDistance = prototype .mDistance;
        this .mUpDirection = new Vector3f( prototype .mUpDirection );
        this .mLookDirection = new Vector3f( prototype .mLookDirection );
        this .mOrthographic = prototype .mOrthographic;
        this .mStereoAngle = prototype .mStereoAngle;
    }

    public boolean isPerspective()
    {
        return ! mOrthographic;
    }

    /**
     * The width of the frustum at the look-at point is held
     * constant, as well as the other dimensions of the frustum.
     * @param value
     */
    public void setPerspective( boolean value )
    {
        mOrthographic = ! value;
    }

    public void setStereoAngle( float value )
    {
        mStereoAngle = value;
        // now make sure the stereo/mono switch keeps effective object sizes the same
        if ( value == 0d )
            mWidth = mWidth * 2f;
        else
            mWidth = mWidth / 2f;
    }

    public float getFieldOfView()
    {
        return (float) ( 2 * Math .atan( (mWidth/2) / mDistance ) );
    }

    public float getViewDistance()
    {
        return mDistance;
    }

    public void getViewOrientation( Vector3f lookDir, Vector3f upDir )
    {
        lookDir .set( mLookDirection );
        upDir .set( mUpDirection );
    }

    @JsonIgnore
    public float getMagnification()
    {
        return (float) Math .log( mDistance / ORIG_DISTANCE );
    }

    static final double EPSILON_ABSOLUTE = 1.0e-5;



    /**
     * Get the mapping from view to world coordinates
     * @param trans
     */
    @JsonIgnore
    public void getViewTransform( Matrix4f matrix )
    {
        RealMatrix4 rm4 = getViewMatrix();
        matrix .set( rm4 .toArray() );
    }

    @JsonIgnore
    public RealMatrix4 getViewMatrix()
    {
        Point3f eyePoint = getPosition();
        RealVector eye = new RealVector( eyePoint.x, eyePoint.y, eyePoint.z );
        RealVector lookAt = new RealVector( mLookAtPoint.x, mLookAtPoint.y, mLookAtPoint.z );
        RealVector up = new RealVector( mUpDirection.x, mUpDirection.y, mUpDirection.z );
        return RealMatrix4.lookAt( eye, lookAt, up );
    }
    
    @JsonIgnore
    public RealMatrix4 getEyeMatrix()
    {
        if ( ! this .isPerspective() )
            return ortho();
        else
            return perspective();// TODO - make aspect ratio track the screen window shape
    }

    public RealMatrix4 perspective()
    {
        return RealMatrix4.perspective( getFieldOfView(), 1.0f, mNear, mFar ); // FIX this aspect ratio
    }

    private RealMatrix4 ortho()
    {
        float edge = this .getWidth() / 2;
        float left = -edge, bottom = -edge;
        float right = edge, top = edge;
        return RealMatrix4.ortho( left, right, bottom, top, mNear, mFar );
    }

    public Point3f getPosition()
    {
        Point3f eyePoint = new Point3f( mLookAtPoint );
        Vector3f dir = new Vector3f( mLookDirection );
        dir .scale( -mDistance );
        eyePoint .add( dir );
        return eyePoint;
    }
    
    public Vector3f getLookDirection()
    {
        return this .mLookDirection;
    }
    
    public Point3f getLookAtPoint()
    {
        return mLookAtPoint;
    }

    public Vector3f getUpDirection()
    {
        return this .mUpDirection;
    }


    public void setViewDirection( Vector3f lookDir )
    {
        mLookDirection .set( lookDir );
        mLookDirection .normalize();
        mUpDirection .set( ORIG_UP );
    }


    public void setViewDirection( Vector3f lookDir, Vector3f upDir )
    {
        mLookDirection .set( lookDir );
        mLookDirection .normalize();
        mUpDirection .set( upDir );
        mUpDirection .normalize();
    }


    public void setLookAtPoint( Point3f lookAt )
    {
        mLookAtPoint = lookAt;
    }


    public void addViewpointRotation( Quat4f rotation )
    {
        Matrix3f m = new Matrix3f();
        m .set( rotation );
        m .invert();
        m .transform( mLookDirection );
        m .transform( mUpDirection );
    }


    /**
     * All view parameters will scale with distance, to keep the frustum
     * shape fixed.
     * @param distance
     */
    public void setViewpointDistance( float distance )
    {
        float ratio = distance / mDistance;
        mDistance = distance;
        mNear = mNear * ratio;
        mFar = mFar * ratio;
        mWidth = mWidth * ratio;
    }

    public final void setMagnification( float exp )
    {
        setViewpointDistance( (float) ( ORIG_DISTANCE * Math .pow( Math.E, exp ) ) );
    }

    public boolean isStereo()
    {
        return mStereoAngle != 0d;
    }

    public float getNearClipDistance()
    {
        return mNear;
    }

    public float getFarClipDistance()
    {
        return mFar;
    }

    public float getWidth()
    {
        return mWidth;
    }

    public Element getXML( Document doc )
    {
        Element result = doc .createElement( "ViewModel" );
        DomUtils .addAttribute( result, "near", Double .toString( mNear ) );
        DomUtils .addAttribute( result, "far", Double .toString( mFar ) );
        DomUtils .addAttribute( result, "width", Double .toString( mWidth ) );
        DomUtils .addAttribute( result, "distance", Double .toString( mDistance ) );
        DomUtils .addAttribute( result, "stereoAngle", Double .toString( mStereoAngle ) );
        DomUtils .addAttribute( result, "parallel", Boolean .toString( mOrthographic ) );
        {
            Element child = doc .createElement( "LookAtPoint" );
            DomUtils .addAttribute( child, "x", Double .toString( mLookAtPoint .x ) );
            DomUtils .addAttribute( child, "y", Double .toString( mLookAtPoint .y ) );
            DomUtils .addAttribute( child, "z", Double .toString( mLookAtPoint .z ) );
            result .appendChild( child );
        }
        {
            Element child = doc .createElement( "UpDirection" );
            DomUtils .addAttribute( child, "x", Double .toString( mUpDirection .x ) );
            DomUtils .addAttribute( child, "y", Double .toString( mUpDirection .y ) );
            DomUtils .addAttribute( child, "z", Double .toString( mUpDirection .z ) );
            result .appendChild( child );
        }
        {
            Element child = doc .createElement( "LookDirection" );
            DomUtils .addAttribute( child, "x", Double .toString( mLookDirection .x ) );
            DomUtils .addAttribute( child, "y", Double .toString( mLookDirection .y ) );
            DomUtils .addAttribute( child, "z", Double .toString( mLookDirection .z ) );
            result .appendChild( child );
        }
        return result;
    }


    public Camera( Element viewElem )
    {
        this();

        String str = viewElem .getAttribute( "near" );
        this .mNear = Float .parseFloat( str );
        str = viewElem .getAttribute( "far" );
        this .mFar = Float .parseFloat( str );
        str = viewElem .getAttribute( "width" );
        this .mWidth = Float .parseFloat( str );
        str = viewElem .getAttribute( "distance" );
        this .mDistance = Float .parseFloat( str );
        str = viewElem .getAttribute( "stereoAngle" );
        this .mStereoAngle = Float .parseFloat( str );
        str = viewElem .getAttribute( "parallel" );
        this .mOrthographic = Boolean .valueOf( str );
        {
            Element child = (Element) viewElem .getElementsByTagName( "LookAtPoint" ) .item( 0 );
            str = child .getAttribute( "x" );
            float x = Float .parseFloat( str );
            str = child .getAttribute( "y" );
            float y = Float .parseFloat( str );
            str = child .getAttribute( "z" );
            float z = Float .parseFloat( str );
            mLookAtPoint = new Point3f( x, y, z );
        }
        {
            Element child = (Element) viewElem .getElementsByTagName( "UpDirection" ) .item( 0 );
            str = child .getAttribute( "x" );
            float x = Float .parseFloat( str );
            str = child .getAttribute( "y" );
            float y = Float .parseFloat( str );
            str = child .getAttribute( "z" );
            float z = Float .parseFloat( str );
            mUpDirection = new Vector3f( x, y, z );
        }
        {
            Element child = (Element) viewElem .getElementsByTagName( "LookDirection" ) .item( 0 );
            str = child .getAttribute( "x" );
            float x = Float .parseFloat( str );
            str = child .getAttribute( "y" );
            float y = Float .parseFloat( str );
            str = child .getAttribute( "z" );
            float z = Float .parseFloat( str );
            mLookDirection = new Vector3f( x, y, z );
        }
    }
}
