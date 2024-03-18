
package com.vzome.api;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Properties;

import com.vzome.core.commands.Command;
import com.vzome.core.editor.DocumentModel;
import com.vzome.core.editor.FieldApplication;
import com.vzome.core.editor.SymmetryPerspective;
import com.vzome.core.editor.api.Shapes;
import com.vzome.core.exporters.GeometryExporter;
import com.vzome.core.math.Polyhedron;
import com.vzome.core.render.Colors;

public class Application
{
	private final com.vzome.core.editor.Application delegate;
	
	public Application()
	{
		this( new Command.FailureChannel() {
			
            @Override
			public void reportFailure( Command.Failure f )
			{
				f .printStackTrace();
			}
		} );
	}

	public Application( Command.FailureChannel failures )
	{
	    this( failures, new Properties() );
	}

	public Application( Command.FailureChannel failures, Properties props )
	{
	    props .setProperty( "no.line.numbers", "true" );
		// TODO cleaner abstraction to wrap FailureChannel
	    this .delegate = new com.vzome.core.editor.Application( true, failures, props );
	}
    
    public Document loadFile( String path ) throws Exception
    {
        File file = new File( path );
        InputStream bytes = new FileInputStream( file );
        return loadDocument( bytes, false );
    }
    
    public Document loadUrl( String path ) throws Exception
    {
        URL url = new URL( path );
        InputStream bytes= null;
        HttpURLConnection conn = (HttpURLConnection) url .openConnection();
        // See https://stackoverflow.com/questions/1884230/urlconnection-doesnt-follow-redirect
        //  This won't switch protocols, but seems to work otherwise.
        conn .setInstanceFollowRedirects( true );
        bytes = conn .getInputStream();
        return loadDocument( bytes, false );
    }

    public Document loadDocument( InputStream bytes ) throws Exception
    {
        return this .loadDocument( bytes, true );
    }

    public Document loadDocument( InputStream bytes, boolean finish ) throws Exception
    {
		DocumentModel docDelegate = this .delegate .loadDocument( bytes );
		if ( finish )
		    docDelegate .finishLoading( false, false );
		return new Document( docDelegate );
    }
    
    public Polyhedron getBallShape()
    {
        return getBallShape( "golden", "icosahedral", "default" );
    }
    
    public Polyhedron getBallShape( String fieldName, String symmetryName, String style )
    {
		FieldApplication fieldApp = this .delegate .getDocumentKind( fieldName );
		SymmetryPerspective symmPer = fieldApp .getSymmetryPerspective( symmetryName );
		for ( Shapes shapes : symmPer .getGeometries() ) {
			if ( shapes .getName() .equals( style ) )
				return shapes .getConnectorShape();
		}
		return null;
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
        String urlStr = "http://vzome.com/models/2007/04-Apr/5cell/A4_9.vZome";
        if ( args.length > 1 )
            urlStr = args[ 1 ];
		Application app = new Application();
		try {
		    Document model = app .loadUrl( urlStr );
		    model .getDocumentModel() .finishLoading( false, false );
            Exporter exporter = app .getExporter( format );
            exporter .doExport( model, new PrintWriter( System.out ), 1080, 1920 );
		} catch ( Exception e ) {
			e.printStackTrace();
		}
			
	}

    public Exporter getExporter( String format )
    {
        GeometryExporter privateExp = this .delegate .getExporter( format );
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
