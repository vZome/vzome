/*
 * Created on Jun 25, 2003
 */
package com.vzome.core.viewing;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.vzome.core.algebra.AlgebraicNumber;
import com.vzome.core.construction.Color;
import com.vzome.core.math.Polyhedron;
import com.vzome.core.math.symmetry.Direction;
import com.vzome.core.math.symmetry.Symmetry;
import com.vzome.core.parts.FastDefaultStrutGeometry;
import com.vzome.core.parts.StrutGeometry;
import com.vzome.core.render.Shapes;


public abstract class AbstractShapes implements Shapes
{
    private final Map<Direction, Map<AlgebraicNumber, Polyhedron> > strutShapesByLengthAndOrbit = new HashMap<>();

    private final Map<Direction, StrutGeometry> strutGeometriesByOrbit = new HashMap<>();

    protected final String mPkgName;

    final String mName, alias;

    protected final Symmetry mSymmetry;

    protected Polyhedron mConnectorGeometry;

    public AbstractShapes( String pkgName, String name, String alias, Symmetry symm )
    {
        mPkgName = pkgName;
        mName = name;
        this .alias = alias;
        mConnectorGeometry = null;
        mSymmetry = symm;
    }

    public AbstractShapes( String pkgName, String name, Symmetry symm )
    {
        this( pkgName, name, null, symm );
    }

    @Override
    public Color getColor( Direction dir )
    {
        return null;
    }

    @Override
    public boolean hasColors()
    {
        return false;
    }

    protected StrutGeometry createStrutGeometry( Direction dir )
    {
        return new FastDefaultStrutGeometry( dir );
    }
    
    private StrutGeometry getStrutGeometry( Direction orbit )
    {
        StrutGeometry orbitStrutGeometry = strutGeometriesByOrbit.get( orbit );

        if ( orbitStrutGeometry == null ) {
            orbitStrutGeometry = createStrutGeometry( orbit );
            strutGeometriesByOrbit.put( orbit, orbitStrutGeometry );
        }
        return orbitStrutGeometry;
    }
    
    public Map<String, StrutGeometry> getStrutGeometries()
    {
        // Will lazy-populate strutGeometries
        return Arrays .stream( this .mSymmetry .getDirectionNames() )
                .collect( Collectors .toMap( Function.identity(),
                        name -> getStrutGeometry( this .mSymmetry .getDirection( name ) ) ) );
    }

    @Override
    public String getName()
    {
        return mName;
    }

    @Override
    @JsonIgnore
    public String getAlias()
    {
        return this.alias;
    }

    @Override
    @JsonIgnore
    public String getPackage()
    {
        return mPkgName;
    }

    @Override
    public Polyhedron getConnectorShape()
    {
        if ( mConnectorGeometry == null ) {
            mConnectorGeometry = buildConnectorShape( mPkgName );
            mConnectorGeometry .setName( "ball" );
        }
        return mConnectorGeometry;
    }

    protected abstract Polyhedron buildConnectorShape( String pkgName );

    /*
     * TODO:
     * 
     * I need a scheme that does not involve division to render struts.
     * 
     * This could be accomplished by NOT trying to reuse one polyhedron per length,
     * for all orientations in the orbit, since the VEF-exported strut model does not
     * insist upon scaling, but simply offsets the end of the strut.  This week
     * I experimented with making all StrutGeometries work that way, and it is feasible
     * (since ModeledShapes is no longer used anywhere).  This approach, however, means
     * making no use of the orientation to render; that could even be a speedup, hard to say.
     * It doesn't work for non-DefaultStrutGeometry, since it requires that the vertices of the
     * prototype shape be transformed first, to match the offset vector direction.
     * 
     * (Basically, I changed "length" below to "offset", everywhere, and removed the scaling
     * in the caller of this method.  See "normSquared" below. )
     * 
     * I think the simplest thing to do is to inverse-transform the offset vector into alignment
     * with the prototype for the orbit, and use the absolute offset to create a representative
     * Polyhedron for the orbit+length, while continuing to use the orientation when rendering.
     * This requires a bit of up-front determination of inverse orientations, but should not require
     * any matrix inversion.
     * 
     * For now I'm reverting to the usual behavior, but I want to come back and implement what I
     * just described above.
     */

    @Override
    public Polyhedron getStrutShape( Direction orbit, AlgebraicNumber length )
    {
        Map<AlgebraicNumber, Polyhedron> strutShapesByLength = strutShapesByLengthAndOrbit.get( orbit );
        if ( strutShapesByLength == null ) {
            strutShapesByLength = new HashMap<>();
            strutShapesByLengthAndOrbit.put( orbit, strutShapesByLength );
        }
        //        int[] normSquared = mSymmetry .getField() .dot( offset, offset );
        Polyhedron lengthShape = strutShapesByLength.get( length );
        if ( lengthShape == null ) {
            StrutGeometry orbitStrutGeometry = getStrutGeometry( orbit ); // may lazy-create the geometry
            lengthShape = orbitStrutGeometry .getStrutPolyhedron( length );
            strutShapesByLength.put( length, lengthShape );
            if ( lengthShape != null ) {
                lengthShape .setName( orbit .getName() + strutShapesByLength .size() );
                lengthShape .setOrbit( orbit );
                // reproduce the calculation in LengthModel .setActualLength()                
                lengthShape .setLength( orbit .getLengthInUnits( length ) );
            }
        }
        return lengthShape;
    }

    // no changes are ever generated

    @Override
    public void addListener( Changes changes )
    {}

    @Override
    public void removeListener( Changes changes )
    {}

    @Override
    @JsonIgnore
    public Symmetry getSymmetry()
    {
        return mSymmetry;
    }
}
