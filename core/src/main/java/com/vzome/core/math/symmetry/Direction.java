

package com.vzome.core.math.symmetry;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.vzome.core.algebra.AlgebraicField;
import com.vzome.core.algebra.AlgebraicMatrix;
import com.vzome.core.algebra.AlgebraicNumber;
import com.vzome.core.algebra.AlgebraicVector;
import com.vzome.core.algebra.AlgebraicVectors;
import com.vzome.core.math.RealVector;

/**
 * A single orbit in a Symmetry group.
 * Consists of a collection of zones (Axis), each of which is an infinite family of parallel lines.
 * There is a prototype zone (Axis) which has index==0; ideally, that zone should
 * have normal vector =~ (1,e,e), for 0 < e << 1, but this is not true, historically.
 * 
 * The orbit is represented by a single "dot" on the fundamental region triangle, and typically
 * struts in the orbit are rendered with a shape and color unique from all other orbits.
 * 
 * @author Scott Vorthmann
 */
public class Direction implements Comparable<Direction>, Iterable<Axis>
{	
    @Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + index;
		result = prime * result
				+ ((mPrototype == null) ? 0 : mPrototype.hashCode());
		result = prime * result
				+ ((mSymmetryGroup == null) ? 0 : mSymmetryGroup.hashCode());
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
		Direction other = (Direction) obj;
		if (index != other.index) {
			return false;
		}
		if (mPrototype == null) {
			if (other.mPrototype != null) {
				return false;
			}
		} else if (!mPrototype.equals(other.mPrototype)) {
			return false;
		}
		if (mSymmetryGroup == null) {
			if (other.mSymmetryGroup != null) {
				return false;
			}
		} else if (!mSymmetryGroup.equals(other.mSymmetryGroup)) {
			return false;
		}
		return true;
	}

	private String mName, canonicalName;
    
    private final Axis[][][] zoneNames;
    
    private final Map<String, Axis> zoneVectors = new HashMap<>();
    
    private final Symmetry mSymmetryGroup;
    
    private final AlgebraicVector mPrototype;
    
    private boolean mStandard; // Standard orbits are typically used as defaults in a presentation layer.
    						   // They tend to correlate with axes of symmetry.
    						   // This concept probably should be removed here, and added at the presentation layer.

    private boolean mAutomatic; // Automatic orbits are not predefined statically, but are created as needed
    							// for new vectors.
    							// This concept probably should be removed here, and added at the presentation layer.

    private boolean hasHalfSizes; // True if half struts are typically useful.
    							  // This concept probably should be removed here, and added at the presentation layer.
    
    private final String[] scaleNames = new String[]{ "shorter", "short", "medium", "long" };

    @JsonIgnore
    public final AlgebraicNumber[] scales = new AlgebraicNumber[ scaleNames.length ];

    private AlgebraicNumber unitLength, unitLengthReciprocal;
    
    private double dotX = -999d, dotY = -999d;

    private static Logger logger = Logger .getLogger( "com.vzome.core.math.symmetry.Orbit" );

    public final void setAutomatic( boolean auto )
    {
        mAutomatic = auto;
    }
    
    @JsonIgnore
    public boolean isAutomatic()
    {
        return mAutomatic;
    }
    
    @JsonIgnore
    public boolean isStandard()
    {
        return mStandard;
    }
    
    private static int globalIndex = 0;
    
    private final int index, prototype, rotatedPrototype;

    public int canonicalize = 0;
    private boolean needsCanonicalization = false;
    
    public Direction( String name, Symmetry group, int prototype, int rotatedPrototype, AlgebraicVector vector, boolean isStd )
    {
        this.prototype = prototype;
        this.rotatedPrototype = rotatedPrototype;
        this.index = globalIndex++; // we want to just retain the order used to create these
        mStandard = isStd;
        mName = name;
        canonicalName = null;
        mSymmetryGroup = group;
        for ( int i = 0; i < scales .length; i++ ) {
            scales[ i ] = mSymmetryGroup .getField() .createPower( i - 1 );
        }
        mPrototype = vector;
        int order = group .getChiralOrder();
        zoneNames = new Axis[ 2 ][ 2 ][ order ];
    }
    
    private final Map<String, Axis> getZoneVectors()
    {
        if ( this.zoneVectors .isEmpty() ) {
            if ( logger .isLoggable( Level.FINER ) )
                logger .finer( "creating zone vectors: " + this .toString() );
            for ( int i = 0; i < this.mSymmetryGroup .getChiralOrder(); i++ ) {
                AlgebraicMatrix transform = this.mSymmetryGroup .getMatrix( i );
                Permutation perm = this.mSymmetryGroup .getPermutation( i );
                int j = perm .mapIndex( this.prototype );
                int rotated = perm .mapIndex( this.rotatedPrototype );
                AlgebraicVector normal = transform .timesColumn( this.mPrototype );
                int rot = this.mSymmetryGroup .getMapping( j, rotated );
                this .createAxis( j, rot, normal );
            }
        }
        return zoneVectors;
    }
    
    @Override
    public String toString()
    {
        return mSymmetryGroup .getName() + " " + this .mName;
    }
    
    public AlgebraicVector getPrototype()
    {
        return mPrototype;
    }
    
    @Override
    public Iterator<Axis> iterator()
    {
        return getZoneVectors() .values() .iterator();
    }
      
    @JsonIgnore
    public Symmetry getSymmetry()
    {
        return mSymmetryGroup;
    }
    
    public String getName()
    {
        return this .mName;
    }
    
    public String getCanonicalName()
    {
        if ( this .canonicalName == null ) {
            Axis canonicalAxis = this .getCanonicalAxis( 0, 0 );
            AlgebraicVector vector = canonicalAxis .normal();
            
            // canonicalAxis is on the -Z side of the XY plane, but we want the
            //  canonical name to reflect the +Z side, to match Observable
            AlgebraicNumber x = vector .getComponent( 0 );
            AlgebraicNumber y = vector .getComponent( 1 );
            AlgebraicNumber z = vector .getComponent( 2 ) .negate();
            
            if ( x .isZero() ) {  // TODO generalize this better!
                // not icosahedral symmetry
                this .canonicalName = this .mName;
            } else
            {
                // Now we do the Gnomonic projection, to the X=1 plane
                y = y .dividedBy( x );
                z = z .dividedBy( x );
                this .canonicalName = "["
                        + Arrays.toString( y .toTrailingDivisorExact() ) .replace( " ", "" )
                        + ","
                        + Arrays.toString( z .toTrailingDivisorExact() ) .replace( " ", "" )
                        + "]";
                logger.finer("Direction.canonicalName: " + this.canonicalName);
            }
        }
        return this .canonicalName;
    }
    
    public Axis getAxis( AlgebraicVector vector )
    {
        for (Axis axis : getZoneVectors() .values()) {
            AlgebraicVector normal = axis .normal();
            if ( AlgebraicVectors.areParallel( normal, vector ) ) {
                // parallel
                AlgebraicNumber dotProd = normal .dot( vector );
                if ( dotProd .evaluate() > 0 ) { // positive
                    return axis;
                } else {
                	AlgebraicMatrix principalReflection = this .mSymmetryGroup .getPrincipalReflection();
                	if ( principalReflection == null ) {
                		// the traditional way... anti-parallel means just flip the sense
                        int opp = ( axis .getSense() + 1 ) % 2;
                        return getAxis( opp, axis .getOrientation() );
                	}
                	else {
                		// anti-parallel mean flip inbound to outbound or vice-versa
                		return getAxis( axis .getSense(), axis .getOrientation(), ! axis .isOutbound() );
                	}
                }
            }
        }
        return null;
    }
    
    public Axis getAxis( RealVector vector )
    {
        return this .getSymmetry() .getAxis( vector, Collections .singleton( this ) );
    }
    
    Axis getChiralAxis( RealVector vector )
    {
        if ( RealVector .ORIGIN .equals( vector ) ) {
            return null;
        }
        // largest cosine means smallest angle
        //  and cosine is (a . b ) / |a| |b|
        double vectorLength = vector .length();
        Set<Axis> checked = new HashSet<>();
        int closestOrientation = 0;
        int closestSense = Symmetry.PLUS;
        Axis closestAxis = this .getCanonicalAxis( Symmetry.PLUS, 0 );
        checked .add( closestAxis );
        RealVector axisV = closestAxis .normal() .toRealVector(); // TODO invert the Embedding to get this right
        double maxCosine = vector .dot( axisV ) / (vectorLength * axisV .length());
        if ( maxCosine < 0 ) {
            // wrong hemisphere, flip to the other one
            closestAxis = this .getCanonicalAxis( Symmetry.MINUS, 0 );
            closestSense = Symmetry.MINUS;
            checked .add( closestAxis );
            axisV = closestAxis .normal() .toRealVector();
            maxCosine = vector .dot( axisV ) / (vectorLength * axisV .length());
        }
        boolean finished = false;
        while ( !finished ) {
            int[] incidentOrientations = this .getSymmetry() .getIncidentOrientations( closestOrientation );
            if ( incidentOrientations == null ) {
                // this symmetry group has not implemented getIncidentOrientations, so we'll fall through to brute force
                break;
            }
            int reverseSense = (closestSense+1)%2;
            for ( int i : incidentOrientations ) {
                Axis neighbor = this .getCanonicalAxis( reverseSense, i );
                if ( checked .contains( neighbor ) )
                    continue;
                checked .add( neighbor );
                axisV = neighbor .normal() .toRealVector();
                double cosine = vector .dot( axisV ) / (vectorLength * axisV .length());
                if ( cosine > maxCosine ) {
                    maxCosine = cosine;
                    closestAxis = neighbor;
                    closestOrientation = i;
                    closestSense = reverseSense;  // this assignment prevents the "success" return below
                }
            }
            if ( reverseSense != closestSense ) {
                // didn't flip the sense, which means we didn't find a better cosine,
                //   so we're done, maxed out.
                return closestAxis;
            }
        }
        // Fall back to brute force search
        return this .getAxisBruteForce( vector );
    }
    
    Axis getAxisBruteForce( RealVector vector )
    {        
        Axis closestAxis = null;
        double maxCosine = -1d;
        for ( Axis axis : this ) {
            RealVector axisV = axis .normal() .toRealVector(); // TODO invert the Embedding to get this right
            double cosine = vector .dot( axisV ) / (vector .length() * axisV .length());
            if ( cosine > maxCosine ) {
                maxCosine = cosine;
                closestAxis = axis;
            }
		}
        return closestAxis;
    }

    // default visibility; only used for creating frame orbits
    boolean zoneInitialized( int sense, int unit )
    {
        return zoneNames[ 1 ][ sense ][ unit ] != null;
    }

    public final Axis getAxis( int sense, int index )
    {
        return this .getAxis( sense, index, true );
    }
    
    public final Axis getAxis( int sense, int index, boolean outbound )
    {
        this.getZoneVectors(); // force initialization of zones
        return zoneNames[ outbound? 1 : 0 ][ sense ][ index ];
    }
    
    public Direction withCorrection()
    {
        this .needsCanonicalization = true;
        return this;
    }
    
    /**
     * Get the axis that protrudes from the canonical direction on the zome ball.
     * Many Directions (orbits) are created without regard to whether "axis 0" actually sticks out
     * of the ball in the fundamental domain with index 0.
     * @param sense
     * @param index
     * @return
     */
    public Axis getCanonicalAxis( int sense, int index )
    {
        if ( this .needsCanonicalization ) {
            // making this lazy so that Direction init doesn't have to compute all zones
            Axis treatedAs0 = this .getAxisBruteForce( RealVector.DIRECTION_0 );
            this .canonicalize = treatedAs0 .getOrientation();
            if ( treatedAs0 .getSense() == Symmetry.MINUS )
                this .canonicalize *= -1;
            this .needsCanonicalization = false;
        }
        if ( this.canonicalize != 0 )
        {
            if ( this .canonicalize < 0 )
                sense = ( sense + 1 ) % 2;
            Permutation target = this .mSymmetryGroup .getPermutation( index );
            index = target .mapIndex( Math.abs( this .canonicalize ) );
        }
        return this .getAxis( sense, index );
    }

    public void createAxis( int orientation, int rotation, int[][] norm )
    {
        AlgebraicVector aNorm = this .mSymmetryGroup .getField() .createVector( norm );
        this .createAxis( orientation, rotation, aNorm );
    }
    
    /**
     * Create a zone as a pair of opposite vectors, named as two Axes,
     * or as two pair, in the case of symmetry groups that have a principal reflection.
     * 
     * @param orientation the index of this axis (zone) in its Direction (orbit)
     * @param rotation the index of the permutation that is a rotation around this axis, or NO_ROTATION
     */
    public final void createAxis( int orientation, int rotation, AlgebraicVector norm )
    {
        Permutation perm = mSymmetryGroup .getPermutation( rotation );
        recordZone( this, orientation, Symmetry.PLUS, rotation, perm, norm, true );
        
        AlgebraicMatrix inversion = this .mSymmetryGroup .getPrincipalReflection();
        if ( inversion == null ) {
        	// the legacy case, most existing symmetry groups
        	if ( perm != null )
        		// This is a bug! We should have adjusted rotation to be perm .mapIndex( 0 );
        		//  Now we're stuck with it, unless we can demonstrate that is has no impact
        		//  on opening legacy models.
        		perm = perm .inverse();
        	recordZone( this, orientation, Symmetry.MINUS, rotation, perm, norm .negate(), true );
        }
        else {
        	// This symmetry group has a principal reflection, so we create the zone
        	//   and its reflection at the same time.
        	// (First example is the "heptagon antiprism corrected" symmetry,
        	//    though the "dodecagon" symmetry should have a corrected form, too.)
        	int reverseRotation = rotation;
        	Permutation reversePerm = perm;
        	if ( perm != null ) {
        		reversePerm = perm .inverse();
        		reverseRotation = perm .mapIndex( 0 );
        	}
    		recordZone( this, orientation, Symmetry.PLUS, reverseRotation, reversePerm, norm .negate(), false );
        	AlgebraicVector reflectedNorm = inversion .timesColumn( norm );
        	recordZone( this, orientation, Symmetry.MINUS, reverseRotation, reversePerm, reflectedNorm, true );
        	reflectedNorm = reflectedNorm .negate();
    		recordZone( this, orientation, Symmetry.MINUS, rotation, perm, reflectedNorm, false );
        }
    }
    
    private final void recordZone( Direction dir, int orientation, int sense, int rotation, Permutation rotPerm, AlgebraicVector normal, boolean outbound )
    {
        Axis zone = this.zoneVectors .get( normal .toString() );
        if ( zone == null )
        {
            // vector never seen before
            zone = new Axis( this, orientation, sense, rotation, rotPerm, normal, outbound );
            this.zoneVectors .put( normal .toString(), zone );
            if ( logger .isLoggable( Level.FINER ) )
                logger .finer( "creating zone " + zone .toString() + " " + normal .toString() );
        }
        else 
        {
            // known vector, but maybe we have a better name for the zone
            if ( outbound && ! zone .isOutbound() ) 
            {
                // always upgrade from inbound to outbound, regardless of sense
                String oldName = zone .toString();
                zone .rename( sense, orientation, true );
                if ( logger .isLoggable( Level.FINER ) )
                    logger .finer( "zone " + oldName + " upgraded to " + zone .toString() );
            } 
            else if ( zone .isOutbound() && ! outbound )
            {
                // never downgrade from outbound to inbound, regardless of sense
                if ( logger .isLoggable( Level.FINEST ) )
                    logger .finest( "zone " + zone .toString() + " aliased as " + ( ( sense == Axis.MINUS )? "-" : "" ) + orientation + ( outbound? "" : "i" ) );
            }
            else if ( sense == Axis.PLUS && ( zone .getSense() == Axis.MINUS ) )
            {
                // both outbound or both inbound, but upgrade from MINUS to PLUS
                String oldName = zone .toString();
                zone .rename( sense, orientation, outbound );
                if ( logger .isLoggable( Level.FINER ) )
                    logger .finer( "zone " + oldName + " upgraded to " + zone .toString() );
            }
            else {
                if ( logger .isLoggable( Level.FINEST ) )
                    logger .finest( "zone " + zone .toString() + " aliased as " + ( ( sense == Axis.MINUS )? "-" : "" ) + orientation + ( outbound? "" : "i" ) );
            }
        }
        zoneNames[ outbound? 1 : 0 ][ sense ][ orientation ] = zone;
    }

    @Override
    public int compareTo( Direction other )
    {
        return this.index - other.index;
    }
    
    public void setHalfSizes( boolean value )
    {
        hasHalfSizes = value;
    }

    public boolean hasHalfSizes()
    {
        return hasHalfSizes;
    }
    
    public void setScaleNames( String[] names )
    {
        for (int i = 0; i < names.length; i++)
        {
            scaleNames[ i ] = names[ i ];
        }
    }
    
    public String getScaleName( int scale )
    {
        if ( scale < scaleNames.length && scale >= 0 )
            return scaleNames[ scale ];
        else
            return "scale " + (scale-1);
    }
    
    public void setUnitLength( AlgebraicNumber unitLength )
    {
        this .unitLength = unitLength;
        this .unitLengthReciprocal = unitLength .reciprocal(); // do the matrix inversion just once
    }

    public AlgebraicNumber getUnitLength()
    {
        if ( unitLength == null)
            return mSymmetryGroup .getField() .one();
        else
            return unitLength;
    }
    
    public static final int USER_SCALE = 3;
    
    public AlgebraicNumber getLengthInUnits( AlgebraicNumber rawLength )
    {
        // reproduce the calculation in LengthModel .setActualLength()
        // TODO rationalize that
        AlgebraicField field = mSymmetryGroup .getField();
        AlgebraicNumber scaledLength = rawLength .times( field .createPower( - USER_SCALE ) );
        if ( unitLength == null)
            return scaledLength;
        else
            return scaledLength .times( unitLengthReciprocal );
    }

    public String getLengthName( AlgebraicNumber length )
    {
        for ( int i = 0; i < scales .length; i++ ) {
            if ( scales[ i ] .equals( length ) ) {
                return scaleNames[ i ];
            }
        }
        return "";
    }

    public void getLengthExpression( StringBuffer buf, AlgebraicNumber length )
    {
        int bufLen = buf.length();
        buf .append(getLengthName( length ) );
        if(buf.length() == bufLen) {
            // Be sure to append something before the colon delimiter
            // so the StringTokenizer that uses buf doesn't choke on a missing token.
            buf .append( " " );
        }
        buf .append( ":" );
        length .getNumberExpression( buf, AlgebraicField .EXPRESSION_FORMAT );
    }

    @JsonIgnore
	public double getDotX()
	{
		return this .dotX;
	}

    @JsonIgnore
	public double getDotY()
	{
		return this .dotY;
	}
	
	public void setDotLocation( double x, double y )
	{
		this .dotX = x;
		this .dotY = y;
	}
}
