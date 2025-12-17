package com.vzome.core.viewing;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.vzome.core.algebra.AlgebraicField;
import com.vzome.core.algebra.AlgebraicNumber;
import com.vzome.core.algebra.AlgebraicVector;
import com.vzome.core.algebra.PolygonField;
import com.vzome.core.math.symmetry.AntiprismSymmetry;

import defs.js.BigInt;

public class AntiprismTrackball {
    private static final Logger LOGGER = Logger.getLogger( new Throwable().getStackTrace()[0].getClassName() );
    
    public static InputStream getTrackballModelStream(InputStream bytes, AntiprismSymmetry symm) {
        final String fieldName = symm.getField().getName();
        if(! fieldName.startsWith("polygon")) {
            return bytes; // unchanged
        }
        
        // convert InputStream to String
        java.util.Scanner s = new java.util.Scanner(bytes, StandardCharsets.UTF_8.name());
        s.useDelimiter("\\A");
        String template = s.hasNext() ? s.next() : "";
        s.close();
        
        // replace fieldName in 2 places
        template = template.replace("polygon5", fieldName);
        
        // replace ImportColoredMeshJson scale
        StringBuilder buf = new StringBuilder(template.length());
        buf.append(template.substring(0, template.indexOf("scale=\"1 0\"")));
        buf.append("scale=\"");
        buf.append(symm.getField().one().toString(AlgebraicField.ZOMIC_FORMAT));
        
        buf.append(template.substring(template.indexOf("\">{"), template.indexOf("  [ [")));
        
        // replace ImportColoredMeshJson vertices
        AlgebraicVector[] vertices = getAntiprismTrackballVertices(symm);
        String delim1 = "";
        for(AlgebraicVector vertex : vertices) {
            buf.append(delim1);
            buf.append("  [");
            String delim2 = "";
            for(AlgebraicNumber num : vertex.getComponents()) {
                buf.append(delim2);
                buf.append(" [");
                String delim3 = "";
                for(BigInt i : num.toTrailingDivisorExact()) {
                    buf.append(delim3);
                    buf.append(" ");
                    buf.append(i);
                    delim3 = ",";
                }
                buf.append(" ]");
                delim2 = ",";
            }
            buf.append(" ]");
            delim1 = ", \n ";
        }
        buf.append("\n  ],\n  ");
        
        String ending = template.substring(template.indexOf("\"balls\""));
        if(symm.getField().isOdd()) {
            // The upper and lower case 'd' in the colors indicate which panels to change.
            // Adjust these panels to be white and gray for odd-gons.
            ending = ending
                    .replace("#008D36", "#FFFFFF")  // upper case D: green becomes white
                    .replace("#008d36", "#999999"); // lower case d: green becomes gray
        }
        ending = ending.replace("antiprism5", symm.getName());
        
        buf.append(ending);
        
        if(LOGGER.isLoggable(Level.FINE)) {
            LOGGER .fine( buf.toString() );
        }
//        System.out.println(buf.toString());
        
        // convert StringBuilder to InputStream
        return new ByteArrayInputStream(buf.toString().getBytes(StandardCharsets.UTF_8));
    }
    
    private static AlgebraicVector[] getAntiprismTrackballVertices(AntiprismSymmetry symm) {
        PolygonField field = symm.getField();
        
        AlgebraicNumber axisScale = field.createRational(21); // length of the X,Y and Z axis struts
        AlgebraicNumber scale = field.createRational(18); // radius of the trackball
        AlgebraicNumber n1_6 = field.createRational(1,6); // size of the corner panels
        AlgebraicNumber n5_6 = field.one().minus(n1_6);
        
        AlgebraicVector origin = field.origin(3);
        AlgebraicVector xAxis = field.basisVector(3, AlgebraicVector.X).scale(axisScale);
        AlgebraicVector yAxis = field.basisVector(3, AlgebraicVector.Y).scale(axisScale);
        AlgebraicVector zAxis = field.basisVector(3, AlgebraicVector.Z).scale(axisScale);
        
        AlgebraicVector[] orbitTriangle = symm.getOrbitTriangle();
        AlgebraicVector green = orbitTriangle[0].scale(scale);
        AlgebraicVector blue = orbitTriangle[2].scale(scale);
        AlgebraicVector posRed = orbitTriangle[1].scale(scale);
        AlgebraicVector negRed = posRed.negate();
        
        AlgebraicVector posRB = posRed.scale(n5_6).plus(blue.scale(n1_6));
        AlgebraicVector negRB = negRed.scale(n5_6).plus(blue.scale(n1_6));
        AlgebraicVector posBR = blue.scale(n5_6).plus(posRed.scale(n1_6));
        AlgebraicVector negBR = blue.scale(n5_6).plus(negRed.scale(n1_6));
        
        AlgebraicVector posRG = posRed.scale(n5_6).plus(green.scale(n1_6));
        AlgebraicVector negRG = negRed.scale(n5_6).plus(green.scale(n1_6));
        AlgebraicVector posGR = green.scale(n5_6).plus(posRed.scale(n1_6));
        AlgebraicVector negGR = green.scale(n5_6).plus(negRed.scale(n1_6));

        AlgebraicVector greenBlue = green.scale(n5_6).plus(blue.scale(n1_6));
        AlgebraicVector blueGreen = blue.scale(n5_6).plus(green.scale(n1_6));

        // the order of these vertices corresponds to the panel indices in the template file
        return new AlgebraicVector[] {
            negRed,
            posRed,
            negRG,
            posRG,
            negRB,
            posRB,
            negGR,
            posGR,
            green,
            greenBlue,
            negBR,
            posBR,
            blueGreen,
            blue,
            origin,
            xAxis,
            yAxis,
            zAxis
        };
    }

}
