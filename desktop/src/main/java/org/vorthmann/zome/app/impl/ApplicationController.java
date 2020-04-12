package org.vorthmann.zome.app.impl;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.vorthmann.j3d.J3dComponentFactory;
import org.vorthmann.j3d.Platform;
import org.vorthmann.ui.Controller;
import org.vorthmann.ui.DefaultController;
import org.vorthmann.zome.ui.ApplicationUI;

import com.vzome.core.commands.Command.Failure;
import com.vzome.core.commands.Command.FailureChannel;
import com.vzome.core.editor.DocumentModel;
import com.vzome.core.exporters.Exporter3d;
import com.vzome.core.math.symmetry.Symmetry;
import com.vzome.core.render.Colors;
import com.vzome.core.render.RenderedModel;
import com.vzome.core.viewing.Lights;

public class ApplicationController extends DefaultController
{
    private static final Logger logger = Logger.getLogger( "org.vorthmann.zome.controller" );

    private final Map<String, DocumentController> docControllers = new HashMap<>();

    private final UI ui;

    private final Properties userPreferences = new Properties();

    private final Properties properties = new Properties();

    private J3dComponentFactory rvFactory;

    private final com.vzome.core.editor.Application modelApp;

    private final File preferencesFile;

    private int lastUntitled = 0;

    private Map<String, RenderedModel> symmetryModels = new HashMap<String, RenderedModel>();
    
    public interface UI
    {
        public void doAction( String action );
    }

    public ApplicationController( UI ui, Properties commandLineArgs, J3dComponentFactory rvFactory )
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
        this.preferencesFile = prefsFile;
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

        // This seems to get rid of the "white-out" problem on David's (Windows) computer.
        // Otherwise it shows up sporadically, but still frequently. 
        // It is usually, but not always, triggered by such events as context menus,
        // tool tips, or modal dialogs being rendered on top of the main frame.
        // Since the problem has not been reported elsewhere, this fix will be configurable, rather than hard coded.
        final String NOERASEBACKGROUND = "sun.awt.noerasebackground";
        if( propertyIsTrue(NOERASEBACKGROUND)) { // if it's set to true in the prefs file or command line
            System.setProperty(NOERASEBACKGROUND, "true"); // then set the System property so the AWT/Swing components will use it.
            logger .config( NOERASEBACKGROUND + " is set to 'true'." );
        }

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

        if ( rvFactory != null ) {
            this .rvFactory = rvFactory;
        }
        else
        {
            boolean useEmissiveColor = ! propertyIsTrue( "no.glowing.selection" );
            // need this set up before we do any loadModel
            String factoryName = getProperty( "RenderingViewer.Factory.class" );
            if ( factoryName == null )
                factoryName = "org.vorthmann.zome.render.jogl.JoglFactory";
            try {
                Class<?> factoryClass = Class.forName( factoryName );
                Constructor<?> constructor = factoryClass .getConstructor( new Class<?>[] { Colors.class, Boolean.class } );
                this .rvFactory = (J3dComponentFactory) constructor.newInstance( new Object[] { colors, useEmissiveColor } );
            } catch ( Exception e ) {
                mErrors.reportError( "Unable to instantiate RenderingViewer.Factory class: " + factoryName, new Object[] {} );
                System.exit( 0 );
            }
        }

