package com.vzome.opengl;

import java.nio.FloatBuffer;

public interface InstancedGeometry {

    int getVertexCount();

    int getLineVertexCount();

    int getVerticesVBO();

    int getNormalsVBO();

    int getPositionsVBO();

    int getColorsVBO();
    
    int getLineVerticesVBO();

    int prepareToRender( BufferStorage storage );

    public interface BufferStorage
    {
        int storeBuffer( FloatBuffer buffer, int oldId );
    }

    FloatBuffer getLineVertices();

    FloatBuffer getPositions();

    FloatBuffer getVertices();

    FloatBuffer getNormals();

    FloatBuffer getColors();
}