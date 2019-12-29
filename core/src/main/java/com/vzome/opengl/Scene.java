package com.vzome.opengl;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class Scene implements Iterable<ShapeClass>
{
    private Set<ShapeClass> shapes = new HashSet<ShapeClass>();

	private float[][] orientations;
	
	private float[] background;

    public Scene( Set<ShapeClass> shapes, float[][] orientations )
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
	public Iterator<ShapeClass> iterator()
	{
		return this .shapes .iterator();
	}
	
	public int numShapeClasses()
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
