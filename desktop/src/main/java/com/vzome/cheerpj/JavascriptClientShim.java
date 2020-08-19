package com.vzome.cheerpj;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.vorthmann.zome.app.impl.DocumentController;
import org.vorthmann.zome.app.impl.JsonClientShim;

import com.leaningtech.client.Global;

public class JavascriptClientShim extends JsonClientShim 
{
    public JavascriptClientShim( String logLevel )
    {
        super( logLevel );
    }

    static JavascriptClientShim SHIM;

    @Override
    public void dispatchSerializedJson( String eventStr )
    {
        Global .jsCallS( "dispatchToRedux", Global.JSString( eventStr ) );
    }

    @Override
    public void openApplication( File file )
    {
        Path path = file .toPath();
        System.out.println( "openApplication " + path );
        try {
            byte[] bytes = Files.readAllBytes( path );
            String name = file .getName();
            
            System.out.println( "openApplication got bytes" );

            Global .jsCallS( "fileExported", name, bytes );
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    // %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
    // %%%%%%  These are the entry points.  Always catch Throwable!
    // %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

    public static void main( String[] args )
    {
        try {
            String logLevel = "WARNING";
            if ( args .length > 0 )
                logLevel = args[ 0 ];
            SHIM = new JavascriptClientShim( logLevel );
            System .out .println( "vZome application initialized" );
        } catch (Throwable t) {
            t .printStackTrace();
        }
    }

    public static DocumentController openFile( String path )
    {
        try {
            SHIM .applicationController .doFileAction( "open", new File( path ) );
            return SHIM .renderDocument( path );
        } catch (Throwable t) {
            t .printStackTrace();
            return null;
        }
    }
}
