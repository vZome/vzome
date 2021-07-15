package com.vzome.core.edits;

import java.util.List;
import java.util.Map;

import org.w3c.dom.Element;

import com.vzome.core.algebra.AlgebraicField;
import com.vzome.core.algebra.AlgebraicNumber;
import com.vzome.core.algebra.AlgebraicVector;
import com.vzome.core.algebra.AlgebraicVectors;
import com.vzome.core.algebra.PolygonField;
import com.vzome.core.commands.Command;
import com.vzome.core.commands.Command.Failure;
import com.vzome.core.commands.XmlSaveFormat;
import com.vzome.core.construction.FreePoint;
import com.vzome.core.construction.Point;
import com.vzome.core.construction.SegmentJoiningPoints;
import com.vzome.core.editor.api.ChangeManifestations;
import com.vzome.core.editor.api.EditorModel;
import com.vzome.core.model.Manifestation;
import com.vzome.core.model.Strut;

public class ChordRatio extends ChangeManifestations {

    protected final AlgebraicField field;
    String mode = null;
    int reps = 1;
    AlgebraicNumber chordRatio = null;
    
    public ChordRatio(EditorModel editorModel) {
        super(editorModel);
        field = editorModel.getRealizedModel().getField();
        this.setOrderedSelection(true);
    }

    @Override
    public void perform() throws Command.Failure
    {
        // Accepts exactly 2 struts as parameters
        // Any selected balls or panels are ignored
        String errorMsg = "This command requires two selected struts with a common vertex.\n"
                + "The struts must not be collinear.";

        Strut strut1 = null, strut2 = null;
        for (Manifestation man : mSelection) {
            if (man instanceof Strut) {
                if (strut1 != null) {
                    // too many struts
                    fail(errorMsg);
                }
                strut1 = strut2;
                strut2 = (Strut) man;
            }
        }
        if (strut1 == null) {
            // not enough struts
            fail(errorMsg);
        }

        // find the offsets and the endpoint vectors: v0, v1, v2
        AlgebraicVector offset2 = strut2.getOffset();
        AlgebraicVector common = null, v1 = null, v2 = null;
        {
            AlgebraicVector loc1 = strut1.getLocation();
            AlgebraicVector end1 = strut1.getEnd();
            AlgebraicVector loc2 = strut2.getLocation();
            AlgebraicVector end2 = strut2.getEnd();
            // reverse the offset direction(s) if necessary so the two offsets have a common starting point
            if (loc1.equals(loc2)) {
                common = loc1;
                v1 = end1;
                v2 = end2;
            } else if (end1.equals(loc2)) {
                common = end1;
                v1 = loc1;
                v2 = end2;
            } else if (end1.equals(end2)) {
                common = end1;
                v1 = loc1;
                v2 = loc2;
                offset2 = offset2.negate();
            } else if (loc1.equals(end2)) {
                common = loc1;
                v1 = end1;
                v2 = loc2;
                offset2 = offset2.negate();
            } else {
                // struts don't have a common end point
                fail(errorMsg);
            }
            if(AlgebraicVectors.areCollinear(common, v1, v2)) {
                fail(errorMsg);
            }
        }
        
        // Don't call fail() after this point because we don't want to unselect anything if we fail.
        // Only unselect struts after validation is complete.
        unselect(strut1);
        unselect(strut2);
        redo(); 
        // Don't call manifestConstruction() before this point.
        // Only add new balls and struts after validation is complete.
        
        /*
         *         |<== s2.offset * chordRatio ==>|     
         *      v1 *                             (*) v3 = new ball
         *         |                             /
         *  strut1 |                            / <==== strut3 = new strut 
         *         |                           /
         *  common *--------------------------* v2
         *         |<======== strut2 ========>|     
         */        

        // Ensure that balls exist and are selected in order at both ends of strut1
        // TODO: DJH - Is it a problem to use FreePoints here?
        // Should we loop through mManifestations and look for an existing ball before we make a new one?
        // I think it's safe, but I'm seeing some odd selection behavior occasionally 
        // immediately after using this command, especially after calling fail(). 
        // Not sure if it's related to this or not but but I'll do it the easy way for now. 
        select( manifestConstruction( new FreePoint(v1) ) );
        select( manifestConstruction( new FreePoint(common) ) );
        Point p2 = new FreePoint(v2);
        Manifestation ball2 = manifestConstruction( p2 ); 
        
        AlgebraicVector original = v1;
        for(int i = 1; i <= reps; i++) {
            AlgebraicVector v3 = v1.plus(offset2.scale(chordRatio));
            if(v3.equals(v2)) {
                // shouldn't be possible unless the first two struts are collinear and have just the right chordRatio 
                throw new IllegalStateException("vertices have unexpectedly converged at " + v3 + "after " + i + " iterations with mode = " + mode);
            }
            Point p3 = new FreePoint(v3);
            Manifestation ball3 = manifestConstruction( p3 );
            Strut strut3 = (Strut) manifestConstruction( new SegmentJoiningPoints(p2, p3) );
            // don't select the new strut yet but do select its new end points
            select(ball2);
            select(ball3);
            // shift everything to get ready for the next iteration
            v1 = common;
            common = v2;
            v2 = v3;
            p2 = new FreePoint(v2);
            strut1 = strut2;
            strut2 = strut3;
            offset2 = strut2.getOffset();
            redo(); // probably not necessary inside the loop
            if(original.equals(common)) {
                if(i+1 < reps) {
                    System.out.println("ChordRatio/" + mode + " has converged after only " + i + " reps.\n"
                        + "Expected " + (reps-2) + " reps using a chordRatio of "  + chordRatio);
                }
                break;
            }
        }
        if(!original.equals(common)) {
            // We haven't converged yet so select tyhe last two struts
            // so we're set up to repeat the command.
            // This will always occur when chordRatio >= 3 or chordRatio <= -1
            // and often in between -1 and 3 as well.
            // One example is when mode = parabola so chordFactor = 3 and reps = 1
            select(strut1);
            select(strut2);
        }
        redo();
    }
    
