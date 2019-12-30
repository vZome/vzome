
//(c) Copyright 2014, Scott Vorthmann.

package org.vorthmann.zome.render.jogl;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import com.vzome.core.render.Color;
import com.vzome.core.render.Colors;
import com.vzome.core.render.OpenGlSceneLoader;
import com.vzome.core.render.RenderedManifestation;
import com.vzome.core.render.RenderingChanges;
import com.vzome.core.viewing.Lights;
import com.vzome.opengl.RenderingProgram;
import com.vzome.opengl.Scene;

public class JoglScene implements RenderingChanges, PropertyChangeListener
{
    private final Colors colors;
    private Color bkgdColor;
    private static final float MODEL_SCALE_FACTOR = 3.5f; // this seems to align with the way Java3d rendering came out
    private Scene scene;
    private float[][] orientations;
    
    JoglScene( Lights lights, Colors colors, boolean isSticky )
	{
        this.colors = colors;
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
        if ( scene != null ) {
            //        renderer .bindBuffers( gl, scene );
            
            if ( this.orientations == null ) {
                this .orientations = scene .getOrientations();
                renderer .setOrientations( this .orientations );
            }
            float[] rgba = new float[4];
            this .bkgdColor .getRGBColorComponents( rgba );
            scene .setBackground( rgba );
            renderer .renderScene( scene );
        }
    }

	@Override
	public void reset()
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void manifestationAdded( RenderedManifestation manifestation )
	{
        scene = OpenGlSceneLoader .getOpenGlScene( manifestation .getModel(), this .colors, MODEL_SCALE_FACTOR );
	}

	@Override
	public void manifestationRemoved( RenderedManifestation manifestation )
	{
        scene = OpenGlSceneLoader .getOpenGlScene( manifestation .getModel(), this .colors, MODEL_SCALE_FACTOR );
	}

	@Override
	public void manifestationSwitched( RenderedManifestation from, RenderedManifestation to )
	{
        scene = OpenGlSceneLoader .getOpenGlScene( to .getModel(), this .colors, MODEL_SCALE_FACTOR );
	}

	@Override
	public void glowChanged( RenderedManifestation manifestation )
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void colorChanged( RenderedManifestation manifestation )
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void locationChanged( RenderedManifestation manifestation )
	{
        scene = OpenGlSceneLoader .getOpenGlScene( manifestation .getModel(), this .colors, MODEL_SCALE_FACTOR );
	}

	@Override
	public void orientationChanged( RenderedManifestation manifestation )
	{
        scene = OpenGlSceneLoader .getOpenGlScene( manifestation .getModel(), this .colors, MODEL_SCALE_FACTOR );
	}

	@Override
	public void shapeChanged( RenderedManifestation manifestation )
	{
        scene = OpenGlSceneLoader .getOpenGlScene( manifestation .getModel(), this .colors, MODEL_SCALE_FACTOR );
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
