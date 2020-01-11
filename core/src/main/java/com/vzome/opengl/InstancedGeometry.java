package com.vzome.opengl;

import java.nio.FloatBuffer;

public interface InstancedGeometry {

    int getVertexCount();

    int getLineVertexCount();
    
    int getLineVerticesVBO();

    int getVerticesVBO();

    int getNormalsVBO();

    int getInstancesVBO();

    int getColorsVBO();

    int prepareToRender( BufferStorage storage );

    public interface BufferStorage
    {
        int storeBuffer( FloatBuffer buffer, int oldId );
    }

    FloatBuffer getLineVerticesBuffer();

    FloatBuffer getVerticesBuffer();

    FloatBuffer getNormalsBuffer();

    FloatBuffer getInstancesBuffer();

    FloatBuffer getColorsBuffer();
}