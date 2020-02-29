/*
 * Created on Jul 3, 2004
 */
package com.vzome.core.editor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.vzome.core.commands.Command;
import com.vzome.core.commands.Command.Failure;
import com.vzome.core.commands.XmlSaveFormat;
import com.vzome.core.editor.UndoableEdit.Context;
import com.vzome.core.math.DomUtils;
import com.vzome.core.model.Manifestation;
import com.vzome.xml.LocationData;

public class EditHistory implements Iterable<UndoableEdit>
{	
    private List<UndoableEdit> mEdits = new ArrayList<>();

    // mEditNumber is the number of "redone" UndoableEdits in mEdits

    private int mEditNumber = 0;

    private boolean breakpointHit = false;

    private static final Logger logger = Logger .getLogger( "com.vzome.core.EditHistory" );
    private static final Logger breakpointLogger = Logger .getLogger( "com.vzome.core.editor.Breakpoint" );

    public interface Listener
    {
        void showCommand( Element xml, int editNumber );
        
        void publishChanges();
    }

    public Listener listener;

    public void setListener( Listener listener )
    {
        this .listener = listener;
    }

    public void addEdit( UndoableEdit edit, Context context )
    {
        if ( ! edit .isDestructive() )
        {
            // just insert at the current point, don't invalidate the redoable edits
            mEdits .add( mEditNumber, edit );
            ++ mEditNumber;
            return;
        }

        if ( mEditNumber < mEdits .size() )
        {
            // There are "dead" edits to discard.  If any of them are sticky (i.e. represent a snapshot),
            //   we will make a branch and keep up to the last sticky edit.
            boolean makeBranch = false;
            // first, find the last isSticky() edit in the dead edits being removed.
            int lastStickyEdit = mEditNumber - 1;
            int deadEditIndex = mEditNumber;
            for ( Iterator<UndoableEdit> deadEdits = mEdits .listIterator( mEditNumber ); deadEdits .hasNext(); ) {
                UndoableEdit dead = deadEdits .next();
                if ( dead .isSticky() )
                {
                    makeBranch = true;
                    lastStickyEdit = deadEditIndex;
                }
                ++deadEditIndex;
            }
            Branch branch = makeBranch? new Branch( context ) : null;
            deadEditIndex = mEditNumber;
            // Now, remove all the dead edits.  Add them to the branch if necessary.
            for ( Iterator<UndoableEdit> deadEdits = mEdits .listIterator( mEditNumber ); deadEdits .hasNext(); ) {
                UndoableEdit removed = deadEdits .next();
                deadEdits .remove();
                if ( deadEditIndex <= lastStickyEdit )
                {
                    // note that lastStickyEdit is initialized with the last edit not discarded,
                    // so this can only be true if a sticky edit had been found, and the loop
                    // has not reached it yet
                    branch .addEdit( removed );
                }
                ++deadEditIndex;
            }
            if ( makeBranch )
            {
                mEdits .add( branch );
                ++ mEditNumber;
            }
        }
        mEdits .add( edit );
        ++ mEditNumber;
    }

    public UndoableEdit undoAll()
    {
        UndoableEdit last = null;
        do {
            UndoableEdit edit = undo();
            if ( edit == null )
                break;
            last = edit;
        } while ( true );
        this .listener .publishChanges();
        return last;
    }

    public UndoableEdit undoToManifestation( Manifestation man )
    {
        UndoableEdit edit = null;
        do {
            edit = undo();
            if ( edit == null )
                break;
            if ( ( edit instanceof ChangeManifestations )
                    && ((ChangeManifestations) edit) .showsManifestation( man ) ) {
                break;
            }
        } while ( true );
        this .listener .publishChanges();
        return edit;
    }

    public UndoableEdit redoToBreakpoint() throws Command.Failure
    {
        // always try once, to skip over a breakpoint we might be sitting on
        UndoableEdit edit = redo();
        if ( edit == null )
            return edit;
        do {
            if ( this .atBreakpoint() )
                break;
            edit = redo();
            if ( edit == null )
                break;
        } while ( true );
        this .listener .publishChanges();
        return edit;
    }
    
    private boolean atBreakpoint()
    {
        if ( mEditNumber == mEdits .size() )
            return false;
        UndoableEdit edit = mEdits .get( mEditNumber );
        return edit .hasBreakpoint() ;
    }

