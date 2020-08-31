package com.vzome.core.render;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonValue;
import com.vzome.core.algebra.AlgebraicField;
import com.vzome.core.algebra.AlgebraicVector;
import com.vzome.core.construction.Color;
import com.vzome.core.editor.api.OrbitSource;
import com.vzome.core.editor.api.Shapes;
import com.vzome.core.exporters.Exporter3d;
import com.vzome.core.math.Polyhedron;
import com.vzome.core.math.RealVector;
import com.vzome.core.math.symmetry.Axis;
import com.vzome.core.math.symmetry.Direction;
import com.vzome.core.math.symmetry.Embedding;
import com.vzome.core.math.symmetry.OrbitSet;
import com.vzome.core.math.symmetry.Symmetry;
import com.vzome.core.model.Connector;
import com.vzome.core.model.Manifestation;
import com.vzome.core.model.ManifestationChanges;
import com.vzome.core.model.ManifestationImpl;
import com.vzome.core.model.Panel;
import com.vzome.core.model.Strut;

public class RenderedModel implements ManifestationChanges, Iterable<RenderedManifestation>
{
	protected List<RenderingChanges> mListeners = new ArrayList<>();
	
	private Shapes mPolyhedra;

	private float mSelectionGlow = 0.8f;

	protected final HashSet<RenderedManifestation> mRendered = new HashSet<>();
	
	protected final HashMap<UUID, RenderedManifestation> byID = new HashMap<>();

	private final AlgebraicField field;

    private OrbitSource orbitSource;
    
    private boolean oneSidedPanels = false;

    private RenderingChanges mainListener;
    
	private boolean enabled = true;

    private boolean colorPanels = true;
    
    private static final class SymmetryOrbitSource implements OrbitSource
    {
        private final Symmetry symmetry;

        private final OrbitSet orbits;

        private SymmetryOrbitSource( Symmetry symmetry )
        {
            this.symmetry = symmetry;
            orbits = new OrbitSet( symmetry );
        }

        @Override
        public Color getColor( Direction orbit )
        {
            return new Color( 128, 123, 128 );
        }

        @Override
        public Axis getAxis( AlgebraicVector vector )
        {
            return symmetry .getAxis( vector );
        }

        @Override
        public OrbitSet getOrbits()
        {
            return orbits;
        }

        @Override
        public Shapes getShapes() {
            return null;
        }

        @Override
        public Symmetry getSymmetry()
        {
            return symmetry;
        }

        @Override
        public Color getVectorColor( AlgebraicVector vector )
        {
            return null;
        }

        @Override
        public String getName()
        {
            return null;
        }
    }

    public RenderedModel( final AlgebraicField field, final OrbitSource orbitSource )
    {
        super();
        this .field = field;
        this .orbitSource = orbitSource;
        this .mPolyhedra = ( orbitSource == null )? null : orbitSource .getShapes();
    }
    
    public RenderedModel( final Symmetry symmetry )
    {
        this( symmetry .getField(), new SymmetryOrbitSource( symmetry ));
    }
    
    public RenderedModel( final Symmetry symmetry, boolean enabled )
    {
        this( symmetry .getField(), new SymmetryOrbitSource( symmetry ));
        this .enabled = enabled;
    }
        
    public RenderedModel( final AlgebraicField field, boolean enabled )
    {
        this( field, null );
        this .enabled = enabled;
    }
    
    public RenderedModel withColorPanels( boolean setting )
    {
        this .colorPanels = setting;
        return this;
    }
    
    public AlgebraicField getField()
    {
        return this.field;
    }

    public void addListener( RenderingChanges listener )
    {
        if ( mainListener == null )
            this .mainListener = listener;
        else
            mListeners .add( listener );
    }
    public void removeListener( RenderingChanges listener )
    {
        if ( mainListener == listener )
            mainListener = null;
        else
            mListeners .remove( listener );
    }
    
    public RenderedManifestation render( Manifestation manifestation )
    {
        RenderedManifestation rm = new RenderedManifestation( manifestation );
        rm .setModel( this );
        rm .resetAttributes( this .orbitSource, this .mPolyhedra, this .oneSidedPanels, this .colorPanels );
        return rm;
    }

    @Override
	public void manifestationAdded( Manifestation m )
	{
        ManifestationImpl mi = (ManifestationImpl) m;
		if ( ! this .enabled )
		{
		    mi .setRenderedObject( new RenderedManifestation( m ) );
			return;
		}
		
	    RenderedManifestation rm = render( m );
        Polyhedron poly = rm .getShape();
	    if ( poly == null )
	        return; // no direction for this strut
	    mi .setRenderedObject( rm );
        
	    mRendered .add( rm );
	    this .byID .put( rm .getGuid(), rm );
	    if ( mainListener != null )
	        mainListener .manifestationAdded( rm );
        for (RenderingChanges listener : mListeners) {
            listener .manifestationAdded( rm );
        }
	}
	
