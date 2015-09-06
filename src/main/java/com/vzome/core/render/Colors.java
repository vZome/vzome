

package com.vzome.core.render;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.TreeMap;


public class Colors
{
    public Colors( Properties props )
    {
        super();
        this .properties = props;
    }

    /**
     * @author Scott Vorthmann
     *
     */
    public interface Changes
    {
        void colorAdded( String name, Color color );
        
        void colorChanged( String name, Color newColor );
    }
    
    public static final String
        RGB_CUSTOM = "rgb.custom",
        RGB_ORBIT = "rgb.orbit",
        PREFIX = "",
    	BACKGROUND = PREFIX + "background",
    	PANEL = PREFIX + "panel",
    	HIGHLIGHT = PREFIX + "highlight",
    	HIGHLIGHT_MAC = HIGHLIGHT + ".mac",
    	CONNECTOR = PREFIX + "connector",
		DIRECTION = PREFIX + "direction.",
        PLANE = DIRECTION + "plane.";
    
    private final Map mColors = new TreeMap();
    
    private final List mListeners = new ArrayList();
    
    private final Properties properties;

    public void addColor( String name, Color color )
    {
        mColors .put( name, color );
        for ( Iterator it = mListeners .iterator(); it .hasNext(); )
            ((Changes) it .next()) .colorAdded( name, color );
    }

    public void setColor( String name, Color color )
    {
        mColors .put( name, color );
        for ( Iterator it = mListeners .iterator(); it .hasNext(); )
            ((Changes) it .next()) .colorChanged( name, color );
    }
	
	public void addListener( Changes changes )
	{
        mListeners .add( changes );
	}
	
	public void removeListener( Changes changes )
	{
        mListeners .remove( changes );
	}
    
    private int mNextNewColor = 0xC95;  // picked to put bits in each of RGB
    
    private final int STEP = 73; // picked to be a prime

    private static final float[] NO_VECTOR = new float[] { 0f, 0f, 0f };

    public float[] getVectorPref( String name )
    {
        float[] result = NO_VECTOR;
        String pref = properties .getProperty( name );
        if ( pref == null || pref.equals( "" ) )
            return result;
        result = (float[]) result.clone();
        StringTokenizer tokens = new StringTokenizer( pref, ", " );
        int i = 0;
        while ( tokens.hasMoreTokens() )
            result[i++] = Float.parseFloat( tokens.nextToken() );
        return result;
    }

    public Color getColorPref( String name )
    {
        float[] percents = getVectorPref( "color.percent." + name );
        if ( percents != NO_VECTOR ) {
            return new Color( Math.round( percents[0] * 0xFF / 100 ), Math
                    .round( percents[1] * 0xFF / 100 ), Math
                    .round( percents[2] * 0xFF / 100 ) );
        }
        String pref = properties .getProperty( "color." + name );
        return parseColor( pref );
    }
    
    public static Color parseColor( String colorString )
    {
        if ( colorString == null || colorString.equals( "" ) )
            return Color.WHITE;
        StringTokenizer tokens = new StringTokenizer( colorString, ", " );
        int[] rgb = new int[] { 0, 0, 0 };
        int i = 0;
        while ( tokens.hasMoreTokens() )
            rgb[i++] = Integer.parseInt( tokens.nextToken() );
        return new Color( rgb[0], rgb[1], rgb[2] );
    }
    
    public static String getColorName( Color color )
    {
        return RGB_ORBIT + " " + color.red + " " + color.green + " " + color.blue;
    }

    public Color getColor( String name )
    {
        Color color = (Color) mColors .get( name );
        if ( color == null )
        {
            if ( name .startsWith( DIRECTION ) ) {
                String prefName = name .substring( DIRECTION .length() );
                color = getColorPref( prefName );
            }
            else if ( name .startsWith( PLANE ) ) {
                String prefName = name .substring( PLANE .length() );
                color = getColorPref( prefName );
                color = color .getPastel();
            }
            else if ( name .startsWith( RGB_ORBIT ) || name .startsWith( RGB_CUSTOM ) ) {
            	// when rendering with Java3d, this is called the first time we create an
            	//   Appearance for a color name.
                StringTokenizer tokens = new StringTokenizer( name );
                tokens .nextToken(); // skip RGB_CUSTOM or RGB_ORBIT
                int r = Integer .parseInt( tokens .nextToken() );
                int g = Integer .parseInt( tokens .nextToken() );
                int b = Integer .parseInt( tokens .nextToken() );
                color = new Color( r, g, b );
            }
            else if ( name .equals( CONNECTOR ) )
                color = getColorPref( "white" );
            else if ( name .equals( HIGHLIGHT ) )
                color = getColorPref( "highlight" );
            else if ( name .equals( HIGHLIGHT_MAC ) )
                color = getColorPref( "highlight.mac" );
            else if ( name .equals( PANEL ) )
                color = getColorPref( "panels" );
            else if ( name .equals( BACKGROUND ) )
                color = getColorPref( "background" );
            //
            if ( color == null )
            {
                // each of r,g,b will have bits 1XXXX111, where XXXX are determined
                //  by the hex digits of mNextNewColor
                mNextNewColor = (mNextNewColor + STEP) % 0x1000; // can only use one hex digit per RGB
                int i = mNextNewColor;
                int r = 0x87 + ((i % 0x10) << 3);
                i = i >> 4;
                int g = 0x87 + ((i % 0x10) << 3);
                i = i >> 4;
                int b = 0x87 + ((i % 0x10) << 3);
                i = i >> 4;
                color = new Color( r, g, b );
            }
            addColor( name, color );
        }
		return color;
    }
	
    public Iterator getColorNames()
    {
        return mColors .keySet() .iterator();
    }

    public void reset()
    {}
}
