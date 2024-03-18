package com.vzome.core.editor.api;

import java.util.SortedSet;
import java.util.function.Predicate;

import com.vzome.core.algebra.AlgebraicVector;
import com.vzome.core.generic.FilteredIterator;
import com.vzome.core.model.Connector;
import com.vzome.core.model.Manifestation;
import com.vzome.core.model.Panel;
import com.vzome.core.model.Strut;

/**
 * @author David Hall
 */
public class Manifestations {
    
    // Manifestations
    public static ManifestationIterator visibleManifestations(Iterable<Manifestation> manifestations) {
        return new ManifestationIterator(Filters::isVisible, manifestations, null);
    }
    public static ManifestationIterator visibleManifestations(Predicate<Manifestation> preTest, Iterable<Manifestation> manifestations) {
        return new ManifestationIterator(preTest, manifestations, Filters::isVisible);
    }
    public static ManifestationIterator visibleManifestations(Iterable<Manifestation> manifestations, Predicate<Manifestation> postTest) {
        return new ManifestationIterator(Filters::isVisible, manifestations, postTest);
    }
    public static class ManifestationIterator extends FilteredIterator<Manifestation, Manifestation> {
        public ManifestationIterator(Predicate<Manifestation> preTest, Iterable<Manifestation> manifestations, Predicate<Manifestation> postTest) {
            super(preTest, manifestations, postTest);
        }
        @Override
        protected Manifestation apply(Manifestation element) {
            return element;
        }
    }
        
    // Connectors        
    public static ConnectorIterator getConnectors(Iterable<Manifestation> manifestations) {
        return new ConnectorIterator( null, manifestations, null );
    }
    public static ConnectorIterator getConnectors(Iterable<Manifestation> manifestations, Predicate<Connector> postFilter) {
        return new ConnectorIterator(null, manifestations, postFilter);
    }
    public static ConnectorIterator getConnectors(Predicate<Manifestation> preFilter, Iterable<Manifestation> manifestations, Predicate<Connector> postFilter) {
        return new ConnectorIterator(preFilter, manifestations, postFilter);
    }
    public static ConnectorIterator getVisibleConnectors(Iterable<Manifestation> manifestations) {
        return getVisibleConnectors(manifestations, null);
    }
    public static ConnectorIterator getVisibleConnectors(Iterable<Manifestation> manifestations, Predicate<Connector> postFilter) {
        return new ConnectorIterator(Filters::isVisible, manifestations, postFilter);
    }
    public static ConnectorIterator getHiddenConnectors(Iterable<Manifestation> manifestations) {
        return getHiddenConnectors(manifestations, null);
    }
    public static ConnectorIterator getHiddenConnectors(Iterable<Manifestation> manifestations, Predicate<Connector> postFilter) {
        return new ConnectorIterator(Filters::isHidden, manifestations, postFilter);
    }
    
    public static class ConnectorIterator extends FilteredIterator<Manifestation, Connector>
    {
        @Override
        public boolean preFilter( Manifestation element )
        {
            return ( element != null && element instanceof Connector )
                    ? super.preFilter(element)
                    : false;
        }
        
        @Override
        protected Connector apply( Manifestation element )
        {
            return (Connector) element;
        }
        
        private ConnectorIterator( Predicate<Manifestation> preFilter, Iterable<Manifestation> manifestations, Predicate<Connector> postFilter )
        {
            super( preFilter, manifestations, postFilter );
        }
    }

    // Struts
    public static StrutIterator getStruts(Iterable<Manifestation> manifestations) {
        return new StrutIterator(null, manifestations, null);
    }
    public static StrutIterator getStruts(Iterable<Manifestation> manifestations, Predicate<Strut> postFilter) {
        return new StrutIterator(null, manifestations, postFilter);
    }
    public static StrutIterator getStruts(Predicate<Manifestation> preFilter, Iterable<Manifestation> manifestations, Predicate<Strut> postFilter) {
        return new StrutIterator(preFilter, manifestations, postFilter);
    }
    public static StrutIterator getVisibleStruts(Iterable<Manifestation> manifestations) {
        return getVisibleStruts(manifestations, null);
    }
    public static StrutIterator getVisibleStruts(Iterable<Manifestation> manifestations, Predicate<Strut> postFilter) {
        return new StrutIterator(Filters::isVisible, manifestations, postFilter);
    }
    public static StrutIterator getHiddenStruts(Iterable<Manifestation> manifestations) {
        return getHiddenStruts(manifestations, null);
    }
    public static StrutIterator getHiddenStruts(Iterable<Manifestation> manifestations, Predicate<Strut> postFilter) {
        return new StrutIterator(Filters::isHidden, manifestations, postFilter);
    }
    
