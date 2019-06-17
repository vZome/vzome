
//(c) Copyright 2005, Scott Vorthmann.  All rights reserved.

package com.vzome.core.editor;


import com.vzome.core.model.Manifestation;

public class DeselectAll extends ChangeSelection
{
    public DeselectAll( EditorModel editor )
    {
        super( editor .getSelection() );
        
        // TODO: move this to perform()
        for ( Manifestation man : this .mSelection ) {
            unselect( man, true );
        }
    }

    @Override
    protected String getXmlElementName()
    {
        return "DeselectAll";
    }

}
