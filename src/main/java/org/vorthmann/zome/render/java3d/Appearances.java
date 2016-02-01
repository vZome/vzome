package org.vorthmann.zome.render.java3d;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.media.j3d.Appearance;
import javax.media.j3d.ColoringAttributes;
import javax.media.j3d.Material;
import javax.media.j3d.TransparencyAttributes;
import javax.vecmath.Color3f;
import javax.vecmath.Color4f;

import com.vzome.core.render.Color;
import com.vzome.core.render.Colors;

public class Appearances
{
	static final  float PREVIEW_TRANSPARENCY = 0.45f;

	Color3f mGlowColor, mPanelColor;

	static final Color3f WHITE = new Color3f( 0.85f, 0.85f, 0.85f );

	static final Color3f BLACK = new Color3f( 0f, 0f, 0f );

	static final Color3f GREY = new Color3f( 0.3f, 0.3f, 0.3f );

	protected final Colors mColors;
	
	protected final boolean mHasEmissiveColor;

	private Map<Color, Appearance[][]> mAppearances = new HashMap<>();

	public Appearances( Colors colors, boolean hasEmissiveColor )
	{
		mColors = colors;
		mHasEmissiveColor = hasEmissiveColor;
		Color color = mColors .getColor( Colors .HIGHLIGHT );
		if ( ! hasEmissiveColor )
		    color = mColors .getColor( Colors .HIGHLIGHT_MAC );
		float[] rgb = new float[3];
		mGlowColor = new Color3f( color .getRGBColorComponents( rgb ) );

        for (String name : mColors) {
            color = mColors .getColor( name );
            Appearance[][] set = makeAppearances( color );
            mAppearances .put( color, set );
        }
	}
	 
	private Material makeMaterial( Color4f color, boolean glowing )
	{
	    Material material;
	    Color3f justColor = new Color3f( color .x, color.y, color.z );
	    // Material constructor: ambient, emissive, diffuse, specular, shininess
		if ( mHasEmissiveColor )
		    material = new Material( justColor, glowing? mGlowColor : BLACK, justColor, GREY, 100f );
		else
		    material = new Material( glowing? mGlowColor : justColor, BLACK, glowing? mGlowColor : justColor, GREY, 100f );
		material .setCapability( Material .ALLOW_COMPONENT_READ );
		return material;
	}

	 
	private Appearance makeAppearance( Material material, Color4f color, boolean transparent )
	{
		Appearance appearance = new Appearance();
		appearance .setMaterial( material );
		appearance .setCapability( Appearance .ALLOW_MATERIAL_READ );
		appearance .setCapability( Appearance .ALLOW_MATERIAL_WRITE );
		material .setLightingEnable( true );
	    Color3f justColor = new Color3f( color .x, color.y, color.z );
		appearance .setColoringAttributes( new ColoringAttributes( justColor, ColoringAttributes .SHADE_FLAT ) );
		if ( transparent || color.w < 1.0f ) {
			TransparencyAttributes ta = new TransparencyAttributes();
			ta .setTransparencyMode( TransparencyAttributes .BLENDED );
			float alpha = transparent? ( PREVIEW_TRANSPARENCY * color.w ) : color.w;
			ta .setTransparency( PREVIEW_TRANSPARENCY );
			appearance .setTransparencyAttributes( ta );
		}		
		return appearance;
	}
	
	private Appearance[][] makeAppearances( Color color )
	{
		float[] rgba = new float[4];
		Color4f jColor = new Color4f( color .getRGBColorComponents( rgba ) );
	    Appearance[][] set = new Appearance[2][2];
		for ( int glow = 0; glow < 2; glow++ ) {
			Material material = makeMaterial( jColor, glow == 1 );
			for ( int transp = 0; transp < 2; transp++ )
			    set [ glow ] [ transp ] = makeAppearance( material, jColor, transp == 1 );
		}
		return set;
	}

	public Appearance getAppearance( Color color, boolean glowing, boolean transparent )
	{
        if ( color == null )
        	color = Color .WHITE;
		Appearance[][] set = mAppearances.get( color );
		if ( set == null )
        {
		    set = makeAppearances( color );
		    mAppearances .put( color, set );
		}
		return set [ glowing? 1:0 ][ transparent?1:0 ];
	}

    Colors getColors()
    {
        return mColors;
    }
}


