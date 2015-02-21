
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
import com.vzome.core.algebra.PentagonField;
import com.vzome.core.construction.Construction;
import com.vzome.core.construction.FreePoint;
import com.vzome.core.construction.ModelRoot;
import com.vzome.core.construction.Point;
import com.vzome.core.construction.Polygon;
import com.vzome.core.construction.PolygonFromVertices;
import com.vzome.core.construction.Segment;
import com.vzome.core.construction.SegmentJoiningPoints;
import com.vzome.core.math.DomUtils;
import com.vzome.core.math.GoldenField;
import com.vzome.core.math.RootTwoField;
import com.vzome.core.math.symmetry.Axis;
import com.vzome.core.math.symmetry.OrbitSet;
import com.vzome.core.math.symmetry.QuaternionicSymmetry;
import com.vzome.core.math.symmetry.Symmetry;

public class XmlSaveFormat
{
    protected final boolean mProject4d, mSelectionNotSaved, mRationalVectors, mGroupingInSelection;
    
    private transient ModelRoot mModelRoot;
    
    private transient AlgebraicField mField;
    
    private transient OrbitSet.Field symmetries;
    
    private transient int mScale;
    
    private transient AlgebraicNumber mMultiplier;
    
    private transient String writerVersion; // see initialize()
    
    private final String version;
    
    private final Set capabilities = new HashSet();

    private Properties properties;
        
    public static final String CURRENT_FORMAT = "http://xml.vzome.com/vZome/4.0.0/";
    
    private static final String PROJECT_4D = "project-4D-to-3D",
                                SELECTION_NOT_SAVED = "selection-not-saved",
                                FORMAT_2_1_0 = "interim-210-format",
                                GROUPING_IN_SELECTION = "grouping-in-selection",
                                RATIONAL_VECTORS = "rational-vectors",
//                                ACTION_HISTORY = "action-history",  // not in use yet
                                COMPACTED_COMMAND_EDITS = "compacted-command-edits",
                                MULTIPLE_DESIGNS = "multiple-designs";
    
    private static final Map FORMATS = new HashMap();
    
    static Logger logger = Logger .getLogger( "com.vzome.core.commands.XmlSaveFormat" );

    public static final String UNKNOWN_COMMAND = "unknown.command";
    
    static {
        new XmlSaveFormat( "http://tns.vorthmann.org/vZome/2.0/",   new String[]{ PROJECT_4D, SELECTION_NOT_SAVED } );
        new XmlSaveFormat( "http://tns.vorthmann.org/vZome/2.0.1/", new String[]{ PROJECT_4D, SELECTION_NOT_SAVED } );
        new XmlSaveFormat( "http://tns.vorthmann.org/vZome/2.0.2/", new String[]{ SELECTION_NOT_SAVED } );
        new XmlSaveFormat( "http://tns.vorthmann.org/vZome/2.0.3/", new String[]{ SELECTION_NOT_SAVED } );
        new XmlSaveFormat( "http://tns.vorthmann.org/vZome/2.1.0/", new String[]{ SELECTION_NOT_SAVED, FORMAT_2_1_0 } );
        
        // the format for vZome 2.1
        new XmlSaveFormat( "http://tns.vorthmann.org/vZome/3.0.0/", new String[]{ GROUPING_IN_SELECTION } );
        
        // the format for vZome 3.0 alpha, elem names still determined by UndoableEdit, but using AlgebraicField and rationals
        new XmlSaveFormat( "http://tns.vorthmann.org/vZome/4.0.0/", new String[]{ RATIONAL_VECTORS, GROUPING_IN_SELECTION } );
        
        // the format for vZome 3.0 beta, compacting CommandEdits
        new XmlSaveFormat( "http://tns.vorthmann.org/vZome/5.0.0/", new String[]{ RATIONAL_VECTORS, COMPACTED_COMMAND_EDITS } );
        
        // the format for vZome 4.0, including multiple designs
        new XmlSaveFormat( CURRENT_FORMAT, new String[]{ RATIONAL_VECTORS, COMPACTED_COMMAND_EDITS, MULTIPLE_DESIGNS } );
    }
    
    public static XmlSaveFormat getFormat( String namespace )
    {
        XmlSaveFormat format = (XmlSaveFormat) FORMATS .get( namespace );
        return format;
    }
        
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
    public void initialize( AlgebraicField field, OrbitSet.Field symms, int scale,
        String writerVersion, Properties props )
    {
        this.properties = props;
        this.writerVersion = writerVersion;
        mField = field;
        this .symmetries = symms;
        mModelRoot = new ModelRoot( field );
        mScale = scale;
        if ( scale == 0 )
            mMultiplier = null;
        else
            mMultiplier = field .createPower( scale );
    }
    
    private XmlSaveFormat( String version, String[] capabilities )
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
    
    
    
