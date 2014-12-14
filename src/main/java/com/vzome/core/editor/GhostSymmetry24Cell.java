
//(c) Copyright 2005, Scott Vorthmann.  All rights reserved.

package com.vzome.core.editor;

import org.w3c.dom.Element;

import com.vzome.core.algebra.AlgebraicField;
import com.vzome.core.algebra.RationalMatrices;
import com.vzome.core.commands.XmlSaveFormat;
import com.vzome.core.construction.FreePoint;
import com.vzome.core.construction.ModelRoot;
import com.vzome.core.construction.Point;
import com.vzome.core.construction.Segment;
import com.vzome.core.math.Projection;
import com.vzome.core.math.QuaternionProjection;
import com.vzome.core.math.symmetry.Direction;
import com.vzome.core.math.symmetry.DodecagonalSymmetry;
import com.vzome.core.math.symmetry.Symmetry;
import com.vzome.core.model.RealizedModel;

public class GhostSymmetry24Cell extends ChangeConstructions
{
    private final ModelRoot root;
    private final AlgebraicField field;
    private Projection proj;
    private Segment symmAxis;
    private DodecagonalSymmetry symm;

    public GhostSymmetry24Cell( Selection selection, RealizedModel realized, ModelRoot root, Segment symmAxis, boolean groupInSelection )
    {
        super( selection, realized, groupInSelection );

        this.root = root;
        this.field = root .getField();
        this.symm = (DodecagonalSymmetry) field .getSymmetry( "dodecagonal" );
        this.symmAxis = symmAxis;
    }

    protected String getXmlElementName()
    {
        return "GhostSymmetry24Cell";
    }

    public void getXmlAttributes( Element result )
    {
        if ( symmAxis != null )
            XmlSaveFormat .serializeSegment( result, "start", "end", symmAxis );
    }

    public void setXmlAttributes( Element xml, XmlSaveFormat format )
    {
        symmAxis = format .parseSegment( xml, "start", "end" );
    }
    
    
    public void perform()
    {
        if ( symmAxis == null )
            this.proj = new Projection.Default( field );
        else
            this.proj = new QuaternionProjection( field, null, RationalMatrices .scaleVector( field, symmAxis .getOffset(),
                    field .createPower( -5 ) ) );

        Direction blue = symm .getDirection( "blue" );
        Direction green = symm .getDirection( "green" );
        
        /*
         * From David Richter:
         * 
Let w=(sqrt(3)+i)/2, (a primitive twelfth root of unity).  For each k=0,1,...,11, let
A(k)=(w^k,w^(5k)+w^(5k+1))
and
B(k)=(w^k+w^(k+1),w^(5k+3)).
Then {A(k),B(k)} are the vertices of a 24-cell in C^2=R^4.  Project to R^3 by ignoring any of the 
four real coordinates to obtain a model of the 24-cell with 12-fold ghost symmetry.

         *
         * SV note:
         * 
         * Note that I've added "+2" to all the indices above, and I've also constructed the 4D
         * vectors as (A2,A1) and (B2, B1).  This combination gives me the nicest fit into the
         * octahedral system... the axes of ghost symmetry are adjacent 3-fold and 4-fold
         * symmetry axes.  Also, the axis orthogonal to both is a 2-fold axis.
         * 
         * In essence, the 3D projection can be rotated by pi/6 around the Z axis,
         * and this rotation gives me "green" struts (45-degree angle) in the octahedral rendering,
         * rather than "red" ones (15-degree angle).
         */
        for ( int k = 0; k < 12; k++ )
        {
            int[] A1 = blue .getAxis( Symmetry.PLUS, (k+2) % 12 ) .normal();
            int[] A2 = green .getAxis( Symmetry.PLUS, (5*k+2) % 12 ) .normal();
            int[] B1 = green .getAxis( Symmetry.PLUS, (k+2) % 12 ) .normal();
            int[] B2 = blue .getAxis( Symmetry.PLUS, (5*k+5) % 12 ) .normal();
            
            int[] projected = new int[ 16 ];
            field .setVectorComponent( projected, 0, field .getVectorComponent( A2, 0 ) );
            field .setVectorComponent( projected, 1, field .getVectorComponent( A2, 1 ) );
            field .setVectorComponent( projected, 2, field .getVectorComponent( A1, 0 ) );
            field .setVectorComponent( projected, 3, field .getVectorComponent( A1, 1 ) );
            
            if ( proj != null )
                projected = proj .projectImage( projected, true );
            
            Point p = new FreePoint( RationalMatrices .scaleVector( field, projected, field .createPower( 5 ) ), root );
            p .setIndex( k );
            addConstruction( p );
            manifestConstruction( p );

            projected = new int[ 16 ];
            field .setVectorComponent( projected, 0, field .getVectorComponent( B2, 0 ) );
            field .setVectorComponent( projected, 1, field .getVectorComponent( B2, 1 ) );
            field .setVectorComponent( projected, 2, field .getVectorComponent( B1, 0 ) );
            field .setVectorComponent( projected, 3, field .getVectorComponent( B1, 1 ) );
            
            if ( proj != null )
                projected = proj .projectImage( projected, true );
            
            p = new FreePoint( RationalMatrices .scaleVector( field, projected, field .createPower( 5 ) ), root );
            p .setIndex( 12 + k );
            addConstruction( p );
            manifestConstruction( p );
        }
        redo();
    }
}

