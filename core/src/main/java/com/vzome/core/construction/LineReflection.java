package com.vzome.core.construction;

import com.vzome.core.algebra.AlgebraicNumber;
import com.vzome.core.algebra.AlgebraicVector;
import com.vzome.core.algebra.AlgebraicVectors;

public class LineReflection extends Transformation {
    private final AlgebraicNumber two;
    private final Line mMirrorLine;
    private final AlgebraicVector mStart;
    private final AlgebraicVector mEnd;

    public LineReflection(Segment axis) {
        super(axis.field);
        two = field .createRational(2);
        mMirrorLine = new LineExtensionOfSegment(axis);
        mStart = axis.getStart();
        mEnd = axis.getEnd();
        // DJH: Note that PointReflection and PlaneReflection (and other Constructions)
        // call mapParamsToState() at the end of their c'tor.
        // mapParamsToState() in turn, calls setStateVariables().
        // I don't really understand the purpose of these two methods, 
        // so I haven't followed that pattern for the LineReflection class.
        // I think that pattern is probably only necessary for linear transformations
        // that make use of the mTransform matrix and the mOffset vector in the base class.
        // This LineReflection class is not a linear transformation 
        // so it doesn't make use any of that base class functionality. 
        // Therefore I won't mess with overriding or calling mapParamsToState().
        // I'm not sure what implication this may have down the road 
        // so I'm just making a note of it here for now.
    }

    @Override
    protected final boolean mapParamsToState() {
        return true;
    }

    @Override
    public AlgebraicVector transform(AlgebraicVector arg) {
        AlgebraicVector norm1 = AlgebraicVectors.getNormal(mStart, mEnd, arg);
        if(norm1.isOrigin()) {
            // arg is collinear with mMirrorLine so return it unchanged
            return arg;
        }
        AlgebraicVector norm2 = AlgebraicVectors.getNormal(mStart, mEnd, mEnd.plus(norm1));
        // norm2 is a vector that is orthogonal to mMirrorLine
        // and is on the plane formed by mMirrorLine and v,
        // so the intersection of mMirrorLine and line2 
        // is the point on mMirrorLine that is closest to v.
        
        // I'm just going to use a local LineLineIntersectionPoint 
        // to do the work instead of duplicating the math.
        // This approach has been used in StrutIntersection and PolygonPolygonProjectionToSegment
        Line line2 = new LineFromPointAndVector( arg, norm2 );
        Point point = new LineLineIntersectionPoint(mMirrorLine, line2 );
        AlgebraicVector intersection = point.getLocation();
        
        // double the distance to the intersection point to get the mirror translation vector.
        AlgebraicVector translation = intersection.minus(arg).scale(two);
        // return the translated input
        return arg.plus(translation);
    }
}
