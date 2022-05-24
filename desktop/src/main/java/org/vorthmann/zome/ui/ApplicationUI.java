
package org.vorthmann.zome.ui;

import java.awt.Desktop;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.FileSystemNotFoundException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Properties;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import org.vorthmann.j3d.Platform;
import org.vorthmann.ui.SplashScreen;

import com.vzome.dap.DapAdapter;
import com.vzome.desktop.api.Controller;
import com.vzome.desktop.awt.ApplicationController;
import com.vzome.desktop.awt.GraphicsController;

/**
 * Top-level UI class for vZome.
 * 
 * This class has few responsibilities:
 * 
 *  - initialization of the root level logging system
 *  - create and destroy a splash screen during initial launch
 *  - create DocumentFrames for DocumentControllers
 *  - convey command line arguments to the ApplicationController
 *  - provide a static main() entry point
 *  - provide a common static entry point (initialize() method) for different launch adapters
 *  - provide quit, about, and open handlers for the Mac platform Adapter
 *  - provide the final Controller.ErrorChannel UI
 *  - display the About dialog
 *  - log build properties as early as possible
 *  
 *  I've tried to remove any state (fields) that is not necessary for those functions,
 *  and delegated as much as possible to the ApplicationController, including management
 *  of user preferences.
 *  
 * @author vorth
 *
 */
public final class ApplicationUI implements ApplicationController.UI, PropertyChangeListener
{
    private ApplicationController mController;

    private DapAdapter debugger;

    private Controller.ErrorChannel errors;

    private final Collection<DocumentFrame> windowsToClose = new ArrayList<>();

    // loggerClassName = "org.vorthmann.zome.ui.ApplicationUI"
    // Initializing it this way just ensures that any copied code uses the correct class name for a static Logger in any class.
    private static final String loggerClassName = new Throwable().getStackTrace()[0].getClassName();
    private static final Logger logger = Logger .getLogger( loggerClassName );

    // static class initializer configures global logging before any instance of the class is created.
    static {
        Logger rootLogger = Logger.getLogger("");
        Level minLevel = Level.SEVERE;
        if (rootLogger.getLevel().intValue() > minLevel.intValue()) {
            // Set minimum logging level
            rootLogger.setLevel(minLevel);
        }

        // If a FileHandler is already pre-configured by the logging.properties file then just use it as-is.
        FileHandler fh = null;
        for (Handler handler : rootLogger.getHandlers()) {
            if (handler.getClass().isAssignableFrom(FileHandler.class)) {
                fh = (FileHandler) handler;
                break;
            }
        }

        // If no FileHandler was pre-configured, then initialize our own default
        if (fh == null) {
            Path logsFolder = Platform.logsFolder();
            try {
                if(! Files.exists(logsFolder)) {
                    Files .createDirectory( logsFolder );
                }
                // If there is a log file naming conflict and no "%u" field has been specified, 
                //  an incremental unique number will be added at the end of the filename after a dot.
                // This behavior interferes with file associations based on the .log file extension.
                // It also results in the log files accumulating forever rather than overwriting the older ones
                //  and leaving only the number of log files specified in the constructor,
                //  so be sure to specify %u in the format string.
                // If we limit the number of logs to 10, then sorting them alphabetically (0-9) conveniently sorts them by date & time as well.
                // 
                // SV: I've reversed the %u and %g, so that sorting by name puts related logs together, in order.  The Finder / Explorer already
                //   knows how to sort by date, so we don't need to support that.
                fh = new FileHandler("%h/" + Platform.logsPath() + "/vZome7.0_%u_%g.log", 500000, 10);
            } catch (Exception e1) {
                rootLogger.log(Level.WARNING, "unable to set up vZome file log handler", e1);
                try {
                    fh = new FileHandler();
                } catch (Exception e2) {
                    rootLogger.log(Level.WARNING, "unable to set up default file log handler", e2);
                }
            }
            if (fh != null) {
                fh.setFormatter(new SimpleFormatter());
                rootLogger.addHandler(fh);
            }
        }
        // The following reflection code generates a warning in Java 11 so I'm omitting it, 
        // but still leaving it here so it can be uncommented for debugging
//        if(fh != null) {
//            try {
//                Field privateFilesField = fh.getClass().getDeclaredField("files");
//                privateFilesField.setAccessible(true);
//                File[] files = (File[]) privateFilesField.get(fh);
//                System.out.println("Log file " + files[0].getCanonicalPath());
//            } catch(Exception ex) {
//                ex.printStackTrace();
//            }
//        }
    }

