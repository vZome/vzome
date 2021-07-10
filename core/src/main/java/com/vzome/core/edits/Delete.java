package com.vzome.core.edits;

import java.util.ArrayList;

import com.vzome.core.commands.Command.Failure;
import com.vzome.core.editor.api.ChangeManifestations;
import com.vzome.core.editor.api.EditorModel;
import com.vzome.core.model.Manifestation;

public class Delete extends ChangeManifestations
{
	@Override
	public void perform() throws Failure
	{
        ArrayList<Manifestation> inputs = new ArrayList<>();
        for (Manifestation man : mSelection) {
        	inputs .add( man );
            unselect(man);
        }
        redo();  // commit the initial unselect operations

        for ( Manifestation man : inputs )
        {
			this .deleteManifestation( man );
		}
        super .perform();
	}

	public Delete( EditorModel editorModel )
    {
        super( editorModel );
	}

	@Override
	protected String getXmlElementName()
	{
		return "Delete";
	}

}
