package com.vzome.xml;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ResourceLoader
{
    private static ResourceLoader RESOURCE_LOADER = new ResourceLoader();

    private static final Logger logger = Logger.getLogger( "com.vzome.xml.ResourceLoader" );

    public static void setResourceLoader( ResourceLoader loader )
    {
        RESOURCE_LOADER = loader;
    }
    
    public static String loadStringResource( String path )
    {
        return RESOURCE_LOADER .loadTextResource( path );
    }
    
    public String loadTextResource( String path )
    {
        try {
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
        } catch (Exception e) {
            if ( logger .isLoggable( Level.FINE ) )
                logger .fine( "problem loading resource: " + path );
            return null;
        }
    }
}
