
//(c) Copyright 2014, Scott Vorthmann.

package org.vorthmann.zome.render.jogl;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.vzome.core.math.Polyhedron;
import com.vzome.core.render.Color;
import com.vzome.core.render.Colors;
import com.vzome.core.render.OpenGlSceneLoader;
import com.vzome.core.render.RenderedManifestation;
import com.vzome.core.render.RenderingChanges;
import com.vzome.core.viewing.Lights;
import com.vzome.opengl.RenderingProgram;
import com.vzome.opengl.ShapeClass;

public class JoglScene implements RenderingChanges, PropertyChangeListener
{
    private Color bkgdColor;
    private static final float MODEL_SCALE_FACTOR = 2f; // this seems to align with the way Java3d rendering came out
    private float[][] orientations;
    private final Map<Polyhedron, ShapeClass> shapeClasses = new HashMap<>();
    
    private static class MutableShapeClass extends ShapeClass
    {
        public Set<RenderedManifestation> instances = new HashSet<>();  // needed when we remove something
    }

    JoglScene( Lights lights, Colors colors, boolean isSticky )
	{
        this.bkgdColor = lights .getBackgroundColor();
        
        lights .addPropertyListener( new PropertyChangeListener(){

            @Override
            public void propertyChange( PropertyChangeEvent chg )
            {
                if ( "backgroundColor" .equals( chg .getPropertyName() ) )
                {
                    int rgb =  Integer .parseInt( (String) chg .getNewValue(), 16 );
                    bkgdColor = new Color( rgb );
                }
            }} );
	}

    void render( RenderingProgram renderer )
    {
        if ( this .orientations != null ) {
            float[] rgba = new float[4];
            this .bkgdColor .getRGBColorComponents( rgba );
            renderer .setOrientations( this .orientations );
            renderer .renderScene( rgba, shapeClasses .values() );
        }
    }

	@Override
	public void reset()
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void manifestationAdded( RenderedManifestation rm )
	{
	    Polyhedron shape = rm .getShape();
	    MutableShapeClass shapeClass = (MutableShapeClass) this .shapeClasses .get( shape );
	    if ( shapeClass == null ) {
	        shapeClass = new MutableShapeClass();
	        OpenGlSceneLoader .setShapeData( shapeClass, shape, rm.getColor(), MODEL_SCALE_FACTOR );
	        this .shapeClasses .put( shape, shapeClass );

	        // This will happen only once, until we switch symmetry systems
	        if ( this.orientations == null ) {
                this .orientations = OpenGlSceneLoader .createOrientationsArray( rm .getModel() .getOrbitSource() );
            }
	    }
	    shapeClass .instances .add( rm );
	    shapeClass .replacePositions( OpenGlSceneLoader .createPositionsArray( shapeClass .instances, MODEL_SCALE_FACTOR ) );
	}

	@Override
	public void manifestationRemoved( RenderedManifestation rm )
	{
        Polyhedron shape = rm .getShape();
        MutableShapeClass shapeSet = (MutableShapeClass) this .shapeClasses .get( shape );
        shapeSet .instances .remove( rm );
        shapeSet .replacePositions( OpenGlSceneLoader .createPositionsArray( shapeSet .instances, MODEL_SCALE_FACTOR ) );
    }

	@Override
	public void manifestationSwitched( RenderedManifestation from, RenderedManifestation to )
	{
	}

	@Override
	public void glowChanged( RenderedManifestation rm )
	{
        Polyhedron shape = rm .getShape();
        MutableShapeClass shapeSet = (MutableShapeClass) this .shapeClasses .get( shape );
        shapeSet .replacePositions( OpenGlSceneLoader .createPositionsArray( shapeSet .instances, MODEL_SCALE_FACTOR ) );
    }

	@Override
	public void colorChanged( RenderedManifestation manifestation )
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void locationChanged( RenderedManifestation manifestation )
	{
	}

	@Override
	public void orientationChanged( RenderedManifestation manifestation )
	{
	}

	@Override
	public void shapeChanged( RenderedManifestation manifestation )
	{
	}

    @Override
    public void propertyChange( PropertyChangeEvent evt )
    {
        switch ( evt .getPropertyName() ) {

        case "drawNormals":
            break;

        case "drawOutlines":
            break;

        case "showFrameLabels":
            break;

        case "showIcosahedralLabels":
            break;

        default:
            break;
        }
    }

}
