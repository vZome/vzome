package com.vzome.xml;

import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.sax.SAXSource;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

public class DomParser
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
}
