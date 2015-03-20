package com.vzome.core.zomic.program;

import com.vzome.core.zomic.ZomicException;

/**
 * Description here.
 * 
 * @author Scott Vorthmann 2003
 */
public class Build extends ZomicStatement {

    private  boolean m_build, m_destroy;

    public Build( boolean build, boolean destroy ) {
        super();

            m_build = build;
            m_destroy = destroy;
        }

    public  void accept( Visitor visitor ) throws ZomicException
	{
        visitor .visitBuild( m_build, m_destroy );
    }

    public  void setBuild( boolean value ) {
    		m_build = value;
    }

    public  void setDestroy( boolean value ) {
    		m_destroy = value;
    }
    
    public boolean justMoving(){
    		return m_build == m_destroy;
    }

}
