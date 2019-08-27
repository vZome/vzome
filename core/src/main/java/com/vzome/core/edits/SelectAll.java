
//(c) Copyright 2005, Scott Vorthmann.  All rights reserved.

package com.vzome.core.edits;


import com.vzome.core.editor.ChangeSelection;
import com.vzome.core.editor.EditorModel;
import com.vzome.core.model.Manifestation;

public class SelectAll extends ChangeSelection
{
    public SelectAll( EditorModel editor )
    {
        super( editor .getSelection() );
        
        for (Manifestation m : editor .getRealizedModel() ) {
            if ( m .isRendered() )
            {
                if ( ! this .mSelection .manifestationSelected( m ) )
                    select( m, true );
            }
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
        return "SelectAll";
    }

}