    @Override
    public void configure( Map<String,Object> props ) 
    {
        this.mode = (String) props .get( "mode" );
        configure();
    }

    protected void configure( ) 
    {
        if ( mode == null ) {
            mode = "4";
        }
        switch(mode) {
        case "3":
            reps = 3;
            chordRatio = field.zero();
            return;
        case "4":
            reps = 4;
            chordRatio = field.one();
            return;
        case "5":
            reps = 5;
            chordRatio = this.getGoldenRatio();
            return;
        case "pentagram":
            reps = 5;
            chordRatio = field.one().minus(this.getGoldenRatio());
            return;
        case "6":
            reps = 6;
            chordRatio = field.createRational(2);
            return;
        case "10":
            reps = 10;
            chordRatio = this.getGoldenRatio();
            chordRatio = chordRatio.times(chordRatio); // phi squared;
            return;
        case "parabola":
            reps = 1;
            chordRatio = field.createRational(3);
            return;
        case "-1":
            reps = 1;
            chordRatio = field.createRational(-1);
            return;
        default:
            if(field instanceof PolygonField) {
                int nSides = ((PolygonField)field).polygonSides();
                int nSteps = Integer.parseInt(mode);
                if(nSteps == nSides) {
                    reps = nSteps;
                    chordRatio = field.getAffineScalar();
                    return;
                }
                List<Integer> factors = PolygonField.factors(nSides);
                if(factors.contains(nSteps)) {
                    reps = nSteps;
                    chordRatio = getIntermediateChordRatio(nSteps);
                    return;
                }
            }
        }
        throw new IllegalStateException(" Unknown mode: '" + mode + "'");
    }
    
    protected AlgebraicNumber getIntermediateChordRatio(int nSteps) {
        if(field instanceof PolygonField) {
            PolygonField polyField = (PolygonField)field;
            int nSides = polyField.polygonSides();
            int nDiags = polyField.diagonalCount();
            int stepSize = nSides / nSteps;
            int start = stepSize - 1;
            int end = 3 * stepSize - 1;
            if(end > nDiags) {
                // if we wrap over center
                end = nDiags = end;
            }
            AlgebraicNumber den = polyField.getUnitDiagonal(start);
            AlgebraicNumber num = polyField.getUnitDiagonal(end);
            return num.dividedBy(den);
        }
        throw new IllegalStateException("nSteps: '" + nSteps + "'");
    }

    protected AlgebraicNumber getGoldenRatio() {
        AlgebraicNumber goldenRatio = field.getGoldenRatio();
        if(goldenRatio == null) {
            throw new IllegalStateException("Invalid mode: Golden Ratio is null for the " + field.getName() + " field.");
        }
        return goldenRatio;
    }
    
    @Override
    protected String getXmlElementName() {
        return "ChordRatio";
    }

    @Override
    protected void getXmlAttributes( Element element )
    {
        element .setAttribute( "mode", mode );
    }

    @Override
    protected void setXmlAttributes( Element xml, XmlSaveFormat format )
            throws Failure
    {
        mode = xml.getAttribute( "mode" );
        configure();
    }

}
