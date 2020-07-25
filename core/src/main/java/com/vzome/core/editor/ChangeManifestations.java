
//(c) Copyright 2005, Scott Vorthmann.  All rights reserved.

package com.vzome.core.editor;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.function.Predicate;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.vzome.core.construction.Construction;
import com.vzome.core.model.Color;
import com.vzome.core.model.Connector;
import com.vzome.core.model.Manifestation;
import com.vzome.core.model.Panel;
import com.vzome.core.model.RealizedModel;
import com.vzome.core.model.Strut;
import com.vzome.core.render.RenderedManifestation;
import com.vzome.xml.DomUtils;

public abstract class ChangeManifestations extends ChangeSelection
{
    protected final RealizedModel mManifestations;

    public ChangeManifestations( Selection selection, RealizedModel realized )
    {
        super( selection );

        mManifestations = realized;
        // TODO: DJH: Can this be replaced by a HashSet since the key is always equal to the value.
        mManifestedNow = new HashMap<>();
    }

    /**
     * This records the NEW manifestations produced by manifestConstruction for this edit,
     * to avoid creating colliding manifestations.  It is never referenced after the last
     * call to manifestConstruction.
     * 
     * TODO: look at implications for unmanifestConstruction
     */
    // TODO: DJH: Can this be replaced by a HashSet since the key is always equal to the value.
    private transient Map<Manifestation, Manifestation> mManifestedNow;  // used only while calling manifest

    @Override
    public void redo()
    {
        if ( mManifestedNow != null )
            // TODO: DJH: Can this be replaced by a HashSet since the key is always equal to the value.
            mManifestedNow = new HashMap<>();
        super .redo();
        //        System.out.print( " manifestations: " + mManifestations .size() );
    }

    @Override
    public void undo()
    {
        if ( mManifestedNow != null )
            mManifestedNow = null;
        super .undo();
    }

    protected Manifestation getManifestation( Construction c )
    {
        return mManifestations .getManifestation( c );
        // TODO assert that the result is non-null
    }

    public Manifestation manifestConstruction( Construction c )
    {
        //        if ( realizer != null )
        //            return realizeConstruction( c );
        //        
        Manifestation m = mManifestations .findConstruction( c );
        if ( m == null )
            return null;
        Manifestation made = mManifestedNow .get( m );
        if ( made != null )
            return made;
        if ( m .isUnnecessary() )  { // just manifested, not added yet
            // TODO: DJH: Can this be replaced by a HashSet since the key is always equal to the value.
            mManifestedNow .put( m, m );
            plan( new ManifestConstruction( c, m, true ) );
        }
        else {
            // already manifested, just make sure it shows
            if ( m .getRenderedObject() == null )
                plan( new RenderManifestation( m, true ) );
        }
        return m;
    }

    //    /**
    //     * This is adapting the current notion of Constructions to the future notion
    //     * of Realizer.
    //     * 
    //     * @param c
    //     * @return
    //     */
    //    private Manifestation realizeConstruction( Construction c )
    //    {
    //        if ( c instanceof Point )
    //            return realizer .realizeBall( ((Point) c) .getLocation() );
    //        if ( c instanceof Segment )
    //        {
    //            Segment segment = (Segment) c;
    //            return realizer .realizeStrut( segment .getCenter(), segment .getLength(), segment .getAxis() );
    //        }
    //        return null;
    //    }

    protected Manifestation unmanifestConstruction( Construction c )
    {
        Manifestation m = mManifestations .removeConstruction( c );
        if ( m == null )
            return null;
        plan( new ManifestConstruction( c, m, false ) );
        return m;
    }

    protected void deleteManifestation( Manifestation man )
    {
        plan( new DeleteManifestation( man ) );
    }

    protected void showManifestation( Manifestation m )
    {
        plan( new RenderManifestation( m, true ) );
    }

    protected void hideManifestation( Manifestation m )
    {
        plan( new RenderManifestation( m, false ) );
    }

