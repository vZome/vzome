
//(c) Copyright 2011, Scott Vorthmann.

package com.vzome.core.editor;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.function.Function;

import com.vzome.core.algebra.AlgebraicVector;
import com.vzome.core.commands.Command.Failure;
import com.vzome.core.construction.FreePoint;
import com.vzome.core.construction.Point;
import com.vzome.core.construction.Segment;
import com.vzome.core.construction.SegmentJoiningPoints;
import com.vzome.core.model.Manifestation;
import com.vzome.core.model.Panel;
import com.vzome.core.model.RealizedModel;
import com.vzome.core.model.Strut;

public class Validate2Manifold extends ChangeManifestations
{
	public Validate2Manifold( Selection selection, RealizedModel model )
	{
		super( selection, model );
	}

	@SuppressWarnings("serial")
    private static final class Edges extends HashMap<Strut,Collection<Panel>>
	{
        void addStrut( Strut strut, Panel panel )
        {
            Collection<Panel> existing = this .get( strut );
            if ( existing == null ) {
                existing = new HashSet<Panel>();
                this .put( strut, existing );
            }
            existing .add( panel );
        }
	}
	
	private final void showStrut( Strut strut )
	{
        Point a = new FreePoint( strut .getLocation() );
        manifestConstruction( a );
        Point b = new FreePoint( strut .getEnd() );
        manifestConstruction( b );
        Segment segment = new SegmentJoiningPoints( a, b );
        manifestConstruction( segment );
	}

	@Override
	public void perform() throws Failure
	{
	    for ( Manifestation man : mSelection )
	        super .unselect( man, true );
        hideConnectors();
        hideStruts();
        redo();

        // First, compute the degree (incident faces) for each edge
        Edges edges = new Edges();
	    for ( Panel panel : Manifestations .getVisiblePanels( this .mManifestations ) )
	    {
	        AlgebraicVector v0 = null, prev = null;
	        for ( AlgebraicVector vertex : panel )
	        {
	            if ( v0 == null ) {
	                v0 = vertex;
	                prev = vertex;
	            }
	            else {
	                // We are using Strut rather than Segment because of the equality,
	                //  which ignores orientation.
	                Strut strut = new Strut( prev, vertex );
	                edges .addStrut( strut, panel );
	                prev = vertex;
	            }
	        }
            Strut strut = new Strut( prev, v0 );
            edges .addStrut( strut, panel );
	    }
	    
	    boolean invalid = false;
	    // Now, look for any edges with degree != 2
	    for ( Map.Entry<Strut,Collection<Panel>> entry : edges .entrySet() )
	    {
	        if ( entry .getValue() .size() != 2 ) {
	            showStrut( entry .getKey() );
                invalid = true;
	        }
	    }
	    if ( invalid ) {
	        hidePanels();
	        redo();
	        return;
	    }
	    
	    // Next, lets find any edges where panel orientations disagree.
	    
        for ( Map.Entry<Strut,Collection<Panel>> entry : edges .entrySet() )
        {
            // We don't care about strut orientation, since we are
            //  only testing whether the two panels are oriented
            //  consistently with whatever the orientation is.
            Strut strut = entry .getKey();
            final AlgebraicVector v1 = strut .getLocation();
            final AlgebraicVector v2 = strut .getEnd();
            
            // Define a function that returns true if the pair (v1,v2)
            //  is consistently ordered in the vertex list
            Function<Panel, Boolean> oriented = ( Panel p ) -> {
                AlgebraicVector prev = null;
                for ( AlgebraicVector v : p ) {
                    if ( v .equals( v2 ) && prev != null )
                        return v1 .equals( prev );
                    prev = v;
                }
                // v2 must have been first, check if v1 was last
                return v1 .equals( prev );
            };
            Panel[] panels = new Panel[2];
            entry .getValue() .toArray( panels );
            boolean c1 = oriented .apply( panels[0] );
            boolean c2 = oriented .apply( panels[1] );
            if ( c1 == c2 ) {
                // Both panels list the edge vertices in the same order,
                //  so the two panels are in opposite orientations!
                // (Mesh the curled fingers of your hands, with the thumbs pointing up;
                //   knuckle order is consistent, but the hands have opposite handedness.)
                invalid = true;
                showStrut( strut );
            }
        }

        if ( invalid ) {
            redo();
            return;
        }
	}

	@Override
	protected String getXmlElementName()
	{
		return "Validate2Manifold";
	}
}
