package com.vzome.core.edits;

import com.vzome.core.algebra.AlgebraicNumber;
import com.vzome.core.algebra.AlgebraicVector;
import com.vzome.core.commands.Command;
import com.vzome.core.construction.FreePoint;
import com.vzome.core.construction.Point;
import com.vzome.core.construction.SegmentJoiningPoints;
import com.vzome.core.editor.api.ChangeManifestations;
import com.vzome.core.editor.api.Selection;
import com.vzome.core.model.Manifestation;
import com.vzome.core.model.RealizedModel;
import com.vzome.core.model.Strut;

public class JoinSkewLines extends ChangeManifestations {
    public static final String NAME = "JoinSkewLines";

    public JoinSkewLines(Selection selection, RealizedModel realized) {
        super(selection, realized);
    }
    
    @Override
    public void perform() throws Command.Failure
    {
        final StringBuilder errorMsg = new StringBuilder();
        errorMsg.append("This command requires two non-parallel struts.\n") ;
        
        Strut s0 = null;
        Strut s1 =  null;
        int qty = 0;

        for (Manifestation man : mSelection) {
            if ( man instanceof Strut ) {
                switch(qty) {
                case 0:
                    s0 = (Strut) man;
                    break;
                case 1:
                    s1 = (Strut) man;
                    break;
                default:
                    errorMsg.append("\ntoo many struts are selected.");
                    fail( errorMsg.toString() ); // throw an exception and we're done.
                }
                qty++;
            }
            // balls and panels are also unselected
            unselect( man );
        }
        
        if(qty < 2) {
            errorMsg.append(qty == 1 ? "\nonly one strut is selected." : "\nno struts are selected.");
            fail( errorMsg.toString() ); // throw an exception and we're done.
        }

        // we can safely assume that struts have non-zero offsets
        AlgebraicVector u =  s0.getOffset();
        AlgebraicVector v =  s1.getOffset();

        // At this point, we could check if(u.cross(v).isOrigin())
        // and fail if it's true which indicates that the struts are parallel,
        // but the denominator must be calculated below anyway 
        // and it allows to make the same check without checking the cross product here
        
        // This algorithm is based on http://geomalgorithms.com/a07-_distance.html
        // Variable names are derived from the variable names used in the article 
        // for easy cross reference, but I have also modified some of them to  
        // make a few things clearer (for me) than they were in the original article.
        
        // The article explains that the vector to be derived is perpendicular to both of the originals,
        // which is obvious, but it wasn't initially obvoius to me why that fact 
        // is equivalent to two the equations which are to be solved simultaneously.

        // The dot product of two vectors is the product of their magnitudes 
        // and the cosine of the smallest angle between them.
        // In this case, we want the angle between each of the original struts 
        // and the new strut we're calculating to be a right angle, 
        // so the we want the cosine of the angle between each of the originals 
        // and the final vector to be 0 which is the cosine of 90 degrees.
        // We don't have to calculate the cosine anywhere. 
        // That's all just an explanatory note to help me understand how the algorithm works.
        
        AlgebraicVector p0 = s0.getLocation();
        AlgebraicVector q0 = s1.getLocation();
        
        AlgebraicNumber uuA = u.dot(u);                                 // always >= 0
        AlgebraicNumber uvB = u.dot(v);
        AlgebraicNumber vvC = v.dot(v);                                 // always >= 0
        AlgebraicNumber denD = uuA.times(vvC). minus(uvB.times(uvB));   // always >= 0

        if(denD.isZero()) {
            errorMsg.append("\nstruts are parallel.");
            fail( errorMsg.toString() ); // throw an exception and we're done.
        }
        // we've validated all inputs, so commit all of the unselects
        redo();
        
        AlgebraicVector w = p0.minus(q0);
        AlgebraicNumber uwD = u.dot(w);
        AlgebraicNumber vwE = v.dot(w);

        // sc is the distance from p0 to w0 in units of vector u
        AlgebraicNumber sc = (uvB.times(vwE). minus(vvC.times(uwD))). dividedBy(denD);
        // tc is the distance from q0 to w1 in units of vector v
        AlgebraicNumber tc = (uuA.times(vwE). minus(uvB.times(uwD))). dividedBy(denD);
        
        // At this point, the two lines may intersect or they may be skewed.
        // if they intersect, we just make one ball at that point.
        // Otherwise, make two balls, one collinear with each original strut,
        // and then add the strut between them.
        // In either case, we'll need at least one ball

        AlgebraicVector w0 = p0.plus(u.scale(sc)); // point on s0
        Point pw0 = new FreePoint( w0 );
        select( manifestConstruction( pw0 ) );

        AlgebraicVector w1 = q0.plus(v.scale(tc));
        if(! w1.equals(w0)) {
            // lines don't intersect, so add the 2nd ball and the strut
            Point pw1 = new FreePoint( w1 );
            select( manifestConstruction( pw1 ) );
            select( manifestConstruction( new SegmentJoiningPoints( pw0, pw1 ) ) );
        }
        
        redo(); // commit our selects
    }

    @Override
    protected String getXmlElementName() {
        return NAME;
    }
}
