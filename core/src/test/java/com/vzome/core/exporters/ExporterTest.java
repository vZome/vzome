package com.vzome.core.exporters;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Properties;

import org.junit.Test;

import com.vzome.core.algebra.AlgebraicField;
import com.vzome.core.algebra.AlgebraicNumber;
import com.vzome.core.algebra.AlgebraicVector;
import com.vzome.core.algebra.HeptagonField;
import com.vzome.core.algebra.PentagonField;
import com.vzome.core.algebra.RootThreeField;
import com.vzome.core.algebra.RootTwoField;
import com.vzome.core.algebra.SnubDodecField;
import com.vzome.core.commands.Command;
import com.vzome.core.editor.Application;
import com.vzome.core.editor.DocumentModel;
import com.vzome.core.editor.UndoableEdit;
import com.vzome.core.model.Connector;
import com.vzome.core.model.Exporter;
import com.vzome.core.model.Manifestation;
import com.vzome.core.model.Panel;
import com.vzome.core.model.VefModelExporter;
import com.vzome.core.render.RenderedManifestation;

/**
 * @author David Hall
 */
public class ExporterTest {
    
    private static class TestApp extends Application
    {
        
        final private DocumentModel doc;
        public TestApp(String fieldName) {
            super(true, 
                    new Command.FailureChannel() {
                        @Override
                        public void reportFailure( Command.Failure f )
                        {
                            throw new RuntimeException( f .getMessage() );
                        }
                    },
                    new Properties()
            );
            doc = createDocument(fieldName);
        }
    
        public void importVefData(String vefData) {
            doc.importVEF( doc .getField() .one(), vefData );
        }

        public void select( Manifestation man ) {
            UndoableEdit edit = doc.selectManifestation (man, false);
            try {
                edit.perform();
            } catch (Command.Failure ex) {
                throw new RuntimeException(ex);
            }
        }

        public String exportModelAsVEF() {
            StringWriter out = new StringWriter();
            Exporter exporter = new VefModelExporter(out, this. doc.getField());
            for (RenderedManifestation rm : doc.getRenderedModel()) {
                exporter.exportManifestation(rm.getManifestation());
            }
            exporter.finish();
            return out.toString();
        }

