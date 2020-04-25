

package com.vzome.core.render;

import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.vzome.core.algebra.AlgebraicMatrix;
import com.vzome.core.algebra.AlgebraicNumber;
import com.vzome.core.algebra.AlgebraicVector;
import com.vzome.core.editor.SymmetrySystem;
import com.vzome.core.math.Polyhedron;
import com.vzome.core.math.RealVector;
import com.vzome.core.math.symmetry.Axis;
import com.vzome.core.math.symmetry.Direction;
import com.vzome.core.math.symmetry.Embedding;
import com.vzome.core.mesh.Color;
import com.vzome.core.model.Connector;
import com.vzome.core.model.Manifestation;
import com.vzome.core.model.Panel;
import com.vzome.core.model.Strut;

/**
 * @author Scott Vorthmann
 *
 */
public class RenderedManifestation
{
    private final Manifestation mManifestation;

    private Polyhedron mShape;

    private RenderedModel model;

    private Color color = null;

    private AlgebraicMatrix mOrientation;

    private float mGlow = 0.0f, mTransparency = 0.0f;

    private Object mGraphicsObject;

    private boolean mPickable = true;

    private boolean isOffset = false;
    private AlgebraicVector location;
    private AlgebraicVector fixedLocation;

    private int strutZone = -1;

    private AlgebraicNumber strutLength = null;

    private Direction strutOrbit = null;

    private int strutSense;

    private final UUID guid = UUID .randomUUID();

    //    private transient Axis mAxis = null;
    
    private static final Logger logger = Logger.getLogger( "com.vzome.core.render.RenderedManifestation" );

    public RenderedManifestation( Manifestation m )
    {
        mManifestation = m;
        if ( m != null )
            location = m .getLocation();
        this .fixedLocation = location;
        //        AlgebraicField factory = (AlgebraicField) m .getLocation() .getFactory();
        mOrientation = null; // factory .identity();
    }

    @Override
    public String toString()
    {
        return this .mManifestation .toString();
    }

    public UUID getGuid()
    {
        return guid;
    }

    public void setModel( RenderedModel model )
    {
        this .model = model;
    }

    public RenderedModel getModel()
    {
        return this .model;
    }

    public void setGraphicsObject( Object go )
    {
        mGraphicsObject = go;
    }

    public Object getGraphicsObject()
    {
        return mGraphicsObject;
    }


    public void setGlow( float glow )
    {
        mGlow = glow;
    }


    public float getGlow()
    {
        return mGlow;
    }


    public void setTransparency( float trans )
    {
        mTransparency = trans;
    }


    public float getTransparency()
    {
        return mTransparency;
    }

    public Polyhedron getShape()
    {
        return mShape;
    }

    public void setPickable( boolean value )
    {
        mPickable = value;
    }

    public boolean isPickable()
    {
        return mPickable;
    }

    public Manifestation getManifestation()
    {
        return mManifestation;
    }

    public Color getColor()
    {
        return this.color;
    }

    public void setColor( Color color )
    {
        this.color = color;
    }

    public void setOrientation( AlgebraicMatrix m )
    {
        mOrientation = m;
    }

    public AlgebraicMatrix getOrientation()
    {
        return mOrientation;
    }

    //
    //    /**
    //     * Useful only for struts.  This is for optimization.
    //     * @param axis
    //     */
    //    public void setAxis( Axis axis )
    //    {
    //        mAxis = axis;
    //    }
    //    
    //    public Axis getAxis()
    //    {
    //        return mAxis;
    //    }

    public RealVector getLocation()
    {
        return this .model .renderVector( this .location );
    }

    public AlgebraicVector getLocationAV()
    {
        return this .location;
    }

    public Embedding getEmbedding()
    {
        return this .model .getEmbedding();
    }

    @Override
    public int hashCode()
    {
        return this .guid .hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        RenderedManifestation other = (RenderedManifestation) obj;
        if (fixedLocation == null) {
            if (other.fixedLocation != null) {
                return false;
            }
        } else if (!fixedLocation.equals(other.fixedLocation)) {
            return false;
        }
        if (isOffset != other.isOffset) {
            return false;
        }
        if (mOrientation == null) {
            if (other.mOrientation != null) {
                return false;
            }
        } else if (!mOrientation.equals(other.mOrientation)) {
            return false;
        }
        if (mShape == null) {
            if (other.mShape != null) {
                return false;
            }
        } else if (!mShape.equals(other.mShape)) {
            return false;
        }
        if (strutSense != other.strutSense) {
            return false;
        }
        return true;
    }

    public RenderedManifestation copy()
    {
        RenderedManifestation copy = new RenderedManifestation( null );
        copy .location = this .location;
        copy .fixedLocation = this .fixedLocation;
        copy .color = this .color;
        copy .mGlow = this .mGlow;
        copy .mOrientation = this .mOrientation;
        copy .mShape = this .mShape;
        copy .mTransparency = this .mTransparency;
        copy .strutLength = this .strutLength;
        copy .strutZone = this .strutZone;
        return copy;
    }

    public void setStrut( Direction orbit, int zone, int sense, AlgebraicNumber length )
    {
        this .strutOrbit = orbit;
        this .strutZone = zone;
        this .strutSense = sense;
        this .strutLength = length;
    }

    public int getStrutZone()
    {
        return this .strutZone;
    }

    public int getStrutSense()
    {
        return this .strutSense;
    }

    public AlgebraicNumber getStrutLength()
    {
        return this .strutLength;
    }

    public Direction getStrutOrbit()
    {
        return this .strutOrbit;
    }

    void offsetLocation()
    {
        if ( this .mManifestation != null ) {
            Strut strut = (Strut) this .mManifestation;
            this .location = strut .getEnd();
            this .isOffset = true;
        }
    }

