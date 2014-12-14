
//(c) Copyright 2007, Scott Vorthmann.  All rights reserved.

package com.vzome.core.editor;

import java.util.Iterator;

import org.w3c.dom.Element;

import com.vzome.core.commands.XmlSaveFormat;
import com.vzome.core.commands.Command.Failure;
import com.vzome.core.model.Manifestation;
import com.vzome.core.model.RealizedModel;
import com.vzome.core.render.Color;
import com.vzome.core.render.Colors;
import com.vzome.core.render.RenderedManifestation;

public class ColorManifestations extends ChangeManifestations
{
    private Color color;
    
    public ColorManifestations( Selection selection, RealizedModel realized, Color color, boolean groupInSelection )
    {
        super( selection, realized, groupInSelection );

        if ( color != null )
            initialize( color );
    }
    
    private void initialize( Color color )
    {
        this.color = color;
        for ( Iterator all = mSelection .iterator(); all .hasNext(); ) {
            Manifestation m = (Manifestation) all .next();
            RenderedManifestation rm = (RenderedManifestation) m .getRenderedObject();
            if ( rm != null )
                plan( new ColorManifestation( m, color ) );
            unselect( m, true );
        }
    }

    public void getXmlAttributes( Element result )
    {
        result .setAttribute( "red", "" + color .getRed() );
        result .setAttribute( "green", "" + color .getGreen() );
        result .setAttribute( "blue", "" + color .getBlue() );
    }

    public void setXmlAttributes( Element xml, XmlSaveFormat format ) throws Failure
    {
        if ( format .loadToRender() )
        {
            String red = xml .getAttribute( "red" );
            String green = xml .getAttribute( "green" );
            String blue = xml .getAttribute( "blue" );
            initialize( new Color( Integer .parseInt( red ), Integer .parseInt( green ), Integer .parseInt( blue ) ) );
        }
        else
            initialize( null );
    }

    
    private class ColorManifestation implements SideEffect
    {
        private final Manifestation mManifestation;

        private final String oldColorName, newColorName;
        
        public ColorManifestation( Manifestation manifestation, Color color )
        {
            mManifestation = manifestation;
            newColorName = Colors.RGB_CUSTOM + " " + color.getRed() + " " + color.getGreen() + " " + color.getBlue();
            RenderedManifestation rm = (RenderedManifestation) manifestation .getRenderedObject();
            if ( rm != null )
                oldColorName = rm .getColorName();
            else
                oldColorName = Colors.RGB_CUSTOM + " 25 25 25"; // TODO fix this case
        }

        public void redo()
        {
            mManifestations .setColorName( mManifestation, newColorName );
        }

        public void undo()
        {
            mManifestations .setColorName( mManifestation, oldColorName );
        }
    }


    protected String getXmlElementName()
    {
        return "setItemColor";
    }
}
