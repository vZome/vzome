
package com.vzome.core.editor.api;

import java.util.Iterator;
import java.util.function.Predicate;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.vzome.core.construction.Color;
import com.vzome.core.construction.Construction;
import com.vzome.core.model.Connector;
import com.vzome.core.model.Manifestation;
import com.vzome.core.model.Panel;
import com.vzome.core.model.RealizedModel;
import com.vzome.core.model.Strut;
import com.vzome.xml.DomUtils;

public abstract class ChangeManifestations extends ChangeSelection
{
    protected final RealizedModel mManifestations;

    public ChangeManifestations( EditorModel editorModel )
    {
        super( editorModel .getSelection() );

        mManifestations = editorModel .getRealizedModel();
        mManifestations .clearPerEditManifestations();
    }

    @Override
    public void redo()
    {
        mManifestations .clearPerEditManifestations();
        super .redo();
    }

    @Override
    public void undo()
    {
        mManifestations .clearPerEditManifestations();
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
    	String sig = c .getSignature();
    	Manifestation m = mManifestations .findConstruction( c );
        if ( m == null )
            return null;
        Manifestation made = mManifestations .findPerEditManifestation( sig );
        if ( made != null )
            return made;
        if ( m .isUnnecessary() )  { // just manifested, not added yet
            // TODO: DJH: Can this be replaced by a HashSet since the key is always equal to the value.
            mManifestations .addPerEditManifestation( sig, m );
            plan( new ManifestConstruction( c, m, true ) );
        }
        else {
            // already manifested, just make sure it shows
            if ( ! m .isRendered() )
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

    public void labelManifestation( Manifestation m, String label )
    {
        plan( new LabelManifestation( m, label ) );
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
                if ( mManifestation .isUnnecessary() ) {
                    mManifestation .addConstruction( mConstruction );
                    mManifestations .add(  mManifestation );
                }
                // note the asymmetry... we want to unhide when adding
                mManifestations .show( mManifestation ); // TODO make this more immediate, call renderer here
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
            oldColor = manifestation .getColor();
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

    private class LabelManifestation implements SideEffect
    {
        private final Manifestation mManifestation;
        private final String oldLabel, newLabel;
        
        public LabelManifestation( Manifestation m, String label )
        {
            this.mManifestation = m;
            this.newLabel = label;
            this.oldLabel = m .getLabel();
        }

        @Override
        public void undo()
        {
            mManifestations .setLabel( mManifestation, oldLabel );
        }

        @Override
        public Element getXml( Document doc )
        {
            Element result = doc .createElement( "label" );
            DomUtils .addAttribute( result, "text", newLabel );
            Element man = mManifestation .getXml( doc );
            result .appendChild( man );
            return result;
        }

        @Override
        public void redo()
        {
            mManifestations .setLabel( mManifestation, newLabel );
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