    void resetLocation()
    {
        if ( this .mManifestation != null ) {
            location = this .mManifestation .getLocation();
            this .isOffset = false;
        }
    }

    public String getSymmetryShapes()
    {
        SymmetrySystem symmetrySystem = (SymmetrySystem) this .model .getOrbitSource();
        return symmetrySystem .getName() + ":" + symmetrySystem .getStyle() .getName();
    }

    public void resetAttributes( RenderedModel.OrbitSource orbitSource, Shapes shapes, boolean oneSidedPanels, boolean colorPanels )
    {
        if ( this .mManifestation instanceof Panel ) {
            resetPanelAttributes( orbitSource, shapes, oneSidedPanels, colorPanels );
        }
        else if ( shapes == null ) {
            return; // rendering a symmetry model with panels only
        }
        else if ( this .mManifestation instanceof Connector ) {
            this .resetConnectorAttributes( orbitSource, shapes, (Connector) this .mManifestation );
        }
        else if ( this .mManifestation instanceof Strut ) {
            Strut strut = (Strut) this .mManifestation;
            this .resetStrutAttributes( orbitSource, shapes, strut );
        }
        else 
            throw new UnsupportedOperationException( "only strut, ball, and panel shapes currently supported" );
    }

    private void resetPanelAttributes( RenderedModel.OrbitSource orbitSource, Shapes shapes, boolean oneSidedPanels, boolean colorPanels )
    {
        Panel panel = (Panel) this .mManifestation;
        Polyhedron shape = makePanelPolyhedron( panel, oneSidedPanels );
        if ( shape == null )
            return;
        AlgebraicVector normal = panel .getNormal();
        if ( normal .isOrigin() )
            return;
        this .mShape = shape;  
        if ( ! colorPanels )
            return;

        this .setOrientation( orbitSource .getSymmetry() .getField() .identityMatrix( 3 ) );

        try {
            Axis axis = orbitSource .getAxis( normal );
            if ( axis == null ) {
                this .setColor( Color.WHITE );
                return;
            }

            // This lets the Panel represent Planes better.
            panel .setZoneVector( axis .normal() );
            
            Direction orbit = axis .getDirection();

            Color color = this .mManifestation .getColor();
            if ( color == null ) {
                color = shapes .getColor( orbit );
                if ( color == null )
                    color = orbitSource .getColor( orbit );
                if ( color != null )
                    color = color .getPastel();
            }
            this .setColor( color );
        } catch ( IllegalStateException e ) {
            if ( logger .isLoggable( Level.WARNING ) )
                logger .warning( "Unable to set color for panel, normal = " + normal .toString() );
        }
    }

    protected void resetStrutAttributes( RenderedModel.OrbitSource orbitSource, Shapes shapes, Strut strut )
    {
        AlgebraicVector offset = strut .getOffset();
        if ( offset .isOrigin() )
            return; // should catch this earlier
        Axis axis = orbitSource .getAxis( offset );
        if ( axis == null )
            return; // this should only happen when using the bare Symmetry-based OrbitSource
        
        // This lets the Strut represent Lines better.
        strut .setZoneVector( axis .normal() );
        
        Direction orbit = axis .getDirection();
        
        // TODO remove this length computation... see the comment on AbstractShapes.getStrutShape()
        
        AlgebraicNumber len = axis .getLength( offset );
        
        Polyhedron prototypeLengthShape = shapes .getStrutShape( orbit, len );
        this .mShape = prototypeLengthShape;

        int orn = axis .getOrientation();
        AlgebraicMatrix orientation = shapes .getSymmetry() .getMatrix( orn );
        
        // Strut geometries only need to be mirrored for certain symmetries
        AlgebraicMatrix reflection = orbitSource .getSymmetry() .getPrincipalReflection();
        if ( reflection != null ) {
            /*
             *  Odd prismatic group, where getPrincipalReflection() != null:
             */
            if ( logger .isLoggable( Level.FINE ) ) {
                logger .fine( "rendering " + offset + " as " + axis );              
            }
            if ( axis .getSense() == Axis .MINUS ) {
                if ( logger .isLoggable( Level.FINER ) ) {
                    logger .finer( "mirroring orientation " + orn );                
                }
                this .mShape = prototypeLengthShape .getEvilTwin( reflection );
            }
            if ( ! axis .isOutbound() ) {  // see declaration for details
                this .offsetLocation();
            } else
                this .resetLocation(); // might be switching between systems
        } else {
            // MINUS orbits are handled just by offsetting... rendering the strut from the opposite end
            if ( axis .getSense() == Axis .MINUS ) {
                this .offsetLocation();
            } else
                this .resetLocation(); // might be switching between systems
        }
        this .setStrut( orbit, orn, axis .getSense(), len );
        this .setOrientation( orientation );
        
        Color color = this .getManifestation() .getColor();
        if ( color == null )
            color = shapes .getColor( orbit );
        if ( color == null )
            color = orbitSource .getColor( orbit );
        this .setColor( color );
    }

    protected void resetConnectorAttributes( RenderedModel.OrbitSource orbitSource, Shapes shapes, Connector m )
    {
        this .mShape = shapes .getConnectorShape();
        Color color = this .getManifestation() .getColor();
        if ( color == null )
            color = shapes .getColor( null );
        if ( color == null )
            color = orbitSource .getColor( null );
        this .setColor( color );
        this .setOrientation( orbitSource .getSymmetry() .getField() .identityMatrix( 3 ) );
    }

    private static Polyhedron makePanelPolyhedron( Panel panel, boolean oneSided )
    {
        Polyhedron poly = new Polyhedron( panel .getZoneVector() .getField() );
        poly .setPanel( true );
        int arity = 0;
        for( AlgebraicVector gv : panel) {
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
}
