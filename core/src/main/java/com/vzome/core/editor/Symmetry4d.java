
//(c) Copyright 2005, Scott Vorthmann.  All rights reserved.

package com.vzome.core.editor;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.vzome.core.algebra.AlgebraicVector;
import com.vzome.core.algebra.Quaternion;
import com.vzome.core.commands.Command;
import com.vzome.core.commands.Command.Failure;
import com.vzome.core.construction.Construction;
import com.vzome.core.construction.FreePoint;
import com.vzome.core.construction.Point;
import com.vzome.core.construction.PointRotated4D;
import com.vzome.core.construction.Polygon;
import com.vzome.core.construction.PolygonRotated4D;
import com.vzome.core.construction.Segment;
import com.vzome.core.construction.SegmentRotated4D;
import com.vzome.core.math.symmetry.QuaternionicSymmetry;
import com.vzome.core.model.Manifestation;
import com.vzome.core.model.RealizedModel;

/**
 * This is a modern replacement for CommandQuaternionSymmetry, which is a legacy command.
 * It duplicates the math from that command, but one key change: only parameter objects that lie
 * in the W=0 plane are transformed.  This makes it safe and predictable to use
 * on objects produced by Polytope4d, which retain their 4D coordinates.
 *   
 * As with CommandQuaternionSymmetry, all transformed vertices are projected to the W=0 plane
 * before being added to the model.
 *   
 * @author vorth
 *
 */
public class Symmetry4d extends ChangeManifestations
{    
	private final QuaternionicSymmetry left, right;

    public Symmetry4d( Selection selection, RealizedModel realized, final QuaternionicSymmetry leftSymm, final QuaternionicSymmetry rightSymm )
    {
        super( selection, realized );
		this.left = leftSymm;
		right = rightSymm;
    }

    @Override
    protected String getXmlElementName()
    {
        return "Symmetry4d";
    }
    
    private static boolean inW0hyperplane( AlgebraicVector v )
    {
    	if ( v .dimension() > 3 )
    		return v .getComponent( AlgebraicVector .W4 ) .isZero();
    	else
    		return true;
    }
    
    @Override
    public void perform() throws Failure
    {
        List<Construction> params = new ArrayList<>();
        for (Manifestation man : mSelection) {
            // avoid SELECTION BUG: the unselect() below just plans the unselection, but... (search for the next SELECTION BUG comment)
            unselect( man );
            // here is the difference from CommandQuaternionSymmetry
            Iterator<Construction> cs = man .getConstructions();
            Construction useThis = null;
            if ( ! cs .hasNext() )
                throw new Command.Failure( "No construction for this manifestation" );
            for (Iterator<Construction> iterator = man .getConstructions(); iterator.hasNext();) {
                Construction construction = iterator.next();
                if (construction instanceof Point) {
                    Point p = (Point) construction;
                    if ( ! inW0hyperplane( p .getLocation() ) )
                        throw new Command.Failure( "Some ball is not in the W=0 hyperplane." );
                } else if (construction instanceof Segment) {
                    Segment s = (Segment) construction;
                    if ( ! inW0hyperplane( s .getStart()  ) )
                        throw new Command.Failure( "Some strut end is not in the W=0 hyperplane." );
                    if ( ! inW0hyperplane( s .getEnd() ) )
                        throw new Command.Failure( "Some strut end is not in the W=0 hyperplane." );
                } else if (construction instanceof Polygon) {
                    Polygon p = (Polygon) construction;
                    for( int i = 0; i < p.getVertexCount(); i++) {
                        if (!inW0hyperplane(p.getVertex(i))) {
                            throw new Command.Failure( "Some panel vertex is not in the W=0 hyperplane." );
                        }
                    }
                } else {
                    throw new Command.Failure( "Unknown construction type." );
                }
                useThis = construction;
            }
            if ( useThis != null )
            	params .add( useThis );
        }
        
        // avoid SELECTION BUG: what we want to do here is to redo what we've planned so far, so that the selection state is in sync
        redo();

        Quaternion[] leftRoots = this .left .getRoots();
        Quaternion[] rightRoots = this .right .getRoots();
        for (Quaternion leftRoot : leftRoots) {
            for (Quaternion rightRoot : rightRoots) {
                for (Construction construction : params) {
                    Construction result = null;
                    if (construction instanceof Point) {
                        result = new PointRotated4D(leftRoot, rightRoot, (Point) construction);
                    } else if (construction instanceof Segment) {
                        result = new SegmentRotated4D(leftRoot, rightRoot, (Segment) construction);
                    } else if (construction instanceof Polygon) {
                        result = new PolygonRotated4D(leftRoot, rightRoot, (Polygon) construction);
                    } else {
                        // TODO handle other constructions 
                        // TODO handle other constructions
                    }
                    if ( result == null )
                        continue;
                    manifestConstruction( result );
                }
            }
        }
        redo();
    }
    
    FreePoint rotateAndProject( AlgebraicVector loc3d, Quaternion leftQuaternion, Quaternion rightQuaternion )
    {
        AlgebraicVector loc = loc3d .inflateTo4d( true );
        loc = rightQuaternion .leftMultiply( loc );
        loc = leftQuaternion .rightMultiply( loc );
        loc = loc .projectTo3d( true );
        return new FreePoint( loc );
    }
}

