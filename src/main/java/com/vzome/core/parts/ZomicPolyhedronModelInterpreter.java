package com.vzome.core.parts;

import java.util.HashMap;
import java.util.Map;

import com.vzome.core.algebra.AlgebraicNumber;
import com.vzome.core.algebra.AlgebraicVector;
import com.vzome.core.math.Polyhedron;
import com.vzome.core.math.symmetry.Axis;
import com.vzome.core.math.symmetry.Permutation;
import com.vzome.core.math.symmetry.Symmetry;
import com.vzome.core.render.AbstractZomicEventHandler;
import com.vzome.core.zomic.Interpreter;
import com.vzome.core.zomic.program.ZomicStatement;

/**
 * builds a polyhedron model from a Zomic script.
 */
public class ZomicPolyhedronModelInterpreter extends Interpreter
{
    protected Polyhedron mPolyhedron;

    protected transient Polyhedron.Face m_face;

    protected transient Map<String, Integer> m_labels;

    private static class LocationTracker extends AbstractZomicEventHandler
    {
        private AlgebraicVector mLocation;

        protected final AlgebraicNumber m_variableLength;

        public LocationTracker( Permutation orientation, int handedness,
                AlgebraicNumber variableLength, Symmetry symm )
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

        @Override
        protected AbstractZomicEventHandler copyLocation()
        {
            return new LocationTracker( this );
        }

        @Override
        protected void restoreLocation( AbstractZomicEventHandler changed )
        {
            mLocation = ((LocationTracker) changed).mLocation;
        }

        @Override
        public void step( Axis axis, AlgebraicNumber length )
        {
            axis = mOrientation.permute( axis, mHandedNess );
            if ( length .isZero() )
                // see XML2AST.startElement()
                length = m_variableLength;
            else
                length = length .times( mScale );

            mLocation = mLocation .plus( axis .normal() .scale( length ) );
        }

        public AlgebraicVector getLocation()
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
    public ZomicPolyhedronModelInterpreter( Symmetry symmetry, ZomicStatement zomicProgram,
            AlgebraicNumber variableLength, Permutation orientation, int handedness )
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
    
    @Override
    public void visitLabel( String id )
    {
        if ( UNIT_START.equals( id ) ) {
            m_face = null;
            m_labels = new HashMap<>();
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
            AlgebraicVector loc = ((LocationTracker) mEvents).getLocation();
            AlgebraicVector halfLoc = loc .scale( mPolyhedron .getField() .createRational( 1, 2 ) );
            Integer vertexObj = mPolyhedron.addIndexedVertex( halfLoc );
            m_labels.put( id, vertexObj );
            // System .out .println ( id + " = " + loc );
        } else {
            Integer vertexObj = m_labels.get( id );
            if ( ((LocationTracker) mEvents).isLeftHanded() )
                m_face.add( 0, vertexObj );
            else
                m_face.add( vertexObj );
        }

    }
}
