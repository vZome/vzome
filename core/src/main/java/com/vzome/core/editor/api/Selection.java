package com.vzome.core.editor.api;

import java.util.List;

import com.vzome.core.model.Group;
import com.vzome.core.model.Manifestation;

public interface Selection extends Iterable<Manifestation>
{
    void clear();

    boolean manifestationSelected( Manifestation man );

    void selectWithGrouping( Manifestation mMan );

    void unselectWithGrouping( Manifestation mMan );

    void select( Manifestation mMan );

    void unselect( Manifestation mMan );
    
    Manifestation getSingleSelection( final Class<? extends Manifestation> kind );

    // only for GroupSelection
    void gatherGroup();
    void gatherGroup211();
    void scatterGroup();
    void scatterGroup211();
    boolean isSelectionAGroup();

    int size();

    // only for ModuleTool
    void copy(List<Manifestation> bookmarkedSelection);

    // only for ChangeSelection
    static Group biggestGroup( Manifestation m )
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
}
