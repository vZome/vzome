
package com.vzome.desktop.controller;

import java.awt.Component;
import java.awt.event.MouseEvent;
import java.awt.image.RenderedImage;

import javax.vecmath.Point3d;

import org.vorthmann.j3d.J3dComponentFactory;

import com.vzome.core.render.ManifestationPicker;
import com.vzome.core.render.RenderingChanges;
import com.vzome.core.viewing.Lights;

public interface RenderingViewer extends ViewPlatformModel.Viewer, ManifestationPicker
{
    void pickPoint( MouseEvent e, Point3d imagePt, Point3d eyePt );
	
    com.vzome.core.render.RenderingChanges getRenderingChanges();
    
//    org.vorthmann.zome.scenegraph.Factory getSceneGraphFactory();
    
    void captureImage( int maxSize, ImageCapture capture );

    public interface ImageCapture
    {
        void captureImage( RenderedImage image );
    }
    
    public interface Factory extends J3dComponentFactory
    {
        RenderingViewer createRenderingViewer( RenderingChanges scene, Component canvas );
        
        RenderingChanges createRenderingChanges( Lights lights, boolean isSticky );
    }

//    Group getSceneGraphRoot();
}