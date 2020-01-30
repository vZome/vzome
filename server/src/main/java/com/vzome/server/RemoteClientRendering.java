package com.vzome.server;

import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Collection;

import javax.vecmath.Matrix4d;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.vzome.core.math.Line;
import com.vzome.core.render.JsonMapper;
import com.vzome.core.render.RenderedManifestation;
import com.vzome.core.render.RenderingChanges;
import com.vzome.core.render.Shapes;
import com.vzome.desktop.controller.RenderingViewer;

class RemoteClientRendering implements RenderingChanges, RenderingViewer, PropertyChangeListener
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
	public void setEye( int eye ) {}

	@Override
	public void setViewTransformation( Matrix4d trans, int eye ) {}

	@Override
	public void setPerspective( double fov, double aspectRatio, double near, double far ) {}

	@Override
	public void setOrthographic( double halfEdge, double near, double far ) {}

	@Override
	public RenderedManifestation pickManifestation( MouseEvent e )
	{
		return null;
	}

	@Override
	public Collection<RenderedManifestation> pickCube()
	{
		return null;
	}

	@Override
	public Line pickRay( MouseEvent e )
	{
	    return null;
	}

	@Override
	public RenderingChanges getRenderingChanges()
	{
		return this;
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
	public void propertyChange( PropertyChangeEvent chg ) {}

    @Override
    public void captureImage(int maxSize, boolean withAlpha, ImageCapture capture) {}

    @Override
    public boolean shapesChanged( Shapes shapes )
    {
        // TODO Auto-generated method stub
        return false;
    }
}