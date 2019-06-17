
//(c) Copyright 2007, Scott Vorthmann.  All rights reserved.

package com.vzome.core.editor;

import com.vzome.core.commands.AttributeMap;

import org.w3c.dom.Element;

import com.vzome.core.commands.XmlSaveFormat;
import com.vzome.core.construction.Point;
import com.vzome.core.model.RealizedModel;

public class ShowPoint extends ChangeManifestations
{
    private Point point;
    
    public ShowPoint( Point point, Selection selection, RealizedModel realized )
    {
        super( selection, realized );
        this.point = point;
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
