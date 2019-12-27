package com.vzome.opengl;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class Scene implements Iterable<ShapeClass>
{
    private Set<ShapeClass> shapes = new HashSet<ShapeClass>();

	private float[][] orientations;

    public Scene( Set<ShapeClass> shapes, float[][] orientations )
    {
		super();
		this.shapes = shapes;
		this.orientations = orientations;
	}

    public float[][] getOrientations()
    {
		return this .orientations;
	}

	@Override
	public Iterator<ShapeClass> iterator()
	{
		return this .shapes .iterator();
	}
	
	public int numShapeClasses()
	{
		return this .shapes .size();
	}
}
