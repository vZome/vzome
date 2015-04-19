/*
 * Created on May 12, 2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.vzome.core.zomic.program;


/**
 * @author vorth
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class Label extends ZomicStatement {

	protected final String mLabel;
	
	public Label( String id ){
		
		this .mLabel = id;
	}

	/* (non-Javadoc)
	 * @see com.vzome.core.zomic.program.ZomicProgram#accept(com.vzome.core.zomic.program.Visitor)
	 */
	public void accept(Visitor visitor) {
		
		visitor .visitLabel( mLabel );
	}

}