    @Override
	public void manifestationRemoved( Manifestation m )
	{
        ManifestationImpl mi = (ManifestationImpl) m;
		if ( ! this .enabled ) {
			mi .setRenderedObject( null );
			return;
		}
		
	    RenderedManifestation rendered = (RenderedManifestation) m .getRenderedObject();
	    if ( rendered == null )
	        return; // there was no way to render the shape
	    
	    for (RenderingChanges listener : mListeners) {
            listener .manifestationRemoved( rendered );
        }
	    if ( mainListener != null )
            mainListener .manifestationRemoved( rendered );
	    if ( ! mRendered .remove( rendered ) )
	        throw new IllegalStateException( "unable to remove RenderedManifestation" );
	    
        this .byID .remove( rendered .getGuid() );
        mi .setRenderedObject( null );
	}
    
    public RenderedManifestation getRenderedManifestation( String guid )
    {
        return this .byID .get( UUID .fromString( guid ) );
    }

	public void setManifestationGlow( Manifestation m, boolean on )
	{
        RenderedManifestation rendered = (RenderedManifestation) m .getRenderedObject();
        if ( rendered == null )
            return; // could not find a shape for m, probably
        rendered .setGlow( on? mSelectionGlow : 0f );
        if ( mainListener != null )
            mainListener .glowChanged( rendered );
        for (RenderingChanges listener : mListeners) {
            listener .glowChanged( rendered );
        }
    }

    
    public void setManifestationColor( Manifestation m, Color color )
    {
        RenderedManifestation rendered = (RenderedManifestation) m .getRenderedObject();
        if ( rendered == null )
            return; // could not find a shape for m, probably
        rendered .setColor( color );
        if ( mainListener != null )
            mainListener .colorChanged( rendered );
        for (RenderingChanges listener : mListeners) {
            listener .colorChanged( rendered );
        }
    }

    
    public void setManifestationTransparency( Manifestation m, boolean on )
    {
        RenderedManifestation rendered = (RenderedManifestation) m .getRenderedObject();
        if ( rendered == null )
            return; // could not find a shape for m, probably
        rendered .setTransparency( on? mSelectionGlow : 0f );
        if ( mainListener != null )
            mainListener .colorChanged( rendered );
        for (RenderingChanges listener : mListeners) {
            listener .colorChanged( rendered );
        }
    }
	
    @Override
    @JsonValue
	public Iterator<RenderedManifestation> iterator()
	{
	    return mRendered .iterator();
	}

	public OrbitSource getOrbitSource()
	{
	    return this .orbitSource;
	}

    // TODO add changeScale( int increment )

    public void setShapes( Shapes shapes )
    {
        boolean supported = this .mainListener .shapesChanged( shapes );
        if ( ! supported )
            // Oddity: this assumes that the orbitSource has already been updated
            this .setOrbitSource( orbitSource ); // gets the job done, but overkill
    }

	public void setOrbitSource( OrbitSource orbitSource )
	{
        this .orbitSource = orbitSource;
        this .enabled = true;

        mPolyhedra = orbitSource .getShapes();        
        if ( mPolyhedra == null )
            return;

        {
//            int yieldFreq = mRendered .size() / 20;
            int yieldCount = 0;
            HashSet<RenderedManifestation> newSet = new HashSet<>();
            for ( Iterator<RenderedManifestation> rms = mRendered .iterator(); rms .hasNext(); ) {
                yieldCount = (++yieldCount) % 20;
                if ( yieldCount == 0 )
                    Thread .yield();
                RenderedManifestation rendered = rms .next();
                rms .remove();
                Manifestation m = rendered .getManifestation();
                if ( m .isHidden() )
                    continue;
                if ( rendered .getShape() != null )
                {
                    if ( mainListener != null ) {
                        mainListener .manifestationRemoved( rendered );
                    }
                    for (RenderingChanges listener : mListeners) {
                        listener .manifestationRemoved( rendered );
                    }
                }
              
                rendered .resetAttributes( orbitSource, mPolyhedra, this .oneSidedPanels, this .colorPanels );
                newSet .add( rendered );  // must re-hash, since shape has changed

                float glow = rendered .getGlow();
                if ( rendered .getShape() != null )
                {
                    if ( mainListener != null )
                    {
                        mainListener .manifestationAdded( rendered );
                        if ( glow != 0f )
                            mainListener .glowChanged( rendered );
                    }
                    for (RenderingChanges listener : mListeners) {
                        listener .manifestationAdded( rendered );
                        if ( glow != 0f )
                            listener .glowChanged( rendered );
                    }
                }
            }
            mRendered .addAll( newSet );
            for ( RenderedManifestation rm : newSet ) {
                this .byID .put( rm .getGuid(), rm );
            }
        }
	    
	}


    @Override
    public void manifestationColored( Manifestation m, Color color )
    {
		if ( this .enabled )
			this .setManifestationColor( m, color );
    }

    public RenderedModel snapshot()
    {
        RenderedModel snapshot = new RenderedModel( this .orbitSource .getSymmetry(), false );
        for (RenderedManifestation rm : mRendered) {
            RenderedManifestation copy = rm .copy();
            copy .setModel( this );
            snapshot .mRendered .add( copy );
        }
        return snapshot;
    }
    
