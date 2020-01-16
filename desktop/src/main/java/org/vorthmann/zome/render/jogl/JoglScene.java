
//(c) Copyright 2014, Scott Vorthmann.

package org.vorthmann.zome.render.jogl;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.Map;

import org.vorthmann.ui.Controller;

import com.vzome.core.render.Color;
import com.vzome.core.render.RenderedManifestation;
import com.vzome.core.render.RenderingChanges;
import com.vzome.core.render.ShapeAndInstances;
import com.vzome.core.render.Shapes;
import com.vzome.core.render.SymmetryRendering;
import com.vzome.core.viewing.Lights;
import com.vzome.opengl.Renderer;

public class JoglScene implements RenderingChanges, PropertyChangeListener
{
    private Color bkgdColor;
    private final Map<String, SymmetryRendering> symmetries = new HashMap<>();
    private int forceRender = 3; // double-buffering means we cannot simply use a boolean
    private boolean drawOutlines;

    private static final float MODEL_SCALE_FACTOR = 2f; // this seems to align with the way Java3d rendering came out

    JoglScene( Controller controller, Lights lights, boolean isSticky )
	{
        this.bkgdColor = lights .getBackgroundColor();
        
        this .drawOutlines = controller .propertyIsTrue( "drawOutlines" );
        
        lights .addPropertyListener( new PropertyChangeListener(){

            @Override
            public void propertyChange( PropertyChangeEvent chg )
            {
                if ( "backgroundColor" .equals( chg .getPropertyName() ) )
                {
                    int rgb =  Integer .parseInt( (String) chg .getNewValue(), 16 );
                    bkgdColor = new Color( rgb );
                    forceRender();
                }
            }
        });
	}

    void render( Renderer solids, Renderer outlines, boolean viewChanged )
    {
        if ( viewChanged )
            this .forceRender();

        if ( this .forceRender > 0 ) {
            float[] rgba = new float[4];
            this .bkgdColor .getRGBColorComponents( rgba );
            solids .clear( rgba );
            outlines .clear( rgba ); // should not really clear

            try {
                for (SymmetryRendering symmetry : this.symmetries.values()) {
                    // Just render them all; no harm in mixing, and little cost for empty ones
                    solids.renderSymmetry(symmetry);
                    if (this.drawOutlines)
                        outlines.renderSymmetry(symmetry);
                }
                --this.forceRender;
            } catch (ConcurrentModificationException ex) {
                // Rather than synchronizing these collections, just bail out
                // of the rendering loop if a ConcurrentModificationException happens.
                // Subsequent renderings will resolve any visual defect.
                this.forceRender();
                // log to stdErr for now, so we have an idea how often it happens
                System.err.println("Ignoring ConcurrentModificationException on thread: " 
                        + Thread.currentThread().toString());
                ex.printStackTrace();
            }
        }
    }

	@Override
	public void reset()
	{
        for ( SymmetryRendering symmetryRendering : this .symmetries .values() ) {
            symmetryRendering .reset();
        }
        this .forceRender();
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
        this .forceRender();
	}

	@Override
	public void manifestationRemoved( RenderedManifestation rm )
	{
        for ( SymmetryRendering symmetryRendering : this .symmetries .values() ) {
            symmetryRendering .manifestationRemoved( rm ); // this will be a no-op in all but one
        }
        this .forceRender();
    }

    @Override
    public boolean shapesChanged( Shapes shapes )
    {
        String symmetryName = shapes .getSymmetry() .getName();
        SymmetryRendering symmetryRendering = this .symmetries .get( symmetryName );
        boolean success = symmetryRendering .shapesChanged( shapes );
        if ( success )
            this .forceRender();
        return success;
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
        this .forceRender();
    }

	@Override
	public void colorChanged( RenderedManifestation rm )
	{
        String symmetryName = rm .getModel() .getOrbitSource() .getSymmetry() .getName();
        this .symmetries .get( symmetryName ) .colorChanged( rm );
        this .forceRender();
    }

	@Override
	public void locationChanged( RenderedManifestation rm )
	{
        String symmetryName = rm .getModel() .getOrbitSource() .getSymmetry() .getName();
        this .symmetries .get( symmetryName ) .locationChanged( rm );
        this .forceRender();
    }

	@Override
	public void orientationChanged( RenderedManifestation rm )
	{
        String symmetryName = rm .getModel() .getOrbitSource() .getSymmetry() .getName();
        this .symmetries .get( symmetryName ) .orientationChanged( rm );
        this .forceRender();
    }

	@Override
	public void shapeChanged( RenderedManifestation rm )
	{
        String symmetryName = rm .getModel() .getOrbitSource() .getSymmetry() .getName();
        this .symmetries .get( symmetryName ) .shapeChanged( rm );
        this .forceRender();
    }

    @Override
    public void propertyChange( PropertyChangeEvent evt )
    {
        switch ( evt .getPropertyName() ) {

        case "drawNormals":
            break;

        case "drawOutlines":
            drawOutlines = (Boolean) evt .getNewValue();
            this .forceRender();
            break;

        case "showFrameLabels":
            break;

        case "showIcosahedralLabels":
            break;

        default:
            break;
        }
    }

    private void forceRender()
    {
        this .forceRender = 3;  // 2 should suffice, but we do get flicker
    }

    public void pick( ShapeAndInstances.Intersector intersector )
    {
        for ( SymmetryRendering symmetryRendering : this .symmetries .values() )
            symmetryRendering .pick( intersector ); // this will be a no-op in all but one
    }
}
