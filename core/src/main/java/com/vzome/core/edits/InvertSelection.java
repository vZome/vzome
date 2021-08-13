
package com.vzome.core.edits;


import com.vzome.core.editor.api.ChangeSelection;
import com.vzome.core.editor.api.EditorModel;
import com.vzome.core.model.Manifestation;
import com.vzome.core.model.RealizedModel;

public class InvertSelection extends ChangeSelection
{
    protected final RealizedModel mManifestations;

    public InvertSelection( EditorModel editor )
    {
        super( editor .getSelection() );
        mManifestations = editor .getRealizedModel();
    }
    
    @Override
    public void perform()
    {
        for (Manifestation m : mManifestations) {
            if ( m .isRendered() )
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
