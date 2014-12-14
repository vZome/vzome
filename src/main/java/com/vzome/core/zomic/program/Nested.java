
package com.vzome.core.zomic.program;

import com.vzome.core.zomic.ZomicException;

public class Nested extends Anything{

	protected  Anything m_body;

	public  void accept( Visitor visitor ) throws ZomicException {
		visitor .visitNested( this );
	}

	public  void setBody( Anything body ) {
		m_body = body;
	}

	public  Anything getBody() {
		return m_body;
	}


}


