package org.vorthmann.zome.ui;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import com.vzome.desktop.awt.GraphicsController;

final class ContextualMenuMouseListener extends MouseAdapter
{
    private final GraphicsController controller;

    private final ContextualMenu popupMenu;

    ContextualMenuMouseListener( GraphicsController controller, ContextualMenu popupMenu )
    {
        this.controller = controller;
        this.popupMenu = popupMenu;
    }

	@Override
    public void mousePressed( MouseEvent e )
    {
        maybeShowPopup( e );
    }

	@Override
    public void mouseReleased( MouseEvent e )
    {
        maybeShowPopup( e );
    }

    private void maybeShowPopup( MouseEvent e )
    {
        if ( e.isPopupTrigger() ) {
            popupMenu .enableActions( controller, e );
            popupMenu .show( e.getComponent(), e.getX(), e.getY() );
        }
    }
}