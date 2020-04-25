package com.vzome.core.edits;

import static com.vzome.xml.DomUtils.addAttribute;

import java.util.Map;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.vzome.core.commands.Command;
import com.vzome.core.commands.Command.Failure;
import com.vzome.core.editor.ChangeManifestations;
import com.vzome.core.editor.EditorModel;
import com.vzome.core.editor.SideEffects;
import com.vzome.core.editor.SymmetrySystem;
import com.vzome.core.editor.SideEffects.SideEffect;
import com.vzome.core.edits.ManifestationColorMappers.ManifestationColorMapper;
import com.vzome.core.mesh.Color;
import com.vzome.core.commands.XmlSaveFormat;
import com.vzome.core.model.Manifestation;
import com.vzome.core.render.RenderedManifestation;

/**
 * @author David Hall
 */
public class MapToColor extends ChangeManifestations {

    private ManifestationColorMapper colorMapper;
    private final EditorModel editor;

    public MapToColor( EditorModel editor )
    {
        super( editor .getSelection(), editor .getRealizedModel() );
        this.editor = editor;
    }
    
    @Override
    public void configure( Map<String,Object> props ) 
    {
        String colorMapperName = (String) props .get( "mode" );
        SymmetrySystem symmetry = this.editor .getSymmetrySystem();
        if ( colorMapperName != null )
            this .colorMapper = ManifestationColorMappers .getColorMapper( colorMapperName, symmetry );
    }

    /**
     * Either configure() or setXmlAttributes() is always called before perform()
     */
    @Override
	public void perform() throws Failure
    {
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
    public void getXmlAttributes( Element result )
    {
        result .setAttribute( COLORMAPPER_ATTR_NAME, colorMapper.getName() );
        colorMapper.getXmlAttributes(result);
    }

    @Override
    public void setXmlAttributes( Element xml, XmlSaveFormat format ) throws Command.Failure
    {
        SymmetrySystem symmetry = this .editor .getSymmetrySystem( xml .getAttribute( "symmetry" ) );
        String colorMapperName = xml .getAttribute( COLORMAPPER_ATTR_NAME );
        this .colorMapper = ManifestationColorMappers .getColorMapper( colorMapperName, symmetry );

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
