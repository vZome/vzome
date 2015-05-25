package org.vorthmann.zome.app.impl;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.vecmath.Vector3f;

import org.vorthmann.ui.ApplicationController;
import org.vorthmann.ui.Controller;
import org.vorthmann.ui.DefaultController;

import com.vzome.core.algebra.AlgebraicField;
import com.vzome.core.commands.Command.Failure;
import com.vzome.core.commands.Command.FailureChannel;
import com.vzome.core.editor.Application;
import com.vzome.core.editor.DocumentModel;
import com.vzome.core.exporters.Exporter3d;
import com.vzome.core.math.symmetry.Symmetry;
import com.vzome.core.model.Connector;
import com.vzome.core.model.Strut;
import com.vzome.core.render.Color;
import com.vzome.core.render.Colors;
import com.vzome.core.render.RenderedManifestation;
import com.vzome.core.render.RenderedModel;
import com.vzome.core.viewing.Lights;
import com.vzome.desktop.controller.RenderingViewer;

public class DefaultApplication extends DefaultController implements ApplicationController
{
	public DefaultApplication()
    {
		super();
		this .properties = Application .loadDefaults();
	}

	public void doScriptAction( String command, String script )
    {
		this .topEditor .doScriptAction( command, script );
	}

    RenderingViewer.Factory getJ3dFactory()
    {
        return rvFactory;
    }

    public Controller loadController( InputStream bytes, Properties props )
    {
        if ( bytes == null )
    	{
    	    String fieldName = props .getProperty( "field" );
    	    if ( fieldName == null )
    	        fieldName = "golden";
            DocumentModel document = modelApp .createDocument( fieldName );
            this .topEditor = new EditorController( document, this, props, true );
            return this .topEditor;
    	}
        else
        {
        	String fileName = props.getProperty( "url.file.name" );
        	if ( fileName == null ||  fileName .toLowerCase() .endsWith( ".vzome" )  ) {
        		// assume it is XML for .vZome
        		try {
        			DocumentModel document = modelApp .loadDocument( bytes, true );
        			this .topEditor = new EditorController( document, this, props, false );
                	return this .topEditor;
        		} catch ( Exception e ) {
        			mErrors .reportError( Controller.UNKNOWN_ERROR_CODE, new Object[] { e } );
        			return null;
        		}
        	}
        	else {
        		fileName = fileName .toLowerCase();
            	String extension = null;
        		if ( fileName .endsWith( ".zomic" ) )
        			extension = "zomic";
        		else if ( fileName .endsWith( ".py" ) )
        			extension = "py";
        		else if ( fileName .endsWith( ".vef" ) )
        			extension = "vef";
        		else if ( fileName .endsWith( ".zomod" ) )
        			extension = "zomod";
        		else if ( fileName .endsWith( ".zcd" ) )
        			extension = "zcd";
        		else {
        			mErrors.reportError( "No valid import extension for " + fileName, new Object[] {} );
        			return null;
        		}
        		// import a non .vZome format
        		try {
        			ByteArrayOutputStream out = new ByteArrayOutputStream();
        			byte[] buf = new byte[1024];
        			int num;
        			while ( ( num = bytes.read( buf, 0, 1024 ) ) > 0 )
        				out.write( buf, 0, num );
        			bytes .close();
        			String script = new String( out.toByteArray() );
        			DocumentModel document = modelApp .importDocument( script, extension );
        			this .topEditor = new EditorController( document, this, props, true );
        			this .topEditor .syncRendering();
                	return this .topEditor;
        		} catch ( IOException e ) {
        			mErrors.reportError( Controller.UNKNOWN_ERROR_CODE, new Object[] { e } );
        			return null;
        		}
        	}
        }
    }



    public boolean userHasEntitlement( String propName )
    {
        // this one is implemented here, not ApplicationUI, so that the headless
        // test framework can open files with all predefined orbits
        if ( "all.tools" .equals( propName ) )
            return propertyIsTrue( "entitlement.all.tools" );

        return super .userHasEntitlement( propName );
    }
    
    public Properties getDefaults()
    {
    	return properties;
    }
    
    public String getProperty( String string )
    {
        if ( "window.class.name".equals( string ) )
            return "org.vorthmann.zome.ui.DocumentFrame";

        if ( "splash.image.resource".equals( string ) )
            return "org/vorthmann/zome/ui/vZome50Splash.gif";

        if ( "field.name".equals( string ) )
            return "golden";

        return properties .getProperty( string );
    }
    
