package com.vzome.core.algebra;

import java.util.ArrayList;
import java.util.List;

public class AlgebraicSeries
{
    public AlgebraicNumber[] series;
    
    public AlgebraicSeries( AbstractAlgebraicField field, int power )
    {
        List<Integer> sequence = new ArrayList<>();
        sequence .add( 0 ); // always seed the sequence with the unit, the 0th irrational
        
        AlgebraicNumber divisor = field .getUnitTerm( 1 );
        divisor = divisor .times( divisor );
        
        // We'll overshoot by two powers, since we want to divide the values
        //   by 1/a^2 for the irrational a
        for ( int i = 0; i < power+2; i++ )
        {
            sequence = field .recurrence( sequence );
        }
        this .series = new AlgebraicNumber[ sequence .size() + 1 ]; // leave room for 0
        this .series[ 0 ] = field .zero();
        int prevIndex = 0;
        for ( Integer integer : sequence )
        {
            AlgebraicNumber prev = this .series[ prevIndex++ ];
            AlgebraicNumber step = field .getUnitTerm( integer ) .dividedBy( divisor );
            this .series[ prevIndex ] = prev .plus( step );
        }
    }

    public AlgebraicNumber nearestAlgebraicNumber( double target )
    {
        boolean negative = target < 0d;
        if ( negative )
            target = -target;
        AlgebraicNumber positive = checkRange( 0, series.length -1, target );
        if ( negative )
            return positive .negate();
        else
            return positive;
    }

    private AlgebraicNumber checkRange( int minIndex, int maxIndex, double target )
    {
        if ( minIndex >= maxIndex )
            // just to be safe, though this should never happen
            return series[ maxIndex ];
        else {
            double lowDiff = target - series[ minIndex ] .evaluate();
            double highDiff = series[ maxIndex ] .evaluate() - target;
            if ( maxIndex == minIndex + 1 ) {
                return ( highDiff < lowDiff )? series[ maxIndex ] : series[ minIndex ];
            } else {
                int midIndex = (int) Math.floor( ( maxIndex + minIndex ) / 2 );
                return ( highDiff < lowDiff )? checkRange( midIndex, maxIndex, target ) : checkRange( minIndex, midIndex, target );
            }
        }
    }
    
    public String toString()
    {
        String result = "";
        for ( AlgebraicNumber algebraicNumber : series ) {
            result += algebraicNumber .toString() + ", ";
        }
        return result;
    }
    
    public static void main( String[] args )
    {
        PentagonField field = new PentagonField();
        AlgebraicSeries series = field .generateSeries( 30d );
        System.out.println( series );
        AlgebraicNumber best = series .nearestAlgebraicNumber( 13.14159d );
        System.out.println( best );
        System.out.println( best .evaluate() );
    }
}
