package com.vzome.server;

import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import javax.vecmath.Matrix4d;
import javax.vecmath.Point3d;

import org.eclipse.jetty.websocket.api.Session;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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
	private final Session session;
	private final ObjectMapper objectMapper = new ObjectMapper();
	private final Set<String> shapeIds = new HashSet<>();

	public RemoteClientRendering( Session session )
	{
		this .session = session;
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
		String shapeId = shape .getGuid() .toString();
		try {
			if ( ! this .shapeIds .contains( shapeId ) )
			{
				this .shapeIds .add( shapeId );
				String shapeJson = this .objectMapper .writeValueAsString( shape );
				this .session .getRemote() .sendString( "{ \"render\": \"shape\", \"shape\": " + shapeJson +" }", null );
			}
			if ( man instanceof Strut )
			{
				Strut strut = (Strut) man;
				RealVector start = strut .getLocation() .toRealVector();
				RealVector end = strut .getEnd() .toRealVector();
				String startJson = this .objectMapper .writeValueAsString( start );
				String endJson = this .objectMapper .writeValueAsString( end );
				String color = rm .getColor() .toWebString();
				this .session .getRemote() .sendString( "{ \"render\": \"segment\", \"start\": " + startJson + ", \"end\": " + endJson
						+ ", \"id\": \"" + rm .getGuid()
						+ "\", \"color\": \"" + color + "\" }", null );
			}
			else if ( man instanceof Connector )
			{
				Connector ball = (Connector) man;
				RealVector center = ball .getLocation() .toRealVector();
				String centerJson = this .objectMapper .writeValueAsString( center );
				Color color = rm .getColor();
				if ( color == null )
					color = Color.WHITE;
				String colorStr = color .toWebString();
				this .session .getRemote() .sendString( "{ \"render\": \"ball\", \"center\": " + centerJson
						+ ", \"id\": \"" + rm .getGuid()
						+ "\", \"shape\": \"" + shapeId
						+ "\", \"color\": \"" + colorStr + "\" }", null );
			}
		} catch ( JsonProcessingException e ) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void manifestationRemoved( RenderedManifestation rm )
	{
		Manifestation man = rm .getManifestation();
		if ( man instanceof Strut ) {
			this .session .getRemote() .sendString( "{ \"render\": \"delete\", \"id\": \"" + rm .getGuid() + "\" }", null );
		}
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
	public void propertyChange( PropertyChangeEvent evt )
	{
		//this .session .getRemote() .sendString( "property " + evt .getPropertyName() + " now: " + evt .getNewValue(), null );
	}
}