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
//    private static final NumberFormat FORMAT = NumberFormat .getNumberInstance( Locale .US );
    private static final int X = AlgebraicVector.X;
    private static final int Y = AlgebraicVector.Y;

    public TrigTableExporter(Camera scene, Colors colors, Lights lights, RenderedModel model) {
        super(scene, colors, lights, model);
//        FORMAT .setMinimumFractionDigits( 16 ); // same as OFF exporter
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
            
            final int nSides = field.polygonSides();
            AlgebraicVector v = vX;
            final String title = nSides + "-gon Algebraic Field Trig Table";

            StringBuilder buf = new StringBuilder();

            // TODO: replace all of this manually coded HTML with a resource template like the DaeExporter does
            buf.append( "<!DOCTYPE html>\n" );
            buf.append( "<html lang='en'>\n" );
            buf.append( "<head>\n" );
            buf.append( "<meta http-equiv='Content-Type' content='text/html; charset=utf-8'/>\n" );
            buf.append( "<title>" ).append( title ).append( "</title>\n" );
            
            buf.append( "<script>\n" );
            buf.append( "function toggleNumberFormat() {\n" );
            buf.append( "  var checked = document.getElementById('number-format').checked;\n" );
            // TODO: get these next two rules by name, not by ordinal
            buf.append( "  // TODO: get these next two rules by name, not by ordinal\n");
            buf.append( "  document.styleSheets[0].rules[0].style.display =  checked ? 'none' : 'block';\n" );
            buf.append( "  document.styleSheets[0].rules[1].style.display = !checked ? 'none' : 'block';\n" );
            buf.append( "}\n" );
            buf.append( "</script>\n" );
            
            buf.append( "<style>\n" );
            buf.append(
            "div.alg-num {\n").append( // TODO: for now, toggleNumberFormat() expects this to be rule 0
            "  display: block;\n").append( // default to show algebraic number form
            "  white-space: nowrap;\n").append(
            "}\n").append(
            "div.dec-num {\n").append( // TODO: for now, toggleNumberFormat() expects this to be rule 1
            "  display:none;\n").append( // default to hiding decimal number form
            "}\n").append( 
            "table, th, td {\n").append( 
            "  border: 1px solid black;\n").append( 
            "  border-collapse: collapse;\n").append(
            "  padding-left: 5px;\n").append(
            "  padding-right: 5px;\n").append(
            "}\n").append(
            "tr:nth-child(even) {\n").append(
            "  background-color: rgba(0, 118, 149, 0.4);\n").append( // semi-transparent blue strut
            "}\n").append(
            "tr:nth-child(odd) {\n").append(
            "  background-color: rgba(0, 141, 54, 0.4);\n").append( // semi-transparent green strut
            "}\n").append(
            "tr:hover {\n").append(
            "  background-color: rgba(199, 199, 199, 0.1);\n").append( // semi-transparent gray
            "}\n").append(
            "th {\n").append(
            "  background-color: rgba(0, 192, 192, 1.0);\n").append( // solid light teal
            "}\n").append(
            "</style>\n" );
            
            buf.append( "</head>\n<body>\n" );

            // now for the actual rendered page
            buf.append( "<p><h1>" ).append( title ).append( "</h1></p>\n" );
            
            buf.append( "<p>\n" );
            buf.append( "<input id='number-format' type='checkbox' onclick='toggleNumberFormat()'>\n" );
            buf.append( "<label for='number-format'>Show algebraic numbers as decimals</label>\n" );
            buf.append( "</p>\n" );
                    
            // common algebraic values
            buf.append( "<p>\n" );
            // TODO: add rows for sqrt2, sqrt3, sqrt7 and other significant values supported by the field
            // TODO: add columns for the numerator / denominator pairs that generate the ratio
            buf.append( "<!-- TODO: add rows for sqrt2, sqrt3, sqrt7 and other significant values supported by the field -->\n" );
            buf.append( "<!-- TODO: add columns for the numerator / denominator pairs that generate the ratio -->\n" );
            AlgebraicNumber phi = field.getGoldenRatio();
            if(phi != null) {
                buf.append( "<table>\n" );
                buf.append( "<tr><th>name</th><th>value</th><th>reciprocal</th></tr>\n" );
                buf.append( "<tr><td>phi</td><td>" )
                .append( formatAN(phi) ).append( "</td><td>" )
                .append( formatAN(phi.reciprocal()) )
                .append( "</td></tr>\n" );
                buf.append( "</table>\n" );
            }
            buf.append( "</p>\n" );

            // trig table
            buf.append( "<p>\n" );
            buf.append( "<table>\n" );
            buf.append( "<tr><th>rotation</th><th>radians</th><th>degrees</th><th>sin</th><th>cos</th><th>tan</th>")
            .append("<th>csc = 1/sin</th><th>sec = 1/cos</th><th>cot = 1/tan</th></tr>\n" );            
            for(int i = 0; i < nSides; i++) {
                writeTrigEntry(i, nSides, v, bisector, buf);
                v = rotationMatrix.timesColumn(v);
                bisector = rotationMatrix.timesColumn(bisector);
            }
            buf.append( "</table>\n" );
            buf.append( "</p>\n" );
            
            // TODO: Add simple multiplication, division and exponential tables
            // TODO: Add an option to show algebraic numbers positionally with trailing divisor and no missing elements  
            
            buf.append( "</body>\n</html>\n" );

            output = new PrintWriter( writer );
            output.println(buf.toString());
            output .flush();
        }
        catch(ClassCastException | IllegalStateException ex) {
            throw new IllegalArgumentException("Trig exports are only implemented for even polygon fields", ex);
        }
    }
    
    private void writeTrigEntry(int i, int nSides, AlgebraicVector vStep, AlgebraicVector bisector, StringBuilder buf) {
        String startl = "<tr><td>";
        String delim = "</td><td>";
        String endl = "</td></tr>\n";
        String infinite = "&#x221e;";
        AlgebraicVector v = vStep;
        for(int n = 0; n < 2; n++) {
            final int k = (i*2)+n;
            AlgebraicNumber sin = v.getComponent(Y); 
            AlgebraicNumber cos = v.getComponent(X); 
            
            buf.append(startl);
            buf.append(n == 0 ? i : "").append(delim); // rotation
            buf.append(k).append("&#x3C0;/").append(nSides).append(delim); // radians
            buf.append(k*180.0d/nSides).append(delim); // degrees
            
            buf.append(formatAN(sin)).append(delim);
            buf.append(formatAN(cos)).append(delim);
            buf.append(cos.isZero() ? infinite : formatAN(sin.dividedBy(cos))).append(delim); // tan
            buf.append(sin.isZero() ? infinite : formatAN(sin.reciprocal())).append(delim);   // csc
            buf.append(cos.isZero() ? infinite : formatAN(cos.reciprocal())).append(delim);   // sec
            buf.append(sin.isZero() ? infinite : formatAN(cos.dividedBy(sin)));               // cot
            buf.append(endl);
            v = bisector; // repeat it all using the bisector
        }
    }

    private String formatAN(AlgebraicNumber n) {
//      buf.append(FORMAT.format(doubleValue));
        return "<div class='alg-num'>" + n.toString() + "</div>" +
               "<div class='dec-num'>" + n.evaluate() + "</div>";
    }
    
    @Override
    public String getFileExtension() {
        return "html";
    }

    @Override
    public String getContentType()
    {
        return "text/html";
    }

}
