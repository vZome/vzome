package com.vzome.opengl;

import java.nio.FloatBuffer;

public interface InstancedGeometry {

    FloatBuffer getVertices();

    FloatBuffer getNormals();

    FloatBuffer getPositions();

    FloatBuffer getColors();

    int getVertexCount();

    int getInstanceCount();

    boolean usesVBOs();

    int getVerticesVBO();

    int getNormalsVBO();

    int getPositionsVBO();

    int getColorsVBO();

    void setBuffers( int verticesId, int normalsId, int positionsId, int colorsId );
}