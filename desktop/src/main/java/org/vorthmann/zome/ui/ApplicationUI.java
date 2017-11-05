
package org.vorthmann.zome.ui;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.lang.reflect.Field;
import java.net.MalformedURLException;
import java.net.URL;
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

import org.vorthmann.j3d.Platform;
import org.vorthmann.ui.Controller;
import org.vorthmann.ui.SplashScreen;
import org.vorthmann.zome.app.impl.ApplicationController;

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
public final class ApplicationUI implements ActionListener, PropertyChangeListener
{
    private Controller mController;
    
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

        // If no FileHandler was pre-configured, then initialze our own default
        if (fh == null) {
            File logsFolder = Platform.logsFolder();
            logsFolder.mkdir();
            try {
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
                fh = new FileHandler("%h/" + Platform.logsPath() + "/vZome60_%u_%g.log", 500000, 10);
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
    }

    private static ApplicationUI theUI;

    private ApplicationUI() {}

    // This is not used on the Mac, where the MacAdapter is the main class.
    public static void main( String[] args )
    {
        try {
            String prop = System .getProperty( "user.dir" );
            File workingDir = new File( prop );
            URL codebase = workingDir .toURI() .toURL();
            initialize( args, codebase );
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
    public static ApplicationUI initialize( String[] args, URL codebase ) throws MalformedURLException
    {
    	/*
    	 * First, fail-fast if we can see any environmental issue that will prevent vZome from launching correctly.
    	 */
    	
		if( System.getProperty("os.name").toLowerCase().contains("windows")) {
			if( "console".compareToIgnoreCase(System.getenv("SESSIONNAME")) != 0) {
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
        String splashImage = "org/vorthmann/zome/ui/vZome-6-splash.png";
        if ( splashImage != null ) {
            splash = new SplashScreen( splashImage );
            splash .splash();
            logger .info( "splash screen displayed" );
        } 
        else {
            logger .severe( "splash screen not found at " + splashImage );
        }

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
        EventQueue .invokeLater( new InitializationWorker( theUI, args, codebase, splash ) );
        
        return theUI;
    }
    
    private static class InitializationWorker implements Runnable
    {
    	private final ApplicationUI ui;
		private final String[] args;
		private final URL codebase;
		private final SplashScreen splash;

		public InitializationWorker( ApplicationUI ui, String[] args, URL codebase, SplashScreen splash )
    	{
			this.ui = ui;
			this.args = args;
			this.codebase = codebase;
			this.splash = splash;
    	}

		@Override
		public void run()
		{
			String defaultAction = "launch";
	        Properties configuration = new Properties();
	        for ( int i = 0; i < args.length; i++ ) {
	            if ( args[i] .startsWith( "-" ) ) {
	                String propName = args[i++] .substring( 1 );
	                String propValue = args[i];
	                configuration .setProperty( propName, propValue );
	            }
	            else
		            try {
		                URL url = new URL( codebase, args[i] );
		                defaultAction = "openURL-" + url .toExternalForm();
		            } catch ( MalformedURLException e ) {
		            	logger .severe( "Unable to construct URL from codebase: " + codebase + ", url argument" + args[i] );
	  	            }
	        }

	        configuration .putAll( loadBuildProperties() );

	        ui .mController = new ApplicationController( ui, configuration );

	        configuration .setProperty( "coreVersion", ui .mController .getProperty( "coreVersion" ) );
			logConfig( configuration );

            ui.errors =  new Controller.ErrorChannel()
	        {
				@Override
	            public void reportError( String errorCode, Object[] arguments )
	            {
	            	// code copied from DocumentFrame!
	            	
	                if ( Controller.USER_ERROR_CODE.equals( errorCode ) ) {
	                    errorCode = ( (Exception) arguments[0] ).getMessage();
	                    // don't want a stack trace for a user error
	                    logger.log( Level.WARNING, errorCode );
	                } else if ( Controller.UNKNOWN_ERROR_CODE.equals( errorCode ) ) {
	                    errorCode = ( (Exception) arguments[0] ).getMessage();
	                    logger.log( Level.WARNING, "internal error: " + errorCode, ( (Exception) arguments[0] ) );
	                    errorCode = "internal error, see the log file at " + getLogFileName();
	                } else {
	                    logger.log( Level.WARNING, "reporting error: " + errorCode, arguments );
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
	        
	        ui.mController .actionPerformed( new ActionEvent( this, ActionEvent.ACTION_PERFORMED, defaultAction ) );

        	if ( splash != null )
        		splash .dispose();
		}
    }
    
	@Override
	public void propertyChange( PropertyChangeEvent evt )
	{
		switch ( evt .getPropertyName() ) {

		case "newDocument":
			Controller controller = (Controller) evt. getNewValue();
			DocumentFrame window = new DocumentFrame( controller );
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
	public void actionPerformed( ActionEvent event )
	{
		String action = event. getActionCommand();

		if ( "new" .equals( action ) )
            action = "new-golden";

		switch ( action ) {

		case "showAbout":
            about();
			break;

    	case "openURL":
            String str = JOptionPane .showInputDialog( null, "Enter the URL for an online .vZome file.", "Open URL",
                    JOptionPane.PLAIN_MESSAGE );
        	mController .actionPerformed( new ActionEvent( this, ActionEvent.ACTION_PERFORMED, "openURL-" + str ) );
            break;

		case "quit":
            quit();
			break;
			
		default:
			JOptionPane .showMessageDialog( null,
					"No handler for action: \"" + action + "\"",
					"Error Performing Action", JOptionPane.ERROR_MESSAGE );
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
            "logfile.name",
            "logging.properties.filename",
            "logging.properties.file.exists",
        });
		appendPropertiesList(sb, src, new String[]
			{	// use with src.getProperty(propName)
				"edition",
				"version",
				"buildNumber",
				"gitCommit",
				"coreVersion"
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
    
    private static Properties loggingProperties() {
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
        props.put("logfile.name", getLogFileName());        
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

    private static String logFileName = null;

    public static String getLogFileName() {
        // determined on demand and cached so we only need to do all of this the first time.
        if (logFileName == null) {
            for (Handler handler : Logger.getLogger("").getHandlers()) {
                if (handler.getClass().isAssignableFrom(FileHandler.class)) {
                    FileHandler fileHandler = (FileHandler) handler;
                    try {
                        // FileHandler.files has private access, 
                        // so I'm going to resort to reflection to get the file name.
                        Field privateFilesField = fileHandler.getClass().getDeclaredField("files");
                        privateFilesField.setAccessible(true); // allow access to this private field
                        File[] files = (File[]) privateFilesField.get(fileHandler);
                        logFileName = files[0].getCanonicalPath();
                        break;
                    } catch (NullPointerException | NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException | IOException ex) {
                        logger.log(Level.SEVERE, "Unable to determine log file name.", ex);
                    }
                }
            }
            if (logFileName == null) {
                logFileName = "your home directory"; // just be sure it's not null
                logger.warning("Unable to identify log file name.");
            }
        }
        return logFileName;
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
    	String version = mController.getProperty( "edition" ) + " " + mController.getProperty( "version" ) + ", build "
        		+ mController .getProperty( "buildNumber" );
    	if ( mController .userHasEntitlement( "developer.extras" ) )
    		version += "\n\nGit commit: " + mController .getProperty( "gitCommit" )
    					+ "\n\nvzome-core: " + mController .getProperty( "coreVersion" );
        JOptionPane.showMessageDialog( null, version + "\n\n"

                + "Contributors:\n\n" + "Scott Vorthmann\n" + "David Hall\n" + "\n"
                
                + "Acknowledgements:\n\n" + "Paul Hildebrandt\n" + "Marc Pelletier\n"
                + "David Richter\n" + "Brian Hall\n" + "Dan Duddy\n" + "Fabien Vienne\n" + "George Hart\n"
                + "Edmund Harriss\n" + "Corrado Falcolini\n" + "Ezra Bradford\n" + "Chris Kling\n" + "Samuel Verbiese\n" + "Walt Venable\n"
                + "Will Ackel\n" + "Tom Darrow\n" + "Sam Vandervelde\n" + "Henri Picciotto\n" + "Florelia Braschi\n"
                
                + "\n" + "Dedicated to Everett Vorthmann,\n" + "who made me an engineer\n"
                + "\n",
                "About vZome", JOptionPane.PLAIN_MESSAGE );
    }
} 
