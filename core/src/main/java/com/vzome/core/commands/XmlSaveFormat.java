
//(c) Copyright 2005, Scott Vorthmann.  All rights reserved.

package com.vzome.core.commands;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.vzome.core.algebra.AlgebraicField;
import com.vzome.core.algebra.AlgebraicNumber;
import com.vzome.core.algebra.AlgebraicVector;
import com.vzome.core.construction.Construction;
import com.vzome.core.construction.FreePoint;
import com.vzome.core.construction.Point;
import com.vzome.core.construction.Polygon;
import com.vzome.core.construction.PolygonFromVertices;
import com.vzome.core.construction.Segment;
import com.vzome.core.construction.SegmentJoiningPoints;
import com.vzome.xml.DomUtils;

public class XmlSaveFormat
{
    protected final boolean mProject4d, mSelectionNotSaved, mRationalVectors, mGroupingInSelection;
        
    private transient AlgebraicField mField;
    
    private transient int mScale;
    
    private transient AlgebraicNumber mMultiplier;
    
    private transient String writerVersion; // see initialize()
    
    private final String version;
    
    private final Set<String> capabilities = new HashSet<>();

    private Properties properties;
        
    public static final String CURRENT_FORMAT = "http://xml.vzome.com/vZome/4.0.0/";
    
    protected static final String PROJECT_4D = "project-4D-to-3D",
                                SELECTION_NOT_SAVED = "selection-not-saved",
                                FORMAT_2_1_0 = "interim-210-format",
                                GROUPING_IN_SELECTION = "grouping-in-selection",
                                RATIONAL_VECTORS = "rational-vectors",
//                                ACTION_HISTORY = "action-history",  // not in use yet
                                COMPACTED_COMMAND_EDITS = "compacted-command-edits",
                                MULTIPLE_DESIGNS = "multiple-designs";
    
    protected static final Map<String, XmlSaveFormat> FORMATS = new HashMap<>();
    
    private static final Logger logger = Logger .getLogger( "com.vzome.core.commands.XmlSaveFormat" );

    public static final String UNKNOWN_COMMAND = "unknown.command";
        
    /**
     * Initialize.
     * 
     * If you're tempted to add another parameter, see if you can make it a property instead.
     * 
     * Shouldn't we just replace all the parameters with one Controller object?
     * 
     * @param root
     * @param field
     * @param symms
     * @param scale
     * @param writerVersion
     * @param props
     */
    public void initialize( AlgebraicField field, int scale,
        String writerVersion, Properties props )
    {
        this.properties = props;
        this.writerVersion = writerVersion;
        if ( ( writerVersion == null ) || writerVersion .isEmpty() )
        	this.writerVersion = "before 2.1 Beta 7";
        mField = field;
        mScale = scale;
        if ( scale == 0 )
            mMultiplier = null;
        else
            mMultiplier = field .createPower( scale );
    }
    
    protected XmlSaveFormat( String version, String[] capabilities )
    {
        super();
        
        this .version = version;
        
        this .capabilities .addAll( Arrays .asList( capabilities ) );
        
        mProject4d = this.capabilities .contains( PROJECT_4D );
        mSelectionNotSaved = this.capabilities .contains( SELECTION_NOT_SAVED );
        mRationalVectors = this.capabilities .contains( RATIONAL_VECTORS );
        mGroupingInSelection = this.capabilities .contains( GROUPING_IN_SELECTION );
        
        FORMATS .put( version, this );
    }
    
    protected String getVersion()
    {
        return this .version;  // never really called, just here to make the version warning go away
        // ( this.version is valuable when debugging )
    }
            
    public boolean isMigration()
    {
        return ! multipleDesigns();
    }

    public boolean selectionsNotSaved()
    {
        return mSelectionNotSaved;
    }

    public boolean rationalVectors()
    {
        return mRationalVectors;
    }

    public boolean actionHistory()
    {
        return false;  // never true, yet
    }

    public boolean commandEditsCompacted()
    {
        return capabilities .contains( COMPACTED_COMMAND_EDITS );
    }

    public boolean multipleDesigns()
    {
        return capabilities .contains( MULTIPLE_DESIGNS );
    }

    public boolean groupingDoneInSelection()
    {
        // true for any version up to and including 2.1.2, or for 3.0 alpha versions
        return mGroupingInSelection && ! "2.1.3" .equals( writerVersion );
    }

    public boolean groupingRecursive()
    {
        // true for any version including and after 2.1.2, or for 3.0 beta versions
        return  ! groupingDoneInSelection() || "2.1.2".equals( writerVersion );
    }

    public boolean interim210format()
    {
        return capabilities .contains( FORMAT_2_1_0 );
    }

    
    
    // ======================= LOADING
    
    
    
