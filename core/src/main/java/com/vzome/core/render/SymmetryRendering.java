package com.vzome.core.render;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.vzome.core.algebra.AlgebraicField;
import com.vzome.core.algebra.AlgebraicMatrix;
import com.vzome.core.algebra.AlgebraicVector;
import com.vzome.core.math.Polyhedron;
import com.vzome.core.math.RealVector;
import com.vzome.core.math.symmetry.Embedding;
import com.vzome.core.math.symmetry.Symmetry;
import com.vzome.core.render.RenderedModel.OrbitSource;
import com.vzome.opengl.InstancedGeometry;

public class SymmetryRendering implements RenderingChanges
{
    private final float[][] orientations;
    private final Map<Polyhedron, InstancedGeometry> geometries = new HashMap<Polyhedron, InstancedGeometry>();
    private final float globalScale;
    private final OrbitSource orbits;
    
    private static final Embedding TRIVIAL_EMBEDDING = new Embedding.Trivial();

    public SymmetryRendering( OrbitSource orbits, float globalScale )
    {
        this .orbits = orbits;
        this .globalScale = globalScale;
        Embedding embedding = orbits .getSymmetry();

        Symmetry symmetry = orbits .getSymmetry();
        AlgebraicField field = symmetry .getField();
        int order = symmetry .getChiralOrder();
        this .orientations = new float[order][];
        for ( int orientation = 0; orientation < order; orientation++ )
        {
            float[] asFloats = new float[ 16 ];
            AlgebraicMatrix transform = symmetry .getMatrix( orientation );
            for ( int i = 0; i < 3; i++ )
            {
                AlgebraicVector columnSelect = field .basisVector( 3, i );
                AlgebraicVector columnI = transform .timesColumn( columnSelect );
                RealVector colRV = embedding .embedInR3( columnI );
                asFloats[ i*4+0 ] = (float) colRV.x;
                asFloats[ i*4+1 ] = (float) colRV.y;
                asFloats[ i*4+2 ] = (float) colRV.z;
                asFloats[ i*4+3 ] = 0f;
            }
            asFloats[ 12 ] = 0f;
            asFloats[ 13 ] = 0f;
            asFloats[ 14 ] = 0f;
            asFloats[ 15 ] = 1f;
            this .orientations[ orientation ] = asFloats;
        }
    }

    public SymmetryRendering( RenderedModel renderedModel, float globalScale )
    {
        this( renderedModel .getOrbitSource(), globalScale );
        
        for ( RenderedManifestation rm : renderedModel ) {
            this .manifestationAdded( rm );
        }
    }
    
    public void pick( ShapeAndInstances.Intersector intersector )
    {
        for ( InstancedGeometry geometry : this .geometries .values()) {
            ShapeAndInstances shapeAndInstances = (ShapeAndInstances) geometry;
            shapeAndInstances .pick( intersector, this .orientations );
        }
    }

    public float[][] getOrientations()
    {
        return this .orientations;
    }

    public Iterable<InstancedGeometry> getGeometries()
    {
        return this .geometries .values();
    }

    @Override
    public boolean shapesChanged( Shapes shapes )
    {
        for ( InstancedGeometry geometry : this .geometries .values() ) {
            ShapeAndInstances shapesAndInstances = ((ShapeAndInstances) geometry);
            if ( shapesAndInstances .getShape() .isPanel() )
                continue; // no change necessary
            Collection<RenderedManifestation> instances = shapesAndInstances .getInstances();
            Polyhedron shape = null;
            for ( RenderedManifestation rm : instances ) {
                rm .resetAttributes( this .orbits, shapes, false, true );
                shape = rm .getShape(); // they are all the same shape!
            }
            ShapeAndInstances newShapesAndInstances = this .getShapeAndInstances( shape );
            for ( RenderedManifestation rm : instances ) {
                newShapesAndInstances .addInstance( rm );
            }
            shapesAndInstances .removeInstances();
            shapesAndInstances = newShapesAndInstances;
        }
        return true;
    }
    
    private ShapeAndInstances getShapeAndInstances( Polyhedron shape )
    {
        ShapeAndInstances shapesAndInstances = (ShapeAndInstances) this .geometries .get( shape );
        if ( shapesAndInstances == null ) {
            shapesAndInstances = new ShapeAndInstances( shape, this .globalScale );
            this .geometries .put( shape, shapesAndInstances );
        }
        return shapesAndInstances;
    }

    @Override
    public void manifestationAdded( RenderedManifestation rm )
    {
        Polyhedron shape = rm .getShape();
        ShapeAndInstances shapesAndInstances = this .getShapeAndInstances( shape );
        shapesAndInstances .addInstance( rm );
    }

    @Override
    public void reset()
    {
        for ( InstancedGeometry geometry : this .geometries .values() ) {
            ((ShapeAndInstances) geometry) .removeInstances();
        }
    }

    public void refresh()
    {
        for ( InstancedGeometry geometry : this .geometries .values() ) {
            ((ShapeAndInstances) geometry) .rebuildInstanceData();
        }
    }

    @Override
    public void manifestationRemoved( RenderedManifestation rm )
    {
        Polyhedron shape = rm .getShape();
        ShapeAndInstances shapesAndInstances = (ShapeAndInstances) this .geometries .get( shape );
        if ( shapesAndInstances != null )
            shapesAndInstances .removeInstance( rm );
    }

    @Override
    public void manifestationSwitched( RenderedManifestation from, RenderedManifestation to )
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void glowChanged( RenderedManifestation rm )
    {
        Polyhedron shape = rm .getShape();
        ShapeAndInstances shapesAndInstances = (ShapeAndInstances) this .geometries .get( shape );
        shapesAndInstances .rebuildInstanceData();
    }

    @Override
    public void colorChanged( RenderedManifestation rm )
    {
        // TODO Auto-generated method stub
    }

    @Override
    public void locationChanged( RenderedManifestation rm )
    {
        // TODO Auto-generated method stub
    }

    @Override
    public void orientationChanged( RenderedManifestation rm )
    {
        // TODO Auto-generated method stub
    }

    @Override
    public void shapeChanged( RenderedManifestation rm )
    {
        // TODO Auto-generated method stub
    }

    public int numGeometries()
    {
        return this .geometries .size();
    }
}