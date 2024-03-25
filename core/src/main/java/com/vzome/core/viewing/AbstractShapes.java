/*
 * Created on Jun 25, 2003
 */
package com.vzome.core.viewing;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.vzome.core.algebra.AlgebraicMatrix;
import com.vzome.core.algebra.AlgebraicNumber;
import com.vzome.core.algebra.AlgebraicVector;
import com.vzome.core.construction.Color;
import com.vzome.core.editor.api.Shapes;
import com.vzome.core.math.Polyhedron;
import com.vzome.core.math.symmetry.Axis;
import com.vzome.core.math.symmetry.Direction;
import com.vzome.core.math.symmetry.Permutation;
import com.vzome.core.math.symmetry.Symmetry;
import com.vzome.core.parts.FastDefaultStrutGeometry;
import com.vzome.core.parts.StrutGeometry;


public abstract class AbstractShapes implements Shapes
{
    private final Map<Direction, Map<AlgebraicNumber, Polyhedron> > strutShapesByLengthAndOrbit = new HashMap<>();

    private final Map<Direction, StrutGeometry> strutGeometriesByOrbit = new HashMap<>();
    
    private final Map<Integer, Map<AlgebraicNumber, Map<Direction, HashMap<List<AlgebraicVector>, Polyhedron>>>> panelShapes = new HashMap<>();

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

    @Override
    public String toString()
    {
        return this.getClass().getSimpleName()
            + "( Symmetry:" + mSymmetry.getName()
            + ", PkgName:" + mPkgName
            + ", Name:" + mName 
            + ( alias == null ? "" : (", Alias:" + alias) )
            +" )";
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
    @JsonIgnore
    public Symmetry getSymmetry()
    {
        return mSymmetry;
    }

    private Polyhedron makePanelPolyhedron( Iterable<AlgebraicVector> vertices, boolean oneSided )
    {
        Polyhedron poly = new Polyhedron( this .mSymmetry .getField() );
        poly .setPanel( true );
        int arity = 0;
        for( AlgebraicVector gv : vertices) {
            arity++;
            poly .addVertex( gv );            
        }
        if ( poly .getVertexList() .size() < arity )
            return null;
        Polyhedron.Face front = poly .newFace();
        Polyhedron.Face back = poly .newFace();
        for ( int i = 0; i < arity; i++ ) {
            Integer j = i;
            front .add( j );
            back .add( 0, j );
        }
        poly .addFace( front );
        if ( ! oneSided )
            poly .addFace( back );
        return poly;
    }

    @Override
    public Polyhedron getPanelShape( int vertexCount, AlgebraicNumber quadrea, Axis zone, Iterable<AlgebraicVector> vertices, boolean oneSidedPanels )
    {
        Map<AlgebraicNumber, Map<Direction, HashMap<List<AlgebraicVector>, Polyhedron>>> map1 = this .panelShapes .get( vertexCount );
        if ( map1 == null ) {
            map1 = new HashMap<>();
            this .panelShapes .put( vertexCount, map1 );
        }
        Map<Direction, HashMap<List<AlgebraicVector>, Polyhedron>> map2 = map1 .get( quadrea );
        if ( map2 == null ) {
            map2 = new HashMap<>();
            map1 .put( quadrea, map2 );
        }
        Direction orbit = zone .getDirection();
        HashMap<List<AlgebraicVector>, Polyhedron> map3 = map2 .get( orbit );
        if ( map3 == null ) {
            map3 = new HashMap<>();
            map2 .put( orbit, map3 );
        }
        
        // now map normal to the canonical zone for orbit
        int orientation = zone .getOrientation();
        Permutation perm = this .mSymmetry .getPermutation( orientation ) .inverse();
        int inverseOrientation = perm .mapIndex( 0 );
        AlgebraicMatrix inverseTrans = this .mSymmetry .getMatrix( inverseOrientation );
        
        // now find or make a canonically-oriented shape
        List<AlgebraicVector> canonicalVertices = new ArrayList<>();
        for (AlgebraicVector vertex : vertices ) {
            canonicalVertices .add( inverseTrans .timesColumn( vertex ) );
        }
        Polyhedron shape = map3 .get( canonicalVertices );
        if ( shape == null ) {
            shape = makePanelPolyhedron( canonicalVertices, oneSidedPanels );
            map3 .put( canonicalVertices, shape );
        }
        return shape;
    }
}
