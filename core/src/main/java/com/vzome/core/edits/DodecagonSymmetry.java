
//(c) Copyright 2005, Scott Vorthmann.  All rights reserved.

package com.vzome.core.edits;


import com.vzome.core.construction.Construction;
import com.vzome.core.construction.Point;
import com.vzome.core.construction.SymmetryTransformation;
import com.vzome.core.construction.Transformation;
import com.vzome.core.editor.api.ChangeManifestations;
import com.vzome.core.editor.api.EditorModel;
import com.vzome.core.editor.api.ImplicitSymmetryParameters;
import com.vzome.core.editor.api.SymmetryAware;
import com.vzome.core.math.symmetry.Symmetry;
import com.vzome.core.model.Manifestation;

public class DodecagonSymmetry extends ChangeManifestations
{
    private final Point center;
    
    private final Symmetry symmetry;
    
    public DodecagonSymmetry( EditorModel editor )
    {
        super( editor .getSelection(), editor .getRealizedModel() );
        this.center = ((ImplicitSymmetryParameters) editor) .getCenterPoint();
        this.symmetry = ((SymmetryAware) editor) .getSymmetrySystem() .getSymmetry();
    }
    
    @Override
    public void perform()
    {
        Transformation transform = new SymmetryTransformation( symmetry, 1, center );
        
        for (Manifestation man : mSelection) {
            Construction c = man .getFirstConstruction();

            for ( int i = 0; i < 11; i++ )
            {
                c = transform .transform( c );
                if ( c == null )
                    continue;
                select( manifestConstruction( c ) );
            }
        }
        redo();
    }
    
    @Override
    protected String getXmlElementName()
    {
        return "DodecagonSymmetry";
    }
}
