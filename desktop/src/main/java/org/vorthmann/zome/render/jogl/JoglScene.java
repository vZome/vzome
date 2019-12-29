
//(c) Copyright 2014, Scott Vorthmann.

package org.vorthmann.zome.render.jogl;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import com.jogamp.opengl.math.FloatUtil;
import com.vzome.core.render.Color;
import com.vzome.core.render.Colors;
import com.vzome.core.render.OpenGlSceneLoader;
import com.vzome.core.render.RenderedManifestation;
import com.vzome.core.render.RenderedModel;
import com.vzome.core.render.RenderingChanges;
import com.vzome.core.viewing.Lights;
import com.vzome.opengl.RenderingProgram;
import com.vzome.opengl.Scene;

public class JoglScene implements RenderingChanges, PropertyChangeListener
{
	private RenderedModel model;
    private final Colors colors;
    private Color bkgdColor;

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

    void render( RenderingProgram renderer, float[] camera, int width, int height )
    {
        Scene scene = OpenGlSceneLoader .getOpenGlScene( this .model, this .colors );
        float[] rgba = new float[4];
        this .bkgdColor .getRGBColorComponents( rgba );
        scene .setBackground( rgba );

        //        renderer .bindBuffers( gl, scene );
        
        float[] projection = new float[16];
        float[] objectTrans = new float[16];

        // Object first appears directly in front of user
        FloatUtil.makeIdentity( objectTrans );

        // Build the camera matrix and apply it to the ModelView.
//        FloatUtil.makeLookAt( camera, 0, new float[]{0f, 0f, 4f}, 0, new float[]{0f, 0f, 0f}, 0, new float[]{0f, 1f, 0f}, 0, new float[16] );
        
        float aspectRatio = (float) width / (float) height;
        FloatUtil.makePerspective( projection, 0, true, 0.4f, aspectRatio, 0.1f, 1000f );

        renderer .setOrientations( scene .getOrientations() );
        renderer .setUniforms( objectTrans, camera, projection );
        renderer .renderScene( scene );
    }

	@Override
	public void reset()
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void manifestationAdded( RenderedManifestation manifestation )
	{
		this .model = manifestation .getModel();
	}

	@Override
	public void manifestationRemoved( RenderedManifestation manifestation )
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void manifestationSwitched( RenderedManifestation from, RenderedManifestation to )
	{
		// TODO Auto-generated method stub

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
		// TODO Auto-generated method stub

	}

	@Override
	public void orientationChanged( RenderedManifestation manifestation )
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void shapeChanged( RenderedManifestation manifestation )
	{
		// TODO Auto-generated method stub

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
