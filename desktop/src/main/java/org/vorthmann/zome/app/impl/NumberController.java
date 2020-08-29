
//(c) Copyright 2007, Scott Vorthmann.  All rights reserved.

package org.vorthmann.zome.app.impl;

import java.math.BigInteger;
import java.util.StringTokenizer;

import org.vorthmann.ui.DefaultController;

import com.vzome.core.algebra.AlgebraicField;
import com.vzome.core.algebra.AlgebraicNumber;
import com.vzome.core.algebra.BigRational;

public class NumberController extends DefaultController
{
    private AlgebraicNumber value;
    
    private final AlgebraicField field;
        
    public NumberController( AlgebraicField field )
    {
        this .field = field;
        this .value = field .one();
    }

    @Override
    public String[] getCommandList( String listName )
    {
        int order = this .field .getOrder();

        switch ( listName ) {

        case "labels":
            // first label should always be "1", last should always be "/"
            String[] result = new String[ order + 1 ];
            result[ 0 ] = "1";
            result[ order ] = "/";
            for( int i = 1; i < order; i++ )
                result[ i ] = this .field .getIrrational( i );
            return result;

        case "values":
            int[] td = value .toTrailingDivisor();
            result = new String[ td .length ];
            for( int i = 0; i < order; i++ )
                result[ i ] = Integer .toString( td[ i ] );
            return result;

		default:
	        return super.getCommandList( listName );
		}
    }

    @Override
    public void setModelProperty( String property, Object value )
    {
        switch ( property ) {

        case "values":
            StringTokenizer values = new StringTokenizer( (String) value );
            int[] inputs = new int[ field .getOrder() ]; // divisor will be the last int
            int divisor = 1;
            for ( int i = 0; values .hasMoreTokens(); i++ )
                if ( i < inputs.length )
                    inputs[ i ] = Integer .parseInt( values .nextToken() );
                else
                    divisor = Integer .parseInt( values .nextToken() );
            this .value = field .createAlgebraicNumber( inputs ) .dividedBy( field .createRational( divisor ) );
            return;

		default:
            super .setModelProperty( property, value );
		}
    }

    public AlgebraicNumber getValue()
    {
        return this .value;
    }

    public void setValue( AlgebraicNumber value )
    {
        this .value = value;
    }
}
