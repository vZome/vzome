package com.vzome.unity;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import javax.vecmath.Matrix3d;
import javax.vecmath.Quat4d;
import javax.vecmath.Vector3d;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vzome.api.Application;
import com.vzome.api.Document;
import com.vzome.core.algebra.AlgebraicMatrix;
import com.vzome.core.commands.Command.Failure;
import com.vzome.core.editor.DocumentModel;
import com.vzome.core.math.RealVector;
import com.vzome.core.math.symmetry.Axis;
import com.vzome.core.math.symmetry.Direction;
import com.vzome.core.math.symmetry.Symmetry;
import com.vzome.core.render.RenderedManifestation;
import com.vzome.core.render.RenderedModel;
import com.vzome.core.render.RenderingChanges;
import com.vzome.core.viewing.ExportedVEFShapes;

public class Adapter
{
    private static final Application APP = new Application();
    private static Method SEND_MESSAGE_METHOD;
    
    private static final Map<String, Adapter> adapters = new HashMap<String, Adapter>();

    public static void initialize( Class<?> unityPlayerClass )
    {
        try {
            SEND_MESSAGE_METHOD = unityPlayerClass .getMethod( "UnitySendMessage", new Class<?>[] { String.class, String.class, String.class } );
        }
        catch ( NoSuchMethodException | SecurityException e )
        {
            e.printStackTrace();
        }
    }

    public static void registerShape( String path, String vef )
    {
        System.out.println( "registerShape " + path );
        ExportedVEFShapes .injectShapeVEF( path, vef );
    }
    
    public static void loadFile( String path, String gameObject )
    {
        try {
            Adapter adapter = new Adapter( gameObject, path, APP .loadFile( path ) );
            adapters .put( path, adapter );
            adapter .sendMessage( "AdapterReady", path );
        }
        catch ( Exception e )
        {
            e .printStackTrace();
        }
    }
    
    public static Adapter getAdapter( String path )
    {
        return adapters .get( path );
    }
    
    public static Adapter loadUrl( String url, String gameObject )
    {
        try {
            return new Adapter( gameObject, url, APP .loadUrl( url ) );
        }
        catch (Exception e)
        {
            e .printStackTrace();
            return null;
        }
    }

    private final String gameObjectName;
    private final String path;
    private final DocumentModel model;
    private final RenderingChanges renderer;
    private final RenderedModel renderedModel;
    private final ObjectMapper objectMapper;

    private Adapter( String gameObjectName, String path, Document doc )
    {
        this .gameObjectName = gameObjectName;
        this .path = path;
        this .model = doc .getDocumentModel();
        this .renderer = new Renderer( this );

        this .objectMapper = new ObjectMapper();
//
        logInfo( "loaded: " + path );
        renderedModel = model .getRenderedModel();
        renderedModel .addListener( this .renderer );

        // This just to render the center ball
        RenderedModel .renderChange( new RenderedModel( null, null ), renderedModel, this .renderer );

        try {
            model .finishLoading( false, false );
            this .logInfo( "DONE rendering!" );
        }
        catch ( Failure e )
        {
            this .logException( e );
        }
    }
    
    protected void sendMessage( String callbackFn, String message )
    {
        try {
            SEND_MESSAGE_METHOD .invoke( null, this .gameObjectName, callbackFn, message );
        }
        catch ( SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e )
        {
            e.printStackTrace();
        }
    }

    protected void logInfo( String message )
    {
        this .sendMessage( "LogInfo", message );
    }

    protected void logException( Exception e )
    {
        e .printStackTrace();
        this .sendMessage( "LogException", e .getMessage() );
    }
    
    public void doAction( String action )
    {
        try {
            switch ( action ) {

            case "undo":
                this .model .getHistoryModel() .undo( true );
                break;

            case "redo":
                this .model .getHistoryModel() .redo( true );
                break;

            case "undoAll":
                this .model .getHistoryModel() .undoAll();
                break;

            case "redoAll":
                this .model .getHistoryModel() .redoAll( - 1 );
                break;

            default:
                if ( action .startsWith( "{" ) ) {
                    
                    JsonNode node = this .objectMapper .readTree( action );
                    action = node .get( "action" ) .asText();
                    switch ( action ) {

                    case "MoveObject":
                        String guid = node .get( "id" ) .asText();
                        RenderedManifestation rm = this .renderedModel .getRenderedManifestation( guid );
                        
                        JsonNode posNode = node .get( "position" );
                        RealVector posR = this .objectMapper .treeToValue( posNode, RealVector.class );

                        Direction orbit = rm .getStrutOrbit();
                        if ( orbit != null ) {
                            JsonNode rotNode = node .get( "rotation" );
                            Quat4d rot = this .objectMapper .treeToValue( rotNode, Quat4d.class );
                            Matrix3d m = new Matrix3d();
                            m .set( rot );

                            RealVector protoR = orbit .getPrototype() .toRealVector();
                            Vector3d prototype = new Vector3d( protoR.x, protoR.y, protoR.z );
                            m .transform( prototype );
                            RealVector rotated = new RealVector( prototype .x, prototype .y, prototype .z );
                            
                            Axis axis = orbit .getAxis( rotated );
                            Symmetry symmetry = orbit .getSymmetry();
                            AlgebraicMatrix matrix = symmetry .getMatrix( axis .getOrientation() );                    
                            rm .setOrientation( matrix );
                        }

                        this .renderer .locationChanged( rm );
                        break;

                    default:
                        break;
                    }
                }
                else
                    this .model .doEdit( action );
                break;
            }
        }
        catch ( Exception e )
        {
            e .printStackTrace();
            this .logException( e );
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
        registerShape( "default-connector", twoPanels );
        Adapter adapter = loadUrl( "http://vzome.com/models/2008/02-Feb/06-Scott-K4more/K4more.vZome", null );
        System.out.println( "%%%%%%%%%%%%%" );
        System.out.println( "%%%%%%%%%%%%%" );
        System.out.println( "%%%%%%%%%%%%%" );
        System.out.println( "%%%%%%%%%%%%%" );
        System.out.println( "%%%%%%%%%%%%%" );
        System.out.println( "%%%%%%%%%%%%%" );
        System.out.println( "%%%%%%%%%%%%%" );
        System.out.println( "%%%%%%%%%%%%%" );
        adapter .doAction( "undo" );
        adapter .doAction( "undo" );
    }
}
