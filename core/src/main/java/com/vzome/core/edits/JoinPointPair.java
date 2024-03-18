package com.vzome.core.edits;

import java.util.Map;

import org.w3c.dom.Element;

import com.vzome.core.commands.Command.Failure;
import com.vzome.core.commands.XmlSaveFormat;
import com.vzome.core.construction.Point;
import com.vzome.core.construction.Segment;
import com.vzome.core.construction.SegmentJoiningPoints;
import com.vzome.core.editor.api.ChangeManifestations;
import com.vzome.core.editor.api.EditorModel;

public class JoinPointPair extends ChangeManifestations
{
    private transient Point start, end;
    
    // Either configure() or setXmlAttributes() will be called before perform().
    public JoinPointPair( EditorModel editor )
    {
        super( editor );
    }
        
    @Override
    public void configure( Map<String,Object> props ) 
    {
        start = (Point) props .get( "start" );
        end = (Point) props .get( "end" );
    }

    @Override
    protected void getXmlAttributes( Element element )
    {	
        XmlSaveFormat .serializePoint( element, "start", start );
        XmlSaveFormat .serializePoint( element, "end", end );
    }

    @Override
    protected void setXmlAttributes(Element xml, XmlSaveFormat format) throws Failure
    {
        start = format .parsePoint( xml, "start" );
        end = format .parsePoint( xml, "end" );
    }

    @Override
    public void perform() throws Failure
    {
        if ( ( start != end ) && !( start .getLocation() .equals( end .getLocation() ) ) ) {
            Segment segment = new SegmentJoiningPoints( start,  end );
           manifestConstruction( segment );
        }
        redo();
    }

    @Override
    protected String getXmlElementName()
    {
        return "JoinPointPair";
    }

}
