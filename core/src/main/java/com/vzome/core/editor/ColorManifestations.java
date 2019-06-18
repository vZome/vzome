
//(c) Copyright 2007, Scott Vorthmann.  All rights reserved.

package com.vzome.core.editor;


import java.util.Map;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.vzome.core.commands.Command.Failure;
import com.vzome.core.commands.XmlSaveFormat;
import com.vzome.core.math.DomUtils;
import com.vzome.core.model.Manifestation;
import com.vzome.core.model.RealizedModel;
import com.vzome.core.render.Color;
import com.vzome.core.render.RenderedManifestation;

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
            RenderedManifestation rm = m .getRenderedObject();
            if ( rm != null )
                plan( new ColorManifestation( m, color ) );
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

    
    private class ColorManifestation implements SideEffect
    {
        private final Manifestation mManifestation;

        private final Color oldColor, newColor;
        
        public ColorManifestation( Manifestation manifestation, Color color )
        {
            mManifestation = manifestation;
            this .newColor = color;
            RenderedManifestation rm = manifestation .getRenderedObject();
            if ( rm != null ) {
            	oldColor = rm .getColor();
            }
            else
            	oldColor = Color .GREY_TRANSPARENT; // TODO fix this case
        }

        @Override
        public void redo()
        {
            mManifestations .setColor( mManifestation, newColor );
        }

        @Override
        public void undo()
        {
            mManifestations .setColor( mManifestation, oldColor );
        }

        @Override
        public Element getXml( Document doc )
        {
            Element result = doc .createElement( "color" );
            DomUtils .addAttribute( result, "rgb", newColor .toString() );
            Element man = mManifestation .getXml( doc );
            result .appendChild( man );
            return result;
        }
    }


    @Override
    protected String getXmlElementName()
    {
        return "setItemColor";
    }
}
