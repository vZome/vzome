/*
 * Created on Dec 7, 2003
 */
package org.vorthmann.j3d;

import java.awt.event.MouseEvent;

/**
 * @author vorth
 */
public class MouseListenerSwitch14 extends MouseListenerSwitch
{
	protected boolean firstButton;

    @Override
	public void mouseClicked( MouseEvent e )
	{
 	    if ( e .getButton() == MouseEvent .BUTTON1 )
	        first .mouseClicked( e );
		else
		    second .mouseClicked( e );
	}

    @Override
	public  void mousePressed( MouseEvent e )
	{
		lastPosition = e;
		dragging = true;
		firstButton = e .getButton() == MouseEvent .BUTTON1;
		if ( firstButton )
			first .mousePressed( e );
		else
			second .mousePressed( e );
	}

    @Override
	public  void mouseDragged( MouseEvent e )
	{
		lastPosition = e;
		dragging = true;
		if ( firstButton )
			first .mouseDragged( e );
		else
			second .mouseDragged( e );
	}

    @Override
	public  void mouseMoved( MouseEvent e )
	{
		if ( firstButton )
			first .mouseMoved( e );
		else
			second .mouseMoved( e );
	}

    @Override
	public  void mouseReleased( MouseEvent e )
	{
		lastPosition = e;
		dragging = false;
		if ( firstButton )
			first .mouseReleased( e );
		else
			second .mouseReleased( e );
	}

}
