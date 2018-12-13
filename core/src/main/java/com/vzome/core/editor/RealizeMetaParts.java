
//(c) Copyright 2005, Scott Vorthmann.  All rights reserved.

package com.vzome.core.editor;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.vzome.core.algebra.AlgebraicField;
import com.vzome.core.algebra.AlgebraicMatrix;
import com.vzome.core.algebra.AlgebraicNumber;
import com.vzome.core.algebra.AlgebraicVector;
import com.vzome.core.commands.Command;
import com.vzome.core.commands.Command.Failure;
import com.vzome.core.commands.XmlSaveFormat;
import com.vzome.core.construction.FreePoint;
import com.vzome.core.construction.Point;
import com.vzome.core.construction.Polygon;
import com.vzome.core.construction.PolygonFromVertices;
import com.vzome.core.math.Polyhedron;
import com.vzome.core.model.Exporter;
import com.vzome.core.model.Manifestation;
import com.vzome.core.model.RealizedModel;
import com.vzome.core.model.VefModelExporter;
import com.vzome.core.render.RenderedManifestation;

public class RealizeMetaParts extends ChangeManifestations
{
    public static final String NAME = "realizeMetaParts";
    
    private final boolean replace;
    
    private String vef;

    @Override
    public void perform() throws Command.Failure
    {
        StringWriter out = new StringWriter();
        Exporter exporter = new VefModelExporter( out, this .mManifestations .getField() );

        AlgebraicNumber scale = null;
        ArrayList<Manifestation> inputs = new ArrayList<>();
        for (Manifestation man : mSelection) {
            inputs .add( man );
            unselect( man );
            RenderedManifestation rm = man .getRenderedObject();
            if (rm != null) {
                Polyhedron shape = rm .getShape();
                if ( scale == null ) {
                    // wait until we know the field
                    AlgebraicField field = shape .getField();
                    scale = field .createPower( 5 ); // .times( field .createRational( 2 ));
                }
                AlgebraicMatrix orientation = rm .getOrientation();
                List<AlgebraicVector> vertexList = shape .getVertexList();
                if ( ! replace )
                    for (AlgebraicVector vertex : shape .getVertexList()) {
                        Point vertexPt = transformVertex( vertex, man .getLocation(), scale, orientation );
                        Manifestation ball = manifestConstruction( vertexPt );
                        select( ball );
                        exporter .exportManifestation( ball );
                    }
                for (Polyhedron.Face face : shape .getFaceSet()) {
                    Point[] vertices = new Point[ face .size() ];
                    for ( int i = 0; i < vertices.length; i++ ) {
                        int vertexIndex = face .getVertex( i );
                        AlgebraicVector vertex = vertexList .get( vertexIndex );
                        vertices[ i ] = transformVertex( vertex, man .getLocation(), scale, orientation );
                    }
                    Polygon polygon = new PolygonFromVertices( vertices );
                    Manifestation panel = manifestConstruction( polygon );
                    select( panel );
                    exporter .exportManifestation( panel );
                }
            }
        }
        exporter .finish();
        this .vef = out .toString();

        redo();

        if ( replace )
            for ( Manifestation man : inputs )
            {
                this .deleteManifestation( man );
            }
        super .perform();
    }

    private Point transformVertex( AlgebraicVector vertex, AlgebraicVector offset, AlgebraicNumber scale, AlgebraicMatrix orientation )
    {
        if ( orientation != null )
            vertex = orientation .timesColumn( vertex );
        if ( offset != null )
            vertex = vertex .plus( offset );
        // no scaling for replace mode, where a single strut/ball is replaced with equivalent panels
        return this .replace? new FreePoint( vertex ): new FreePoint( vertex .scale( scale ) );
    }

    public RealizeMetaParts( Selection selection, RealizedModel realized )
    {
        this( selection, realized, false );
    }

    public RealizeMetaParts( Selection selection, RealizedModel realized, boolean replace )
    {
        super( selection, realized, false );
        this .replace = replace;
    }

    @Override
    protected void getXmlAttributes( Element element )
    {
        Node textNode = element .getOwnerDocument() .createTextNode( XmlSaveFormat .escapeNewlines( this .vef ) );
        element .appendChild( textNode );
    }

    @Override
    protected void setXmlAttributes( Element xml, XmlSaveFormat format ) throws Failure
    {
        this .vef = xml .getTextContent();
    }

    @Override
    protected String getXmlElementName()
    {
        return NAME;
    }
}