    protected AlgebraicVector parseAlgebraicVector( Element elem )
    {        
        String val = elem .getAttribute( "x" );
        AlgebraicNumber x = (val==null || val .isEmpty() )? mField .zero() : mField .parseLegacyNumber( val );
        val = elem .getAttribute( "y" );
        AlgebraicNumber y = (val==null || val .isEmpty() )? mField .zero() : mField .parseLegacyNumber( val );
        val = elem .getAttribute( "z" );
        AlgebraicNumber z = (val==null || val .isEmpty() )? mField .zero() : mField .parseLegacyNumber( val );
        val = elem .getAttribute( "w" );
        boolean threeD = val==null || val .isEmpty();
        AlgebraicNumber w = null;
        if ( !threeD )
        	w =  mField .parseLegacyNumber( val );

        AlgebraicVector value = threeD?  new AlgebraicVector( x, y, z ) : new AlgebraicVector( w, x, y, z );
        
        if ( mProject4d && !threeD ) {        
            if ( ! w .isZero() && logger .isLoggable( Level.WARNING ) )
                logger .warning( "stripping non-zero W component from " + value .toString() );

            value = mField .projectTo3d( value, true );
        }
        if ( mMultiplier != null )
            value = value .scale( mMultiplier );
        return value;
    }
    
    public static final Object NOT_AN_ATTRIBUTE = new Object();

    public Object parseAlgebraicObject( String valName, Element val )
    {
        Object value = NOT_AN_ATTRIBUTE;
        if ( valName .equals( "Null" ) )
            value = null;
        else if ( valName .equals( "RationalVector" ) ) {
            String nums = val .getAttribute( "nums" );
            if ( nums == null || nums .isEmpty() )
                return mField .origin( 3 );
            String denoms = val .getAttribute( "denoms" );
            StringTokenizer tokens = new StringTokenizer( nums );
            
            // TODO: This should be generalized for any order field. It's currently hard coded for a 3d vector in an order 2 field.
            int[] result = new int[]{ 0,1, 0,1,   0,1, 0,1,   0,1, 0,1 };
            for ( int i = 0; i < mField .getOrder(); i++ )
            {
                String token = tokens .nextToken();
                if ( token == null )
                    throw new IllegalStateException( "RationalVector nums too short for field " + mField .getName() );
                result[ i * 2 ] = Integer .parseInt( token );
            }
            if ( denoms != null && ! denoms .isEmpty() )
            {
                tokens = new StringTokenizer( denoms );
                for ( int i = 0; i < mField .getOrder(); i++ )
                {
                    String token = tokens .nextToken();
                    if ( token == null )
                        throw new IllegalStateException( "RationalVector denoms too short for field " + mField .getName() );
                    result[ i * 2 + 1 ] = Integer .parseInt( token );
                }
            }
            // split the result array into 3 equal length arrays, one for each dimension
            final int oneThirdLen = result.length/3;
            final int twoThirdLen = oneThirdLen * 2;
            int[][] result3d = new int[3][oneThirdLen];
            for(int i=0; i < oneThirdLen; i++) {
                result3d[0][i] = result[i];
                result3d[1][i] = result[i+oneThirdLen];
                result3d[2][i] = result[i+twoThirdLen];
            }
            // parseAlgebraicObject is currently limited to parsing Integer valued vectors, not Longs, and definitely not BigIntegers
            value = mField .createVector( result3d );
        }
        else if ( valName .equals( "GoldenVector" ) )
            value = parseAlgebraicVector( val );
        else if ( valName .equals( "Boolean" ) ) {
            String gnum = val .getAttribute( "value" );
            value = Boolean .parseBoolean( gnum );
        }
        else if ( valName .equals( "Integer" ) ) {
            String gnum = val .getAttribute( "value" );
            value = Integer .parseInt( gnum );
        }
        else if ( valName .equals( "GoldenNumber" )
                || valName .equals( "IntegralNumber" ) ) {
            String gnum = val .getAttribute( "value" );
            value = mField .parseLegacyNumber( gnum );
            if ( mMultiplier != null )
                value = ((AlgebraicNumber) value) .times( mMultiplier );
        }
        else if ( valName .equals( "String" ) )
            value = val .getTextContent();
        return value;
    }

    /**
     * this is for the old format (before rationalVectors)
     */
    public Construction parseConstruction( String apName, Element attrOrParam )
    {
    	return this .parseConstruction( apName, attrOrParam, false );
    }

