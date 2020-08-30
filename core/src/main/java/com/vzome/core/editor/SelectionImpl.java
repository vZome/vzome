

package com.vzome.core.editor;


import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.vzome.core.model.Group;
import com.vzome.core.model.GroupElement;
import com.vzome.core.model.Manifestation;
import com.vzome.core.model.ManifestationChanges;

/**
 * @author Scott Vorthmann
 */
public class SelectionImpl implements Selection
{
    // Note that LinkedHashSet maintains insertion-order, which is significant in this case.
    private Collection<Manifestation> mManifestations = new LinkedHashSet<>();
    
    private final List<ManifestationChanges> mListeners = new ArrayList<>();
    
    private Group mSelectedGroup = null;  // when the selection is exactly one entire group
    
    private static final Logger logger = Logger .getLogger( "com.vzome.core.editor.selection" );

    public void copy( List<Manifestation> target )
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
    
    @Override
    public Iterator<Manifestation> iterator()
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
        for (ManifestationChanges mc : mListeners) {
            mc .manifestationAdded( m );
        }
    }
    
    public void unselect( Manifestation m )
    {
        if ( mManifestations .remove( m ) )
        {
            if ( logger .isLoggable( Level .FINER ) )
                logger .finer( "deselect: " + m .toString() );
            for (ManifestationChanges mc : mListeners) {
                mc .manifestationRemoved( m );
            }
        }
    }
    
    public void selectWithGrouping( Manifestation m )
    {
        if ( mManifestations .contains( m ) )
            return;
        
        // this accommodates a bug in 2.1.1... I don't know what made m null, but this works
        if ( m == null )
            return;

        Group group = Selection.biggestGroup( m );
        if ( group == null )
            add( m );
        else
            selectGroup( group );
        mSelectedGroup = group;
    }
    
    public void unselectWithGrouping( Manifestation m )
    {
        if ( mManifestations .contains( m ) ) {
            Group group = Selection.biggestGroup( m );
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
        for (ManifestationChanges mc : mListeners) {
            mc .manifestationAdded( m );
        }
    }
    
    private void remove( Manifestation m )
    {
        if ( mManifestations .remove( m ) )
        {
            if ( logger .isLoggable( Level .FINER ) )
                logger .finer( "deselect: " + m .toString() );
            for (ManifestationChanges mc : mListeners) {
                mc .manifestationRemoved( m );
            }
        }
    }
    
    private void selectGroup( Group group )
    {
        for (GroupElement next : group) {
            if ( next instanceof Group )
                selectGroup( (Group) next );
            else
                add( (Manifestation) next );
        }
    }
    
    private void unselectGroup( Group group )
    {
        for (GroupElement next : group) {
            if ( next instanceof Group )
                unselectGroup( (Group) next );
            else
                remove( (Manifestation) next );
        }
    }
    
    public Manifestation getSingleSelection( final Class<? extends Manifestation> kind )
    {
        int count = 0;
        Manifestation result = null;
        for (Manifestation next : mManifestations) {
            if ( kind .isAssignableFrom( next.getClass() ) ) {
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
        
        for (Manifestation m : mManifestations) {
            if ( onlyOne && ( selectedGroup != null ) )
                // should have already verified with isSelectionAGroup() that there
                //  is only one group represented
                return selectedGroup;
            
            Group group = Selection.biggestGroup( m );
            if ( group == null )
                return null; // some ungrouped manifestation selected
            else if ( selectedGroup == null )
                selectedGroup = group; // first group found
            else if ( group != selectedGroup )
                return null; // more than one root group selected
            // still the same root group
        }
        return selectedGroup;
    }
    
    public void gatherGroup()
    {
//        if ( getSelectedGroup() != null )
//            throw new IllegalStateException( "already grouped" );
//        
        Group newGroup = new Group();
        
        for (Manifestation m : mManifestations) {
            Group group = Selection.biggestGroup( m );
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
        
        for ( Iterator<GroupElement> ms = selectedGroup .iterator(); ms .hasNext(); )
        {
            GroupElement next = ms .next();
            ms .remove();
            next .setContainer( null );
        }
    }

    public void gatherGroup211()
    {
        if ( mSelectedGroup != null )
            return;
        
        mSelectedGroup = new Group();
        
        for (Manifestation m : mManifestations) {
            Group group = Selection.biggestGroup( m );
            if ( group == null ) {
                mSelectedGroup .add( m );
//                System.out.println( "GROUP add:" + m .toString() );
            }
            else {
                mSelectedGroup .add( group );
//                System.out.println( "GROUP add: nested group of " + m .toString() );
            }
        }
        for (GroupElement next : mSelectedGroup) {
            next .setContainer( mSelectedGroup );
        }
    }

    public void scatterGroup211()
    {
        if ( mSelectedGroup == null )
            return;
        
        for ( Iterator<GroupElement> ms = mSelectedGroup .iterator(); ms .hasNext(); )
        {
            GroupElement next = ms .next();
            ms .remove();
//            if ( next instanceof Manifestation )
//                System.out.println( "GROUP remove:" + next .toString() );
//            else
//                System.out.println( "GROUP remove: nested group" );
            next .setContainer( null );
        }
    }

	public void refresh( boolean on, SelectionImpl otherSelection )
	{
        for (Manifestation m : mManifestations) {
            if (otherSelection == null || ! otherSelection .mManifestations .contains( m )) {
                if (on) {
                    for (ManifestationChanges mc : mListeners) {
                        mc.manifestationAdded( m );
                    }
                } else {
                    for (ManifestationChanges mc : mListeners) {
                        mc  .manifestationRemoved( m );
                    }
                }
            }
        }
	}

    public int size()
    {
        return this .mManifestations .size();
    }

	public void clear()
	{
		// for ChangeSelection undo when the selection is ordered
		this .mManifestations .clear();
	}
}
