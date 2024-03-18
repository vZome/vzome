
package com.vzome.core.editor.api;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.logging.Logger;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.vzome.core.commands.Command.Failure;
import com.vzome.core.commands.XmlSaveFormat;
import com.vzome.core.model.Connector;
import com.vzome.core.model.Group;
import com.vzome.core.model.GroupElement;
import com.vzome.core.model.Manifestation;
import com.vzome.core.model.Panel;
import com.vzome.core.model.Strut;
import com.vzome.xml.DomUtils;

public abstract class ChangeSelection extends SideEffects
{
    protected final Selection mSelection;

    private boolean groupingDoneInSelection;

    private boolean orderedSelection = false;

    private transient Deque<SideEffect> selectionEffects = null;

    protected static final Logger logger = Logger.getLogger( "com.vzome.core.editor.ChangeSelection" );

    public ChangeSelection( Selection selection )
    {
        mSelection = selection;
        groupingDoneInSelection = false;
    }

    public void setOrderedSelection( boolean orderedSelection )
	{
		this.orderedSelection = orderedSelection;
	}

	@Override
	public void undo()
	{
		if ( this .orderedSelection ) {

			Deque<SideEffect> stack = new ArrayDeque<SideEffect>();
			this .selectionEffects = stack;
			// selectionEffects must be set, so that selection-affecting side effects get pushed
			super.undo();
						
			// This is currently the only place that clear() is called.
			// If undo() is called when a failure occcurs (e.g. invalid inputs) 
			// on an edit with orderedSelection = true, then 
			// the clearing of the selection here was not being recorded in the history
			// and the document change listeners were not notified so,
			// for example, the parts tab and the measure tab would get out of sync.
			// The fact that the selection was cleared but not recorded in the history
			// Obviously makes for some potentially difficult to track bugs when reopening documents.
			// I don't know if any legacy behaviors will be broken if I remove this line.
			// The regression tests amd unit tests don't indicate any issue so 
			// I'll just comment it out for now and leave here it as a reminder.
			// mSelection .clear();
			
			// to let the SideEffects undo correctly, selectionEffects must be cleared
			this .selectionEffects = null;
			while ( ! stack .isEmpty() ) {
				SideEffect se = stack .pop();
				se .undo();
			}
		}
		else
			super.undo();
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

    protected void adjustSelection(Manifestation man, ActionEnum action) {
        switch (action) {
        case SELECT:
            select(man);
            break;

        case DESELECT:
            unselect(man);
            break;

        case IGNORE:
            break;

        default:
            logger.warning("unexpected action: " + action.name() );
            break;
        }
    }

    /**
     * Any subclass can override to alter loading, or migrate (insert other edits), etc.
     * ALWAYS DO SOME INSERT, or all trace of the command will disappear!
     */
    @Override
    public void loadAndPerform( Element xml, XmlSaveFormat format, Context context ) throws Failure
    {
        String grouping = xml .getAttribute( "grouping" );
        if ( this .groupingAware() && ( format .groupingDoneInSelection() || "2.1.1" .equals( grouping ) ) )
            groupingDoneInSelection = true;
        setXmlAttributes( xml, format );

        context .performAndRecord( this );
        //        if ( mSelection != null )
        //            System.out.print( " selection: " + mSelection .size() );
    }

    protected boolean groupingAware()
    {
        return false;
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
    }

    public void select( Manifestation man )
    {
        select( man, false );
    }

    public void recordSelected( Manifestation man )
    {
        if ( ! mSelection .manifestationSelected( man ) )
            return;
        plan( new RecordSelectedManifestation( man ) );
    }

    public void select( Manifestation man, boolean ignoreGroups )
    {
        if ( groupingDoneInSelection )  // the legacy form, pre 2.1.2
        {
            plan( new SelectManifestation( man, true ) );
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
                else if ( selectionEffects != null ) {
                    // undoing a deselect in an orderedSelection edit;
                    //  the actual selects will happen at the end, with pops
                    selectionEffects .push( this );
                }
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
    /// Support for correctly undoing commands that care about selection order.
    ////////////////////////

    // for an orderedSelection edit
    private class RecordSelectedManifestation implements SideEffect
    {
        private final Manifestation mMan;

        public RecordSelectedManifestation( Manifestation man )
        {
            mMan = man;
            logger .finest( "constructing RecordSelectedManifestation" );
        }

        @Override
        public void redo()
        {
            logger .finest( "redoing RecordSelectedManifestation" );
        }

        @Override
        public void undo()
        {
            logger .finest( "undoing RecordSelectedManifestation" );
            if ( selectionEffects == null )
                // in the pop phase in CS.undo()
                mSelection .select( mMan );
            else
                selectionEffects .push( this );
        }

        @Override
        public Element getXml(Document doc)
        {
            return doc .createElement( "recordSelected" );
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
