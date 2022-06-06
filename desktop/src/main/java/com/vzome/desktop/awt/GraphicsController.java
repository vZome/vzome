package com.vzome.desktop.awt;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.MouseEvent;

import org.vorthmann.j3d.MouseTool;

import com.vzome.desktop.api.Controller;

public interface GraphicsController extends Controller
{
    void repaintGraphics( String panelName, Graphics graphics, Dimension size );

    boolean[] enableContextualCommands( String[] menu, MouseEvent e );
    
    MouseTool getMouseTool();

    void attachViewer( RenderingViewer viewer, Component canvas );
}
