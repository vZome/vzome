/*
 * Created on Dec 14, 2003
 */
package com.vzome.core.zomic.program;

import com.vzome.core.zomic.ZomicException;

/**
 * @author vorth
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
    @Override
    public void accept( Visitor visitor ) throws ZomicException
    {
        visitor .visitUntranslatable( message == null? "" : message );
    }
}
