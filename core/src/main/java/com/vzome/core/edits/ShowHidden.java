
package com.vzome.core.edits;


import com.vzome.core.editor.api.ChangeManifestations;
import com.vzome.core.editor.api.EditorModel;
import com.vzome.core.model.Manifestation;

public class ShowHidden extends ChangeManifestations
{

    public ShowHidden( EditorModel editor )
    {
        super( editor );
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