    public void setBreakpoints( int[] lineNumbers )
    {
        Arrays .sort( lineNumbers );
        int index = 0;
        int lineNumber = lineNumbers[ index ];
        for ( UndoableEdit edit : mEdits ) {
            int startLine = edit .getLineNumber();
            if ( startLine != 0 && startLine >= lineNumber ) {
                edit .setBreakpoint( true );
                ++index;
                if ( index < lineNumbers .length )
                    lineNumber = lineNumbers[ index ];
                else
                    // We must continue to loop, to clear old breakpoints
                    lineNumber = Integer .MAX_VALUE;
            } else {
                edit .setBreakpoint( false );
            }
        }
    }
    
    public List<Integer> getBreakpoints()
    {
        List<Integer> result = new ArrayList<>();
        for ( UndoableEdit edit : mEdits ) {
            if ( edit .hasBreakpoint() )
                result .add( edit .getLineNumber() );
        }
        return result;
    }
    
    public UndoableEdit redoAll( int breakpoint ) throws Command.Failure
    {
        UndoableEdit last = null;
        breakpointHit = false;  // different mechanism than the int parameter
        do {
            UndoableEdit edit = redo();
            if ( edit == null )
                break;
            last = edit;
            if ( breakpointHit )
            {
                breakpointHit = false;
                break;
            }
        } while ( breakpoint == -1 || mEditNumber < breakpoint );
        this .listener .publishChanges();
        return last;
    }

    public void goToEdit( int editNum ) throws Command.Failure
    {
        if ( editNum == -1 )
            // -1 means redoAll
            editNum = mEdits .size();
        if ( editNum == mEditNumber )
            return;
        // undo() and redo() inlined here to avoid isVisible() and block limitations
        while ( mEditNumber < editNum )
        {
            if ( mEditNumber == mEdits .size() )
                break;
            UndoableEdit undoable = mEdits .get( mEditNumber++ );
            undoable .redo();
        }
        while ( mEditNumber > editNum )
        {
            if ( mEditNumber == 0 )
                break;
            UndoableEdit undoable = mEdits .get( --mEditNumber );
            undoable .undo();
        }
        this .listener .publishChanges();
    }

    public UndoableEdit undo()
    {
        return this .undo( true );
    }

    public UndoableEdit undo( boolean useBlocks )
    {
        if ( mEditNumber == 0 )
            return null;
        UndoableEdit undoable = mEdits .get( --mEditNumber );
        if ( useBlocks && undoable instanceof EndBlock )
            return undoBlock();

        undoable .undo();
        logger .fine( "undo: " + undoable .toString() );

        if ( undoable instanceof BeginBlock )
            return undoable;
        if ( ! undoable .isVisible() )
            return undo(); // undo another one, until we find one we want to return
        this .listener .publishChanges();
        return undoable;
    }

    private UndoableEdit undoBlock()
    {
        UndoableEdit undone;
        do {
            undone = undo();
        } while ( ! (undone instanceof BeginBlock) );
        return undone;
    }
    
    public int getNextLineNumber()
    {
        if(mEdits.isEmpty())
            return 3; // this works on a brand new model with an empty EditHistory, though it's a bit of a hack
        
        int editNumber = mEditNumber;
        if( editNumber >= mEdits.size() )
            editNumber = mEdits.size() - 1; // avoid overflow by getting the last edit
        
        UndoableEdit undoable = mEdits .get( editNumber );
        if ( undoable instanceof DeferredEdit )
            return ((DeferredEdit) undoable) .getLineNumber();
        else
            // This happens upon the initial VSCode debugger launch if 
            // we don't set lastStickyEdit="-1" in the vZome file we're debugging
            // or when we single step past the last edit
            return 0;
    }

    public UndoableEdit redo() throws Command.Failure
    {
        return this .redo( true );
    }

    public UndoableEdit redo( boolean useBlocks ) throws Command.Failure
    {
        if ( mEditNumber == mEdits .size() )
            return null;
        UndoableEdit undoable = mEdits .get( mEditNumber++ );
        if ( useBlocks && undoable instanceof BeginBlock )
            return redoBlock();

        try {
            if ( logger .isLoggable( Level .FINE ) )
                logger .fine( "redo: " + undoable .toString() );
            undoable .redo();
        } catch ( RuntimeException e ) {
            if ( logger .isLoggable( Level .WARNING ) )
                logger .warning( "edit number that failed is " + (mEditNumber-1) );
            throw e;
        }

        if ( undoable instanceof EndBlock )
            return undoable;
        if ( ! undoable .isVisible() )
            return redo(); // redo another one, until we find one we want to return
        this .listener .publishChanges();
        return undoable;
    }

