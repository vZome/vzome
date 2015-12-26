package com.vzome.core.editor;

import java.util.ArrayList;

import com.vzome.core.construction.Construction;
import com.vzome.core.construction.ConstructionChanges;

// TODO use this in CommandEdit as well
//
public class ManifestConstructions extends ArrayList implements ConstructionChanges
{
	private final ChangeManifestations edit;

	public ManifestConstructions( ChangeManifestations edit )
	{
		this .edit = edit;
	}

    public void constructionAdded( Construction c )
    {
    	this .edit .manifestConstruction( c );
    	this .edit .redo();
    }
}