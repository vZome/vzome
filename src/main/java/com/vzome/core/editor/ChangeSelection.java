
//(c) Copyright 2006, Scott Vorthmann

package com.vzome.core.editor;

import java.util.logging.Logger;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.vzome.core.commands.XmlSaveFormat;
import com.vzome.core.commands.Command.Failure;
import com.vzome.core.math.DomUtils;
import com.vzome.core.model.Connector;
import com.vzome.core.model.Group;
import com.vzome.core.model.GroupElement;
import com.vzome.core.model.Manifestation;
import com.vzome.core.model.Panel;
import com.vzome.core.model.Strut;

public abstract class ChangeSelection extends SideEffects
{
    protected final Selection mSelection;
    
    private boolean mDirty = false;
    
    private boolean groupingDoneInSelection;

    protected static final Logger logger = Logger.getLogger( "com.vzome.core.editor.ChangeSelection" );

    ChangeSelection( Selection selection )
    {
        this(selection, false);
    }

    ChangeSelection( Selection selection, boolean groupInSelection )
    {
        mSelection = selection;
        groupingDoneInSelection = groupInSelection;
    }

    protected void getXmlAttributes( Element element ) {}
    
    protected void setXmlAttributes( Element xml, XmlSaveFormat format ) throws Failure {}
    
    protected abstract String getXmlElementName();
    
    @Override
    public Element getXml( Document doc )
    {
        Element result = doc .createElement( getXmlElementName() );
        if ( groupingDoneInSelection )
            DomUtils .addAttribute( result, "grouping", "2.1.1" );
        getXmlAttributes( result );
        return result;
    }

    /**
     * Any subclass can override to alter loading, or migrate (insert other edits), etc.
     * ALWAYS DO SOME INSERT, or all trace of the command will disappear!
     */
    @Override
    public void loadAndPerform( Element xml, XmlSaveFormat format, Context context ) throws Failure
    {
        String grouping = xml .getAttribute( "grouping" );
        if ( "2.1.1" .equals( grouping ) )
            groupingDoneInSelection = true;
        setXmlAttributes( xml, format );
        
        context .performAndRecord( this );
//        if ( mSelection != null )
//            System.out.print( " selection: " + mSelection .size() );
    }

    public boolean selectionChanged()
    {
        return mDirty;
    }
    
    protected void setDirty()
    {
        mDirty = true;
    }
    
    public void unselect( Manifestation man )
    {
        unselect( man, false );
    }
    
    public void unselect( Manifestation man, boolean ignoreGroups )
    {
        if ( groupingDoneInSelection )  // the legacy form, pre 2.1.2
        {
            plan( new SelectManifestation( man, false ) );
            mDirty = true;
            return;
        }
        if ( man == null )
        {
            // 2015-02-10
            //
            // Tonight I printed out enough hashcode() results to know that the defect here
            //   is a disparity between the manifestations created during initial edit
            //   and the manifestations created during replay of history on file load.
            //   The defect could be in any edit.  Some implicit parameter that is not captured
            //   on serialization?
            //
            //  To find the defect, I think I'll have to implement some sort of self-check mode
            //  that compares the live history as it is replayed on open with a recorded history
            //  from the initial save.  That still might not catch it, if the disparity is actually
            //  between the realized model and the in-memory history!  Hmmm... the later disparity
            //  should show up as unnecessary side-effects that cannot be undone.
            //  
            //  The defect is an old one, and it still exists, since even files from 2014 exhibit
            //  the accommodations.
            logBugAccommodation( "null manifestation" );
            return;
        }
        
        if ( ! mSelection .manifestationSelected( man ) )
            return;
        Group group = ignoreGroups? null : Selection .biggestGroup( man );
        if ( group == null )
            plan( new SelectManifestation( man, false ) );
        else
            unselectGroup( group );
    	mDirty = true;
    }
    
    public void select( Manifestation man )
    {
        select( man, false );
    }
    
    public void select( Manifestation man, boolean ignoreGroups )
    {
        if ( groupingDoneInSelection )  // the legacy form, pre 2.1.2
        {
            plan( new SelectManifestation( man, true ) );
            mDirty = true;
            return;
        }
        if ( man == null )
        {
            logBugAccommodation( "null manifestation" );
            return;
        }
        
        if ( mSelection .manifestationSelected( man ) )
            return;
        Group group = ignoreGroups? null : Selection .biggestGroup( man );
        if ( group == null )
            plan( new SelectManifestation( man, true ) );
        else
            selectGroup( group );
    	mDirty = true;
    }
    
