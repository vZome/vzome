package org.vorthmann.zome.ui;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import org.vorthmann.ui.Controller;

final class ContextualMenuMouseListener extends MouseAdapter
{
    private final Controller controller;

    private final PickerPopup pickerPopup;

    ContextualMenuMouseListener( Controller controller, PickerPopup pickerPopup )
    {
        this.controller = controller;
        this.pickerPopup = pickerPopup;
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
            pickerPopup.enableActions( controller, e );
            pickerPopup.show( e.getComponent(), e.getX(), e.getY() );
        }
    }
}