    private static ApplicationUI theUI;

    private ApplicationUI() {}

    // This is not used on the Mac, where the MacAdapter is the main class.
    public static void main( String[] args )
    {
        try {
            initialize( args );
        } catch ( Throwable e ) {
            e .printStackTrace();
            System.out.println( "problem in main(): " + e.getMessage() );
        }
    }

    /**
     * A common entry point for main() and com.vzome.platform.mac.Adapter.main().
     * 
     * @param args
     * @param codebase
     * @return
     * @throws MalformedURLException
     */
    public static ApplicationUI initialize( String[] args ) throws MalformedURLException
    {
        /*
         * First, fail-fast if we can see any environmental issue that will prevent vZome from launching correctly.
         */

        if( System.getProperty("os.name").toLowerCase().contains("windows")) {
            String sessionName = System.getenv("SESSIONNAME");
            // Apparently, the sessionName environment variable can be null in some obscure situations.
            // This can happen in the rare case when Explorer.exe has been killed and restarted manually.
            // We will allow for that condition to avoid the NPE.
            if( sessionName != null && "console".compareToIgnoreCase(sessionName) != 0) {
                logger.info("Java OpenGL (JOGL) is not supported by Windows Terminal Services.");
                final String msg = "vZome cannot be run under Windows Terminal Services.";
                logger.severe(msg);
                JOptionPane.showMessageDialog( null, msg, "vZome Fatal Error", JOptionPane.ERROR_MESSAGE );
                System .exit( 0 );
            }
        }

        /*
         * Get the splash screen up before doing any more work.
         */

        SplashScreen splash = null;
        String splashImage = "org/vorthmann/zome/ui/vZome-7-splash.png";
        splash = new SplashScreen( splashImage );
        splash .splash();
        logger .info( "splash screen displayed" );

        theUI = new ApplicationUI();

        /*
         * Implementation Note:
         *
         * Note that the launch thread of any GUI application is in effect an initial 
         * worker thread - it is not the event dispatch thread, where the bulk of processing
         * takes place. Thus, once the launch thread realizes a window, then the launch 
         * thread should almost always manipulate such a window through 
         * <code>EventQueue.invokeLater</code>. (This is done for closing the splash 
         * screen, for example.)
         */

        // NOW we're ready to spend the cost of further initialization, but on the event thread
        SwingUtilities .invokeLater( new InitializationWorker( theUI, args, splash ) );

        return theUI;
    }

    private static class InitializationWorker implements Runnable
    {
        private final ApplicationUI ui;
        private final String[] args;
        private final SplashScreen splash;

        public InitializationWorker( ApplicationUI ui, String[] args, SplashScreen splash )
        {
            this.ui = ui;
            this.args = args;
            this.splash = splash;
        }

