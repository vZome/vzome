
//(c) Copyright 2011, Scott Vorthmann.

package com.vzome.core.editor;


import org.w3c.dom.Element;

import com.vzome.core.commands.XmlSaveFormat;
import com.vzome.core.commands.Command.Failure;
import com.vzome.core.model.Connector;
import com.vzome.core.model.Manifestation;

public class DeselectByClass extends ChangeSelection
{
    private boolean balls;

	public DeselectByClass( Selection selection, boolean balls )
    {
        super( selection, false );
        this .balls = balls;
    }
    
    public void perform()
    {
        for (Manifestation man : mSelection) {
            if ( balls )
            {
                if ( man instanceof Connector )
                    unselect( man, true );
            }
            else
            {
                if ( ! ( man instanceof Connector ) )
                    unselect( man, true );
            }
        }
        redo();
    }

    protected void getXmlAttributes( Element element )
    {
        element .setAttribute( "class", balls? "balls" : "struts" );
    }

    protected void setXmlAttributes( Element xml, XmlSaveFormat format ) throws Failure
    {
        String attr = xml .getAttribute( "class" );
        balls = attr .equals( "balls" );
    }

    protected String getXmlElementName()
    {
        return "DeselectByClass";
    }

}
