
//(c) Copyright 2005, Scott Vorthmann.  All rights reserved.

package com.vzome.core.edits;

import java.util.List;
import java.util.Map;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.vzome.core.algebra.AlgebraicMatrix;
import com.vzome.core.algebra.AlgebraicVector;
import com.vzome.core.algebra.VefVectorExporter;
import com.vzome.core.commands.Command;
import com.vzome.core.commands.Command.Failure;
import com.vzome.core.commands.XmlSaveFormat;
import com.vzome.core.construction.Color;
import com.vzome.core.construction.Construction;
import com.vzome.core.construction.FreePoint;
import com.vzome.core.construction.Point;
import com.vzome.core.construction.Polygon;
import com.vzome.core.construction.PolygonFromVertices;
import com.vzome.core.construction.Segment;
import com.vzome.core.editor.ChangeManifestations;
import com.vzome.core.editor.EditorModel;
import com.vzome.core.editor.OrbitSource;
import com.vzome.core.editor.SymmetrySystem;
import com.vzome.core.editor.api.Shapes;
import com.vzome.core.math.Polyhedron;
import com.vzome.core.math.VefToPolyhedron;
import com.vzome.core.math.symmetry.Axis;
import com.vzome.core.math.symmetry.Direction;
import com.vzome.core.math.symmetry.OrbitSet;
import com.vzome.core.math.symmetry.Symmetry;
import com.vzome.core.model.Manifestation;
import com.vzome.core.model.Panel;
import com.vzome.core.model.RenderedObject;
import com.vzome.core.render.RenderedManifestation;
import com.vzome.core.render.RenderedModel;

public class ReplaceWithShape extends ChangeManifestations
{
    public static final String NAME = "ReplaceWithShape";

    // These three fields are used for pick-based operation
    private String vef;
    private Polyhedron shape;
    private Manifestation ballOrStrut;

    // This field is used for selection-based operation
    private String symmetryShapes;

    private EditorModel editor;

    private void replace( Manifestation man, RenderedObject renderedObject, Polyhedron shape )
    {
        if ( man instanceof Panel )
            return;
        if (renderedObject != null) {
            AlgebraicMatrix orientation = renderedObject .getOrientation();
            List<AlgebraicVector> vertexList = shape .getVertexList();
            for (Polyhedron.Face face : shape .getFaceSet()) {
                Point[] vertices = new Point[ face .size() ];
                for ( int i = 0; i < vertices.length; i++ ) {
                    int vertexIndex = face .getVertex( i );
                    AlgebraicVector vertex = vertexList .get( vertexIndex );
                    vertices[ i ] = transformVertex( vertex, renderedObject .getLocationAV(), orientation );
                }
                Polygon polygon = new PolygonFromVertices( vertices );
                Manifestation panel = this .manifestConstruction( polygon );
                this .select( panel );
            }
        }
        this .deleteManifestation( man );
    }
    
    @Override
    public void perform() throws Command.Failure
    {
        if ( this .symmetryShapes != null ) {
            // selection-based
            String[] tokens = this .symmetryShapes .split( ":" );
            OrbitSource symmetrySystem = this .editor .getSymmetrySystem( tokens[ 0 ] );
            Shapes shapes = ((SymmetrySystem) symmetrySystem) .getStyle( tokens[1] );
            RenderedModel model = new RenderedModel( symmetrySystem .getSymmetry() .getField(), new OrbitSource() {
                
                @Override
                public Symmetry getSymmetry() {
                    return symmetrySystem .getSymmetry();
                }
                
                @Override
                public Shapes getShapes() {
                    return shapes; // THIS is the reason for the OrbitSource.
                      // We don't want to change the shapes of the current SymmetrySystem,
                      //  if it happens to be the one named.
                }
                
                @Override
                public OrbitSet getOrbits() {
                    return symmetrySystem .getOrbits();
                }
                
                @Override
                public Color getColor( Direction orbit ) {
                    return symmetrySystem .getColor( orbit );
                }
                
                @Override
                public Axis getAxis( AlgebraicVector vector ) {
                    return symmetrySystem .getAxis( vector );
                }

                @Override
                public String getName() {
                    return symmetrySystem .getName();
                }

                @Override
                public Color getVectorColor( AlgebraicVector vector )
                {
                    return symmetrySystem .getVectorColor( vector );
                }
            } );
            if ( this .ballOrStrut != null ) {
                // pick-based
                for (Manifestation man : mSelection) {
                    unselect( man );
                }
                redo();
                RenderedManifestation rm = model .render( this .ballOrStrut );
                this .replace( this .ballOrStrut, rm, rm .getShape() );
            }
            else
                // selection-based
                for (Manifestation man : mSelection) {
                    unselect( man );
                    RenderedManifestation rm = model .render( man );
                    this .replace( man, rm, rm .getShape() );
                }
        } else {
            // legacy pick-based
            for (Manifestation man : mSelection) {
                unselect( man );
            }
            redo();
            this .replace( this .ballOrStrut, this .ballOrStrut .getRenderedObject(), this .shape );
        }
        super .perform();
    }

    private static Point transformVertex( AlgebraicVector vertex, AlgebraicVector offset, AlgebraicMatrix orientation )
    {
        if ( orientation != null )
            vertex = orientation .timesColumn( vertex );
        if ( offset != null )
            vertex = vertex .plus( offset );
        return new FreePoint( vertex );
    }

    public ReplaceWithShape( EditorModel editor )
    {
        super( editor .getSelection(), editor .getRealizedModel() );
        this.editor = editor;
    }

    @Override
    public void configure( Map<String,Object> props )
    {
        Manifestation m = (Manifestation) props .get( "picked" );
        if ( m != null ) {
            // pick-based edit
            this .symmetryShapes = m .getRenderedObject() .getSymmetryShapes();
            this .ballOrStrut = m;
        }
        else
            // selection-based edit
            this .symmetryShapes = (String) props .get( "mode" );
    }

    @Override
    protected void getXmlAttributes( Element element )
    {
        if ( this .ballOrStrut != null ) {
            // pick-based
            Construction construction = this .ballOrStrut .getFirstConstruction();
            if ( construction instanceof Point )
                XmlSaveFormat .serializePoint( element, "point", (Point) construction );
            else
                XmlSaveFormat .serializeSegment( element, "startSegment", "endSegment", (Segment) construction );
        }
        if ( this .shape != null ) {
            // legacy format, which fails to use correct orientations after a symmetry change
            if ( this .vef == null ) {
                this .vef = VefVectorExporter .exportPolyhedron( this .shape );
            }
            Node textNode = element .getOwnerDocument() .createTextNode( XmlSaveFormat .escapeNewlines( this .vef ) );
            element .appendChild( textNode );
        }
        else {
            element .setAttribute( "shapes", this .symmetryShapes );
        }
    }

    @Override
    protected void setXmlAttributes( Element xml, XmlSaveFormat format ) throws Failure
    {
        String attr = xml.getAttribute( "shapes" );
        if (attr != null && !attr.isEmpty()) {
            this .symmetryShapes = attr;
        }
        else {
            // legacy format, which fails to use correct orientations after a symmetry change
            this .vef = xml .getTextContent();
            this .shape = VefToPolyhedron .importPolyhedron( format .getField(), this .vef );
        }
        Construction construction = format .parsePoint( xml, "point" );
        if ( construction == null )
            construction = format .parseSegment( xml, "startSegment", "endSegment" );
        if ( construction != null )
            this .ballOrStrut = this .getManifestation( construction );
    }

    @Override
    protected String getXmlElementName()
    {
        return NAME;
    }
}
