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
    
    public static String loadStringResource( String path ) throws IOException
    {
        String content = INJECTED_RESOURCES .get( path );
        if ( content != null )
            return content;
        
        InputStream input = ResourceLoader.class .getClassLoader() .getResourceAsStream( path );
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] buf = new byte[1024];
        int num;
        while ( ( num = input .read( buf, 0, 1024 )) > 0 )
            out .write( buf, 0, num );
        return new String( out .toByteArray() );
    }
}
