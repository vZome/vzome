package com.vzome.desktop.controller;

import com.vzome.core.algebra.AlgebraicField;
import com.vzome.core.algebra.AlgebraicNumber;
import com.vzome.core.math.symmetry.Direction;
import com.vzome.desktop.api.Controller;

public class LengthController extends DefaultController
{
    /**
     * A model for a scale slider.  Value range centers on scale 0.
     * 
     * Actual scale
     * 
     * @author Scott Vorthmann
     *
     */
    class ScaleController extends DefaultController
    {
        private static final int MAX_SCALE = 6, MIN_SCALE = -6;

        @Override
        public void doAction( String action ) throws Exception
        {
            if ( "scaleUp" .equals( action ) )
                setScale( this .scale + 1 );
            else if ( "scaleDown" .equals( action ) )
                setScale( this .scale - 1 );
//            else if ( "factor" .equals( action ) )
//                ; // TODO factor as many of these out of the lengthModel as you can
//            else if ( "zero" .equals( action ) )
//                ; // TODO multiply this value into the lengthModel, and zero this value
            else
                super.doAction( action );
        }

        private int scale = 0;

        @Override
        public void setModelProperty( String property, Object value )
        {
            if ( "scale" .equals( property ) )
            {
                setScale( Integer .parseInt( (String) value ) );
                return;
            }
            else
                super .setModelProperty( property, value );
        }
        
        @Override
        public String getProperty( String string )
        {
            if ( "scale" .equals( string ) )
                return Integer .toString( scale );

            if ( "scaleHtml" .equals( string ) )
            {
                if ( scale == 0 )
                    return "\u2070";
                int absScale = Math .abs( scale );
                String result = ( absScale == scale )? "" : "\u207B"; // prepend sign exponent
                switch ( absScale ) {
                case 1:
                    result += "\u00B9";
                    break;

                case 2:
                    result += "\u00B2";
                    break;

                case 3:
                    result += "\u00B3";
                    break;

                case 4:
                    result += "\u2074";
                    break;

                case 5:
                    result += "\u2075";
                    break;

                case 6:
                    result += "\u2076";
                    break;

                case 7:
                    result += "\u2077";
                    break;

                case 8:
                    result += "\u2078";
                    break;

                case 9:
                    result += "\u2079";
                    break;

                default:
                    result += "\u207F";
                    break;
                }
                return result;
            }

            return super.getProperty( string );
        }

        public void setScale( int amt )
        {
            int oldScale = scale;
            
            scale = amt;
            
            // keep the scale between MIN and MAX, inclusive
            if ( scale > MAX_SCALE )
                scale = MAX_SCALE;
            else if ( scale < MIN_SCALE )
                scale = MIN_SCALE;
            
            if ( oldScale != scale )
                // don't want to generate change events when there is no change
                LengthController .this .fireLengthChange();
        }
        
        int getScale()
        {
            return scale;
        }
    }

    /**
     * This is a permanent adjustment of the scale slider.  When the scale reads 0 for the user,
     * the actual scale used internally will be SCALE_OFFSET.
     */
    public static final int SCALE_OFFSET = Direction .USER_SCALE;
    protected ScaleController[] currentScales;
    protected NumberController unitController;
    /**
     * This is the internal factor applied, determined by the orbit, and fixed.
     */
    protected final AlgebraicNumber fixedFactor;
    /**
     * This is the user's basis for scale... when the slider is centered on "unit", this is the length value.
     */
    protected AlgebraicNumber unitFactor;
    protected int multiplier;
    protected final AlgebraicNumber standardUnitFactor;
    private boolean half = false;
    protected final AlgebraicField field;

    public LengthController( AlgebraicField field )
    {
        this( field .one(), field .one(), field );
    }

    public LengthController( AlgebraicNumber fixedFactor, AlgebraicNumber standardUnitFactor, AlgebraicField field )
    {
        super();
        this.fixedFactor = fixedFactor;
        this.standardUnitFactor = standardUnitFactor;
        this.field = field;
        this .multiplier = 0;
        this .unitFactor = standardUnitFactor;
        this .currentScales = new ScaleController[ field .getNumMultipliers() ];
        for ( int i = 0; i < currentScales.length; i++ ) {
            this .currentScales[ i ] = new ScaleController();
            this .addSubController( "scale." + i, this .currentScales[ i ] );
        }
        this .unitController = new NumberController( field );
        this .addSubController( "unit", unitController );
    }

    @Override
    public Controller getSubController(String name)
    {
    	switch ( name ) {
    
    	case "unit":
    		return this .unitController;
    
    	case "scale":
    		return this .currentScales[ this .multiplier ];
    
    	default:
    		return super.getSubController( name );
    	}
    }

    public void fireLengthChange() {
        firePropertyChange( "length", true, false );
    }

    private void resetScales() {
        for (int i = 0; i < this .currentScales.length; i++) {
            this .currentScales[ i ] .setScale( 0 );
        }
    }

