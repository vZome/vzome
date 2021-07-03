
package com.vzome.core.edits;

import java.util.Map;

import org.w3c.dom.Element;

import com.vzome.core.algebra.AlgebraicVector;
import com.vzome.core.commands.AttributeMap;
import com.vzome.core.commands.XmlSaveFormat;
import com.vzome.core.construction.FreePoint;
import com.vzome.core.construction.Point;
import com.vzome.core.editor.api.ChangeManifestations;
import com.vzome.core.editor.api.EditorModel;
import com.vzome.core.editor.api.ImplicitSymmetryParameters;

public class ShowPoint extends ChangeManifestations
{
    private Point point;
    private final ImplicitSymmetryParameters parameters;
    
    public ShowPoint( EditorModel editor )
    {
        super( editor );
        this.parameters = (ImplicitSymmetryParameters) editor;
    }
    
    @Override
    public void configure( Map<String,Object> props ) 
    {
        switch ( (String) props .get( "mode" ) ) {

        case "origin":
            AlgebraicVector origin = this.mManifestations .getField() .origin( 3 );
            this.point = new FreePoint( origin );
            break;

        case "symmCenter":
            this.point = this.parameters .getCenterPoint();
            break;
        }
    }

    @Override
    public void perform()
    {
        manifestConstruction( point );
        redo();
    }

    @Override
    public void getXmlAttributes( Element xml )
    {
        XmlSaveFormat .serializePoint( xml, "point", this.point );
    }

    @Override
    public void setXmlAttributes( Element xml, XmlSaveFormat format )
    {
        if ( format .commandEditsCompacted() )
            this.point = format .parsePoint( xml, "point" );
        else
        {
            AttributeMap attrs = format .loadCommandAttributes( xml );
            this.point = (Point) attrs .get( "point" );
        }
    }

    @Override
    protected String getXmlElementName()
    {
        return "ShowPoint";
    }
}
