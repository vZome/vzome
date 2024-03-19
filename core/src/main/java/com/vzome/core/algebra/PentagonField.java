
package com.vzome.core.algebra;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;

import com.vzome.core.commands.Command.Failure;
import com.vzome.core.commands.ZomicVirtualMachine;
import com.vzome.core.construction.ConstructionChanges;
import com.vzome.core.construction.Point;
import com.vzome.core.math.symmetry.IcosahedralSymmetry;
import com.vzome.core.math.symmetry.Symmetry;
import com.vzome.core.zomic.Interpreter;
import com.vzome.core.zomic.ZomicASTCompiler;
import com.vzome.core.zomic.ZomicException;
import com.vzome.core.zomic.parser.ErrorHandler;
import com.vzome.core.zomic.program.ZomicStatement;
import com.vzome.core.zomod.parser.Parser;

@SuppressWarnings( "deprecation" )
public final class PentagonField extends AbstractAlgebraicField
{
    public static final String FIELD_NAME = "golden";

    /**
     * 
     * @return the coefficients of this AlgebraicField class. 
     * This can be used to determine when two fields have compatible coefficients 
     * without having to generate an instance of the class. 
     */
    public static double[] getFieldCoefficients() {
        return new double[] { 1.0d, PHI_VALUE };
    }

    @Override
    public double[] getCoefficients() {
        return getFieldCoefficients();
    }

    public PentagonField()
    {
        super( FIELD_NAME, 2, AlgebraicNumberImpl.FACTORY );
    };

    public static final double PHI_VALUE = ( 1.0 + Math.sqrt( 5.0 ) ) / 2.0;

    // I haven't dug into why this is true, at the moment
    //
    public static final double B1_LENGTH = 2d * PHI_VALUE * PHI_VALUE * PHI_VALUE;

    private static final int ONES_PLACE = 0, PHIS_PLACE = 1;
    
    @Override
    public boolean doubleFrameVectors()
    {
        return true;
    }

    @Override
    double evaluateNumber( BigRational[] factors )
    {
        return factors[ ONES_PLACE ] .evaluate() + PHI_VALUE * factors[ PHIS_PLACE ] .evaluate();
    }

    @Override
    public final BigRational[] multiply( BigRational[] v1, BigRational[] v2 )
    {
        BigRational phis = v1[PHIS_PLACE] .times( v2[ONES_PLACE] ) .plus( v1[ONES_PLACE] .times( v2[PHIS_PLACE] ) ) .plus( v1[PHIS_PLACE] .times( v2[PHIS_PLACE] ) );
        BigRational ones = v1[ONES_PLACE] .times( v2[ONES_PLACE] ) .plus( v1[PHIS_PLACE] .times( v2[PHIS_PLACE] ) );

        return new BigRational[]{ ones, phis };
    }

    @Override
    protected BigRational[] reciprocal( BigRational[] v2 )
    {
        BigRational ones = v2[1] .plus( v2[0] );
        BigRational phis = v2[1] .negate();
        
        BigRational denominator = v2[0].times(v2[0]) .plus( v2[0].times(v2[1]) ) .minus( v2[1].times(v2[1]) );
        if ( ! denominator .isOne() ) {
            BigRational reciprocal = denominator .reciprocal();
            ones = ones .times( reciprocal );
            phis = phis .times( reciprocal );
        }
        return new BigRational[]{ ones, phis };
    }

    /**
     * scalar for an affine pentagon
     * @return 
     */
    @Override
    public AlgebraicNumber getAffineScalar() {
        return getUnitTerm( 1 );
    }

    @Override
    public AlgebraicNumber getGoldenRatio()
    {
        return getUnitTerm(1);
    }

    @Override
    BigRational[] scaleBy( BigRational[] factors, int whichIrrational )
    {
        switch (whichIrrational) {
        case 0:
            return factors;
        case 1:
            return new BigRational[]{ factors[ 1 ], factors[ 0 ] .plus( factors[ 1 ] ) };
        default:
            throw new IllegalArgumentException(whichIrrational + " is not a valid irrational in this field");
        }
    }

    @Override
    public List<Integer> recurrence( List<Integer> input )
    {        
        ArrayList<Integer> output = new ArrayList<>();
        for ( int item : input )
        {
            switch ( item ) {

            case 0:
                output .add( 1 );
                break;

            default:
                output .add( 0 );
                output .add( 1 );
                break;
            }
        }
        return output;
    }

    private static final String[][] IRRATIONAL_LABELS = new String[][] {
        {" ", " "},
        {"\u03C6", "phi"}
    };
    
    @Override
    public String getIrrational( int i, int format )
    {
        return IRRATIONAL_LABELS[i][format];
    }

    @Override
    public AlgebraicNumber parseLegacyNumber( String string )
    {
        int div = 1;
        if ( string .startsWith( "(" ) ) {
            int closeParen = string .indexOf( ')' );
            div = Integer .parseInt( string .substring( closeParen+2 ) );
            string = string .substring( 1, closeParen );
        }

        int phis = 0;
        int phiIndex = string .indexOf( "phi" );
        if ( phiIndex >= 0 ) {
            String part = string .substring( 0, phiIndex );
            if ( part .length() == 0 )
                phis = 1;
            else if ( part .equals( "-" ) )
                phis = -1;
            else
                phis = Integer .parseInt( part );
            string = string .substring( phiIndex+3 );
        }

        int ones;
        if ( string .length() == 0 )
            ones = 0;
        else {
            if ( string .startsWith( "+" ) )
                string = string .substring( 1 );
            ones = Integer .parseInt( string );
        }
        return createAlgebraicNumber( ones, phis, div, 0 );
    }

    private ZomicStatement parseZomodScript( String script ) throws Failure
    {
        Parser parser = new Parser( new IcosahedralSymmetry( new PentagonField() ));
        List<String> errors = new ArrayList<>();
        ZomicStatement program = parser .parse(
            new ByteArrayInputStream( script .getBytes() ), new ErrorHandler.Default( errors ), "" );
        if ( errors.size() > 0 )
            throw new Failure( errors .get(0) );
        return program;
    }

    @Override
    public void interpretScript( String script, String language, Point offset, Symmetry symmetry, ConstructionChanges effects ) throws Exception
    {
        ArrayList<String> errorList = new ArrayList<>();
        ErrorHandler errors = new ErrorHandler.Default( errorList );

        ZomicStatement program = null;
        switch ( language ) {

        case "zomic":
            try {
                ZomicASTCompiler compiler = new ZomicASTCompiler( (IcosahedralSymmetry) symmetry );
                program = compiler .compile( script, errors );
            } catch ( ClassCastException e ) {
                errors .parseError( 0, 0, "Tried to compile Zomic with non-icosahedral symmetry" );
                return;
            }
            break;
            
        case "zomod":
            program = parseZomodScript( script );

        default:
            errors .parseError( 0, 0, language+" is not a supported script language." );
            return;
        }

        if ( errorList.size() > 0 )
            throw new Failure( errorList .get(0) );

        ZomicVirtualMachine builder = new ZomicVirtualMachine( offset, effects, symmetry );
        try {
            program .accept( new Interpreter( builder, symmetry ) );
        } catch ( ZomicException e ) {
            throw new Failure( e );
        }
    }
}
