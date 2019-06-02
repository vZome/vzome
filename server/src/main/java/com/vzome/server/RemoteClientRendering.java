package com.vzome.server;

import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.vecmath.Matrix4d;
import javax.vecmath.Point3d;
import javax.vecmath.Quat4d;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.vzome.core.algebra.AlgebraicMatrix;
import com.vzome.core.algebra.AlgebraicNumber;
import com.vzome.core.construction.Polygon;
import com.vzome.core.math.Polyhedron;
import com.vzome.core.math.RealVector;
import com.vzome.core.model.Connector;
import com.vzome.core.model.Manifestation;
import com.vzome.core.model.Strut;
import com.vzome.core.render.Color;
import com.vzome.core.render.RenderedManifestation;
import com.vzome.core.render.RenderingChanges;
import com.vzome.desktop.controller.RenderingViewer;

class RemoteClientRendering implements RenderingChanges, RenderingViewer, PropertyChangeListener
{
    private static class RealTrianglesView implements AlgebraicNumber.Views.Real, Polygon.Views.Triangles {}

    // Keep things simple for the client code: all real numbers, all faces triangulated
    private final ObjectMapper objectMapper = new ObjectMapper();
	private final ObjectWriter objectWriter = objectMapper .writerWithView( RealTrianglesView.class );

	private final Set<String> shapeIds = new HashSet<>();
	private Map<AlgebraicMatrix,Quat4d> rotations = new HashMap<>();
    private JsonSink queue;
	
	public interface JsonSink
	{
	    void sendJson( JsonNode node );
	}
	
	private void sendJson( JsonNode node )
	{
	    this .queue .sendJson( node );
	}
	
	private JsonNode asTreeWithView( Object object )
	{
	    try {
            return objectMapper .readTree( objectWriter .writeValueAsString( object ) );
        } catch (IOException e) {
            e .printStackTrace();
            return objectMapper .createObjectNode();
        }
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
	public void pickPoint(MouseEvent e, Point3d imagePt, Point3d eyePt) {}

	@Override
	public RenderingChanges getRenderingChanges()
	{
		return this;
	}

	@Override
	public void captureImage(int maxSize, ImageCapture capture) {}

	@Override
	public void reset() {}

	@Override
	public void manifestationAdded( RenderedManifestation rm )
	{
		Manifestation man = rm .getManifestation();
		Polyhedron shape = rm .getShape();
		Quat4d quaternion = getQuaternion( rm .getOrientation() );
		String shapeId = shape .getGuid() .toString();
		if ( ! this .shapeIds .contains( shapeId ) )
		{
		    this .shapeIds .add( shapeId );
		    ObjectNode node = this .objectMapper .createObjectNode();
		    node .put( "render", "shape" );
		    node .set( "shape", this .asTreeWithView( shape ) );
		    sendJson( node );
		}
		if ( man instanceof Strut )
		{

		    ObjectNode node = this .objectMapper .createObjectNode();
		    node .put( "render", "segment" );
		    node .put( "id", rm .getGuid() .toString() );
		    node .put( "shape", shapeId );

		    node .put( "color", rm .getColor() .toWebString() );

		    node .set( "start", this .asTreeWithView( rm .getLocation() ) );

		    node .set( "rotation", this .asTreeWithView( quaternion ) );

		    sendJson( node );
		}
		else if ( man instanceof Connector )
		{
		    ObjectNode node = this .objectMapper .createObjectNode();
		    node .put( "render", "ball" );
		    node .put( "id", rm .getGuid() .toString() );
		    node .put( "shape", shapeId );

		    Color color = rm .getColor();
		    if ( color == null )
		        color = Color.WHITE;
		    node .put( "color", color .toWebString() );

		    RealVector center = ((Connector) man) .getLocation() .toRealVector();
		    node .set( "center", this .asTreeWithView( center ) );

		    sendJson( node );
		}
	}

	private Quat4d getQuaternion( AlgebraicMatrix orientation )
	{
		Quat4d result = this .rotations .get( orientation );
		if ( result == null ) {
			Matrix4d matrix = new Matrix4d();
			for ( int i = 0; i < 3; i++) {
				for ( int j = 0; j < 3; j++) {
					double value = orientation .getElement( i, j ) .evaluate();
					matrix .setElement( i, j, value );
				}
			}
			result = new Quat4d();
			matrix .get( result );
			this .rotations .put( orientation, result );
		}
		return result;
	}

	@Override
	public void manifestationRemoved( RenderedManifestation rm )
	{
        ObjectNode node = this .objectMapper .createObjectNode();
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
}