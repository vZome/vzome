package com.vzome.core.editor.api;

import java.util.function.Predicate;

import com.vzome.core.generic.FilteredIterator;
import com.vzome.core.generic.SubClassIterator;
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
        return new ConnectorIterator(Filters::isVisible, manifestations, postFilter);
    }
    public static ConnectorIterator getHiddenConnectors(Iterable<Manifestation> manifestations) {
        return getHiddenConnectors(manifestations, null);
    }
    public static ConnectorIterator getHiddenConnectors(Iterable<Manifestation> manifestations, Predicate<Connector> postFilter) {
        return new ConnectorIterator(Filters::isHidden, manifestations, postFilter);
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
        return new StrutIterator(Filters::isVisible, manifestations, postFilter);
    }
    public static StrutIterator getHiddenStruts(Iterable<Manifestation> manifestations) {
        return getHiddenStruts(manifestations, null);
    }
    public static StrutIterator getHiddenStruts(Iterable<Manifestation> manifestations, Predicate<Strut> postFilter) {
        return new StrutIterator(Filters::isHidden, manifestations, postFilter);
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
        return new PanelIterator(Filters::isVisible, manifestations, postFilter);
    }
    public static PanelIterator getHiddenPanels(Iterable<Manifestation> manifestations) {
        return getHiddenPanels(manifestations, null);
    }
    public static PanelIterator getHiddenPanels(Iterable<Manifestation> manifestations, Predicate<Panel> postFilter) {
        return new PanelIterator(Filters::isHidden, manifestations, postFilter);
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

        public static boolean isHidden(Manifestation man) {
            return man.isHidden();
        }

    }
}
