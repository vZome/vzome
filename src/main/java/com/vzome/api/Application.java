
//(c) Copyright 2011, Scott Vorthmann.

package com.vzome.api;

import java.io.InputStream;
import java.io.PrintWriter;
import java.net.URL;
import java.util.Properties;

import com.vzome.core.algebra.AlgebraicField;
import com.vzome.core.commands.Command;
import com.vzome.core.editor.DocumentModel;
import com.vzome.core.exporters.Exporter3d;
import com.vzome.core.math.Polyhedron;
import com.vzome.core.math.symmetry.Symmetry;
import com.vzome.core.render.Colors;
import com.vzome.core.render.Shapes;

public class Application
{
	private final com.vzome.core.editor.Application delegate;
	
	public Application()
	{
		this( new Command.FailureChannel() {
			
			public void reportFailure( Command.Failure f )
			{
				f .printStackTrace();
			}
		}, null );
	}

	public Application( Command.FailureChannel failures )
	{
	    this( failures, null );
	}

	public Application( Command.FailureChannel failures, Properties props )
	{
		// TODO cleaner abstraction to wrap FailureChannel
	    this .delegate = new com.vzome.core.editor.Application( true, failures, props );
	}

	public Document loadDocument( InputStream bytes ) throws Exception
	{
		DocumentModel docDelegate = this .delegate .loadDocument( bytes );
		docDelegate .finishLoading( false, false );
		return new Document( docDelegate );
    }
    
    public Polyhedron getBallShape()
    {
        return getBallShape( "golden", "icosahedral", "default" );
    }
    
    public Polyhedron getBallShape( String fieldName, String symmetryName, String style )
    {
		AlgebraicField field = this .delegate .getField( fieldName );
		Symmetry symm = field .getSymmetry( symmetryName );
		Shapes shapes = this .delegate .getGeometry( symm, style );
		return shapes .getConnectorShape();
	}
	
	public Colors getColors()
	{
		return this .delegate .getColors();
	}
	
	public static void main( String[] args )
	{
        String format = "pov";
        if ( args.length > 0 )
            format = args[ 0 ];
        String urlStr = "http://vzome.com/models/2007/07-Jul/affine120-bop/purpleBlueOrange-affine120cell.vZome";
        if ( args.length > 1 )
            urlStr = args[ 1 ];
		Application app = new Application();
		try {
			InputStream bytes = new URL( urlStr ) .openStream();
			Document model = app .loadDocument( bytes );
            Exporter exporter = app .getExporter( format );
            exporter .doExport( model, new PrintWriter( System.out ), 1080, 1920 );
		} catch ( Exception e ) {
			e.printStackTrace();
		}
			
	}

    public Exporter getExporter( String format )
    {
        Exporter3d privateExp = this .delegate .getExporter( format );
        return new Exporter( privateExp );
    }

	public com.vzome.core.editor.Application getDelegate()
	{
		return this .delegate;
	}

	public Document createDocument( String fieldName )
	{
		DocumentModel docDelegate = this .delegate .createDocument( fieldName );
		return new Document( docDelegate );
	}
}