    private UndoableEdit redoBlock() throws Command.Failure
    {
        String lastSuccessfulRedo = "none";
        int startingEditNumber = mEditNumber;
        UndoableEdit redone;
        do {
            redone = redo();
            if(redone == null) {
                // Should never occur, but this loop has hung several times, so just in case.... 
                throw new IllegalStateException("All " + mEditNumber + " edits have been redone without reaching an EndBlock. " 
                        + "Starting edit number was " + startingEditNumber + ". "
                        + "Last successful redo was " + lastSuccessfulRedo + ". ");
            }
            lastSuccessfulRedo = redone.getClass().getSimpleName();
            if(logger.isLoggable(Level.FINE)) {
                String msg = "redoBlock is redoing edits from " + startingEditNumber + ". Current edit number is " + mEditNumber + ". Last redone was " + lastSuccessfulRedo;
//                System.out.println(msg);
                logger.fine(msg);
            }
        } while (! (redone instanceof EndBlock) );
        return redone;
    }

    public Element getDetailXml( Document doc )
    {
        Element result = doc .createElement( "EditHistoryDetails" );
        DomUtils .addAttribute( result, "editNumber", Integer.toString( this .mEditNumber ) );

        int edits = 0, lastStickyEdit=-1;
        for (UndoableEdit undoable : this) {
            Element edit = undoable .getDetailXml( doc );
            ++ edits;
            DomUtils .addAttribute( edit, "editNumber", Integer.toString( edits ) );
            if ( logger .isLoggable( Level.FINEST ) )
                logger .finest( "side-effect: " + DomUtils .getXmlString( edit ) );
            result .appendChild( edit );
            if ( undoable .isSticky() )
                lastStickyEdit = edits;
        }
        result .setAttribute( "lastStickyEdit", Integer .toString( lastStickyEdit ) );

        return result;
    }

    public Element getXml( Document doc )
    {
        Element result = doc .createElement( "EditHistory" );
        DomUtils .addAttribute( result, "editNumber", Integer.toString( this .mEditNumber ) );
        return result;
        // edits are now serialized in calling EditorController
    }

    public void mergeSelectionChanges()
    {
        // TODO record the state well enough that it can be recovered

        int cursor = mEditNumber;

        if ( cursor == 0 )
            return;
        -- cursor;
        UndoableEdit above = mEdits .get( cursor );

        if ( above instanceof ChangeManifestations )
            return;
        if ( ! ( above instanceof ChangeSelection ) )
            return;
        // okay, last edit was a selection change, now look if there's another one before it

        if ( cursor == 0 )
            return;
        -- cursor;
        UndoableEdit below = mEdits .get( cursor );

        if ( below instanceof ChangeManifestations )
            return;

        if ( below instanceof ChangeSelection )
        {
            // two in a row, wrap with begin/end pair
            UndoableEdit bracket = new BeginBlock( null );
            mEdits .add( cursor, bracket );
            bracket = new EndBlock( null );
            mEdits .add( bracket );
            mEditNumber += 2;
        }
        else if ( below instanceof EndBlock )
        {
            // match BeginBlock unless you find a real edit
            int scan = cursor - 1;
            boolean done = false;
            while ( ! done )
            {
                UndoableEdit next = mEdits .get( scan );
                if ( next instanceof ChangeManifestations )
                    return;
                if ( next instanceof ChangeSelection )
                    --scan; // keep going
                else if ( next instanceof BeginBlock )
                {
                    // merge new selection change into block by swapping with EndBlock
                    mEdits .remove( above );
                    mEdits .add( cursor, above );
                    return;
                }
                else
                    return;
            }
        }
    }

    public void replaceEdit( UndoableEdit oldEdit, UndoableEdit newEdit )
    {
        mEdits .set( mEdits .indexOf( oldEdit ), newEdit );
    }

    /**
     * This is used during DeferredEdit .redo(), possibly to migrate one UndoableEdit into several.
     * It must maintain the invariant that the next UndoableEdit is the next DeferredEdit to redo.
     * @param edit
     */
    public void insert( UndoableEdit edit )
    {
        mEdits .add( mEditNumber++, edit );
    }

    public class Breakpoint extends UndoableEdit
    {
        @Override
        public Element getXml( Document doc )
        {
            return doc .createElement( "Breakpoint" );
        }

        @Override
        public boolean isNoOp()
        {
            return false;
        }

        @Override
        public boolean isVisible()
        {
            return true;
        }

        @Override
        public void loadAndPerform( Element xml, XmlSaveFormat format,
                Context context ) throws Failure
        {
            context .performAndRecord( this );
        }

        @Override
        public void perform() throws Failure
        {
            breakpointLogger .info( "hit a Breakpoint at " + mEditNumber );
            breakpointHit = true;
        }

