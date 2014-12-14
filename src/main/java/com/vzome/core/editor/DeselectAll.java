
//(c) Copyright 2005, Scott Vorthmann.  All rights reserved.

package com.vzome.core.editor;

import java.util.Iterator;

import com.vzome.core.model.Manifestation;

public class DeselectAll extends ChangeSelection
{
    public DeselectAll( Selection selection, boolean groupInSelection )
    {
        super( selection, groupInSelection );
        
        for ( Iterator all = selection .iterator(); all .hasNext(); )
            unselect( (Manifestation) all .next(), true );
    }

    protected String getXmlElementName()
    {
        return "DeselectAll";
    }

}