    public static class StrutIterator extends FilteredIterator<Manifestation, Strut>
    {
        @Override
        public boolean preFilter( Manifestation element )
        {
            return ( element != null && element instanceof Strut )
                    ? super.preFilter(element)
                    : false;
        }
        
        @Override
        protected Strut apply( Manifestation element )
        {
            return (Strut) element;
        }
        
        private StrutIterator( Predicate<Manifestation> preFilter, Iterable<Manifestation> manifestations, Predicate<Strut> postFilter )
        {
            super( preFilter, manifestations, postFilter );
        }
    }

    // Panels
    public static PanelIterator getPanels(Iterable<Manifestation> manifestations) {
        return new PanelIterator(null, manifestations, null);
    }
    public static PanelIterator getPanels(Iterable<Manifestation> manifestations, Predicate<Panel> postFilter) {
        return new PanelIterator(null, manifestations, postFilter);
    }
    public static PanelIterator getPanels(Predicate<Manifestation> preFilter, Iterable<Manifestation> manifestations, Predicate<Panel> postFilter) {
        return new PanelIterator(preFilter, manifestations, postFilter);
    }
    public static PanelIterator getVisiblePanels(Iterable<Manifestation> manifestations) {
        return getVisiblePanels(manifestations, null);
    }
    public static PanelIterator getVisiblePanels(Iterable<Manifestation> manifestations, Predicate<Panel> postFilter) {
        return new PanelIterator(Filters::isVisible, manifestations, postFilter);
    }
    public static PanelIterator getHiddenPanels(Iterable<Manifestation> manifestations) {
        return getHiddenPanels(manifestations, null);
    }
    public static PanelIterator getHiddenPanels(Iterable<Manifestation> manifestations, Predicate<Panel> postFilter) {
        return new PanelIterator(Filters::isHidden, manifestations, postFilter);
    }
    
    public static class PanelIterator extends FilteredIterator<Manifestation, Panel>
    {
        @Override
        public boolean preFilter( Manifestation element )
        {
            return ( element != null && element instanceof Panel )
                    ? super.preFilter(element)
                    : false;
        }
        
        @Override
        protected Panel apply( Manifestation element )
        {
            return (Panel) element;
        }
        
        private PanelIterator( Predicate<Manifestation> preFilter, Iterable<Manifestation> manifestations, Predicate<Panel> postFilter )
        {
            super( preFilter, manifestations, postFilter );
        }
    }

    public static class Filters
    {
        private Filters() {}; // no public c'tor

        public static boolean isRendered(Manifestation man) {
            return man.isRendered();
        }

        public static boolean isVisible(Manifestation man) {
            return !man.isHidden();
        }

        public static boolean isHidden(Manifestation man) {
            return man.isHidden();
        }

        // Required to overcome invalid overload of ConnectorIterator, etc. in JSweet
        public static boolean is( Manifestation man )
        {
            return true;
        }
    }
    
    /**
     * 
     * @param manifestations
     * @param output
     * @return last selected Connector location, or last selected Strut location, or last vertex of last selected Panel
     */
    public static AlgebraicVector sortVertices( Iterable<Manifestation> manifestations, SortedSet<AlgebraicVector> output )
    {
        AlgebraicVector lastBall = null;
        AlgebraicVector lastVertex = null;

        // phase one: find and index all vertices
        for ( Manifestation man : manifestations ) {
            if ( man instanceof Connector )
            {
                lastBall = man .getLocation();
                output .add( lastBall );
            }
            else if ( man instanceof Strut )
            {
                lastVertex = man .getLocation();
                output .add( lastVertex );
                output .add( ((Strut) man) .getEnd() );
            }
            else if ( man instanceof Panel )
            {
                for ( AlgebraicVector vertex : (Panel) man ) {
                    lastVertex = vertex;
                    output .add( vertex );
                }
            }
        }
        return ( lastBall != null )? lastBall : lastVertex;
    }
}
