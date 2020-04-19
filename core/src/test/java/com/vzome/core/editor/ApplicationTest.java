package com.vzome.core.editor;

import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.vzome.core.model.RealizedModel;

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
    public void testImportMesh()
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
        InputStream stream = new ByteArrayInputStream( testJson.getBytes() );
        try {
            RealizedModel model = application .importMesh( stream );
            // TODO put some better assertions here
            assertNotNull( model );
        }
        catch (IOException e) {
            fail( e .getMessage() );
        }
    }

}