    public void colorManifestation( Manifestation m, Color color )
    {
        plan( new ColorManifestation( m, color ) );
    }

    protected void hideConnectors()
    {
        for ( Connector connector : Manifestations .getVisibleConnectors( this .mManifestations ) )
            hideManifestation( connector );
    }

    protected void showConnectors()
    {
        for ( Connector connector : Manifestations .getHiddenConnectors( this .mManifestations ) )
            showManifestation( connector );
    }

    protected void hideStruts()
    {
        for ( Strut strut : Manifestations .getVisibleStruts( this .mManifestations ) )
            hideManifestation( strut );
    }

    protected void showStruts()
    {
        for ( Strut strut : Manifestations .getHiddenStruts( this .mManifestations ) )
            showManifestation( strut );
    }

    protected void hidePanels()
    {
        for ( Panel panel : Manifestations .getVisiblePanels( this .mManifestations ) )
            hideManifestation( panel );
    }

    protected void showPanels()
    {
        for ( Panel panel : Manifestations .getHiddenPanels( this .mManifestations ) )
            showManifestation( panel );
    }

    public boolean showsManifestation( Manifestation man )
    {
        for (Iterator<SideEffect> iterator = this .getEffects(); iterator.hasNext();) {
            SideEffect effect = iterator.next();
            if ( effect instanceof ManifestConstruction ) {
                ManifestConstruction show = (ManifestConstruction) effect;
                if ( show .showsManifestation( man ) )
                    return true;
            }
            else if ( effect instanceof RenderManifestation ) {
                RenderManifestation show = (RenderManifestation) effect;
                if ( show .showsManifestation( man ) )
                    return true;
            }
        }
        return false;
    }    

    private class ManifestConstruction implements SideEffect
    {
        private final Manifestation mManifestation;

        private final Construction mConstruction;

        private final boolean mShowing;

        public ManifestConstruction( Construction construction, Manifestation manifestation, boolean showing )
        {
            mConstruction = construction;
            mManifestation = manifestation;
            mShowing = showing;
        }

        @Override
        public void redo()
        {
            if ( mShowing ) {
                if ( mManifestation .isUnnecessary() )
                    mManifestations .add(  mManifestation );
                // note the asymmetry... we want to unhide when adding
                mManifestations .show( mManifestation ); // TODO make this more immediate, call renderer here
                mManifestation .addConstruction( mConstruction );
            }
            else {
                mManifestation .removeConstruction( mConstruction );
                if ( mManifestation .isUnnecessary() ) {
                    mManifestations .hide( mManifestation ); // TODO make this more immediate, call renderer here
                    mManifestations .remove( mManifestation );
                }
            }
        }

        @Override
        public void undo()
        {
            if ( mShowing ) {
                mManifestation .removeConstruction( mConstruction );
                if ( mManifestation .isUnnecessary() ) {
                    mManifestations .hide( mManifestation ); // TODO make this more immediate, call renderer here
                    mManifestations .remove(  mManifestation );
                }
            }
            else {
                if ( mManifestation .isUnnecessary() )
                    mManifestations .add(  mManifestation );
                // note the asymmetry... we want to unhide when adding
                mManifestations .show( mManifestation ); // TODO make this more immediate, call renderer here
                mManifestation .addConstruction( mConstruction );
            }
        }

        @Override
        public Element getXml( Document doc )
        {
            Element result = this .mShowing ? doc .createElement( "mshow" ) : doc .createElement( "mhide" );
            Element man = mConstruction .getXml( doc );
            result .appendChild( man );
            return result;
        }

        public boolean showsManifestation( Manifestation man )
        {
            return this .mShowing && this .mManifestation .equals( man );
        }
    }

    private class RenderManifestation implements SideEffect
    {
        private final Manifestation mManifestation;

        private final boolean mShowing;

        public RenderManifestation( Manifestation manifestation, boolean showing )
        {
            mManifestation = manifestation;
            mShowing = showing;
        }

