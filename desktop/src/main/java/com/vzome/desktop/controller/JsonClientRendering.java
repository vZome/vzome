package com.vzome.desktop.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.vzome.core.render.JsonMapper;
import com.vzome.core.render.RenderedManifestation;
import com.vzome.core.render.RenderingChanges;
import com.vzome.core.render.Shapes;

public class JsonClientRendering implements RenderingChanges
{
    private EventDispatcher dispatcher;
    
    private final JsonMapper mapper = new JsonMapper();
	
	public interface EventDispatcher
	{
	    void dispatchEvent( String type, JsonNode node );
	}
	
	public JsonClientRendering( EventDispatcher dispatcher )
	{
        this .dispatcher = dispatcher;
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
                this .dispatcher .dispatchEvent( "SHAPE_DEFINED", shapeNode );
            }
            node .put( "id", rm .getGuid() .toString() );
            this .dispatcher .dispatchEvent( "INSTANCE_ADDED", node );
        }
	}

	@Override
	public void manifestationRemoved( RenderedManifestation rm )
	{
        ObjectNode node = this .mapper .getObjectMapper() .createObjectNode();
        node .put( "render", "delete" );
        node .put( "id", rm .getGuid() .toString() );
        this .dispatcher .dispatchEvent( "INSTANCE_REMOVED", node );
	}

    @Override
    public void colorChanged( RenderedManifestation rm )
    {
        ObjectNode node = this .mapper .getObjectMapper() .createObjectNode();
        node .put( "render", "changeColor" );
        node .put( "id", rm .getGuid() .toString() );
        node .put( "color", rm .getColor() .toWebString() );
        this .dispatcher .dispatchEvent( "INSTANCE_COLORED", node );
    }

	@Override
	public void manifestationSwitched(RenderedManifestation from, RenderedManifestation to) {}

	@Override
	public void glowChanged(RenderedManifestation manifestation) {}

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