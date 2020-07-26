package com.vzome.desktop.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.vzome.core.render.JsonMapper;
import com.vzome.core.render.RenderedManifestation;
import com.vzome.core.render.RenderingChanges;
import com.vzome.core.render.Shapes;

public class RemoteClientRendering implements RenderingChanges
{
    private JsonSink queue;
    
    private final JsonMapper mapper = new JsonMapper();
	
	public interface JsonSink
	{
	    void sendJson( JsonNode node );
	}
	
	private void sendJson( JsonNode node )
	{
	    this .queue .sendJson( node );
	}

	public RemoteClientRendering( JsonSink queue )
	{
        this .queue = queue;
	}

	@Override
	public void reset() {}

	@Override
	public void manifestationAdded( RenderedManifestation rm )
	{
        ObjectNode node = this .mapper .getObjectNode( rm );
        if ( node != null ) {
            ObjectNode shapeNode = this .mapper .getShapeNode( rm .getShape() );
            if ( shapeNode != null )
            {
                shapeNode .put( "render", "shape" );
                sendJson( shapeNode );
            }
            node .put( "render", "instance" );
            node .put( "id", rm .getGuid() .toString() );
            sendJson( node );
        }
	}

	@Override
	public void manifestationRemoved( RenderedManifestation rm )
	{
        ObjectNode node = this .mapper .getObjectMapper() .createObjectNode();
        node .put( "render", "delete" );
        node .put( "id", rm .getGuid() .toString() );
        sendJson( node );
	}

	@Override
	public void manifestationSwitched(RenderedManifestation from, RenderedManifestation to) {}

	@Override
	public void glowChanged(RenderedManifestation manifestation) {}

	@Override
	public void colorChanged(RenderedManifestation manifestation) {}

	@Override
	public void locationChanged(RenderedManifestation manifestation) {}

	@Override
	public void orientationChanged(RenderedManifestation manifestation) {}

	@Override
	public void shapeChanged(RenderedManifestation manifestation) {}

    @Override
    public boolean shapesChanged( Shapes shapes )
    {
        return false;
    }
}