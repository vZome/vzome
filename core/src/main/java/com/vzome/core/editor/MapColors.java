package com.vzome.core.editor;

import com.vzome.core.commands.Command;
import com.vzome.core.commands.Command.Failure;
import com.vzome.core.commands.XmlSaveFormat;
import com.vzome.core.editor.ManifestationColorMappers.ManifestationColorMapper;
import static com.vzome.core.math.DomUtils.addAttribute;
import com.vzome.core.model.Manifestation;
import com.vzome.core.model.RealizedModel;
import com.vzome.core.render.Color;
import com.vzome.core.render.RenderedManifestation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * @author David Hall
 */
public class MapColors extends ChangeManifestations {

    private final ManifestationColorMapper colorMapper;

    public MapColors( Selection selection, RealizedModel realized, ManifestationColorMapper colorMapper )
    {
        super( selection, realized );
        this.colorMapper = colorMapper;
    }

    @Override
	public void perform() throws Failure {
        if( colorMapper .requiresOrderedSelection() ) {
            setOrderedSelection( true );
        }
        colorMapper.initialize( getRenderedSelection() );
        for( Manifestation man : getRenderedSelection() ) {
            plan( new ColorMapManifestation( man, colorMapper.apply(man) ) );
            unselect( man, true );
        }
        redo();
    }

    private static final String COLORMAPPER_ATTR_NAME = "colorMapper";

    @Override
    public void getXmlAttributes( Element result ) {
        result .setAttribute( COLORMAPPER_ATTR_NAME, colorMapper.getName() );
        colorMapper.getXmlAttributes(result);
    }

    @Override
    public void setXmlAttributes( Element xml, XmlSaveFormat format ) throws Command.Failure
    {
        String colorMapperName = xml .getAttribute( COLORMAPPER_ATTR_NAME );
        if( !colorMapper.getName().equals(colorMapperName) ) {
            logger.warning("Substituting " + colorMapper.getName() + " for specifed " + COLORMAPPER_ATTR_NAME + ": " + colorMapperName);
        }
        colorMapper.setXmlAttributes(xml);
    }

    @Override
    protected String getXmlElementName() {
        return "MapToColor";
    }

    private class ColorMapManifestation implements SideEffect
    {
        private final Manifestation mManifestation;

        private final Color oldColor, newColor;

        public ColorMapManifestation( Manifestation manifestation, Color color )
        {
            mManifestation = manifestation;
            this .newColor = color;
            RenderedManifestation rm = manifestation .getRenderedObject();
            if ( rm != null ) {
            	oldColor = rm .getColor();
            }
            else
            	oldColor = Color .GREY_TRANSPARENT; // TODO: Be sure this can't happen then remove this line
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
            addAttribute( result, "rgb", newColor .toString() );
            Element man = mManifestation .getXml( doc );
            result .appendChild( man );
            return result;
        }
    }

}
