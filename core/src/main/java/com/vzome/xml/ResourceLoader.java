package com.vzome.xml;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class ResourceLoader
{
    private static final Map<String, String> INJECTED_RESOURCES = new HashMap<String, String>();
    
    public static void injectResource( String path, String content )
    {
        INJECTED_RESOURCES .put( path, content );
    }
    
    public static boolean hasInjectedResource( String path )
    {
        return INJECTED_RESOURCES .containsKey( path );
    }
    
    public static String getInjectedResource( String path )
    {
        return INJECTED_RESOURCES .get( path );
    }
    
    public static String loadStringResource( String path ) throws IOException
    {
        if ( hasInjectedResource( path ) )
            return getInjectedResource( path );
        
        InputStream input = ResourceLoader.class .getClassLoader() .getResourceAsStream( path );
        if ( input == null )
            return null; // Should never happen?
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] buf = new byte[1024];
        int num;
        while ( ( num = input .read( buf, 0, 1024 )) > 0 )
            out .write( buf, 0, num );
        input .close(); 
        return new String( out .toByteArray() );
    }
}