    private void selectGroup( Group group )
    {
        for (GroupElement next : group) {
            if ( next instanceof Group )
                selectGroup( (Group) next );
            else
                plan( new SelectManifestation( (Manifestation) next, true ) );
        }
    }
    
    private void unselectGroup( Group group )
    {
        for (GroupElement next : group) {
            if ( next instanceof Group )
                unselectGroup( (Group) next );
            else
                plan( new SelectManifestation( (Manifestation) next, false ) );
        }
    }
    
    private class SelectManifestation implements SideEffect
    {
        private final Manifestation mMan;
        
        private final boolean mOn;

        public SelectManifestation( Manifestation man, boolean value )
        {
            mMan = man;
            mOn = value;
            logger .finest( "constructing SelectManifestation" );
        }

        @Override
        public void redo()
        {
            if ( groupingDoneInSelection ) {
                if ( mOn )
                    mSelection .selectWithGrouping( mMan );
                else
                    mSelection .unselectWithGrouping( mMan );
            }
            else
                if ( mOn )
                    mSelection .select( mMan );
                else
                    mSelection .unselect( mMan );
        }

        @Override
        public void undo()
        {
            if ( groupingDoneInSelection ) {
                if ( mOn )
                    mSelection .unselectWithGrouping( mMan );
                else
                    mSelection .selectWithGrouping( mMan );
            }
            else
                if ( mOn )
                    mSelection .unselect( mMan );
                else
                    mSelection .select( mMan );
        }

        @Override
        public Element getXml( Document doc )
        {
            Element result = this .mOn ? doc .createElement( "select" ) : doc .createElement( "deselect" );
            if ( mMan != null ) {
                Element man = mMan .getXml( doc );
                result .appendChild( man );
            }
            return result;
        }
    }

    ////////////////////////
    /// getSelected...
    ////////////////////////

    protected Manifestations.ConnectorIterator getSelectedConnectors() {
        return Manifestations.getConnectors(mSelection);
    }

    protected Manifestations.StrutIterator getSelectedStruts() {
        return Manifestations.getStruts(mSelection);
    }

    protected Manifestations.PanelIterator getSelectedPanels() {
        return Manifestations.getPanels(mSelection);
    }

    ////////////////////////
    /// getLastSelected...
    ////////////////////////

    public Manifestation getLastSelectedManifestation() {
        Manifestation last = null;
        for( Manifestation man : mSelection ) {
            last = man;
        }
        return last;
    }

    public Connector getLastSelectedConnector() {
        Connector last = null;
        for( Connector connector : getSelectedConnectors() ) {
            last = connector;
        }
        return last;
    }

    public Strut getLastSelectedStrut() {
        Strut last = null;
        for( Strut strut : getSelectedStruts() ) {
            last = strut;
        }
        return last;
    }

    public Panel getLastSelectedPanel() {
        Panel last = null;
        for( Panel panel : getSelectedPanels() ) {
            last = panel;
        }
        return last;
    }

    ////////////////////////
    // unselect...
    ////////////////////////

    public boolean unselectAll() {
        boolean anySelected = false;
        for( Manifestation man : mSelection ) {
            anySelected = true;
            this.unselect(man);
        }
        if(anySelected) {
            redo();
        }
        return anySelected;
    }

    public boolean unselectConnectors() {
        boolean anySelected = false;
        for( Connector connector : getSelectedConnectors() ) {
            anySelected = true;
            this.unselect(connector);
        }
        if(anySelected) {
            redo();
        }
        return anySelected;
    }

    public boolean unselectStruts() {
        boolean anySelected = false;
        for( Strut strut : getSelectedStruts() ) {
            anySelected = true;
            this.unselect(strut);
        }
        if(anySelected) {
            redo();
        }
        return anySelected;
    }

    public boolean unselectPanels() {
        boolean anySelected = false;
        for( Panel panel : getSelectedPanels() ) {
            anySelected = true;
            this.unselect(panel);
        }
        if(anySelected) {
            redo();
        }
        return anySelected;
    }

}