    // Shapes

    private final Lights mLights = new Lights();

    private final Map mSymmetryModels = new HashMap();

    private RenderingViewer.Factory rvFactory;
    
    private EditorController topEditor = null;
    
    private Properties properties;  // these are initially just internal defaults, then later replaced during initialization with user prefs
    
    private Application modelApp;

    // private final RenderingViewer.Factory mRVFactory;

    /**
     * A separate initialization phase, to keep the constructor cheap. We want
     * to be able to reply to getProperty() quickly, to get the splash image up
     * ASAP.
     */
    public void initialize( Properties props )
    {
        long starttime = System.currentTimeMillis();

        if ( logger .isLoggable( Level .INFO ) )
            logger .info( "DefaultApplication .initialize() starting" );

        // this now includes overrides to the defaults originally loaded
        this.properties = props;

        boolean enableCommands = userHasEntitlement( "model.edit" );;
        final FailureChannel failures = new FailureChannel()
        {	
			public void reportFailure( Failure f )
			{
	            mErrors.reportError( USER_ERROR_CODE, new Object[] { f } );
			}
		};
        modelApp = new Application( enableCommands, failures, props );
        
        Colors colors = modelApp .getColors();

        for ( int i = 1; i <= 3; i++ ) {
            Color color = colors .getColorPref( "light.directional." + i );
            Vector3f dir = new Vector3f( colors .getVectorPref( "direction.light." + i ) );
            mLights.addDirectionLight( color, dir );
        }
        mLights .setAmbientColor( colors .getColorPref( "light.ambient" ) );
        mLights .setBackgroundColor( colors .getColor( Colors.BACKGROUND ) );

        if ( ! "true".equals( props .getProperty( "no.rendering" ) )
        &&   ! "true" .equals( props .getProperty( "no.viewing" ) )
        &&   ! "true" .equals( props .getProperty( "no.java3d" ) ) )
        {
        	boolean useEmissiveColor = ! "true".equals( props .getProperty( "no.glowing.selection" ) );
            // need this set up before we do any loadModel
            String factoryName = getProperty( "RenderingViewer.Factory.class" );
            if ( factoryName == null )
                factoryName = "org.vorthmann.zome.render.java3d.Java3dFactory";
            try {
                Class factoryClass = Class.forName( factoryName );
                Constructor constructor = factoryClass .getConstructor( new Class[] { Colors.class, Boolean.class } );
                rvFactory = (RenderingViewer.Factory) constructor.newInstance( new Object[] { colors, new Boolean( useEmissiveColor ) } );
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
            logger .info( "DefaultApplication .initialize() in milliseconds: " + ( endtime - starttime ) );
        
//         String teaPort = props .getProperty( "tea.agent.port" );
//         if ( teaPort != null )
//         {
//         	int port = Integer .parseInt( teaPort );
//         	TeaAgent agent = new TeaAgent( this );
//         	try {
// 				agent .startAgentServer( port );
// 			} catch ( Exception e ) {
// 				// TODO Auto-generated catch block
// 				e.printStackTrace();
// 			}
//         }
    }

    public RenderedModel loadModelPanels( String path )
    {
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        InputStream bytes = cl.getResourceAsStream( path );

        try {
    		DocumentModel document = this .modelApp .loadDocument( bytes, false );
    		// a RenderedModel that only creates panels
    		document .setRenderedModel( new RenderedModel( document .getField(), true ) 	 	 
    		{
    			protected void resetAttributes( RenderedManifestation rm, 	 	 
    					boolean justShape, Strut strut ) {} 	 	 

    			protected void resetAttributes(RenderedManifestation rm, 	 	 
    					boolean justShape, Connector m) {} 	 	 
    		} .withColorPanels( false ) ); 
    		document .loadXml( false, false );
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
    
    public String[] getCommandList( String listName )
    {
        if ( listName.startsWith( "fields." ) ) {
            Set names = modelApp .getFieldNames();
            return (String[]) names.toArray( new String[0] );
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
        return mLights;
    }

    static Logger logger = Logger.getLogger( "org.vorthmann.zome.controller" );

    static Properties noRenderProps = new Properties();

    public void doFileAction( String command, File file )
    {
        noRenderProps.setProperty( "no.rendering", "true" );
        if ( "checkAllFiles".equals( command ) )
            new FileChecker( this, file ).run();
        else
            super.doFileAction( command, file );
    }
}
