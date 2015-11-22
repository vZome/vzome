

package com.vzome.core.render;

import java.util.StringTokenizer;

/**
 * @author Scott Vorthmann
 */
public class Color
{
    public static final Color BLACK = new Color( 0, 0, 0 );

    public static final Color WHITE = new Color( 0xFF, 0xFF, 0xFF );

    public static final Color GREY_TRANSPARENT = new Color( 25, 25, 25, 50 );

    public int red, green, blue, alpha;

    public Color( int r, int g, int b, int a )
    {
        red = r > 0xFF? 0xFF : ( r < 0? 0 : r );
        green = g > 0xFF? 0xFF : ( g < 0? 0 : g );
        blue = b > 0xFF? 0xFF : ( b < 0? 0 : b );
        alpha = a > 0xFF? 0xFF : ( a < 0? 0 : a );
    }

    public Color( int r, int g, int b )
    {
        this( r, g, b, 0xFF );
    }
    
    public Color( int rgb )
    {
        this( ( rgb >> 16 ) & 0xFF, ( rgb >> 8 ) & 0xFF, rgb & 0xFF );
    }

    public float[] getRGBColorComponents( float[] rgb )
    {
        rgb[0] = red / 255f;
        rgb[1] = green / 255f;
        rgb[2] = blue / 255f;
        if ( rgb.length == 4 )
            rgb[3] = alpha / 255f;
        return rgb;
    }
    
    public int hashCode()
    {
        return getRGB();
    }
    
    public boolean equals( Object other )
    {
        if ( this == other )
            return true;
        if ( other == null )
            return true;
        if ( !( other instanceof Color ) )
            return false;
        Color c = (Color) other;
        return red == c.red && green==c.green && blue==c.blue && alpha==c.alpha;
    }
    
    public Color getPastel()
    {
        int r = red + (0xFF - red) / 2;
        int g = green + (0xFF - green) / 2;
        int b = blue + (0xFF - blue) / 2;
        
        return new Color( r, g, b );
    }


    /**
     * @return
     */
    public int getRGB()
    {
        return red * 0x010000 + green * 0x0100 + blue;
    }
    
    public String toString()
    {
        return red + "," + green + "," + blue + ( (alpha<0xFF)? ","+alpha : "" );
    }

    public static Color parseColor( String str )
    {
        StringTokenizer toks = new StringTokenizer( str, "," );
        String red = toks .nextToken();
        String green = toks .nextToken();
        String blue = toks .nextToken();
        return new Color( Integer.parseInt( red ), Integer.parseInt( green ), Integer.parseInt( blue ) );
    }

	public int getRed()
	{
		return red;
	}

	public int getGreen()
	{
		return this.green;
	}

	public int getBlue()
	{
		return this.blue;
	}

	public int getAlpha()
	{
		return this.alpha;
	}
}
