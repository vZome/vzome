
package com.vzome.unity;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.vzome.core.algebra.AlgebraicNumber;
import com.vzome.core.editor.api.Shapes;
import com.vzome.core.math.Polyhedron;
import com.vzome.core.render.JsonMapper;
import com.vzome.core.render.RenderedManifestation;
import com.vzome.core.render.RenderingChanges;

class Renderer implements RenderingChanges
{    
    static class UnityMeshView implements AlgebraicNumber.Views.Real, Polyhedron.Views.UnityMesh {}

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
            this .adapter .logException( e );
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
    public void glowChanged( RenderedManifestation rm ) {}

    @Override
    public void colorChanged( RenderedManifestation rm ) {}

    @Override
    public void locationChanged( RenderedManifestation rm ) {}

    @Override
    public void orientationChanged( RenderedManifestation rm ) {}

    @Override
    public void shapeChanged( RenderedManifestation rm ) {}

    @Override
    public boolean shapesChanged( Shapes shapes )
    {
        return true;
    }
}