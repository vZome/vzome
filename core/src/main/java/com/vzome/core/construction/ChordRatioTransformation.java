package com.vzome.core.construction;

import com.vzome.core.algebra.AlgebraicVector;

public class ChordRatioTransformation extends Transformation {

    private final AlgebraicVector inputStart;
    private final AlgebraicVector inputEnd;
    private final AlgebraicVector outputStart;
    private final AlgebraicVector outputOffset;

    public ChordRatioTransformation(AlgebraicVector inStart, AlgebraicVector inEnd, AlgebraicVector outStart, AlgebraicVector outOffset) {
        super(inStart.getField());
        this.inputStart = inStart;
        this.inputEnd = inEnd;
        this.outputStart = outStart;
        this.outputOffset = outOffset;
//        mapParamsToState();
    }

    @Override
    public Construction transform(Construction c) {
        if (c instanceof Segment) {
            Segment s = (Segment) c;
            if(inputStart.equals(s.getStart()) && inputEnd.equals(s.getEnd())) {
                if(outputOffset == null) {
                    return new FreePoint(outputStart);
                } 
                s = new TransformedSegment(this, s);
                s.setStateVariables(outputStart, outputOffset, false);
                return s;
            }
        }
        return null;
    }

    @Override
    public AlgebraicVector transform( AlgebraicVector arg )
    {
        return arg;
    }
    
    @Override
    protected boolean mapParamsToState() {
        // TODO Auto-generated method stub
        // return setStateVariables( ??? );
        return false;
    }
}
