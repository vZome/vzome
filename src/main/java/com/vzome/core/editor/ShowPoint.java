
//(c) Copyright 2007, Scott Vorthmann.  All rights reserved.

package com.vzome.core.editor;

import java.util.Map;

import org.w3c.dom.Element;

import com.vzome.core.commands.XmlSaveFormat;
import com.vzome.core.construction.Point;
import com.vzome.core.model.RealizedModel;

public class ShowPoint extends ChangeConstructions
{
    private Point point;
    
    public ShowPoint( Point point, Selection selection, RealizedModel realized, boolean groupInSelection )
    {
        super( selection, realized, groupInSelection );
        this.point = point;
    }
    
    public void perform()
    {
        manifestConstruction( point );
        redo();
    }

    public void getXmlAttributes( Element xml )
    {
        XmlSaveFormat .serializePoint( xml, "point", this.point );
    }

    public void setXmlAttributes( Element xml, XmlSaveFormat format )
    {
        if ( format .commandEditsCompacted() )
            this.point = format .parsePoint( xml, "point" );
        else
        {
            Map attrs = format .loadCommandAttributes( xml, true );
            this.point = (Point) attrs .get( "point" );
        }
    }

    protected String getXmlElementName()
    {
        return "ShowPoint";
    }
}