        @Override
        public void run()
        {
            // This is executed on the EDT.  Should it be?

            Properties configuration = new Properties();
            Path fileArgument = null;
            for ( int i = 0; i < args.length; i++ ) {
                if ( args[i] .startsWith( "-" ) ) {
                    String propName = args[i++] .substring( 1 );
                    String propValue = args[i];
                    configuration .setProperty( propName, propValue );
                } else {
                    String arg = args[i];
                    logger.info( "OS file argument: " + arg );
                    Path normalizedPath = null;
                    try {
                        // standard Java 7 idiom for dealing with file URIs, which is what
                        //   Windows will pass when opening with double-click or drag-n-drop
                        normalizedPath = Paths .get( new URL( arg ) .toURI() );
                    } catch ( MalformedURLException | URISyntaxException e ) {
                        // probably just on Mac or Linux
                        normalizedPath = Paths .get( arg );
                    } catch ( FileSystemNotFoundException e ) {
                        // Someone passed an HTTP URL, and Paths.get() can't handle it.
                        logger.warning( "URL command-line arguments are not supported." );
                        continue; // leave fileArgument as null
                    }
                    fileArgument = normalizedPath .toAbsolutePath() .normalize();
                    logger.info( "Normalized file argument: " + fileArgument );
                }
            }

            configuration .putAll( loadBuildProperties() );

            ui .mController = new ApplicationController( ui, configuration, null );

            if ( ! ui .mController .propertyIsTrue( "vzome.disable.system.laf" ) ) {
                String className = UIManager.getSystemLookAndFeelClassName();
                try {
                    // Set System L&F
                    UIManager .setLookAndFeel( className );
                } 
                catch (Exception e) {
                    // live without it
                    logger.severe( "The look&feel was not set successfully: " + className );
                }
            }

            logConfig( configuration );

            ui.errors =  new Controller.ErrorChannel()
            {
                @Override
                public void reportError( String errorCode, Object[] arguments )
                {
                    // TODO: Refactor this duplicate code into one place
                    // code copied from DocumentFrame!

                    if ( Controller.USER_ERROR_CODE.equals( errorCode ) ) {
                        errorCode = ( (Exception) arguments[0] ).getMessage();
                        // don't want a stack trace for a user error
                        logger.log( Level.WARNING, errorCode );
                    } else if ( Controller.UNKNOWN_ERROR_CODE.equals( errorCode ) ) {
                        errorCode = ( (Exception) arguments[0] ).getMessage();
                        logger.log( Level.WARNING, "internal error: " + errorCode, ( (Exception) arguments[0] ) );
                        errorCode = "internal error has been logged";
                    } else {
                        logger.log( Level.WARNING, "reporting error: " + errorCode, arguments );
                        if(arguments != null) {
                            StringBuilder buf = new StringBuilder(errorCode);
                            for(Object arg : arguments) {
                                buf.append("\n")
                                .append(arg.toString());
                            }
                            errorCode = buf.toString();
                        }
                        // TODO use resources
                    }
                    // TODO use resources
                    JOptionPane .showMessageDialog( null, errorCode, "Error", JOptionPane.ERROR_MESSAGE );
                }

                @Override
                public void clearError()
                {}
            };

            ui.mController .setErrorChannel( ui.errors );
            ui.mController .addPropertyListener( ui );

            if ( fileArgument == null )
                ui .mController .actionPerformed( this, "launch" );
            else
                ui .mController .doFileAction( "open", fileArgument .toFile() );

            if ( splash != null )
                splash .dispose();
            
            String debugPortStr = ui .mController .getProperty( "debug.adapter.port" );
            if ( debugPortStr != null ) {
                try {
                    ui .debugger = new DapAdapter(); // inert unless we start the server
                    Integer debugPort = Integer .parseInt( debugPortStr );
                    ui .debugger .startServer( debugPort, ui .mController );
                } catch ( NumberFormatException e ) {
                    if ( logger .isLoggable( Level .WARNING ) )
                        logger .warning( "debug.adapter.port not an integer; debugger not listening" );
                }
            }
        }
    }

    @Override
    public void propertyChange( PropertyChangeEvent evt )
    {
        switch ( evt .getPropertyName() ) {

        case "newDocument":
            GraphicsController controller = (GraphicsController) evt. getNewValue();
            DocumentFrame window = new DocumentFrame( controller, this .mController .getJ3dFactory() );
            window .setVisible( true );
            window .setAppUI( new PropertyChangeListener() {

                @Override
                public void propertyChange( PropertyChangeEvent evt )
                {
                    windowsToClose .remove( window );
                }
            } );
            windowsToClose .add( window );
            break;

        default:
            break;
        }
    }

    @Override
    public void doAction( String action )
    {
        switch ( action ) {

        case "showAbout":
            about();
            break;

        case "openURL":
            String str = JOptionPane .showInputDialog( null, "Enter the URL for an online .vZome file.", "Open URL",
                    JOptionPane.PLAIN_MESSAGE );
            if ( str != null )
                mController .actionPerformed( this, "openURL-" + str );
            break;

        case "new-polygon":
            newPolygon();
            break;

        case "quit":
            quit();
            break;

        default:
            if ( action .startsWith( "browse-" ) )
            {
                String url = action .substring( "browse-" .length() );
                if ( Desktop.isDesktopSupported() && Desktop.getDesktop() .isSupported( Desktop.Action.BROWSE ) ) {
                    try {
                        Desktop.getDesktop() .browse( new URI( url ) );
                    } catch (IOException | URISyntaxException e) {
                        e .printStackTrace();
                        JOptionPane .showMessageDialog( null,
                                "Sorry, I am unable to launch the browser.",
                                "Error Performing Action", JOptionPane.ERROR_MESSAGE );
                    }
                }
            }
            else {
                JOptionPane .showMessageDialog( null,
                        "No handler for action: \"" + action + "\"",
                        "Error Performing Action", JOptionPane.ERROR_MESSAGE );
            }
        }
    }