    /**
     * this is for the old format (before rationalVectors)
     */
    public Construction parseConstruction( String apName, Element attrOrParam, boolean projectTo3d )
    {
        Construction c = null;
        if ( apName .equals( "point" ) ) {
            AlgebraicVector loc = parseAlgebraicVector( attrOrParam );
            if ( projectTo3d )
            	loc = loc .projectTo3d( true );
            c = new FreePoint( loc );
        } else if ( apName .equals( "segment" ) ) {
            Element start = DomUtils .getFirstChildElement( attrOrParam, "start" );
            Element end = DomUtils .getFirstChildElement( attrOrParam, "end" );
            AlgebraicVector sloc = parseAlgebraicVector( start );
            AlgebraicVector eloc = parseAlgebraicVector( end );
            if ( projectTo3d ) {
            	sloc = sloc .projectTo3d( true );
            	eloc = eloc .projectTo3d( true );
            }
            c = new SegmentJoiningPoints( new FreePoint( sloc ), new FreePoint( eloc ) );
        } else if ( apName .equals( "polygon" ) ) {
        	NodeList kids = attrOrParam .getElementsByTagName( "vertex" );
            Point[] pts = new Point[ kids .getLength() ];
            for ( int k = 0; k < kids .getLength(); k++ ) {
                AlgebraicVector loc = parseAlgebraicVector( (Element) kids .item( k ) );
                if ( projectTo3d )
                	loc = loc .projectTo3d( true );
                pts[ k ] = new FreePoint( loc );
            }
            c = new PolygonFromVertices( pts );
        }
        return c;
    }
    
//    public AttributeMap loadCommandAttributesOld( Element editElem )
//    {
//        AttributeMap attrs = new AttributeMap();
//        for ( int j = 0; j < editElem .getChildCount(); j++ ) {
//            Node kid2 = editElem .getChild( j );
//            if ( kid2 instanceof Element ) {
//                Element attrOrParam = (Element) kid2;
//                String apName = attrOrParam .getLocalName();
//                if ( apName .equals( "attr" ) ) {
//                    String attrName = attrOrParam .getAttributeValue( "name" );
//                    Object value = null;
//                    Elements kids = attrOrParam .getChildElements();
//                    if ( kids .size() != 0 ) {
//                        // attr value is not empty
//                        Element val = kids .get( 0 );
//                        String valName = val .getLocalName();
//                        value = parseAlgebraicObject( valName, val );
//                        if ( value == null )
//                            value = parseConstruction( valName, val );
//                    }
//                    attrs .put( attrName, value );
//                }
//            }
//        }
//        return attrs;
//    }

    public AttributeMap loadCommandAttributes( Element editElem )
    {
    	return this .loadCommandAttributes( editElem, false );
    }
    
    public AttributeMap loadCommandAttributes( Element editElem, boolean projectTo3d )
    {
        AttributeMap attrs = new AttributeMap(); // need this to be ordered for the purpose of regression testing
        NodeList kids = editElem .getChildNodes();
        for ( int j = 0; j < kids .getLength(); j++ )
        {
        	Node node = kids .item( j );
        	if ( ! ( node instanceof Element ) )
        		continue;
        	Element attrElem = (Element) node;
        	String elemName = attrElem .getLocalName();
            String attrName = attrElem .getAttribute( "attrName" );
            if ( interim210format() )
            {
                attrName = attrElem .getAttribute( "name" );
                Element elemKid = DomUtils .getFirstChildElement( attrElem );
                if ( elemKid != null ) {
            		attrElem = elemKid;
                    elemName = attrElem .getLocalName();
                }
            }
        	Object value = parseAlgebraicObject( elemName, attrElem );
        	if ( value == NOT_AN_ATTRIBUTE )
        	    if ( rationalVectors() )
        	    {
        	        if ( elemName .equals( "point" ) )
        	            value = parsePoint( attrElem, "at", projectTo3d );
        	        else if ( elemName .equals( "segment" ) )
        	            value = parseSegment( attrElem, "start", "end", projectTo3d );
        	        else if ( elemName .equals( "polygon" ) )
        	            value = parsePolygon( attrElem, "vertex", projectTo3d );
                    else
        	            throw new IllegalStateException( "unknown parameter construction: " + elemName );
        	    }
        	    else
        	        value = parseConstruction( elemName, attrElem, projectTo3d );
        	attrs .put( attrName, value );
        }
        return attrs;
    }

    public AlgebraicField getField()
    {
        return mField;
    }
        
    public int getScale()
    {
        return mScale;
    }
    
    public static final void serializeNumber( Element xml, String attrName, AlgebraicNumber number )
    {
    	DomUtils .addAttribute( xml, attrName, number .toString( AlgebraicField .ZOMIC_FORMAT ) );
    }
    
    public static final void serializePoint( Element xml, String attrName, Point point )
    {
    	DomUtils .addAttribute( xml, attrName, point .getLocation() .getVectorExpression( AlgebraicField .ZOMIC_FORMAT ) );
    }
    
    public static final void serializeSegment( Element xml, String startAttrName, String endAttrName, Segment segment )
    {
    	DomUtils .addAttribute( xml, startAttrName, segment .getStart() .getVectorExpression( AlgebraicField .ZOMIC_FORMAT ) );
    	DomUtils .addAttribute( xml, endAttrName, segment .getEnd() .getVectorExpression( AlgebraicField .ZOMIC_FORMAT ) );
    }
    
