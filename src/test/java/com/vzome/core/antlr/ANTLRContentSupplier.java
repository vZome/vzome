
package com.vzome.core.antlr;

import org.xml.sax .*;
import org.xml.sax.helpers .AttributesImpl;
import antlr .*;

@Deprecated
public class ANTLRContentSupplier extends ANTLR2XML .Default{

	public  ANTLRContentSupplier( ContentHandler handler ) {
		super();

			m_handler = handler;
		}

    private final  ContentHandler m_handler;

	private 
	AttributesImpl m_attrBuffer = new AttributesImpl();

	private  boolean m_startIsPending = false;

    @Override
	public 
	void startElement( String name ) throws RecognitionException {
		try  {
			flushPendingStart();
			super .startElement( name );
			m_startIsPending = true;
		} catch( SAXException se ) {
			throwForANTLR( se );
		}
	}

    @Override
	public 
	void attribute( String name, String value ) throws RecognitionException {
		m_attrBuffer .addAttribute( null, name, name, null, value );
	}

    @Override
	public  void endElement() throws RecognitionException {
		try  {
			flushPendingStart();
			String name = topElementName();
			m_handler .endElement( null, name, name );
			super .endElement();
		} catch( SAXException se ) {
			throwForANTLR( se );
		}
	}

	private  void flushPendingStart() throws SAXException {
		if ( m_startIsPending ) {
			String name = topElementName();
			m_handler .startElement( null, name, name, m_attrBuffer );
			m_attrBuffer = new AttributesImpl();
			m_startIsPending = false;
		}
	}

	private 
	void throwForANTLR( SAXException se ) throws RecognitionException {
		Exception e = se .getException();
		if ( e == null ) {
			e = se;
		}
        Token token;
        try {
            token = m_parser.LT(1);
        }
        catch (TokenStreamException tse) {
            throw new RuntimeException( "there should already be a good token" );
        }
		throw new ANTLR2XMLException( token, e .getMessage() );
	}


}


