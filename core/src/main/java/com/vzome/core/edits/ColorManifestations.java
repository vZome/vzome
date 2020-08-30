
//(c) Copyright 2007, Scott Vorthmann.  All rights reserved.

package com.vzome.core.edits;


import java.util.Map;

import org.w3c.dom.Element;

import com.vzome.core.commands.Command.Failure;
import com.vzome.core.construction.Color;
import com.vzome.core.commands.XmlSaveFormat;
import com.vzome.core.editor.ChangeManifestations;
import com.vzome.core.editor.Selection;
import com.vzome.core.model.Manifestation;
import com.vzome.core.model.RealizedModel;

public class ColorManifestations extends ChangeManifestations
{
    private Color color;
    
    public ColorManifestations( Selection selection, RealizedModel realized )
    {
        super( selection, realized );
    }
    
    @Override
    public void configure( Map<String,Object> props ) 
    {
        String mode = (String) props .get( "mode" );
        if ( mode != null )
            initialize( new Color( mode ) );
    }

    private void initialize( Color color )
    {
        this.color = color;

        // TODO: this behavior should move to perform()
        for (Manifestation m : mSelection) {
            if ( m .isRendered() )
                // This test is probably unnecessary now that we store color in Manifestation,
                //  but I don't want to risk a subtle bug by removing the test.
                colorManifestation( m, color );
            unselect( m, true );
        }
    }

    @Override
    public void getXmlAttributes( Element result )
    {
        result .setAttribute( "red", "" + color .getRed() );
        result .setAttribute( "green", "" + color .getGreen() );
        result .setAttribute( "blue", "" + color .getBlue() );
        int alpha = color .getAlpha();
        if ( alpha < 0xFF )
            result .setAttribute( "alpha", "" + alpha );
    }

    @Override
    public void setXmlAttributes( Element xml, XmlSaveFormat format ) throws Failure
    {
        if ( format .loadToRender() )
        {
            String red = xml .getAttribute( "red" );
            String green = xml .getAttribute( "green" );
            String blue = xml .getAttribute( "blue" );
            String alphaStr = xml .getAttribute( "alpha" );
            int alpha = ( alphaStr==null || alphaStr .isEmpty() )? 0xff : Integer .parseInt( alphaStr );
            initialize( new Color( Integer .parseInt( red ), Integer .parseInt( green ), Integer .parseInt( blue ), alpha ) );
        }
        else
            initialize( null );
    }

    @Override
    protected String getXmlElementName()
    {
        return "setItemColor";
    }
}
