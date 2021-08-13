package org.vorthmann.ui;

import java.awt.Component;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;

import javax.swing.event.MouseInputAdapter;
import javax.swing.event.MouseInputListener;

import org.vorthmann.j3d.MouseTool;

/**
 * A MouseInputAdapter that separates press-release sequences into pure clicks and
 * press-drag-release sequences.
 * Press-release-click sequences are never generated.
 * Drag events (and the initial press) are not generated until HYSTERESIS msec have passed since the actual press.
 * If the mouse is released before that time has passed, a clicked event is generated.
 * 
 * Thus, even if the mouse is moving, press-release within HYSTERESIS msec becomes a click event,
 * with no drag events.
 * 
 * This class also excludes "popup trigger" events.
 * 
 * @author Scott Vorthmann
 *
 */
public class LeftMouseDragAdapter extends MouseInputAdapter implements MouseTool
{
    private static final long HYSTERESIS = 200; // milliseconds
    
    private MouseInputListener mDelegate;
    
    private transient long mPressTime;
    
    private transient MouseEvent mPressEvent;
    
    private transient boolean mPressSent, mPopupTrigger;
    
    private final long hysteresis;

    public LeftMouseDragAdapter( MouseInputListener drags, long hysteresis )
    {
        super();
        
        mDelegate = drags;
        this .hysteresis = hysteresis;
    }

    public LeftMouseDragAdapter( MouseInputListener drags )
    {
        this( drags, HYSTERESIS );
    }

    @Override
    public void mousePressed( MouseEvent e )
    {
        if ( e .isPopupTrigger() ) {
            mPopupTrigger = true;
            return;
        }
        
        mPopupTrigger = false;
        mPressTime = System .currentTimeMillis();
        mPressEvent = e;
    }

    @Override
    public void mouseDragged( MouseEvent arg0 )
    {
        if ( arg0 .isPopupTrigger() || mPopupTrigger )
            return;
        
        long time = System .currentTimeMillis();
        if ( time - mPressTime > this .hysteresis ) {
            if ( mPressEvent != null ) {
                mDelegate .mousePressed( mPressEvent ); // TODO massage this into a press event
                mPressEvent = null;
                mPressSent = true;
            }
            mDelegate .mouseDragged( arg0 );
        }
    }

    @Override
    public void mouseReleased( MouseEvent arg0 )
    {
        if ( arg0 .isPopupTrigger() || mPopupTrigger )
            return;
        
        if ( mPressSent )
            mDelegate .mouseReleased( arg0 );
        else
            mDelegate .mouseClicked( arg0 ); // TODO massage this into a click event
        mPressSent = false;
    }

    @Override
    public void mouseClicked( MouseEvent arg0 )
    {
        // all clicks generated in mouseReleased
    }

    @Override
    public void mouseWheelMoved( MouseWheelEvent arg0 )
    {}

    @Override
    public void attach( Component canvas )
    {
        canvas .addMouseListener( this );
        canvas .addMouseMotionListener( this );
        canvas .addMouseWheelListener( this );
    }
    
    @Override
    public void detach( Component canvas )
    {
        canvas .removeMouseListener( this );
        canvas .removeMouseMotionListener( this );
        canvas .removeMouseWheelListener( this );
    }

}
