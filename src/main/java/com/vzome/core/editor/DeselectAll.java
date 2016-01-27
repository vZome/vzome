
//(c) Copyright 2005, Scott Vorthmann.  All rights reserved.

package com.vzome.core.editor;


import com.vzome.core.model.Manifestation;

public class DeselectAll extends ChangeSelection
{
    public DeselectAll( Selection selection, boolean groupInSelection )
    {
        super( selection, groupInSelection );
        
        for (Manifestation man : selection) {
            unselect( man, true );
        }
    }

    protected String getXmlElementName()
    {
        return "DeselectAll";
    }

}
