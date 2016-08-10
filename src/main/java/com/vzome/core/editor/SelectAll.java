
//(c) Copyright 2005, Scott Vorthmann.  All rights reserved.

package com.vzome.core.editor;


import com.vzome.core.model.Manifestation;
import com.vzome.core.model.RealizedModel;

public class SelectAll extends ChangeSelection
{
    public SelectAll( Selection selection, RealizedModel model, boolean groupInSelection )
    {
        super( selection, groupInSelection );
        selection .selectAll( model );
    }

    @Override
    protected String getXmlElementName()
    {
        return "SelectAll";
    }

}
