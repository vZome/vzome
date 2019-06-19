
//(c) Copyright 2005, Scott Vorthmann.  All rights reserved.

package com.vzome.core.editor;


import com.vzome.core.model.Manifestation;

public class SelectAll extends ChangeSelection
{
    public SelectAll( EditorModel editor )
    {
        super( editor .getSelection() );
        
        for (Manifestation m : editor .getRealizedModel() ) {
            if ( m .getRenderedObject() != null )
            {
                if ( ! this .mSelection .manifestationSelected( m ) )
                    select( m, true );
            }
        }
    }

    @Override
    protected String getXmlElementName()
    {
        return "SelectAll";
    }

}
