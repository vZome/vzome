
//(c) Copyright 2005, Scott Vorthmann.  All rights reserved.

package com.vzome.core.editor;

import java.util.List;
import java.util.Properties;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.vzome.core.algebra.AlgebraicMatrix;
import com.vzome.core.algebra.AlgebraicVector;
import com.vzome.core.commands.Command;
import com.vzome.core.commands.Command.Failure;
import com.vzome.core.commands.XmlSaveFormat;
import com.vzome.core.construction.Construction;
import com.vzome.core.construction.FreePoint;
import com.vzome.core.construction.Point;
import com.vzome.core.construction.Polygon;
import com.vzome.core.construction.PolygonFromVertices;
import com.vzome.core.construction.Segment;
import com.vzome.core.math.Polyhedron;
import com.vzome.core.math.VefToPolyhedron;
import com.vzome.core.model.Manifestation;
import com.vzome.core.model.VefModelExporter;
import com.vzome.core.render.RenderedManifestation;

public class ReplaceWithShape extends ChangeManifestations
{
    public static final String NAME = "ReplaceWithShape";

    private String vef;

    private Polyhedron shape;

    private Manifestation ballOrStrut;

    @Override
    public void perform() throws Command.Failure
    {
        for (Manifestation man : mSelection) {
            unselect( man );
        }
        redo();

        RenderedManifestation rm = this .ballOrStrut .getRenderedObject();
        if (rm != null) {
            AlgebraicMatrix orientation = rm .getOrientation();
            List<AlgebraicVector> vertexList = this .shape .getVertexList();
            for (Polyhedron.Face face : this .shape .getFaceSet()) {
                Point[] vertices = new Point[ face .size() ];
                for ( int i = 0; i < vertices.length; i++ ) {
                    int vertexIndex = face .getVertex( i );
                    AlgebraicVector vertex = vertexList .get( vertexIndex );
                    vertices[ i ] = transformVertex( vertex, rm .getLocationAV(), orientation );
                }
                Polygon polygon = new PolygonFromVertices( vertices );
                Manifestation panel = manifestConstruction( polygon );
                select( panel );
            }
        }

        this .deleteManifestation( this .ballOrStrut );

        super .perform();
    }

    private Point transformVertex( AlgebraicVector vertex, AlgebraicVector offset, AlgebraicMatrix orientation )
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
    }

    @Override
    public void configure( Properties props )
    {
        this .ballOrStrut = (Manifestation) props .get( "picked" );
        if ( this .ballOrStrut != null ) // first creation from the editor
            this .shape = ballOrStrut .getRenderedObject() .getShape();
    }

    @Override
    protected void getXmlAttributes( Element element )
    {
        Construction construction = this .ballOrStrut .getConstructions() .next();
        if ( construction instanceof Point )
            XmlSaveFormat .serializePoint( element, "point", (Point) construction );
        else
            XmlSaveFormat .serializeSegment( element, "startSegment", "endSegment", (Segment) construction );
        if ( this .vef == null ) {
            this .vef = VefModelExporter .exportPolyhedron( this .shape );
        }
        Node textNode = element .getOwnerDocument() .createTextNode( XmlSaveFormat .escapeNewlines( this .vef ) );
        element .appendChild( textNode );
    }

    @Override
    protected void setXmlAttributes( Element xml, XmlSaveFormat format ) throws Failure
    {
        Construction construction = format .parsePoint( xml, "point" );
        if ( construction == null )
            construction = format .parseSegment( xml, "startSegment", "endSegment" );
        this .ballOrStrut = this .getManifestation( construction );
        this .vef = xml .getTextContent();
        this .shape = VefToPolyhedron .importPolyhedron( format .getField(), this .vef );
    }

    @Override
    protected String getXmlElementName()
    {
        return NAME;
    }
}
