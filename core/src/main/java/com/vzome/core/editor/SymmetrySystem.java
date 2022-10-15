
package com.vzome.core.editor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Logger;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.vzome.api.Tool;
import com.vzome.core.algebra.AlgebraicField;
import com.vzome.core.algebra.AlgebraicNumber;
import com.vzome.core.algebra.AlgebraicVector;
import com.vzome.core.commands.AbstractCommand;
import com.vzome.core.commands.Command;
import com.vzome.core.construction.Color;
import com.vzome.core.editor.api.Context;
import com.vzome.core.editor.api.EditorModel;
import com.vzome.core.editor.api.OrbitSource;
import com.vzome.core.editor.api.Shapes;
import com.vzome.core.math.Polyhedron;
import com.vzome.core.math.RealVector;
import com.vzome.core.math.symmetry.Axis;
import com.vzome.core.math.symmetry.Direction;
import com.vzome.core.math.symmetry.OrbitSet;
import com.vzome.core.math.symmetry.Symmetry;
import com.vzome.core.render.Colors;
import com.vzome.xml.DomUtils;

public class SymmetrySystem implements OrbitSource
{
    private static final Logger logger = Logger .getLogger( "com.vzome.core.editor" );
    private static int NEXT_NEW_AXIS = 0;

    private final Symmetry symmetry;
    private final OrbitSet orbits;
    private final Map<String, Color> orbitColors = new HashMap<>();
    private Shapes shapes;
    private Map<String,Axis> vectorToAxis = new HashMap<>();
    private boolean noKnownDirections = false;

    private final SymmetryPerspective symmetryPerspective;
    private final Map<Tool.Kind,List<Tool.Factory>> toolFactoryLists = new HashMap<>();
    private final Map<Tool.Kind,List<Tool>> toolLists = new HashMap<>();
    private final Context context;
    private EditorModel editor;

    public SymmetrySystem( Element symmXml, SymmetryPerspective symmetryPerspective,
            Context context, Colors colors, boolean allowNonstandard )
    {
        this .symmetryPerspective = symmetryPerspective;
        this .context = context;
        this .symmetry = symmetryPerspective .getSymmetry();
        String styleName = symmetryPerspective .getDefaultGeometry() .getName();
        orbits = new OrbitSet( symmetry );
        if ( symmXml == null ) 
        {
            for ( Direction orbit : symmetry .getOrbitSet() .getDirections() ) {
                if ( symmetryPerspective .orbitIsStandard( orbit ) || allowNonstandard )  // reader
                    orbits .add( orbit );
                Color color = colors .getColor( Colors.DIRECTION + orbit .getName() );
                orbitColors .put( orbit.getName(), color );
            }
        }
        else
        {
            styleName = symmXml .getAttribute( "renderingStyle" );
            NodeList nodes = symmXml .getChildNodes();
            for ( int i = 0; i < nodes .getLength(); i++ ) {
                Node node = nodes .item( i );
                if ( node instanceof Element ) {
                    Element dirElem = (Element) node;
                    String name = dirElem .getAttribute( "name" );
                    Direction orbit = null;
                    String nums = dirElem .getAttribute( "prototype" );
                    if ( nums != null && ! nums .isEmpty() )
                    {
                        try {
                            AlgebraicVector prototype = symmetry .getField() .parseVector( nums );
                            orbit = symmetry .createNewZoneOrbit( name, 0, Symmetry.NO_ROTATION, prototype );
                        } catch ( NumberFormatException e )
                        {
                            System.err.println( "Integer overflow happened while creating orbit: " + name );
                            continue;
                        }
                        orbit .setAutomatic( true );
                        try {
                            int autoNum = Integer .parseInt( name );
                            if ( autoNum >= NEXT_NEW_AXIS )
                                NEXT_NEW_AXIS = ++autoNum;  // make sure new auto directions don't collide with this
                            else if ( autoNum < NEXT_NEW_AXIS )
                                name = "" + NEXT_NEW_AXIS++;
                        } catch ( NumberFormatException e ) {
                            // never mind, these used to be named things like "unnamed_13"
                            System.err.println( e .getMessage() );
                        }
                    }
                    else
                    {
                        orbit = symmetry .getDirection( name );
                        if ( orbit == null )
                            continue;
                    }
                    orbits .add( orbit );

                    String str = dirElem .getAttribute( "color" );
                    if ( str != null && ! str .isEmpty() ) {
                        Color color = Color .parseColor( str );
                        orbitColors .put( orbit .getName(), color );
                    }
                }
            }
            // fill in the orbits that might be newer than what the file had
            for (Direction orbit : symmetry .getOrbitSet() .getDirections()) {
                if ( orbits .contains( orbit ) )
                    continue;
                if ( orbit .isStandard() || allowNonstandard )  // reader
                    orbits .add( orbit );
                Color color = colors .getColor( Colors.DIRECTION + orbit .getName() );
                orbitColors .put( orbit .getName(), color );
            }
        }
        this .setStyle( styleName );
    }

