package org.vorthmann.j3d;

import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;


/**
 * Description here.
 * 
 * @author Scott Vorthmann 2003
 */
public class MouseListenerSwitch extends MouseAdapter implements MouseTool {

    protected MouseTool first, second;
    
    protected MouseEvent lastPosition;
    
    protected boolean dragging = false, wasSecond = false;
    
	public MouseListenerSwitch() {}

	public void setFirst( MouseTool listener )
	{
	    first = listener;
	}
	
	public void setSecond( MouseTool listener )
	{
	    second = listener;
	}
	
	public synchronized void toggle()
	{
	    boolean inDrag = dragging && lastPosition != null;
	    if ( inDrag )
	        mouseReleased( lastPosition );
		MouseTool temp = first;
		first = second;
		second = temp;
		if ( inDrag )
		    mousePressed( lastPosition );
	}
	

    @Override
    public void attach( Component component )
    {
        component .addMouseListener( this );
		component .addMouseMotionListener( this );
		component .addMouseWheelListener( this );
    }
    
    @Override
    public void detach( Component component )
    {
        component .removeMouseListener( this );
		component .removeMouseMotionListener( this );
		component .removeMouseWheelListener( this );
    }

    protected static boolean isSecondButton( MouseEvent e )
    {
        return ( e .getModifiersEx() & ( MouseEvent.BUTTON2_DOWN_MASK | MouseEvent.BUTTON3_DOWN_MASK ) ) != 0;
    }
    
    @Override
    public void mouseClicked( MouseEvent e )
    {
		lastPosition = e;
		if ( isSecondButton( e ) )
			second .mouseClicked( e );
		else
		    first .mouseClicked( e );
    }

    @Override
    public  void mousePressed( MouseEvent e )
    {
        lastPosition = e;
        dragging = true;
        wasSecond = isSecondButton( e );
		if ( wasSecond )
			second .mousePressed( e );
		else
		    first .mousePressed( e );
	}

    @Override
    public  void mouseDragged( MouseEvent e )
    {
		lastPosition = e;
		dragging = true;
		boolean wasFirst = ! wasSecond;
		wasSecond = isSecondButton( e );
		if ( wasSecond ) {
		    if ( wasFirst ) {
				first .mouseReleased( lastPosition );
				second .mousePressed( lastPosition );
		    }
			second .mouseDragged( e );
		}
		else {
		    if ( ! wasFirst ) {
				second .mouseReleased( lastPosition );
				first .mousePressed( lastPosition );
			}
			first .mouseDragged( e );
		}
	}

    @Override
	public  void mouseMoved( MouseEvent e )
	{
		lastPosition = e;
		first .mouseMoved( e );
	}

    @Override
	public  void mouseReleased( MouseEvent e )
	{
		lastPosition = e;
		dragging = false;
		boolean wasFirst = ! wasSecond;
		wasSecond = isSecondButton( e );
		if ( wasSecond ) {
			if ( wasFirst ) {
			    first .mouseReleased( lastPosition );
			    second .mousePressed( lastPosition );
			}
			second .mouseReleased( e );
		}
		else {
			if ( wasFirst ) {
				second .mouseReleased( lastPosition );
				first .mousePressed( lastPosition );
			}
			first .mouseReleased( e );
		}
	}

    /* (non-Javadoc)
     * @see java.awt.event.MouseWheelListener#mouseWheelMoved(java.awt.event.MouseWheelEvent)
     */
    @Override
    public void mouseWheelMoved( MouseWheelEvent e )
    {
        wasSecond = isSecondButton( e );
        if ( wasSecond )
            second .mouseWheelMoved( e );
        else
            first .mouseWheelMoved( e );
    }
    
    

}
