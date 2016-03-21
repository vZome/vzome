
//(c) Copyright 2007, Scott Vorthmann.  All rights reserved.

package com.vzome.core.commands;


import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.vzome.core.algebra.AlgebraicVector;
import com.vzome.core.construction.Construction;
import com.vzome.core.math.DomUtils;
import com.vzome.core.math.symmetry.Axis;
import com.vzome.core.math.symmetry.IcosahedralSymmetry;
import com.vzome.core.math.symmetry.QuaternionicSymmetry;
import com.vzome.core.math.symmetry.Symmetry;


public abstract class AbstractCommand implements Command
{
    /**
     * This default behavior deserializes in the old way, before XmlSaveFormat .COMPACTED_COMMAND_EDITS
     * @param attributes
     * @param xml
     * @param format
     * @return
     */
    public AttributeMap setXml( Element xml, XmlSaveFormat format ) 
    {
        AttributeMap attrs = format .loadCommandAttributes( xml );
        
        setFixedAttributes( attrs, format );
        
        return attrs;
    }

    
    public void setFixedAttributes( AttributeMap attributes, XmlSaveFormat format )
    {
        attributes .put( Command .FIELD_ATTR_NAME, format .getField() );
    }

    /**
     * This default behavior serializes in the old way, before XmlSaveFormat .COMPACTED_COMMAND_EDITS
     * @param attributes
     * @return
     */
    public void getXml( Element result, AttributeMap attributes )
    {
        if ( attributes == null )
            return;
        for (String key : attributes .keySet()) {
            // don't get confused... mAttrs is a set of "command attributes",
            //   each of which will be saved as an XML element with a "name" attribute.
            //
            // skip all of these... synthesized at load time
            if ( key .equals( Command.FIELD_ATTR_NAME ) )
                continue;
            if ( key .equals( CommandTransform .SYMMETRY_CENTER_ATTR_NAME ) )
                continue;
            if ( key .equals( CommandTransform .SYMMETRY_AXIS_ATTR_NAME ) )
                continue;
            if ( key .equals( CommandImportVEFData.FIELD_ATTR_NAME ) )
                continue;
            Object value = attributes .get( key );
            if ( value instanceof IcosahedralSymmetry )
                continue;  // we never record this one, as an optimization... see setXml below
            saveCommandAttribute( result, key, value );
        }
    }

    
    public static void saveCommandAttribute( Element command, String attrName, Object value )
    {
    	Document doc = command .getOwnerDocument();
        Element valElem = null;
        if ( value instanceof int[] ) {
            int[] v = (int[]) value;
            valElem = command .getOwnerDocument() .createElement( "RationalVector" );
            boolean allOnes = true, allZeros = true;
            for ( int i = 0; i < v.length/2; i++ ) {
                allZeros = allZeros && (v[ 2*i ] == 0);
                allOnes = allOnes && (v[ 2*i+1 ] == 1);
            }
            if ( !allZeros ) {
                StringBuffer numerators = new StringBuffer();
                for ( int i = 0; i < v.length/2; i++ ) {
                    if ( i > 0 )
                        numerators .append( " " );
                    numerators .append( v[ 2*i ] );
                }
                DomUtils .addAttribute( valElem, "nums", numerators .toString() );
                if ( !allOnes ) {
                    StringBuffer denominators = new StringBuffer();
                    for ( int i = 0; i < v.length/2; i++ ) {
                        if ( i > 0 )
                            denominators .append( " " );
                        denominators .append( v[ 2*i+1 ] );
                    }
                    DomUtils .addAttribute( valElem, "denoms", denominators .toString() );
                }
            }
        } else if ( value instanceof Axis ) {
            valElem = doc .createElement( "Axis" );
            ((Axis) value) .getXML( valElem );
        } else if ( value instanceof Boolean ) {
            valElem = doc .createElement( "Boolean" );
            DomUtils .addAttribute( valElem, "value", ((Boolean) value) .toString() );
        } else if ( value instanceof Integer ) {
            valElem = doc .createElement( "Integer" );
            DomUtils .addAttribute( valElem, "value", ((Integer) value) .toString() );
        } else if ( value instanceof Construction ) {
            valElem = ((Construction) value) .getXml( command .getOwnerDocument() );
        } else if ( value instanceof String ) {
            valElem = doc .createElement( "String" );
            String str = XmlSaveFormat .escapeNewlines( (String) value );
            valElem .appendChild( doc .createTextNode( str ) );
        } else if ( value instanceof QuaternionicSymmetry ) {
            valElem = doc .createElement( "QuaternionicSymmetry" );
            DomUtils .addAttribute( valElem, "name", ((QuaternionicSymmetry) value) .getName() );
        } else if ( value instanceof Symmetry ) {
            valElem = doc .createElement( "Symmetry" );
            DomUtils .addAttribute( valElem, "name", ((Symmetry) value) .getName() );
        } else if ( value == null ) {
            valElem = doc .createElement( "Null" );
        } else {
            throw new IllegalStateException( "unable to save " + value .getClass() .getName() );
        }
//        if ( valElem != null )
//            elem .appendChild( valElem );
        DomUtils .addAttribute( valElem, "attrName", attrName );
        command .appendChild( valElem );
    }
    
    
    public boolean attributeIs3D( String attrName )
    {
        return true;
    }

    public void setQuaternion( AlgebraicVector offset )
    {}
}
