package com.vzome.core.editor.api;

import com.vzome.core.algebra.AlgebraicField;
import com.vzome.core.algebra.AlgebraicMatrix;
import com.vzome.core.algebra.AlgebraicVector;
import com.vzome.core.construction.Color;
import com.vzome.core.math.RealVector;
import com.vzome.core.math.symmetry.Axis;
import com.vzome.core.math.symmetry.Direction;
import com.vzome.core.math.symmetry.OrbitSet;
import com.vzome.core.math.symmetry.Symmetry;

public interface OrbitSource
{
    Symmetry getSymmetry();
	    	
    Axis getAxis( AlgebraicVector vector );
    
    Color getColor( Direction orbit );  // used only in rendering

    Color getVectorColor( AlgebraicVector vector );

	OrbitSet getOrbits();
	
	Shapes getShapes();

    String getName();  // used in serializing edits, and in rendering
    
    default Axis getZone( String orbit, int orientation )
    {
        return getSymmetry() .getDirection( orbit ) .getAxis( Symmetry.PLUS, orientation );
    }
    
    default float[] getEmbedding()
    {
        Symmetry symmetry = this .getSymmetry();
        AlgebraicField field = symmetry .getField();
        float[] embedding = new float[ 16 ];
        for ( int i = 0; i < 3; i++ )
        {
            AlgebraicVector columnSelect = field .basisVector( 3, i );
            RealVector colRV = symmetry .embedInR3( columnSelect );
            embedding[ i*4+0 ] = colRV.x;
            embedding[ i*4+1 ] = colRV.y;
            embedding[ i*4+2 ] = colRV.z;
            embedding[ i*4+3 ] = 0f;
        }
        embedding[ 12 ] = 0f;
        embedding[ 13 ] = 0f;
        embedding[ 14 ] = 0f;
        embedding[ 15 ] = 1f;
        return embedding;
    }

    default float[][] getOrientations( boolean rowMajor )
    {
        Symmetry symmetry = this .getSymmetry();
        AlgebraicField field = symmetry .getField();
        int order = symmetry .getChiralOrder();
        float[][] orientations = new float[order][];        
        for ( int orientation = 0; orientation < order; orientation++ )
        {
            if ( rowMajor ) {
                orientations[ orientation ] = symmetry .getMatrix( orientation ) .getRowMajorRealElements();
                continue;
            }
            float[] asFloats = new float[ 16 ];
            AlgebraicMatrix transform = symmetry .getMatrix( orientation );
            for ( int i = 0; i < 3; i++ )
            {
                AlgebraicVector columnSelect = field .basisVector( 3, i );
                AlgebraicVector columnI = transform .timesColumn( columnSelect );
                RealVector colRV = columnI .toRealVector();
                asFloats[ i*4+0 ] = colRV.x;
                asFloats[ i*4+1 ] = colRV.y;
                asFloats[ i*4+2 ] = colRV.z;
                asFloats[ i*4+3 ] = 0f;
            }
            asFloats[ 12 ] = 0f;
            asFloats[ 13 ] = 0f;
            asFloats[ 14 ] = 0f;
            asFloats[ 15 ] = 1f;
            orientations[ orientation ] = asFloats;
        }
        return orientations;
    }

    default float[][] getOrientations()
    {
        return getOrientations( false );
    }
}