package com.vzome.core.edits;

import java.io.IOException;

import com.vzome.core.algebra.AlgebraicField.Registry;
import com.vzome.core.algebra.AlgebraicVector;
import com.vzome.core.editor.EditorModel;
import com.vzome.core.editor.ManifestConstructions;
import com.vzome.core.model.ColoredMeshJson;

public class ImportColoredMeshJson extends ImportMesh
{
    public ImportColoredMeshJson( EditorModel editor )
    {
        super( editor );
    }

    @Override
    protected void parseMeshData( AlgebraicVector offset, ManifestConstructions events, Registry registry )
            throws IOException
    {
        ColoredMeshJson .parse( meshData, offset, events, registry );
    }

    @Override
    protected String getXmlElementName()
    {
        return "ImportColoredMeshJson";
    }
}