        public String exportPartGeometry() {
            StringWriter out = new StringWriter();
            PartGeometryExporter exporter = (PartGeometryExporter) doc.getStructuredExporter("partgeom", null, null, null, doc.getRenderedModel() );
            try {
                exporter.doExport(null, new PrintWriter( out ), 0, 0 );
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
            return out.toString();
        }

        public String exportModelAsOFF() {
            StringWriter out = new StringWriter();
            OffExporter exporter = new OffExporter(null, null, null, doc.getRenderedModel() );
            try {
                exporter.doExport(null, new PrintWriter( out ), 0, 0 );
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
            return out.toString();
        }
    }

    private static void testImportMatchesVEFExport(String fieldName, String vefData, String expected) {
        TestApp app = new TestApp (fieldName);
        app.importVefData(vefData);
        String exportedOriginal = app.exportModelAsVEF();

        String exported = compactWhitespace(exportedOriginal);
        expected = compactWhitespace(expected);

        if( !expected.equals(exported) ) {
            int pos = 0;
            for(int i = 0; i < expected.length() && i < exported.length(); i++ ) {
                pos = i;
                if( expected.charAt(i) != exported.charAt(i) ) {
                    System.out.println("Mismatch at position " + pos + " of compacted results");
                    break;
                }
            }
            
            System.out.println(exportedOriginal);
        }
        assertEquals(expected, exported);
    }

    private static void testImportMatchesPartGeometryExport(
            String fieldName, String vefData, String expected,
            AlgebraicVector tipVector, AlgebraicVector[] middlePanelNormals)
    {
        TestApp app = new TestApp (fieldName);
        app.importVefData(vefData);

        // now select the specified ball as "tip" and panels as "middle".
        for (RenderedManifestation rm : app.doc.getRenderedModel()) {
            Manifestation man = rm.getManifestation();
            if( man instanceof Connector ) {
                AlgebraicVector location = man.getLocation();
                if( location.equals(tipVector) ) {
                    app.select(man);
                }
            }
            else if( man instanceof Panel ) {
                Panel panel = Panel.class.cast(man);
                for(AlgebraicVector normal : middlePanelNormals) {
                    if( panel.getNormal().equals(normal) ) {
                        app.select(panel);
                        break;
                    }
                }
            }
        }

        String exportedOriginal = app.exportPartGeometry();

        String exported = compactWhitespace(exportedOriginal);
        expected = compactWhitespace(expected);

        if( !expected.equals(exported) ) {
            int pos = 0;
            for(int i = 0; i < expected.length() && i < exported.length(); i++ ) {
                pos = i;
                if( expected.charAt(i) != exported.charAt(i) ) {
                    System.out.println("Mismatch at position " + pos + " of compacted results");
                    break;
                }
            }

            System.out.println(exportedOriginal);
        }
        assertEquals(expected, exported);
    }

    private static void testImportMatchesOFFExport(String fieldName, String vefData, String expected) {
        TestApp app = new TestApp (fieldName);
        app.importVefData(vefData);
        String exportedOriginal = app.exportModelAsOFF();

        String exported = compactWhitespace(exportedOriginal);
        expected = compactWhitespace(expected);

        if( !expected.equals(exported) ) {
            int pos = 0;
            for(int i = 0; i < expected.length() && i < exported.length(); i++ ) {
                pos = i;
                if( expected.charAt(i) != exported.charAt(i) ) {
                    System.out.println("Mismatch at position " + pos + " of compacted results");
                    break;
                }
            }
            
            System.out.println(exportedOriginal);
        }
        assertEquals(expected, exported);
    }

    private static String compactWhitespace(final String str) {
        return compactWhitespace(str, true);
    }

    private static String compactWhitespace(final String str, boolean strict) {
        // At a minimum, we strip the \r characters (in Windows). This may not even be necessary on the MAC
        String result = str.replace("\r", "");

        if(! strict) {
            // We'll can replace all consecutive whitespace chars with a single space to make the subsequent comparisons less strict,
            // but it makes reading the long strings in the debugger more difficult when there are no newlines.
            // Replace whitespace chars with an actual space
            result = str.replace("\n", " ").replace("\r", " ").replace("\t", " ");
            int prev = 0;
            int len = result.length();
            final String twoSpaces = "  ";
            final String oneSpace = " ";
            do {
                prev = len;
                result = result.replace(twoSpaces, oneSpace);
                len = result.length();
            } while( len != prev );
        }
        return result;
    }
    
    @Test
    public void testSortedVefExport() {
        // Just for fun, the input is a 4D permutohedron
        // with a few random struts added so they get tested too.
        String vefData =
                "vZome VEF 7 field rational\n" +
                "\n" +
                "actual scale 10\n" +
                "\n" +
                "24\n" +
                "0 1 2 3\n" +
                "0 1 3 2\n" +
                "0 2 3 1\n" +
                "1 2 3 0\n" +
                "0 2 1 3\n" +
                "0 3 1 2\n" +
                "0 3 2 1\n" +
                "1 3 2 0\n" +
                "1 2 0 3\n" +
                "1 3 0 2\n" +
                "2 3 0 1\n" +
                "2 3 1 0\n" +
                "1 0 2 3\n" +
                "1 0 3 2\n" +
                "2 0 3 1\n" +
                "2 1 3 0\n" +
                "2 0 1 3\n" +
                "3 0 1 2\n" +
                "3 0 2 1\n" +
                "3 1 2 0\n" +
                "2 1 0 3\n" +
                "3 1 0 2\n" +
                "3 2 0 1\n" +
                "3 2 1 0\n" +
                "\n" +
                "\n" +
                "\n" +
                "6\n" +
                "0 1\n" +
                "1 2\n" +
                "2 6\n" +
                "6 5\n" +
                "5 4\n" +
                "0 4\n" +
                "\n" +
                "\n" +
                "\n" +
                "14\n" +
                "6 0 1 2 6 5 4\n" +
                "6 0 4 8 20 16 12\n" +
                "6 12 16 17 18 14 13\n" +
                "6 1 13 14 15 3 2\n" +
                "6 23 19 18 17 21 22\n" +
                "6 23 11 7 3 15 19\n" +
                "6 11 10 9 5 6 7\n" +
                "6 22 21 20 8 9 10\n" +
                "4 0 12 13 1\n" +
                "4 23 22 10 11\n" +
                "4 4 5 9 8\n" +
                "4 19 15 14 18\n" +
                "4 2 3 7 6\n" +
                "4 16 20 21 17\n" +
                "\n" +
                "\n" +
                "\n" +
                "22\n" +
                "23 22 21 20 0 1 2 3 4 5 6 7 8 9 17 16 15 14 13 12 11 10 \n";

        String expected = 
                "vZome VEF 6 field golden\n"
                + "\n"
                + "25\n"
                + "(0,0) (0,0) (0,0) (0,0)\n"
                + "(0,0) (0,0) (0,10) (0,20)\n"
                + "(0,0) (0,0) (0,10) (0,30)\n"
                + "(0,0) (0,0) (0,20) (0,10)\n"
                + "(0,0) (0,0) (0,20) (0,30)\n"
                + "(0,0) (0,0) (0,30) (0,10)\n"
                + "(0,0) (0,0) (0,30) (0,20)\n"
                + "(0,0) (0,10) (0,0) (0,20)\n"
                + "(0,0) (0,10) (0,0) (0,30)\n"
                + "(0,0) (0,10) (0,20) (0,0)\n"
                + "(0,0) (0,10) (0,20) (0,30)\n"
                + "(0,0) (0,10) (0,30) (0,0)\n"
                + "(0,0) (0,10) (0,30) (0,20)\n"
                + "(0,0) (0,20) (0,0) (0,10)\n"
                + "(0,0) (0,20) (0,0) (0,30)\n"
                + "(0,0) (0,20) (0,10) (0,0)\n"
                + "(0,0) (0,20) (0,10) (0,30)\n"
                + "(0,0) (0,20) (0,30) (0,0)\n"
                + "(0,0) (0,20) (0,30) (0,10)\n"
                + "(0,0) (0,30) (0,0) (0,10)\n"
                + "(0,0) (0,30) (0,0) (0,20)\n"
                + "(0,0) (0,30) (0,10) (0,0)\n"
                + "(0,0) (0,30) (0,10) (0,20)\n"
                + "(0,0) (0,30) (0,20) (0,0)\n"
                + "(0,0) (0,30) (0,20) (0,10)\n"
                + "\n"
                + "\n"
                + "\n"
                + "6\n"
                + "10 12\n"
                + "10 16\n"
                + "12 18\n"
                + "18 24\n"
                + "22 16\n"
                + "24 22\n"
                + "\n"
                + "\n"
                + "\n"
                + "14\n"
                + "4  2 8 7 1 \n"
                + "4  9 11 5 3 \n"
                + "4  10 4 6 12 \n"
                + "4  15 13 19 21 \n"
                + "4  16 22 20 14 \n"
                + "4  18 17 23 24 \n"
                + "6  4 2 1 3 5 6 \n"
                + "6  10 12 18 24 22 16 \n"
                + "6  10 16 14 8 2 4 \n"
                + "6  12 6 5 11 17 18 \n"
                + "6  13 7 8 14 20 19 \n"
                + "6  15 9 3 1 7 13 \n"
                + "6  15 21 23 17 11 9 \n"
                + "6  21 19 20 22 24 23 \n"
                + "\n"
                + "\n"
                + "\n"
                + "23\n"
                + "0 1 2 4 5 6 7 8 10 11 \n"
                + "12 13 14 15 16 17 18 19 20 21 \n"
                + "22 23 24 \n"
                + "\n";

        String fieldName = "golden";
        testImportMatchesVEFExport(fieldName, vefData, expected);
        
        // importing pre-sorted vefData should match sorted export if we disable scaling
        vefData = expected.replace(fieldName, fieldName + " actual scale 1");
        testImportMatchesVEFExport(fieldName, vefData, expected);
    }

    //@Test
    public void testSortedPartGeometryExport() {
        // Just for fun, the input is a 4D permutohedron
        // It is not a realistic shape to use as a part geometry, but it still exercises the code correctly
        String vefData =
                "vZome VEF 7 field rational\n" +
                "\n" +
                "actual scale 10\n" +
                "\n" +
                "24\n" +
                "0 1 2 3\n" +
                "0 1 3 2\n" +
                "0 2 3 1\n" +
                "1 2 3 0\n" +
                "0 2 1 3\n" +
                "0 3 1 2\n" +
                "0 3 2 1\n" +
                "1 3 2 0\n" +
                "1 2 0 3\n" +
                "1 3 0 2\n" +
                "2 3 0 1\n" +
                "2 3 1 0\n" +
                "1 0 2 3\n" +
                "1 0 3 2\n" +
                "2 0 3 1\n" +
                "2 1 3 0\n" +
                "2 0 1 3\n" +
                "3 0 1 2\n" +
                "3 0 2 1\n" +
                "3 1 2 0\n" +
                "2 1 0 3\n" +
                "3 1 0 2\n" +
                "3 2 0 1\n" +
                "3 2 1 0\n" +
                "\n" +
                "\n" +
                "\n" +
                "0\n" +
                "\n" +
                "\n" +
                "\n" +
                "14\n" +
                "6 0 1 2 6 5 4\n" +
                "6 0 4 8 20 16 12\n" +
                "6 12 16 17 18 14 13\n" +
                "6 1 13 14 15 3 2\n" +
                "6 23 19 18 17 21 22\n" +
                "6 23 11 7 3 15 19\n" +
                "6 11 10 9 5 6 7\n" +
                "6 22 21 20 8 9 10\n" +
                "4 0 12 13 1\n" +
                "4 23 22 10 11\n" +
                "4 4 5 9 8\n" +
                "4 19 15 14 18\n" +
                "4 2 3 7 6\n" +
                "4 16 20 21 17\n" +
                "\n" +
                "\n" +
                "\n" +
                "22\n" +
                "23 22 21 20 0 1 2 3 4 5 6 7 8 9 17 16 15 14 13 12 11 10 \n";

        String expected =
                "vZome VEF 6 field golden\n"
                + "\n"
                + "scale 1\n"
                + "25\n"
                + "(0,0) (0,0) (0,0) (0,0)\n"
                + "(0,0) (0,0) (0,10) (0,20)\n"
                + "(0,0) (0,0) (0,10) (0,30)\n"
                + "(0,0) (0,0) (0,20) (0,10)\n"
                + "(0,0) (0,0) (0,20) (0,30)\n"
                + "(0,0) (0,0) (0,30) (0,10)\n"
                + "(0,0) (0,0) (0,30) (0,20)\n"
                + "(0,0) (0,10) (0,0) (0,20)\n"
                + "(0,0) (0,10) (0,0) (0,30)\n"
                + "(0,0) (0,10) (0,20) (0,0)\n"
                + "(0,0) (0,10) (0,20) (0,30)\n"
                + "(0,0) (0,10) (0,30) (0,0)\n"
                + "(0,0) (0,10) (0,30) (0,20)\n"
                + "(0,0) (0,20) (0,0) (0,10)\n"
                + "(0,0) (0,20) (0,0) (0,30)\n"
                + "(0,0) (0,20) (0,10) (0,0)\n"
                + "(0,0) (0,20) (0,10) (0,30)\n"
                + "(0,0) (0,20) (0,30) (0,0)\n"
                + "(0,0) (0,20) (0,30) (0,10)\n"
                + "(0,0) (0,30) (0,0) (0,10)\n"
                + "(0,0) (0,30) (0,0) (0,20)\n"
                + "(0,0) (0,30) (0,10) (0,0)\n"
                + "(0,0) (0,30) (0,10) (0,20)\n"
                + "(0,0) (0,30) (0,20) (0,0)\n"
                + "(0,0) (0,30) (0,20) (0,10)\n"
                + "\n"
                + "\n"
                + "\n"
                + "0\n"
                + "\n"
                + "\n"
                + "\n"
                + "14\n"
                + "4  2 8 7 1 \n"
                + "4  9 11 5 3 \n"
                + "4  10 4 6 12 \n"
                + "4  15 13 19 21 \n"
                + "4  16 22 20 14 \n"
                + "4  18 17 23 24 \n"
                + "6  4 2 1 3 5 6 \n"
                + "6  10 12 18 24 22 16 \n"
                + "6  10 16 14 8 2 4 \n"
                + "6  12 6 5 11 17 18 \n"
                + "6  13 7 8 14 20 19 \n"
                + "6  15 9 3 1 7 13 \n"
                + "6  15 21 23 17 11 9 \n"
                + "6  21 19 20 22 24 23 \n"
                + "\n"
                + "\n"
                + "\n"
                + "23\n"
                + "0 1 2 4 5 6 7 8 10 11 \n"
                + "12 13 14 15 16 17 18 19 20 21 \n"
                + "22 23 24 \n"
                + "\n"
                + "\n"
                + "tip 24\n"
                + "\n"
                + "middle\n" +
                "10 4 6 12 \n" +
                "16 22 20 14 \n" +
                "18 17 23 24 \n" +
                "\n";

        AlgebraicField field = new PentagonField();
        AlgebraicVector tipVector = new AlgebraicVector(
                new AlgebraicNumber[]{
                    field.createRational(30),
                    field.createRational(20),
                    field.createRational(10),});

        AlgebraicVector normal0 = new AlgebraicVector(
                new AlgebraicNumber[]{
                    field.createRational(0),
                    field.createRational(-100),
                    field.createRational(-100),}
        );

        AlgebraicVector normal1 = new AlgebraicVector(
                new AlgebraicNumber[]{
                    field.createRational(-100),
                    field.createRational(0),
                    field.createRational(-100),}
        );
        AlgebraicVector normal2 = new AlgebraicVector(
                new AlgebraicNumber[]{
                    field.createRational(-100),
                    field.createRational(-100),
                    field.createRational(0),}
        );

        AlgebraicVector[] panelNormals = new AlgebraicVector[] { normal0, normal1, normal2 };

        testImportMatchesPartGeometryExport(field.getName(), vefData, expected, tipVector, panelNormals);
    }

    @Test
    public void testSortedOffExport() {
        // Just for fun, the input is a 4D permutohedron
        // with a few random struts added so they get tested too
        // and all scaled so that the OFF output contains decimals instead of just integers
        String vefData
                = "vZome VEF 7 field rational\n"
                + "\n"
                + "actual scale 5/3\n"
                + "\n"
                + "24\n"
                + "0 1 2 3\n"
                + "0 1 3 2\n"
                + "0 2 3 1\n"
                + "1 2 3 0\n"
                + "0 2 1 3\n"
                + "0 3 1 2\n"
                + "0 3 2 1\n"
                + "1 3 2 0\n"
                + "1 2 0 3\n"
                + "1 3 0 2\n"
                + "2 3 0 1\n"
                + "2 3 1 0\n"
                + "1 0 2 3\n"
                + "1 0 3 2\n"
                + "2 0 3 1\n"
                + "2 1 3 0\n"
                + "2 0 1 3\n"
                + "3 0 1 2\n"
                + "3 0 2 1\n"
                + "3 1 2 0\n"
                + "2 1 0 3\n"
                + "3 1 0 2\n"
                + "3 2 0 1\n"
                + "3 2 1 0\n"
                + "\n"
                + "\n"
                + "\n"
                + "6\n"
                + "0 1\n"
                + "1 2\n"
                + "2 6\n"
                + "6 5\n"
                + "5 4\n"
                + "0 4\n"
                + "\n"
                + "\n"
                + "\n"
                + "14\n"
                + "6 0 1 2 6 5 4\n"
                + "6 0 4 8 20 16 12\n"
                + "6 12 16 17 18 14 13\n"
                + "6 1 13 14 15 3 2\n"
                + "6 23 19 18 17 21 22\n"
                + "6 23 11 7 3 15 19\n"
                + "6 11 10 9 5 6 7\n"
                + "6 22 21 20 8 9 10\n"
                + "4 0 12 13 1\n"
                + "4 23 22 10 11\n"
                + "4 4 5 9 8\n"
                + "4 19 15 14 18\n"
                + "4 2 3 7 6\n"
                + "4 16 20 21 17\n"
                + "\n"
                + "\n"
                + "\n"
                + "22\n"
                + "23 22 21 20 0 1 2 3 4 5 6 7 8 9 17 16 15 14 13 12 11 10 \n";

        String expected
                = "OFF\n"
                + "# numVertices numFaces numEdges (numEdges is ignored)\n"
                + "25 14 6\n"
                + "\n"
                + "# Vertices.  Each line is the XYZ coordinates of one vertex.\n"
                + "0 0 0\n"
                + "0 1.6666666666666667 3.3333333333333335\n"
                + "0 1.6666666666666667 5\n"
                + "0 3.3333333333333335 1.6666666666666667\n"
                + "0 3.3333333333333335 5\n"
                + "0 5 1.6666666666666667\n"
                + "0 5 3.3333333333333335\n"
                + "1.6666666666666667 0 3.3333333333333335\n"
                + "1.6666666666666667 0 5\n"
                + "1.6666666666666667 3.3333333333333335 0\n"
                + "1.6666666666666667 3.3333333333333335 5\n"
                + "1.6666666666666667 5 0\n"
                + "1.6666666666666667 5 3.3333333333333335\n"
                + "3.3333333333333335 0 1.6666666666666667\n"
                + "3.3333333333333335 0 5\n"
                + "3.3333333333333335 1.6666666666666667 0\n"
                + "3.3333333333333335 1.6666666666666667 5\n"
                + "3.3333333333333335 5 0\n"
                + "3.3333333333333335 5 1.6666666666666667\n"
                + "5 0 1.6666666666666667\n"
                + "5 0 3.3333333333333335\n"
                + "5 1.6666666666666667 0\n"
                + "5 1.6666666666666667 3.3333333333333335\n"
                + "5 3.3333333333333335 0\n"
                + "5 3.3333333333333335 1.6666666666666667\n"
                + "\n"
                + "# Faces.  numCorners vertexIndex[0] ... vertexIndex[numCorners-1]\n"
                + "4 2 8 7 1\n"
                + "4 9 11 5 3\n"
                + "4 10 4 6 12\n"
                + "4 15 13 19 21\n"
                + "4 16 22 20 14\n"
                + "4 18 17 23 24\n"
                + "6 4 2 1 3 5 6\n"
                + "6 10 12 18 24 22 16\n"
                + "6 10 16 14 8 2 4\n"
                + "6 12 6 5 11 17 18\n"
                + "6 13 7 8 14 20 19\n"
                + "6 15 9 3 1 7 13\n"
                + "6 15 21 23 17 11 9\n"
                + "6 21 19 20 22 24 23\n";

        String fieldName = "golden";
        testImportMatchesOFFExport(fieldName, vefData, expected);
    }

    @Test
    public void testAppendVector() {
        System.out.println("appendVector");
        final int ones = 7, irrat = 3, denom = 5;

        final AlgebraicField[] fields = {
        	new PentagonField(),
            new RootTwoField(),
            new RootThreeField(),
            new HeptagonField(),
            new SnubDodecField(),
        };
        final String[] expectations = new String[]{
            "(0,0) (3/5,7/5) (0,0) (0,0)",      // 1D: 0 X 0 0
            "(0,0) (3/5,7/5) (3/5,7/5) (0,0)",  // 2D: 0 X Y 0
            "(0,0) (3/5,7/5) (3/5,7/5) (0,1)",  // 3D: 0 X Y Z
            "(3/5,7/5) (3/5,7/5) (0,1) (0,2)",  // 4D: W X Y Z
        };

        int fieldsPassed = 0;
        for(AlgebraicField field : fields ) {
            System.out.println( field.getName() + ": order " + field.getOrder());
            String zeroPad = field.zero().toString(AlgebraicField.VEF_FORMAT);
            zeroPad = zeroPad.replace("0,0)", "");

            final AlgebraicNumber[] dimensions = new AlgebraicNumber[] {
                field.createAlgebraicNumber(ones, irrat, denom, 0),
                field.createRational(ones, denom).plus( field.createPower(1).times( field.createRational(irrat, denom) ) ),
                field.one(),
                field.one().plus(field.one()),
            };

            assertEquals(dimensions[0], dimensions[1]); // not necessary

            int testsPassed = 0;
            for( int d = 1; d <= dimensions.length; d++ ) {
                System.out.print( "  using a " + d + "D vector: ");
                StringBuffer buffer = new StringBuffer();

                AlgebraicNumber[] dims = new AlgebraicNumber[d];
                System.arraycopy(dimensions, 0, dims, 0, d);
                AlgebraicVector vector = new AlgebraicVector( dims );

                // Here's what we're actually testing!
                VefModelExporter.appendVector( buffer, vector );

                String expected = expectations[d - 1];
                expected = expected.replace("(", zeroPad);

                System.out.println(" " + buffer.toString());
                assertEquals(expected, buffer.toString());
                testsPassed ++;
            }

            assertEquals( dimensions.length, testsPassed );
            assertTrue( testsPassed >= 4 ); // complete test requires testing 1D to 4D per field
            fieldsPassed ++;
        }
        assertEquals(fields.length, fieldsPassed);
        assertTrue(fieldsPassed == 5); // complete test requires testing all fields
    }

}
