

package com.vzome.core.model;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.vzome.core.algebra.AlgebraicVector;
import com.vzome.core.construction.Construction;
import com.vzome.core.render.RenderedManifestation;

/**
 * @author Scott Vorthmann
 */
public abstract class Manifestation implements GroupElement
{
    protected final Set<Construction> mManifests = new HashSet<>(5);
    
    protected RenderedManifestation mRendered = null;
    
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
    
    public Iterator<Construction> getConstructions()
    {
        return mManifests .iterator();
    }
    
    public boolean isUnnecessary()
    {
        return mManifests .isEmpty();
    }
    
    public void setRenderedObject( RenderedManifestation obj )
    {
        mRendered = obj;
    }
    
    public RenderedManifestation getRenderedObject()
    {
        return mRendered;
    }
    
    public boolean isHidden()
    {
        return this .hidden;
    }
    
    public abstract AlgebraicVector getLocation();

    public abstract AlgebraicVector getCentroid();
    
    private Group mContainer;
    
    public Group getContainer()
    {
        return mContainer;
    }
    
    @Override
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

    public Element getXml( Document doc )
    {
    	return mManifests .isEmpty()
                ? doc .createElement( "NoConstructions" )
                : mManifests .iterator() .next() .getXml( doc );
    }
}
