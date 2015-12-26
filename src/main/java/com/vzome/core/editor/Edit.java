package com.vzome.core.editor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import com.vzome.core.commands.Command;
import com.vzome.core.construction.Construction;
import com.vzome.core.construction.ConstructionChanges;
import com.vzome.core.construction.ConstructionList;


/**
 * Description here.
 * 
 * @author Scott Vorthmann 2003
 */
public class Edit implements ConstructionChanges{


	Edit( Command command, Map attrs, ConstructionList start )
    {
        mCommand = command;
        mAttrs = attrs;
        mStart = start;
        mEnd = start;
    }
    
	public void setEnd( ConstructionList end )
	{
	    mEnd = end;
	}
	
	
	public ConstructionList getStart()
	{
		return mStart;
	}

	public ConstructionList getEnd()
	{
	    return mEnd;
	}
    
    public Map getAttributes()
    {
        return mAttrs;
    }

    public void undo( ConstructionChanges renderer )
    {
        for ( Iterator added = mHiddenConstructions .iterator(); added .hasNext(); ) {
            Construction constr = (Construction) added .next();
            constr .setVisible( true );
            if ( renderer != null )
                renderer .constructionRevealed( constr );
        }
        for ( Iterator added = mRevealedConstructions .iterator(); added .hasNext(); ) {
            Construction constr = (Construction) added .next();
            constr .setVisible( false );
            if ( renderer != null )
                renderer .constructionHidden( constr );
        }
    }
    
    public void redo( ConstructionChanges renderer )
    {
        for ( Iterator added = mRevealedConstructions .iterator(); added .hasNext(); ) {
            Construction constr = (Construction) added .next();
            constr .setVisible( true );
            renderer .constructionRevealed( constr );
        }
        for ( Iterator added = mHiddenConstructions .iterator(); added .hasNext(); ) {
            Construction constr = (Construction) added .next();
            constr .setVisible( false );
            renderer .constructionHidden( constr );
        }
    }

    protected final Collection mAddedConstructions = new ArrayList();

	protected final Collection mRemovedConstructions = new ArrayList();
	
	protected final Collection mHiddenConstructions = new ArrayList();
	
	protected final Collection mRevealedConstructions = new ArrayList();
    
    protected ConstructionList mStart, mEnd;
    
    protected Command mCommand;
    
    protected Map mAttrs;


    public void constructionAdded( Construction c )
    {
        mAddedConstructions .add( c );
    }


    public void constructionRemoved( Construction c )
    {
        mRemovedConstructions .add( c );
    }


    public void constructionHidden( Construction c )
    {
        mHiddenConstructions .add( c );
    }


    public void constructionRevealed( Construction c )
    {
        mRevealedConstructions .add( c );
    }



    public String getCommandName()
    {
        return mCommand .getClass() .getName();
    }
}
