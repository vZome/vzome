
package com.vzome.core.exporters;

import java.io.File;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Optional;
import java.util.SortedSet;
import java.util.TreeSet;

import com.vzome.core.algebra.AlgebraicVector;
import com.vzome.core.commands.Command;
import com.vzome.core.construction.Construction;
import com.vzome.core.construction.Point;
import com.vzome.core.construction.Polygon;
import com.vzome.core.editor.DocumentModel;
import com.vzome.core.editor.Tool;
import com.vzome.core.editor.ToolsModel;
import com.vzome.core.model.Manifestation;
import com.vzome.core.model.Panel;
import com.vzome.core.render.RenderedManifestation;

/**
 * An exporter that produces a parametric OpenSCAD file,
 * to support generation of STL files for struts of arbitrary length.
 * This is based on Aaron Siegel's "zome-strut.scad" library.
 * 
 * @author vorth
 *
 */
public class OpenScadExporter extends Exporter3d
{
    public OpenScadExporter()
    {
        super( null, null, null, null );
    }

    @Override
    public void exportDocument( DocumentModel doc, File file, Writer writer, int height, int width ) throws Exception
    {
        ToolsModel toolsModel = doc .getToolsModel();
        super .mModel = doc .getRenderedModel();

        Optional<Tool> tipBookmark = toolsModel .values() .stream() .filter( tool -> "tip_vertex" .equals( tool.getLabel() ) ) .findAny();
        if ( ! tipBookmark .isPresent() )
            throw new Command.Failure( "You must have a bookmark named \"tip_vertex\" for the strut endpoint." );
        Construction tipPoint = tipBookmark .get() .getParameters() .get( 0 );
        if ( ! (tipPoint instanceof Point ) )
            throw new Command.Failure( "The \"tip_vertex\" bookmark must select a single ball." );

        Optional<Tool> floatingBookmark = toolsModel .values() .stream() .filter( tool -> "floating_panels" .equals( tool.getLabel() ) ) .findAny();
        SortedSet<AlgebraicVector> floatingVerticesSet = new TreeSet<>();
        if ( ! floatingBookmark .isPresent() )
            throw new Command.Failure( "You must have a bookmark named \"floating_panels\"." );
        for ( Construction polygon : floatingBookmark .get() .getParameters() ) {
            if ( ! (polygon instanceof Polygon ) )
                throw new Command.Failure( "The \"floating_panels\" bookmark must select only panels." );
            for ( AlgebraicVector vertex : ((Polygon) polygon) .getVertices() ) {
                floatingVerticesSet .add( vertex );
            }
        }
        
        SortedSet<AlgebraicVector> fixedVerticesSet = new TreeSet<>();
        for ( RenderedManifestation rm : super .mModel ) {
            Manifestation man = rm .getManifestation();
            if ( man instanceof Panel )
            {
                Panel panel = (Panel) man;
                for ( AlgebraicVector vertex : panel ) {
                    if ( ! floatingVerticesSet .contains( vertex ) )
                        fixedVerticesSet .add( vertex );
                }
            }
        }
        
        // Now all panel vertices are in one of two sorted sets, fixed and floating.
        // Up to this point, the sorted TreeSets have collected and sorted every unique vertex of every panel.
        // From now on we'll need their index, so we copy them into two ArrayLists, preserving their sorted order,
        // so we can get their index into that array.
        ArrayList<AlgebraicVector> sortedFixedVertexList = new ArrayList<>(fixedVerticesSet);
        ArrayList<AlgebraicVector> sortedFloatingVertexList = new ArrayList<>(floatingVerticesSet);
        // we no longer need the vertices collections, 
        // so set them to null to free the memory and to ensure we don't use them later by mistake.
        fixedVerticesSet = null;
        floatingVerticesSet = null;
        
        
        
        
        
        
        
        
        super .output = new PrintWriter( writer );

        super .printBoilerplate( "com/vzome/core/exporters/zome-strut-prelude.scad" );
        
        output .println( "irrational = " + super .mModel .getField() .getIrrational( 1 ) + ";" );
        output .println();

        String tipVertex = super .mModel .renderVector( ((Point) tipPoint) .getLocation() ) .toString();
        output .println( "tip_vertex = [ " + tipVertex + " ];" );
        output .println();
        
        output .println( "faces = [ " );
        for ( RenderedManifestation rm : super .mModel ) {
            Manifestation man = rm .getManifestation();
            if ( man instanceof Panel )
            {
                output .print( "[ " );
                boolean first = true;
                Panel panel = (Panel) man;
                for ( AlgebraicVector vertex : panel ) {
                    if ( ! first )
                        output .print( ", " );
                    output .print( super .mModel .renderVector( vertex ) .toString() );
                    first = false;
                }
                output .print( " ]" );
            }
        }
        output .println( " ];" );

        output.flush();
    }

//    private void exportSelection()
//    {
//        // save the first selected ball as "tip" and sort the selected panels
//        Connector tip = null;
//        ArrayComparator<AlgebraicVector> arrayComparator = new ArrayComparator<>();
//        SortedSet<AlgebraicVector[]> panelVertices = new TreeSet<>( arrayComparator.getLengthFirstArrayComparator() );
//        Map<AlgebraicVector[], Panel> vertexArrayPanelMap = new HashMap<>();
//
//		for (Manifestation man : selection) {
//			if ( man instanceof Connector ) {
//                if(tip == null) {
//                    tip = (Connector) man;
//                }
//                // else just ignore the other balls
//            }
//            else if ( man instanceof Panel ) {
//                Panel panel = (Panel) man;
//                // Unsorted list retains the panel vertex order
//                ArrayList<AlgebraicVector> corners = new ArrayList<>(panel.getVertexCount());
//                for (AlgebraicVector vertex : panel) {
//                    corners .add( vertex );
//                }
//                AlgebraicVector[] cornerArray = new AlgebraicVector[corners.size()];
//                corners.toArray(cornerArray);
//                panelVertices.add( cornerArray );
//                vertexArrayPanelMap.put(cornerArray, panel);
//            }
//        }
//
//		/* 
//			ExportedVEFShapes requires "tip" to precede "middle" when importing the vef
//				so be sure to export Connectors before Panels
//				regardless of which order the user selected them.
//			Be sure that exactly one "tip" is selected if any panels are exported as "middle".
//            Selected Panels should be exported in sorted order using the same panel sorting logic as VefModelExporter
//		*/
//
//        // TODO: Should we warn the user if more than one ball was selected and that extra ones are being ignored?
//        // TODO: Should we warn the user if no ball was selected and that any "middle" panels are being ignored?
//        // If so, how do we notify them? Should we throw an Exception or write to the log?
//
//        if(tip != null) {
//            exporter .exportSelectedManifestation( null ); // newline
//            // Connectors ("tip")
//            exporter .exportSelectedManifestation( tip );
//
//            if(! panelVertices.isEmpty()) {
//                exporter .exportSelectedManifestation( null ); // newline
//                // Panels ("middle")
//                for(AlgebraicVector[] vertexArray : panelVertices) {
//                    Panel panel = vertexArrayPanelMap.get(vertexArray);
//                    exporter .exportSelectedManifestation( panel );
//                }
//            }
//            exporter .exportSelectedManifestation( null ); // newline
//        }
//    }

    @Override
    public String getFileExtension()
    {
        return "scad";
    }

    @Override
    public void doExport( File file, Writer writer, int height, int width ) throws Exception {}  // This won't be called
}
