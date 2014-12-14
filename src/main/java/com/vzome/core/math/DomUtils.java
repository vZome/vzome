package com.vzome.core.math;

import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSSerializer;

public class DomUtils {
	
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
	
	public static String toString( Element element )
	{
		DOMImplementationLS lsImpl = (DOMImplementationLS) element .getOwnerDocument() .getImplementation() .getFeature( "LS", "3.0" );
		LSSerializer serializer = lsImpl .createLSSerializer();
		serializer .getDomConfig() .setParameter( "xml-declaration", false ); //by default its true, so set it to false to get String without xml-declaration
		return serializer .writeToString( element );
	}
	
	public static void serialize( Document doc, OutputStream out ) throws UnsupportedEncodingException, TransformerException
	{		
        TransformerFactory tf = TransformerFactory .newInstance();
        Transformer transformer = tf .newTransformer();
        transformer .setOutputProperty( OutputKeys.OMIT_XML_DECLARATION, "no" );
        transformer .setOutputProperty( OutputKeys.METHOD, "xml" );
        transformer .setOutputProperty( OutputKeys.INDENT, "yes" );
        transformer .setOutputProperty( OutputKeys.ENCODING, "UTF-8" );
        transformer .setOutputProperty( "{http://xml.apache.org/xslt}indent-amount", "2" );
        
        transformer.transform( new DOMSource( doc ), 
             new StreamResult( new OutputStreamWriter( out, "UTF-8" ) ) );
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
	
	public static String getXmlString( Element node )
	{
	    DOMImplementationLS lsImpl = (DOMImplementationLS)node.getOwnerDocument().getImplementation().getFeature("LS", "3.0");
	    LSSerializer serializer = lsImpl.createLSSerializer();
	    serializer.getDomConfig().setParameter("xml-declaration", false); //by default its true, so set it to false to get String without xml-declaration
	    return serializer.writeToString(node);
	}
}