        @Override
        public void redo() throws Failure
        {
            perform();
        }

        @Override
        public void undo()
        {}

        @Override
        public void configure( Map<String,Object> props ) {}

        @Override
        public boolean isSticky()
        {
            return false;
        }

        @Override
        public boolean isDestructive()
        {
            return false;
        }

        @Override
        public Element getDetailXml( Document doc )
        {
            return getXml( doc );
        }
    }

    private class DeferredEdit extends UndoableEdit
    {
        private final XmlSaveFormat format;

        private final Element xml;

        private Context context;
        
        private boolean isBreakpoint = false;

        public DeferredEdit( XmlSaveFormat format, Element editElem, Context context )
        {
            this.format = format;
            this.xml = editElem;
            this.context = context;
        }

        @Override
        public void setBreakpoint( boolean value )
        {
            this .isBreakpoint = value;
        }
        
        @Override
        public boolean hasBreakpoint()
        {
            return this .isBreakpoint;
        }

        @Override
        public boolean isNoOp()
        {
            return false;
        }
        
        @Override
        public int getLineNumber()
        {
            LocationData locationData = (LocationData) xml .getUserData( LocationData .LOCATION_DATA_KEY );
            if ( locationData != null )
                return locationData .getStartLine();
            else
                return 0;
        }

        @Override
        public Element getXml( Document doc )
        {
            // Use doc.importNode() instead of returning the xml directly.
            // This avoids a org.w3c.dom.DOMException: WRONG_DOCUMENT_ERR: A node is used in a different document than the one that created it.
            //
            // This can occur in DocumentModel.serialize()
            // when saving a file that includes a DeferredEdit imported from Prototypes\golden.vZome
            // For example, the following snippet of XML as a valid part of that file can cause the condition described
            // by simply opening vzome and immediately selecting "Save As..." from the menu and saving the new empty file
            // which now includes the deferred edits from the prototype file.
            /*
              <EditHistory editNumber="0" lastStickyEdit="-1">
                <StrutCreation anchor="0 0 0 0 0 0" index="9" len="2 4"/>
              </EditHistory>
             */
            return ( doc.equals( xml.getOwnerDocument() ) )
                    ? xml
                            : (Element) doc.importNode(xml, true);
        }

        @Override
        public boolean isVisible()
        {
            return true;
        }

        @Override
        public boolean isDestructive()
        {
            return true;
        }

        @Override
        public void redo() throws Command.Failure
        {
            /*
             * The following things need to happen:
             * 
             * 1. this DeferredEdit will remove itself from the history
             * 
             * 2. a single UndoableEdit is created based on the xml
             * 
             * 3. the UndoableEdit may migrate itself, generating
             */
            int num = this .getLineNumber();
            mEdits .remove( --mEditNumber );

            if ( logger.isLoggable( Level.FINE ) ) // see the logger declaration to enable FINE
                logger.fine( "%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% " + num + ": " + DomUtils .getXmlString( xml ) );

            UndoableEdit realized = null;
            String cmdName = xml.getLocalName();
            
            if ( cmdName .equals( "Breakpoint" ) )
            {
                realized = new Breakpoint();
            }
            else
                realized = context .createEdit( xml );
            //            System.out.println( "edit: " + num + " " + cmdName );
            realized .setLineNumber( num );

            try {
                EditHistory .this .listener .showCommand( xml, num );
                realized. loadAndPerform(xml, format, new UndoableEdit.Context()
                {
                    @Override
                    public void performAndRecord( UndoableEdit edit )
                    {
                        // realized is responsible for inserting itself, or any replacements (migration)
                        try {
                            edit .perform();
                            if ( edit .isNoOp() )
                                return;
//                            System.out.println( DomUtils .getXmlString( details ) );
                            if ( logger .isLoggable( Level.FINEST ) ) {
                                Element details = edit .getDetailXml( xml .getOwnerDocument() );
                                logger .finest( "side-effect: " + DomUtils .getXmlString( details ) );
                            }
                        } catch (Failure e) {
                            // really hacky tunneling
                            throw new RuntimeException( e );
                        }
                        EditHistory .this .insert( edit );
                    }

                    @Override
                    public UndoableEdit createEdit( Element xml )
                    {
                        UndoableEdit edit = context .createEdit( xml );
                        edit .setLineNumber( getLineNumber() );
                        return edit;
                    }
                } ); // this method needs to have the history, since it may migrate
                //        		System.out.println();

                // no longer doing redo() and mHistory.replace() here, so each UndoableEdit may
                // either migrate itself, or determine whether it requires a redo() after deserialization.
            } catch ( RuntimeException e ) {
                logger.warning( "failure during initial edit replay:\n" + DomUtils .getXmlString( xml ) );
                // errors will be reported by caller!
                // mErrors .reportError( UNKNOWN_ERROR_CODE, new Object[]{ e } );
                throw e; // interrupt the redoing
            }
        }

