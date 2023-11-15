package com.vzome.core.edits;

import com.vzome.core.algebra.AlgebraicVector;
import com.vzome.core.commands.Command;
import com.vzome.core.construction.FreePoint;
import com.vzome.core.construction.Polygon;
import com.vzome.core.construction.SegmentJoiningPoints;
import com.vzome.core.editor.api.ChangeManifestations;
import com.vzome.core.editor.api.EditorModel;
import com.vzome.core.model.Manifestation;
import com.vzome.core.model.Panel;

public class PanelPerimeters extends ChangeManifestations {

	public PanelPerimeters(EditorModel editorModel) {
		super(editorModel);
	}

    @Override
    public void perform() throws Command.Failure
    {
        for (Manifestation man : mSelection) {
        	if(!(man instanceof Panel)) {
        		unselect(man);
        	}
        }
        redo(); // commit the unselects
        
        for (Manifestation man : mSelection) {
        	if(man instanceof Panel) {
        		unselect(man);
        		Panel panel = (Panel)man;
        		Polygon polygon = (Polygon) panel .getFirstConstruction();
        		AlgebraicVector[] vertices =  polygon.getVertices();
        		final FreePoint first = new FreePoint(vertices[0]);
        		FreePoint start = first;
        		for(int i = 1; i < vertices.length; i++) {
            		FreePoint end = new FreePoint(vertices[i]);
            		select(manifestConstruction(start));
            		select(manifestConstruction(new SegmentJoiningPoints(start, end)));
            		start = end; //prep for next iteration and exiting the loop
        		}
        		// close the loop
        		select(manifestConstruction(start));
        		select(manifestConstruction(new SegmentJoiningPoints(start, first)));
        	}
        }
        redo(); // commit new constructions
    }

	@Override
	protected String getXmlElementName() {
		return "PanelPerimeters";
	}

}
