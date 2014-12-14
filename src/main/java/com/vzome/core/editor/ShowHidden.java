
//(c) Copyright 2007, Scott Vorthmann.  All rights reserved.

package com.vzome.core.editor;

import java.util.Iterator;

import com.vzome.core.model.Manifestation;
import com.vzome.core.model.RealizedModel;

public class ShowHidden extends ChangeManifestations
{

    public ShowHidden( Selection selection, RealizedModel realized, boolean groupInSelection )
    {
        super( selection, realized, groupInSelection );
    }
    
    public void perform()
    {
        for ( Iterator all = mManifestations .getAllManifestations(); all .hasNext(); ) {
            Manifestation m = (Manifestation) all .next();
            if ( ! m .isRendered() )
            {
                showManifestation( m );
                select( m );
            }
            else if ( mSelection .manifestationSelected( m ) )
                unselect( m );
        }
        redo();
    }

    protected String getXmlElementName()
    {
        return "ShowHidden";
    }

}
