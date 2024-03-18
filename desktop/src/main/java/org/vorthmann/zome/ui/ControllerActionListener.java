package org.vorthmann.zome.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import com.vzome.desktop.api.Controller;
import com.vzome.desktop.controller.DefaultController;

public class ControllerActionListener implements ActionListener
{
    private final DefaultController controller;

    public ControllerActionListener( Controller controller )
    {
        this.controller = (DefaultController) controller;
    }

    @Override
    public void actionPerformed( ActionEvent e )
    {
        controller .actionPerformed( e .getSource(), e .getActionCommand() );
    }

}
