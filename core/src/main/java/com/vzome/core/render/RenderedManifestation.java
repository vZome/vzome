

package com.vzome.core.render;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.vzome.core.algebra.AlgebraicMatrix;
import com.vzome.core.algebra.AlgebraicNumber;
import com.vzome.core.algebra.AlgebraicVector;
import com.vzome.core.construction.Color;
import com.vzome.core.editor.api.OrbitSource;
import com.vzome.core.editor.api.Shapes;
import com.vzome.core.math.Polyhedron;
import com.vzome.core.math.RealVector;
import com.vzome.core.math.symmetry.Axis;
import com.vzome.core.math.symmetry.Direction;
import com.vzome.core.math.symmetry.Embedding;
import com.vzome.core.model.Connector;
import com.vzome.core.model.Manifestation;
import com.vzome.core.model.Panel;
import com.vzome.core.model.RenderedObject;
import com.vzome.core.model.Strut;

/**
 * @author Scott Vorthmann
 *
 */
public class RenderedManifestation implements RenderedObject
{
    private final Manifestation mManifestation;

    private Polyhedron mShape;

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

    private OrbitSource orbitSource;

    private String label;

    public RenderedManifestation( Manifestation m, OrbitSource orbitSource )
    {
        mManifestation = m;
        this.orbitSource = orbitSource;
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

    @JsonGetter( "id" )
    public UUID getGuid()
    {
        return guid;
    }

    public void setGraphicsObject( Object go )
    {
        mGraphicsObject = go;
    }

    @JsonIgnore
    public Object getGraphicsObject()
    {
        return mGraphicsObject;
    }

    public void setGlow( float glow )
    {
        mGlow = glow;
    }

    @JsonIgnore
    public float getGlow()
    {
        return mGlow;
    }

    public void setTransparency( float trans )
    {
        mTransparency = trans;
    }

    @JsonIgnore
    public float getTransparency()
    {
        return mTransparency;
    }

    @JsonGetter( "shape" )
    public UUID getShapeId()
    {
        return this .mShape .getGuid();
    }
    
    @JsonIgnore
    public Polyhedron getShape()
    {
        return mShape;
    }

    public void setPickable( boolean value )
    {
        mPickable = value;
    }

    @JsonIgnore
    public boolean isPickable()
    {
        return mPickable;
    }

    @JsonIgnore
    public Manifestation getManifestation()
    {
        return mManifestation;
    }

    @JsonIgnore
    public Color getColor()
    {
        return this.color;
    }

    @JsonGetter( "color" )
    public String getColorWeb()
    {
        return this.color.toWebString();
    }

    public void setColor( Color color )
    {
        this.color = color;
    }

    public void setOrientation( AlgebraicMatrix m )
    {
        mOrientation = m;
    }

    @JsonIgnore
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

    @JsonGetter( "position" )
    public RealVector getLocation()
    {
        if ( this .location != null )
            return getEmbedding() .embedInR3( this .location );
        else
            return new RealVector( 0d, 0d, 0d );
    }

    @JsonIgnore
    public AlgebraicVector getLocationAV()
    {
        return this .location;
    }

    @JsonIgnore
    public Embedding getEmbedding()
    {
        return this .orbitSource .getSymmetry();
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
        RenderedManifestation copy = new RenderedManifestation( null, this .orbitSource );
        copy .location = this .location;
        copy .fixedLocation = this .fixedLocation;
        copy .color = this .color;
        copy .mGlow = this .mGlow;
        copy .mOrientation = this .mOrientation;
        copy .mShape = this .mShape;
        copy .mTransparency = this .mTransparency;
        copy .strutLength = this .strutLength;
        copy .strutZone = this .strutZone;
        copy .label = this .label;
        return copy;
    }

    public void setStrut( Direction orbit, int zone, int sense, AlgebraicNumber length )
    {
        this .strutOrbit = orbit;
        this .strutZone = zone;
        this .strutSense = sense;
        this .strutLength = length;
    }

    @JsonIgnore
    public int getStrutZone()
    {
        return this .strutZone;
    }

    @JsonIgnore
    public int getStrutSense()
    {
        return this .strutSense;
    }

    @JsonIgnore
    public AlgebraicNumber getStrutLength()
    {
        return this .strutLength;
    }

    @JsonIgnore
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

    @JsonIgnore
    public String getSymmetryShapes()
    {
        return this .orbitSource .getName() + ":" + this .orbitSource .getShapes() .getName();
    }

    public void resetAttributes( boolean oneSidedPanels, boolean colorPanels )
    {
        String label = this.mManifestation .getLabel();
        if ( null != label ) {
            this .setLabel( label );
        }
        if ( this .mManifestation instanceof Panel ) {
            resetPanelAttributes( oneSidedPanels, colorPanels );
        }
        else if ( this .orbitSource .getShapes() == null ) {
            return; // rendering a symmetry model with panels only
        }
        else if ( this .mManifestation instanceof Connector ) {
            this .resetConnectorAttributes( (Connector) this .mManifestation );
        }
        else if ( this .mManifestation instanceof Strut ) {
            Strut strut = (Strut) this .mManifestation;
            this .resetStrutAttributes( strut );
        }
        else 
            throw new UnsupportedOperationException( "only strut, ball, and panel shapes currently supported" );
    }

    private void resetPanelAttributes( boolean oneSidedPanels, boolean colorPanels )
    {
        Shapes shapes = orbitSource .getShapes();

        Panel panel = (Panel) this .mManifestation;
        this .location = panel .getFirstVertex();
        
        List<AlgebraicVector> relativeVertices = new ArrayList<>();
        for (AlgebraicVector vertex : panel ) {
            relativeVertices .add( vertex .minus( this .location ) );
        }

        AlgebraicVector normal = panel .getNormal();
        if ( normal .isOrigin() )
            return;
        Axis zone = orbitSource .getAxis( normal );
        Polyhedron shape = shapes .getPanelShape( panel .getVertexCount(), panel .getQuadrea(), zone, relativeVertices, oneSidedPanels );
        if ( shape == null )
            return;
        this .mShape = shape;  

        if ( zone == null ) {
            this .setColor( Color.WHITE );
            return; // this should only happen when using the bare Symmetry-based OrbitSource
        }

        // Not sure this is going to be correct
        int orn = zone .getOrientation();
        AlgebraicMatrix orientation = shapes .getSymmetry() .getMatrix( orn );
        this .setOrientation( orientation );
        this .strutZone = zone .getOrientation();

        if ( ! colorPanels )
            return;

        try {
            // This lets the Panel represent Planes better.
            panel .setZoneVector( zone .normal() );
            
            Direction orbit = zone .getDirection();

            Color color = this .mManifestation .getColor();
            if ( color == null ) {
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

    protected void resetStrutAttributes( Strut strut )
    {
        Shapes shapes = orbitSource .getShapes();
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
        if ( prototypeLengthShape == null )
            return; // This happens for very short struts, when the shape would be inside-out
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

    protected void resetConnectorAttributes( Connector m )
    {
        Shapes shapes = orbitSource .getShapes();
        this .mShape = shapes .getConnectorShape();
        Color color = this .getManifestation() .getColor();
        if ( color == null )
            color = shapes .getColor( null );
        if ( color == null )
            color = orbitSource .getColor( null );
        this .setColor( color );
        this .setOrientation( orbitSource .getSymmetry() .getField() .identityMatrix( 3 ) );
    }

    public void setOrbitSource( OrbitSource orbitSource )
    {
        this.orbitSource = orbitSource;
    }

    @JsonIgnore
    public OrbitSource getOrbitSource()
    {
        return this.orbitSource;
    }

    public String getLabel()
    {
        return this.label;
    }

    public void setLabel( String label )
    {
        this.label = label;
    }
}
