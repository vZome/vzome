
package com.vzome.core.exporters;

import java.io.File;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.SortedSet;
import java.util.TreeSet;

import com.vzome.core.algebra.AbstractAlgebraicField;
import com.vzome.core.algebra.AlgebraicVector;
import com.vzome.core.commands.Command;
import com.vzome.core.construction.Construction;
import com.vzome.core.construction.Point;
import com.vzome.core.construction.Polygon;
import com.vzome.core.editor.DocumentModel;
import com.vzome.core.editor.Tool;
import com.vzome.core.editor.ToolsModel;
import com.vzome.core.math.RealVector;
import com.vzome.core.model.Manifestation;
import com.vzome.core.model.Panel;
import com.vzome.core.model.Strut;
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
        AbstractAlgebraicField field = (AbstractAlgebraicField) super .mModel .getField();

        // Extract the tip_vertex parameter from its bookmark
        Optional<Tool> tipBookmark = toolsModel .values() .stream() .filter( tool -> "tip vertex" .equals( tool.getLabel() ) ) .findAny();
        if ( ! tipBookmark .isPresent() )
            throw new Command.Failure( "You must have a bookmark named \"tip vertex\" for the strut endpoint." );
        List<Construction> tipItems = tipBookmark .get() .getParameters();
        Construction tipPoint = tipItems .get( 0 );
        if ( tipItems .size() > 1 || ! (tipPoint instanceof Point ) )
            throw new Command.Failure( "The \"tip vertex\" bookmark must select a single ball." );
        AlgebraicVector tipVertex = ((Point) tipPoint) .getLocation();

        // Extract the "floating panels" parameter from its bookmark
        Optional<Tool> floatingBookmark = toolsModel .values() .stream() .filter( tool -> "floating panels" .equals( tool.getLabel() ) ) .findAny();
        SortedSet<AlgebraicVector> floatingVerticesSet = new TreeSet<>();
        if ( ! floatingBookmark .isPresent() )
            throw new Command.Failure( "You must have a bookmark named \"floating panels\"." );
        for ( Construction polygon : floatingBookmark .get() .getParameters() ) {
            if ( ! (polygon instanceof Polygon ) )
                throw new Command.Failure( "The \"floating panels\" bookmark must select only panels." );
            for ( AlgebraicVector vertex : ((Polygon) polygon) .getVertices() ) {
                floatingVerticesSet .add( vertex );
            }
        }
        
        // Extract the optional "bottom face" parameter from its bookmark
        AlgebraicVector bottomFaceNormal = null; // a sentinel value tested later
        Optional<Tool> bottomFaceBookmark = toolsModel .values() .stream() .filter( tool -> "bottom face" .equals( tool.getLabel() ) ) .findAny();
        if ( bottomFaceBookmark .isPresent() ) {
            List<Construction> bottomFaceItems = bottomFaceBookmark .get() .getParameters();
            Construction bottomFacePanel = bottomFaceItems .get( 0 );
            if ( bottomFaceItems .size() > 1 || ! (bottomFacePanel instanceof Polygon ) )
                throw new Command.Failure( "The \"bottom face\" bookmark must select a single panel." );
            bottomFaceNormal = ((Polygon) bottomFacePanel) .getNormal();
        }
        
        // Find all the vertices of all the panels
        SortedSet<AlgebraicVector> fixedVerticesSet = new TreeSet<>();
        String orbitName = null;
        for ( RenderedManifestation rm : super .mModel ) {
            Manifestation man = rm .getManifestation();
            if ( man instanceof Panel )
            {
                Panel panel = (Panel) man;
                for ( AlgebraicVector vertex : panel ) {
                    if ( ! floatingVerticesSet .contains( vertex ) )
                        fixedVerticesSet .add( vertex );
                }
            } else if ( man instanceof Strut ) {
                if ( orbitName != null )
                    throw new Command.Failure( "The model must contain a single prototype strut." );
                orbitName = rm .getStrutOrbit() .getName();
            }
        }
        if ( orbitName == null )
            throw new Command.Failure( "The model must contain a single prototype strut." );
        
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

        // %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% OK, ready to output!
        
        super .output = new PrintWriter( writer );

        String prelude = super .getBoilerplate( "com/vzome/core/exporters/zome-strut-prelude.scad" );
        prelude = prelude .replaceAll( "%%ORBIT%%", orbitName );
        output .println( prelude );

        output .println( "  irrational = " + field .getCoefficients()[ 1 ] + ";" );
        output .println();
        
        output .println( "module " + orbitName + "_strut( size, scalar=1.0, offsets=0 ) {" );
        output .println();

        if ( bottomFaceNormal == null ) {
            output .println( "  // WARNING: The vZome design contained no \"bottom face\" bookmark." );
            output .println( "  bottom_face_normal = [ 0, 0, -1 ];" );
        } else {
            RealVector bottomFaceDirection = super .mModel .renderVector( bottomFaceNormal ) .normalize();
            output .println( "  bottom_face_normal = [ " + bottomFaceDirection.toString() + " ];" );
        }
        output .println();

        String tipVertexString = super .mModel .renderVector( tipVertex ) .scale( RZOME_MM_SCALING ) .toString();
        output .println( "  tip_vertex = [ " + tipVertexString + " ];" );
        output .println();
        
        output .println( "  fixed_vertices = [ " );
        for ( AlgebraicVector vertex : sortedFixedVertexList ) {
            output .print( "[ " );
            output .print( super .mModel .renderVector( vertex ) .scale( RZOME_MM_SCALING ) .toString() );
            output .print( " ], " );
        }
        output .println( " ];" );
        
        output .println( "  floating_vertices = [ " );
        for ( AlgebraicVector vertex : sortedFloatingVertexList ) {
            output .print( "[ " );
            output .print( super .mModel .renderVector( vertex ) .scale( RZOME_MM_SCALING ) .toString() );
            output .print( " ], " );
        }
        output .println( " ];" );
        
        output .println( "  faces = [ " );
        for ( RenderedManifestation rm : super .mModel ) {
            Manifestation man = rm .getManifestation();
            if ( man instanceof Panel )
            {
                output .print( "[ " );
                Panel panel = (Panel) man;
                for ( AlgebraicVector vertex : panel ) {
                    int index = sortedFixedVertexList .indexOf( vertex );
                    if ( index >= 0 ) {
                        output .print( index );
                    } else {
                        index = sortedFloatingVertexList .indexOf( vertex );
                        output .print( sortedFixedVertexList .size() + index );
                    }
                    output .print( ", " );
                }
                output .print( " ], " );
            }
        }
        output .println( " ];" );

        output .println( "  zome_strut( tip_vertex, fixed_vertices, floating_vertices, faces, bottom_face_normal, size, scalar, offsets );" );
        output .println( "}" );

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
