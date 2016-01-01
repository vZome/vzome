package org.vorthmann.zome.app.impl;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.net.URI;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.vorthmann.j3d.Platform;
import org.vorthmann.ui.Controller;
import org.vorthmann.ui.DefaultController;
import org.vorthmann.zome.ui.ApplicationUI;

import com.vzome.core.algebra.AlgebraicField;
import com.vzome.core.commands.Command.Failure;
import com.vzome.core.commands.Command.FailureChannel;
import com.vzome.core.editor.DocumentModel;
import com.vzome.core.exporters.Exporter3d;
import com.vzome.core.math.symmetry.Symmetry;
import com.vzome.core.model.Connector;
import com.vzome.core.model.Strut;
import com.vzome.core.render.Colors;
import com.vzome.core.render.RenderedManifestation;
import com.vzome.core.render.RenderedModel;
import com.vzome.core.viewing.Lights;
import com.vzome.desktop.controller.RenderingViewer;

public class ApplicationController extends DefaultController
{
	private final Map<String, DocumentController> docControllers = new HashMap<>();
	
	private final ActionListener ui;
    
    private final Properties userPreferences = new Properties();
    
    private final Properties properties = new Properties();
    
    private final Map<Symmetry, RenderedModel> mSymmetryModels = new HashMap<>();

    private RenderingViewer.Factory rvFactory;

    private final com.vzome.core.editor.Application modelApp;

	private final File prefsFile;

    private int lastUntitled = 0;

    
	public ApplicationController( ActionListener ui, Properties commandLineArgs )
    {
		super();
		
        long starttime = System.currentTimeMillis();

        if ( logger .isLoggable( Level .INFO ) )
            logger .info( "ApplicationController .initialize() starting" );

		this.ui = ui;
        
        File prefsFolder = Platform .getPreferencesFolder();        
        File prefsFile = new File( prefsFolder, "vZome.preferences" );
        if ( ! prefsFile .exists() ) {
            prefsFolder = new File( System.getProperty( "user.home" ) );
            prefsFile = new File( prefsFolder, "vZome.preferences" );
        }
        if ( ! prefsFile .exists() ) {
            prefsFile = new File( prefsFolder, ".vZome.prefs" );
        }
        this.prefsFile = prefsFile;
        if ( ! prefsFile .exists() ) {
        	logger .config( "Used default preferences." );
        } else {
            try {
                InputStream in = new FileInputStream( prefsFile );
                userPreferences .load( in );
    			logger .config( "User Preferences loaded from " + prefsFile .getAbsolutePath() );
            } catch ( Throwable t ) {
    			logger .severe( "problem reading user preferences: " + t.getMessage() );
            }
        }
        
		Properties defaults = new Properties();
        String defaultRsrc = "org/vorthmann/zome/app/defaultPrefs.properties";
        try {
            ClassLoader cl = ApplicationUI.class.getClassLoader();
            InputStream in = cl.getResourceAsStream( defaultRsrc );
            if ( in != null )
            	defaults .load( in ); // override the core defaults
        } catch ( IOException ioe ) {
        	logger.severe( "problem reading default preferences: " + defaultRsrc );
        }

        // last-wins, so getProperty() will show command-line args overriding user prefs, which override built-in defaults
        properties .putAll( defaults );
        properties .putAll( userPreferences );
        properties .putAll( commandLineArgs );

        final FailureChannel failures = new FailureChannel()
        {	
			@Override
			public void reportFailure( Failure f )
			{
	            mErrors.reportError( USER_ERROR_CODE, new Object[] { f } );
			}
		};
        modelApp = new com.vzome.core.editor.Application( true, failures, properties );
        
        Colors colors = modelApp .getColors();
        
        {
        	boolean useEmissiveColor = ! propertyIsTrue( "no.glowing.selection" );
            // need this set up before we do any loadModel
            String factoryName = getProperty( "RenderingViewer.Factory.class" );
            if ( factoryName == null )
                factoryName = "org.vorthmann.zome.render.java3d.Java3dFactory";
            try {
                Class<?> factoryClass = Class.forName( factoryName );
                Constructor<?> constructor = factoryClass .getConstructor( new Class<?>[] { Colors.class, Boolean.class } );
                rvFactory = (RenderingViewer.Factory) constructor.newInstance( new Object[] { colors, useEmissiveColor } );
            } catch ( Exception e ) {
                mErrors.reportError( "Unable to instantiate RenderingViewer.Factory class: " + factoryName, new Object[] {} );
                System.exit( 0 );
            }
        }

        RenderedModel model;
        
        AlgebraicField field = modelApp .getField( "golden" );
        Symmetry symmetry = field .getSymmetry( "icosahedral" );
        {
            if ( propertyIsTrue( "rzome.trackball" ) )
                model = loadModelPanels( "org/vorthmann/zome/app/rZomeTrackball-vef.vZome" );
            else
                model = loadModelPanels( "org/vorthmann/zome/app/icosahedral-vef.vZome" );
            mSymmetryModels.put( symmetry, model );
        }
        symmetry = field .getSymmetry( "octahedral" );
        {
            // this has to happen after the OctahedralSymmetry constructor, which registers with the field
            model = loadModelPanels( "org/vorthmann/zome/app/octahedral-vef.vZome" );
            mSymmetryModels.put( symmetry, model );
        }

        field = modelApp .getField( "snubDodec" );
        symmetry = field .getSymmetry( "icosahedral" );
        {
            model = loadModelPanels( "org/vorthmann/zome/app/icosahedral-vef.vZome" );
            mSymmetryModels.put( symmetry, model );
        }

        field = modelApp .getField( "rootTwo" );
        symmetry = field .getSymmetry( "octahedral" );
        {
            model = loadModelPanels( "org/vorthmann/zome/app/octahedral-vef.vZome" );
            mSymmetryModels.put( symmetry, model );
            symmetry = field .getSymmetry( "synestructics" );
            mSymmetryModels.put( symmetry, model );
        }

        field = modelApp .getField( "rootThree" );
        symmetry = field .getSymmetry( "octahedral" );
        {
            // yes, reusing the model from above
            mSymmetryModels.put( symmetry, model );
        }
        symmetry = field .getSymmetry( "dodecagonal" );
        {
            model = loadModelPanels( "org/vorthmann/zome/app/dodecagonal.vZome" );
            mSymmetryModels.put( symmetry, model );
        }

        // addStyle( new ModeledShapes( "pentagonal", "pentagonal prismatic", DecagonSymmetry.INSTANCE ) );

        if ( propertyIsTrue( "enable.heptagon.field" ) )
        {
            field = modelApp .getField( "heptagon" );
            symmetry = field .getSymmetry( "octahedral" );
            mSymmetryModels.put( symmetry, model );
        }

        long endtime = System.currentTimeMillis();
        if ( logger .isLoggable( Level .INFO ) )
            logger .log(Level.INFO, "ApplicationController initialization in milliseconds: {0}", ( endtime - starttime ));
   	}
	
