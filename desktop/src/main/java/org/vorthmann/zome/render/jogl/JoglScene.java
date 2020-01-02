
//(c) Copyright 2014, Scott Vorthmann.

package org.vorthmann.zome.render.jogl;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashMap;
import java.util.Map;

import com.vzome.core.render.Color;
import com.vzome.core.render.RenderedManifestation;
import com.vzome.core.render.RenderedManifestation.Intersector;
import com.vzome.core.render.RenderingChanges;
import com.vzome.core.render.SymmetryRendering;
import com.vzome.core.viewing.Lights;
import com.vzome.opengl.RenderingProgram;

public class JoglScene implements RenderingChanges, PropertyChangeListener
{
    private Color bkgdColor;
    private final Map<String, SymmetryRendering> symmetries = new HashMap<>();

    private static final float MODEL_SCALE_FACTOR = 2f; // this seems to align with the way Java3d rendering came out

    JoglScene( Lights lights, boolean isSticky )
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
            }
        });
	}

    void render( RenderingProgram renderer )
    {
        float[] rgba = new float[4];
        this .bkgdColor .getRGBColorComponents( rgba );
        renderer .clear( rgba );

        for ( SymmetryRendering symmetry : this .symmetries .values() ) {
            // Just render them all; no harm in mixing, and little cost for empty ones
            renderer .renderSymmetry( symmetry );
        }
    }

	@Override
	public void reset()
	{
        for ( SymmetryRendering symmetryRendering : this .symmetries .values() ) {
            symmetryRendering .reset();
        }
	}

	@Override
	public void manifestationAdded( RenderedManifestation rm )
	{
	    String symmetryName = rm .getModel() .getOrbitSource() .getSymmetry() .getName();
	    SymmetryRendering symmetryRendering = this .symmetries .get( symmetryName );
	    if ( symmetryRendering == null ) {
	        symmetryRendering = new SymmetryRendering( rm .getModel() .getOrbitSource(), MODEL_SCALE_FACTOR );
	        this .symmetries .put( symmetryName, symmetryRendering );
	    }
	    this .symmetries .get( symmetryName ) .manifestationAdded( rm );
	}

	@Override
	public void manifestationRemoved( RenderedManifestation rm )
	{
        for ( SymmetryRendering symmetryRendering : this .symmetries .values() ) {
            symmetryRendering .manifestationRemoved( rm ); // this will be a no-op in all but one
        }
    }

	@Override
	public void manifestationSwitched( RenderedManifestation from, RenderedManifestation to )
	{
	}

	@Override
	public void glowChanged( RenderedManifestation rm )
	{
        String symmetryName = rm .getModel() .getOrbitSource() .getSymmetry() .getName();
        this .symmetries .get( symmetryName ) .glowChanged( rm );
    }

	@Override
	public void colorChanged( RenderedManifestation rm )
	{
        String symmetryName = rm .getModel() .getOrbitSource() .getSymmetry() .getName();
        this .symmetries .get( symmetryName ) .colorChanged( rm );
    }

	@Override
	public void locationChanged( RenderedManifestation rm )
	{
        String symmetryName = rm .getModel() .getOrbitSource() .getSymmetry() .getName();
        this .symmetries .get( symmetryName ) .locationChanged( rm );
    }

	@Override
	public void orientationChanged( RenderedManifestation rm )
	{
        String symmetryName = rm .getModel() .getOrbitSource() .getSymmetry() .getName();
        this .symmetries .get( symmetryName ) .orientationChanged( rm );
    }

	@Override
	public void shapeChanged( RenderedManifestation rm )
	{
        String symmetryName = rm .getModel() .getOrbitSource() .getSymmetry() .getName();
        this .symmetries .get( symmetryName ) .shapeChanged( rm );
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

    public RenderedManifestation pick( Intersector intersector )
    {
        for ( SymmetryRendering symmetryRendering : this .symmetries .values() ) {
            RenderedManifestation rm = symmetryRendering .pick( intersector ); // this will be a no-op in all but one
            if ( rm != null )
                return rm;
        }
        return null;
    }

}