    private void newPolygon() {
        // A PolygonField with more sides than this can be invoked directly from the custom menu: e.g new-polygon96
        int max = 60; // but this is the most we'll allow via this dialog 
        String limit = "The number must be between 4 and " + max + ".";
        String msg = "Enter the number of sides for the polygon field.\n\n" + limit;
        String str = JOptionPane .showInputDialog( null, msg, "Create a new design in a polygon field", JOptionPane.PLAIN_MESSAGE );
        if ( str != null ) {
            int nSides = 0;
            try {
                nSides = Integer.parseInt(str.trim());
                if( nSides < 4 || nSides > max) { 
                    throw new IllegalArgumentException(limit);
                }
            } catch (IllegalArgumentException e) {
                JOptionPane .showMessageDialog( null, e.getMessage() + "\n\n" + limit, "Invalid Numeric Value", JOptionPane.ERROR_MESSAGE );
                nSides = -1;
            }
            if(nSides >= 4 ) {
                mController .actionPerformed( this, "new-polygon" + nSides );
            }
        }
    }
    
    public static Properties loadBuildProperties()
    {
        String defaultRsrc = "build.properties";
        Properties defaults = new Properties();
        try {
            ClassLoader cl = ApplicationUI.class.getClassLoader();
            InputStream in = cl.getResourceAsStream( defaultRsrc );
            if ( in != null ) 
                defaults .load( in );
        } catch ( IOException ioe ) {
            logger.warning( "problem reading build properties: " + defaultRsrc );
        }
        return defaults;
    }

    // Be sure logConfig is not called until after loadBuildProperties()
    private static void logConfig( Properties src )
    {
        StringBuilder sb = new StringBuilder("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% Initializing Application:");
        appendPropertiesList(sb, null, new String[]
                {	// use with System.getProperty(propName)
                        "java.version",
                        "java.vendor",
                        "java.home",
                        "java.util.logging.config.file",
                        "user.dir", // current working directory
                        "os.name",
                        "os.arch",
                        "sun.java.command",
                        "file.encoding",
                }); 
        appendPropertiesList(sb, loggingProperties(), new String[] 
                {
                        "default.charset",
                        "logging.properties.filename",
                        "logging.properties.file.exists",
                });
        appendPropertiesList(sb, src, new String[]
                {	// use with src.getProperty(propName)
                        "edition",
                        "version",
                        "buildNumber",
                        "gitCommit",
                });
        // Use an anonymousLogger to ensure that this is always written to the log file 
        // regardless of the settings in the logging.properties file
        // and without changing the settings of any static loggers including the root logger
        Logger anonymousLogger = Logger.getAnonymousLogger();
        anonymousLogger.setLevel(Level.ALL);
        anonymousLogger.config(sb.toString());
        logJVMArgs();
        logExtendedCharacters();
    }

