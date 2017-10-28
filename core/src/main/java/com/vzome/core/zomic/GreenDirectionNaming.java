
//(c) Copyright 2005, Scott Vorthmann.  All rights reserved.

package com.vzome.core.zomic;

import com.vzome.core.math.symmetry.Axis;
import com.vzome.core.math.symmetry.Direction;
import com.vzome.core.math.symmetry.DirectionNaming;
import com.vzome.core.math.symmetry.Symmetry;


public class GreenDirectionNaming extends DirectionNaming
    {
		protected final Axis[] mMap;
        
        protected final DirectionNaming mRedNames, mYellowNames;

        GreenDirectionNaming( Direction dir, DirectionNaming reds, DirectionNaming yellows )
		{
            super( dir, dir .getName() );
            mRedNames = reds;
            mYellowNames = yellows;
            mMap = new Axis[ dir .getSymmetry() .getChiralOrder() ];
            for ( int i = 0; i < mMap .length; i++ ) {
                Axis axis = dir .getAxis( Symmetry.PLUS, i );
                String ry = getName( axis );
                int sense = getSign( ry );
                int index = getInteger( ry );
                if ( sense == Symmetry .MINUS )
                    axis = dir .getAxis( sense, i );
                mMap[ index ] = axis;

//                System .out .println( dir .getName() + " " + ry + " is   " + super .getName( axis ) );
            }
		}
		
        @Override
		public Axis getAxis( String axisName )
		{
			int sense = getSign( axisName );
			int ry = getInteger( axisName );
			Axis axis = mMap[ ry ];
			if ( sense == Symmetry .MINUS )
			    axis = getDirection() .getAxis( sense, axis .getOrientation() );
			return axis;
		}
		
        @Override
		public String getName( Axis axis )
		{
		    Axis redNeighbor = mRedNames .getDirection() .getAxis( axis .getSense(), axis .getOrientation() );
		    String redName = mRedNames .getName( redNeighbor );
		    Axis yellowNeighbor = mYellowNames .getDirection() .getAxis( axis .getSense(), axis .getOrientation() );
		    String yellowName = mYellowNames .getName( yellowNeighbor ) .substring( 1 ); // remove the sign
		    return redName + yellowName;
		}
    }
