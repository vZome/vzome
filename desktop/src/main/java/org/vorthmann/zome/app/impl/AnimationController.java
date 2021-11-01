package org.vorthmann.zome.app.impl;

public interface AnimationController
{
    int getImageSize();
    
    boolean finished();

    void rotate();
}