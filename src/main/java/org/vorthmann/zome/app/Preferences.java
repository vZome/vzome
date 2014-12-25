/*
 * Created on Jun 9, 2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package org.vorthmann.zome.app;

import java.util.StringTokenizer;

import org.vorthmann.ui.Controller;

import com.vzome.core.render.Color;

/**
 * @author vorth
 * 
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class Preferences
{
    public Preferences( Controller props )
    {
        mProperties = props;
    }

    private final Controller mProperties;

    public String getPreference( String key )
    {
        return mProperties.getProperty( key );
    }

    public float getFloatPref( String name )
    {
        String pref = getPreference( name );
        if ( pref == null || pref.equals( "" ) )
            return 0.0f;
        return Float.parseFloat( pref );

    }

    public int getIntPref( String name )
    {
        String pref = getPreference( name );
        if ( pref == null || pref.equals( "" ) )
            return 0;
        return Integer.parseInt( pref );

    }

    public boolean getBooleanPref( String name )
    {
        String pref = getPreference( name );
        if ( pref == null || pref.equals( "" ) )
            return false;
        return Boolean.valueOf( pref ).booleanValue();
    };

    public Color getColorPref( String name )
    {
        float[] percents = getVectorPref( "color.percent." + name );
        if ( percents != NO_VECTOR ) {
            return new Color( Math.round( percents[0] * 0xFF / 100 ), Math
                    .round( percents[1] * 0xFF / 100 ), Math
                    .round( percents[2] * 0xFF / 100 ) );
        }
        String pref = getPreference( "color." + name );
        if ( pref == null || pref.equals( "" ) )
            return Color.WHITE;
        StringTokenizer tokens = new StringTokenizer( pref, ", " );
        int[] rgb = new int[] { 0, 0, 0 };
        int i = 0;
        while ( tokens.hasMoreTokens() )
            rgb[i++] = Integer.parseInt( tokens.nextToken() );
        return new Color( rgb[0], rgb[1], rgb[2] );
    }

    private static final float[] NO_VECTOR = new float[] { 0f, 0f, 0f };

    public float[] getVectorPref( String name )
    {
        float[] result = NO_VECTOR;
        String pref = getPreference( name );
        if ( pref == null || pref.equals( "" ) )
            return result;
        result = (float[]) result.clone();
        StringTokenizer tokens = new StringTokenizer( pref, ", " );
        int i = 0;
        while ( tokens.hasMoreTokens() )
            result[i++] = Float.parseFloat( tokens.nextToken() );
        return result;
    }

}
