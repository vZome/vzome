package com.vzome.core.editor;

import com.vzome.core.generic.SubClassIterator;
import com.vzome.core.generic.FilteredIterator;
import com.vzome.core.generic.Predicate;
import com.vzome.core.model.Connector;
import com.vzome.core.model.Manifestation;
import com.vzome.core.model.Panel;
import com.vzome.core.model.Strut;
import com.vzome.core.render.RenderedManifestation;

/**
 * @author David Hall
 */
public class Manifestations {
	
	private static final ManifestationIsVisible manifestationIsVisible = new ManifestationIsVisible();
    
	private static final ManifestationIsRendered manifestationIsRendered = new ManifestationIsRendered();
	
	private static final Predicate<RenderedManifestation> resultNotNull = new Predicate<RenderedManifestation>()
	{
		@Override
		public boolean test( RenderedManifestation rm )
		{
			return rm != null;
		}
	};
    
    // Manifestations
    public static ManifestationIterator visibleManifestations(Iterable<Manifestation> manifestations) {
        return new ManifestationIterator(manifestationIsVisible, manifestations, null);
    }
    public static ManifestationIterator visibleManifestations(Predicate<Manifestation> preTest, Iterable<Manifestation> manifestations) {
        return new ManifestationIterator(preTest, manifestations, manifestationIsVisible);
    }
    public static ManifestationIterator visibleManifestations(Iterable<Manifestation> manifestations, Predicate<Manifestation> postTest) {
        return new ManifestationIterator(manifestationIsVisible, manifestations, postTest);
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
    
    // RenderedManifestations
    public static RenderedManifestationIterator getRenderedManifestations(Iterable<Manifestation> manifestations) {
        // Either this preTest or this postTest would do the job in this case.
        // Using both doesn't help or hurt. It just documents the options.
        return new RenderedManifestationIterator(manifestationIsRendered, manifestations, resultNotNull);
    }
    public static RenderedManifestationIterator getRenderedManifestations(Predicate<Manifestation> preTest, Iterable<Manifestation> manifestations) {
        return new RenderedManifestationIterator(preTest, manifestations, resultNotNull);
    }
    public static RenderedManifestationIterator getRenderedManifestations(Iterable<Manifestation> manifestations, Predicate<RenderedManifestation> postTest) {
        return new RenderedManifestationIterator(manifestationIsRendered, manifestations, postTest);
    }
    public static class RenderedManifestationIterator extends FilteredIterator<Manifestation, RenderedManifestation> {
        public RenderedManifestationIterator(Predicate<Manifestation> preTest, Iterable<Manifestation> manifestations, Predicate<RenderedManifestation> postTest) {
            super(preTest, manifestations, postTest);
        }
        @Override
        protected RenderedManifestation apply(Manifestation element) {
            return element.getRenderedObject();
        }
    }

    // Connectors        
    public static ConnectorIterator getConnectors(Iterable<Manifestation> manifestations) {
        return new ConnectorIterator(null, manifestations, null);
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
        return new ConnectorIterator(manifestationIsVisible, manifestations, postFilter);
    }
    public static class ConnectorIterator extends SubClassIterator<Manifestation, Connector> {
        private ConnectorIterator(Predicate<Manifestation> preFilter, Iterable<Manifestation> manifestations, Predicate<Connector> postFilter) {
            super(Connector.class, preFilter, manifestations, postFilter);
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
        return new StrutIterator(manifestationIsVisible, manifestations, postFilter);
    }
    public static class StrutIterator extends SubClassIterator<Manifestation, Strut> {
        private StrutIterator(Predicate<Manifestation> preFilter, Iterable<Manifestation> manifestations, Predicate<Strut> postFilter) {
            super(Strut.class, preFilter, manifestations, postFilter);
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
        return new PanelIterator(manifestationIsVisible, manifestations, postFilter);
    }
    public static class PanelIterator extends SubClassIterator<Manifestation, Panel> {
        private PanelIterator(Predicate<Manifestation> preFilter, Iterable<Manifestation> manifestations, Predicate<Panel> postFilter) {
            super(Panel.class, preFilter, manifestations, postFilter);
        }
    }

    public static class Filters {
        private Filters() {}; // no public c'tor

        public static boolean isRendered(Manifestation man) {
            return man.isRendered();
        }

        public static boolean isVisible(Manifestation man) {
            return !man.isHidden();
        }
    }
    
    public static class ManifestationIsRendered implements Predicate<Manifestation>
    {
		@Override
		public boolean test( Manifestation man )
		{
			return man .isRendered();
		}
    }
    
    public static class ManifestationIsVisible implements Predicate<Manifestation>
    {
		@Override
		public boolean test( Manifestation man )
		{
            return !man.isHidden();
		}
    }
}
