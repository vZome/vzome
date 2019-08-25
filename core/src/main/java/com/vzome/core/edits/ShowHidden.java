
//(c) Copyright 2007, Scott Vorthmann.  All rights reserved.

package com.vzome.core.edits;


import com.vzome.core.editor.ChangeManifestations;
import com.vzome.core.editor.Selection;
import com.vzome.core.model.Manifestation;
import com.vzome.core.model.RealizedModel;

public class ShowHidden extends ChangeManifestations
{

    public ShowHidden( Selection selection, RealizedModel realized )
    {
        super( selection, realized );
    }
    
    @Override
    public void perform()
    {
        for (Manifestation m : mManifestations) {
            if ( m .isHidden() )
            {
                showManifestation( m );
                select( m );
            }
            else if ( mSelection .manifestationSelected( m ) )
                unselect( m );
        }
        redo();
    }

    @Override
    protected String getXmlElementName()
    {
        return "ShowHidden";
    }

}
