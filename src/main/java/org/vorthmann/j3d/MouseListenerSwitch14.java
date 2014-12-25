/*
 * Created on Dec 7, 2003
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package org.vorthmann.j3d;

import java.awt.event.MouseEvent;

/**
 * @author vorth
 *
 * To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public class MouseListenerSwitch14 extends MouseListenerSwitch
{
	protected boolean firstButton;

	public void mouseClicked( MouseEvent e )
	{
 	    if ( e .getButton() == MouseEvent .BUTTON1 )
	        first .mouseClicked( e );
		else
		    second .mouseClicked( e );
	}

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

	public  void mouseDragged( MouseEvent e )
	{
		lastPosition = e;
		dragging = true;
		if ( firstButton )
			first .mouseDragged( e );
		else
			second .mouseDragged( e );
	}

	public  void mouseMoved( MouseEvent e )
	{
		if ( firstButton )
			first .mouseMoved( e );
		else
			second .mouseMoved( e );
	}

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
