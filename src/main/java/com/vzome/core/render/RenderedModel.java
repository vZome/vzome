package com.vzome.core.render;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import com.vzome.core.algebra.AlgebraicField;
import com.vzome.core.algebra.AlgebraicMatrix;
import com.vzome.core.algebra.AlgebraicNumber;
import com.vzome.core.algebra.AlgebraicVector;
import com.vzome.core.math.Polyhedron;
import com.vzome.core.math.symmetry.Axis;
import com.vzome.core.math.symmetry.Direction;
import com.vzome.core.math.symmetry.OrbitSet;
import com.vzome.core.math.symmetry.Symmetry;
import com.vzome.core.model.Connector;
import com.vzome.core.model.Manifestation;
import com.vzome.core.model.ManifestationChanges;
import com.vzome.core.model.Panel;
import com.vzome.core.model.Strut;

public class RenderedModel implements ManifestationChanges
{
	protected List mListeners = new ArrayList();
	
	private Shapes mPolyhedra;

	private float mSelectionGlow = 0.8f;

	protected final HashSet mRendered = new HashSet();

	private final AlgebraicField field;

    private OrbitSource orbitSource;
    
    private boolean oneSidedPanels = false;

    private RenderingChanges mainListener;

	private boolean enabled = true;

    private boolean colorPanels = true;
    
    static Logger logger = Logger.getLogger( "com.vzome.core.render.RenderedModel" );

    public interface OrbitSource
    {
    	Symmetry getSymmetry();
    	
        Axis getAxis( AlgebraicVector vector );
        
        Color getColor( Direction orbit );

		OrbitSet getOrbits();
		
		Shapes getShapes();
    }
        
    public RenderedModel( final AlgebraicField field, final OrbitSource orbitSource )
    {
        super();
        this .field = field;
        this .orbitSource = orbitSource;
        this .mPolyhedra = ( orbitSource == null )? null : orbitSource .getShapes();
    }
    
