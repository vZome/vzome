

//(c) Copyright 2005, Scott Vorthmann.  All rights reserved.

package org.vorthmann.ui;

import java.awt.Component;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * An Action that is time-consuming and must not occur on the event-handling thread.
 * Only one such action may be started at once, and it can be canceled before completion.
 * 
 * @author Scott Vorthmann
 *
 */
public abstract class ExclusiveAction implements ActionListener
{
    public static class Excluder
    {
        private final List mListeners = new ArrayList(40);
        
        private final PropertyChangeListener statusListener;
        
        private boolean busy = false;
        
        public Excluder( PropertyChangeListener statusListener )
        {
			this.statusListener = statusListener;
		}

		public boolean isBusy()
        {
        	return this.busy;
        }
        
        public void grab( SwingWorker worker )
        {
            grab();
            worker .start();
        }

        public void grab()
        {
        	this .busy = true;
            for ( Iterator listeners = mListeners .iterator(); listeners .hasNext(); )
            {
                Component component = (Component) listeners .next();
                component .setEnabled( false );
            }
        }

        public void release()
        {
            for ( Iterator listeners = mListeners .iterator(); listeners .hasNext(); )
            {
                ((Component) listeners .next()) .setEnabled( true );
            }
        	this .statusListener .propertyChange( new PropertyChangeEvent( this, "command.status", "busy...", "" ) );
            this .busy = false;
        }
//        
//        private void setEnabled( Component object, boolean value )
//        {
//            try {
//                Class clazz = object .getClass();
//                Method method = clazz .getMethod( "setEnabled", new Class[]{ Boolean.TYPE } );
//                method .invoke( object, new Object[]{ new Boolean( value ) } );
//            } catch ( Exception e ) {
//                throw new RuntimeException( e );
//            }
//        }
        
        public void addExcludable( Component component )
        {
        	// disabling this behavior, in favor of a simple "beep" action when another ExclusiveAction is in progress
//            if ( component .isEnabled() )
//                mListeners .add( component );
        }

        public void reportBusy()
        {
        	this .statusListener .propertyChange( new PropertyChangeEvent( this, "command.status", "", "busy..." ) );
        }
    }
    
    private final Excluder mExcluder;
    
    private static Logger logger = Logger .getLogger( "org.vorthmann.ui.background" );
    
    public ExclusiveAction( Excluder excluder )
    {
        super();
        mExcluder = excluder;
    }

    public void actionPerformed( final ActionEvent e )
    {
    	if ( this .mExcluder .isBusy() ) {
    		this .mExcluder .reportBusy();
    		Toolkit .getDefaultToolkit() .beep();
    		return;
    	}
    	
        final SwingWorker worker = new SwingWorker()
        {            
            public Object construct()
            {
                try {
                    doAction( e );
                    return null;
                } catch ( Exception e ) {
                    logger .log( Level.INFO, e .getMessage(), e );
                    return e;
                }
            }
            
            public void finished()
            {
                Object error = get();
                if ( error != null )
                    showError( (Exception) error );
                mExcluder .release();
            }
        };
        mExcluder .grab( worker );
    }
    
    protected abstract void doAction( ActionEvent e ) throws Exception;
    
    protected abstract void showError( Exception e );
}