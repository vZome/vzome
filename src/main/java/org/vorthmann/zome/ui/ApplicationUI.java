
package org.vorthmann.zome.ui;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
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

/**
 * Top-level UI class for vZome.
 * 
 * This class has few responsibilities:
 * 
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
		 * Next, get logging configured.
		 */

        Logger rootLogger = Logger .getLogger( "" );
		rootLogger.setLevel(Level.CONFIG);
        File logsFolder = Platform .logsFolder();
        logsFolder .mkdir();
        Handler fh = null;
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
            fh = new FileHandler( "%h/" + Platform .logsPath() + "/vZome50_%u_%g.log", 500000, 10 );
        } catch ( Exception e1 ) {
        	rootLogger .log( Level.WARNING, "unable to set up vZome file log handler", e1 );
            try {
                fh = new FileHandler();
            } catch ( Exception e2 ) {
            	rootLogger .log( Level.WARNING, "unable to set up default file log handler", e2 );
            }        
        }
        if ( fh != null )
        {
            fh .setFormatter( new SimpleFormatter() );
            rootLogger .addHandler( fh );
        }
        
        /*
         * Now, get the splash screen up before doing any more work.
         */

        SplashScreen splash = null;
        String splashImage = "org/vorthmann/zome/ui/vZome50Splash.gif";
        if ( splashImage != null ) {
            splash = new SplashScreen( splashImage );
            splash .splash();
        }
        if ( logger .isLoggable( Level .INFO ) )
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
        EventQueue .invokeLater( new InitializationWorker( theUI, args, codebase, splash ) );
        
        return theUI;
    }
    

    private static class InitializationWorker implements Runnable
    {
    	private ApplicationUI ui;
		private String[] args;
		private URL codebase;
		private SplashScreen splash;

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
			logConfig( configuration );

	        String controllerClassName = "org.vorthmann.zome.app.impl.ApplicationController";
	        try {
	            Class controllerClass = Class .forName( controllerClassName );
	            Constructor<?> constructor = controllerClass .getConstructor( new Class<?>[] { ActionListener.class, Properties.class } );
	            ui .mController = (Controller) constructor .newInstance( new Object[] { ui, configuration } );
	        } catch ( Exception e ) {
	            logger .log( Level.SEVERE, "controller class could not instantiate: " + controllerClassName, e );
	            System .exit( 0 );
	        }

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
	                    errorCode = "internal error, see vZome50_*_*.log in your home or logs directory";
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
				"user.dir", // current working directory
				"os.name",
				"os.arch"
			}); 
		appendPropertiesList(sb, src, new String[]
			{	// use with src.getProperty(propName)
				"edition",
				"version",
				"buildNumber",
				"gitCommit"
			});
		logger.config(sb.toString());
	}
	
	private static void appendPropertiesList(StringBuilder sb, Properties src, String[] propNames)
	{
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
        String licensee = mController .getProperty( "licensed.user" );
        if ( licensee == null )
            licensee = "unlicensed";
        else
            licensee = "licensed to " + licensee;
        JOptionPane.showMessageDialog( null, mController.getProperty( "edition" ) + " " + mController.getProperty( "version" ) + ", build "
        		+ mController .getProperty( "buildNumber" ) + "\n\n"
                + "  by Scott Vorthmann\n\n"
                // + licensee + "\n\n"
                + "Acknowledgements:\n\n" + "Paul Hildebrandt\n" + "Marc Pelletier\n" + "David Hall\n"
                + "David Richter\n" + "Brian Hall\n" + "Dan Duddy\n" + "Fabien Vienne\n" + "George Hart\n"
                + "Edmund Harriss\n" + "Corrado Falcolini\n" + "Ezra Bradford\n" + "Chris Kling\n" + "Samuel Verbiese\n" + "Walt Venable\n"
                + "Will Ackel\n" + "Tom Darrow\n" + "Sam Vandervelde\n" + "Henri Picciotto\n" + "Florelia Braschi\n"
                + "\n" + "Dedicated to Everett Vorthmann,\n" + "who made me an engineer\n" + "\n"
                + "  Copyright 2015, Scott Vorthmann \n", "About vZome", JOptionPane.PLAIN_MESSAGE );
    }
} 