    /**
     * Switch a scene graph (changes) from rendering one RenderedModel to another one.
     * For RenderedManifestations that show the same object in both, just update the
     * attributes.
     * When "from" is empty, this is the initial rendering of the "to" RenderedModel.
     * @param from is an empty RenderedModel in some cases
     * @param to
     * @param changes is a scene graph
     */
    public static void renderChange( RenderedModel from, RenderedModel to, RenderingChanges changes )
    {
        // TODO: Does clone() perform any better than new HashSet(), or is there any other reason to keep it?
//        Set<RenderedManifestation> toRemove = (Set<RenderedManifestation>) from .mRendered .clone();
        HashSet<RenderedManifestation> toRemove = new HashSet<>(from.mRendered);
        toRemove .removeAll( to .mRendered );
        for ( RenderedManifestation rm : toRemove ) {
            changes .manifestationRemoved( rm );
        }
        // TODO: Does clone() perform any better than new HashSet(), or is there any other reason to keep it?
//        Set<RenderedManifestation> toAdd = (Set<RenderedManifestation>) to .mRendered .clone();
        HashSet<RenderedManifestation> toAdd = new HashSet<>(to.mRendered);
        toAdd .removeAll( from .mRendered );
        for ( RenderedManifestation rm : toAdd ) {
            changes .manifestationAdded( rm );
        }
        for ( RenderedManifestation fromRm : from .mRendered ) {
            for ( RenderedManifestation toRm : to .mRendered ) {
                if ( fromRm .equals( toRm ) )
                {
                    // This part is fragile.  The next call relies on the fact that "changes"
                    //   is the only sticky RenderingChanges that ever touches these RMs,
                    //   or picking will break.
                    changes .manifestationSwitched( fromRm, toRm );
                    if ( Float.floatToIntBits( fromRm .getGlow() ) != Float .floatToIntBits( toRm .getGlow() ) )
                        changes .glowChanged( toRm );
                    Color fromColor = fromRm .getColor();
                    Color toColor = toRm .getColor();
                    if ( fromColor == null && toColor == null )
                    	continue;
                    if ( ( fromColor == null && toColor != null )
                      || ( fromColor != null && toColor == null )
                      || ! fromColor .equals( toColor ) )
                        changes .colorChanged( toRm );
                }
            }
        }
    }

	public RealVector renderVector( AlgebraicVector av )
	{
		if ( av != null )
			return getEmbedding() .embedInR3( av );
		else
            return new RealVector( 0d, 0d, 0d );
	}

	public Embedding getEmbedding()
	{
		return this .orbitSource .getSymmetry();
	}

    public double measureDistanceCm( Connector c1, Connector c2 )
    {
        return measureLengthCm( renderVector( c1 .getLocation() .minus( c2 .getLocation() ) ) );
    }

    public static double measureLengthCm( RealVector rv )
    {
        return rv.length() * Exporter3d.RZOME_CM_SCALING;
    }

    public double measureLengthCm( Strut strut )
    {
        return measureLengthCm( this .renderVector( strut .getOffset() ) );
    }

	public double measureDihedralAngle( Panel p1, Panel p2 )
	{
        RealVector v1 = p1 .getNormal( this .getEmbedding() );
        RealVector v2 = p2 .getNormal( this .getEmbedding() );
        return safeAcos(v1, v2);
	}

    public double measureAngle( Strut s1, Strut s2 )
    {
        RealVector v1 = this .renderVector( s1 .getOffset() );
        RealVector v2 = this .renderVector( s2 .getOffset() );
        return safeAcos(v1, v2);
    }
    
    /*
     * The two struts in this VEF exemplify cosine being barely out of range:
vZome VEF 6 field heptagon
4
(0,0,0) (-1,3,-2) (2,-1,2) (1,2,0)
(0,0,0) (0,2,-1) (2,-1,2) (0,0,0)
(0,0,0) (2,-1,2) (0,2,-1) (0,0,0)
(0,0,0) (1,2,0) (0,0,0) (1,2,0)
2
2 1
3 0
0
0
     */
    public static double safeAcos(RealVector v1, RealVector v2) {
        double cosine = v1 .dot( v2 ) / ( v1 .length() * v2 .length() );
        // rounding errors can result in cosine being slightly outside the valid range 
        // of -1 to +1 in which case Math.acos() returns NaN 
        cosine = Math.min( 1.0d, cosine);
        cosine = Math.max(-1.0d, cosine);
        return Math.acos( cosine );
    }

    public RenderedManifestation getNearbyBall( RealVector location, double tolerance )
    {
        for ( RenderedManifestation rm : this .mRendered ) {
            if ( rm .getManifestation() instanceof Connector ) {
                RealVector ballLoc = rm .getLocation();
                double distance = ballLoc .minus( location ) .length();
                if ( distance < tolerance )
                    return rm;
            }
        }
        return null;
    }

    public Iterable<Manifestation> getManifestations()
    {
        return this .mRendered .stream() .map( rm -> rm .getManifestation() ) .collect( Collectors .toList() );
    }
}
