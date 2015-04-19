
package com.vzome.core.zomic.program;

import com.vzome.core.zomic.ZomicException;

public class Nested extends ZomicStatement{

	protected  ZomicStatement m_body;

	public  void accept( Visitor visitor ) throws ZomicException {
		visitor .visitNested( this );
	}

	public  void setBody( ZomicStatement body ) {
		m_body = body;
	}

	public  ZomicStatement getBody() {
		return m_body;
	}


}


