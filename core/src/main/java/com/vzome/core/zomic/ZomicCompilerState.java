package com.vzome.core.zomic;

import java.util.Stack;

import com.vzome.core.algebra.AlgebraicNumber;
import com.vzome.core.math.symmetry.Axis;
import com.vzome.core.math.symmetry.Direction;
import com.vzome.core.math.symmetry.IcosahedralSymmetry;
import com.vzome.core.zomic.program.Move;
import com.vzome.core.zomic.program.Nested;
import com.vzome.core.zomic.program.Reflect;
import com.vzome.core.zomic.program.Rotate;
import com.vzome.core.zomic.program.Scale;
import com.vzome.core.zomic.program.Symmetry;
import com.vzome.core.zomic.program.Walk;
import com.vzome.core.zomic.program.ZomicStatement;

public class ZomicCompilerState
{
    public ZomicCompilerState( IcosahedralSymmetry icosaSymm )
    {
        icosaSymmetry = icosaSymm;
        namingConvention = new ZomicNamingConvention( icosaSymm );
    }
    
    private final IcosahedralSymmetry icosaSymmetry;
    private final ZomicNamingConvention namingConvention ;
    private final Stack<ZomicStatement> statements = new Stack<>();
    private final Stack<ZomicStatementTemplate<? extends ZomicStatement> > templates = new Stack<>();
    
    public Walk getProgram()
    {
        return statements.size() == 0 
                ? new Walk() // just so we never return null, even parsing errors or an empty string
                : (Walk) statements.firstElement();
    }
    
    public void prepareStatement( ZomicStatement statement )
    {
        statements.push(statement);
    }
    
    public ZomicStatementTemplate<? extends ZomicStatement> peekTemplate()
    {
        return this .templates .peek();
    }
    
    public ZomicStatementTemplate<? extends ZomicStatement> popTemplate()
    {
        return this .templates .pop();
    }
    
    public void commitLastStatement() {
        ZomicStatement statement = statements.pop();
        if ( statement instanceof Nested ) {
            ZomicStatement body = ((Nested)statement).getBody();
            if ( (body == null) || ( (body instanceof Walk) && ((Walk)body).size() == 0 ) )
            {
                // don't bother saving an empty Nested statement
                return;
            }
        }
        commit(statement);
    }
            
    public void commit(ZomicStatement newStatement)
    {
        ZomicStatement currentStatement = statements.peek();
        if ( currentStatement instanceof Walk ) {
            ((Walk) currentStatement).addStatement( newStatement );
        } else {
            ((Nested) currentStatement).setBody( newStatement );
        }
    }

    public void reset()
    {
        this .statements .clear();
        this .templates .clear();
    }

    /* 
    ******************************************************
    * BEGIN ZomicStatementTemplate and supporting classes
    ******************************************************
    */

    public interface ZomicStatementTemplate <T extends ZomicStatement> {
        T generate() throws Exception;
    }

    public interface IHaveAxisInfo {
        // e.g. red -2+
        String axisColor();             // red
        void axisColor(String s);

        String indexNumber();           // -2
        void indexNumber(String s);

        String handedness();            // +
        void handedness(String s);

        String indexFullName();         // -2+
    }

    public class AxisInfo 
    implements IHaveAxisInfo
    {
        private String axisColor = "";
        private String indexNumber = "";
        private String handedness = "";

        Axis generate() throws Exception {
            try {
                Axis axis = namingConvention.getAxis(axisColor, indexFullName() );
                // This check has been moved into a thorough unit test instead of a runtime check.
                // but I'll leave the code commented out here in case additional colors are eventually supported and need debugging
                //                  String check = namingConvention.getName( axis );
                //                  if ( axis != namingConvention.getAxis(axisColor, check ) ) {
                //                      log( axisColor + " " + indexFullName() + " mapped to " + check );
                //                  }
                if(axis == null) {
                    String msg = "bad axis specification: '" + axisColor + " " + indexFullName() + "'";
                    throw new Exception( msg );
                }
                return axis;
                //} catch( ArrayIndexOutOfBoundsException ex ){
                //} catch( NullPointerException  ex ) {
            } catch( RuntimeException ex) {
                String msg = "bad axis specification: '" + axisColor + " " + indexFullName() + "'";
                throw new Exception( msg, ex);
            } 
        }

        @Override
        public String axisColor() { return axisColor; }
        @Override
        public void axisColor(String s) { axisColor = s; }

        @Override
        public String indexNumber() { return indexNumber; }
        @Override
        public void indexNumber(String s) { indexNumber = s; }

        @Override
        public String handedness() { return handedness; }
        @Override
        public void handedness(String s) { handedness = s; }

        @Override
        public String indexFullName() { return indexNumber + handedness; }
    }

