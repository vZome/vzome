package com.vzome.unity;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import com.vzome.api.Application;
import com.vzome.api.Document;
import com.vzome.core.editor.DocumentModel;
import com.vzome.core.render.RenderedModel;
import com.vzome.core.render.RenderingChanges;
import com.vzome.core.viewing.ExportedVEFShapes;

public class Adapter
{
    private final Class<?> unityPlayerClass;
    private final String gameObjectName;
    private final Application app;
    private final RenderingChanges renderer;

    public Adapter( Class<?> unityPlayerClass, String gameObjectName )
    {
        this .unityPlayerClass = unityPlayerClass;
        this .gameObjectName = gameObjectName;
        
        this .app = new Application();
        this .renderer = new Renderer( this );
    }
    
    void sendMessage( String callbackFn, String message )
    {
//        System.out.println( message );
        try {
            Method method = this .unityPlayerClass .getMethod( "UnitySendMessage", new Class<?>[] { String.class, String.class, String.class } );
            method .invoke( null, gameObjectName, callbackFn, message );
        }
        catch ( NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e ) {
            e.printStackTrace();
        }
    }

    public void logInfo( String message )
    {
        this .sendMessage( "LogInfo", message );
    }

    public void logException( Exception e )
    {
        e .printStackTrace();
        this .sendMessage( "LogException", e .getMessage() );
    }

    public void registerShape( String path, String vef )
    {
        System.out.println( "registerShape " + path );
        ExportedVEFShapes .injectShapeVEF( path, vef );
    }
    
    public Controller loadFile( String path )
    {
        try {
            Document doc = this .app .loadFile( path );
            logInfo( "loadFile done: " + path );
            DocumentModel model = doc .getDocumentModel();
            RenderedModel renderedModel = model .getRenderedModel();
            renderedModel .addListener( this .renderer );
            this .logInfo( "renderer is listening" );

            // This just to render the center ball
            RenderedModel .renderChange( new RenderedModel( null, null ), renderedModel, this .renderer );

            model .finishLoading( false, false );
            this .logInfo( "DONE rendering!" );

            return new Controller( model, this );
        }
        catch (Exception e)
        {
            this .logException( e );
            return null;
        }
    }
    
    public Controller loadUrl( String url )
    {
        try {
            Document doc = this .app .loadUrl( url );
            logInfo( "loadUrl done: " + url );
            DocumentModel model = doc .getDocumentModel();
            RenderedModel renderedModel = model .getRenderedModel();
            renderedModel .addListener( this .renderer );
            this .logInfo( "renderer is listening" );

            // This just to render the center ball
            RenderedModel .renderChange( new RenderedModel( null, null ), renderedModel, this .renderer );

            model .finishLoading( false, false );
            this .logInfo( "DONE rendering!" );

            return new Controller( model, this );
        }
        catch (Exception e)
        {
            this .logException( e );
            return null;
        }
    }
        
    public static void main( String[] args )
    {
        String twoPanels = 
                "vZome VEF 6 field golden\n" + 
                "\n" + 
                "6\n" + 
                "(0,0) (0,0) (0,0) (0,0)\n" + 
                "(0,0) (0,0) (0,0) (4,2)\n" + 
                "(0,0) (0,0) (4,2) (0,0)\n" + 
                "(0,0) (0,0) (4,2) (4,2)\n" + 
                "(0,0) (4,2) (0,0) (0,0)\n" + 
                "(0,0) (4,2) (4,2) (0,0)\n" + 
                "\n" + 
                "0\n" + 
                "\n" + 
                "2\n" + 
                "4  2 5 4 0 \n" + 
                "4  4 5 3 1 \n" + 
                "\n" + 
                "0\n" + 
                "";
        Adapter adapter = new Adapter( null, null );
        adapter .registerShape( "default-connector", twoPanels );
        Controller c = adapter .loadUrl( "http://vzome.com/models/2008/02-Feb/06-Scott-K4more/K4more.vZome" );
        System.out.println( "%%%%%%%%%%%%%" );
        System.out.println( "%%%%%%%%%%%%%" );
        System.out.println( "%%%%%%%%%%%%%" );
        System.out.println( "%%%%%%%%%%%%%" );
        System.out.println( "%%%%%%%%%%%%%" );
        System.out.println( "%%%%%%%%%%%%%" );
        System.out.println( "%%%%%%%%%%%%%" );
        System.out.println( "%%%%%%%%%%%%%" );
        c .doAction( "undo" );
        c .doAction( "undo" );
    }
}