    public void setEditorModel( EditorModel editor )
    {
        this.editor = editor;
    }

    public void createToolFactories( ToolsModel tools )
    {
        // Here we go from support for viewing, to support for editing

        for ( Tool.Kind kind : Tool.Kind.values() )
        {
            List<Tool.Factory> list = this .symmetryPerspective .createToolFactories( kind, tools );
            // toolFactoryLists manifest to the Controller automatically
            this .toolFactoryLists .put( kind, list );
            
            // TODO: fix this horrible hack!
            //  All SymmetrySystems feed their predef tools into the same ToolsModel,
            //  with name collisions!  That only works if the transformations of, say,
            //  tetrahedral symmetry are the same for all systems.  That was mostly
            //  true, except for a defect for the "synestructics" system that bit me
            //  in the transcribed Javascript, where the collision resolved differently!
            //  See SymmetryTool.checkSelection().
            //
            List<Tool> toolList = this .symmetryPerspective .predefineTools( kind, tools );
            this .toolLists .put( kind, toolList );
        }
    }

    @JsonIgnore
    public String getName()
    {
        return this .symmetry .getName();
    }

    @Override
    public Axis getAxis( AlgebraicVector vector )
    {
        if ( vector .isOrigin() ) {
            return null;
        }
        Axis line = this .vectorToAxis .get( vector.toString() );
        if ( line != null )
            return line;
        if ( ! this .noKnownDirections )
        {
            line = this .symmetry .getAxis( vector, this .orbits );
            if ( line != null ) {
                this .vectorToAxis .put( vector.toString(), line );
                return line;
            }
        }
        Direction dir = this .createAnonymousOrbit( vector );
        line = dir .getAxis( vector );
        this .vectorToAxis .put( vector.toString(), line );
        return line;
    }

    public Direction createAnonymousOrbit( AlgebraicVector vector )
    {
        Symmetry symm = orbits .getSymmetry();
        AlgebraicField field = symm .getField();
        AlgebraicNumber longer = field .createPower( 1 );
        AlgebraicNumber shorter = field .createPower( -1 );

        // first, find a good "scale 0" length
        RealVector rv =  vector .toRealVector();
        AlgebraicVector longVector = vector, shortVector = vector;
        double longLen = 2d, shortLen = 2d, len = rv .length();
        if ( len > 2d )
        {
            longLen = len;
            longVector = vector;
            while ( longLen > 2d )
            {
                shortVector = longVector .scale( shorter );
                shortLen =  shortVector  .toRealVector() .length();
                if ( shortLen <= 2d )
                    break;
                longLen = shortLen;
                longVector = shortVector;
            }
        }
        else
        {
            shortLen = len;
            shortVector = vector;
            while ( shortLen <= 2d )
            {
                longVector = shortVector .scale( longer );
                longLen = longVector .toRealVector() .length();
                if ( longLen > 2d )
                    break;
                shortLen = longLen;
                shortVector = longVector;
            }
        }
        if ( (2d / shortLen) > longLen )
            vector = longVector;
        else
            vector = shortVector;

        String colorName = "" + NEXT_NEW_AXIS++;  // we want it easy to keep these unique when loading files (see above)
        Direction orbit = symm .createNewZoneOrbit( colorName, 0, Symmetry.NO_ROTATION, vector );
        orbit .setAutomatic( true );
        orbits .add( orbit );
        this .orbitColors .put( orbit .getName(), Color.WHITE );
        return orbit;
    }

    public Color getVectorColor( AlgebraicVector vector ) {
        if( vector.isOrigin() ) {
            return Color.WHITE;
        }
        // try to get from cache
        Axis line = this .vectorToAxis .get( vector.toString() );
        if(line == null) {
            // calculate
            line = this .symmetry .getAxis( vector, this .orbits );
        }
        // don't create a new unnecessary Automatic direction just to determine the color
        // as in the case where this is called for color mapping a ball
        return (line == null)
                ? Color.WHITE
                        : getColor(line.getDirection());
    }

