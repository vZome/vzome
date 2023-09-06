package com.vzome.xml;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class DomUtils
{
    public static void addAttribute( Element elem, String name, String value )
	{
		elem .setAttribute( name, value );
	}

	public static Element getFirstChildElement( Element elem, String name )
	{
		NodeList elems = elem .getElementsByTagName( name );
		return (Element) elems .item( 0 );
	}

	public static void preserveSpace( Element contentElem )
	{
		contentElem .setAttributeNS( "http://www.w3.org/XML/1998/namespace", "xml:space", "preserve" );
	}

	public static Element getFirstChildElement( Element parent )
	{
        NodeList children = parent .getChildNodes();
        if ( children .getLength() == 0 )
            return null;
        for ( int k = 0; k < children .getLength(); k++ ) 
        {
        	Node kid = children .item( k );
        	if ( kid instanceof Element ) {
        		return (Element) kid;
        	}
        }
		return null;
	}

	public static Element getChild( Element parent, int i )
	{
        NodeList children = parent .getChildNodes();
        if ( children .getLength() == 0 )
            return null;
        int count = 0;
        for ( int k = 0; k < children .getLength(); k++ ) 
        {
        	Node kid = children .item( k );
        	if ( kid instanceof Element ) {
        		if ( count == i )
        			return (Element) kid;
        		else
        			++ count;
        	}
        }
		return null;
	}
	
	/**
	 * This is required for JSweet, which ignores the radix in Integer.toString( i, 2 )
	 * @param i
	 * @return
	 */
	public static String byteToBinary( int i )
	{
	    // I tried to be more performant with a byte array, but JSweet screwed that up, too.
	    String result = "";
        result += ( i / 8 == 1 )? "1" : "0"; i = i % 8;
        result += ( i / 4 == 1 )? "1" : "0"; i = i % 4;
        result += ( i / 2 == 1 )? "1" : "0"; i = i % 2;
        result += ( i     == 1 )? "1" : "0";

        return result;
    }
}
