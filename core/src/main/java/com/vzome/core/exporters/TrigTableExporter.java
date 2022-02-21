package com.vzome.core.exporters;

import java.io.File;
import java.io.PrintWriter;
import java.io.Writer;

import com.vzome.core.algebra.AlgebraicMatrix;
import com.vzome.core.algebra.AlgebraicNumber;
import com.vzome.core.algebra.AlgebraicVector;
import com.vzome.core.algebra.PolygonField;
import com.vzome.core.math.symmetry.AntiprismSymmetry;
import com.vzome.core.render.Colors;
import com.vzome.core.render.RenderedModel;
import com.vzome.core.viewing.Camera;
import com.vzome.core.viewing.Lights;

public class TrigTableExporter extends Exporter3d {
    private static final int X = AlgebraicVector.X;
    private static final int Y = AlgebraicVector.Y;

    public TrigTableExporter(Camera scene, Colors colors, Lights lights, RenderedModel model) {
        super(scene, colors, lights, model);
    }

    @Override
    public void doExport(File file, Writer writer, int height, int width) throws Exception {
        try {
            final PolygonField field = (PolygonField) this.mModel.getField();
            if(field.isOdd()) {
                throw new IllegalStateException("The " + field.getName() + " field is odd.");
            }
            final AlgebraicMatrix rotationMatrix = (new AntiprismSymmetry(field)).getRotationMatrix();
            final AlgebraicVector vX = field.basisVector(3, X); // rotation matrix expects 3D even though we only use 2
            final AlgebraicVector v1 = rotationMatrix.timesColumn(vX);
            AlgebraicVector bisector = vX.plus(v1).scale(field.getUnitTerm(1).reciprocal());
            AlgebraicVector v = vX;

            int nSides = field.polygonSides();
            StringBuilder buf = new StringBuilder();

            buf.append( "{\n" );
            buf.append( " 'field' : { ")
                .append("'name' : '" ).append( field.getName() ).append( "', ")
                .append("'order' : '" ).append( field.getOrder() ).append( "', ")
                .append("'polygonSides' : '" ).append( nSides ).append( "' },\n" );
            buf.append( " 'trig' : [\n" );
                    
            // trig table
            for(int i = 0; i < nSides; i++) {
                writeTrigEntry(i, nSides, v, bisector, buf);
                buf.append(i == nSides-1 ? "\n" : ",\n");
                v = rotationMatrix.timesColumn(v);
                bisector = rotationMatrix.timesColumn(bisector);
            }

            buf.append( " ]\n" );
            buf.append( "}\n" );

            output = new PrintWriter( writer );
            // we've used single quote as the delimiters so far for simplicity. Now switch to double quotes for json
            output.println(buf.toString().replace("'", "\""));
            output .flush();
            generateHtml(file);
        }
        catch(ClassCastException | IllegalStateException ex) {
            throw new IllegalArgumentException("Trig exports are only implemented for even polygon fields", ex);
        }
    }
    
    private void generateHtml(File file) {
        if(file != null) {
            // TODO: Generate an HTML that uses the json. 
            // This should probably just be a String resource 
            // that's written to an associated file name with minimal changes.
        }
    }
    
    private void writeTrigEntry(int i, int nSides, AlgebraicVector vStep, AlgebraicVector bisector, StringBuilder buf) {
        final String delim1 = "', ";
        final String delim2 = ", ";
        final String infinite = "{ 'alg' : '\u221e',  'dec' : '\u221e' }";
        AlgebraicVector v = vStep;
        for(int n = 0; n < 2; n++) {
            final int k = (i*2)+n;
            Double degrees = k*180.0d/nSides;
            AlgebraicNumber sin = v.getComponent(Y); 
            AlgebraicNumber cos = v.getComponent(X); 
            
            buf.append("  { ");
            buf.append("'rot' : '").append(k).append("/").append(nSides*2).append(delim1); // rotation
            buf.append("'rad' : '").append(k).append("\u03C0/").append(nSides).append(delim1); // radians
            buf.append("'deg' : '").append(degrees).append(delim1); // degrees
            
            buf.append("'sin' : ").append(formatAN(sin)).append(delim2);
            buf.append("'cos' : ").append(formatAN(cos)).append(delim2);
            buf.append("'tan' : ").append(cos.isZero() ? infinite : formatAN(sin.dividedBy(cos))).append(delim2);
            buf.append("'csc' : ").append(sin.isZero() ? infinite : formatAN(sin.reciprocal())).append(delim2);
            buf.append("'sec' : ").append(cos.isZero() ? infinite : formatAN(cos.reciprocal())).append(delim2);
            buf.append("'cot' : ").append(sin.isZero() ? infinite : formatAN(cos.dividedBy(sin)));
            buf.append(" }");
            if(n == 0) {
                buf.append(",\n");
            }
            v = bisector; // repeat it all using the bisector
        }
    }

    private String formatAN(AlgebraicNumber n) {
        return "{ 'alg' : '" + n + "',  'dec' : '" + n.evaluate() + "' }";
    }
    
    @Override
    public String getFileExtension() {
        return "trig.json";
    }

    @Override
    public String getContentType()
    {
        return "application/json";
    }

}
