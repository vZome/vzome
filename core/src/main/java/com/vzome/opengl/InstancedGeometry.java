package com.vzome.opengl;

import java.nio.FloatBuffer;

public interface InstancedGeometry {

    FloatBuffer getVertices();

    FloatBuffer getNormals();

    FloatBuffer getPositions();

    float[] getColor();

    int getVertexCount();

    int getInstanceCount();

    boolean usesVBOs();

    int getVerticesVBO();

    int getNormalsVBO();

    int getPositionsVBO();

    void setBuffers( int i, int j, int k );
}