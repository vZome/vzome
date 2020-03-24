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
import com.vzome.core.algebra.AlgebraicField;
import com.vzome.core.algebra.AlgebraicVector;
import com.vzome.core.commands.Command.Failure;
import com.vzome.core.construction.FreePoint;
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
    
    public static void loadUrl( String url, String gameObject )
    {
        try {
            Adapter adapter = new Adapter( gameObject, url, APP .loadUrl( url ) );
            adapters .put( url, adapter );
            adapter .sendMessage( "AdapterReady", url );
        }
        catch (Exception e)
        {
            e .printStackTrace();
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
        System.out.println( "Adapter.sendMessage(): " + callbackFn + " : " + message );
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
            if ( action .startsWith( "{" ) ) {

                System.out.println( action );
                JsonNode node = this .objectMapper .readTree( action );
                action = node .get( "action" ) .asText();
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

                case "MoveObject":
                    String guid = node .get( "id" ) .asText();
                    RenderedManifestation rm = this .renderedModel .getRenderedManifestation( guid );
                    AlgebraicField field = this .renderedModel .getField();

                    JsonNode posNode = node .get( "position" );
                    RealVector posR = this .objectMapper .treeToValue( posNode, RealVector.class );

                    RenderedManifestation nearby = this .renderedModel .getNearbyBall( posR, 0.6d );
                    AlgebraicVector targetPosition = null;
                    if ( nearby != null )
                        targetPosition = nearby .getLocationAV();
                    else {
                        AlgebraicVector snapPos = field .nearestAlgebraicVector( posR );
                        targetPosition = snapPos;
                    }

                    Direction orbit = rm .getStrutOrbit();
                    if ( orbit != null ) {

                        JsonNode rotNode = node .get( "rotation" );
                        Quat4d rot = this .objectMapper .treeToValue( rotNode, Quat4d.class );
                        Matrix3d m = new Matrix3d();
                        m .set( rot );

                        // Do NOT use orbit .getPrototype(), since that does not always line up with the shape.
                        //  See ExportedVEFShapes .createStrutGeometry()
                        RealVector protoR = orbit .getAxis( Symmetry .PLUS, 0 ) .normal() .toRealVector();
                        Vector3d prototype = new Vector3d( protoR.x, protoR.y, protoR.z );
                        m .transform( prototype );
                        RealVector rotated = new RealVector( prototype .x, prototype .y, prototype .z );

                        Axis axis = orbit .getAxis( rotated );

                        Map<String,Object> props = new HashMap<>();
                        props .put( "oldStrut", rm .getManifestation() );
                        props .put( "anchor", new FreePoint( targetPosition ) );
                        props .put( "zone", axis );
                        props .put( "length", rm .getStrutLength() );
                        this .model .doEdit( "StrutMove", props );
                    }
                    break;

                default:
                    break;
                }
            }
            else
                this .model .doEdit( action );
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
        String url = "http://vzome.com/models/2008/02-Feb/06-Scott-K4more/K4more.vZome";
        loadUrl( url, null );
        System.out.println( "%%%%%%%%%%%%%" );
        System.out.println( "%%%%%%%%%%%%%" );
        System.out.println( "%%%%%%%%%%%%%" );
        System.out.println( "%%%%%%%%%%%%%" );
        System.out.println( "%%%%%%%%%%%%%" );
        System.out.println( "%%%%%%%%%%%%%" );
        System.out.println( "%%%%%%%%%%%%%" );
        System.out.println( "%%%%%%%%%%%%%" );
        Adapter adapter = getAdapter( url );
    }
}
