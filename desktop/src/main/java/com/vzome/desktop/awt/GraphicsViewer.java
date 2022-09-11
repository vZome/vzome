package com.vzome.desktop.awt;

import java.awt.Component;
import java.awt.image.BufferedImage;

/**
 * This is for just 2D rendering.
 * 
 * @author vorth
 *
 */
public interface GraphicsViewer
{
    Component getCanvas();

    BufferedImage captureImage(int maxSize, boolean withAlpha);
}