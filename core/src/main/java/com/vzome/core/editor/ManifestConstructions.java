package com.vzome.core.editor;

import java.util.ArrayList;

import com.vzome.core.construction.Construction;
import com.vzome.core.construction.ConstructionChanges;
import com.vzome.core.mesh.Color;
import com.vzome.core.mesh.ColoredMeshJson;
import com.vzome.core.model.Manifestation;

// TODO use this in CommandEdit as well
//
@SuppressWarnings("serial")
public class ManifestConstructions extends ArrayList<Construction> implements ConstructionChanges, ColoredMeshJson.Events
{
	private final ChangeManifestations edit;

	public ManifestConstructions( ChangeManifestations edit )
	{
		this .edit = edit;
	}

    @Override
    public void constructionAdded( Construction c )
    {
        this .edit .manifestConstruction( c );
        this .edit .redo();
    }

    @Override
    public void constructionAdded( Construction c, Color color )
    {
        Manifestation manifestation = this .edit .manifestConstruction( c );
        if ( color != null )
            this .edit .colorManifestation( manifestation, color );
        this .edit .select( manifestation );
        this .edit .redo();
    }
}