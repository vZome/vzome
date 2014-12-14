

package com.vzome.core.editor;


import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.vzome.core.model.Group;
import com.vzome.core.model.Manifestation;
import com.vzome.core.model.ManifestationChanges;

/**
 * @author Scott Vorthmann
 */
public class Selection implements Iterable<Manifestation>
{
    private Collection mManifestations = new LinkedHashSet();
    
    private final List mListeners = new ArrayList();
    
    private Group mSelectedGroup = null;  // when the selection is exactly one entire group
    
    private static final Logger logger = Logger .getLogger( "com.vzome.core.editor.selection" );

    public void copy( List target )
    {
        target .addAll( mManifestations );
    }
    
    public void addListener( ManifestationChanges listener )
    {
        mListeners .add( listener );
    }
    
    public void removeListener( ManifestationChanges listener )
    {
        mListeners .remove( listener );
    }
    
    
    public boolean manifestationSelected( Manifestation m )
    {
        return mManifestations .contains( m );
    }
    
    public boolean isEmpty()
    {
    	return mManifestations .isEmpty();
    }
    
    public Iterator iterator()
    {
        return mManifestations .iterator();
    }
    
    public void select( Manifestation m )
    {
        if ( mManifestations .contains( m ) )  // TO DO how does this happen?
            return;
        mManifestations .add( m );
        if ( logger .isLoggable( Level .FINER ) )
            logger .finer( "  select: " + m .toString() );
        for ( Iterator ls = mListeners .iterator(); ls .hasNext(); )
            ((ManifestationChanges) ls .next()) .manifestationAdded( m );
    }
    
    public void unselect( Manifestation m )
    {
        if ( mManifestations .remove( m ) )
        {
            if ( logger .isLoggable( Level .FINER ) )
                logger .finer( "deselect: " + m .toString() );
            for ( Iterator ls = mListeners .iterator(); ls .hasNext(); )
                ((ManifestationChanges) ls .next()) .manifestationRemoved( m );
        }
    }
    
    public void selectWithGrouping( Manifestation m )
    {
        if ( mManifestations .contains( m ) )
            return;
        
        // this accommodates a bug in 2.1.1... I don't know what made m null, but this works
        if ( m == null )
            return;

        Group group = biggestGroup( m );
        if ( group == null )
            add( m );
        else
            selectGroup( group );
        mSelectedGroup = group;
    }
    
    public void unselectWithGrouping( Manifestation m )
    {
        if ( mManifestations .contains( m ) ) {
            Group group = biggestGroup( m );
            if ( group == null )
                remove( m );
            else
                unselectGroup( group );
            mSelectedGroup = null;
        }
    }
    
    private void add( Manifestation m )
    {
        mManifestations .add( m );
        if ( logger .isLoggable( Level .FINER ) )
            logger .finer( "  select: " + m .toString() );
        for ( Iterator ls = mListeners .iterator(); ls .hasNext(); )
            ((ManifestationChanges) ls .next()) .manifestationAdded( m );
    }
    
    private void remove( Manifestation m )
    {
        if ( mManifestations .remove( m ) )
        {
            if ( logger .isLoggable( Level .FINER ) )
                logger .finer( "deselect: " + m .toString() );
            for ( Iterator ls = mListeners .iterator(); ls .hasNext(); )
                ((ManifestationChanges) ls .next()) .manifestationRemoved( m );
        }
    }
    
    private void selectGroup( Group group )
    {
        for ( Iterator it = group .iterator(); it .hasNext(); ) {
            Object next = it .next();
            if ( next instanceof Group )
                selectGroup( (Group) next );
            else
                add( (Manifestation) next );
        }
    }
    
    private void unselectGroup( Group group )
    {
        for ( Iterator it = group .iterator(); it .hasNext(); ) {
            Object next = it .next();
            if ( next instanceof Group )
                unselectGroup( (Group) next );
            else
                remove( (Manifestation) next );
        }
    }
    
    public Manifestation getSingleSelection( Class kind )
    {
        int count = 0;
        Manifestation result = null;
        for ( Iterator mans = mManifestations .iterator(); mans .hasNext(); ) {
            Manifestation next = (Manifestation) mans .next();
            if ( kind .isAssignableFrom( next .getClass() ) ) {
                ++count;
                result = next;                    
            }
        }
        if ( count == 1 )
            return result;
        else
            return null;
    }
    