    protected AlgebraicVector parseAlgebraicVector( Element val, boolean threeD )
    {
        AlgebraicVector value = null;
        if ( mField instanceof PentagonField )
            value = GoldenField.INSTANCE .parseRationalVector( val );
        else
            value = RootTwoField.INSTANCE .parseRationalVector( val );
        
        if ( threeD && ! value .getComponent( AlgebraicVector .W4 ) .isZero()
                && logger .isLoggable( Level.FINE ) )
            logger .fine( "stripping non-zero W component from " + value .toString() );
//            throw new IllegalStateException( "stripping non-zero W component from " + RationalNumbers .toString( value ) );
        
        if ( mProject4d || threeD )
            value = mField .projectTo3d( value, true );
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
            
            int[] result = new int[]{ 0,1, 0,1, 0,1, 0,1, 0,1, 0,1 };
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
            value = mField .createVector( result );
        }
        else if ( valName .equals( "GoldenVector" ) )
            value = parseAlgebraicVector( val, true ); // TODO this attribute may need to be 4D for some model files
        else if ( valName .equals( "Boolean" ) ) {
            String gnum = val .getAttribute( "value" );
            value = new Boolean( Boolean .getBoolean( gnum ) );
        }
        else if ( valName .equals( "Integer" ) ) {
            String gnum = val .getAttribute( "value" );
            value = new Integer( Integer .parseInt( gnum ) );
        }
        else if ( valName .equals( "GoldenNumber" )
                || valName .equals( "IntegralNumber" ) ) {
            String gnum = val .getAttribute( "value" );
            if ( mField instanceof PentagonField )
                value = GoldenField.INSTANCE .parseString( gnum ) .getAlgebraicNumber();
            else
                value = RootTwoField.INSTANCE .parseString( gnum ) .getAlgebraicNumber();
            if ( mMultiplier != null )
                value = ((AlgebraicNumber) value) .times( mMultiplier );
        }
        else if ( valName .equals( "Symmetry" ) ) {
            String name = val .getAttribute( "name" );
            value = parseSymmetry( name );
        }
        else if ( valName .equals( "QuaternionicSymmetry" ) ) {
            String name = val .getAttribute( "name" );
            value = symmetries .getQuaternionSet( name );
        }
        else if ( valName .equals( "String" ) )
            value = val .getTextContent();
        else if ( valName .equals( "Axis" ) )
            value = parseAxis( val, "symm", "dir", "index", "sense" );
        return value;
    }
    
    QuaternionicSymmetry getQuaternionicSymmetry( String name )
    {
        return symmetries .getQuaternionSet( name );
    }
    
    public Symmetry parseSymmetry( String sname )
    {
        OrbitSet group = symmetries .getGroup( sname );  //  Symmetry symm = parseSymmetry( sname );
        Symmetry symm = group .getSymmetry();
        if ( symm == null )
        {
            logger .severe( "UNSUPPORTED symmetry: " + sname );
            throw new IllegalStateException( "no symmetry with name=" + sname );
        }
        else
            return symm;
    }

    /**
     * this is for the old format (before rationalVectors)
     * @param threeD 
     */
    public Construction parseConstruction( String apName, Element attrOrParam, boolean threeD )
    {
        Construction c = null;
        if ( apName .equals( "point" ) ) {
            AlgebraicVector loc = parseAlgebraicVector( attrOrParam, threeD );
            c = new FreePoint( loc, mModelRoot );
        } else if ( apName .equals( "segment" ) ) {
            Element start = DomUtils .getFirstChildElement( attrOrParam, "start" );
            Element end = DomUtils .getFirstChildElement( attrOrParam, "end" );
            AlgebraicVector sloc = parseAlgebraicVector( start, threeD );
            AlgebraicVector eloc = parseAlgebraicVector( end, threeD );
            c = new SegmentJoiningPoints( new FreePoint( sloc, mModelRoot ), new FreePoint( eloc, mModelRoot ) );
        } else if ( apName .equals( "polygon" ) ) {
        	NodeList kids = attrOrParam .getElementsByTagName( "vertex" );
            Point[] pts = new Point[ kids .getLength() ];
            for ( int k = 0; k < kids .getLength(); k++ ) {
                AlgebraicVector loc = parseAlgebraicVector( (Element) kids .item( k ), threeD );
                pts[ k ] = new FreePoint( loc, mModelRoot );
            }
            c = new PolygonFromVertices( pts );
        }
        return c;
    }
    