    @Override
    public void doAction(String action) throws Exception {
            switch ( action ) {
    
            case "setCustomUnit":
                // push the value to the NumberController
                this .unitController .setValue( this .unitFactor );
                return;
    
            case "getCustomUnit":
                // get the value from the NumberController
                this .unitFactor = this .unitController .getValue();
                // now reset everything according to that unitFactor
                resetScales();
                this .multiplier = 0;
                fireLengthChange();
                return;
    
            case "toggleHalf":
            {
                this .half = ! this .half;
                fireLengthChange();
                return;
            }
    
            case "reset":
            case "short":
            {
                this .unitFactor = standardUnitFactor;
                resetScales();
                this .multiplier = 0;
                fireLengthChange();
                return;
            }
    
            case "supershort":
            {
                this .unitFactor = standardUnitFactor;
                resetScales();
                currentScales[ 0 ] .setScale( -1 );
                this .multiplier = 0;
                fireLengthChange();
                return;
            }
    
            case "medium":
            {
                this .unitFactor = standardUnitFactor;
                resetScales();
                currentScales[ 0 ] .setScale( 1 );
                this .multiplier = 0;
                fireLengthChange();
                return;
            }
    
            case "long":
            {
                this .unitFactor = standardUnitFactor;
                resetScales();
                currentScales[ 0 ] .setScale( 2 );
                this .multiplier = 0;
                fireLengthChange();
                return;
            }
    
            case "scaleUp":
            case "scaleDown":
                currentScales[ this .multiplier ] .doAction( action );
                return;
    
            case "newZeroScale":
            {
                this .unitFactor = applyScales( this .unitFactor );
                resetScales();
                this .multiplier = 0;
                fireLengthChange();
                return;
            }
                
    		default:
    		    if ( action .startsWith( "setMultiplier." ) ) {
    		        action = action .substring( "setMultiplier." .length() );
    		        int i = Integer.parseInt( action );
    		        this .multiplier = i;
    	            fireLengthChange();
    		    }
    		    else
    		        super.doAction( action );
    		}
            
    //        else if ( action .startsWith( "adjustScale." ) )
    //        {
    //            int amt = Integer .parseInt( action .substring( "adjustScale." .length() ) );
    //            this .scale -= amt;
    //        }
        }

    @Override
    public void setModelProperty(String property, Object value) {
        switch ( property ) {
    
        case "half":
        {
            boolean oldHalf = this .half;
            this .half = Boolean .parseBoolean( (String) value );
            if ( this .half != oldHalf )
                fireLengthChange();
            break;
        }
    
        case "scale":
        {
            currentScales[ this .multiplier ] .setModelProperty( property, value );
            return;
        }
    
        default:
            super .setModelProperty( property, value );
        }
    }

    @Override
    public String getProperty(String name) {
        switch ( name ) {
    
        case "multiplier":
            return Integer .toString( this .multiplier );
    
        case "half":
            return Boolean .toString( this .half );
    
        case "scale":
            return currentScales[ this .multiplier ] .getProperty( name );
    
        case "unitText":
            return readable( unitFactor );
    
        case "unitIsCustom":
            return Boolean .toString( ! unitFactor .equals( standardUnitFactor ) );
    
        case "lengthText":
        {
            AlgebraicNumber result = this .unitFactor;
            result = applyScales( result );
            return readable( result );
        }
        
        case "lengthMathML":
        {
            AlgebraicNumber result = this .unitFactor;
            result = applyScales( result );
            return result .getMathML();
        }

        case "scaleFactorHtml":
        {
            String html = "";
            for ( int i = 0; i < this .currentScales.length; i++ ) {
                html += field .getIrrational( i+1 ) + currentScales[ i ] .getProperty( "scaleHtml" ) + "  \u2715  ";
            }
            return html;
        }
    
        default:
            return super.getProperty( name );
        }
    }

    private AlgebraicNumber applyScales( AlgebraicNumber value )
    {
        for ( int i = 0; i < this .currentScales.length; i++ ) {
            int scale = currentScales[ i ] .getScale();
            value = value .times( field .createPower( scale, i+1 ) );
        }
        return value;
    }

    private String readable( AlgebraicNumber unitFactor2 )
    {
        StringBuffer buf = new StringBuffer();
        unitFactor2 .getNumberExpression( buf, AlgebraicField.DEFAULT_FORMAT );
        return buf .toString();
    }

    /**
     * Get the actual length value to use when rendering.  This value will multiply the zone's normal vector,
     * whatever its length is (not necessarily a unit vector).
     */
    public AlgebraicNumber getValue()
    {
        // TODO push part of this into AlgebraicField somehow?
        AlgebraicNumber result = this .unitFactor .times( this .fixedFactor );
        if ( half )
            result = result .times( field .createRational( 1, 2 ) );
        
        result = result .times( field .createPower( SCALE_OFFSET ) );
        result = applyScales( result );
        return result;
    }

    public int getScale()
    {
        return this .currentScales[ this .multiplier ] .getScale();
    }

    public void setScale( int amt )
    {
        this .currentScales[ this .multiplier ] .setScale( amt );
    }

    /**
     * This is basically an inverse of getValue(), but with scale fixed at zero,
     * thus forcing unitFactor to float.
     * 
     * @param length
     */
    public void setActualLength( AlgebraicNumber length )
    {
        half = false;
        resetScales();
    
        length = length .times( this .field .createPower( -SCALE_OFFSET ) );
        unitFactor = length .dividedBy( this .fixedFactor );
        fireLengthChange();
    }

}