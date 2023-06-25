
package com.vzome.desktop.awt;

import java.awt.Component;
import java.awt.event.MouseWheelEvent;

import org.vorthmann.j3d.CanvasTool;
import org.vorthmann.j3d.MouseTool;
import org.vorthmann.j3d.MouseToolDefault;

import com.vzome.desktop.controller.LengthController;

/**
 * Because of MOUSE_WHEEL_GAIN issues, this is more a model of the length panel than an actual length scalar value.
 *
 */
public class LengthCanvasTool implements CanvasTool
{    
    private final MouseTool tool;

    private static final int MOUSE_WHEEL_GAIN = 4;

    public LengthCanvasTool( LengthController controller )
    {
        this .tool = new MouseToolDefault()
        {
            int wheelClicks = 0;
           
            /**
             * Simply dividing the roll amt MOUSE_WHEEL_GAIN would be insufficient, because then
             *  wheeling slowing and precisely might never get above 0.  I though perhaps a minimum scale change of +/-1
             *  on any roll might accomplish the right thing, but then it is not possible to wheel
             *  slowly enough.
             * By keeping an internal state (wheelClicks), and applying MOUSE_WHEEL_GAIN,
             * we can generate courser grained scale changes without those unnatural effects.
             */
            @Override
            public void mouseWheelMoved( MouseWheelEvent e )
            {
                int amt = e .getWheelRotation();
                int oldScaled = wheelClicks / MOUSE_WHEEL_GAIN;
                wheelClicks = wheelClicks + amt;
                int newScaled = wheelClicks / MOUSE_WHEEL_GAIN;
                
                if ( oldScaled != newScaled )
                    // don't want to generate change events when there is no change
                    controller .setScale( controller .getScale() - newScaled + oldScaled  ); // reverse the sense of the wheel,
                    // since mouseWheel clicks are set up for scrollbars
            }
        };
    }

    public MouseTool getMouseTool()
    {
        return this .tool;
    }

    @Override
    public void attach( Component canvas )
    {
        this .tool .attach( canvas );
    }

    @Override
    public void detach( Component canvas )
    {
        this .tool .detach( canvas );
    }
}
