
//(c) Copyright 2005, Scott Vorthmann.  All rights reserved.

package com.vzome.core.editor;

import java.util.Iterator;

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
    
    public void perform()
    {
        for ( Iterator all = mManifestations .getAllManifestations(); all .hasNext(); ) {
            Manifestation m = (Manifestation) all .next();
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

    protected String getXmlElementName()
    {
        return "InvertSelection";
    }

}