    public void setCurrentScale(int scale) { 
        ((ScaleInfo)templates.peek()).scale = scale;
    }

    public class ScaleInfo {
        public int ones = 1;
        public int phis = 0;
        public int scale = 1;

        AlgebraicNumber generate(IcosahedralSymmetry symmetry) {
            return symmetry.getField().createAlgebraicNumber( ones, phis, 1, scale );
        }
    }

    public class ScaleTemplate 
    extends ScaleInfo
    implements ZomicStatementTemplate<Scale>
    {   
        @Override
        public Scale generate() {
            AlgebraicNumber algebraicNumber = generate(icosaSymmetry);
            return new Scale(algebraicNumber);
        }
    }

    public class MoveTemplate 
    extends ScaleInfo
    implements ZomicStatementTemplate<Move>, IHaveAxisInfo
    {
        private final AxisInfo axisInfo = new AxisInfo();

        public int denominator = 1;
        public String sizeRef = null;

        public MoveTemplate() {
            scale = ZomicNamingConvention.MEDIUM;
        }

        private boolean isVariableLength = false;
        public boolean isVariableLength() { 
            return ( isVariableLength   // DJH New proposed alternative to size -99
                    || (-99 == scale)   // old variable size flag used only internally by strut resources.
                    ); 
        }
        public void isVariableLength(boolean is)  { isVariableLength = is; }

        AlgebraicNumber generate(String axisColor) throws Exception {
            // validate 
            if ( denominator != 1 ) {
                Direction direction = icosaSymmetry.getDirection(axisColor);
                if ( direction == null || ! direction.hasHalfSizes()) {
                    String msg = "half struts are not allowed on '" + axisColor + "' axes.";
                    throw new Exception( msg );
                }
            } 
            // Zero is the "variable" length indicator. Used only by internal core strut resources
            if (isVariableLength()) { 
                return icosaSymmetry.getField().zero();
            }
            // adjustments per color
            int lengthFactor = 1;
            int scaleOffset = 0;
            switch (axisColor) {
            case "blue":
                lengthFactor = 2;
                break;
            case "green":
                lengthFactor = 2;
                break;
            case "yellow":
                scaleOffset = -1;
                break;
            case "purple":
                scaleOffset = -1;
                break;
            default:
                break;
            }
            // calculate
            return icosaSymmetry.getField().createAlgebraicNumber( 
                    ones * lengthFactor, 
                    phis * lengthFactor, 
                    denominator, 
                    scale + scaleOffset);
        }

        @Override
        public Move generate() throws Exception {
            Axis axis = axisInfo.generate();
            AlgebraicNumber strutLength = generate(axisColor());
            return new Move(axis, strutLength);
        }

        @Override
        public String axisColor() { return axisInfo.axisColor; }
        @Override
        public void axisColor(String s) { axisInfo.axisColor(s); }

        @Override
        public String indexNumber() { return axisInfo.indexNumber; }
        @Override
        public void indexNumber(String s) { axisInfo.indexNumber(s); }
        @Override

        public String handedness() { return axisInfo.handedness; }
        @Override
        public void handedness(String s) { axisInfo.handedness(s); }


        @Override
        public String indexFullName() { return axisInfo.indexFullName(); }
    }

    public class RotateTemplate 
    implements ZomicStatementTemplate<Rotate>, IHaveAxisInfo
    {
        private final AxisInfo axisInfo = new AxisInfo();
        public int steps = 1;

        @Override
        public Rotate generate() throws Exception {
            Axis axis = axisInfo.generate();
            return new Rotate(axis, steps);
        }

        @Override
        public String axisColor() { return axisInfo.axisColor; }
        @Override
        public void axisColor(String s) { axisInfo.axisColor(s); }

        @Override
        public String indexNumber() { return axisInfo.indexNumber; }
        @Override
        public void indexNumber(String s) { axisInfo.indexNumber(s); }
        @Override

        public String handedness() { return axisInfo.handedness; }
        @Override
        public void handedness(String s) { axisInfo.handedness(s); }


        @Override
        public String indexFullName() { return axisInfo.indexFullName(); }
    }

