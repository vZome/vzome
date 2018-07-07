
//(c) Copyright 2005, Scott Vorthmann.  All rights reserved.

package com.vzome.desktop.controller;

import java.awt.Component;

import org.vorthmann.ui.Controller;

import com.vzome.core.render.RenderingChanges;

public interface Controller3d extends Controller
{
    void attachViewer( RenderingViewer viewer, RenderingChanges scene, Component canvas );
}
