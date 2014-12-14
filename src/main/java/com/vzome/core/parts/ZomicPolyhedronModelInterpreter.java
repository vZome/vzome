package com.vzome.core.parts;

import java.util.HashMap;
import java.util.Map;

import com.vzome.core.algebra.AlgebraicField;
import com.vzome.core.algebra.RationalNumbers;
import com.vzome.core.math.Polyhedron;
import com.vzome.core.math.symmetry.Axis;
import com.vzome.core.math.symmetry.Permutation;
import com.vzome.core.math.symmetry.Symmetry;
import com.vzome.core.render.AbstractZomicEventHandler;
import com.vzome.core.zomic.Interpreter;
import com.vzome.core.zomic.program.Anything;
import com.vzome.core.zomic.program.Move;

/**
 * builds a polyhedron model from a Zomic script.
 */
public class ZomicPolyhedronModelInterpreter extends Interpreter
{
    protected Polyhedron mPolyhedron;

    protected transient Polyhedron.Face m_face;

    protected transient Map m_labels;

    private static class LocationTracker extends AbstractZomicEventHandler
    {
        private int[] mLocation;

        protected final int[] m_variableLength;

        public LocationTracker( Permutation orientation, int handedness,
                int[] variableLength, Symmetry symm )
        {
            super( symm );
            mLocation = symm .getField() .origin( 3 );
            mOrientation = orientation;
            mHandedNess = handedness;
            m_variableLength = variableLength;
        }

        private LocationTracker( LocationTracker prototype )
        {
            super( prototype.mSymmetry );
            mLocation = prototype.mLocation;
            m_variableLength = prototype.m_variableLength;
        }

        protected AbstractZomicEventHandler copyLocation()
        {
            return new LocationTracker( this );
        }

        protected void restoreLocation( AbstractZomicEventHandler changed )
        {
            mLocation = ((LocationTracker) changed).mLocation;
        }

        public void step( Axis axis, int[] length )
        {
            AlgebraicField f = mSymmetry .getField();
            axis = mOrientation.permute( axis, mHandedNess );
            if ( length == Move.VARIABLE_SIZE )
                length = m_variableLength;
            else
                length = f .multiply( length, mScale );

            mLocation = f .add( mLocation, f .scaleVector( axis.normal(), length ) );
        }

        public int[] /*AlgebraicVector*/ getLocation()
        {
            return mLocation;
        }

        public boolean isLeftHanded()
        {
            return mHandedNess == Symmetry.MINUS;
        }

    }

    /**
     * Interprets the zomic program using the given permutation and size
     * variable/
     * 
     * @param zomicProgram
     * @param variableSize
     *            value to replace "-99" when it appears as a strut size
     * @param orientation
     */
    public ZomicPolyhedronModelInterpreter( Symmetry symmetry, Anything zomicProgram,
            int[] /*AlgebraicNumber*/ variableLength, Permutation orientation, int handedness )
    {
        super( new LocationTracker( orientation, handedness, variableLength, symmetry ), symmetry );

        this.mPolyhedron = new Polyhedron( symmetry .getField() );

        try {
            zomicProgram.accept( this );
        } catch ( Exception e ) {
            e.printStackTrace();
            throw new RuntimeException();
        }
    }

    public Polyhedron getPolyhedron()
    {
        return mPolyhedron;
    }

    public static final String UNIT_START = "unit.start",
            UNIT_FACE = "unit.face", UNIT_END = "unit.end";

    public static final String STRIP_START = "strip.start",
            STRIP_END = "strip.end";

    private static final int[] TWO = { 2, 1 };
    
    public void visitLabel( String id )
    {
        if ( UNIT_START.equals( id ) ) {
            m_face = null;
            m_labels = new HashMap();
            return;
        }
        if ( UNIT_FACE.equals( id ) ) {
            if ( m_face != null )
                mPolyhedron.addFace( m_face );
            m_face = mPolyhedron .newFace();
            return;
        }
        if ( UNIT_END.equals( id ) ) {
            if ( m_face != null )
                mPolyhedron.addFace( m_face );
            return;
        }
        if ( m_face == null ) {
            int[] /*AlgebraicVector*/ loc = ((LocationTracker) mEvents).getLocation();
            int[] halfLoc = new int[ loc.length ];
            for ( int i = 0; i < loc.length/2; i++ )
                RationalNumbers .divide( loc, i, TWO, 0, halfLoc, i );
            Integer vertexObj = mPolyhedron.addVertex( halfLoc );
            m_labels.put( id, vertexObj );
            // System .out .println ( id + " = " + loc );
        } else {
            Integer vertexObj = (Integer) m_labels.get( id );
            if ( ((LocationTracker) mEvents).isLeftHanded() )
                m_face.add( 0, vertexObj );
            else
                m_face.add( vertexObj );
        }

    }
}
