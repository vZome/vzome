package com.vzome.core.viewing;

import javax.vecmath.AxisAngle4d;
import javax.vecmath.Matrix3d;
import javax.vecmath.Matrix4d;
import javax.vecmath.Point3d;
import javax.vecmath.Quat4d;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector3f;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.vzome.core.math.DomUtils;
import com.vzome.core.render.Renderable;

public class ViewModel implements Renderable
{
    /**
	 * The original frustum.
	 */
	public static final double ORIG_WIDTH = 18f, ORIG_DISTANCE = 40f;
	
	protected static final double ORIG_NEAR = 0.1f, ORIG_FAR = 2 * ORIG_DISTANCE;
    
	protected static final Vector3d ORIG_LOOK = new Vector3d(0,0,-1);
	
	protected static final Vector3d ORIG_UP = new Vector3d(0,1,0);

	
	private Point3d mLookAtPoint = new Point3d();
	
	private double mNear = ORIG_NEAR;
	
	private double mFar = ORIG_FAR;

	private double mWidth = ORIG_WIDTH;
	
	private double mDistance = ORIG_DISTANCE;

	private Vector3d mUpDirection = new Vector3d( ORIG_UP ), mLookDirection = new Vector3d( ORIG_LOOK );

	private boolean mOrthographic = false;
    
	private double mStereoAngle = 0d;


    public boolean equals( Object object )
    {
        if ( object == null )
            return false;
        if ( object == this )
            return true;
        if ( ! ( object instanceof ViewModel ) )
            return false;
        
        ViewModel that = (ViewModel) object;

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
        if ( ! this.mLookDirection .equals( that.mLookDirection ) )
            return false;
        return true;
    }

    public ViewModel()
    {
    }
    
