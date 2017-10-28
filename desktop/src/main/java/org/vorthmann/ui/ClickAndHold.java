
//(c) Copyright 2005, Scott Vorthmann.  All rights reserved.

package org.vorthmann.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * @author Scott Vorthmann
 *
 */
public class ClickAndHold extends MouseAdapter implements Runnable
{
    private final static long THRESHOLD = 400;
    
    private final ActionListener mAction;
    
    private transient Thread mThread;
    
    public ClickAndHold( ActionListener action )
    {
        mAction = action;
    }

    @Override
    public void mousePressed( MouseEvent arg0 )
    {
        mThread = new Thread( this );
        mThread .start();
    }

    @Override
    public void mouseReleased( MouseEvent arg0 )
    {
        mThread .interrupt();
    }


    @Override
    public void run()
    {
        try {
            Thread .sleep( THRESHOLD );
            mAction .actionPerformed( new ActionEvent( this, 0, "" ) );
        } catch ( InterruptedException e ) {
            // business as usual, mouseReleased was called
        }
    }
}
