
//(c) Copyright 2005, Scott Vorthmann.  All rights reserved.

package com.vzome.core.zomic;

import com.vzome.core.math.symmetry.Axis;
import com.vzome.core.math.symmetry.Direction;
import com.vzome.core.math.symmetry.DirectionNaming;
import com.vzome.core.math.symmetry.Permutation;
import com.vzome.core.math.symmetry.Symmetry;


public class BlackDirectionNaming extends DirectionNaming
    {
		protected final Axis[][] mMap;
        
        protected final DirectionNaming mRedNames, mYellowNames;

        BlackDirectionNaming( Direction dir, DirectionNaming reds, DirectionNaming yellows )
		{
            super( dir, dir .getName() );
            mRedNames = reds;
            mYellowNames = yellows;
            mMap = new Axis[ 2 ][ dir .getSymmetry() .getChiralOrder() ];
            
            for ( int sense = Symmetry .PLUS; sense <= Symmetry .MINUS; sense++ )
            for ( int i = 0; i < mMap[ Symmetry .PLUS ] .length; i++ ) {
                Axis axis = dir .getAxis( sense, i );
                String ry = getName( axis );
                if ( getSign( ry ) == Symmetry .MINUS )
                    continue;
                boolean minused = ry .endsWith( "-" );
                int index = getInteger( ry );
                int sign = minused? Symmetry .MINUS : Symmetry .PLUS;
                mMap[ sign ][ index ] = axis;
//                System .out .println( "mMap[ " + (sign==1?"-":"+") + index +  "] = " + super .getName( axis ) + " is " + ry + " is " + axis .normal() );
            }

		}
    	
    	protected int getInteger( String axisName )
    	{
    	    if ( axisName .endsWith( "-" ) || axisName .endsWith( "+" ) )
    	        axisName = axisName .substring( 0, axisName .length() - 1 );
    	    if ( axisName .startsWith( "-" ) || axisName .startsWith( "+" ) )
    	        return Integer .parseInt( axisName .substring( 1 ) );
    	    return Integer .parseInt( axisName );
    	}
		
		public Axis getAxis( String axisName )
		{
		    boolean minused = axisName .endsWith( "-" );
		    int sense = getSign( axisName );
		    int ry = getInteger( axisName );
		    Axis axis = mMap[ minused? Symmetry.MINUS : Symmetry.PLUS ][ ry ];
		    if ( sense == Symmetry .MINUS )
		        axis = getDirection() .getAxis( (axis .getSense()+1)%2, axis .getOrientation() );
		    return axis;
		}
		
		public String getName( Axis axis )
		{
			int orn = axis .getOrientation();
		    Axis redNeighbor = mRedNames .getDirection() .getAxis( axis .getSense(), orn );
		    String redName = mRedNames .getName( redNeighbor );
		    Permutation rot = redNeighbor .getRotationPermutation();
		    if ( axis .getSense() == Symmetry .MINUS )
		        rot = rot .inverse();
		    orn = rot .mapIndex( orn );
		    int redSign = getSign( redName );
		    Axis yellowNeighbor = mYellowNames .getDirection() .getAxis( redSign, orn );
		    String yellowName = mYellowNames .getName( yellowNeighbor ) .substring( 1 ); // remove the sign
		    if ( axis .getSense() == redSign )
		        yellowName += "-";
		    
//		    System .out .println( SIGN[ axis.getSense() ] + axis .getOrientation() + " is black " + redName + yellowName );
		    
		    return redName + yellowName;
		}
    }