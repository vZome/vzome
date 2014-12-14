package com.vzome.core.exporters;

import java.awt.Dimension;
import java.io.File;
import java.io.PrintWriter;
import java.io.Writer;

import com.vzome.core.render.Colors;
import com.vzome.core.render.RenderedModel;
import com.vzome.core.viewing.Lights;
import com.vzome.core.viewing.ViewModel;

public abstract class Exporter3d
{
    public static final double VZOME_BLUE_DIAMETER = 2.0d;
    
    //  nominal ball diameter in rZome: .700 in
    //  plastic shrinkage in rZome production: .994
    //    so actual ball diameter = .6958
    public static final double RZOME_BLUE_DIAMETER_INCHES = 0.6958d;
    
    public static final double RZOME_BLUE_DIAMETER_MM = 1768d;
    
    public static final double RZOME_INCH_SCALING = RZOME_BLUE_DIAMETER_INCHES / VZOME_BLUE_DIAMETER;
    
    public static final double RZOME_MM_SCALING = RZOME_BLUE_DIAMETER_MM / VZOME_BLUE_DIAMETER;
	
    // This scale factor corresponds to a vZome model that uses a long blue as the radius of a ball.
    //  norm squared of diameter in vZome: 1967.87  => diameter == 44.36
	static final double VZOME_STRUT_MODEL_BALL_DIAMETER = 44.36d;

	static final double VZOME_STRUT_MODEL_INCH_SCALING = RZOME_BLUE_DIAMETER_INCHES / VZOME_STRUT_MODEL_BALL_DIAMETER;

	protected transient PrintWriter output;
	
	protected final ViewModel mScene;
	protected final Colors mColors;
	protected final Lights mLights;
	protected final RenderedModel mModel;
	
	public Exporter3d( ViewModel scene, Colors colors, Lights lights, RenderedModel model )
	{
	    mScene = scene;
	    mColors = colors;
	    mLights = lights;
        mModel = model;
	}

	public abstract void doExport( File directory, Writer writer, Dimension screenSize ) throws Exception;


    public abstract String getFileExtension();


    public void doExport( File file, File parentFile, Writer out, Dimension dimension ) throws Exception
    {
        this .doExport( parentFile, out, dimension );
    }
    
}


