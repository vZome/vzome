package com.vzome.core.edits;

import java.util.Map;

import org.w3c.dom.Element;

import com.vzome.core.commands.XmlSaveFormat;
import com.vzome.core.construction.Construction;
import com.vzome.core.construction.Segment;
import com.vzome.core.editor.EditorModel;
import com.vzome.core.model.Strut;

public class StrutMove extends StrutCreation
{
    private Strut oldStrut;

    public StrutMove( EditorModel editor )
    {
        super( editor );
    }

    @Override
    public void configure( Map<String, Object> params )
    {
        super .configure( params );
        
        this .oldStrut = (Strut) params .get( "oldStrut" ); 
    }

    @Override
    public void perform()
    {
        this .deleteManifestation( this .oldStrut );
        manifestConstruction( this .mAnchor );

        super .perform();
    }

    @Override
    protected void getXmlAttributes( Element xml )
    {
        XmlSaveFormat .serializeSegment( xml, "startSegment", "endSegment", (Segment) this .oldStrut .getFirstConstruction() );

        super .getXmlAttributes( xml );
    }

    @Override
    public void setXmlAttributes( Element xml, XmlSaveFormat format )
    {
        Construction construction = format .parseSegment( xml, "startSegment", "endSegment" );
        if ( construction != null )
            this .oldStrut = (Strut) this .getManifestation( construction );
        
        super .setXmlAttributes( xml, format );
    }

    @Override
    protected String getXmlElementName()
    {
        return "StrutMove";
    }

}