    public boolean isSelectionAGroup()
    {
        return getSelectedGroup( false ) != null;
    }

    private Group getSelectedGroup( boolean onlyOne )
    {
        Group selectedGroup = null;
        
        for ( Iterator ms = mManifestations .iterator(); ms .hasNext(); )
        {
            if ( onlyOne && ( selectedGroup != null ) )
                // should have already verified with isSelectionAGroup() that there
                //  is only one group represented
                return selectedGroup;
            
            Manifestation m = (Manifestation) ms .next();
            Group group = biggestGroup( m );
            if ( group == null )
                return null; // some ungrouped manifestation selected
            else if ( selectedGroup == null )
                selectedGroup = group; // first group found
            else if ( group != selectedGroup )
                return null; // more than one root group selected
            else
                continue; // still the same root group
        }
        return selectedGroup;
    }
    
    public static Group biggestGroup( Manifestation m )
    {
        Group parent = m .getContainer();
        Group group = parent;
        while ( parent != null ) {
            parent = group .getContainer();
            if ( parent == null )
                break;
            group = parent;
        }
        return group;
    }

    public void gatherGroup()
    {
//        if ( getSelectedGroup() != null )
//            throw new IllegalStateException( "already grouped" );
//        
        Group newGroup = new Group();
        
        for ( Iterator ms = mManifestations .iterator(); ms .hasNext(); )
        {
            Manifestation m = (Manifestation) ms .next();
            Group group = biggestGroup( m );
            if ( group == newGroup )
                ; // already added some ancestor group of m
            else if ( group == null )
            {
                newGroup .add( m );
                m .setContainer( newGroup );
            }
            else
            {
                newGroup .add( group );
                group .setContainer( newGroup );
            }
        }
    }

    public void scatterGroup()
    {
        Group selectedGroup = getSelectedGroup( true );  // assume there's only one, already tested in isSelectionAGroup()
        if ( selectedGroup == null )
            return;
        
        for ( Iterator ms = selectedGroup .iterator(); ms .hasNext(); )
        {
            Object next = ms .next();
            ms .remove();
            if ( next instanceof Group )
                ((Group) next) .setContainer( null );
            else
                ((Manifestation) next) .setContainer( null );
        }
    }

    public void gatherGroup211()
    {
        if ( mSelectedGroup != null )
            return;
        
        mSelectedGroup = new Group();
        
        for ( Iterator ms = mManifestations .iterator(); ms .hasNext(); )
        {
            Manifestation m = (Manifestation) ms .next();
            Group group = biggestGroup( m );
            if ( group == null )
                mSelectedGroup .add( m );
            else
                mSelectedGroup .add( group );
        }

        for ( Iterator ms = mSelectedGroup .iterator(); ms .hasNext(); )
        {
            Object next = ms .next();
            if ( next instanceof Group )
                ((Group) next) .setContainer( mSelectedGroup );
            else
                ((Manifestation) next) .setContainer( mSelectedGroup );
        }
    }

    public void scatterGroup211()
    {
        if ( mSelectedGroup == null )
            return;
        
        for ( Iterator ms = mSelectedGroup .iterator(); ms .hasNext(); )
        {
            Object next = ms .next();
            ms .remove();
            if ( next instanceof Group )
                ((Group) next) .setContainer( null );
            else
                ((Manifestation) next) .setContainer( null );
        }
    }

	public void refresh( boolean on, Selection otherSelection )
	{
		for (Iterator iterator = mManifestations.iterator(); iterator.hasNext();) {
			Manifestation m = (Manifestation) iterator.next();
			if ( otherSelection == null || ! otherSelection .mManifestations .contains( m ) )
			{
			    if ( on ) 
			    {
			        for ( Iterator ls = mListeners .iterator(); ls .hasNext(); )
			            ((ManifestationChanges) ls .next()) .manifestationAdded( m );
			    }
			    else
			    {
			        for ( Iterator ls = mListeners .iterator(); ls .hasNext(); )
			            ((ManifestationChanges) ls .next()) .manifestationRemoved( m );
			    }
			}
		}
	}

    public int size()
    {
        return this .mManifestations .size();
    }
}
