package com.vzome.core.editor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.vzome.core.commands.AbstractCommand;
import com.vzome.core.commands.Command;
import com.vzome.core.commands.CommandHide;
import com.vzome.core.commands.CommandObliquePentagon;
import com.vzome.core.commands.CommandTransform;
import com.vzome.core.commands.XmlSaveFormat;
import com.vzome.core.commands.Command.Failure;
import com.vzome.core.construction.Construction;
import com.vzome.core.construction.ConstructionChanges;
import com.vzome.core.construction.ConstructionList;
import com.vzome.core.construction.ModelRoot;
import com.vzome.core.construction.Segment;
import com.vzome.core.math.DomUtils;
import com.vzome.core.model.Manifestation;



/**
 * Just a mechanism to incorporate the legacy edit mechanism into the new undo/redo.
 * 
 * @author Scott Vorthmann 2006
 */
public class CommandEdit extends ChangeConstructions
{
    public static final String UNKNOWN_COMMAND = "unknown.command";

	private EditorModel mEditorModel;
	
    private AbstractCommand mCommand;
    
    private ModelRoot modelRoot;
    
    private Map mAttrs;
        
	public CommandEdit( AbstractCommand cmd, EditorModel editor, ModelRoot derivationModel, boolean groupInSelection )
    {
        super( editor .mSelection, editor .getRealizedModel(), groupInSelection );
        mEditorModel = editor;  // only needed to set symmetry axis/center
        modelRoot = derivationModel;
        mCommand = cmd;
    }

    protected String getXmlElementName()
    {
        String cmdName = mCommand .getClass() .getName();
        int lastDot = cmdName .lastIndexOf( '.' );
        return cmdName .substring( lastDot + 1 + "Command".length() );
    }

    public void getXmlAttributes( Element result )
    {
        mCommand .getXml( result, mAttrs );
    }
    
    public void setXmlAttributes( Element xml, XmlSaveFormat format ) throws Command.Failure
    {        
        mAttrs = mCommand .setXml( xml, format );
        mAttrs .put( Command .LOADING_FROM_FILE, Boolean .TRUE );
    }

    public void perform() throws Command.Failure
    {
        boolean isHide = mCommand instanceof CommandHide;
        
        ConstructionList constrsBefore = new ConstructionList();
        for ( Iterator mans = mSelection .iterator(); mans .hasNext(); )
        {
            Manifestation man = (Manifestation) mans .next();
            // SELECTION BUG: the unselect() below just plans the unselection, but... (search for the next SELECTION BUG comment)
            unselect( man );
            if ( isHide )
                hideManifestation( man );
            else {
                Iterator cs = man .getConstructions();
                if ( ! cs .hasNext() )
                    throw new Command.Failure( "No construction for this manifestation" );
                constrsBefore .add( (Construction) cs .next() );  // yes, just using the first one
            }
        }
        
        // SELECTION BUG: what we want to do here is to redo what we've planned so far, so that the selection state is in sync
        redo();

        if ( isHide )
            return;

        if ( mAttrs == null )
            mAttrs = new HashMap();
        Segment symmAxis = mEditorModel .getSymmetrySegment();
        if ( symmAxis != null )
            mAttrs .put( CommandTransform .SYMMETRY_AXIS_ATTR_NAME, symmAxis );
        mAttrs .put( CommandTransform .SYMMETRY_CENTER_ATTR_NAME, mEditorModel .getCenterPoint() );
        mAttrs .put( Command .MODEL_ROOT_ATTR_NAME, modelRoot );
        mAttrs .put( Command.FIELD_ATTR_NAME, modelRoot .getField() );
        
        // TODO do we really need to do it this way?  Or can we use the other NewConstructions() class?
        //   That one does the manifestConstruction() and redo() immediately.
        NewConstructions news = new NewConstructions();
        
        ConstructionList selectionAfter = null;  // use params to make selection additive
        Object[][] signature = mCommand .getParameterSignature();
        int actualsLen = constrsBefore .size();
        if ( ( signature.length == actualsLen )
        || ( signature.length==1 && signature[0][0] .equals( Command.GENERIC_PARAM_NAME ) ) )
            try {
                // command specifically applies to a collection of constructions (like a Transformation)
                selectionAfter = mCommand .apply( constrsBefore, mAttrs, news );
            } catch ( Failure f ) {
                undo();  // NEW SELECTION BUG: undo the redo above
                throw f;
            }
        else if ( signature.length > actualsLen )
            fail( "Too few objects in the selection." );
        else if ( signature.length == 1 ) {
            // parallel applications
            ConstructionList partial;
            selectionAfter = new ConstructionList();
            for ( int i = 0; i < actualsLen; i++ ) {
                Object param = constrsBefore .get( i );
                if ( ((Class) signature[0][1]) .isAssignableFrom( param .getClass() ) ) {
                    ConstructionList single = new ConstructionList();
                    single .addConstruction( (Construction) param );
                    partial = mCommand .apply( single, mAttrs, news );
                    selectionAfter .addAll( partial );
                }
                else
                    selectionAfter .add( param ); // leave inappropriate Constructions in the new selection
            }
        }
        else
            fail( "Too many objects in the selection." );
        
        for ( Iterator adds = news .iterator(); adds .hasNext(); ) {
            Construction c = (Construction) adds .next();
            addConstruction( c );
            manifestConstruction( c );
        }
        for ( int i = 0; i < selectionAfter .size(); i++ ) {
            Construction cons = (Construction) selectionAfter .get( i );

            // here we accommodate model files that include failed edits in their history
            if ( cons .failed() )
            {
                logBugAccommodation( "failed construction" );
                mEditorModel .addFailedConstruction( cons );
                continue;
            }
            Manifestation man = manifestConstruction( cons );
            if ( man != null )
                // SELECTION BUG: ... if no planned unselect is redone yet,
                //    any construction selected before and after will not be unselected yet,
                //    so this select will be a no-op                             (search for the preceding SELECTION BUG comment)
                select( man );
        }
        redo();
    }
    
