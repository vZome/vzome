package com.vzome.xml;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSSerializer;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

public class DomUtils
{
    public static Element parseXml( InputStream bytes, boolean captureLineNumbers ) throws Exception
    {
        Document doc = null;
        // parse the bytes as XML
        DocumentBuilderFactory factory = DocumentBuilderFactory .newInstance();
        factory .setNamespaceAware( true );

        if ( captureLineNumbers ) {

            // This complicated solution for capturing line numbers is from
            //    http://javacoalface.blogspot.com/2011/04/line-and-column-numbers-in-xml-dom.html

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer nullTransformer = transformerFactory.newTransformer();

            DocumentBuilder docBuilder = factory .newDocumentBuilder();
            doc = docBuilder.newDocument();
            DOMResult domResult = new DOMResult( doc );

            /*
             * Create SAX parser/XMLReader that will parse XML. If factory
             * options are not required then this can be short cut by:
             *      xmlReader = XMLReaderFactory.createXMLReader();
             */
            SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
            saxParserFactory .setNamespaceAware( true );            
            SAXParser saxParser = saxParserFactory.newSAXParser();
            XMLReader xmlReader = saxParser.getXMLReader();

            /*
             * Create our filter to wrap the SAX parser, that captures the
             * locations of elements and annotates their nodes as they are
             * inserted into the DOM.
             */
            xmlReader = new LocationAnnotator( xmlReader, doc );

            /*
             * Create the SAXSource to use the annotator.
             */
            InputSource inputSource = new InputSource( bytes );
            SAXSource saxSource = new SAXSource( xmlReader, inputSource );

            /*
             * Finally read the XML into the DOM.
             */
            nullTransformer.transform(saxSource, domResult);
        }
        else {
            DocumentBuilder builder = factory .newDocumentBuilder();
            doc = builder .parse( bytes );
        }
        bytes.close();

        return doc .getDocumentElement();
    }
	
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
        DOMImplementation impl = element .getOwnerDocument() .getImplementation();
        if ( impl .hasFeature( "LS", "3.0" ) ){
            DOMImplementationLS lsImpl = (DOMImplementationLS) impl .getFeature("LS", "3.0");
            LSSerializer serializer = lsImpl .createLSSerializer();
            serializer .getDomConfig() .setParameter( "xml-declaration", false ); //by default its true, so set it to false to get String without xml-declaration
            return serializer .writeToString( element );
        }
        else {
            try {
                Transformer transformer = TransformerFactory.newInstance().newTransformer();
                StringWriter stringWriter = new StringWriter();
                transformer.transform(new DOMSource(element), new StreamResult(stringWriter));
                return stringWriter.toString();
            } catch (TransformerException e) {
                e.printStackTrace();
                return "<unableToSerialize/>";
            }
        }
    }
	
    public static void serialize( Document doc, Writer out ) throws UnsupportedEncodingException, TransformerException
    {       
        TransformerFactory tf = TransformerFactory .newInstance();
        Transformer transformer = tf .newTransformer();
        transformer .setOutputProperty( OutputKeys.OMIT_XML_DECLARATION, "no" );
        transformer .setOutputProperty( OutputKeys.METHOD, "xml" );
        transformer .setOutputProperty( OutputKeys.INDENT, "yes" );
        transformer .setOutputProperty( OutputKeys.STANDALONE, "no" );
        transformer .setOutputProperty( OutputKeys.ENCODING, "UTF-8" );
        transformer .setOutputProperty( "{http://xml.apache.org/xslt}indent-amount", "2" );
        
        transformer.transform( new DOMSource( doc ), new StreamResult( out ) );
    }
    
    public static void serialize( Document doc, OutputStream out ) throws UnsupportedEncodingException, TransformerException
    {
        serialize( doc, new OutputStreamWriter( out, "UTF-8" ) );
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
	    return toString( node );
	}
}
