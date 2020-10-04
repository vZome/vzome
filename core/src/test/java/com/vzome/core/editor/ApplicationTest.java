package com.vzome.core.editor;

import static org.junit.Assert.fail;

import java.io.IOException;
import java.util.Properties;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.vzome.core.algebra.AlgebraicField;
import com.vzome.core.construction.Color;
import com.vzome.core.construction.Construction;
import com.vzome.core.math.Projection;
import com.vzome.core.model.ColoredMeshJson;

public class ApplicationTest {

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
    }

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testColoredMeshJson()
    
    {
        String testJson = "{\n" + 
                "  \"field\" : \"golden\",\n" + 
                "  \"symmetry\" : \"icosahedral\",\n" + 
                "  \"vertices\" : [\n" + 
                "    [ [ 5, 7 ], [ 2, 5 ], [ 0, 0 ] ], [ [ 7, 6 ], [ -2, 6 ], [ 0, -1 ] ], [ [ 7, 6 ], [ -2, 6 ], [ 0, 1 ] ],\n" + 
                "    [ [ 4, 8 ], [ -2, 6 ], [ 0, 0 ] ], [ [ 3, 9 ], [ 4, -1 ], [ -2, -2 ] ], [ [ 3, 9 ], [ 4, -1 ], [ 2, 2 ] ],\n" + 
                "    [ [ 5, 8 ], [ 0, 0 ], [ -2, -3 ] ], [ [ 5, 8 ], [ 0, 0 ], [ 2, 3 ] ], [ [ 2, 10 ], [ 0, 0 ], [ -2, -2 ] ],\n" + 
                "    [ [ 2, 10 ], [ 0, 0 ], [ 2, 2 ] ], [ [ 7, 7 ], [ 4, -1 ], [ 0, 0 ] ], [ [ 9, 6 ], [ 0, 0 ], [ 0, -1 ] ],\n" + 
                "    [ [ 9, 6 ], [ 0, 0 ], [ 0, 1 ] ], [ [ 6, 8 ], [ 0, 0 ], [ 0, 0 ] ] ],\n" + 
                "  \"balls\" : [ ],\n" + 
                "  \"struts\" : [ ],\n" + 
                "  \"panels\" : [ {\n" + 
                "    \"vertices\" : [ 3, 2, 5, 9, 12, 10 ],\n" + 
                "    \"color\" : \"#FFFFFF\"\n" + 
                "  }, {\n" + 
                "    \"vertices\" : [ 4, 8, 6 ],\n" + 
                "    \"color\" : \"#FFCC00\"\n" + 
                "  }, {\n" + 
                "    \"vertices\" : [ 2, 0, 3 ],\n" + 
                "    \"color\" : \"#CC0000\"\n" + 
                "  }, {\n" + 
                "    \"vertices\" : [ 5, 9, 7 ],\n" + 
                "    \"color\" : \"#FFCC00\"\n" + 
                "  }, {\n" + 
                "    \"vertices\" : [ 10, 11, 13 ],\n" + 
                "    \"color\" : \"#0099CC\"\n" + 
                "  }, {\n" + 
                "    \"vertices\" : [ 10, 12, 13 ],\n" + 
                "    \"color\" : \"#0099CC\"\n" + 
                "  }, {\n" + 
                "    \"vertices\" : [ 1, 0, 3 ],\n" + 
                "    \"color\" : \"#CC0000\"\n" + 
                "  }, {\n" + 
                "    \"vertices\" : [ 3, 1, 4, 8, 11, 10 ],\n" + 
                "    \"color\" : \"#666666\"\n" + 
                "  } ]\n" + 
                "}";
        Application application = new Application( false, null, new Properties() );
        ColoredMeshJson.Events events = new ColoredMeshJson.Events()
        {    
            @Override
            public void constructionAdded( Construction c, Color color )
            {
                // TODO Auto-generated method stub
            }
        };
        try {
            AlgebraicField field = application .getField( "golden" );
            ColoredMeshJson .parse( testJson, null, new Projection.Default( field ), events, application );
            // TODO put some better assertions here
        }
        catch (IOException e) {
            fail( e .getMessage() );
        }
    }

}
