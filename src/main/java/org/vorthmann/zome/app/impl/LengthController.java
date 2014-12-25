
//(c) Copyright 2007, Scott Vorthmann.  All rights reserved.

package org.vorthmann.zome.app.impl;

import java.awt.event.ActionEvent;
import java.awt.event.MouseWheelEvent;
import java.util.StringTokenizer;

import org.vorthmann.j3d.MouseTool;
import org.vorthmann.j3d.MouseToolDefault;
import org.vorthmann.ui.DefaultController;
import org.w3c.dom.Element;

import com.vzome.core.algebra.AlgebraicField;
import com.vzome.core.algebra.RationalVectors;
import com.vzome.core.math.DomUtils;
import com.vzome.core.math.symmetry.Direction;

/**
 * Because of MOUSE_WHEEL_GAIN issues, this is more a model of the length panel than an actual length scalar value.
 * It should probably be renamed LengthController, in any case.
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

        public void doAction( String action, ActionEvent e ) throws Exception
        {
            if ( "scaleUp" .equals( action ) )
                setScale( this .scale + 1 );
            else if ( "scaleDown" .equals( action ) )
                setScale( this .scale - 1 );
            else if ( "factor" .equals( action ) )
                ; // TODO factor as many of these out of the lengthModel as you can
            else if ( "zero" .equals( action ) )
                ; // TODO multiply this value into the lengthModel, and zero this value
            super.doAction( action, e );
        }

        private int scale = 0;
        
        private final MouseTool tool;

        public ScaleController()
        {
            // TODO Auto-generated constructor stub
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
                public void mouseWheelMoved( MouseWheelEvent e )
                {
                    int amt = e .getWheelRotation();
                    int oldScaled = wheelClicks / MOUSE_WHEEL_GAIN;
                    wheelClicks = wheelClicks + amt;
                    int newScaled = wheelClicks / MOUSE_WHEEL_GAIN;
                    
                    if ( oldScaled != newScaled )
                        // don't want to generate change events when there is no change
                        setScale( scale - newScaled + oldScaled  ); // reverse the sense of the wheel,
                        // since mouseWheel clicks are set up for scrollbars
                }
            };
        }

        public void setProperty( String property, Object value )
        {
            if ( "scale" .equals( property ) )
            {
                setScale( Integer .parseInt( (String) value ) );
                return;
            }
            else
                super.setProperty( property, value );
        }
        
        public String getProperty( String string )
        {
            if ( "scale" .equals( string ) )
                return Integer .toString( scale );

            if ( "scaleHtml" .equals( string ) )
            {
                if ( scale == 0 )
                    return " \u2070";  // space to pad, attempting to prevent width recalc. when sign appears
                int absScale = Math .abs( scale );
                String result = ( absScale == scale )? " " : "\u207B"; // prepend sign exponent or space to fill
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

        public MouseTool getMouseTool()
        {
            return tool;
        }
    }

    private ScaleController currentScale;
    
    private static final int MOUSE_WHEEL_GAIN = 4;
    

    /**
     * This is the internal factor applied, determined by the orbit, and fixed.
     */
    private final int[] fixedFactor;
    
    /**
     * This is the user's basis for scale... when the slider is centered on "unit", this is the length value.
     */
    private int[] unitFactor;
    
    private final int[] standardUnitFactor;
    
    private boolean half = false;
        
    private final AlgebraicField field;
        
    public LengthController( Direction orbit )
    {
        this .field = orbit .getSymmetry() .getField();
        this .standardUnitFactor = field .createPower( 0 );
        this .unitFactor = standardUnitFactor;
        this .fixedFactor = orbit .getUnitLength();
        this .currentScale = new ScaleController();
        this .currentScale .setNextController( this );
    }

    public void fireLengthChange()
    {
        properties() .firePropertyChange( "length", true, false );
    }

    public void getXml( Element lengthElem )
    {
        // backward-compatible, for now
        DomUtils .addAttribute( lengthElem, "scale", Integer.toString( currentScale .getScale() + SCALE_OFFSET ) );
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
        this .currentScale .setScale( scale - SCALE_OFFSET );  // vZome files record the actual scale, not user scale
        
        // TODO handle other two attribute values!
        
        attrValue = length .getAttribute( "divisor" );
        half = ( attrValue == null || attrValue .isEmpty() )? false : "2" .equals( attrValue );
    }

    public void doAction( String action, ActionEvent e ) throws Exception
    {
        if ( "toggleHalf" .equals( action ) )
        {
            this .half = ! this .half;
            fireLengthChange();
        }
        else if ( "reset" .equals( action ) || "short" .equals( action ) )
        {
            this .unitFactor = standardUnitFactor;
            currentScale .setScale( 0 );
        }
        else if ( "supershort" .equals( action ) )
        {
            this .unitFactor = standardUnitFactor;
            currentScale .setScale( -1 );
        }
        else if ( "medium" .equals( action ) )
        {
            this .unitFactor = standardUnitFactor;
            currentScale .setScale( 1 );
        }
        else if ( "long" .equals( action ) )
        {
            this .unitFactor = standardUnitFactor;
            currentScale .setScale( 2 );
        }
        else if ( "scaleUp" .equals( action ) )
            currentScale .doAction( action, e );
        else if ( "scaleDown" .equals( action ) )
            currentScale .doAction( action, e );
        else if ( "newZeroScale" .equals( action ) )
        {
            int realScale = currentScale .getScale();
            unitFactor = field .multiply( unitFactor, field .createPower( realScale ) );
            currentScale .setScale( 0 );
        }
        
//        else if ( action .startsWith( "adjustScale." ) )
//        {
//            int amt = Integer .parseInt( action .substring( "adjustScale." .length() ) );
//            this .scale -= amt;
//        }
        else
            super.doAction( action, e );
    }

    public String[] getCommandList( String listName )
    {
        int order = this .field .getOrder();

        if ( "labels" .equals( listName ) )
        {
            // first label should always be "1", last should always be "/"
            String[] result = new String[ order + 1 ];
            result[ 0 ] = "1";
            result[ order ] = "/";
            for( int i = 1; i < order; i++ )
                result[ i ] = this .field .getIrrational( i );
            return result;
        }

        if ( "values" .equals( listName ) )
        {
            int[] intsWithDivisor = field .toIntegersWithDivisor( unitFactor );
            // last value should always be the global divisor
            String[] result = new String[ order + 1 ];
            for( int i = 0; i <= order; i++ )
                result[ i ] = Integer .toString( intsWithDivisor[ i ] );
            return result;
        }

        return super.getCommandList( listName );
    }

    public void setProperty( String property, Object value )
    {
        if ( "half" .equals( property ) )
        {
            boolean oldHalf = half;
            half = Boolean .getBoolean( (String) value );
            if ( half != oldHalf )
                fireLengthChange();
        }
        else if ( "scale" .equals( property ) )
        {
            currentScale .setProperty( property, value );
            return;
        }
        else if ( "customUnit" .equals( property ) )
        {
            StringTokenizer values = new StringTokenizer( (String) value );
            int[] inputs = new int[ field .getOrder() + 1 ]; // divisor will be the last int
            for ( int i = 0; values .hasMoreTokens(); i++ )
                inputs[ i ] = Integer .parseInt( values .nextToken() );
            unitFactor = field .fromIntegersWithDivisor( inputs );
            currentScale .setScale( 0 );
            fireLengthChange();
            return;
        }
        else
            super.setProperty( property, value );
    }

    public String getProperty( String string )
    {
        if ( "half" .equals( string ) )
            return Boolean .toString( this .half );

        if ( "scale" .equals( string ) )
            return currentScale .getProperty( string );

        if ( "unitText" .equals( string ) )
            return readable( unitFactor );

        if ( "unitIsCustom" .equals( string ) )
            return Boolean .toString( ! RationalVectors .equals( unitFactor, 0, standardUnitFactor, 0 ) );

        if ( "lengthText" .equals( string ) )
        {
            int realScale = currentScale .getScale();
            int[] result = this .unitFactor;
            result = field .multiply( result, field .createPower( realScale ) );
            return readable( result );
        }

        if ( "scaleFactorHtml" .equals( string ) )
        {
            return field .getIrrational( 0 ) + "<font size=+1>" + currentScale .getProperty( "scaleHtml" ) + "</font>";
        }

        return super.getProperty( string );
    }
    
    private String readable( int[] value )
    {
        StringBuffer buf = new StringBuffer();
        field .getNumberExpression( buf, value, 0, AlgebraicField.DEFAULT_FORMAT );
        return buf .toString();
    }

    /**
     * Get the actual length value to use when rendering.  This value will multiply the zone's normal vector,
     * whatever its length is (not necessarily a unit vector).
     */
    public int[] getValue()
    {
        // TODO push part of this into AlgebraicField somehow?
        int[] result = field .multiply( this .unitFactor, this .fixedFactor );
        if ( half )
            result = field .multiply( result, field .createRational( new int[]{ 1,2 } ) );
        
        // TODO support more than one scaling, like rho and sigma for heptagons
        int realScale = currentScale .getScale() + SCALE_OFFSET;
        result = field .multiply( result, field .createPower( realScale ) );
        return result;
    }

    public MouseTool getMouseTool()
    {
        return currentScale .getMouseTool();
    }

    /**
     * This is basically an inverse of getValue(), but with scale fixed at zero,
     * thus forcing unitFactor to float.
     * 
     * @param actualLength
     */
    public void setActualLength( int[] actualLength )
    {
        half = false;
        currentScale .setScale( 0 );

        actualLength = field .multiply( actualLength, field .createPower( -SCALE_OFFSET ) );
        unitFactor = field .divide( actualLength, fixedFactor );
        fireLengthChange();
    }
}
