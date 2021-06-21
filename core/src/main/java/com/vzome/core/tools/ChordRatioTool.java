package com.vzome.core.tools;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Element;

import com.vzome.core.algebra.AlgebraicField;
import com.vzome.core.algebra.AlgebraicNumber;
import com.vzome.core.algebra.AlgebraicVector;
import com.vzome.core.commands.Command.Failure;
import com.vzome.core.commands.XmlSaveFormat;
import com.vzome.core.construction.ChordRatioTransformation;
import com.vzome.core.construction.Construction;
import com.vzome.core.construction.Transformation;
import com.vzome.core.editor.AbstractToolFactory;
import com.vzome.core.editor.Tool;
import com.vzome.core.editor.ToolsModel;
import com.vzome.core.editor.api.ChangeManifestations;
import com.vzome.core.editor.api.Selection;
import com.vzome.core.math.symmetry.Axis;
import com.vzome.core.math.symmetry.Direction;
import com.vzome.core.math.symmetry.Symmetry;
import com.vzome.core.model.Manifestation;
import com.vzome.core.model.Strut;

public class ChordRatioTool extends TransformationTool {
    
    private static final String CATEGORY = "chord ratio transformation";
    private static final String LABEL = "Create a chord ratio transformation tool";
    private static final String TOOLTIP = "<p>" +
            "<b>For experts and Linear Algebra students...</b><br>" +
            "<br>" +
            "Each tool applies a chord ratio transformation to a pair of struts <br>" +
            "to generate one new ball and strut.<br>" +
            "<br>" +
            "To create a tool, select two struts in the same orbit (color). <br>" +
            "The selection order matters.  The chord ratio will be the length <br>" +
            "of the first strut divided by the length of the second strut. <br>" +
            "<br>" +
//            "Alternatively, a negative chord ratio can be defined by selecting three <br>" +
//            "collinear balls A, B and C. The chord ratio will be -1 times the distance <br>" +
//            "from A to B divided by the distance from B to C <br>" +
//            "<br>" +
            "To apply the tool select two struts with a common endpoint.  A new <br>" +
            "strut and ball will be generated at the end of the last selected strut. <br>" +
            "<br>" +
            "All balls and struts that are generated will remain selected so that <br>" +
            "a panel can be generated from them after repeatedly applying the tool. <br>" +
        "</p>";

    public static class Factory extends AbstractToolFactory
    {
        public Factory( ToolsModel tools, Symmetry symmetry )
        {
            super( tools, symmetry, CATEGORY, LABEL, TOOLTIP );
        }

        @Override
        protected boolean countsAreValid( int total, int balls, int struts, int panels )
        {
            return ( total == 2 && struts == 2 );
        }

        @Override
        public Tool createToolInternal( String id )
        {
            AlgebraicField field = this .getToolsModel() .getEditorModel() .getRealizedModel() .getField();
            
            AlgebraicNumber chordRatio = null;
            int reps = 1;
            
            final String prefix = CATEGORY + ".builtin/";
            if(id.startsWith(prefix)) {
                final String s = id.substring(prefix.length()); 
                switch (s) {
                case "parallelagram":
                    chordRatio = field.one();
                    reps = 4;
                    break;
    
                case "affine pentagon":
                    chordRatio = getGoldenRatio(field, id);
                    reps = 5;
                    break;

                case "affine pentagram":
                    chordRatio = field.one().minus(getGoldenRatio(field, id));
                    reps = 5;
                    break;
    
                case "affine hexagon":
                    chordRatio = field.createRational(2);
                    reps = 6;
                    break;
    
                case "affine decagon":
                    chordRatio = getGoldenRatio(field, id);
                    chordRatio = chordRatio.times(chordRatio); // goldenRatio squared
                    reps = 10;
                    break;
    
                case "parabola":
                    chordRatio = field.createRational(3);
                    reps = 1;
                    break;
    
                case "affine polygon":
                    chordRatio = field.getAffineScalar();
                    if(chordRatio == null || chordRatio.isOne()) {
                        String msg = field.getName() + " should not create a '" + id + 
                                "' tool unless getAffineScalar() is overridden." ;
                        throw new IllegalStateException(msg);
                    }
                    break;
                    
                default:
                    throw new IllegalStateException("Unknown id: " + id);
                }
            } else {
                chordRatio = calculateChordRatio(this .getToolsModel() .getEditorModel() .getSelection());
            }
            
            return new ChordRatioTool( id, getToolsModel(), chordRatio, reps);
        }
        
