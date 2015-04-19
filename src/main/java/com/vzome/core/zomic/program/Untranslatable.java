/*
 * Created on Dec 14, 2003
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package com.vzome.core.zomic.program;

import com.vzome.core.zomic.ZomicException;

/**
 * @author vorth
 *
 * To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public class Untranslatable extends ZomicStatement
{
    protected String message;
    
    public Untranslatable( String msg )
    {
        message = msg;
    }
    
    public void setMessage( String msg )
    {
        message = msg;
    }
    
    /* (non-Javadoc)
     * @see com.vzome.core.zomic.program.ZomicProgram#accept(com.vzome.core.zomic.program.Visitor)
     */
    public void accept( Visitor visitor ) throws ZomicException
    {
        visitor .visitUntranslatable( message == null? "" : message );
    }
}
