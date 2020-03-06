package com.vzome.unity;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.vzome.api.Application;
import com.vzome.api.Document;
import com.vzome.core.algebra.AlgebraicNumber;
import com.vzome.core.editor.DocumentModel;
import com.vzome.core.math.Polyhedron;
import com.vzome.core.render.JsonMapper;
import com.vzome.core.render.RenderedManifestation;
import com.vzome.core.render.RenderedModel;
import com.vzome.core.render.RenderingChanges;
import com.vzome.core.render.Shapes;

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
    
    public DocumentModel loadUrl( String url )
    {
        try {
            Document doc = this .app .loadUrl( url );
            this .sendMessage( "LogInfo", "loadUrl done: " + url );
            DocumentModel model = doc .getDocumentModel();
            RenderedModel renderedModel = model .getRenderedModel();
            renderedModel .addListener( this .renderer );
            this .sendMessage( "LogInfo", "renderer is listening" );

            model .finishLoading( false, false );
            this .sendMessage( "LogInfo", "DONE rendering!" );

            return model;
        }
        catch (Exception e) {
            e.printStackTrace();
            this .sendMessage( "LogException", e .getMessage() );
            return null;
        }
    }
    
    private static class UnityMeshView implements AlgebraicNumber.Views.Real, Polyhedron.Views.UnityMesh {}

    private class Renderer implements RenderingChanges
    {
        private final Adapter adapter;
        private final JsonMapper mapper = new JsonMapper( UnityMeshView.class );
        private final ObjectWriter objectWriter = mapper .getObjectMapper() .writer();

        Renderer( Adapter adapter )
        {
            this.adapter = adapter;
        }

        private void sendJson( String callbackFn, ObjectNode node )
        {
            try {
                String jsonString = this .objectWriter .writeValueAsString( node );
                this .adapter .sendMessage( callbackFn, jsonString );
            }
            catch (JsonProcessingException e) {
                e.printStackTrace();
                this .adapter .sendMessage( "LogException", e .getMessage() );
            }
        }

        @Override
        public void manifestationAdded( RenderedManifestation rm )
        {
            ObjectNode node = this .mapper .getObjectNode( rm );
            if ( node != null ) {
                Polyhedron shape = rm .getShape();
                ObjectNode shapeNode = this .mapper .getShapeNode( shape );
                if ( shapeNode != null )
                {
                    shapeNode .put( "id", shape .getGuid() .toString() );
                    sendJson( "DefineMesh", shapeNode );
                }
                node .put( "id", rm .getGuid() .toString() );
                sendJson( "CreateGameObject", node );
            }
        }

        @Override
        public void manifestationRemoved( RenderedManifestation rm )
        {
            ObjectNode node = this .mapper .getObjectMapper() .createObjectNode();
            node .put( "id", rm .getGuid() .toString() );
            sendJson( "DeleteGameObject", node );
        }

        @Override
        public void reset() {}

        @Override
        public void manifestationSwitched( RenderedManifestation from, RenderedManifestation to ) {}

        @Override
        public void glowChanged( RenderedManifestation manifestation ) {}

        @Override
        public void colorChanged( RenderedManifestation manifestation ) {}

        @Override
        public void locationChanged( RenderedManifestation manifestation ) {}

        @Override
        public void orientationChanged( RenderedManifestation manifestation ) {}

        @Override
        public void shapeChanged( RenderedManifestation manifestation ) {}

        @Override
        public boolean shapesChanged( Shapes shapes )
        {
            return true;
        }
    }
    
    public static void main( String[] args )
    {
        Adapter adapter = new Adapter( null, null );
        adapter .loadUrl( "http://vzome.com/models/2008/02-Feb/06-Scott-K4more/K4more.vZome" );
    }
}
