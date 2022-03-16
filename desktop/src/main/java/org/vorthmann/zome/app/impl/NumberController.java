
package org.vorthmann.zome.app.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.vorthmann.ui.DefaultController;

import com.vzome.core.algebra.AlgebraicField;
import com.vzome.core.algebra.AlgebraicNumber;

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
        switch ( listName ) {

        case "labels":
            int order = this .field .getOrder();
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
            for( int i = 0; i < td .length; i++ )
                result[ i ] = Integer .toString( td[ i ] );
            return result;

        case "named-values":
            return getNamedValues();

        case "math.operations":
            return MATH_OPS;

		default:
	        return super.getCommandList( listName );
		}
    }

    private static String[] OPTIONAL_NAMED_VALUES = new String[] {
        // increasing order except that phi and other greek letters go before any sqrtN
        "phi",      // 5,2 
        "rho",      // 7,2
        "sigma",    // 7,3
        // alpha, beta and gamma are ambiguous when nSides is a mutiple of both 9 and 13
        // but since 9*13=117, and we seldom use 117N-gons, I'll live with it. 
        "alpha",    // 13,2 and 9,2
        "beta",     // 13,3 and 9,3
        "gamma",    // 13,4 and 9,4
        "delta",    // 13,5
        "epsilon",  // 13,6
        "theta",    // 11,2
        "kappa",    // 11,3
        "lambda",   // 11,4
        "mu",       // 11,5
        "seperator",
        // square roots
        "\u221A2",
        "\u221A3",
        "\u221A5",
        "\u221A6",
        "\u221A7",
        "\u221A8",
        "\u221A10",
   };

    private String[] getNamedValues() {
        boolean seperateNext = false;
        List<String> list = new ArrayList<>();
        // These are always valid
        list.add("zero");  
        list.add("one");
        // These are only added to the list if the field supports them
        for(String test : OPTIONAL_NAMED_VALUES) {
            if(test.equals("seperator")) {
                seperateNext = true;
            } else {
                if(field.getNumberByName(test) != null) {
                    if(seperateNext ) {
                        seperateNext = false;
                        list.add("seperator");
                    }
                    list.add(test);
                }
            }
        }
        return list.toArray(new String[list.size()]);
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

        case "named-value":
            setValueByName( String.valueOf(value) ); // null safe
            return;

        case "math.operation":
            if(doMath( String.valueOf(value) ) ) { // null safe
                return;
            }
        }
		super .setModelProperty( property, value );
    }

    private void setValueByName(String name) {
        AlgebraicNumber n = field.getNumberByName(name);
        if(n != null) {
            setValue(n);
        }
    }

    private static final String[] MATH_OPS = new String[] {"Negate", "Reciprocal", "Square"};
    
    private boolean doMath(String operation) {
        switch(operation) {
        case "Negate":
            setValue( getValue().negate() );
            return true;
    
        case "Reciprocal":
            if(! getValue().isZero()) {
                setValue( getValue().reciprocal() );
            }
            return true;

        case "Square":
            setValue( getValue().times(getValue()) );
            return true;
        }
        return false; // unhandled command
    }

    @Override
    public String getProperty(String property) {
        switch(property) {
        case "value":
            return value.toString();
            
        case "evaluate":
            return String.valueOf(value.evaluate());
            
        default:
            return super.getProperty(property);
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