    public RenderedModel( final AlgebraicField field, boolean enabled )
    {
        this( field, new OrbitSource()
        {
        	private final Symmetry symmetry = field .getSymmetries() [0];
        	private final OrbitSet orbits = new OrbitSet( symmetry );
        	
			public Color getColor( Direction orbit )
			{
				return new Color( 128, 123, 128 );
			}
			
			public Axis getAxis( AlgebraicVector vector )
			{
				return this .symmetry .getAxis( vector );
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
				return this .symmetry;
			}
		} );
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

	public void manifestationAdded( Manifestation m )
	{
		if ( ! this .enabled )
		{
			m .setRenderedObject( new RenderedManifestation( m ) );
			return;
		}
		
	    RenderedManifestation rm = new RenderedManifestation( m );
//	    resetAxis( rm );
	    resetAttributes( rm, false );
        Polyhedron poly = rm .getShape();
	    if ( poly == null )
	        return; // no direction for this strut
	    m .setRenderedObject( rm );
        
	    mRendered .add( rm );
	    if ( mainListener != null )
    	    mainListener .manifestationAdded( rm );
        for ( int i = 0; i < mListeners .size(); i++ )
            ((RenderingChanges) mListeners .get( i )) .manifestationAdded( rm );
	}
	
	public void manifestationRemoved( Manifestation m )
	{
		if ( ! this .enabled ) {
			m .setRenderedObject( null );
			return;
		}
		
	    RenderedManifestation rendered = (RenderedManifestation) m .getRenderedObject();
	    if ( rendered == null )
	        return; // there was no way to render the shape
	    
	    for ( int i = 0; i < mListeners .size(); i++ )
            ((RenderingChanges) mListeners .get( i )) .manifestationRemoved( rendered );
	    if ( mainListener != null )
            mainListener .manifestationRemoved( rendered );
	    if ( ! mRendered .remove( rendered ) )
	         throw new IllegalStateException( "unable to remove RenderedManifestation" );
        m .setRenderedObject( null );
	}
	            

	public void setManifestationGlow( Manifestation m, boolean on )
	{
        RenderedManifestation rendered = (RenderedManifestation) m .getRenderedObject();
        if ( rendered == null )
            return; // could not find a shape for m, probably
        rendered .setGlow( on? mSelectionGlow : 0f );
        if ( mainListener != null )
            mainListener .glowChanged( rendered );
        for ( int i = 0; i < mListeners .size(); i++ )
            ((RenderingChanges) mListeners .get( i )) .glowChanged( rendered );
    }

    
    public void setManifestationColor( Manifestation m, Color color )
    {
        RenderedManifestation rendered = (RenderedManifestation) m .getRenderedObject();
        if ( rendered == null )
            return; // could not find a shape for m, probably
        rendered .setColor( color );
        if ( mainListener != null )
            mainListener .colorChanged( rendered );
        for ( int i = 0; i < mListeners .size(); i++ )
            ((RenderingChanges) mListeners .get( i )) .colorChanged( rendered );
    }

    
    public void setManifestationTransparency( Manifestation m, boolean on )
    {
        RenderedManifestation rendered = (RenderedManifestation) m .getRenderedObject();
        if ( rendered == null )
            return; // could not find a shape for m, probably
        rendered .setTransparency( on? mSelectionGlow : 0f );
        if ( mainListener != null )
            mainListener .colorChanged( rendered );
        for ( int i = 0; i < mListeners .size(); i++ )
            ((RenderingChanges) mListeners .get( i )) .colorChanged( rendered );
    }
	
	
	public Iterator getRenderedManifestations()
	{
	    return mRendered .iterator();
	}


	public OrbitSource getOrbitSource()
	{
	    return this .orbitSource;
	}

    // TODO add changeScale( int increment )

	public void setOrbitSource( OrbitSource orbitSource )
	{
        this.orbitSource = orbitSource;
        this .enabled = true;

        if ( mPolyhedra == null ) {
            mPolyhedra = orbitSource .getShapes();
            return;
        }
        Symmetry oldSymm = mPolyhedra .getSymmetry();
        boolean hadShapeColors = mPolyhedra .hasColors();
        mPolyhedra = orbitSource .getShapes();
        boolean hasShapeColors = mPolyhedra .hasColors();
        
        if ( false && oldSymm == orbitSource .getSymmetry() && !hadShapeColors && !hasShapeColors ) {
            
//            boolean didOneBall = false;
            
            HashSet newSet = new HashSet();
            for ( Iterator polys = mRendered .iterator(); polys .hasNext(); )
            {
                RenderedManifestation rendered = (RenderedManifestation) polys .next();
                polys .remove();
//                if ( rendered .getManifestation() instanceof Connector ) {
//                    if ( didOneBall )
//                        continue;
//                    else
//                        didOneBall = true;
//                }
                resetAttributes( rendered, false );
                newSet .add( rendered );  // must re-hash, since shape has changed
                if ( mainListener != null )
                    mainListener .shapeChanged( rendered );
                for ( int i = 0; i < mListeners .size(); i++ )
                    ((RenderingChanges) mListeners .get( i )) .shapeChanged( rendered );
            }
            mRendered .addAll( newSet );
        } else {
//            int yieldFreq = mRendered .size() / 20;
            int yieldCount = 0;
            HashSet newSet = new HashSet();
            for ( Iterator rms = mRendered .iterator(); rms .hasNext(); ) {
                yieldCount = (++yieldCount) % 20;
                if ( yieldCount == 0 )
                    Thread .yield();
                RenderedManifestation rendered = (RenderedManifestation) rms .next();
                rms .remove();
                Manifestation m = rendered .getManifestation();
                if ( m .isHidden() )
                    continue;
                if ( rendered .getShape() != null )
                {
                    if ( mainListener != null )
                        mainListener .manifestationRemoved( rendered );
                    for ( int i = 0; i < mListeners .size(); i++ ) {
                        RenderingChanges listener = (RenderingChanges) mListeners .get( i );
                        listener .manifestationRemoved( rendered );
                    }
                }
              
                resetAttributes( rendered, false );
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
                    for ( int i = 0; i < mListeners .size(); i++ ) {
                        RenderingChanges listener = (RenderingChanges) mListeners .get( i );
                        listener .manifestationAdded( rendered );
                        if ( glow != 0f )
                            ((RenderingChanges) mListeners .get( i )) .glowChanged( rendered );
                    }
                }
            }
            mRendered .addAll( newSet );
        }
	    
	}


    private void resetAttributes( RenderedManifestation rm, boolean justShape )
	{
        Color oldColor = rm .getColor();
	    Manifestation m = rm .getManifestation();
        if ( m instanceof Connector ) {
            resetAttributes( rm, justShape, (Connector) m );
        }
        else if ( m instanceof Strut ) {
            Strut strut = (Strut) m;
            resetAttributes( rm, justShape, strut );
        }
        else if ( m instanceof Panel ) {
            Panel panel = (Panel) m;
            Polyhedron shape = makePanelPolyhedron( panel );
            if ( shape == null )
                return;
            AlgebraicVector normal = panel .getNormal( field );
            if ( normal .isOrigin() )
                return;
            rm .setShape( shape );  
            if ( justShape || ! this .colorPanels )
                return;

            rm .setOrientation( field .identityMatrix( 3 ), false );
            rm .setColor( Color.WHITE );

            try {
                Axis axis = orbitSource .getAxis( normal );
                if ( axis == null )
                    return;
                Direction orbit = axis .getDirection();

                Color color = orbitSource .getColor( orbit ) .getPastel();
                rm .setColor( color );
            } catch ( IllegalStateException e ) {
                logger .warning( "Unable to set color for panel, normal = " + normal .toString() );
            }
        }
        else
            throw new UnsupportedOperationException( "only strut, ball, and panel shapes currently supported" );
	    if ( oldColor != null )
	        rm .setColor( oldColor );
	}

