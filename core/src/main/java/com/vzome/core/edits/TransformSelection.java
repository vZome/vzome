
//(c) Copyright 2005, Scott Vorthmann.  All rights reserved.

package com.vzome.core.edits;

import java.util.ArrayList;
import java.util.List;

import com.vzome.core.construction.Construction;
import com.vzome.core.construction.Transformation;
import com.vzome.core.editor.api.ChangeManifestations;
import com.vzome.core.editor.api.Selection;
import com.vzome.core.model.Manifestation;
import com.vzome.core.model.RealizedModel;

public class TransformSelection extends ChangeManifestations
{
    protected final Transformation transform;
    
    public TransformSelection( Selection selection, RealizedModel realized, Transformation transform )
    {
        super( selection, realized );
        this .transform = transform;
    }
    
    @Override
    public void perform()
    {
        List<Manifestation> inputs = new ArrayList<>();
        for (Manifestation man : mSelection) {
            unselect( man );
            inputs .add( man );
        }
        
        redo();  // get the unselects out of the way, if anything needs to be re-selected
        // now apply it to the input objects
        for (Manifestation m : inputs) {
            if ( ! m .isRendered() )
                continue;
            Construction c = m .getFirstConstruction();
            Construction result = transform .transform( c );
            select( manifestConstruction( result ), true );
        }
        redo();
    }
    
    @Override
    protected String getXmlElementName()
    {
        return "TransformSelection";
    }
}