    public void loadAndPerform( Element xml, XmlSaveFormat format, Context context )
            throws Failure
    {
        Logger logger = Logger .getLogger( "com.vzome.core.editor.CommandEdit.loadAndPerform" );
        String cmdName = null;
        if ( format .selectionsNotSaved() )  //&& ! format .interim210format() )
            cmdName = xml .getLocalName();
        else if ( format .commandEditsCompacted() )
            cmdName = "Command" + xml .getLocalName();
        else
            cmdName = xml .getAttribute( "command" );
        if ( cmdName .equals( "CommandIcosahedralSymmetry" ) )
            cmdName = "CommandSymmetry";
        try {
            Class clazz = Class .forName( "com.vzome.core.commands." + cmdName );
            mCommand = (AbstractCommand) clazz .newInstance();
        } catch ( Exception e ) {
            logger .log( Level.SEVERE, "error creating command: " + xml .getLocalName(), e );
            throw new Failure( UNKNOWN_COMMAND );
        }

        if ( format .selectionsNotSaved() )
        {
            // this edit needs to be migrated
            List selectedBefore = new ArrayList();
            context .performAndRecord( new BeginBlock() );

            mAttrs = new HashMap();

            NodeList nodes = xml .getChildNodes();
            for ( int j = 0; j < nodes .getLength(); j++ ) {
                Node kid2 = nodes .item( j );
                if ( kid2 instanceof Element ) {
                    Element attrOrParam = (Element) kid2;
                    String apName = attrOrParam .getLocalName();
                    if ( apName .equals( "attr" ) )
                    {
                        // these are command attributes, they may need to be migrated
                        String attrName = attrOrParam .getAttribute( "name" );

                        // accommodate older versions
                        if ( attrName .endsWith( ".symmetry.center" ) )
                            attrName = CommandTransform .SYMMETRY_CENTER_ATTR_NAME;
                        else if ( attrName .equals( "reflection.mirror.normal.segment" ) )
                            attrName = CommandTransform .SYMMETRY_AXIS_ATTR_NAME;

                        Element val = DomUtils .getFirstChildElement( attrOrParam );
                        String valName = val .getLocalName();

                        if ( valName .equals( "FreePoint" ) )
                            valName = "point"; // compat with save format until first release AFTER 2005-06-18

                        Object value = format .parseAlgebraicObject( valName, val );
                        if ( value == XmlSaveFormat .NOT_AN_ATTRIBUTE )
                            value = format .parseConstruction( valName, val, mCommand .attributeIs3D( attrName ) );
                        // TODO verify that w==0 in all existing models.  This may not be true if
                        //   I hand-edited a model to use a quaternion for 4D rotation

                        if ( attrName .equals( CommandTransform .SYMMETRY_CENTER_ATTR_NAME ) )
                        {
                            UndoableEdit edit = mEditorModel .setSymmetryCenter( (Construction) value );
                            if ( edit != null ) {
                                context .performAndRecord( edit );
                            }
                        }
                        else if ( attrName .equals( CommandTransform .SYMMETRY_AXIS_ATTR_NAME ) )
                        {
                            UndoableEdit edit = mEditorModel .setSymmetryAxis( (Construction) value );
                            if ( edit != null ) {
                                context .performAndRecord( edit );
                            }
                            if ( ! mCommand .attributeIs3D( attrName ) )
                            {
                                int[] vector = ((Segment) value) .getOffset();
                                // Assume this axis is used as a quaternion.
                                //  Note, in format .selectionsNotSaved() (which this is),
                                //  (w,x,y,z) meant x + yi + zj + wk, whereas the Quaternion class
                                //  currently reads (w,x,y,z) as w + xi + yj + zk.
                                //
                                //  However, for some reason, the shift is only indicated for
                                //  ImportVEF, and is counter-indicated for H4Polytope
                                mCommand .setQuaternion( vector );
                            }
                        }
                        else
                            mAttrs .put( attrName, value );

                    } else { 
                        // these are selection parameters, they need to become selection commands
                        //   TODO: verify that w==0 in all existing models!  This may not be true
                        //   if the selected construction was a projection of a 4D point.
                        Construction c = format .parseConstruction( apName, attrOrParam, true );
                        
                        // remember that we're preparing to create selection commands, not creating them yet;
                        //  see below for the actual creation
                        if ( c != null ) {
                            
                            if ( mEditorModel .hasFailedConstruction( c ) )
                            {
                                logBugAccommodation( "skip selecting a failed construction" );
                                continue;
                            }
                            
                            // find c's manifestation... should never be null!
                            Manifestation m = getManifestation( c );
                            if ( m == null || m .isUnnecessary() )
                            {
                                logger .severe( "CommandEdit parameter: " + attrOrParam .toString() );
                                throw new Command.Failure( "no manifestation to be selected." );
                            }
                            if ( ! selectedBefore .contains( m ) ) // expensive, but we must use an ordered list, and avoid duplicates
                                selectedBefore .add( m );
                        }
                    }
                }
            }

            // now create the selection commands efficiently, starting with either selectAll or unselectAll

            if ( selectedBefore .size() > mManifestations .size() / 2 )
            {
                Collection toUnselect = new ArrayList();
                for ( Iterator it = mManifestations .getAllManifestations(); it .hasNext(); )
                {
                    Manifestation m = (Manifestation) it .next();
                    if ( ! selectedBefore .contains( m ) )
                        toUnselect .add( m );
                }
                ChangeSelection edit = new SelectAll( mSelection, mManifestations, false );
                context .performAndRecord( edit );
                for ( Iterator it = toUnselect .iterator(); it .hasNext(); )
                {
                    Manifestation m = (Manifestation) it .next();
                    edit = new SelectManifestation( m, false, mSelection, mManifestations, false );
                    context .performAndRecord( edit );
                }
            }
            else {
                ChangeSelection edit = new DeselectAll( mSelection, false );
                context .performAndRecord( edit );
                for ( Iterator it = selectedBefore .iterator(); it .hasNext(); )
                {
                    Manifestation m = (Manifestation) it .next();
                    edit = new SelectManifestation( m, false, mSelection, mManifestations, false );
                    context .performAndRecord( edit );
                }
            }
            context .performAndRecord( new EndBlock() );
            this .redo(); // sync the selection state
            
            // TODO work out a more generic migration technique
            if ( mCommand instanceof CommandObliquePentagon )
            {
                UndoableEdit edit = new AffinePentagon( mSelection, mManifestations, modelRoot, false );
                context .performAndRecord( edit );
                return;
            }
            
            mCommand .setFixedAttributes( mAttrs, format );

            mAttrs .put( Command .LOADING_FROM_FILE, Boolean .TRUE );

            context .performAndRecord( this );
        }
        else
            super.loadAndPerform( xml, format, context );
    }

    private static class NewConstructions extends ArrayList implements ConstructionChanges
    {
        public void constructionAdded( Construction c )
        {
            add( c );
        }

        public void constructionHidden( Construction c )
        {
            throw new UnsupportedOperationException();
        }

        public void constructionRemoved( Construction c )
        {
            throw new UnsupportedOperationException();
        }

        public void constructionRevealed( Construction c )
        {
            throw new UnsupportedOperationException();
        }
    }

}