        private static AlgebraicNumber getGoldenRatio(AlgebraicField field, String id) {
            AlgebraicNumber goldenRatio = field.getGoldenRatio();
            if(goldenRatio == null) {
                String msg = field.getName() + " should not create a '" + id + 
                        "' tool unless getGoldenRatio() is not null." ;
                throw new IllegalStateException(msg);
            }
            return goldenRatio;
        }
        
        private AlgebraicNumber calculateChordRatio(Selection selection) {
            Symmetry symmetry = getSymmetry();
            if(symmetry == null) {
                // loading from file.. we'll have to wait for xml to deserialize chordRatio
                return null;
            }

            AlgebraicVector offset1 = null;
            AlgebraicVector offset2 = null;
            for (Manifestation man : selection) {
                // don't need to check the type cast since we have exactly 2 struts
                AlgebraicVector offset = ((Strut) man).getOffset();
                if (offset1 == null) {
                    offset1 = offset;
                } else {
                    offset2 = offset;
                }
            }

            Axis zone1 = symmetry .getAxis( offset1 );
            Axis zone2 = symmetry .getAxis( offset2 );
            if ( zone1 == null || zone2 == null )
                return null;
            Direction orbit1 = zone1 .getDirection();
            Direction orbit2 = zone2 .getDirection();
            if ( orbit1 != orbit2 )
                return null;
            
            AlgebraicNumber length1 = zone1 .getLength( offset1 );
            AlgebraicNumber length2 = zone2 .getLength( offset2 );
            return length1.dividedBy(length2);
        }
        
        // This is never called for predefined tools, so chordRatio must be set by createToolInternal
        @Override
        protected boolean bindParameters( Selection selection )
        {
            return calculateChordRatio(selection) != null;
        }
    }

    private AlgebraicNumber chordRatio;
    private final int reps;

    public ChordRatioTool(String name, ToolsModel tools, AlgebraicNumber chordRatio, int reps) {
        super(name, tools);
        this.chordRatio = chordRatio; // may be null when deserializing from file
        this.reps = reps;
        // The first arg to setInputBehaviors must be true 
        // or else everything is deselected before invoking the tool
        // in which case, we can't access the two input struts with a common vertex.
        this.setInputBehaviors(true, false);
        this.setOrderedSelection(true); // TODO: Be sure this is doing its job
    }

    // Checking selection upon tool creation, not when it's being applied
    @Override
    protected String checkSelection(boolean prepareTool) {
        Strut strut1 = null, strut2 = null;
        for (Manifestation man : mSelection) {
            if ( man instanceof Strut ) {
                if(strut1 != null) {
                    return "More than 2 struts are selected";
                }
                strut1 = strut2;
                strut2 = (Strut) man;
            }
        }
        if(strut1 == null) {
            return "Fewer than 2 struts are selected";
        }
        return null;
    }

