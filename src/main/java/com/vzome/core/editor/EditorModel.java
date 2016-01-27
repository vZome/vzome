package com.vzome.core.editor;

import java.util.HashSet;
import java.util.Set;

import com.vzome.core.commands.Command;
import com.vzome.core.construction.Construction;
import com.vzome.core.construction.Point;
import com.vzome.core.construction.Segment;
import com.vzome.core.model.Connector;
import com.vzome.core.model.Manifestation;
import com.vzome.core.model.RealizedModel;
import com.vzome.core.model.Strut;

public class EditorModel
{
    public EditorModel( RealizedModel realized, Selection selection, boolean oldGroups, Point originPoint )
	{
        mRealized = realized;
        mSelection = selection;
        this.oldGroups = oldGroups;

        Manifestation m = realized .manifest( originPoint );
        m .addConstruction( originPoint );
        realized .add( m );
        realized .show( m );
        mCenterPoint = originPoint;
    }
    
    public RealizedModel getRealizedModel()
    {
        return mRealized;
    }
	
	public Point getCenterPoint()
	{
	    return mCenterPoint;
	}
    
    public void setCenterPoint( Construction point )
    {
        mCenterPoint = (Point) point;
    }
    
    public Segment getSymmetrySegment()
    {
        return mSymmetryAxis;
    }
    
    public void setSymmetrySegment( Segment segment )
    {
        mSymmetryAxis = segment;
    }

    public UndoableEdit selectManifestation( Manifestation m, boolean replace )
	{
        ChangeSelection edit = new SelectManifestation( m, replace, mSelection, mRealized, false );
        if ( edit .selectionChanged() )
            return edit;
        else
            return new NoOp();
    }

    public UndoableEdit selectAll()
    {
        ChangeSelection edit = new SelectAll( mSelection, mRealized, false );
        if ( edit .selectionChanged() )
            return edit;
        else
            return new NoOp();
    }

    public UndoableEdit unselectAll()
    {
        ChangeSelection edit = new DeselectAll( mSelection, false );
        if ( edit .selectionChanged() )
            return edit;
        else
            return new NoOp();
    }

	public UndoableEdit unselectConnectors()
	{
        return new DeselectByClass( mSelection, true );
    }
    
	public UndoableEdit unselectStruts()
	{
        return new DeselectByClass( mSelection, false );
    }
    
    public UndoableEdit selectNeighbors()
    {
        ChangeSelection edit = new SelectNeighbors( mSelection, mRealized, false );
        if ( edit .selectionChanged() )
            return edit;
        else
            return new NoOp();
    }

    public UndoableEdit invertSelection()
    {
        return new InvertSelection( mSelection, mRealized, false );
        // always a change, by definition
    }
    
    private RealizedModel mRealized;

    protected Selection mSelection;
    
    private Point mCenterPoint;
    
    private Segment mSymmetryAxis;

    private final boolean oldGroups;

    public Construction getSelectedConstruction( Class kind )
    {
        if ( kind == Point .class )
            kind = Connector.class;
        else if ( kind == Segment .class )
            kind = Strut .class;
        else
            kind = null;
        Manifestation focus = mSelection .getSingleSelection( kind );
        if ( focus != null )
            return focus .getConstructions() .next();
        return null;
    }

    
    public UndoableEdit setSymmetryCenter( Construction target ) throws Command.Failure
    {
        Point newCenter = null;
        if ( target instanceof Point )
            newCenter = (Point) target;
        else if ( target != null )
            throw new Command.Failure( "Target is not a single ball." );
        if ( newCenter == null ) {
            newCenter = (Point) getSelectedConstruction( Point.class );
            if ( newCenter == null )
                throw new Command.Failure( "Selection is not a single ball." );
        }
        if ( newCenter .getLocation() .equals( mCenterPoint .getLocation() ) )
            return null;
        return new SymmetryCenterChange( this, newCenter );
    }
    
    public UndoableEdit setSymmetryAxis( Construction target ) throws Command.Failure
    {
        Segment newAxis = null;
        if ( target instanceof Segment )
            newAxis = (Segment) target;
        else if ( target != null )
            throw new Command.Failure( "Target is not a single strut." );
        if ( newAxis == null ) {
            newAxis = (Segment) getSelectedConstruction( Segment.class );
            if ( newAxis == null )
                throw new Command.Failure( "Selection is not a single strut." );
        }
        if ( ( mSymmetryAxis != null )
           && newAxis .getStart() .equals( mSymmetryAxis .getStart() )
           && newAxis .getEnd() .equals( mSymmetryAxis .getEnd() ) )
                return null;
        return new SymmetryAxisChange( this, newAxis );
    }
    

    public UndoableEdit groupSelection()
    {
        if ( !oldGroups && mSelection .isSelectionAGroup() )
            return new NoOp();
        else
            return new GroupSelection( mSelection, true );
    }

    public UndoableEdit ungroupSelection()
    {
        if ( oldGroups || mSelection .isSelectionAGroup() )
            return new GroupSelection( mSelection, false );
        else
            return new NoOp();
    }
    
    private final Set<Manifestation> failedConstructions = new HashSet<>();

    public void addFailedConstruction( Construction cons )
    {
        failedConstructions .add( mRealized .manifest( cons ) );
    }

    public boolean hasFailedConstruction( Construction cons )
    {
        return failedConstructions .contains( mRealized .manifest( cons ) );
    }
}