    private static Properties loggingProperties()
    {
        File f = new File(".", "logging.properties");
        // Use same logic to locate the file as LogManager.getLogManager().readConfiguration() uses...
        String fname = System.getProperty("java.util.logging.config.file");
        if (fname == null) {
            fname = System.getProperty("java.home");
            if (fname == null) {
                throw new Error("Can't find java.home ??");
            }
            f = new File(fname, "lib");
            f = new File(f, "logging.properties");
        }
        try {
            fname = f.getCanonicalPath();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        Properties props = new Properties();
        props.put("default.charset", java.nio.charset.Charset.defaultCharset().name());
        props.put("logging.properties.filename", fname);        
        props.put("logging.properties.file.exists", Boolean.toString(f.exists()));        
        return props;
    }

    private static void logJVMArgs() {
        Level level = Level.CONFIG;
        if(logger.isLoggable(level)) {
            Properties props = new Properties();
            RuntimeMXBean runtimeMxBean = ManagementFactory.getRuntimeMXBean();
            List<String> arguments = runtimeMxBean.getInputArguments();
            for(String argument : arguments) {
                String[] parts = argument.split("=", 2);
                String key = parts[0];
                String value = (parts.length > 1) ? parts[1] : "";
                props.put(key, value);
            }
            StringBuilder sb = new StringBuilder("JVM args:");
            appendPropertiesList(sb, props);
            logger.log(level, sb.toString());
        }
    }

    private static void logExtendedCharacters() {
        Level level = Level.FINE;
        if(logger.isLoggable(level)) {
            Properties props = new Properties();
            props.put("phi",   "\u03C6");
            props.put("rho",   "\u03C1");
            props.put("sigma", "\u03C3");
            props.put("sqrt",  "\u221A");
            props.put("xi",    "\u03BE");
            StringBuilder sb = new StringBuilder("Extended characters:");
            appendPropertiesList(sb, props);
            logger.log(level, sb.toString());
        }
    }

    // appends the whole list sorted by its keys
    private static void appendPropertiesList(StringBuilder sb, Properties props) {
        String[] keys = props.keySet().toArray(new String[props.keySet().size()]);
        Arrays.sort(keys);
        appendPropertiesList(sb, props, keys);
    }

    private static void appendPropertiesList(StringBuilder sb, Properties src, String[] propNames) {
        for (String propName : propNames) {
            String propValue = (src == null)
                    ? System.getProperty(propName)
                            : src.getProperty(propName);
                    propValue = (propValue == null ? "<null>" : propValue);
                    sb.append(System.getProperty("line.separator"))
                    .append("    ")
                    .append(propName)
                    .append(" = ")
                    .append(propValue);
        }
    }

    @Override
    public void runScript( String script, File file )
    {
        try {
            Runtime .getRuntime() .exec( script + " " + file .getAbsolutePath(),
                    null, file .getParentFile() );
        } catch ( IOException e ) {
            System .err .println( "Runtime.exec() failed on " + file .getAbsolutePath() );
            e .printStackTrace();
        }
    }

    @Override
    public void openApplication( File file )
    {
        try {
            if ( Desktop .isDesktopSupported() ) {
                // DH - The test for file.exists() shouldn't be needed if this method is invoked in the proper sequence
                // so I think it should be omitted eventually so the exceptions will be thrown
                // but I'm leaving it here for now as a debugging aid.
                if( ! file .exists() ) {
                    System .err .println( file .getAbsolutePath() + " does not exist." );
                    //                    return;
                }
                Desktop desktop = Desktop .getDesktop();
                System .err .println( "Opening app for  " + file .getAbsolutePath() + " in thread: " + Thread.currentThread() );
                desktop .open( file );
            }
        } catch ( IOException | IllegalArgumentException e ) {
            System .err .println( "Desktop.open() failed on " + file .getAbsolutePath() );
            if ( ! file .exists() ) {
                System .err .println( "File does not exist." );
            }
            e.printStackTrace();
        }
    }

    //%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
    // These next three methods may be invoked by the mac Adapter.
    //%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

    public void openFile( File file )
    {
        mController .doFileAction( "open", file );
    }

    public boolean quit()
    {
        for ( DocumentFrame documentFrame : windowsToClose ) {
            if ( ! documentFrame .closeWindow() )
                return false;
        }
        return true;
    }

    public void about()
    {
        String version = mController.getProperty( "edition" ) + " " + mController.getProperty( "version" ) + "."
                + mController .getProperty( "buildNumber" );
        if ( mController .userHasEntitlement( "developer.extras" ) )
            version += "\n\nGit commit: " + mController .getProperty( "gitCommit" )
            + "\n\nvzome-core: " + mController .getProperty( "coreVersion" );
        JOptionPane.showMessageDialog( null, version + "\n\n"

                + "Committers:\n\n" 
                + "Scott Vorthmann\n" 
                + "David Hall\n"
                + "Lucas Garron\n"
                + "\n"

                + "Contributors:\n\n"
                + "Paul Hildebrandt\n"
                + "David Richter\n"
                + "Brian Hall\n"
                + "George Hart\n"
                + "Edmund Harriss\n"
                + "Aaron Siegel\n"
                + "John and Jane Kostick\n"
                + "Jacob Rus\n"
                + "Nan Ma\n"
                + "Walt Venable\n"
                + "Will Ackel\n"
                + "Corrado Falcolini\n"
                + "Ezra Bradford\n"
                + "Sam Vandervelde\n"
                + "Dan Duddy\n"
                + "Samuel Verbiese\n"
                + "Tom Darrow\n"
                + "Henri Picciotto\n"
                + "Florelia Braschi\n"

                + "\n" + "In Memoriam:\n\n"
                + "Marc Pelletier\n"
                + "Fabien Vienne\n"
                + "Chris Kling\n"

                + "\n" + "Dedicated to Everett Vorthmann,\n"
                + "inventor of the Hall effect chip,\n"
                + "who made me an engineer\n"
                + "\n",
                "About vZome", JOptionPane.PLAIN_MESSAGE );
    }
} 
