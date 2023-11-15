package com.vzome.core.edits;

import com.vzome.core.commands.Command;
import com.vzome.core.construction.FreePoint;
import com.vzome.core.editor.api.ChangeManifestations;
import com.vzome.core.editor.api.EditorModel;
import com.vzome.core.model.Manifestation;
import com.vzome.core.model.Panel;

public class PanelMidpoints extends ChangeManifestations {

	public PanelMidpoints(EditorModel editorModel) {
		super(editorModel);
	}

    @Override
    public void perform() throws Command.Failure
    {
        for (Manifestation man : mSelection) {
        	unselect(man);
        	if(man instanceof Panel) {
        		Panel panel = (Panel)man;
        		select(manifestConstruction(new FreePoint(panel.getCentroid())));
        	}
        }
        redo(); // commit the unselects and new constructions
    }

    @Override
	protected String getXmlElementName() {
		return "PanelMidpoints";
	}

}
