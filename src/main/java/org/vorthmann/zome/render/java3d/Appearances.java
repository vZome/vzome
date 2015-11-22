package org.vorthmann.zome.render.java3d;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.media.j3d.Appearance;
import javax.media.j3d.ColoringAttributes;
import javax.media.j3d.Material;
import javax.media.j3d.PolygonAttributes;
import javax.media.j3d.TransparencyAttributes;
import javax.vecmath.Color3f;

import com.vzome.core.render.Color;
import com.vzome.core.render.Colors;

public class Appearances extends Object implements Colors.Changes{

	static final  float PREVIEW_TRANSPARENCY = 0.45f;

	Color3f mGlowColor, mPanelColor;

	static final Color3f WHITE = new Color3f( 0.85f, 0.85f, 0.85f );

	static final Color3f BLACK = new Color3f( 0f, 0f, 0f );

	static final Color3f GREY = new Color3f( 0.3f, 0.3f, 0.3f );

	protected final Colors mColors;
	
	protected final boolean mHasEmissiveColor;

	private Map mAppearances = new HashMap();

	public Appearances( Colors colors, boolean hasEmissiveColor )
	{
		mColors = colors;
		mHasEmissiveColor = hasEmissiveColor;
		Color color = mColors .getColor( Colors .HIGHLIGHT );
		if ( ! hasEmissiveColor )
		    color = mColors .getColor( Colors .HIGHLIGHT_MAC );
		float[] rgb = new float[3];
		mGlowColor = new Color3f( color .getRGBColorComponents( rgb ) );

		for ( Iterator it = mColors .getColorNames(); it .hasNext(); ) {
		    String name = (String) it .next();
		    color = mColors .getColor( name );
		    Appearance[][] set = makeAppearances( color );
		    mAppearances .put( name, set );
		}
        colors .addListener( this );
	}


	 
	private Material makeMaterial( Color3f color, boolean glowing )
	{
	    Material material;
	    // Material constructor: ambient, emissive, diffuse, specular, shininess
		if ( mHasEmissiveColor )
		    material = new Material( color, glowing? mGlowColor : BLACK, color, GREY, 100f );
		else
		    material = new Material( glowing? mGlowColor : color, BLACK, glowing? mGlowColor : color, GREY, 100f );
		material .setCapability( Material .ALLOW_COMPONENT_READ );
		return material;
	}

	 
	private Appearance makeAppearance( Material material, Color3f jColor, boolean transparent )
	{
		Appearance appearance = new Appearance();
		appearance .setMaterial( material );
		appearance .setCapability( Appearance .ALLOW_MATERIAL_READ );
		appearance .setCapability( Appearance .ALLOW_MATERIAL_WRITE );
		material .setLightingEnable( true );
		appearance .setColoringAttributes( new ColoringAttributes( jColor, ColoringAttributes .SHADE_FLAT ) );
		if ( transparent ) {
			TransparencyAttributes ta = new TransparencyAttributes();
			ta .setTransparencyMode( TransparencyAttributes .BLENDED );
			ta .setTransparency( PREVIEW_TRANSPARENCY );
			appearance .setTransparencyAttributes( ta );
		}		
		return appearance;
	}
	
	private Appearance[][] makeAppearances( Color color )
	{
		float[] rgb = new float[3];
		Color3f jColor = new Color3f( color .getRGBColorComponents( rgb ) );
	    Appearance[][] set = new Appearance[2][2];
		for ( int glow = 0; glow < 2; glow++ ) {
			Material material = makeMaterial( jColor, glow == 1 );
			for ( int transp = 0; transp < 2; transp++ )
			    set [ glow ] [ transp ] = makeAppearance( material, jColor, transp == 1 );
		}
		return set;
	}

	public Appearance getAppearance( String colorName, boolean glowing, boolean transparent )
	{
		Appearance[][] set = ((Appearance[][]) mAppearances.get( colorName ));
		if ( set == null )
        {
            Color newColor = mColors .getColor( colorName );
            if ( newColor == null )
                newColor = Color .WHITE;
		    // an automatic direction (the old way, all white)
		    set = makeAppearances( newColor );
		    mAppearances .put( colorName, set );
		}
		return set [ glowing? 1:0 ][ transparent?1:0 ];
	}


    public void colorChanged( String name, Color newColor )
    {
		float[] rgb = new float[3];
		Color3f jColor = new Color3f( newColor .getRGBColorComponents( rgb ) );
        if ( ( mHasEmissiveColor && name .equals( Colors.HIGHLIGHT ) ) 
        || name .equals( Colors .HIGHLIGHT_MAC ) ) {
            // simply make new Materials for all the glowing appearances
            mGlowColor = jColor;
            for ( Iterator sets = mAppearances.values() .iterator(); sets .hasNext(); ) {
                Appearance[][] apps = (Appearance[][]) sets .next();
                for ( int t = 0; t < 2; t++ ) {
                    jColor = new Color3f();
                    apps[1][t] .getMaterial() .getAmbientColor( jColor );
                    Material mat = makeMaterial( jColor, true );
                    apps[1][t] .setMaterial( mat );
                }
            }
        }
        else {
            // make new Materials for all appearances for this key
            Appearance[][] apps = (Appearance[][]) mAppearances .get( name );
            if ( apps == null ) {
                // an automatic direction (the new way, a new color for each new auto direction)
                apps = makeAppearances( newColor );
                mAppearances .put( name, apps );
            }
            for ( int g = 0; g < 2; g++ ) {
                Material mat = makeMaterial( jColor, g == 1);
                for ( int t = 0; t < 2; t++ )
                    apps[g][t] .setMaterial( mat );
            }
        }
    }



    Colors getColors()
    {
        return mColors;
    }



    public void colorAdded( String name, Color color )
    {
        colorChanged( name, color );
    }
	
}