        @Override
        public void redo()
        {
            mManifestation .setHidden( ! mShowing );
            if ( mShowing )
                mManifestations .show( mManifestation ); // TODO make this more immediate, call renderer here
            else
                mManifestations .hide( mManifestation );
        }

        @Override
        public void undo()
        {
            mManifestation .setHidden( mShowing );
            if ( mShowing )
                mManifestations .hide( mManifestation );
            else
                mManifestations .show( mManifestation );
        }

        @Override
        public Element getXml( Document doc )
        {
            Element result = this .mShowing ? doc .createElement( "show" ) : doc .createElement( "hide" );
            Element man = mManifestation .getXml( doc );
            result .appendChild( man );
            return result;
        }

        public boolean showsManifestation( Manifestation man )
        {
            return this .mShowing && this .mManifestation .equals( man );
        }

    }

    private class DeleteManifestation implements SideEffect
    {
        private final Manifestation mManifestation;

        public DeleteManifestation( Manifestation manifestation )
        {
            mManifestation = manifestation;
        }

        @Override
        public void redo()
        {
            mManifestation .setHidden( true );
            mManifestations .hide( mManifestation );
            mManifestations .remove( mManifestation );
        }

        @Override
        public void undo()
        {
            mManifestations .add( mManifestation );
            mManifestations .show( mManifestation );
            mManifestation .setHidden( false );
        }

        @Override
        public Element getXml( Document doc )
        {
            Element result = doc .createElement( "delete" );
            Element man = mManifestation .getXml( doc );
            result .appendChild( man );
            return result;
        }
    }

    private class ColorManifestation implements SideEffect
    {
        private final Manifestation mManifestation;

        private final Color oldColor, newColor;

        public ColorManifestation( Manifestation manifestation, Color color )
        {
            mManifestation = manifestation;
            this .newColor = color;
            RenderedManifestation rm = manifestation .getRenderedObject();
            if ( rm != null ) {
                oldColor = rm .getColor();
            }
            else
                oldColor = Color .GREY_TRANSPARENT; // TODO fix this case
        }

        @Override
        public void redo()
        {
            mManifestations .setColor( mManifestation, newColor );
        }

        @Override
        public void undo()
        {
            mManifestations .setColor( mManifestation, oldColor );
        }

        @Override
        public Element getXml( Document doc )
        {
            Element result = doc .createElement( "color" );
            DomUtils .addAttribute( result, "rgb", newColor .toString() );
            Element man = mManifestation .getXml( doc );
            result .appendChild( man );
            return result;
        }
    }


    // Commonly used Filtered Iterators

    protected Manifestations.ManifestationIterator getRenderedSelection() {
        return Manifestations.visibleManifestations(mSelection, Manifestations.Filters::isRendered);
    }

    protected Manifestations.ConnectorIterator getConnectors() {
        return Manifestations.getConnectors(mManifestations);
    }

    protected Manifestations.StrutIterator getStruts() {
        return Manifestations.getStruts(mManifestations);
    }

    protected Manifestations.PanelIterator getPanels() {
        return Manifestations.getPanels(mManifestations);
    }

    protected Manifestations.ConnectorIterator getVisibleConnectors() {
        return Manifestations.getVisibleConnectors(mManifestations);
    }

    protected Manifestations.StrutIterator getVisibleStruts() {
        return Manifestations.getVisibleStruts(mManifestations);
    }

    protected Manifestations.PanelIterator getVisiblePanels() {
        return Manifestations.getVisiblePanels(mManifestations);
    }

    protected Manifestations.ConnectorIterator getVisibleConnectors(Predicate<Connector> postFilter) {
        return Manifestations.getVisibleConnectors(mManifestations, postFilter);
    }

    protected Manifestations.StrutIterator getVisibleStruts(Predicate<Strut> postFilter) {
        return Manifestations.getVisibleStruts(mManifestations, postFilter);
    }

    protected Manifestations.PanelIterator getVisiblePanels(Predicate<Panel> postFilter) {
        return Manifestations.getVisiblePanels(mManifestations, postFilter);
    }

}
