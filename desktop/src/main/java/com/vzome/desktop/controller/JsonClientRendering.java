package com.vzome.desktop.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.vzome.core.editor.api.Shapes;
import com.vzome.core.render.JsonMapper;
import com.vzome.core.render.RenderedManifestation;
import com.vzome.core.render.RenderingChanges;

public class JsonClientRendering implements RenderingChanges
{
    private EventDispatcher dispatcher;
    
    private final JsonMapper mapper = new JsonMapper();

    private boolean instanceStreamEnabled = true;
	
	public interface EventDispatcher
	{
	    void dispatchEvent( String type, JsonNode node );
	}
	
	public JsonClientRendering( EventDispatcher dispatcher, boolean instanceStreamEnabled )
	{
        this .dispatcher = dispatcher;
        this .instanceStreamEnabled = instanceStreamEnabled;
	}
	
	public void enableInstanceStream( boolean value )
	{
        this .instanceStreamEnabled = value;
    }

	@Override
	public void reset() {}

	@Override
	public void manifestationAdded( RenderedManifestation rm )
	{
        ObjectNode shapeNode = this .mapper .getShapeNode( rm .getShape() );
        if ( shapeNode != null )
            this .dispatcher .dispatchEvent( "SHAPE_DEFINED", shapeNode );

        if ( ! this .instanceStreamEnabled )
            return;

        ObjectNode node = this .mapper .getObjectNode( rm, false );
        if ( node != null ) {
            node .put( "id", rm .getGuid() .toString() );
            if ( this .instanceStreamEnabled )
                this .dispatcher .dispatchEvent( "INSTANCE_ADDED", node );
        }
	}

	@Override
	public void manifestationRemoved( RenderedManifestation rm )
	{
        if ( ! this .instanceStreamEnabled )
            return;
        ObjectNode node = this .mapper .getObjectMapper() .createObjectNode();
        node .put( "id", rm .getGuid() .toString() );
        this .dispatcher .dispatchEvent( "INSTANCE_REMOVED", node );
	}

    @Override
    public void colorChanged( RenderedManifestation rm )
    {
        if ( ! this .instanceStreamEnabled )
            return;
        ObjectNode node = this .mapper .getObjectMapper() .createObjectNode();
        node .put( "id", rm .getGuid() .toString() );
        node .put( "color", rm .getColor() .toWebString() );
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