        @Override
        public void loadAndPerform( Element xml, XmlSaveFormat format, Context context ) throws Failure
        {
            throw new IllegalStateException( "should never be called" );
        }

        @Override
        public void undo()
        {
            // called, but must be a no-op
        }

        @Override
        public void configure( Map<String,Object> props ) {}

        @Override
        public void perform()
        {
            // never called
        }

        @Override
        public boolean isSticky()
        {
            return false;
        }

        @Override
        public Element getDetailXml( Document doc )
        {
            return doc .createElement( "deferredEdit" );
        }
    }

    /**
     * Redo to greater of lastStickyEdit and lastDoneEdit, undo back to lastDoneEdit.
     * If there are explicitSnapshots, this is a migration of an old Article, using edit
     * numbers, and we have to redo as far as the last one, inserting snapshots as we go.
     * Note that lastStickyEdit and explicitSnapshots are mutually exclusive, after and before
     * migration, respectively.
     * 
     * @param lastDoneEdit
     * @param lastStickyEdit
     * @param explicitSnapshots 
     * @throws Failure
     */
    public void synchronize( int lastDoneEdit, int lastStickyEdit, UndoableEdit[] explicitSnapshots ) throws Failure
    {
        int redoThreshold = Math .max( lastDoneEdit, lastStickyEdit );

        if ( explicitSnapshots != null )
            redoThreshold = Math .max( redoThreshold, explicitSnapshots .length - 1 );

        mEditNumber = 0;
        int targetEdit = 0;
        List<UndoableEdit> toRedo = new ArrayList<>();
        // here the edits are all still DeferredEdits
        for ( int i = 0; i < redoThreshold; i++ )
            if ( i < mEdits .size() )
                toRedo .add( mEdits .get( i ) );
            else
                break;
        for ( int oldIndex = 0; oldIndex < toRedo .size(); oldIndex++ )
        {	            
            DeferredEdit edit = (DeferredEdit) toRedo .get( oldIndex );
            try {
                if ( explicitSnapshots != null
                        && explicitSnapshots .length > oldIndex
                        && explicitSnapshots[ oldIndex ] != null )
                {
                    // a snapshot editNum of 3 means a snapshot *before* edit #3 is redone,
                    //  so we do this snapshot migration first
                    UndoableEdit snapshot = explicitSnapshots[ oldIndex ];
                    mEdits .add( mEditNumber, snapshot );
                    // keep lastDoneEdit in alignment
                    if ( mEditNumber <= lastDoneEdit )
                        ++ lastDoneEdit;
                    ++ mEditNumber;
                    snapshot .perform();
                }

                ++ mEditNumber;  //match the preconditions like this.redo()
                edit .redo();
                // now the edit is realized

                // lastDoneEdit is in terms of the edits in the file, and we need
                //  to translate it to match the actual edit numbers, after migration
                //  and snapshot creation.  We know this condition will succeed once,
                //  since toRedo.size() is at least as big as lastDoneEdit.
                if ( oldIndex+1 == lastDoneEdit )
                    targetEdit = mEditNumber;

            } catch ( RuntimeException e ) {
                if ( logger.isLoggable( Level.WARNING ) )
                    logger.warning( "edit number that failed is " + ( this .mEditNumber - 1 ) );
                // unwrap
                Throwable t = e.getCause();
                if ( t instanceof Command.Failure )
                    throw (Command.Failure) t;
                else
                    throw e;
            }
        }
        if ( explicitSnapshots != null
                && explicitSnapshots .length > redoThreshold
                && explicitSnapshots[ redoThreshold ] != null )
        {
            // a snapshot editNum of 3 means a snapshot *before* edit #3 is redone,
            //  so we do this snapshot migration first
            UndoableEdit snapshot = explicitSnapshots[ redoThreshold ];
            mEdits .add( mEditNumber, snapshot );
            ++ mEditNumber;
            snapshot .perform();
        }
        goToEdit( targetEdit );
    }

    public void loadEdit( XmlSaveFormat format, Element editElem, Context context )
    {
        DeferredEdit edit = new DeferredEdit( format, editElem, context );
        this .addEdit( edit, context );
    }

    @Override
    public Iterator<UndoableEdit> iterator()
    {
        return this .mEdits .iterator();
    }

    public int getEditNumber()
    {
        return this .mEditNumber;
    }
}
