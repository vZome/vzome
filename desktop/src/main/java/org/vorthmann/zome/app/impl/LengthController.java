
//(c) Copyright 2007, Scott Vorthmann.  All rights reserved.

package org.vorthmann.zome.app.impl;

import java.awt.event.MouseWheelEvent;

import org.vorthmann.j3d.MouseTool;
import org.vorthmann.j3d.MouseToolDefault;
import org.vorthmann.ui.Controller;
import org.vorthmann.ui.DefaultController;
import org.w3c.dom.Element;

import com.vzome.core.algebra.AlgebraicField;
import com.vzome.core.algebra.AlgebraicNumber;
import com.vzome.core.math.DomUtils;
import com.vzome.core.math.symmetry.Direction;

/**
 * Because of MOUSE_WHEEL_GAIN issues, this is more a model of the length panel than an actual length scalar value.
 *
 */
public class LengthController extends DefaultController
{
	/**
     * This is a permanent adjustment of the scale slider.  When the scale reads 0 for the user,
     * the actual scale used internally will be SCALE_OFFSET.
     */
    public static final int SCALE_OFFSET = Direction .USER_SCALE;

    /**
     * A model for a scale slider.  Value range centers on scale 0.
     * 
     * Actual scale
     * 
     * @author Scott Vorthmann
     *
     */
    private class ScaleController extends DefaultController
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
    
    private final MouseTool tool;

    private ScaleController[] currentScales;
    
    private NumberController unitController;
    
    private static final int MOUSE_WHEEL_GAIN = 4;
    

    /**
     * This is the internal factor applied, determined by the orbit, and fixed.
     */
    private final AlgebraicNumber fixedFactor;
    
    /**
     * This is the user's basis for scale... when the slider is centered on "unit", this is the length value.
     */
    private AlgebraicNumber unitFactor;
    
    private int multiplier;
    
    private final AlgebraicNumber standardUnitFactor;
    
    private boolean half = false;
        
    private final AlgebraicField field;
        
    public LengthController( Direction orbit )
    {
        this( orbit .getSymmetry() .getField(), orbit .getUnitLength() );
    }

    public LengthController( AlgebraicField field )
    {
        this( field, field .one() );
    }

    public LengthController( AlgebraicField field, AlgebraicNumber factor )
    {
        this .field = field;
        this .multiplier = 0;
        this .standardUnitFactor = field .createPower( 0 );
        this .unitFactor = standardUnitFactor;
        this .fixedFactor = factor;
        this .currentScales = new ScaleController[ field .getNumMultipliers() ];
        for ( int i = 0; i < currentScales.length; i++ ) {
            this .currentScales[ i ] = new ScaleController();
            this .addSubController( "scale." + i, this .currentScales[ i ] );
        }
        this .unitController = new NumberController( field );
        this .addSubController( "unit", unitController );

        this .tool = new MouseToolDefault()
        {
            int wheelClicks = 0;
           
            /**
             * Simply dividing the roll amt MOUSE_WHEEL_GAIN would be insufficient, because then
             *  wheeling slowing and precisely might never get above 0.  I though perhaps a minimum scale change of +/-1
             *  on any roll might accomplish the right thing, but then it is not possible to wheel
             *  slowly enough.
             * By keeping an internal state (wheelClicks), and applying MOUSE_WHEEL_GAIN,
             * we can generate courser grained scale changes without those unnatural effects.
             */
            @Override
            public void mouseWheelMoved( MouseWheelEvent e )
            {
                int amt = e .getWheelRotation();
                int oldScaled = wheelClicks / MOUSE_WHEEL_GAIN;
                wheelClicks = wheelClicks + amt;
                int newScaled = wheelClicks / MOUSE_WHEEL_GAIN;
                
                if ( oldScaled != newScaled )
                    // don't want to generate change events when there is no change
                    setScale( getScale() - newScaled + oldScaled  ); // reverse the sense of the wheel,
                    // since mouseWheel clicks are set up for scrollbars
            }
        };
    }

    @Override
	public Controller getSubController( String name )
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

    public void fireLengthChange()
    {
        firePropertyChange( "length", true, false );
    }

    public void getXml( Element lengthElem )
    {
        // backward-compatible, for now
        //  TODO THIS IS BROKEN!  Does not deal with more than one multiplier!
        DomUtils .addAttribute( lengthElem, "scale", Integer.toString( currentScales[ this .multiplier ] .getScale() + SCALE_OFFSET ) );
        DomUtils .addAttribute( lengthElem, "taus", "0" );
        DomUtils .addAttribute( lengthElem, "ones", "1" );
        DomUtils .addAttribute( lengthElem, "divisor", half? "2" : "1" );
    }

    public void setXml( Element length )
    {
        String attrValue = length .getAttribute( "scale" );
        // handling nulls since I used a non-published version to migrate internal models,
        //   and it did not serialize any attributes at all
        int scale = ( attrValue != null && ! attrValue .isEmpty() )? Integer .parseInt( attrValue ) : 4;        
        //  TODO THIS IS BROKEN!  Does not deal with more than one multiplier!
        this .currentScales[ this .multiplier ] .setScale( scale - SCALE_OFFSET );  // vZome files record the actual scale, not user scale
        
        // TODO handle other two attribute values!
        
        attrValue = length .getAttribute( "divisor" );
        half = ( attrValue == null || attrValue .isEmpty() )? false : "2" .equals( attrValue );
    }
    
    private void resetScales()
    {
        for (int i = 0; i < this .currentScales.length; i++) {
            this .currentScales[ i ] .setScale( 0 );
        }
    }

    @Override
    public void doAction( String action ) throws Exception
    {
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
    public void setModelProperty( String property, Object value )
    {
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
    public String getProperty( String name )
    {
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

        case "scaleFactorHtml":
        {
            String html = "";
            for ( int i = 0; i < this .currentScales.length; i++ ) {
                html += field .getIrrational( i+1 ) + "<font size=+1>" + currentScales[ i ] .getProperty( "scaleHtml" ) + "</font>  \u2715  ";
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
    
    private int getScale()
    {
        return this .currentScales[ this .multiplier ] .getScale();
    }
    
    private void setScale( int amt )
    {
        this .currentScales[ this .multiplier ] .setScale( amt );
    }

    @Override
    public MouseTool getMouseTool()
    {
        return this .tool;
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