        long endtime = System.currentTimeMillis();
        if ( logger .isLoggable( Level .INFO ) )
            logger .log(Level.INFO, "ApplicationController initialization in milliseconds: {0}", ( endtime - starttime ));
    }

    public RenderedModel getSymmetryModel( String path, Symmetry symmetry )
    {
        RenderedModel result = this .symmetryModels .get( path );
        // The cache does not care if the symmetry matches.
        if ( result != null )
            return result;

        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        InputStream bytes = cl.getResourceAsStream( path );

        try {
            DocumentModel document = this .modelApp .loadDocument( bytes );
            // a RenderedModel that only creates panels
            document .setRenderedModel( new RenderedModel( symmetry ) .withColorPanels( false ) ); 
            document .finishLoading( false, false );
            result = document .getRenderedModel();
            this .symmetryModels .put( path, result );
            return result;
        } catch ( Exception e ) {
            throw new RuntimeException( e );
        }
    }

    @Override
    public void doAction( String action )
    {
        try {
            if ( action .equals( "showAbout" ) 
                    || action .equals( "openURL" ) 
                    || action .equals( "quit" )
                    || action .startsWith( "browse-" )
                    )
            {
                this .ui .doAction( action );
                return;
            }

            if( "launch".equals(action) ) {
                String sawWelcome = userPreferences .getProperty( "saw.welcome" );
                if ( sawWelcome == null )
                {
                    String welcome = properties .getProperty( "welcome" );
                    doAction( "openResource-" + welcome );
                    userPreferences .setProperty( "saw.welcome", "true" );
                    FileWriter writer;
                    try {
                        writer = new FileWriter( preferencesFile );
                        userPreferences .store( writer, "" );
                        writer .close();
                    } catch ( IOException e ) {
                        logger.fine(e.toString());
                    }
                    return;
                }
                action = "new";
            }

            if ( "new" .equals( action ) ) {
                String fieldName = properties .getProperty( "default.field" );
                action = "new-" + fieldName;
            }

            if ( action .startsWith( "new-" ) )
            {
                String fieldName = action .substring( "new-" .length() );
                File prototype = new File( Platform .getPreferencesFolder(), "Prototypes/" + fieldName + ".vZome" );
                if ( prototype .exists() ) {
                    logger.log(Level.CONFIG, "Loading default template from {0}", prototype.getCanonicalPath());
                    doFileAction( "newFromTemplate", prototype );
                }
                else
                {
                    // creating a new Document
                    Properties docProps = new Properties();
                    docProps .setProperty( "new.document", "true" );
                    DocumentModel document = modelApp .createDocument( fieldName );
                    String title = "Untitled " + ++lastUntitled;
                    docProps .setProperty( "window.title", title );
                    docProps .setProperty( "edition", this .properties .getProperty( "edition" ) );
                    docProps .setProperty( "version", this .properties .getProperty( "version" ) );
                    docProps .setProperty( "buildNumber", this .properties .getProperty( "buildNumber" ) );
                    docProps .setProperty( "gitCommit", this .properties .getProperty( "gitCommit" ) );
                    newDocumentController( title, document, docProps );
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
            else if ( action .startsWith( "newFromResource-" ) )
            {
                Properties docProps = new Properties();
                docProps .setProperty( "as.template", "true" ); // don't set window.file!
                String path = action .substring( "newFromResource-" .length() );
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
                try {
                    URL url = new URL( path );
                    InputStream bytes= null;
                    HttpURLConnection conn = (HttpURLConnection) url .openConnection();
                    // See https://stackoverflow.com/questions/1884230/urlconnection-doesnt-follow-redirect
                    //  This won't switch protocols, but seems to work otherwise.
                    conn .setInstanceFollowRedirects( true );
                    bytes = conn .getInputStream();
                    loadDocumentController( path, bytes, docProps );
                }
                catch ( Throwable e ) {
                    e.printStackTrace();
                    this .mErrors .reportError( "Unable to open URL: " + e .getMessage(), new Object[]{ e } );
                }
            }
            else 
            {
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
            Path filePath = file .toPath();
            String path = filePath .toAbsolutePath() .toString();

            int pos = path .toLowerCase() .lastIndexOf( ".vzome." );
            if( pos > 0 ) {
                /*
                 * This allows the user to select a file named "foo.vzome.png" that they can preview,
                 * as a "proxy" that will actually attempt to open the corresponding vzome file.
                 * 
                 * Note that such a "proxy" image file with a ".vome.png" extension is generated automatically 
                 * upon saving a vZome file by adding "save.exports=capture.png" to .vZome.prefs.
                 */
                path = path.substring( 0, pos += 6 );
                file = Paths .get( path ) .toFile();
            }

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

    private void loadDocumentController( final String name, final InputStream bytes, final Properties properties )
    {
        try {
            DocumentModel document = modelApp .loadDocument( bytes );
            newDocumentController( name, document, properties );
        } catch ( Exception e ) {
            e.printStackTrace();
            this .mErrors .reportError( "Unable to load; this may not be a vZome file.", new Object[]{ e } );
        }
    }

    public J3dComponentFactory getJ3dFactory()
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
    public final String getProperty( String propName )
    {
        switch ( propName ) {

        case "formatIsSupported":
            return "true";

        case "untitled.title":
            return "Untitled " + ++lastUntitled;

        case "coreVersion":
            return this .modelApp .getCoreVersion();

        default:
            if ( propName .startsWith( "field.label." ) )
            {
                String fieldName = propName .substring( "field.label." .length() );
                // TODO implement AlgebraicField.getLabel()
                switch ( fieldName ) {

                case "golden":
                    return "Zome (Golden)";

                case "rootTwo":
                    return "\u221A2";

                case "rootThree":
                    return "\u221A3";

                case "snubDodec":
                    return "Snub Dodec";

                case "sqrtPhi":
                    return "\u221A\u03C6";

                default:
                    if( fieldName.startsWith("sqrt") ) {
                        return fieldName.replace("sqrt","\u221A");
                    } else {
                        // capitalize first letter
                        return Character.toUpperCase(fieldName.charAt(0)) + fieldName.substring(1);
                    }
                }
            }
            if ( propName .startsWith( "enable." ) && propName .endsWith( ".field" ) )
            {
                String fieldName = propName .substring( "enable." .length() );
                fieldName = fieldName .substring( 0, fieldName .lastIndexOf( ".field" ) );

                switch ( fieldName ) {

                case "golden":
                    return "false"; // this one is forcibly enabled by the menu, and we don't want it listed twice

                case "dodecagon":
                    return "false"; // this is just an alias for rootThree

                default:
                    // fall through
                }

                if ( getProperty( "vZomeDeveloper" ) != null )
                    return "true"; // developer sees all available fields

                switch ( fieldName ) {

                case "rootTwo":
                case "rootThree":
                case "heptagon":
                    return "true"; // these are enabled for everyone

                default:
                    // fall through, see if it is explicitly set
                }
            }
            return properties .getProperty( propName );
        }
    }

    @Override
    public void setModelProperty( String name, Object value )
    {
        this .properties .setProperty( name, value .toString() );
    }

    @Override
    public Controller getSubController( final String name )
    {
        return docControllers .get( name );
    }

    private void newDocumentController( final String name, final DocumentModel document, final Properties props )
    {
        DocumentController newest = new DocumentController( document, this, props );
        this .registerDocumentController( name, newest );
        // trigger window creation in the UI
        this .firePropertyChange( "newDocument", null, newest );
    }

    private void registerDocumentController( final String name, final DocumentController newest )
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
                    // important to re-register under the new name, AND get a new listener, or removes won't work
                    newest .removePropertyListener( this );
                    registerDocumentController( (String) evt .getNewValue(), newest );
                    break;

                case "visible":
                    if ( Boolean.FALSE .equals( evt .getNewValue() ) ) {
                        docControllers .remove( name );
                        if ( ! propertyIsTrue( "keep.alive" ) && docControllers .isEmpty() )
                            // closed the last window, so we're exiting
                            System .exit( 0 );
                    }
                    break;

                default:
                    break;
                }
            }
        });
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

    @Override
    public String[] getCommandList( String listName )
    {
        if ( listName .startsWith( "fields" ) )
        {
            Set<String> names = modelApp .getFieldNames();
            SortedSet<String> sorted = new TreeSet<String>( names );
            return sorted .toArray( new String[]{} );
        }
        else if ( listName .startsWith( "symmetries." ) )
        {
            //        	return (String[]) this .symmetryPerspective .getGeometries() .stream() .map( e -> e .getName() ) .toArray();
            return null;  // TODO probably unused
        }
        return new String[0];
    }

    public Lights getLights()
    {
        return modelApp .getLights();
    }

}
