package com.vzome.core.editor;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.vzome.core.construction.Construction;
import com.vzome.core.construction.Point;
import com.vzome.core.construction.Segment;
import com.vzome.core.model.Connector;
import com.vzome.core.model.Manifestation;
import com.vzome.core.model.RealizedModel;
import com.vzome.core.model.RealizedModelImpl;
import com.vzome.core.model.Strut;

public class EditorModel
{
    public EditorModel( RealizedModelImpl realized, Point originPoint, FieldApplication kind, SymmetrySystem symmetrySystem, Map<String, SymmetrySystem> symmetrySystems )
    {
        mRealized = realized;
        mSelection = new SelectionImpl();
        this.kind = kind;
        this.symmetrySystem = symmetrySystem;
        this.symmetrySystems = symmetrySystems;
        for ( SymmetrySystem symmetrySys : symmetrySystems .values()) {
            symmetrySys .setEditorModel( this );
        }

        this .selectionSummary = new SelectionSummary( this .mSelection );
        this .mSelection .addListener( this .selectionSummary );

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
        try {
            String[] packages = new String[] {
                    "com.vzome.core.edits",
                    this.getClass() .getPackage() .getName()
            };
            Class<?> factoryClass = null;
            for ( String pkgName : packages ) {
                String className = pkgName + "." + name;
                try {
                    factoryClass = Class.forName( className );
                } catch ( ClassNotFoundException e ) {
                    continue;
                }
                break;
            }
            if ( factoryClass == null )
                return null;

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
        } catch ( InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e ) {
            return null;
            // TODO should be logging this, as "finer" at least
        }
    }

    private final RealizedModelImpl mRealized;

    protected SelectionImpl mSelection;

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
            return focus .getFirstConstruction();
        return null;
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