    public class ReflectTemplate 
    implements ZomicStatementTemplate<Reflect>, IHaveAxisInfo
    {
        private final AxisInfo axisInfo = new AxisInfo();
        private final boolean isThroughCenter;

        public ReflectTemplate(boolean isThruCenter) {
            isThroughCenter = isThruCenter;
        }

        @Override
        public Reflect generate() throws Exception {
            Reflect result = new Reflect();
            if( !isThroughCenter ) {
                if( "".equals(axisColor()) && !"".equals(indexNumber()) ) {
                    axisColor("blue");
                }
                Axis axis = axisInfo.generate();
                result.setAxis(axis);
            }
            return result;
        }

        @Override
        public String axisColor() { return axisInfo.axisColor; }
        @Override
        public void axisColor(String s) {
            if( !"blue".equals(s)) {
                enforceBlueAxis();
            } else {
                axisInfo.axisColor(s); 
            }
        }

        @Override
        public String indexNumber() { return axisInfo.indexNumber; }
        @Override
        public void indexNumber(String s) {
            // Old way silently removes any negative sign on the axis index
            // so we always reflect around the positive axis.
            // Don't know if that matters, but we'll do it the same way here.
            // Note that the "symmetry around axis" statement does not strip the sign.
            // but "symmetry through blueAxisIndex" does, just like ReflectTemplate.
            s = s.replaceFirst("-", "");
            axisInfo.indexNumber(s);
            if( isThroughCenter && !"".equals(indexNumber())) {
                axisColor("blue");
            }
        }

        @Override
        public String handedness() { return axisInfo.handedness; }
        @Override
        public void handedness(String s) {
            enforceBlueAxis();
        }

        @Override
        public String indexFullName() { return axisInfo.indexFullName(); }

        private void enforceBlueAxis() {
            throw new IllegalStateException("Only 'center' or blue axis indexes are allowed.");
        }
    }

    public enum SymmetryModeEnum {
        Icosahedral,    // around the origin
        RotateAroundAxis,
        MirrorThroughBlueAxis,
        ReflectThroughOrigin
    }

    public class SymmetryTemplate 
    implements ZomicStatementTemplate<Symmetry>, IHaveAxisInfo
    {
        private final AxisInfo axisInfo = new AxisInfo();
        private final SymmetryModeEnum symmetryMode;

        public SymmetryTemplate(SymmetryModeEnum mode) {
            symmetryMode = mode;
        }

        @Override
        public Symmetry generate() throws Exception {
            // Rather than creating a new Symmetry statement here, apply the collected template parameters 
            // to the Symmetry statement that's already on the Statement stack collecting other statements in its body,
            // but don't pop it off the Statements stack here, just peek() for now.
            Symmetry result = (Symmetry) statements.peek();
            switch(symmetryMode) {
            case Icosahedral:
                break;
            case RotateAroundAxis:
            {
                Rotate rotate = new Rotate( null, -1 );
                Axis axis = axisInfo.generate();
                rotate.setAxis(axis);
                result.setPermute( rotate );
            }
            break;
            case MirrorThroughBlueAxis:
            {
                Reflect reflect = new Reflect();
                Axis axis = axisInfo.generate();
                reflect.setAxis(axis);
                result.setPermute( reflect );
            }
            break;
            case ReflectThroughOrigin:
                result.setPermute( new Reflect() );
                break;
            default:
                throw new IllegalStateException(
                        "Unexpected SymmetryModeEnum: " + 
                                symmetryMode == null
                                ? "<null>" 
                                        : symmetryMode.toString() 
                        );
            }
            return result;
        }

        @Override
        public String axisColor() { return axisInfo.axisColor; }
        @Override
        public void axisColor(String s) { axisInfo.axisColor(s); }

        @Override
        public String indexNumber() { return axisInfo.indexNumber; }
        @Override
        public void indexNumber(String s) {
            // Note that the "symmetry around axis" statement does not strip the sign.
            // but "symmetry through blueAxisIndex" does, just like ReflectTemplate.
            // Don't know if that matters, but we'll do it the same way here.
            if(symmetryMode == SymmetryModeEnum.MirrorThroughBlueAxis) {
                s = s.replaceFirst("-", "");
            }
            axisInfo.indexNumber(s);
            if( symmetryMode == SymmetryModeEnum.MirrorThroughBlueAxis ) {
                axisColor("blue");
            }
        }

        @Override
        public String handedness() { return axisInfo.handedness; }
        @Override
        public void handedness(String s) { axisInfo.handedness(s); }

        @Override
        public String indexFullName() { return axisInfo.indexFullName(); }
    }

    public void prepareSymmetryTemplate( SymmetryModeEnum symmetryMode )
    {
        SymmetryTemplate template = new SymmetryTemplate(symmetryMode);
        this .templates .push( template );
    }

    public void prepareMoveTemplate()
    {
        MoveTemplate template = new MoveTemplate();
        this .templates .push( template );
    }

    public void prepareScaleTemplate()
    {
        ScaleTemplate template = new ScaleTemplate();
        this .templates .push( template );
    }

    public void prepareReflectTemplate(boolean isThruCenter)
    {
        ReflectTemplate template = new ReflectTemplate(isThruCenter);
        this .templates .push( template );
    }

    public void prepareRotateTemplate()
    {
        RotateTemplate template = new RotateTemplate();
        this .templates .push( template );
    }

}