    @Override
    public Color getColor( Direction orbit )
    {
        if ( orbit == null )
            return Color.WHITE;
        Color shapeColor = this .shapes .getColor( orbit ); // usually null, but see ExportedVEFShapes
        if ( shapeColor == null ) // the usual case
            shapeColor = orbitColors .get( orbit .getName() );
        if ( shapeColor == null )
            return Color.WHITE;
        return shapeColor;
    }

    @Override
    public Symmetry getSymmetry()
    {
        return this .symmetry;
    }

    @Override
    public OrbitSet getOrbits()
    {
        return this .orbits;
    }

    public void disableKnownDirection()
    {
        this .noKnownDirections = true;
    }

    @JsonIgnore
    public Shapes getRenderingStyle()
    {
        return this .shapes;
    }

    public Element getXml( Document doc )
    {
        Element result = doc .createElement( "SymmetrySystem" );
        DomUtils .addAttribute( result, "name", this .getSymmetry() .getName() );
        DomUtils .addAttribute( result, "renderingStyle", this .shapes .getName() );

        for (Direction dir : this .orbits .getDirections()) {
            Element dirElem = doc .createElement( "Direction" );
            if ( dir .isAutomatic() )
                DomUtils .addAttribute( dirElem, "prototype", dir .getPrototype() .getVectorExpression( AlgebraicField .ZOMIC_FORMAT ) );
            DomUtils .addAttribute( dirElem, "name", dir .getName() );
            {
                Color color = getColor( dir );
                if ( color != null )
                    DomUtils .addAttribute( dirElem, "color", color .toString() );
            }
            result .appendChild( dirElem );
        }
        return result;
    }
    
    public Shapes getStyle( String styleName )
    {
        Optional<Shapes> found = this .symmetryPerspective .getGeometries() .stream()
                .filter( e -> styleName .equals( e .getName() )
                        || styleName .equals( e .getAlias() )
                        || styleName .equals( e .getPackage() ) )
                .findFirst();
        if ( found .isPresent() )
            return found .get();
        else
            return null;
    }

    public void setStyle( String styleName )
    {
        Shapes result = this .getStyle( styleName );
        if ( result != null )
            this .shapes = result;
        else {
            logger .warning( "UNKNOWN STYLE NAME: " + styleName );
            this .shapes = this .symmetryPerspective .getDefaultGeometry();
        }
    }

    public String[] getStyleNames()
    {
        return this .symmetryPerspective .getGeometries() .stream() .map( e -> e .getName() ) .toArray( String[]::new );
    }

    @JsonIgnore
    public Shapes getStyle()
    {
        return this .shapes;
    }

    @Override
    @JsonIgnore
    public Shapes getShapes()
    {
        return this .shapes;
    }

    public Polyhedron getShape( AlgebraicVector offset )
    {
        return this .getShape( offset, this.shapes );
    }

    public Polyhedron getShape( AlgebraicVector offset, Shapes shapes )
    {
        if ( offset == null )
            return shapes .getConnectorShape();
        else {
            if ( offset .isOrigin() )
                return null;
            Axis axis = this .getAxis( offset );
            if ( axis == null )
                return null; // this should only happen when using the bare Symmetry-based OrbitSource
            Direction orbit = axis .getDirection();

            // TODO remove this length computation... see the comment on AbstractShapes.getStrutShape()

            AlgebraicNumber len = axis .getLength( offset );

            return shapes .getStrutShape( orbit, len );
        }
    }

    public List<Tool.Factory> getToolFactories( Tool.Kind kind )
    {
        return this .toolFactoryLists .get( kind );
    }

    public List<Tool> getPredefinedTools( Tool.Kind kind )
    {
        return this .toolLists .get( kind );
    }

    public boolean doAction( String action )
    {
        Command command = this .symmetryPerspective .getLegacyCommand( action );
        if ( command != null )
        {
            CommandEdit edit = new CommandEdit( (AbstractCommand) command, this .editor );
            this .context .performAndRecord( edit );
            return true;
        }
        return false;
    }

    @JsonIgnore
    public String getModelResourcePath()
    {
        return this .symmetryPerspective .getModelResourcePath();
    }

    public boolean orbitIsStandard( Direction orbit )
    {
        return this .symmetryPerspective .orbitIsStandard( orbit );
    }

    public boolean orbitIsBuildDefault( Direction orbit )
    {
        return this .symmetryPerspective .orbitIsBuildDefault( orbit );
    }

    public AlgebraicNumber getOrbitUnitLength( Direction orbit )
    {
        return this .symmetryPerspective .getOrbitUnitLength( orbit );
    }
}
