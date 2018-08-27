package com.vzome.core.math.quickhull;

import java.util.Set;
import java.util.TreeSet;

import org.junit.Test;

import com.vzome.core.algebra.AlgebraicField;
import com.vzome.core.algebra.AlgebraicVector;
import com.vzome.core.algebra.PentagonField;
import com.vzome.core.commands.Command.Failure;
import com.vzome.core.construction.Construction;
import com.vzome.core.construction.ConstructionChanges;
import com.vzome.core.construction.Point;
import com.vzome.core.construction.Polygon;
import com.vzome.core.construction.Segment;
import com.vzome.core.construction.VefToModel;
import com.vzome.core.generic.Utilities;
import com.vzome.core.math.convexhull.QuickHull3D;
import com.vzome.fields.sqrtphi.SqrtPhiField;

public class TestSimpleExample {
    @SuppressWarnings("serial")
    private static class NewConstructions extends TreeSet<AlgebraicVector> implements ConstructionChanges
    {
        @Override
        public void constructionAdded( Construction c )
        {
            if(c instanceof Point) {
                add( ((Point) c).getLocation() );
            } else if(c instanceof Segment) {
                Segment s = (Segment) c;
                add( s.getStart() );
                add( s.getEnd() );
            } else if(c instanceof Polygon) {
                for( AlgebraicVector v : ((Polygon) c).getVertices()) {
                    add( v );
                }
            }
        }
    }
    
    @Test
    public void testParsedVef()
    {
        String vefData = "vZome VEF 6 field golden\r\n" + 
                "\r\n" + 
                "20\r\n" + 
                "(0,0) (-3,-2) (0,0) (-1,-1)\r\n" + 
                "(0,0) (-3,-2) (0,0) (1,1)\r\n" + 
                "(0,0) (-2,-1) (-2,-1) (-2,-1)\r\n" + 
                "(0,0) (-2,-1) (-2,-1) (2,1)\r\n" + 
                "(0,0) (-2,-1) (2,1) (-2,-1)\r\n" + 
                "(0,0) (-2,-1) (2,1) (2,1)\r\n" + 
                "(0,0) (-1,-1) (-3,-2) (0,0)\r\n" + 
                "(0,0) (-1,-1) (3,2) (0,0)\r\n" + 
                "(0,0) (0,0) (-1,-1) (-3,-2)\r\n" + 
                "(0,0) (0,0) (-1,-1) (3,2)\r\n" + 
                "(0,0) (0,0) (1,1) (-3,-2)\r\n" + 
                "(0,0) (0,0) (1,1) (3,2)\r\n" + 
                "(0,0) (1,1) (-3,-2) (0,0)\r\n" + 
                "(0,0) (1,1) (3,2) (0,0)\r\n" + 
                "(0,0) (2,1) (-2,-1) (-2,-1)\r\n" + 
                "(0,0) (2,1) (-2,-1) (2,1)\r\n" + 
                "(0,0) (2,1) (2,1) (-2,-1)\r\n" + 
                "(0,0) (2,1) (2,1) (2,1)\r\n" + 
                "(0,0) (3,2) (0,0) (-1,-1)\r\n" + 
                "(0,0) (3,2) (0,0) (1,1)\r\n" + 
                "\r\n" + 
                "\r\n" + 
                "\r\n" + 
                "0\r\n" + 
                "\r\n" + 
                "\r\n" + 
                "\r\n" + 
                "0\r\n" + 
                "\r\n" + 
                "\r\n" + 
                "\r\n" + 
                "20\r\n" + 
                "0 1 2 3 4 5 6 7 8 9 \r\n" + 
                "10 11 12 13 14 15 16 17 18 19 \r\n" + 
                "\r\n" + 
                "\r\n" + 
                "";
        
        AlgebraicField field = new PentagonField();
        AlgebraicVector quaternion = null;
        NewConstructions effects = new NewConstructions();
        VefToModel parser = new VefToModel( quaternion, effects, field.one(), null );
        parser .parseVEF( vefData, field );
        
        AlgebraicVector[] points = effects.toArray(new AlgebraicVector[ effects.size() ]);
                
        QuickHull3D hull = new QuickHull3D();
//        hull.setDebug(true);
        try {
            hull.build(points);
        } catch (Failure e) {
            e.printStackTrace();
        }
      
        hullToVef(hull);
    }
    
    @Test
    public void testSimpleExample() throws Failure {
        System.out.println("starting test: " + Utilities.thisSourceCodeLine() + "\n");
        AlgebraicField field = new PentagonField();
        
        // x y z coordinates of origin and 6 points
        int scale = 10;
        AlgebraicVector[] points = new AlgebraicVector[] { 
                field.createVector(new int[][] {{ 0,scale}, { 0,scale}, { 0,scale} } ),
                field.createVector(new int[][] {{10,scale}, { 5,scale}, { 0,scale} } ),
                field.createVector(new int[][] {{20,scale}, { 0,scale}, { 0,scale} } ),
                field.createVector(new int[][] {{ 5,scale}, { 5,scale}, { 5,scale} } ),
                field.createVector(new int[][] {{ 0,scale}, { 0,scale}, {20,scale} } ),
                field.createVector(new int[][] {{ 1,scale}, { 2,scale}, { 3,scale} } ),
                field.createVector(new int[][] {{ 0,scale}, {20,scale}, { 0,scale} } ) };

        QuickHull3D hull = new QuickHull3D();
//        hull.setDebug(true);
        hull.build(points);
        
        hullToVef(hull, scale);
    }
    
    private void hullToVef(QuickHull3D hull) {
        hullToVef(hull, 1);
    }
    
    private void hullToVef(QuickHull3D hull, int scale) {
        AlgebraicVector[] vertices = hull.getVertices();
        if(vertices.length == 0) {
            return;
        }
        
        AlgebraicField field = vertices[0].getField();
        
        System.out.println("vZome VEF 6 field " + field.getName() + "\n"); // VEF header

        if(scale >= 1) {
            System.out.println("scale " + scale + "\n"); // optional
        }
        
        System.out.println(vertices.length); // vertices
        final int format = AlgebraicField.VEF_FORMAT;
        String w = field.zero().toString(format) + " ";
        for (int i = 0; i < vertices.length; i++) {
            AlgebraicVector pnt = vertices[i];
            System.out.println(w + pnt.getVectorExpression(format));
        }

        int[][] faceIndices = hull.getFaces();

        System.out.println();
        System.out.println(hull.getNumEdges()); // struts
        for (int i = 0; i < faceIndices.length; i++) {
            int b = faceIndices[i][faceIndices[i].length -1];
            AlgebraicVector beg = vertices[b];
            for (int k = 0; k < faceIndices[i].length; k++) {
                int e = faceIndices[i][k];
                AlgebraicVector end = vertices[e];
                if(b < e) {
                    System.out.println(" " + b + " " + e);
                }
                b = e;
                beg = end;
            }
        }

        System.out.println();
        System.out.println(faceIndices.length); // panels
        for (int i = 0; i < faceIndices.length; i++) {
            System.out.print(faceIndices[i].length + "  ");
            for (int k = 0; k < faceIndices[i].length; k++) {
                System.out.print(faceIndices[i][k] + " ");
            }
            System.out.println();
        }

        System.out.println("\n" + vertices.length); // balls
        for (int i = 0; i < vertices.length; i++) {
            System.out.print(i + " ");
            if(i % 10 == 9) {
                System.out.println();
            }
        }
        
        System.out.println("\n");
    }
}
