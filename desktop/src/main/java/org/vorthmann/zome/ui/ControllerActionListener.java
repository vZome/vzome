package org.vorthmann.zome.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import org.vorthmann.ui.DefaultController;

import com.vzome.desktop.api.Controller;

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
