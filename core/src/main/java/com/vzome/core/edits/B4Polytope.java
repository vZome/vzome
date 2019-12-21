
//(c) Copyright 2007, Scott Vorthmann.  All rights reserved.

package com.vzome.core.edits;


import org.w3c.dom.Element;

import com.vzome.core.algebra.AlgebraicField;
import com.vzome.core.algebra.AlgebraicNumber;
import com.vzome.core.algebra.AlgebraicVector;
import com.vzome.core.commands.AttributeMap;
import com.vzome.core.commands.XmlSaveFormat;
import com.vzome.core.construction.FreePoint;
import com.vzome.core.construction.Point;
import com.vzome.core.construction.Segment;
import com.vzome.core.construction.SegmentJoiningPoints;
import com.vzome.core.editor.ChangeManifestations;
import com.vzome.core.editor.Selection;
import com.vzome.core.math.DomUtils;
import com.vzome.core.math.Projection;
import com.vzome.core.math.QuaternionProjection;
import com.vzome.core.math.symmetry.B4Group;
import com.vzome.core.math.symmetry.CoxeterGroup;
import com.vzome.core.math.symmetry.WythoffConstruction;
import com.vzome.core.model.RealizedModel;

/**
 * This is only used when opening legacy files.  The UI and controllers now use the generic Polytope4d.
 * @author Scott Vorthmann
 *
 */
public class B4Polytope extends ChangeManifestations
{
    private int index;
    private Projection proj;
    private Segment symmAxis;
	private final AlgebraicField field;

    public B4Polytope( Selection selection, RealizedModel realized )
    {
        this( selection, realized, realized .getField(), null, 0 );
    }

    public B4Polytope( Selection selection, RealizedModel realized, AlgebraicField field, Segment symmAxis, int index )
    {
        super( selection, realized );
        this.field = field;

        this.index = index;
        this.symmAxis = symmAxis;
    }

    @Override
    protected String getXmlElementName()
    {
        return "B4Polytope";
    }

    @Override
    public void getXmlAttributes( Element result )
    {
        DomUtils .addAttribute( result, "dynkin", Integer .toString( this.index, 2 ) );
        if ( symmAxis != null )
            XmlSaveFormat .serializeSegment( result, "start", "end", symmAxis );
    }

    @Override
    public void setXmlAttributes( Element xml, XmlSaveFormat format )
    {
        String binary = xml .getAttribute( "dynkin" );
        this.index = Integer .parseInt( binary, 2 );
        
        if ( format .commandEditsCompacted() )
            this.symmAxis = format .parseSegment( xml, "start", "end" );
        else
        {
            AttributeMap attrs = format .loadCommandAttributes( xml );
            this.symmAxis = (Segment) attrs .get( "rotation" );
        }
    }
    
    @Override
    public void perform()
    {
        if ( symmAxis == null )
            this.proj = new Projection .Default( field );
        else
        {
            AlgebraicNumber scale = field .createPower( -5 );
            this.proj = new QuaternionProjection( field, null, symmAxis .getOffset() .scale( scale ) );
        }
        
        AlgebraicNumber[] edgeScales = new AlgebraicNumber[4];
        for (int i = 0; i < edgeScales .length; i++)
        {
            edgeScales[ i ] = field .createPower( 0 );
        }

        CoxeterGroup group = new B4Group( field );
        WythoffConstruction .constructPolytope( group, this.index, this.index, edgeScales, group, new WythoffListener( field ) );
        
        redo();
    }
    
    private class WythoffListener implements WythoffConstruction.Listener
    {
        private int numVertices = 0;
        private final AlgebraicNumber scale;
        
        public WythoffListener( AlgebraicField field )
        {
            this.scale = field .createPower( 5 );
        }

        @Override
        public Object addEdge( Object p1, Object p2 )
        {
            Segment edge = new SegmentJoiningPoints( (Point) p1, (Point) p2 );
            manifestConstruction( edge );
            return edge;
        }

        @Override
        public Object addFace( Object[] vertices )
        {
            return null;
        }

        @Override
        public Object addVertex( AlgebraicVector vertex )
        {
            AlgebraicVector projected = vertex;
            
            if ( proj != null )
                projected = proj .projectImage( vertex, true );
            
            Point p = new FreePoint( projected .scale( scale ) );
            p .setIndex( numVertices++ );
            manifestConstruction( p );
            return p;
        }
        
    }
    
//    private static class B3Group implements CoxeterGroup
//    {
//        public int[] createModelVertex( int index, int[][] edges )
//        {
//            // TODO Auto-generated method stub
//            return null;
//        }
//
//        public int getOrder()
//        {
//            return 6*8;
//        }
//
//        public int[] groupAction( int[] model, int element )
//        {
//            int perm = element / 8;
//            int signs = element % 8;
//            IntegralNumber coords[] = new IntegralNumber[ 3 ];
//            for ( int c = 0; c < 3; c++ ) {
//                IntegralNumber coord = null;
//                switch ( B3_PERMS[ perm ][ c ] ) {
//                case 0:
//                    coord = model .getX();
//                    break;
//                case 1:
//                    coord = model .getY();
//                    break;
//                case 2:
//                    coord = model .getZ();
//                    break;
//                default:
//                    break;
//                }
//                if ( signs%2 == 0 )
//                    coords[ c ] = coord;
//                else
//                    coords[ c ] = coord .neg();
//                signs = signs >> 1;
//            }
//            return model .getFactory() .createGoldenVector( coords[0], coords[1], coords[2], model .getFactory() .zero(), model .getFactory() .one() );
//        }
//
//        private static final int[][] B3_PERMS = new int[][]{ {0,1,2}, {0,2,1}, {1,0,2}, {1,2,0}, {2,1,0}, {2,0,1} };
//
//        public int[] getOrigin()
//        {
//            // TODO Auto-generated method stub
//            return null;
//        }
//
//        public int[] getSimpleRoot( int i )
//        {
//            // TODO Auto-generated method stub
//            return null;
//        }
//
//        public int[] getWeight( int i )
//        {
//            // TODO Auto-generated method stub
//            return null;
//        }
//
//        public AlgebraicField getField()
//        {
//            // TODO Auto-generated method stub
//            return null;
//        }
//    }

    
}