	@Override
	public void doAction( String action, ActionEvent event )
	{
		try {
			switch ( action ) {

			case "showAbout":
			case "openURL":
			case "quit":
	        	this .ui .actionPerformed( event );
				break;

			case "launch":
	            String sawWelcome = userPreferences .getProperty( "saw.welcome" );
	            if ( sawWelcome == null )
	            {
	                String welcome = properties .getProperty( "welcome" );
	                doAction( "openResource-" + welcome, null );
	                userPreferences .setProperty( "saw.welcome", "true" );
	                FileWriter writer;
	                try {
	                    writer = new FileWriter( prefsFile );
	                    userPreferences .store( writer, "" );
	                    writer .close();
	                } catch ( IOException e ) {
	                    // TODO Auto-generated catch block
	                    e.printStackTrace();
	                }
	                break;
	            }
				// else no break! fall through to "new"

			case "new":
				action = "new-golden";
				// no break! fall through to default

			default:
		        if ( action .startsWith( "new-" ) )
		        {
		        	String fieldName = action .substring( "new-" .length() );
		            File prototype = new File( Platform .getPreferencesFolder(), "Prototypes/" + fieldName + ".vZome" );
		            if ( prototype .exists() ) {
//		            	this .mController .doAction(action, e);
//		                doFileAction( "newFromTemplate", prototype );
		            }
		            else
		            {
		        		// creating a new Document
		        		Properties docProps = new Properties();
		        		docProps .setProperty( "new.document", "true" );
		        		DocumentModel document = modelApp .createDocument( fieldName );
		        		String title = "Untitled " + ++lastUntitled;
		        		docProps .setProperty( "window.title", title );
		        		DocumentController newest = new DocumentController( document, this, docProps );
		        		newDocumentController( title, newest );
		            }
		        }
		        else if ( action .startsWith( "openResource-" ) )
		        {
	        		Properties docProps = new Properties();
	    			docProps .setProperty( "reader.preview", "true" );
		            String path = action .substring( "openResource-" .length() );
	        		docProps .setProperty( "window.title", path );
	                ClassLoader cl = Thread .currentThread() .getContextClassLoader();
	                InputStream bytes = cl .getResourceAsStream( path );
	                loadDocumentController( path, bytes, docProps );
		        }
		        else if ( action .startsWith( "openURL-" ) )
		        {
	        		Properties docProps = new Properties();
	    			docProps .setProperty( "as.template", "true" );
		            String path = action .substring( "openURL-" .length() );
	        		docProps .setProperty( "window.title", path );
					if ( path .toLowerCase() .endsWith( ".vzome" ) ) {
		            	URI uri = new URI( path );
						URL url = uri .toURL();
			            InputStream bytes = url .openStream();
		                loadDocumentController( path, bytes, docProps );
					}
		        }
		        else
		        	this .mErrors .reportError( UNKNOWN_ACTION, new Object[]{ action } );
			}
		} catch ( Exception e ) {
        	this .mErrors .reportError( UNKNOWN_ERROR_CODE, new Object[]{ e } );
		}
	}

