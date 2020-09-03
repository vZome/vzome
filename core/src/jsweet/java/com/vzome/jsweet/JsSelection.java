package com.vzome.jsweet;

import java.util.Iterator;
import java.util.List;

import com.vzome.core.editor.api.Selection;
import com.vzome.core.model.Manifestation;

public class JsSelection implements Selection
{
    public JsSelection()
    {
        // TODO Auto-generated constructor stub
    }

    @Override
    public Iterator<Manifestation> iterator()
    {
        throw new RuntimeException( "unimplemented" );
    }

    @Override
    public void clear()
    {
        throw new RuntimeException( "unimplemented" );
    }

    @Override
    public boolean manifestationSelected(Manifestation man)
    {
        throw new RuntimeException( "unimplemented" );
    }

    @Override
    public void selectWithGrouping(Manifestation mMan)
    {
        throw new RuntimeException( "unimplemented" );
    }

    @Override
    public void unselectWithGrouping(Manifestation mMan)
    {
        throw new RuntimeException( "unimplemented" );
    }

    @Override
    public void select(Manifestation mMan)
    {
        throw new RuntimeException( "unimplemented" );
    }

    @Override
    public void unselect(Manifestation mMan)
    {
        throw new RuntimeException( "unimplemented" );
    }

    @Override
    public void gatherGroup()
    {
        throw new RuntimeException( "unimplemented" );
    }

    @Override
    public void gatherGroup211()
    {
        throw new RuntimeException( "unimplemented" );
    }

    @Override
    public void scatterGroup()
    {
        throw new RuntimeException( "unimplemented" );
    }

    @Override
    public void scatterGroup211()
    {
        throw new RuntimeException( "unimplemented" );
    }

    @Override
    public boolean isSelectionAGroup()
    {
        throw new RuntimeException( "unimplemented" );
    }

    @Override
    public int size()
    {
        throw new RuntimeException( "unimplemented" );
    }

    @Override
    public void copy(List<Manifestation> bookmarkedSelection)
    {
        throw new RuntimeException( "unimplemented" );
    }

}
