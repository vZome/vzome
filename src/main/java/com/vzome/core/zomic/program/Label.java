/*
 * Created on May 12, 2003
 */
package com.vzome.core.zomic.program;


/**
 * @author vorth
 */
public class Label extends ZomicStatement {

	protected final String mLabel;
	
	public Label( String id ){
		
		this .mLabel = id;
	}

	/* (non-Javadoc)
	 * @see com.vzome.core.zomic.program.ZomicProgram#accept(com.vzome.core.zomic.program.Visitor)
	 */
    @Override
	public void accept(Visitor visitor) {
		
		visitor .visitLabel( mLabel );
	}

}
