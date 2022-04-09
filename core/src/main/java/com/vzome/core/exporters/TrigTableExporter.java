package com.vzome.core.exporters;

import java.io.File;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.function.BiFunction;
import java.util.function.Function;

import com.vzome.core.algebra.AlgebraicField;
import com.vzome.core.algebra.AlgebraicMatrix;
import com.vzome.core.algebra.AlgebraicNumber;
import com.vzome.core.algebra.AlgebraicVector;
import com.vzome.core.algebra.PolygonField;
import com.vzome.core.math.RealVector;
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
            // TODO: consider https://github.com/FasterXML/jackson-databind instead of StringBuilder
            StringBuilder buf = new StringBuilder();

            buf.append( "{\n" );
            writeFieldData(field, buf);
            writeEmbedding(field, buf);
            writeUnitDiagonals(field, buf);
            writeNamedNumbers(field, buf);
            writeMultiplicationTable(field, buf);
            writeDivisionTable(field, buf);
            writeExponentsTable(field, buf);
            writeTrigTable(field, buf);
            buf.append( "}\n" );

            output = new PrintWriter( writer );
            // we've used single quote as the delimiters so far for simplicity. Now switch to double quotes for json
            output.println(buf.toString().replace("'", "\""));
            output .flush();
            generateHtml(file);
        }
        catch(ClassCastException ex) {
            throw new IllegalArgumentException("Trig exports are only implemented for polygon fields", ex);
        }
    }
    
    private void generateHtml(File file) {
        if(file != null) {
            // TODO: Generate an HTML that uses the json. 
            // This should probably just be a String resource 
            // that's written to an associated file name with minimal changes.
        }
    }

    private static void writeFieldData(PolygonField field, StringBuilder buf) {
        buf.append(" 'field': { ")
        .append("'name': '").append(field.getName()).append("', ")
        .append("'order': ").append(field.getOrder()).append(", ")
        .append("'parity': '").append(field.isOdd() ? "odd" : "even").append("', ")
        .append("'diagonalCount': ").append(field.diagonalCount()).append(", ")
        .append("'polygonSides': ").append(field.polygonSides())
        .append(" },\n");
    }
    
    private static void writeEmbedding(PolygonField field, StringBuilder buf) {
        // mostly copied the first loop from ShapesJsonExporter to get the same format
        // First, turn the embedding into a set of real column vectors.
        AntiprismSymmetry symm = new AntiprismSymmetry(field);
        float[] embeddingRows = new float[]{ 1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1 };
        for(int i = 0; i < 3; i++ ) {
            RealVector column = symm .embedInR3( field .basisVector( 3, i ) );
            embeddingRows[ 0 + i ] = column.x;
            embeddingRows[ 4 + i ] = column.y;
            embeddingRows[ 8 + i ] = column.z;
        }
        buf.append(" 'embedding': [ ");
        String delim = "";
        for(float f : embeddingRows) {
            buf.append(delim).append(f);
            delim = ", ";
        }
        buf.append( " ],\n" );
    }

    private static void writeUnitDiagonals(PolygonField field, StringBuilder buf) {
        buf.append( " 'unitDiagonals': [ ");
        String delim = "\n";
        for(int i=0; i < field.diagonalCount(); i++) {
            AlgebraicNumber number = field.getUnitDiagonal(i);
            String name = (i == 0) ? "1" : field.getIrrational(i);
            buf.append(delim);
            delim = ",\n";
            buf.append("  { 'name': '").append(name).append("'");
            buf.append(", 'value': ").append(formatAN(number));
            buf.append(", 'independant': ").append(i < field.getOrder());
            buf.append(" }");
        }
        buf.append( "\n ],\n" );
    }

    // copied this list from NumberController
    public static String[] OPTIONAL_NAMED_VALUES = new String[] {
        // increasing order except that phi and other greek letters go before any sqrtN
        "phi",      // 5,2 
        "rho",      // 7,2
        "sigma",    // 7,3
        // alpha, beta and gamma are ambiguous when nSides is a mutiple of both 9 and 13
        // but since 9*13=117, and we seldom use 117N-gons, I'll live with it. 
        "alpha",    // 13,2 and 9,2
        "beta",     // 13,3 and 9,3
        "gamma",    // 13,4 and 9,4
        "delta",    // 13,5
        "epsilon",  // 13,6
        "theta",    // 11,2
        "kappa",    // 11,3
        "lambda",   // 11,4
        "mu",       // 11,5
        //"seperator",
        // square roots
        "\u221A2",
        "\u221A3",
        "\u221A5",
        "\u221A6",
        "\u221A7",
        "\u221A8",
        "\u221A10",
      };

    private static void writeNamedNumbers(PolygonField field, StringBuilder buf) {
        buf.append( " 'namedNumbers': [" );
        String delim = "\n";
        for(String name : OPTIONAL_NAMED_VALUES) {
            AlgebraicNumber number = field.getNumberByName(name);
            if(number != null) {
                buf.append(delim);
                delim = ",\n";
                buf.append("  { 'name': '").append(name).append("', ");
                buf.append("'value': ").append(formatAN(number)).append(", ");
                switch(name) {
                case "phi":
                    writeDiagonalRatio(field, 5, buf);
                    break;
                case "rho":
                    writeDiagonalRatio(field, 7, buf);
                    break;
                case "sigma":
                    writeDiagonalRatio(field, 7, buf, 3); // sigma uses step = 3
                    break;
                case "\u221A2":
                    writeDiagonalRatio(field, 4, buf);
                    break;
                case "\u221A3":
                    writeDiagonalRatio(field, 6, buf);
                    break;
                default:
                    break;
                }
                buf.append("'reciprocal': ").append(formatAN(number.reciprocal()));
                buf.append(" }");
            }
        }
        buf.append( "\n ],\n" );
    }
    
    private static void writeTrigTable(PolygonField field, StringBuilder buf) {
        buf.append( " 'trig': [\n" );
//        if(field.isEven()) {
            // trig tables don't apply to odd-gon fields because they are embedded
            final AlgebraicMatrix rotationMatrix = (new AntiprismSymmetry(field)).getRotationMatrix();
            final AlgebraicVector vX = field.basisVector(3, X); // rotation matrix expects 3D even though we only use 2
            final AlgebraicVector v1 = rotationMatrix.timesColumn(vX);
            AlgebraicVector bisector = vX.plus(v1).scale(field.getUnitTerm(1).reciprocal());
            AlgebraicVector v = vX;
            int nSides = field.polygonSides();
                    
            for(int i = 0; i < nSides; i++) {
                writeTrigEntry(i, nSides, v, bisector, buf);
                buf.append(i == nSides-1 ? "\n" : ",\n");
                v = rotationMatrix.timesColumn(v);
                bisector = rotationMatrix.timesColumn(bisector);
            }
//        }
        buf.append( " ]\n" );        
    }

    private static void writeMultiplicationTable(PolygonField field, StringBuilder buf) {
        writeTable(field, buf, "multiplication", (AlgebraicNumber n1, AlgebraicNumber n2) -> n1.times(n2));
    }
    
    private static void writeDivisionTable(PolygonField field, StringBuilder buf) {
        writeTable(field, buf, "division", (AlgebraicNumber n1, AlgebraicNumber n2) -> n1.dividedBy(n2));
    }
    
    // this is pretty much a copy of ParameterizedFields.mathTableToString()
    private static void writeTable(AlgebraicField field, StringBuilder buf, String tableName,
            BiFunction<AlgebraicNumber, AlgebraicNumber, AlgebraicNumber> op) {
        // This part is in case we ever use fields other than PolygonFields.
        final Function<Integer, AlgebraicNumber> operandFactory = (field instanceof PolygonField) 
                ? ((PolygonField) field)::getUnitDiagonal 
                : field::getUnitTerm;
        final int limit = (field instanceof PolygonField) ? ((PolygonField) field).diagonalCount() : field.getOrder();

        buf.append(" '").append(tableName).append("': [\n");
        String delim1 = "";
        for (int i = 0; i < limit; i++) {
            AlgebraicNumber n1 = operandFactory.apply(i);
            buf.append(delim1).append("  [ ");
            delim1 = ",\n";
            String delim2 = "";
            for (int j = 0; j < limit; j++) {
                AlgebraicNumber n2 = operandFactory.apply(j);
                AlgebraicNumber result = op.apply(n1, n2);
                buf.append(delim2);
                delim2 = ", ";
                buf.append(formatAN(result));
            }
            buf.append(" ]");
        }
        buf.append("\n ],\n");
    }
    
    private static void writeExponentsTable(PolygonField field, StringBuilder buf) {
        final int limit = field.diagonalCount();
        final int range = 6;
        
        buf.append(" 'exponents': [\n");
        String delim1 = "";
        for (int i = 1; i < limit; i++) {
            buf.append(delim1).append("  {");
            delim1 = ",\n";
            String name = field.getIrrational(i);
            buf.append(" 'base': '").append(name).append("'");
            {
                buf.append(",\n    'positivePowers': [ ");
                String delim2 = "";
                final AlgebraicNumber base = field.getUnitDiagonal(i);
                AlgebraicNumber result = base; 
                for (int power = 1; power <= range; power++) {
                    buf.append(delim2);
                    delim2 = ", ";
                    buf.append(formatAN(result));
                    result = result.times(base);
                }
                buf.append(" ]");
            }
            {
                buf.append(",\n    'negativePowers': [ ");
                String delim2 = "";
                final AlgebraicNumber base = field.getUnitDiagonal(i).reciprocal();
                AlgebraicNumber result = base; 
                for (int power = 1; power <= range; power++) {
                    buf.append(delim2);
                    delim2 = ", ";
                    buf.append(formatAN(result));
                    result = result.times(base);
                }
                buf.append(" ]");
            }
            buf.append("\n  }");
        }
        buf.append("\n ],\n");
    }
    
    private static void writeDiagonalRatio(PolygonField field, int divisor, StringBuilder buf) {
        writeDiagonalRatio(field, divisor, buf, 2); // default step is 2. sigma uses 3
    }

    private static void writeDiagonalRatio(PolygonField field, int divisor, StringBuilder buf, int step) {
        if (field.polygonSides() % divisor == 0) {
            int n = field.polygonSides() / divisor;
            AlgebraicNumber denominator = field.getUnitDiagonal(n - 1); 
            AlgebraicNumber numerator = field.getUnitDiagonal((step * n) - 1);
            buf.append("'numerator': ").append(formatAN(numerator)).append(", ");
            buf.append("'denominator': ").append(formatAN(denominator)).append(", ");
//            buf.append("'verify' : ").append(formatAN(numerator.dividedBy(denominator))).append(", ");
        } else {
            throw new IllegalStateException("shouldn't ever get here");
        }
    }
    
    private static void writeTrigEntry(int i, int nSides, AlgebraicVector vStep, AlgebraicVector bisector, StringBuilder buf) {
        final String delim1 = "', ";
        final String delim2 = ", ";
        final String infinite = "{ 'alg': '\u221e', 'dec': '\u221e', 'tdf': '\u221e' }";
        AlgebraicVector v = vStep;
        for(int n = 0; n < 2; n++) {
            final int k = (i*2)+n;
            Double degrees = k*180.0d/nSides;
            AlgebraicNumber sin = v.getComponent(Y); 
            AlgebraicNumber cos = v.getComponent(X); 
            
            buf.append("  { ");
            buf.append("'rot': '").append(k).append("/").append(nSides*2).append(delim1); // rotation
            buf.append("'rad': '").append(k).append("\u03C0/").append(nSides).append(delim1); // radians
            buf.append("'deg': ").append(degrees).append(delim2); // degrees
            
            buf.append("'sin': ").append(formatAN(sin)).append(delim2);
            buf.append("'cos': ").append(formatAN(cos)).append(delim2);
            buf.append("'tan': ").append(cos.isZero() ? infinite : formatAN(sin.dividedBy(cos))).append(delim2);
            buf.append("'csc': ").append(sin.isZero() ? infinite : formatAN(sin.reciprocal())).append(delim2);
            buf.append("'sec': ").append(cos.isZero() ? infinite : formatAN(cos.reciprocal())).append(delim2);
            buf.append("'cot': ").append(sin.isZero() ? infinite : formatAN(cos.dividedBy(sin)));
            buf.append(" }");
            if(n == 0) {
                buf.append(",\n");
            }
            v = bisector; // repeat it all using the bisector
        }
    }

    private static String formatAN(AlgebraicNumber n) {
        StringBuilder buf = new StringBuilder();
        buf.append("{ 'alg': '").append(n)
        .append("', 'dec': ").append(n.evaluate())
        .append(", 'tdf': [");
        String delim = "";
        for(int term : n.toTrailingDivisor()) {
            buf.append(delim);
            delim = ", ";
            buf.append(term);
        }
        buf.append( "] }");
        return buf.toString();
    }
    
    @Override
    public String getFileExtension() {
        return "math.json";
    }

    @Override
    public String getContentType()
    {
        return "application/json";
    }

}
