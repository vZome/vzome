package com.vzome.server;

import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.vecmath.Matrix4d;
import javax.vecmath.Point3d;
import javax.vecmath.Quat4d;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vzome.core.algebra.AlgebraicMatrix;
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
	private final ObjectMapper objectMapper = new ObjectMapper();
	private final Set<String> shapeIds = new HashSet<>();
	private Map<AlgebraicMatrix,Quat4d> rotations = new HashMap<>();
	private final ThrottledQueue queue;

	public RemoteClientRendering( ThrottledQueue queue )
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
		try {
			if ( ! this .shapeIds .contains( shapeId ) )
			{
				this .shapeIds .add( shapeId );
				String shapeJson = this .objectMapper .writeValueAsString( shape );
				this .queue .add( "{ \"render\": \"shape\", \"shape\": " + shapeJson +" }" );
			}
			if ( man instanceof Strut )
			{
				Strut strut = (Strut) man;
				RealVector start = rm .getLocation();
				String startJson = this .objectMapper .writeValueAsString( start );
				String quatJson = this .objectMapper .writeValueAsString( quaternion );
				String color = rm .getColor() .toWebString();
				this .queue .add( "{ \"render\": \"segment\", \"start\": " + startJson
						+ ", \"id\": \"" + rm .getGuid()
						+ "\", \"shape\": \"" + shapeId
						+ "\", \"rotation\": " + quatJson
						+ ", \"color\": \"" + color + "\" }" );
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
				this .queue .add( "{ \"render\": \"ball\", \"center\": " + centerJson
						+ ", \"id\": \"" + rm .getGuid()
						+ "\", \"shape\": \"" + shapeId
						+ "\", \"color\": \"" + colorStr + "\" }" );
			}
		} catch ( JsonProcessingException e ) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
		Manifestation man = rm .getManifestation();
		this .queue .add( "{ \"render\": \"delete\", \"id\": \"" + rm .getGuid() + "\" }" );
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