	protected void resetAttributes( RenderedManifestation rm, boolean justShape, Strut strut )
	{
		AlgebraicVector offset = strut .getOffset();
		if ( offset .isOrigin() )
		    return; // should catch this earlier
		Axis axis = orbitSource .getAxis( offset );
		if ( axis == null )
			return; // this should only happen when using the bare Symmetry-based OrbitSource
		Direction orbit = axis .getDirection();
		
		// TODO remove this length computation... see the comment on AbstractShapes.getStrutShape()
		
		AlgebraicNumber len = axis .getLength( offset );
		
		Polyhedron prototypeLengthShape = mPolyhedra .getStrutShape( orbit, len );
		rm .setShape( prototypeLengthShape );
		
		if ( justShape )
		    return;
		
		Color color = mPolyhedra .getColor( orbit );
		if ( color == null )
			color = orbitSource .getColor( orbit );
		/*
		 * What a hack!  This defines a "name" that simply encodes the RGB.
		 * The orbitSource color is used only to create this name, and we'll
		 * later use the name to synthesize another copy of the color when
		 * the Java3d rendering calls Colors.getColor() while creating
		 * an appearance for that color name.
		 * 
		 * When translating old 2.0 files (that have no orbits defined within),
		 * we're left with default colors, but only ApplicationUI loads the
		 * preferences (including the built-in defaults), and we don't use
		 * ApplicationUI.  That leaves the Colors.getColorPref() to return
		 * WHITE for everything.  NOTE: This is now fixed, since
		 * DefaultApplication now loads the built-in defaults.
		 */
		rm .setColor( color );

		int orn = axis .getOrientation();
		AlgebraicMatrix orientation = mPolyhedra .getSymmetry() .getMatrix( orn );
		boolean mirrored = false;
		if ( axis .getSense() == Symmetry .MINUS ) {
		    orientation = orientation .negate();
		    mirrored = true;
		}
		rm .setStrut( orbit, orn, len );
		rm .setOrientation( orientation, mirrored );
//            rm .setOrientation( RationalMatrices .identity( m .getLocation() .length / 2 ), false );
	}

	protected void resetAttributes( RenderedManifestation rm, boolean justShape, Connector m )
	{
		rm .setShape( mPolyhedra .getConnectorShape() );
		if ( justShape )
		    return;
		Color color = mPolyhedra .getColor( null );
		if ( color == null )
			color = orbitSource .getColor( null );
		rm .setColor( color );
		rm .setOrientation( field .identityMatrix( 3 ), false );
	}

    private Polyhedron makePanelPolyhedron( Panel panel )
    {
        Polyhedron poly = new Polyhedron( this .field );
        Iterator vertices = panel .getVertices();
        int arity = 0;
        while ( vertices .hasNext() ) {
            AlgebraicVector gv = (AlgebraicVector) vertices .next();
            arity++;
            poly .addVertex( gv );            
        }
        if ( poly .getVertexList() .size() < arity )
            return null;
        Polyhedron.Face front = poly .newFace();
        Polyhedron.Face back = poly .newFace();
        for ( int i = 0; i < arity; i++ ) {
            Integer j = new Integer( i );
            front .add( j );
            back .add( 0, j );
        }
        poly .addFace( front );
        if ( ! this .oneSidedPanels )
            poly .addFace( back );
        return poly;
    }

    public void manifestationColored( Manifestation m, Color color )
    {
		if ( this .enabled )
			this .setManifestationColor( m, color );
    }

    public RenderedModel snapshot()
    {
        RenderedModel snapshot = new RenderedModel( this .field, false );
        for (Iterator iterator = mRendered .iterator(); iterator.hasNext(); ) {
            RenderedManifestation rm = (RenderedManifestation) iterator.next();
            RenderedManifestation copy = rm .copy();
            snapshot .mRendered .add( copy );
        }
        return snapshot;
    }
    
    public static void renderChange( RenderedModel from, RenderedModel to, RenderingChanges changes )
    {
        Set toRemove = (HashSet) from .mRendered .clone();
        toRemove .removeAll( to .mRendered );
        for ( Iterator iterator = toRemove .iterator(); iterator .hasNext(); ) {
            RenderedManifestation rm = (RenderedManifestation) iterator .next();
            changes .manifestationRemoved( rm );
        }
        Set toAdd = (HashSet) to .mRendered .clone();
        toAdd .removeAll( from .mRendered );
        for ( Iterator iterator = toAdd .iterator(); iterator .hasNext(); ) {
            RenderedManifestation rm = (RenderedManifestation) iterator .next();
            changes .manifestationAdded( rm );
        }
        for ( Iterator froms = from .mRendered .iterator(); froms .hasNext(); ) {
            RenderedManifestation fromRm = (RenderedManifestation) froms .next();
            for ( Iterator tos = to .mRendered .iterator(); tos .hasNext(); ) {
                RenderedManifestation toRm = (RenderedManifestation) tos .next();
                if ( fromRm .equals( toRm ) )
                {
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
}


