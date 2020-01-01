package com.vzome.opengl;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class Scene implements Iterable<InstancedGeometry>
{
    private Set<InstancedGeometry> shapes = new HashSet<InstancedGeometry>();

	private float[][] orientations;
	
	private float[] background;

    public Scene( Set<InstancedGeometry> shapes, float[][] orientations )
    {
		super();
		
		// TODO: get rid of this constructor... too batchy
		this.shapes = shapes;
		this.orientations = orientations;
	}

    public float[][] getOrientations()
    {
		return this .orientations;
	}

	@Override
	public Iterator<InstancedGeometry> iterator()
	{
		return this .shapes .iterator();
	}
	
	public int numGeometries()
	{
		return this .shapes .size();
	}

    public float[] getBackground()
    {
        return background;
    }

    public void setBackground( float[] background )
    {
        this.background = background;
    }
}
