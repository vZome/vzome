

package com.vzome.core.model;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import com.vzome.core.construction.Construction;

/**
 * @author Scott Vorthmann
 */
public abstract class Manifestation
{
    protected final Set mManifests = new HashSet(5);
    
    protected Object mRendered = null;
    
    private boolean hidden = false;
    
    private int mId = NO_ID;
    
    private static final int NO_ID = -1;
    
    private static int NEXT_ID = 0;
        
    void resetId()
    {
        NEXT_ID = 0;
        mId = NO_ID;
    }
    
    int getId()
    {
        if ( mId == NO_ID )
            mId = NEXT_ID++;
        return mId;
    }
    
    public void addConstruction( Construction c )
    {
        mManifests .add( c );
    }
    
    public void removeConstruction( Construction c )
    {
        mManifests .remove( c );
    }
    
    public Iterator getConstructions()
    {
        return mManifests .iterator();
    }
    
    public boolean isUnnecessary()
    {
        return mManifests .isEmpty();
    }
    
    public void setRenderedObject( Object obj )
    {
        mRendered = obj;
//        if ( obj != null )
//            System .out.println( "set rendered object" );
    }
    
    public Object getRenderedObject()
    {
        return mRendered;
    }
    
    public boolean isHidden()
    {
        return this .hidden;
    }
    
    public abstract int[] /*AlgebraicVector*/ getLocation();
    
    private Group mContainer;
    
    public Group getContainer()
    {
        return mContainer;
    }
    
    public void setContainer( Group container )
    {
        mContainer = container;
    }

	public void setHidden( boolean hidden )
	{
		this.hidden = hidden;
	}

	public boolean isRendered()
	{
		return mRendered != null;
	}
}
