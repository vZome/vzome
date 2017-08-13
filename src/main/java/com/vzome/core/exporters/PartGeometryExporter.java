
//(c) Copyright 2007, Scott Vorthmann.  All rights reserved.

package com.vzome.core.exporters;

import java.io.File;
import java.io.IOException;
import java.io.Writer;

import com.vzome.core.algebra.AlgebraicField;
import com.vzome.core.algebra.AlgebraicVector;
import com.vzome.core.editor.Selection;
import com.vzome.core.generic.ArrayComparator;
import com.vzome.core.model.Connector;
import com.vzome.core.model.Manifestation;
import com.vzome.core.model.Panel;
import com.vzome.core.model.VefModelExporter;
import com.vzome.core.render.Colors;
import com.vzome.core.render.RenderedManifestation;
import com.vzome.core.render.RenderedModel;
import com.vzome.core.viewing.Lights;
import com.vzome.core.viewing.Camera;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

public class PartGeometryExporter extends VefExporter
{
    Selection selection;

    public PartGeometryExporter( Camera scene, Colors colors,
            Lights lights, RenderedModel model, Selection selection )
    {
        super( scene, colors, lights, model );
        
        this.selection = selection;
    }

	@Override
    public void doExport( File directory, Writer writer, int height, int width ) throws IOException
    {
        AlgebraicField field = mModel .getField();
        VefModelExporter exporter = new VefModelExporter( writer, field );
        
        for (RenderedManifestation rm : mModel) {
            exporter .exportManifestation( rm .getManifestation() );
        }
        
        exporter .finish();
        
        exportSelection(exporter);
    }

    private void exportSelection(VefModelExporter exporter)
    {
        // save the first selected ball as "tip" and sort the selected panels
        Connector tip = null;
        ArrayComparator<AlgebraicVector> arrayComparator = new ArrayComparator<>();
        SortedSet<AlgebraicVector[]> panelVertices = new TreeSet<>( arrayComparator.getLengthFirstArrayComparator() );
        Map<AlgebraicVector[], Panel> vertexArrayPanelMap = new HashMap<>();

		for (Manifestation man : selection) {
			if ( man instanceof Connector ) {
                if(tip == null) {
                    tip = (Connector) man;
                }
                // else just ignore the other balls
            }
            else if ( man instanceof Panel ) {
                Panel panel = (Panel) man;
                // Unsorted list retains the panel vertex order
                ArrayList<AlgebraicVector> corners = new ArrayList<>(panel.getVertexCount());
                for (AlgebraicVector vertex : panel) {
                    corners .add( vertex );
                }
                AlgebraicVector[] cornerArray = new AlgebraicVector[corners.size()];
                corners.toArray(cornerArray);
                panelVertices.add( cornerArray );
                vertexArrayPanelMap.put(cornerArray, panel);
            }
        }

		/* 
			ExportedVEFShapes requires "tip" to precede "middle" when importing the vef
				so be sure to export Connectors before Panels
				regardless of which order the user selected them.
			Be sure that exactly one "tip" is selected if any panels are exported as "middle".
            Selected Panels should be exported in sorted order using the same panel sorting logic as VefModelExporter
		*/

        // TODO: Should we warn the user if more than one ball was selected and that extra ones are being ignored?
        // TODO: Should we warn the user if no ball was selected and that any "middle" panels are being ignored?
        // If so, how do we notify them? Should we throw an Exception or write to the log?

        if(tip != null) {
            exporter .exportSelectedManifestation( null ); // newline
            // Connectors ("tip")
            exporter .exportSelectedManifestation( tip );

            if(! panelVertices.isEmpty()) {
                exporter .exportSelectedManifestation( null ); // newline
                // Panels ("middle")
                for(AlgebraicVector[] vertexArray : panelVertices) {
                    Panel panel = vertexArrayPanelMap.get(vertexArray);
                    exporter .exportSelectedManifestation( panel );
                }
            }
            exporter .exportSelectedManifestation( null ); // newline
        }
    }
}