    /*
     *         |<== s2.length * chordRatio ==>|     
     *       a *                             (*) d = new ball
     *         |                             /
     *  strut1 |                            / <==== new strut 
     *         |                           /
     *       b *--------------------------* c
     *         |<======== strut2 ========>|     
     */
    // This is called when the tool is being applied, not when it's being created.
    @Override
    public void prepare(ChangeManifestations applyTool) {
        this .transforms = new Transformation[] {}; // clear transforms from any prior invocations
        Strut strut1 = null, strut2 = null;
        // By using the last 2 selected struts as inputs and ignoring any others,
        // we can apply the tool repeatedly, even when it never converges 
        // as in the case of a parablola, hyperbola or some ellipses 
        for (Manifestation man : mSelection) {
            if ( man instanceof Strut ) {
                strut1 = strut2;
                strut2 = (Strut) man;
            }
        }
        AlgebraicVector common = findCommonVertex(strut1, strut2);
        if(common == null) {
            return; // Struts do not share a common endpoint
        }
        
        AlgebraicVector v1 = strut1.getEnd();
        if(v1.equals(common)) {
            v1 = strut1.getLocation();
        }
        
        AlgebraicVector v2 = strut2.getEnd();
        if(v2.equals(common)) {
            v2 = strut2.getLocation();
        }

        // Normally, the transformations calculate coordinaltes of  new constructions in transform(), 
        // but since the output of one ChordRatioTransformation becomes the input of the next, 
        // we have to do the math here in the tool, so each ChordRatioTransformation 
        // simply generates its predetermined output. 
        // when the specified input strut is passed to transform() and it ignores all other inputs.
        List<Transformation> transformations = new ArrayList<>(reps);
        final AlgebraicVector inputStart = strut1.getLocation();
        final AlgebraicVector inputEnd = strut1.getEnd();
        AlgebraicVector a = v1;
        AlgebraicVector b = common;
        AlgebraicVector c = v2;
        for(int i = 1; i <= reps; i++) {
            AlgebraicVector offset = a.minus(c).plus(c.minus(b).scale(chordRatio));
            AlgebraicVector d = c.plus(offset);
            if(i == 1) {
                // generate a ball at c
                transformations.add(new ChordRatioTransformation(inputStart, inputEnd, c, null));
            }
            // generate a ball at d
            transformations.add(new ChordRatioTransformation(inputStart, inputEnd, d, null));
            // generate a strut at c with the specified offset
            transformations.add(new ChordRatioTransformation(inputStart, inputEnd, c, offset));
            // prep for next iteration
            a = b;
            b = c;
            c = d;
            if(i < reps && c.equals(v2)) {
                System.out.println("Converged early at " + i + " before the specified " + reps + " reps.");
                break;
            }
        }
        this .transforms = transformations.toArray(this .transforms);
    }

    public static AlgebraicVector findCommonVertex( Strut s1, Strut s2 )
    {
        if(s1 == null || s2 == null || s1.equals(s2)) {
            return null;
        }
        AlgebraicVector common = s1.getLocation();
        if(common.equals(s2.getLocation()) || common.equals(s2.getEnd()))
            return common;
        common = s1.getEnd();
        return (common.equals(s2.getLocation()) || common.equals(s2.getEnd()))
            ? common : null;
    }

    @Override
    public void performEdit( Construction c, ChangeManifestations applyTool )
    {
        // This is copied from the super class implementation
        // but I've taken out all of the color copying code.
        // TODO: When the tools eventually get the option to use orbit colors instead of copying the input,
        // then this tool should default to using orbit colors and this overriden method should be removed.
        for (Transformation transform : transforms) {
            Construction result = transform .transform( c );
            if ( result == null )
                continue;
//            Color color = c .getColor();
//            result .setColor( color ); // just for consistency
            Manifestation m = applyTool .manifestConstruction( result );
//            if ( m != null )  // not sure why, but this happens
//                if ( color != null ) // This can be true in the Javascript world
//                    applyTool .colorManifestation( m, c .getColor() );
        }
        applyTool .redo();
    }
    
    @Override
    protected void getXmlAttributes( Element element )
    {
        XmlSaveFormat.serializeNumber(element, "chordRatio", chordRatio);
        // don't need to serialize reps since it's set in the c'tor
        super .getXmlAttributes( element );
    }

    @Override
    protected void setXmlAttributes( Element element, XmlSaveFormat format ) throws Failure
    {
        this. chordRatio = format.parseNumber(element, "chordRatio");
        // don't need to serialize reps since it's set in the c'tor
        super .setXmlAttributes( element, format );
    }
    
    @Override
    protected String getXmlElementName()
    {
        return "ChordRatioTool";
    }

    @Override
    public String getCategory()
    {
        return CATEGORY;
    }

    @Override
    public String getOverlayText() {
        return (reps > 1)
            ? Integer.toString(reps)
            : super.getOverlayText();
    }
}
