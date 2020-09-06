
//(c) Copyright 2005, Scott Vorthmann.  All rights reserved.

package com.vzome.core.edits;


import com.vzome.core.commands.Command.Failure;
import com.vzome.core.construction.ChangeOfBasis;
import com.vzome.core.construction.Construction;
import com.vzome.core.construction.Point;
import com.vzome.core.construction.Segment;
import com.vzome.core.construction.Transformation;
import com.vzome.core.editor.api.ChangeManifestations;
import com.vzome.core.editor.api.EditorModel;
import com.vzome.core.editor.api.ImplicitSymmetryParameters;
import com.vzome.core.model.Manifestation;
import com.vzome.core.model.Strut;

public class AffineTransformAll extends ChangeManifestations
{
    private Point center;
    
    public AffineTransformAll( EditorModel editor )
    {
        super( editor .getSelection(), editor .getRealizedModel() );
        this.center = ((ImplicitSymmetryParameters) editor) .getCenterPoint();
    }
    
    @Override
    public void perform() throws Failure
    {
        Segment s1 = null, s2 = null, s3 = null;
        for (Manifestation man : mSelection) {
            unselect( man );
            if ( man instanceof Strut )
            {
                if ( s1 == null )
                    s1 = (Segment) man .getFirstConstruction();
                else if ( s2 == null )
                    s2 = (Segment) man .getFirstConstruction();
                else if ( s3 == null )
                    s3 = (Segment) man .getFirstConstruction();
            }
        }
        if ( s3 == null || s2 == null || s1 == null )
            throw new Failure( "three struts required" );
        
        redo();  // get the unselects out of the way, if anything needs to be re-selected
        
        // now, construct the affine transformation
        Transformation transform = new ChangeOfBasis( s1, s2, s3, center, true );

        // now apply it to all objects
        for (Manifestation m : mManifestations) {
            if ( ! m .isRendered() )
                continue;
            Construction c = m .getFirstConstruction();
            Construction result = transform .transform( c );
            select( manifestConstruction( result ) );
        }
        redo();
    }
    
    @Override
    protected String getXmlElementName()
    {
        return "AffineTransformAll";
    }
}
