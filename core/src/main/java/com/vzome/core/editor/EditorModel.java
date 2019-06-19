package com.vzome.core.editor;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashSet;
import java.util.Map;
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
    public EditorModel( RealizedModel realized, Selection selection, Point originPoint, FieldApplication kind, SymmetrySystem symmetrySystem, Map<String, SymmetrySystem> symmetrySystems )
    {
        mRealized = realized;
        mSelection = selection;
        this.kind = kind;
        this.symmetrySystem = symmetrySystem;
        this.symmetrySystems = symmetrySystems;
        for ( SymmetrySystem symmetrySys : symmetrySystems .values()) {
            symmetrySys .setEditorModel( this );
        }

        this .selectionSummary = new SelectionSummary( this .mSelection );

        Manifestation m = realized .manifest( originPoint );
        m .addConstruction( originPoint );
        realized .add( m );
        realized .show( m );
        mCenterPoint = originPoint;
    }

    public void addSelectionSummaryListener( SelectionSummary.Listener listener )
    {
        this .selectionSummary .addListener( listener );
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
            
    public UndoableEdit createEdit( String name )
    {
        // map legacy command names (left) to actual class names
        switch ( name ) {
        case "setItemColor":        name = "ColorManifestations";
            break;
        case "BnPolyope":           name = "B4Polytope";
            break;
        case "DeselectByClass":     name = "AdjustSelectionByClass";
            break;
        case "realizeMetaParts":    name = "RealizeMetaParts";
            break;
        case "SelectSimilarSize":   name = "AdjustSelectionByOrbitLength";
            break;
        case "zomic":               name = "RunZomicScript";
            break;
        case "py":                  name = "RunPythonScript";
            break;
        case "apiProxy":            name = "ApiEdit";
            break;
        }
        String className = this.getClass() .getPackage() .getName() + "." + name;
        try {
            Class<?> factoryClass = Class.forName( className );

            Constructor<?>[] constructors = factoryClass .getConstructors();
            Constructor<?> goodConstructor = null, editorConstructor = null;
            for ( Constructor<?> constructor : constructors ) {
                Class<?>[] parameterTypes = constructor .getParameterTypes();
                if ( parameterTypes.length == 2 && parameterTypes[0] .equals( Selection.class ) && parameterTypes[1] .equals( RealizedModel.class ) ) {
                    goodConstructor = constructor;
                } else if ( parameterTypes.length == 1 && parameterTypes[0] .equals( EditorModel.class ) ) {
                    editorConstructor = constructor;
                }
            }
            if ( editorConstructor != null ) {
                return (UndoableEdit) editorConstructor .newInstance( new Object[] { this } );
            } else if ( goodConstructor != null ) {
                return (UndoableEdit) goodConstructor .newInstance( new Object[] { this.mSelection, this.mRealized } );
            } else {
                return null;
            }
        } catch ( ClassNotFoundException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e ) {
            return null;
            // TODO should be logging this, as "finer" at least
        }
    }

    private final RealizedModel mRealized;

    protected Selection mSelection;

    private SelectionSummary selectionSummary;

    private Point mCenterPoint;

    private Segment mSymmetryAxis;

    private final FieldApplication kind;

    private SymmetrySystem symmetrySystem;

    private final Map<String, SymmetrySystem> symmetrySystems;

    public Construction getSelectedConstruction( Class<? extends Construction > kind )
    {
        Class<? extends Manifestation> manifestationClass;
        if ( kind == Point .class )
            manifestationClass = Connector.class;
        else if ( kind == Segment .class )
            manifestationClass = Strut .class;
        else
            return null;
        Manifestation focus = mSelection .getSingleSelection( manifestationClass );
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

    private final Set<Manifestation> failedConstructions = new HashSet<>();

    public void addFailedConstruction( Construction cons )
    {
        failedConstructions .add( mRealized .manifest( cons ) );
    }

    public boolean hasFailedConstruction( Construction cons )
    {
        return failedConstructions .contains( mRealized .manifest( cons ) );
    }

    public Selection getSelection()
    {
        return this .mSelection;
    }

    public void notifyListeners()
    {
        this .selectionSummary .notifyListeners();
    }

    public SymmetrySystem getSymmetrySystem()
    {
        return this .symmetrySystem;
    }

    public void setSymmetrySystem( SymmetrySystem system )
    {
        this .symmetrySystem = system;
    }

    public SymmetrySystem getSymmetrySystem( String name )
    {
        return this .symmetrySystems .get( name );
    }

    public FieldApplication getKind()
    {
        return kind;
    }
}
