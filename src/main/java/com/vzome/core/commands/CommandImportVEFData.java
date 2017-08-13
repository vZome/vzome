

package com.vzome.core.commands;

import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

import org.w3c.dom.Element;

import com.vzome.core.algebra.AlgebraicField;
import com.vzome.core.algebra.AlgebraicVector;
import com.vzome.core.construction.Construction;
import com.vzome.core.construction.ConstructionChanges;
import com.vzome.core.construction.ConstructionList;
import com.vzome.core.construction.FreePoint;
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

    private static final Logger logger = Logger .getLogger( "com.vzome.core.commands.importVEF" );

    @Override
    public Object[][] getParameterSignature()
    {
        return PARAM_SIGNATURE;
    }

    @Override
    public Object[][] getAttributeSignature()
    {
        return ATTR_SIGNATURE;
    }
    
    @Override
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
    private AlgebraicVector quaternionVector = null;

    /**
     * Only called when migrating a 2.0 model file.
     */
    @Override
    public void setQuaternion( AlgebraicVector offset )
    {
        quaternionVector = offset;
    }

    /*
     * Adding this to support a 4D quaternion.
     */
    @Override
    public AttributeMap setXml( Element xml, XmlSaveFormat format ) 
    {
        AttributeMap attrs = super .setXml( xml, format );
        
        quaternionVector = format .parseRationalVector( xml, "quaternion" );
        
        return attrs;
    }
    
    @Override
    public void getXml( Element result, AttributeMap attributes )
    {
        if ( quaternionVector != null )
        	DomUtils .addAttribute( result, "quaternion", quaternionVector .toString() );
        
        super .getXml( result, attributes );
    }

    
    @Override
    public void setFixedAttributes( AttributeMap attributes, XmlSaveFormat format )
    {
        if ( ! attributes .containsKey( CommandImportVEFData .FIELD_ATTR_NAME ) )
            attributes .put( CommandImportVEFData .FIELD_ATTR_NAME, format .getField() );

        super .setFixedAttributes( attributes, format );
    }

    @Override
    public ConstructionList apply( ConstructionList parameters, AttributeMap attributes,
            ConstructionChanges effects ) throws Failure
    {
        ConstructionList result = new ConstructionList();
        AlgebraicField field = (AlgebraicField) attributes .get( CommandImportVEFData .FIELD_ATTR_NAME );
        if ( field == null )
            field = (AlgebraicField) attributes .get( Command.FIELD_ATTR_NAME );
        Segment symmAxis = (Segment) attributes .get( CommandTransform.SYMMETRY_AXIS_ATTR_NAME );
        String vefData = (String) attributes .get( VEF_STRING_ATTR_NAME );
        Boolean noInversion = (Boolean) attributes .get( NO_INVERSION_ATTR_NAME );
        
        AlgebraicVector quaternion = quaternionVector;
        
        if ( quaternion == null )
            quaternion = (symmAxis==null)? null : symmAxis .getOffset();  // will get inflated to 4D when we know wFirst()
        
        if ( quaternion != null )
            quaternion = quaternion .scale( field .createPower( -5 ) );
        
        if ( noInversion != null && noInversion )
            new VefToModelNoInversion( quaternion, field, effects ) .parseVEF( vefData, field );
        else
            new VefToModel( quaternion, effects, field .createPower( 5 ), null ) .parseVEF( vefData, field );
        
        return result;
    }

    
    private class VefToModelNoInversion extends VefToModel
    {
        protected AlgebraicVector[][] mProjected;
        
        protected final Set<Point> mUsedPoints = new HashSet<>();
        
        public VefToModelNoInversion( AlgebraicVector quaternion, AlgebraicField field, ConstructionChanges effects )
        {
            super( quaternion, effects, field .createPower( 5 ), null );
        }

        @Override
        protected void addVertex( int index, AlgebraicVector location )
        {
            if ( scale != null && ! usesActualScale() ) {
                location = location .scale( scale );
            }
            if ( mProjection != null )
                location = mProjection .projectImage( location, wFirst() );
            mVertices[ index ] = new FreePoint( location );
//            mEffects .constructionAdded( mVertices[ index ] );
        }

        @Override
        protected void startEdges( int numEdges )
        {
            mProjected = new AlgebraicVector[ numEdges ][ 2 ];
        }

        @Override
        protected void addEdge( int index, int v1, int v2 )
        {
            Point p1 = mVertices[ v1 ], p2 = mVertices[ v2 ];
            if ( p1 == null || p2 == null ) return;
            Segment seg = new SegmentJoiningPoints( p1, p2 );
            
            AlgebraicVector pr1 = p1 .getLocation() .projectTo3d( wFirst() ) .negate();
            AlgebraicVector pr2 = p2 .getLocation() .projectTo3d( wFirst() ) .negate();
            
            for ( int i = 0; i < index; i++ ) {
                if ( pr1 .equals( mProjected[ i ][ 0 ] ) && pr2 .equals( mProjected[ i ][ 1 ] ) )
                    return;
                if ( pr2 .equals( mProjected[ i ][ 0 ] ) && pr1 .equals( mProjected[ i ][ 1 ] ) )
                    return;
            }
            mProjected[ index ][ 0 ] = pr1 .negate();
            mProjected[ index ][ 1 ] = pr2 .negate();
            
            mEffects .constructionAdded( seg );
            mUsedPoints .add( p1 );
            mUsedPoints .add( p2 );
        }
        
        @Override
        protected void endEdges()
        {
            for (Point point : mUsedPoints) {
                mEffects .constructionAdded( point );
            }
        }
    }
}
