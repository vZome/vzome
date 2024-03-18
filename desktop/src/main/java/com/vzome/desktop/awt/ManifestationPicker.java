
package com.vzome.desktop.awt;

import java.awt.event.MouseEvent;

import org.vorthmann.j3d.MouseToolDefault;

import com.vzome.core.model.Manifestation;
import com.vzome.core.render.RenderedManifestation;

/**
 * Transducer: turns mouse events into pick events on visible Manifestations
 * 
 * @author Scott Vorthmann
 *
 */
public class ManifestationPicker extends MouseToolDefault
{
    private final RenderingViewer viewer;

    public ManifestationPicker( RenderingViewer viewer )
    {
        this .viewer = viewer;
    }

    @Override
    public void mousePressed( MouseEvent e )
    {
        super.mousePressed( e );

        Manifestation target = pick( e );
        dragStarted( target, ( e .getModifiersEx() & MouseEvent.SHIFT_DOWN_MASK ) != 0 );
    }

    protected void dragStarted( Manifestation target, boolean b )
    {}

    private Manifestation pick( MouseEvent e )
    {
        RenderedManifestation rm = viewer .pickManifestation( e );
        Manifestation targetManifestation = null;
        if ( rm != null && rm.isPickable() )
            targetManifestation = rm.getManifestation();
        return targetManifestation;
    }

    @Override
    public void mouseReleased( MouseEvent e )
    {
        Manifestation target = pick( e );
        dragFinished( target, ( e .getModifiersEx() & MouseEvent.SHIFT_DOWN_MASK ) != 0 );
    }

    protected void dragFinished( Manifestation target, boolean b )
    {}

    @Override
    public void mouseClicked( MouseEvent e )
    {
        Manifestation target = pick( e );
        manifestationPicked( target, ( e .getModifiersEx() & MouseEvent.SHIFT_DOWN_MASK ) != 0 );
    }

    protected void manifestationPicked( Manifestation target, boolean b )
    {}
}
