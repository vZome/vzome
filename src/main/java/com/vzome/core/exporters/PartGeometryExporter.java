
//(c) Copyright 2007, Scott Vorthmann.  All rights reserved.

package com.vzome.core.exporters;

import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.util.Iterator;

import com.vzome.core.algebra.AlgebraicField;
import com.vzome.core.algebra.AlgebraicNumber;
import com.vzome.core.editor.Selection;
import com.vzome.core.model.Connector;
import com.vzome.core.model.Manifestation;
import com.vzome.core.model.Panel;
import com.vzome.core.model.VefModelExporter;
import com.vzome.core.render.Colors;
import com.vzome.core.render.RenderedManifestation;
import com.vzome.core.render.RenderedModel;
import com.vzome.core.viewing.Lights;
import com.vzome.core.viewing.ViewModel;

public class PartGeometryExporter extends VefExporter
{
    Selection selection;

    public PartGeometryExporter( ViewModel scene, Colors colors,
            Lights lights, RenderedModel model, Selection selection )
    {
        super( scene, colors, lights, model );
        
        this.selection = selection;
    }

	@Override
    public void doExport( File directory, Writer writer, int height, int width ) throws IOException
    {
        AlgebraicField field = mModel .getField();
        AlgebraicNumber scale = field .createPower( -5 );
        VefModelExporter exporter = new VefModelExporter( writer, field, scale );
        
        for ( Iterator<RenderedManifestation> rms = mModel .iterator(); rms .hasNext(); )
        {
            Manifestation man = rms .next() .getManifestation();
            
            exporter .exportManifestation( man );
        }
        
        exporter .finish();
        
		/* 
			ExportedVEFShapes requires "tip" to precede "middle" when importing the vef
				so be sure to export Connectors before Panels
				regardless of which order the user selected them.
			Also, be sure that exactly one "tip" is selected if any panels are exported as "middle".
		*/
		// Connectors ("tip")
		int selectedBallCount = 0;
		for (Manifestation man : selection) {
			if ( man instanceof Connector )
			{
				if(selectedBallCount == 0) {
					exporter .exportSelectedManifestation( man );
				}
				selectedBallCount++;
			}
		}
		if(selectedBallCount > 1) {
			// TODO: Should we warn the user that more than one ball was selected,
			// and that the extra ones as well as any selected panels are being ignored?
			// If so, how do we notify them? Should we throw an Exception or write to the log?
		}
		else if(selectedBallCount == 1) { // "middle" section is only allowed with a preceding "tip" section.
			// Panels ("middle")
			int selectedPanelCount = 0;
			for (Manifestation man : selection) {
				if ( man instanceof Panel )
				{
					selectedPanelCount++;
					exporter .exportSelectedManifestation( man );
				}
			}
			if(selectedPanelCount > 0) {
				// pass null param to append a newline when we've finished panels
				exporter .exportSelectedManifestation( null );
			}
		}
    }
}
