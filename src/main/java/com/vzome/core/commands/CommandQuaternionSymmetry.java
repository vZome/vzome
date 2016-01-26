

package com.vzome.core.commands;


import com.vzome.core.algebra.Quaternion;
import com.vzome.core.construction.Construction;
import com.vzome.core.construction.ConstructionChanges;
import com.vzome.core.construction.ConstructionList;
import com.vzome.core.construction.Point;
import com.vzome.core.construction.PointRotated4D;
import com.vzome.core.construction.Polygon;
import com.vzome.core.construction.PolygonRotated4D;
import com.vzome.core.construction.Segment;
import com.vzome.core.construction.SegmentRotated4D;
import com.vzome.core.math.symmetry.QuaternionicSymmetry;

/**
 * @author Scott Vorthmann
 */
public class CommandQuaternionSymmetry extends CommandTransform
{
    public void setFixedAttributes( AttributeMap attributes, XmlSaveFormat format )
    {
        super.setFixedAttributes( attributes, format );
        if ( ! attributes .containsKey( LEFT_SYMMETRY_GROUP_ATTR_NAME ) )
        {
            mLeft = format .getQuaternionicSymmetry( "H_4" );
            attributes .put( LEFT_SYMMETRY_GROUP_ATTR_NAME, mLeft );
        }
        if ( ! attributes .containsKey( RIGHT_SYMMETRY_GROUP_ATTR_NAME ) )
        {
            mRight = format .getQuaternionicSymmetry( "H_4" );
            attributes .put( RIGHT_SYMMETRY_GROUP_ATTR_NAME, mRight );
        }
    }

    public static final String LEFT_SYMMETRY_GROUP_ATTR_NAME = SYMMETRY_GROUP_ATTR_NAME;
    public static final String RIGHT_SYMMETRY_GROUP_ATTR_NAME = "right.symmetry.group";
    
    private QuaternionicSymmetry mLeft, mRight;
    
    public CommandQuaternionSymmetry( QuaternionicSymmetry left, QuaternionicSymmetry right )
    {
        mLeft = left;
        mRight = right;
    }
    
    public CommandQuaternionSymmetry() {}
    
    public Object[][] getAttributeSignature()
    {
        return GROUP_ATTR_SIGNATURE;
    }

    public ConstructionList apply( final ConstructionList parameters, AttributeMap attributes, final ConstructionChanges effects ) throws Failure
    {
        // accommodate bugs fixed in 2.1 beta 14... this was in AbstractCommand.setXmlAttributes(), and needs to go somewhere else
        // TODO put this in QuaternionCommandEdit somehow
//        {
//            attributes .put( CommandQuaternionSymmetry .LEFT_SYMMETRY_GROUP_ATTR_NAME, QuaternionicSymmetry.H4 );
//            attributes .put( CommandQuaternionSymmetry .RIGHT_SYMMETRY_GROUP_ATTR_NAME, QuaternionicSymmetry.H4 );
//        }

        if ( mLeft == null )
            mLeft = (QuaternionicSymmetry) attributes .get( LEFT_SYMMETRY_GROUP_ATTR_NAME );
        else if ( ! attributes .containsKey( LEFT_SYMMETRY_GROUP_ATTR_NAME ) ) // make sure it gets recorded in a save
            attributes .put( LEFT_SYMMETRY_GROUP_ATTR_NAME, mLeft );
        if ( mRight == null )
            mRight = (QuaternionicSymmetry) attributes .get( RIGHT_SYMMETRY_GROUP_ATTR_NAME );
        else if ( ! attributes .containsKey( RIGHT_SYMMETRY_GROUP_ATTR_NAME ) ) // make sure it gets recorded in a save
            attributes .put( RIGHT_SYMMETRY_GROUP_ATTR_NAME, mRight );
        Quaternion[] leftRoots = mLeft .getRoots(), rightRoots = mRight .getRoots();
        
        final Construction[] params = parameters .getConstructions();
        ConstructionList output = new ConstructionList();
        for ( int j = 0; j < params .length; j++ )
            output .addConstruction( params[j] );

        for ( int i = 0; i < leftRoots.length; i++ ) 
            for ( int j = 0; j < rightRoots.length; j++ ) {
                for ( int k = 0; k < params .length; k++ ){
                    Construction result = null;
                    if ( params[k] instanceof Point ) {
                        result = new PointRotated4D( leftRoots[i], rightRoots[j], (Point) params[k] );
                    } else if ( params[k] instanceof Segment ) {
                        result = new SegmentRotated4D( leftRoots[i], rightRoots[j], (Segment) params[k] );
                    } else if ( params[k] instanceof Polygon ) {
                        result = new PolygonRotated4D( leftRoots[i], rightRoots[j], (Polygon) params[k] );
                    } else {
                        // TODO handle other constructions 
                    }
                    if ( result == null )
                        continue;
                    effects .constructionAdded( result );
                    output .addConstruction( result );
                }
            }
        return output;
    }
}
