
//(c) Copyright 2005, Scott Vorthmann.  All rights reserved.

package com.vzome.core.edits;


import com.vzome.core.editor.api.ChangeSelection;
import com.vzome.core.editor.api.EditorModel;
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
    protected boolean groupingAware()
    {
        return true;
    }

    @Override
    protected String getXmlElementName()
    {
        return "DeselectAll";
    }

}