//    public Map loadCommandAttributesOld( Element editElem )
//    {
//        Map attrs = new HashMap();
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

    public Map loadCommandAttributes( Element editElem, boolean threeD )
    {
        Map attrs = new HashMap();
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
            		attrElem = (Element) elemKid;
                    elemName = attrElem .getLocalName();
                }
            }
        	Object value = parseAlgebraicObject( elemName, attrElem );
        	if ( value == NOT_AN_ATTRIBUTE )
        	    if ( rationalVectors() )
        	    {
        	        if ( elemName .equals( "point" ) )
        	            value = parsePoint( attrElem, "at" );
        	        else if ( elemName .equals( "segment" ) )
        	            value = parseSegment( attrElem, "start", "end" );
        	        else if ( elemName .equals( "polygon" ) )
        	            value = parsePolygon( attrElem, "vertex" );
                    else
        	            throw new IllegalStateException( "unknown parameter construction: " + elemName );
        	    }
        	    else
        	        value = parseConstruction( elemName, attrElem, threeD );
        	attrs .put( attrName, value );
        }
        return attrs;
    }

    public AlgebraicField getField()
    {
        return mField;
    }
    
    public ModelRoot getModelRoot()
    {
        return mModelRoot;
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
    
    public static final void serializeAxis( Element xml, String symmAttr, String dirAttr, String indexAttr, String senseAttr, Axis axis )
    {
        String str = axis .getDirection() .getSymmetry() .getName();
        if ( ! "icosahedral" .equals( str ) )
        	DomUtils .addAttribute( xml, symmAttr, str );
        str = axis .getDirection() .getName();
        if ( ! "blue" .equals( str ) )
        	DomUtils .addAttribute( xml, dirAttr, str );
        	DomUtils .addAttribute( xml, indexAttr, Integer .toString( axis .getOrientation() ) );
        if ( axis .getSense() != Symmetry.PLUS )
        	DomUtils .addAttribute( xml, "sense", "minus" );
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
        String nums = xml .getAttribute( attrName );
        if ( nums == null || nums .isEmpty() )
            return null;
        AlgebraicVector loc = mField .parseVector( nums );
        return new FreePoint( loc, mModelRoot );
    }

    public final Segment parseSegment( Element xml, String startAttrName, String endAttrName )
    {
        String nums = xml .getAttribute( endAttrName );
        if ( nums == null || nums .isEmpty() )
            return null;
        AlgebraicVector eloc = mField .parseVector( nums );
        nums = xml .getAttribute( startAttrName );
        AlgebraicVector sloc = ( nums == null || nums .isEmpty() )?
                 mField .origin( eloc .dimension() )
                : mField .parseVector( nums );
        return new SegmentJoiningPoints( new FreePoint( sloc, mModelRoot ), new FreePoint( eloc, mModelRoot ) );
    }

    public final Polygon parsePolygon( Element xml, String vertexChildName )
    {
        NodeList kids = xml .getElementsByTagName( vertexChildName );
        Point[] pts = new Point[ kids .getLength() ];
        for ( int k = 0; k < kids .getLength(); k++ )
        {
            String nums = ((Element) kids .item( k )) .getAttribute( "at" );
            AlgebraicVector loc = mField .parseVector( nums );
            pts[ k ] = new FreePoint( loc, mModelRoot );
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
            pts[ kmax - k ] = new FreePoint( loc, mModelRoot );
        }
        return new PolygonFromVertices( pts );
    }

    public final Axis parseAxis( Element xml, String symmAttr, String dirAttr, String indexAttr, String senseAttr )
    {
        String sname = xml .getAttribute( symmAttr );
        if ( sname == null || sname .isEmpty() )
            sname = "icosahedral";
        OrbitSet group = symmetries .getGroup( sname );  //  Symmetry symm = parseSymmetry( sname );
        String aname = xml .getAttribute( dirAttr );
        if ( aname == null || aname .isEmpty() )
            aname = "blue";
        else if ( aname .equals( "tan" ) )
        	aname = "sand";
        else if ( aname .equals( "spring" ) )
        	aname = "apple";
        String iname = xml .getAttribute( indexAttr );
        sname = xml .getAttribute( senseAttr );
        int index = Integer .parseInt( iname );
        int sense = Symmetry .PLUS;
        if ( "minus" .equals( sname )) { // NOTE: used to say "index < 0"
            sense = Symmetry .MINUS;
//            index *= -1;
        }
        return group .getDirection( aname ) .getAxis( sense, index );
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

	public String getFormatError( Element element, String edition, String version, int svnRevision )
	{
		String fileEdition = element .getAttribute( "edition" );
        if ( fileEdition == null || fileEdition .isEmpty() )
        	fileEdition = "vZome";
        String thisVersion = edition + " " + version;
        String fileVersion = fileEdition + " " + this.writerVersion;
        if ( ( this.writerVersion == null ) || this.writerVersion .isEmpty() )
        	fileVersion = "an old version of vZome (before 2.1 Beta 7)";
        String error = thisVersion + " has a problem opening this file, which was authored using "
        				+ fileVersion + ".\n\n";

		String fileRev = element .getAttribute( "revision" );
		int fileRevision = ( fileRev == null || fileRev .isEmpty() ) ? 0 : Integer .parseInt( fileRev );
		if ( fileRevision <= svnRevision )
			error += "Please ZIP up the contents of your vZome logs folder and send them to bugs@vzome.com.";
		else if ( ! fileEdition .equals( edition ) )
			error += "You need a copy of " + fileVersion + ", or perhaps a newer version of " + edition;
		else
			error += "You need a copy of " + fileVersion + ", or newer.";
		return error;
	}
}
