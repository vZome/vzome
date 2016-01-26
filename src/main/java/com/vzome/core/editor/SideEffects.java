
//(c) Copyright 2005, Scott Vorthmann.  All rights reserved.

package com.vzome.core.editor;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.vzome.core.commands.Command;
import com.vzome.core.math.DomUtils;

public abstract class SideEffects implements UndoableEdit
{
    @Override
    public Element getDetailXml( Document doc )
    {
        Element result = this .getXml( doc );
        Element effects = doc .createElement( "effects" );
        
        for (SideEffect se : mItems) {
            if ( se != null )
            {
                Element effect = se .getXml( doc );
                if ( effect != null ) {
                    if ( BUG_ACCOMMODATION_LOGGER .isLoggable( Level.FINEST ) )
                        BUG_ACCOMMODATION_LOGGER .finest( "side-effect: " + DomUtils .getXmlString( effect ) );
                    effects .appendChild( effect );
                }
                // else effect was ChangeConstructions.AttachConstruction, which we don't need to serialize
            }
        }
        result .appendChild( effects );
        return result;
    }

    public boolean isSticky()
    {
        return false;
    }

    private List<SideEffect> mItems = new ArrayList<>();
    
    /**
     * This lets us use this pattern:
     *      plan plan plan plan
     *      redo
     *      plan plan plan
     *      redo
     *      
     * ... so we can sync the state with a redo
     * 
     */
    private int redone = 0;

    public static final Logger BUG_ACCOMMODATION_LOGGER = Logger.getLogger( "com.vzome.core.bug.accommodations" );

    public static void logBugAccommodation( String accommodation )
    {
        if ( BUG_ACCOMMODATION_LOGGER .isLoggable( Level.WARNING ) )
            BUG_ACCOMMODATION_LOGGER .warning( "ACCOMMODATION: " + accommodation );
    }
    
    public interface SideEffect
    {
        public void undo();
        
        public Element getXml( Document doc );

        public void redo();
    }
    
    public boolean isVisible()
    {
    	return true;
    }

    public boolean isDestructive()
    {
        return true;
    }

    protected void plan( SideEffect se )
    {
        mItems .add( se );
    }
    
    public void perform() throws Command.Failure
    {
        // this default assumes that the constructor has already planned the SideEffects.
        // The more correct pattern is to OVERRIDE perform(), and plan side-effects there,
        //  then either redo() (perhaps several times) or call this inherited perform().
        redo();
    }
    
    protected void fail( String message ) throws Command.Failure
    {
        undo(); // in case there has been any redo() already
        throw new Command.Failure( message );
    }

    public void redo()
    {
        for ( int i = redone; i < mItems .size(); i++ )
        {
            SideEffect se = mItems .get( i );
            if ( se != null )
                se .redo();
        }
        redone = mItems .size();
    }

    public void undo()
    {
        for ( int i = mItems .size(); i > 0; i-- )
        {
            SideEffect se = mItems .get( i-1 );
            if ( se != null )
                se .undo();
        }
        redone = 0;
    }

	protected Iterator<SideEffect> getEffects()
	{
		return this .mItems .iterator();
	}
}