	@Override
    public void doFileAction( String command, File file )
    {
        if ( file != null )
        {
    		Properties docProps = new Properties();
            String path = file .getAbsolutePath();
			docProps .setProperty( "window.title", path );
        	switch ( command ) {

        	case "open":
        		docProps .setProperty( "window.file", path );
				break;

        	case "newFromTemplate":
        		String title = "Untitled " + ++lastUntitled;
        		docProps .setProperty( "window.title", title ); // override the default above
        		docProps .setProperty( "as.template", "true" ); // don't set window.file!
				break;

        	case "openDeferringRedo":
        		docProps .setProperty( "open.undone", "true" );
        		docProps .setProperty( "window.file", path );
				break;

			default:
	        	this .mErrors .reportError( UNKNOWN_ACTION, new Object[]{ command } );
				return;
			}
            try {
                InputStream bytes = new FileInputStream( file );
                loadDocumentController( path, bytes, docProps );
			} catch ( Exception e ) {
	        	this .mErrors .reportError( UNKNOWN_ERROR_CODE, new Object[]{ e } );
			}
        }
    }
	
	private void loadDocumentController( final String name, final InputStream bytes, final Properties properties ) throws Exception
	{
		DocumentModel document = modelApp .loadDocument( bytes );
		DocumentController newest = new DocumentController( document, ApplicationController.this, properties );
		newDocumentController( name, newest );
	}

    RenderingViewer.Factory getJ3dFactory()
    {
        return rvFactory;
    }
    
	@Override
    public boolean userHasEntitlement( String propName )
    {
		switch ( propName ) {

		case "save.files":
            return getProperty( "licensed.user" ) != null;

		case "all.tools":
            return propertyIsTrue( "entitlement.all.tools" );

		case "developer.extras":
            return getProperty( "vZomeDeveloper" ) != null;

		default:
	        // TODO make this work more like developer.extras
	        return propertyIsTrue( "entitlement." + propName );
	        // this IS the backstop controller, so no purpose in calling super
		}
    }

	@Override
    public String getProperty( String string )
    {
		switch ( string ) {
		
		case "formatIsSupported":
            return "true";

		case "untitled.title":
            return "Untitled " + ++lastUntitled;

		default:
	        return properties .getProperty( string );
		}
    }

    public Controller getSubController( final String name )
    {
    	return docControllers .get( name );
    }

    private void newDocumentController( final String name, final DocumentController newest )
    {
        this .docControllers .put( name, newest );
        newest .addPropertyListener( new PropertyChangeListener()
        {
			@Override
			public void propertyChange( PropertyChangeEvent evt )
			{
				switch ( evt .getPropertyName() ) {

				case "name":
			        docControllers .remove( name );
			        docControllers .put( (String) evt .getNewValue(), newest );
					break;

				case "visible":
					if ( Boolean.FALSE .equals( evt .getNewValue() ) ) {
						docControllers .remove( name );
						if ( docControllers .isEmpty() )
							// closed the last window, so we're exiting
							System .exit( 0 );
					}
					break;

				default:
					break;
				}
			}
		});
        // trigger window creation in the UI
		this .properties() .firePropertyChange( "newDocument", null, newest );
    }

    private RenderedModel loadModelPanels( String path )
    {
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        InputStream bytes = cl.getResourceAsStream( path );

        try {
    		DocumentModel document = this .modelApp .loadDocument( bytes );
    		// a RenderedModel that only creates panels
    		document .setRenderedModel( new RenderedModel( document .getField(), true ) 	 	 
    		{
				@Override
    			protected void resetAttributes( RenderedManifestation rm, 	 	 
    					boolean justShape, Strut strut ) {} 	 	 

				@Override
    			protected void resetAttributes(RenderedManifestation rm, 	 	 
    					boolean justShape, Connector m) {} 	 	 
    		} .withColorPanels( false ) ); 
    		document .finishLoading( false, false );
            return document .getRenderedModel();
        } catch ( Exception e ) {
            throw new RuntimeException( e );
        }
    }

    public Colors getColors()
    {
        return this .modelApp .getColors();
    }

    public Exporter3d getExporter( String format )
    {
        return this .modelApp .getExporter( format );
    }
    
    // public RenderingViewer.Factory getRenderingViewerFactory()
    // {
    // return mRVFactory;
    // }

    public RenderedModel getSymmetryModel( Symmetry symm )
    {
        return (RenderedModel) mSymmetryModels.get( symm );
    }
    
	@Override
    public String[] getCommandList( String listName )
    {
        if ( listName.startsWith( "fields." ) ) {
            Set<?> names = (Set<?>)modelApp.getFieldNames();
            return (String[]) names.toArray( );
        } else if ( listName.startsWith( "symmetries." ) ) {
            String fieldName = listName.substring( 11 );
            AlgebraicField field = modelApp .getField( fieldName );
            Symmetry[] symms = field.getSymmetries();
            String[] result = new String[symms.length];
            for ( int i = 0; i < symms.length; i++ )
                result[i] = symms[i].getName();
            return result;
        }
        return new String[0];
    }

    public Lights getLights()
    {
        return modelApp .getLights();
    }

    static final Logger logger = Logger.getLogger( "org.vorthmann.zome.controller" );
}
