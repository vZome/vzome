package com.vzome.core.edits;

import java.util.Map;

import org.w3c.dom.Element;

import com.vzome.core.commands.Command.Failure;
import com.vzome.core.commands.XmlSaveFormat;
import com.vzome.core.construction.Construction;
import com.vzome.core.construction.Point;
import com.vzome.core.construction.Polygon;
import com.vzome.core.construction.Segment;
import com.vzome.core.editor.api.ChangeManifestations;
import com.vzome.core.editor.api.EditorModel;
import com.vzome.core.model.Manifestation;

public class Label extends ChangeManifestations
{
    private Manifestation target;
    private String label;

    public Label( EditorModel editorModel )
    {
        super( editorModel );
    }

    @Override
    public void configure( Map<String,Object> props )
    {
        this .target = (Manifestation) props .get( "picked" );
        this .label = (String) props .get( "text" );
    }

    @Override
    public void perform() throws Failure
    {
        this .labelManifestation( this.target, this.label );
        super .perform();
    }

    @Override
    protected String getXmlElementName()
    {
        return "Label";
    }

    @Override
    protected void getXmlAttributes( Element element )
    {
        Construction construction = this .target .getFirstConstruction();
        if ( construction instanceof Point )
            XmlSaveFormat .serializePoint( element, "point", (Point) construction );
        else if ( construction instanceof Segment )
            XmlSaveFormat .serializeSegment( element, "startSegment", "endSegment", (Segment) construction );
        else if ( construction instanceof Polygon )
            XmlSaveFormat .serializePolygon( element, "polygonVertex", (Polygon) construction );
        element .setAttribute( "text", this .label );
    }

    @Override
    protected void setXmlAttributes( Element xml, XmlSaveFormat format ) throws Failure
    {
        this .label = xml.getAttribute( "text" );
        Construction construction = format .parsePoint( xml, "point" );
        if ( construction == null )
            construction = format .parseSegment( xml, "startSegment", "endSegment" );
        if ( construction == null )
            construction = format .parsePolygon( xml, "polygonVertex" );
        if ( construction != null )
            this .target = this .getManifestation( construction );
    }
}
