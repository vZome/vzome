package com.vzome.core.edits;

import java.io.IOException;

import com.vzome.core.algebra.AlgebraicField.Registry;
import com.vzome.core.editor.api.EditorModel;
import com.vzome.core.editor.api.ManifestConstructions;
import com.vzome.core.algebra.AlgebraicVector;
import com.vzome.core.model.SimpleMeshJson;

public class ImportSimpleMeshJson extends ImportMesh
{
    public ImportSimpleMeshJson( EditorModel editor )
    {
        super( editor );
    }

    @Override
    protected void parseMeshData( AlgebraicVector offset, ManifestConstructions events, Registry registry )
            throws IOException
    {
        SimpleMeshJson .parse( meshData, offset, projection, events, registry );
    }

    @Override
    protected String getXmlElementName()
    {
        return "ImportSimpleMeshJson";
    }
}
