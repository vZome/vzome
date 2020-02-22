package com.vzome.core.edits;

import java.util.ArrayList;

import com.vzome.core.commands.Command.Failure;
import com.vzome.core.editor.ChangeManifestations;
import com.vzome.core.editor.Selection;
import com.vzome.core.model.Manifestation;
import com.vzome.core.model.RealizedModel;

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

	public Delete( Selection selection, RealizedModel realized )
	{
		super( selection, realized );
	}

	@Override
	protected String getXmlElementName()
	{
		return "Delete";
	}

}
