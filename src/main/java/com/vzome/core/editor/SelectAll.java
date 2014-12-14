
//(c) Copyright 2005, Scott Vorthmann.  All rights reserved.

package com.vzome.core.editor;

import java.util.Iterator;

import com.vzome.core.model.Manifestation;
import com.vzome.core.model.RealizedModel;

public class SelectAll extends ChangeSelection
{
    public SelectAll( Selection selection, RealizedModel model, boolean groupInSelection )
    {
        super( selection, groupInSelection );
        for ( Iterator all = model .getAllManifestations(); all .hasNext(); ) {
            Manifestation m = (Manifestation) all .next();
            if ( m .getRenderedObject() != null )
            {
                if ( ! selection .manifestationSelected( m ) )
                    select( m, true );
            }
        }
    }

    protected String getXmlElementName()
    {
        return "SelectAll";
    }

}
