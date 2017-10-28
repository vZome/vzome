
//(c) Copyright 2005, Scott Vorthmann.  All rights reserved.

package com.vzome.core.editor;


import com.vzome.core.model.Manifestation;
import com.vzome.core.model.RealizedModel;

public class InvertSelection extends ChangeSelection
{
    protected final RealizedModel mManifestations;

    public InvertSelection( Selection selection, RealizedModel model, boolean groupInSelection )
    {
        super( selection, groupInSelection );
        mManifestations = model;
    }
    
    @Override
    public void perform()
    {
        for (Manifestation m : mManifestations) {
            if ( m .getRenderedObject() != null )
            {
                if ( mSelection .manifestationSelected( m ) )
                    unselect( m );
                else
                    select( m );
            }
        }
        redo();
    }

    @Override
    protected String getXmlElementName()
    {
        return "InvertSelection";
    }

}
