
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
        for ( Iterator<Manifestation> all = model .iterator(); all .hasNext(); ) {
            Manifestation m = all .next();
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
