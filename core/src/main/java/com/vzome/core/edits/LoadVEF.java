package com.vzome.core.edits;

import java.io.IOException;

import com.vzome.core.algebra.AlgebraicField.Registry;
import com.vzome.core.algebra.AlgebraicVector;
import com.vzome.core.construction.VefToModel;
import com.vzome.core.editor.api.EditorModel;
import com.vzome.core.editor.api.ManifestConstructions;

public class LoadVEF extends ImportMesh {

    public LoadVEF( EditorModel editor )
    {
        super( editor );
    }

    protected boolean deselectInputs()
    {
        return false; // Required for backward compatibility
    }

    @Override
    protected void parseMeshData( AlgebraicVector offset, ManifestConstructions events, Registry registry )
            throws IOException
    {
        VefToModel v2m = new VefToModel( projection, events, scale, offset );
        v2m .parseVEF( this .meshData, this .editor .getSymmetrySystem() .getField() );
    }

    @Override
    protected String getXmlElementName()
    {
        return "LoadVEF";
    }
}