    public static final void serializePolygon( Element xml, String vertexChildName, Polygon polygon )
    {
        polygon .getXml( xml, vertexChildName );
    }

    public final AlgebraicVector parseRationalVector( Element xml, String attrName )
    {
        String nums = xml .getAttribute( attrName );
        if ( nums == null || nums .isEmpty() )
            return null;
        AlgebraicVector loc = mField .parseVector( nums );
        return loc;
    }

    public final AlgebraicNumber parseRationalNumber( Element xml, String attrName )
    {
        String nums = xml .getAttribute( attrName );
        if ( nums == null || nums .isEmpty() )
            return null;
        AlgebraicNumber loc = mField .parseNumber( nums );
        return loc;
    }

    public final Point parsePoint( Element xml, String attrName )
    {
    	return this .parsePoint( xml, attrName, false );
    }

    public final Point parsePoint( Element xml, String attrName, boolean projectTo3d )
    {
        String nums = xml .getAttribute( attrName );
        if ( nums == null || nums .isEmpty() )
            return null;
        AlgebraicVector loc = mField .parseVector( nums );
        if ( projectTo3d )
        	loc = loc .projectTo3d( true );
        return new FreePoint( loc );
    }

    public final Segment parseSegment( Element xml, String startAttrName, String endAttrName )
    {
    	return this .parseSegment( xml, startAttrName, endAttrName, false );
    }

    public final Segment parseSegment( Element xml, String startAttrName, String endAttrName, boolean projectTo3d )
    {
        String nums = xml .getAttribute( endAttrName );
        if ( nums == null || nums .isEmpty() )
            return null;
        AlgebraicVector eloc = mField .parseVector( nums );
        nums = xml .getAttribute( startAttrName );
        AlgebraicVector sloc = ( nums == null || nums .isEmpty() )?
                 mField .origin( eloc .dimension() )
                : mField .parseVector( nums );
        if ( projectTo3d ) {
        	sloc = sloc .projectTo3d( true );
        	eloc = eloc .projectTo3d( true );
        }
        return new SegmentJoiningPoints( new FreePoint( sloc ), new FreePoint( eloc ) );
    }

    public final Polygon parsePolygon( Element xml, String vertexChildName )
    {
    	return this .parsePolygon( xml, vertexChildName, false );
    }

    public final Polygon parsePolygon( Element xml, String vertexChildName, boolean projectTo3d )
    {
        NodeList kids = xml .getElementsByTagName( vertexChildName );
        Point[] pts = new Point[ kids .getLength() ];
        for ( int k = 0; k < kids .getLength(); k++ )
        {
            String nums = ((Element) kids .item( k )) .getAttribute( "at" );
            AlgebraicVector loc = mField .parseVector( nums );
            if ( projectTo3d )
            	loc = loc .projectTo3d( true );
            pts[ k ] = new FreePoint( loc );
        }
        return new PolygonFromVertices( pts );
    }

    public final Polygon parsePolygonReversed( Element xml, String vertexChildName )
    {
    	NodeList kids = xml .getElementsByTagName( vertexChildName );
        Point[] pts = new Point[ kids .getLength() ];
        int kmax = kids .getLength() - 1;
        for ( int k = 0; k < kids .getLength(); k++ )
        {
            String nums = ((Element) kids .item( k )) .getAttribute( "at" );
            AlgebraicVector loc = mField .parseVector( nums );
            pts[ kmax - k ] = new FreePoint( loc );
        }
        return new PolygonFromVertices( pts );
    }

    public AlgebraicNumber parseNumber( Element xml, String attrName )
    {
        String nums = xml .getAttribute( attrName );
        if ( nums == null || nums .isEmpty() )
            return null;
        return mField .parseNumber( nums );
    }

    
    public static String escapeNewlines( String input )
    {
        StringBuffer buf = new StringBuffer();
        
        BufferedReader br = new BufferedReader( new StringReader( input ) );
        String line = null;
        try {
            while ( (line = br .readLine() ) != null ) {
                int comment = line .indexOf( "//" );
                if ( comment >= 0 ) {
                    line = line .substring( 0, comment );
                }
                buf .append( line + "\n" );
            }
        } catch ( IOException e ) {}
            
//        System .out .println( buf );
        return buf .toString();
    }

    public boolean loadToRender()
    {
        return ! "true".equals( properties .getProperty( "no.rendering" ) );
    }
    
	public String getToolVersion( Element element )
	{
		String fileEdition = element .getAttribute( "edition" );
        if ( fileEdition == null || fileEdition .isEmpty() )
        	fileEdition = "vZome";
        return fileEdition + " " + this.writerVersion;
	}
}
