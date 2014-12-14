
//(c) Copyright 2011, Scott Vorthmann.

package com.vzome.core.editor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.vzome.core.algebra.AlgebraicField;
import com.vzome.core.algebra.RationalNumbers;
import com.vzome.core.math.DomUtils;
import com.vzome.core.math.RealVector;
import com.vzome.core.math.symmetry.Axis;
import com.vzome.core.math.symmetry.Direction;
import com.vzome.core.math.symmetry.OrbitSet;
import com.vzome.core.math.symmetry.Symmetry;
import com.vzome.core.render.Color;
import com.vzome.core.render.Colors;
import com.vzome.core.render.Shapes;
import com.vzome.core.render.RenderedModel.OrbitSource;

public class SymmetrySystem implements OrbitSource
{
    private static int NEXT_NEW_AXIS = 0;
    
	private final Symmetry symmetry;
    private final OrbitSet orbits;
    private final Map orbitColors = new HashMap();
    private final Map<String,Shapes> styles = new HashMap();
    private final List<String> styleNames = new ArrayList<String>();
    private Shapes shapes;

    private boolean noKnownDirections = false;

	public SymmetrySystem( Element symmXml, Symmetry symmetry, Colors colors, List<Shapes> styles, boolean allowNonstandard )
	{
		this .symmetry = symmetry;
		for ( Iterator iterator = styles.iterator(); iterator.hasNext(); ) {
            Shapes shapes = (Shapes) iterator.next();
            String name = shapes .getName();
            String alias = shapes .getAlias();
            this .styleNames .add( name );
            this .styles .put( name, shapes );
            this .styles .put( shapes .getPackage(), shapes );
            if ( alias != null && ! alias .equals( name ) )
                this .styles .put( alias, shapes );
        }
        String styleName = symmetry .getDefaultStyle();
		orbits = new OrbitSet( symmetry );
		if ( symmXml == null ) 
		{
	        for ( Iterator dirs = symmetry .getOrbitSet() .iterator(); dirs .hasNext(); )
	        {
	            Direction dir = (Direction) dirs .next();
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
						int[] prototype = RationalNumbers .parseRationalVector( nums );
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
		}
        this .shapes = this .styles .get( styleName );
	}

	@Override
	public Axis getAxis( int[] vector )
	{
        Symmetry symmetry = orbits .getSymmetry();
        AlgebraicField field = symmetry .getField();
        if ( field .isOrigin( vector ) ) {
            return null;
        }
        if ( ! this .noKnownDirections )
        {
        	for ( Iterator dirs = orbits .iterator(); dirs .hasNext(); ) {
        		Direction dir = (Direction) dirs .next();
        		Axis line = dir .getAxis( vector );
        		if ( line != null )
        			return line;
        	}
        }
        Direction dir = this .createAnonymousOrbit( vector );
        return dir .getAxis( Symmetry.PLUS, 0 );
	}
	
	public Direction createAnonymousOrbit( int[] vector )
	{
        Symmetry symmetry = orbits .getSymmetry();
        AlgebraicField field = symmetry .getField();
        int[] longer = field .createPower( 1 );
        int[] shorter = field .createPower( -1 );
                
        // first, find a good "scale 0" length
        RealVector rv = field .getRealVector( vector );
        int[] longVector = vector, shortVector = vector;
        double longLen = 2d, shortLen = 2d, len = rv .length();
        if ( len > 2d )
        {
            longLen = len;
            longVector = vector;
            while ( longLen > 2d )
            {
                shortVector = field .scaleVector( longVector, shorter );
                shortLen = field .getRealVector( shortVector ) .length();
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
                longVector = field .scaleVector( shortVector, longer );
                longLen = field .getRealVector( longVector ) .length();
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
		return (Color) orbitColors .get( orbit );
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

        for ( Iterator dirs = this .getOrbits() .iterator(); dirs .hasNext(); )
        {
            Direction dir = (Direction) dirs .next();
            Element dirElem = doc .createElement( "Direction" );
            if ( dir .isAutomatic() )
                DomUtils .addAttribute( dirElem, "prototype", RationalNumbers .toString( dir .getPrototype() ) );
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
        return (String[]) this .styleNames .toArray( new String[]{} );
    }

    public Shapes getStyle()
    {
        return this .shapes;
    }
}
