package com.vzome.opengl;

import java.nio.FloatBuffer;

public interface InstancedGeometry {

    int getVertexCount();

    int getVerticesVBO();

    int getNormalsVBO();

    int getPositionsVBO();

    int getColorsVBO();

    int prepareToRender( BufferStorage storage );

    public interface BufferStorage
    {
        int storeBuffer( FloatBuffer buffer );
    }
}