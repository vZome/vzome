
//(c) Copyright 2011, Scott Vorthmann.

package com.vzome.core.editor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.vzome.core.algebra.AlgebraicField;
import com.vzome.core.algebra.AlgebraicNumber;
import com.vzome.core.algebra.AlgebraicVector;
import com.vzome.core.math.DomUtils;
import com.vzome.core.math.Polyhedron;
import com.vzome.core.math.RealVector;
import com.vzome.core.math.symmetry.Axis;
import com.vzome.core.math.symmetry.Direction;
import com.vzome.core.math.symmetry.OrbitSet;
import com.vzome.core.math.symmetry.Symmetry;
import com.vzome.core.render.Color;
import com.vzome.core.render.Colors;
import com.vzome.core.render.RenderedModel.OrbitSource;
import com.vzome.core.render.Shapes;

public class SymmetrySystem implements OrbitSource
{
    private static Logger logger = Logger .getLogger( "com.vzome.core.editor" );
    private static int NEXT_NEW_AXIS = 0;
    
	private final Symmetry symmetry;
    private final OrbitSet orbits;
    private final Map<Direction, Color> orbitColors = new HashMap<>();
    private final Map<String,Shapes> styles = new HashMap<>();
    private final List<String> styleNames = new ArrayList<>();
    private Shapes shapes;
    private Map<AlgebraicVector,Axis> vectorToAxis = new HashMap<>();

    private boolean noKnownDirections = false;

	public SymmetrySystem( Element symmXml, Symmetry symmetry, Colors colors, List<Shapes> styles, boolean allowNonstandard )
	{
		this .symmetry = symmetry;
        for (Shapes shape : styles) {
            String name = shape .getName();
            String alias = shape .getAlias();
            this .styleNames .add( name );
            this .styles .put( name, shape );
            this .styles .put( shape .getPackage(), shape );
            if ( alias != null && ! alias .equals( name ) )
                this .styles .put( alias, shape );
        }
        String styleName = symmetry .getDefaultStyle();
		orbits = new OrbitSet( symmetry );
		if ( symmXml == null ) 
		{
            for (Direction dir : symmetry .getOrbitSet()) {
                if ( dir .isStandard() || allowNonstandard )  // reader
                    orbits .add( dir );
                Color color = colors .getColor( Colors.DIRECTION + dir .getName() );
                orbitColors .put( dir, color );
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
					Direction dir = null;
					String nums = dirElem .getAttribute( "prototype" );
					if ( nums != null && ! nums .isEmpty() )
					{
						AlgebraicVector prototype = symmetry .getField() .parseVector( nums );
						try {
						    dir = symmetry .createNewZoneOrbit( name, 0, Symmetry.NO_ROTATION, prototype );
						} catch ( IllegalStateException e )
						{
                            System.err.println( "Integer overflow happened while creating orbit: " + name );
                            continue;
						}
						dir .setAutomatic( true );
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
						dir = symmetry .getDirection( name );
						if ( dir == null )
							continue;
					}
                    orbits .add( dir );

					String str = dirElem .getAttribute( "color" );
					if ( str != null && ! str .isEmpty() ) {
						Color color = Color .parseColor( str );
						orbitColors .put( dir, color );
						//                        colors .addColor( Colors.DIRECTION + name, color );
					}
				}
			}
            // fill in the orbits that might be newer than what the file had
            for (Direction dir : symmetry .getOrbitSet()) {
                if ( orbits .contains( dir ) )
                    continue;
                if ( dir .isStandard() || allowNonstandard )  // reader
                    orbits .add( dir );
                Color color = colors .getColor( Colors.DIRECTION + dir .getName() );
                orbitColors .put( dir, color );
            }
		}
        this .shapes = this .styles .get( styleName );
        
        if ( this .shapes == null ) {
        	logger .warning( "UNKNOWN STYLE NAME: " + styleName );
            this .shapes = this .styles .get( symmetry .getDefaultStyle() );
        }
	}
	
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
        Axis line = this .vectorToAxis .get( vector );
        if ( line != null )
            return line;
        if ( ! this .noKnownDirections )
        {
            line = this .symmetry .getAxis( vector, this .orbits );
            if ( line != null ) {
                this .vectorToAxis .put( vector, line );
                return line;
            }
        }
        Direction dir = this .createAnonymousOrbit( vector );
        line = dir .getAxis( vector );
        this .vectorToAxis .put( vector, line );
        return line;
	}
	
	public Direction createAnonymousOrbit( AlgebraicVector vector )
	{
        Symmetry symmetry = orbits .getSymmetry();
        AlgebraicField field = symmetry .getField();
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
        Direction dir = symmetry .createNewZoneOrbit( colorName, 0, Symmetry.NO_ROTATION, vector );
        dir .setAutomatic( true );
        orbits .add( dir );
        this .orbitColors .put( dir, Color.WHITE );
        return dir;
    }

	@Override
	public Color getColor( Direction orbit )
	{
		Color shapeColor = this .shapes .getColor( orbit ); // usually null, but see ExportedVEFShapes
		if ( shapeColor == null ) // the usual case
			shapeColor = orbitColors .get( orbit );
		return shapeColor;
	}

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

    public Shapes getRenderingStyle()
    {
        return this .shapes;
    }

    public Element getXml( Document doc )
    {
        Element result = doc .createElement( "SymmetrySystem" );
        DomUtils .addAttribute( result, "name", this .getSymmetry() .getName() );
        DomUtils .addAttribute( result, "renderingStyle", this .shapes .getName() );

        for (Direction dir : this .orbits) {
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

    public void setStyle( String styleName )
    {
        this .shapes = this .styles .get( styleName );
    }

    public String[] getStyleNames()
    {
        return this .styleNames .toArray( new String[]{} );
    }

    public Shapes getStyle()
    {
        return this .shapes;
    }

	@Override
	public Shapes getShapes()
	{
		return this .shapes;
	}

	public Polyhedron getShape( AlgebraicVector offset )
	{
		if ( offset == null )
			return this .shapes .getConnectorShape();
		else {
			if ( offset .isOrigin() )
			    return null;
			Axis axis = this .getAxis( offset );
			if ( axis == null )
				return null; // this should only happen when using the bare Symmetry-based OrbitSource
			Direction orbit = axis .getDirection();
			
			// TODO remove this length computation... see the comment on AbstractShapes.getStrutShape()
			
			AlgebraicNumber len = axis .getLength( offset );
			
			return this .shapes .getStrutShape( orbit, len );
		}
	}
}
