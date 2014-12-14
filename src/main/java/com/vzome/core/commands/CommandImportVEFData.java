

package com.vzome.core.commands;

import java.io.InputStream;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import org.w3c.dom.Element;

import com.vzome.core.algebra.AlgebraicField;
import com.vzome.core.algebra.RationalVectors;
import com.vzome.core.construction.Construction;
import com.vzome.core.construction.ConstructionChanges;
import com.vzome.core.construction.ConstructionList;
import com.vzome.core.construction.FreePoint;
import com.vzome.core.construction.ModelRoot;
import com.vzome.core.construction.Point;
import com.vzome.core.construction.Segment;
import com.vzome.core.construction.SegmentJoiningPoints;
import com.vzome.core.construction.VefToModel;
import com.vzome.core.math.DomUtils;

/**
 * @author Scott Vorthmann
 */
public class CommandImportVEFData extends AbstractCommand
{
    public static final int X = 0, Y = 1, Z = 2, W = 3;
    
    public static final String VEF_STRING_ATTR_NAME = "org.vorthmann.zome.commands.CommandImportVEFData" + ".vef.string";

    public static final String FIELD_ATTR_NAME =
            "org.vorthmann.zome.commands.CommandImportVEFData" + ".field";

    public static final String NO_INVERSION_ATTR_NAME =
            "org.vorthmann.zome.commands.CommandImportVEFData" + ".no.inversion";

    private static final Object[][] PARAM_SIGNATURE = new Object[][]{ { GENERIC_PARAM_NAME, Construction.class } };

    private static final Object[][] ATTR_SIGNATURE =
        new Object[][]{ { VEF_STRING_ATTR_NAME, String.class }, { Command.FIELD_ATTR_NAME, InputStream.class }, { NO_INVERSION_ATTR_NAME, InputStream.class } };

    static Logger logger = Logger .getLogger( "com.vzome.core.commands.importVEF" );

    public Object[][] getParameterSignature()
    {
        return PARAM_SIGNATURE;
    }

    public Object[][] getAttributeSignature()
    {
        return ATTR_SIGNATURE;
    }
    
    public boolean attributeIs3D( String attrName )
    {
        if ( "symmetry.axis.segment" .equals( attrName ) )
            return false;
        else
            return true;
    }

    /*
     * Currently, there is no way to set this via the UI... only reading it from XML
     */
    private int[] quaternionVector = null;

    /**
     * Only called when migrating a 2.0 model file.
     */
    public void setQuaternion( int[] offset )
    {
        quaternionVector = offset;
    }

    /*
     * Adding this to support a 4D quaternion.
     */
    public Map setXml( Element xml, XmlSaveFormat format ) 
    {
        Map attrs = super .setXml( xml, format );
        
        quaternionVector = format .parseRationalVector( xml, "quaternion" );
        
        return attrs;
    }
    
    public void getXml( Element result, Map attributes )
    {
        if ( quaternionVector != null )
        	DomUtils .addAttribute( result, "quaternion", RationalVectors .toString( quaternionVector ) );
        
        super .getXml( result, attributes );
    }

    
    public void setFixedAttributes( Map attributes, XmlSaveFormat format )
    {
        if ( ! attributes .containsKey( CommandImportVEFData .FIELD_ATTR_NAME ) )
            attributes .put( CommandImportVEFData .FIELD_ATTR_NAME, format .getField() );

        super .setFixedAttributes( attributes, format );
    }

    public ConstructionList apply( ConstructionList parameters, Map attributes,
            ConstructionChanges effects ) throws Failure
    {
        ConstructionList result = new ConstructionList();
        AlgebraicField field = (AlgebraicField) attributes .get( CommandImportVEFData .FIELD_ATTR_NAME );
        if ( field == null )
            field = (AlgebraicField) attributes .get( Command.FIELD_ATTR_NAME );
        ModelRoot root = (ModelRoot) attributes .get( MODEL_ROOT_ATTR_NAME );
        Segment symmAxis = (Segment) attributes .get( CommandTransform.SYMMETRY_AXIS_ATTR_NAME );
        String vefData = (String) attributes .get( VEF_STRING_ATTR_NAME );
        Boolean noInversion = (Boolean) attributes .get( NO_INVERSION_ATTR_NAME );
        
        int[] quaternion = quaternionVector;
        
        if ( quaternion == null )
            quaternion = (symmAxis==null)? null : symmAxis .getOffset();  // will get inflated to 4D when we know wFirst()
        
        if ( quaternion != null )
            quaternion = field .scaleVector( quaternion, field .createPower( -5 ) );
        
        if ( noInversion != null && noInversion .booleanValue() )
            new VefToModelNoInversion( quaternion, root, effects ) .parseVEF( vefData, field );
        else
            new VefToModel( quaternion, root, effects, field .createPower( 5 ), null ) .parseVEF( vefData, field );
        
        return result;
    }

    
    private class VefToModelNoInversion extends VefToModel
    {
        protected int[][][] mProjected;
        
        protected final Set mUsedPoints = new HashSet();
        
        public VefToModelNoInversion( int[] quaternion, ModelRoot root, ConstructionChanges effects )
        {
            super( quaternion, root, effects, root .getField() .createPower( 5 ), null );
        }

        protected void addVertex( int index, int[] location )
        {
            location = field .scaleVector( location, scale );
            if ( mProjection != null )
                location = mProjection .projectImage( location, wFirst() );
            mVertices[ index ] = new FreePoint( location, mRoot );
//            mEffects .constructionAdded( mVertices[ index ] );
        }

        protected void startEdges( int numEdges )
        {
            mProjected = new int[ numEdges ][ 2 ][];
        }

        protected void addEdge( int index, int v1, int v2 )
        {
            Point p1 = mVertices[ v1 ], p2 = mVertices[ v2 ];
            if ( p1 == null || p2 == null ) return;
            Segment seg = new SegmentJoiningPoints( p1, p2 );
            
            int[] pr1 = field .negate( field .projectTo3d( p1 .getLocation(), wFirst() ) ); // p1 .getLocation() .projectTo3D() .neg();
            int[] pr2 = field .negate( field .projectTo3d( p2 .getLocation(), wFirst() ) ); // p2 .getLocation() .projectTo3D() .neg();
            
            for ( int i = 0; i < index; i++ ) {
                if ( Arrays .equals( pr1, mProjected[ i ][ 0 ] ) && Arrays .equals( pr2, mProjected[ i ][ 1 ] ) )
                    return;
                if ( Arrays .equals( pr2, mProjected[ i ][ 0 ] ) && Arrays .equals( pr1, mProjected[ i ][ 1 ] ) )
                    return;
            }
            mProjected[ index ][ 0 ] = field .negate( pr1 );
            mProjected[ index ][ 1 ] = field .negate( pr2 );
            
            mEffects .constructionAdded( seg );
            mUsedPoints .add( p1 );
            mUsedPoints .add( p2 );
        }
        
        protected void endEdges()
        {
            for ( Iterator pts = mUsedPoints .iterator(); pts .hasNext(); )
                mEffects .constructionAdded( (Point) pts .next() );
        }
    }
}