    public ViewModel( ViewModel prototype )
    {
        this .mLookAtPoint = new Point3d( prototype .mLookAtPoint );
        this .mNear = prototype .mNear;
        this .mFar = prototype .mFar;
        this .mWidth = prototype .mWidth;
        this .mDistance = prototype .mDistance;
        this .mUpDirection = new Vector3d( prototype .mUpDirection );
        this .mLookDirection = new Vector3d( prototype .mLookDirection );
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
    
    public void setStereoAngle( double value )
    {
        mStereoAngle = value;
        // now make sure the stereo/mono switch keeps effective object sizes the same
        if ( value == 0d )
            mWidth = mWidth * 2d;
        else
            mWidth = mWidth / 2d;
    }
	
	public float getFieldOfView()
	{
		return (float) ( 2 * Math .atan( (mWidth/2) / mDistance ) );
	}

	public float getViewDistance()
	{
		return (float) mDistance;
	}
	
	public void getViewOrientation( Vector3d lookDir, Vector3d upDir )
	{
		lookDir .set( mLookDirection );
		upDir .set( mUpDirection );
	}
    
    static final double EPSILON_ABSOLUTE = 1.0e-5;

    private static final boolean almostZero(double a)
    {
    	return ((a < EPSILON_ABSOLUTE) && (a > -EPSILON_ABSOLUTE));
    }

    /**
     * Sets this transform to the identity matrix.
     */
    private static void setIdentity( double[] mat )
    {
    	mat[0] = 1.0;  mat[1] = 0.0;  mat[2] = 0.0;  mat[3] = 0.0;
    	mat[4] = 0.0;  mat[5] = 1.0;  mat[6] = 0.0;  mat[7] = 0.0;
    	mat[8] = 0.0;  mat[9] = 0.0;  mat[10] = 1.0; mat[11] = 0.0;
    	mat[12] = 0.0; mat[13] = 0.0; mat[14] = 0.0; mat[15] = 1.0;
    }

    /**
     * Sets the value of this transform to the matrix conversion
     * of the double precision axis-angle argument; all of the matrix
     * values are modified.
     * @param a1 the axis-angle to be converted (x, y, z, angle)
     */
    private static void set( AxisAngle4d a1, double[] mat )
    {
    	double mag = Math.sqrt( a1.x*a1.x + a1.y*a1.y + a1.z*a1.z);

    	if (almostZero(mag)) {
    		setIdentity( mat );
    	} else {
    		mag = 1.0/mag;
    		double ax = a1.x*mag;
    		double ay = a1.y*mag;
    		double az = a1.z*mag;

    		double sinTheta = Math.sin(a1.angle);
    		double cosTheta = Math.cos(a1.angle);
    		double t = 1.0 - cosTheta;

    		double xz = ax * az;
    		double xy = ax * ay;
    		double yz = ay * az;

    		mat[0] = t * ax * ax + cosTheta;
    		mat[1] = t * xy - sinTheta * az;
    		mat[2] = t * xz + sinTheta * ay;
    		mat[3] = 0.0;

    		mat[4] = t * xy + sinTheta * az;
    		mat[5] = t * ay * ay + cosTheta;
    		mat[6] = t * yz - sinTheta * ax;
    		mat[7] = 0.0;

    		mat[8] = t * xz - sinTheta * ay;
    		mat[9] = t * yz + sinTheta * ax;
    		mat[10] = t * az * az + cosTheta;
    		mat[11] = 0.0;

    		mat[12] = 0.0;
    		mat[13] = 0.0;
    		mat[14] = 0.0;
    		mat[15] = 1.0;
    	}
    }

    /**
     * Transforms the normal parameter by this transform and places the value
     * back into normal.  The fourth element of the normal is assumed to be zero.
     * @param normal   the input normal to be transformed
     */
    private static void transform( double[] mat, Vector3d normal )
    {
        double x =  mat[0]*normal.x + mat[1]*normal.y + mat[2]*normal.z;
        double y =  mat[4]*normal.x + mat[5]*normal.y + mat[6]*normal.z;
        normal.z =  mat[8]*normal.x + mat[9]*normal.y + mat[10]*normal.z;
        normal.x = x;
        normal.y = y;
    }

    /**
     * Helping function that specifies the position and orientation of a
     * view matrix. The inverse of this transform can be used to control
     * the ViewPlatform object within the scene graph.
     * @param eye the location of the eye
     * @param center a point in the virtual world where the eye is looking
     * @param up an up vector specifying the frustum's up direction
     */
    private static void lookAt( double[] mat, Point3d eye, Point3d center, Vector3d up)
    {
    	double forwardx,forwardy,forwardz,invMag;
    	double upx,upy,upz;
    	double sidex,sidey,sidez;

    	forwardx =  eye.x - center.x;
    	forwardy =  eye.y - center.y;
    	forwardz =  eye.z - center.z;

    	invMag = 1.0/Math.sqrt( forwardx*forwardx + forwardy*forwardy + forwardz*forwardz);
    	forwardx = forwardx*invMag;
    	forwardy = forwardy*invMag;
    	forwardz = forwardz*invMag;


    	invMag = 1.0/Math.sqrt( up.x*up.x + up.y*up.y + up.z*up.z);
    	upx = up.x*invMag;
    	upy = up.y*invMag;
    	upz = up.z*invMag;

    	// side = Up cross forward
    	sidex = upy*forwardz-forwardy*upz;
    	sidey = upz*forwardx-upx*forwardz;
    	sidez = upx*forwardy-upy*forwardx;

    	invMag = 1.0/Math.sqrt( sidex*sidex + sidey*sidey + sidez*sidez);
    	sidex *= invMag;
    	sidey *= invMag;
    	sidez *= invMag;

    	// recompute up = forward cross side

    	upx = forwardy*sidez-sidey*forwardz;
    	upy = forwardz*sidex-forwardx*sidez;
    	upz = forwardx*sidey-forwardy*sidex;

    	// transpose because we calculated the inverse of what we want
    	mat[0] = sidex;
    	mat[1] = sidey;
    	mat[2] = sidez;

    	mat[4] = upx;
    	mat[5] = upy;
    	mat[6] = upz;

    	mat[8] =  forwardx;
    	mat[9] =  forwardy;
    	mat[10] = forwardz;

    	mat[3] = -eye.x*mat[0] + -eye.y*mat[1] + -eye.z*mat[2];
    	mat[7] = -eye.x*mat[4] + -eye.y*mat[5] + -eye.z*mat[6];
    	mat[11] = -eye.x*mat[8] + -eye.y*mat[9] + -eye.z*mat[10];

    	mat[12] = mat[13] = mat[14] = 0;
    	mat[15] = 1;
    }


    /**
     * Get the mapping from view to world coordinates
     * @param trans
     */
    public void getViewTransform( Matrix4d matrix, double angle )
    {
        Point3d eyePoint = new Point3d( mLookAtPoint );
        Vector3d dir = new Vector3d( mLookDirection );
        if ( angle != 0d ) {
            double[] rotMat = new double[16];
            AxisAngle4d rotAA = new AxisAngle4d( mUpDirection, angle );
            set( rotAA, rotMat );
            transform( rotMat, dir );
        }
        dir .scale( -mDistance );
        eyePoint .add( dir );
        
        double[] mat = new double[16];
        lookAt( mat, eyePoint, mLookAtPoint, mUpDirection );
        matrix .set( mat );
    }

    
//	public void getWorldRotation( Quat4d q )
//	{
//		Vector3d axis = new Vector3d( q.x, q.y, q.z );
//
//		Transform3D viewTrans = new Transform3D();
//		getViewTransform( viewTrans, 0d );
//        viewTrans .invert();
//
//		// now map the axis back to world coordinates
//		viewTrans .transform( axis );
//		q.x = axis.x; q.y = axis.y; q.z = axis.z;
//	}
	

	public Point3d getLookAtPoint()
	{
		return mLookAtPoint;
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
	
	
	public void setLookAtPoint( Point3d lookAt )
	{
		mLookAtPoint = lookAt;
	}
    

	public void addViewpointRotation( Quat4d rotation )
	{
		Matrix3d m = new Matrix3d();
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
		double ratio = distance / mDistance;
		mDistance = distance;
		mNear = mNear * ratio;
		mFar = mFar * ratio;
		mWidth = mWidth * ratio;
	}

    public boolean isStereo()
    {
        return mStereoAngle != 0d;
    }

	public void getStereoViewTransform( Matrix4d trans, int eye )
	{
		getViewTransform( trans, (eye==1)? -mStereoAngle : mStereoAngle );
	}

	public double getNearClipDistance()
	{
		return mNear;
	}

	public double getFarClipDistance()
	{
		return mFar;
	}

	public double getWidth()
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


    public ViewModel( Element viewElem )
    {
        String str = viewElem .getAttribute( "near" );
        this .mNear = Double .parseDouble( str );
        str = viewElem .getAttribute( "far" );
        this .mFar = Double .parseDouble( str );
        str = viewElem .getAttribute( "width" );
        this .mWidth = Double .parseDouble( str );
        str = viewElem .getAttribute( "distance" );
        this .mDistance = Double .parseDouble( str );
        str = viewElem .getAttribute( "stereoAngle" );
        this .mStereoAngle = Double .parseDouble( str );
        str = viewElem .getAttribute( "parallel" );
        this .mOrthographic = Boolean .valueOf( str ) .booleanValue();
        {
            Element child = (Element) viewElem .getElementsByTagName( "LookAtPoint" ) .item( 0 );
            str = child .getAttribute( "x" );
            double x = Double .parseDouble( str );
            str = child .getAttribute( "y" );
            double y = Double .parseDouble( str );
            str = child .getAttribute( "z" );
            double z = Double .parseDouble( str );
            mLookAtPoint = new Point3d( x, y, z );
        }
        {
            Element child = (Element) viewElem .getElementsByTagName( "UpDirection" ) .item( 0 );
            str = child .getAttribute( "x" );
            double x = Double .parseDouble( str );
            str = child .getAttribute( "y" );
            double y = Double .parseDouble( str );
            str = child .getAttribute( "z" );
            double z = Double .parseDouble( str );
            mUpDirection = new Vector3d( x, y, z );
        }
        {
            Element child = (Element) viewElem .getElementsByTagName( "LookDirection" ) .item( 0 );
            str = child .getAttribute( "x" );
            double x = Double .parseDouble( str );
            str = child .getAttribute( "y" );
            double y = Double .parseDouble( str );
            str = child .getAttribute( "z" );
            double z = Double .parseDouble( str );
            mLookDirection = new Vector3d( x, y, z );
        }
    }
    
    private Object renderedObject = null;


    public Object getRenderedObject()
    {
        // TODO Auto-generated method stub
        return renderedObject;
    }

    public void render( Object renderer )
    {
        // TODO Auto-generated method stub
        
    }

    public void setRenderedObject( Object renderedObject )
    {
        this.renderedObject = renderedObject;
    }
}
