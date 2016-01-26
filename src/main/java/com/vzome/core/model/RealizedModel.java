

package com.vzome.core.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.vzome.core.algebra.AlgebraicField;
import com.vzome.core.algebra.AlgebraicVector;
import com.vzome.core.construction.Construction;
import com.vzome.core.construction.Point;
import com.vzome.core.construction.Polygon;
import com.vzome.core.construction.Segment;
import com.vzome.core.math.Projection;
import com.vzome.core.render.Color;

/**
 * @author Scott Vorthmann
 */
public class RealizedModel implements Iterable<Manifestation> //implements ConstructionChanges
{
    private final List<ManifestationChanges> mListeners = new ArrayList<>( 1 );

    // TODO: DJH: Can this be replaced by a HashSet since the key is always equal to the value.
    private final HashMap<Manifestation, Manifestation> mManifestations = new LinkedHashMap<>( 1000 );
    
    private Projection mProjection;

	private final AlgebraicField field;

    public RealizedModel( AlgebraicField field, Projection projection )
    {
        super();
		this.field = field;
        mProjection = projection;
    }
    
    public Set<Manifestation> moreVisibleThan( RealizedModel other )
    {
        Set<Manifestation> result = new HashSet<>();
        for ( Iterator<Manifestation> iterator = mManifestations .values() .iterator(); iterator .hasNext(); ) {
            Manifestation man = iterator .next();
            if ( man .isHidden() )
                continue;
            Manifestation doppel = other .mManifestations .get( man );
            if ( doppel == null || doppel .isHidden() )
                result .add( man );
        }
        return result;
    }

    public void addListener( ManifestationChanges l )
    {
        mListeners .add( l );
    }

    public void removeListener( ManifestationChanges l )
    {
        mListeners .remove( l );
    }

	@Override
	public Iterator<Manifestation> iterator()
	{
        return mManifestations .keySet() .iterator();
	}

    @Deprecated
	public Iterator<Manifestation> getAllManifestations()
    {
        return this .iterator();
    }
    
    public Manifestation manifest( Construction c )
    {
        Manifestation m = null;
        if ( c instanceof Point )
        {
            Point p = (Point) c;
            m = new Connector( mProjection .projectImage( p .getLocation(), true ) );
        }
        else if ( c instanceof Segment )
        {
            Segment s = (Segment) c;
            AlgebraicVector start = mProjection .projectImage( s .getStart(), true );
            AlgebraicVector end = mProjection .projectImage( s .getEnd(), true );
            if ( ! start .equals( end ) )
            {
                m = new Strut( start, end );
            }
        }
        else if ( c instanceof Polygon )
        {
            Polygon p = (Polygon) c;
            List<AlgebraicVector> vertices = new ArrayList<>();
            AlgebraicVector[] vertexArray = p .getVertices();
            for ( int i = 0; i < vertexArray .length; i++ )
                vertices .add( mProjection .projectImage( vertexArray[ i ], true ) );
        
            m = new Panel( vertices );
        }
        return m;
    }
    
    private static final Logger logger = Logger .getLogger( "com.vzome.core.model" );
    
    public void add( Manifestation m )
    {
        // TODO: DJH: Can this be replaced by a HashSet since the key is always equal to the value.
        mManifestations .put( m, m );
        if ( logger .isLoggable( Level .FINER ) )
            logger .finer( "add manifestation: " + m .toString() );
    }
    
    public void remove( Manifestation m )
    {
        mManifestations .remove( m );
        if ( logger .isLoggable( Level .FINER ) )
            logger .finer( "remove manifestation: " + m .toString() );
    }
    
    public void refresh( boolean on, RealizedModel unused )
    {
        for ( Iterator<Manifestation> iterator = mManifestations .keySet() .iterator(); iterator.hasNext(); ) {
            Manifestation man = iterator.next();
            if ( ! man .isHidden() )
            {
            	if ( on )
            		show( man );
            	else
            		hide( man );
            }
        }
    }
    
    /*
     * idempotent: show,show is the same as show
     */
    public void show( Manifestation m )
    {
        if ( doingBatch )
        {
            if ( removals .contains( m ) )
                removals .remove( m );
            else
                additions .add( m );
        }
        else
            privateShow( m );
    }

    private void privateShow( Manifestation m )
    {
        if ( ! m .isRendered() ) {
            for ( Iterator<ManifestationChanges> listeners = mListeners .iterator(); listeners .hasNext(); ) {
                ManifestationChanges next = listeners .next();
                next .manifestationAdded( m );
                // one side-effect will be to set the rendered object
            }
        }
    }

    /*
     * idempotent: hide,hide is the same as hide
     */
    public void hide( Manifestation m )
    {
        if ( doingBatch )
        {
            if ( additions .contains( m ) )
                additions .remove( m );
            else
                removals .add( m );
        }
        else
            privateHide( m );
    }
    
    private void privateHide( Manifestation m )
    {
        if ( m .isRendered() ) {
            for ( Iterator<ManifestationChanges> listeners = mListeners .iterator(); listeners .hasNext(); ) {
                ManifestationChanges next = listeners .next();
                next .manifestationRemoved( m );
            }
        }
    }
    
    public void setColor( Manifestation m, Color color )
    {
        if ( m .isRendered() ) {
            for ( Iterator<ManifestationChanges> listeners = mListeners .iterator(); listeners .hasNext(); ) {
                ManifestationChanges next = listeners .next();
                next .manifestationColored( m, color );
            }
        }
    }
    
    
    public Manifestation findConstruction( Construction c )
    {
        Manifestation testMan = manifest( c );
        if ( testMan == null )
            return null;
        
        Manifestation actualMan = mManifestations .get( testMan );
        if ( actualMan == null )
            actualMan = testMan;
        
        return actualMan;
    }
    
    public Manifestation removeConstruction( Construction c )
    {
        Manifestation testMan = manifest( c );
        if ( testMan == null )
            return null;
        Manifestation actualMan = mManifestations .get( testMan );
        if ( actualMan == null )
            return null;
        return testMan;
    }

    /**
     * @param c
     * @return
     */
    public Manifestation getManifestation( Construction c )
    {
        Manifestation m = manifest( c );
        return mManifestations .get( m );
    }

	public int size()
	{
		return mManifestations .size();
	}
    
    public boolean equals( Object object )
    {
        if ( object == null ) {
            return false;
        }
        if ( object == this ) {
            return true;
        }
        if ( ! ( object instanceof RealizedModel ) )
            return false;
        
        RealizedModel that = (RealizedModel) object;

        if ( this.size() != that.size() )
            return false;
        for ( Iterator<Manifestation> it = mManifestations .keySet() .iterator(); it .hasNext(); ) {
            if ( ! that .mManifestations .keySet() .contains( it .next() ) )
                return false;
        }
        return true;
    }
    
    public int hashCode()
    {
        return size();
    }
    
    private boolean doingBatch = false;
    
    private final Set<Manifestation> additions = new HashSet<>();
    
    private final Set<Manifestation> removals = new HashSet<>();

    public void startBatch()
    {
        additions .clear();
        removals .clear();
        doingBatch = true;
    }

    public void endBatch()
    {
        for (Iterator<Manifestation> iterator = removals .iterator(); iterator.hasNext(); ) {
            Manifestation m = iterator.next();
            privateHide( m );
        }
        for (Iterator<Manifestation> iterator = additions .iterator(); iterator.hasNext(); ) {
            Manifestation m = iterator.next();
            privateShow( m );
        }
        additions .clear();
        removals .clear();
        this .doingBatch = false;
    }

	public AlgebraicField getField()
	{
		return field;
	}
}
