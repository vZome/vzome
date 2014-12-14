package com.vzome.core.zomic.program;


import com.vzome.core.zomic.ZomicException;

/**
 * Description here.
 * 
 * @author Scott Vorthmann 2003
 */
public class Save extends Nested {

    private  int m_state;

    public  Save( int state ) {
        super();

        m_state = state;
        }

    public  void accept( Visitor visitor ) throws ZomicException {
        visitor .visitSave( this, m_state );
    }

    public  void setState( int state ) {
        m_state = state;
    }



}
