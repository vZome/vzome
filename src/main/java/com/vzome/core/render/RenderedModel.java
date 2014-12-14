package com.vzome.core.render;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import com.vzome.core.algebra.AlgebraicField;
import com.vzome.core.algebra.RationalMatrices;
import com.vzome.core.algebra.RationalVectors;
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
    
    protected final Map mStyles = new HashMap();
    
    protected Map mCurrentStyle = null;
    
    protected final Map mOrientations = new HashMap();
    
    protected final Map mAppearances = new HashMap();
        
    private final AlgebraicField field;

    private OrbitSource orbitSource;
    
    private boolean oneSidedPanels = false;

    private RenderingChanges mainListener;

	private boolean enabled = true;
    
    static Logger logger = Logger.getLogger( "com.vzome.core.render.RenderedModel" );

    public interface OrbitSource
    {
        Axis getAxis( int[] vector );
        
        Color getColor( Direction orbit );

		OrbitSet getOrbits();
    }
        
    public RenderedModel( final AlgebraicField field, final Shapes shapes, final OrbitSource orbitSource )
    {
        super();
        this .field = field;
        this .orbitSource = orbitSource;
        this .mPolyhedra = shapes;
    }
    
    public RenderedModel( final AlgebraicField field, boolean enabled )
    {
        this( field, null, new OrbitSource()
        {
        	private final Symmetry symmetry = field .getSymmetries() [0];
        	private final OrbitSet orbits = new OrbitSet( symmetry );
        	
			public Color getColor( Direction orbit )
			{
				return new Color( 128, 123, 128 );
			}
			
			public Axis getAxis( int[] vector )
			{
				return field .getSymmetries() [0] .getAxis( vector );
			}

			@Override
			public OrbitSet getOrbits()
			{
				return orbits;
			}
		} );
        this .enabled = enabled;
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

    
    public void setManifestationColor( Manifestation m, String colorName )
    {
        RenderedManifestation rendered = (RenderedManifestation) m .getRenderedObject();
        if ( rendered == null )
            return; // could not find a shape for m, probably
        rendered .setColorName( colorName );
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

	public void setSymmetrySystem( OrbitSource orbitSource, Shapes geom )
	{
        this.orbitSource = orbitSource;
        this .enabled = true;

        if ( mPolyhedra == null ) {
            mPolyhedra = geom;
            mStyles .put( geom, new HashMap() );
            return;
        }
        Symmetry oldSymm = mPolyhedra .getSymmetry();
	    mPolyhedra = geom;
                
        if ( oldSymm == geom .getSymmetry() ) {
            
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
        String oldColorName = rm .getColorName();
        if ( oldColorName == null || ! oldColorName .startsWith( "rgb.custom" ) )
            oldColorName = null;
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
            int[] /*AlgebraicVector*/ normal = panel .getNormal( field );
            if ( field .isOrigin( normal ) )
                return;
            rm .setShape( shape );  
            if ( justShape )
                return;

            rm .setOrientation( RationalMatrices .identity( normal .length / 2 ), false );
            rm .setColorName( Colors .getColorName( Color.WHITE ) );

            try {
                Axis axis = orbitSource .getAxis( normal );
                if ( axis == null )
                    return;
                Direction orbit = axis .getDirection();

                Color color = orbitSource .getColor( orbit ) .getPastel();
                rm .setColorName( Colors .getColorName( color ) );
            } catch ( IllegalStateException e ) {
                logger .warning( "Unable to set color for panel, normal = " + Arrays .toString( normal ) );
            }
        }
        else
            throw new UnsupportedOperationException( "only strut, ball, and panel shapes currently supported" );
	    if ( oldColorName != null )
	        rm .setColorName( oldColorName );
	}

	protected void resetAttributes( RenderedManifestation rm, boolean justShape, Strut strut )
	{
		int[] /*AlgebraicVector*/ offset = strut .getOffset();
		if ( RationalVectors .isOrigin( offset ) )
		    return; // should catch this earlier
		Axis axis = orbitSource .getAxis( offset );
		if ( axis == null )
			return; // this should only happen when using the bare Symmetry-based OrbitSource
		Direction orbit = axis .getDirection();
		
		// TODO remove this length computation... see the comment on AbstractShapes.getStrutShape()
		
		int[] /*AlgebraicNumber*/ len = axis .getLength( offset );
		
		Polyhedron prototypeLengthShape = mPolyhedra .getStrutShape( orbit, len );
		rm .setShape( prototypeLengthShape );
		
		if ( justShape )
		    return;
		
		Color color = orbitSource .getColor( orbit );
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
		rm .setColorName( Colors .getColorName( color ) );

		int orn = axis .getOrientation();
		int[][] orientation = mPolyhedra .getSymmetry() .getMatrix( orn );
		boolean mirrored = false;
		if ( axis .getSense() == Symmetry .MINUS ) {
		    orientation = RationalMatrices .negate( orientation  );
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
		rm .setColorName( Colors .CONNECTOR );
		rm .setOrientation( RationalMatrices .identity( m .getLocation() .length / 2 ), false );
	}

    private Polyhedron makePanelPolyhedron( Panel panel )
    {
        Polyhedron poly = new Polyhedron( this .field );
        Iterator vertices = panel .getVertices();
        int arity = 0;
        while ( vertices .hasNext() ) {
            int[] /*AlgebraicVector*/ gv = (int[] /*AlgebraicVector*/) vertices .next();
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

    public void manifestationColored( Manifestation m, String colorName )
    {
		if ( this .enabled )
			this .setManifestationColor( m, colorName );
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
                    if ( ! fromRm .getColorName() .equals( toRm .getColorName() ) )
                        changes .colorChanged( toRm );
                }
            }
        }
    }
}


