
package org.vorthmann.zome.ui;

import java.awt.DisplayMode;
import java.awt.EventQueue;
import java.awt.Frame;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.AccessControlException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import javax.swing.JOptionPane;

import org.vorthmann.j3d.Platform;
import org.vorthmann.ui.ApplicationController;
import org.vorthmann.ui.Controller;
import org.vorthmann.ui.DefaultController;
import org.vorthmann.ui.ExclusiveAction;
import org.vorthmann.ui.SplashScreen;


public final class ApplicationUI extends DefaultController
{
	@Override
    public boolean userHasEntitlement( String propName )
    {
        if ( "save.files" .equals( propName ) )
            return getProperty( "licensed.user" ) != null;

        if ( "developer.extras" .equals( propName ) )
            return "vorth" .equals( getProperty( "vZomeDeveloper" ) );
        
        // TODO make this work more like developer.extras
        return propertyIsTrue( "entitlement." + propName );
        
        // this IS the backstop controller, so no purpose in calling super
    }

	@Override
    public String getProperty( String string )
    {
        if ( "formatIsSupported".equals( string ) )
            return "true";

        return mProperties .getProperty( string );
    }

	@Override
    public void doFileAction( String command, File file )
    {
        if ( file != null )
        {
            Properties props = new Properties();

            if ( "open" .equals( command ) )
                ; // no additional props
            else if ( "newFromTemplate" .equals( command ) )
                props .setProperty( "as.template", "true" );
            else if ( "openDeferringRedo" .equals( command ) )
                props .setProperty( "open.undone", "true" );
            else
                return;

            openFile( file, props );
        }
    }

	@Override
    public void doAction( String action, ActionEvent e )
    {
        if ( "new" .equals( action ) )
            action = "new-golden";

        if ( "openURL" .equals( action ) )
        {
            String str = e .getActionCommand();
            URL url = null;
            try {
                url = new URL( str );
            } catch ( MalformedURLException e1 ) {
                JOptionPane .showMessageDialog( null,
                        "Malformed URL: \"" + str + "\"",
                        "Error Loading URL", JOptionPane.ERROR_MESSAGE );
            }
            openURL( url, new Properties() );
        }
        else if ( "quit" .equals( action ) )
            quit();

        else if ( "showAbout" .equals( action ) )
            about();

        else if ( action .startsWith( "openResource-" ) )
        {
            action = action .substring( "openResource-" .length() );
            Properties docProps = new Properties();
            docProps .setProperty( "resource.document", action );
            docProps .setProperty( "reader.preview", "true" );
            openURL( null, docProps );
        }
        else if ( action .startsWith( "new-" ) )
        {
            action = action .substring( "new-" .length() );
            if ( action == null || "null" .equals( action ) )
                action = "golden";
            
            File prototype = new File( Platform .getPreferencesFolder(), "Prototypes/" + action + ".vZome" );
            if ( prototype .exists() )
                doFileAction( "newFromTemplate", prototype );
            else
            {
                Properties props = new Properties();
                props .setProperty( "field", action );
                openURL( null, props );
            }
        }
    }

    private int mLastUntitled = 0;
    
	// TODO: This collection is only ever added to or removed from, never read or counted. 
	//  Is it really needed? Maybe for future use?
    private final List<Frame> mWindows = new ArrayList<>();
    
    private final Map<String, Frame> mDocuments = new HashMap<>(); // keyed by URL
    
    private final LinkedList<String> mRecentDocs = new LinkedList<>();
    
//    private final JFrame mColorsFrame;
    
    private final Properties mProperties;
    
    private boolean initialAppLaunch = true;
        
//    private final ColorPanel mColorPanel;
    
    private final ApplicationController mController;

	// loggerClassName = "org.vorthmann.zome.ui.ApplicationUI"
	// Initializing it this way just ensures that any copied code uses the correct class name for a static Logger in any class.
	private static final String loggerClassName = new Throwable().getStackTrace()[0].getClassName();
    private static final Logger logger = Logger .getLogger( loggerClassName );

    public ApplicationUI( Properties commandLineArgs,  ApplicationController controller  )
    {
        /*
         * the order of preferences (first wins) is:
         * 
         *   command-line args (per launch)
         *   
         *   ${Platform .getPreferencesFolder()}/vZome.preferences or .vZome.prefs (per user)
         *   
         *   org/vorthmann/zome/app/defaultPrefs.properties resource (built-in defaults)
         */

        userPreferences = new Properties( controller .getDefaults() );
        
        File prefsFolder = Platform .getPreferencesFolder();        
        prefsFile = new File( prefsFolder, "vZome.preferences" );
        try {
            InputStream in = new FileInputStream( prefsFile );
            userPreferences .load( in );
        } catch ( IOException ioe ) {
            prefsFolder = new File( System.getProperty( "user.home" ) );
            prefsFile = new File( prefsFolder, "vZome.preferences" );
            try {
                InputStream in = new FileInputStream( prefsFile );
                userPreferences .load( in );
            } catch ( IOException ioe2 ) {
                prefsFile = new File( prefsFolder, ".vZome.prefs" );
                try {
                    InputStream in = new FileInputStream( prefsFile );
                    userPreferences .load( in );
                } catch ( IOException ioe3 ) {
                    // TODO record the preferences file
                    System.out
                            .println( "Used default preferences.  You can customize by copying \"sample.vZome.prefs\" to" );
                    System.out.println( "\"" + prefsFile.getAbsolutePath() + "\"," );
                    System.out.println( "and adjusting the contents." );
                }
            }
        } catch ( AccessControlException t ) {
            // running in JNLP without signing, cannot even println then...
            //   implies that default prefs are fine
        } catch ( Throwable t ) {
            System.err.println( "problem reading user preferences: "
                    + t.getMessage() );
        }
        
        mProperties = new Properties( userPreferences );
        mProperties .putAll( commandLineArgs );
        mProperties .putAll( loadBuildProperties() );
        	
        mController = controller;
        mController .setNextController( this );

        controller .initialize( mProperties );
		logConfig();
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
            System.err.println( "problem reading build properties: " + defaultRsrc );
        }
        return defaults;
	}

	// Be sure logConfig is not called until after loadBuildProperties()
	private void logConfig() {
		StringBuilder sb = new StringBuilder("Initializing Application:");
		appendPropertiesList(sb, null, new String[]
			{	// use with System.getProperty(propName)
				"java.version",
				"java.vendor",
				"java.home",
				"user.dir", // current working directory
				"os.name",
				"os.arch"
			}); 
		appendPropertiesList(sb, mController, new String[]
			{	// use with ApplicationController.getProperty(propName)
				"edition",
				"version",
				"buildNumber",
				"gitCommit"
			});
		logger.config(sb.toString());
	}
	
	private static void appendPropertiesList(StringBuilder sb, ApplicationController src, String[] propNames) {
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
            Logger .getLogger( loggerClassName )
                .log( Level.SEVERE, "problem in main()", e );
        }
    }
	
    /**
     * A common entry point for main() and WebStartWrapper.main().
     * 
     * @param args
     * @param codebase
     * @return
     * @throws MalformedURLException
     */
    public static ApplicationUI initialize( String[] args, URL codebase ) throws MalformedURLException
    {
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
            fh = new FileHandler( "%h/" + Platform .logsPath() + "/vZomeLog%g.%u.log", 200000, 10 );
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

        Properties props = new Properties();
        String urlStr = null;
        for ( int i = 0; i < args.length; i++ ) {
            if ( args[i] .startsWith( "-" ) ) {
                String propName = args[i++] .substring( 1 );
                String propValue = args[i];
                props .setProperty( propName, propValue );
            }
            else
                urlStr = args[i]; // TODO support opening multiple files, perhaps in a thumbnail browser
        }

        String controllerClassName = props .getProperty( "controller" );
        if ( controllerClassName == null )
            controllerClassName = "org.vorthmann.zome.app.impl.DefaultApplication"; // TODO remove backward compatibility

        ApplicationController controller = null;
        try {
            Class controllerClass = Class .forName( controllerClassName );
            controller = (ApplicationController) controllerClass .newInstance();
        } catch ( Exception e ) {
            logger .log( Level.SEVERE, "controller class could not instantiate: " + controllerClassName, e );
            System .exit( 0 );
        }

        SplashScreen splash = null;
        String splashImage = props .getProperty( "splash" );
        if ( splashImage == null )
            splashImage = controller .getProperty( "splash.image.resource" );
        if ( splashImage != null ) {
            splash = new SplashScreen( splashImage );
            splash .splash();
        }
        
        // NOW we're ready to spend the cost of the controller init
        ApplicationUI appUI = new ApplicationUI( props, controller );
        // props have now been copied to the appUI, so we can add props for the initial doc if we want

        ErrorChannel appErrors =  new ErrorChannel()
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
                    errorCode = "internal error, see vZomeLog0.0.log in your home directory";
                } else {
                    logger.log( Level.WARNING, "reporting error: " + errorCode, arguments );
                    // TODO use resources
                }
                    // TODO use resources
                JOptionPane .showMessageDialog( null, errorCode,
                        "Application Error", JOptionPane.ERROR_MESSAGE );
            }

			@Override
            public void clearError()
            {}
        };
        controller .setErrorChannel( appErrors );
        appUI .setErrorChannel( appErrors );

        URL url = null;
        if ( urlStr != null )
            try {
                url = new URL( codebase, urlStr );
                props .setProperty( "reader.preview", "true" );
            } catch ( MalformedURLException e ) {
                JOptionPane .showMessageDialog( null,
                        e .getMessage(),
                        "malformed URL argument", JOptionPane.ERROR_MESSAGE );
            }
        EventQueue .invokeLater( appUI .new ModelWindowOpener( props, url ) );
        if ( splash != null )
            EventQueue .invokeLater( new SplashScreenCloser( splash ) );
        
        return appUI;
    }
    

    private final class ModelWindowOpener implements Runnable
    {
        private final Properties fProps;
        private final URL fURL;
        
        public ModelWindowOpener( Properties props, URL url ) throws MalformedURLException
        {
            fProps = props;
            fURL = url;
        }
        
        public void run()
        {
            logger .fine( "in ModelWindowOpener.run()" );
            if ( fURL != null )
                openURL( fURL, fProps );
            else if ( initialAppLaunch )
            {
                String sawWelcome = mProperties .getProperty( "saw.welcome" );
                if ( sawWelcome == null )
                {
                    String welcome = fProps .getProperty( "welcome" );
                    if ( welcome == null )
                    {
                        if ( "G4G10" .equals( fProps .getProperty( "licensed.user" ) ) )
                            welcome = "org/vorthmann/zome/content/welcomeG4G10.vZome";
                        else
                            welcome = "org/vorthmann/zome/content/welcomeDodec.vZome";
                    }
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
                }
                else
                    doAction( "new-" + fProps .getProperty( "field" ), null );
            }
        }
    }
    
    /**
     * Removes the splash screen. 
     *
     * Invoke this <code>Runnable</code> using 
     * <code>EventQueue.invokeLater</code>, in order to remove the splash screen
     * in a thread-safe manner.
     */
    private static final class SplashScreenCloser implements Runnable
    {
        private final SplashScreen fSplashScreen;
        
        public SplashScreenCloser( SplashScreen splash )
        {
            fSplashScreen = splash;
        }
        
        public void run() {
            fSplashScreen .dispose();
        }
    }
    
    public void openURL( final URL url, Properties props )
    {
        logger .fine( "in openURL(), clearing initialAppLaunch" );
        this .initialAppLaunch = false;

        if ( props == null )
            props = mProperties;
        
        final boolean asTemplate = "true" .equals( props .getProperty( "as.template" ) );

        // first, determine the title and file
        String path = props .getProperty( "resource.document" );
        String title = props .getProperty( "window.title" );
        
        File file = null;
        if ( path != null ) {
            if ( title == null )
                title = path;
        } else if ( url != null ) {
            if ( url.getProtocol().equals( "file" ) ) {
                if ( asTemplate )
                    ; // leave it as an untitled doc
                else {
                    if ( title == null )
                        title = url.getPath();
                    // this is what will enable "save" rather than "save as"
                    file = new File( title );
                }
            } else if ( title == null )
                title = url.toExternalForm();
        }
        
        // now, check if a window with that title is already open
        if ( title != null ) {
            Frame frame = (Frame) mDocuments .get( title );
            if ( frame != null ) {
                frame .setVisible( true );
                return;
            }
        }
        
        // next, get the bytes from the resource, file, or URL
        InputStream bytes = null;
        if ( path != null ) 
        {
            ClassLoader cl = Thread .currentThread() .getContextClassLoader();
            bytes = cl .getResourceAsStream( path );
            if ( bytes == null )
                title = null;
        }
        else if ( url != null ) {
            try {
                bytes = url .openStream();
                props .setProperty( "url.file.name", url .getFile() );
            } catch ( IOException e ) {
                JOptionPane .showMessageDialog( null,
                        e.getMessage(),
                        "URL I/O error", JOptionPane.ERROR_MESSAGE );
                file = null;
                title = null;
            }
        }

        final Controller docController = mController .loadController( bytes, props );
        
        if ( docController == null )
            return; // unable to read the doc, don't want an empty window... error already reported
        
        final DocumentFrame frame = new DocumentFrame( this, file, docController );
        
		setInitialPosition(frame);

        final String finalTitle = title;
        final String NL = System .getProperty( "line.separator" );
        new ExclusiveAction( frame .getExcluder() )
        {
			@Override
            protected void doAction( ActionEvent e ) throws Exception
            {
            	// exceptions here will end up in showError() below
                docController .doAction( "finish.load", e );
                
                String title = finalTitle;
                String migrated = docController .getProperty( "migrated" );

                if ( ! userHasEntitlement( "model.edit" ) )
                {
                    docController .doAction( "switchToArticle", e );
                    if ( url != null )
                        title = url .toExternalForm();
                    migrated = "false";
                }

                if ( ! asTemplate && "true" .equals( migrated ) ) // a migration
                    if ( "true" .equals( mController .getProperty( "autoFormatConversion" ) ) )
                    {
                        if ( "true" .equals( mController .getProperty( "formatIsSupported" ) ) )
                            JOptionPane .showMessageDialog( frame,
                                    "This document was created by an older version." + NL + 
                                    "If you save it now, it will be converted automatically" + NL +
                                    "to the current format.  It will no longer open using" + NL +
                                    "the older version.",
                                    "Automatic Conversion", JOptionPane.INFORMATION_MESSAGE );
                        else
                        {
                            title = null;
                            frame .setFile( null );
                            JOptionPane .showMessageDialog( frame,
                                    "You have \"autoFormatConversion\" turned on," + NL + 
                                    "but the behavior is disabled until this version of vZome" + NL +
                                    "is stable.  This converted document is being opened as" + NL +
                                    "a new document.",
                                    "Automatic Conversion Disabled", JOptionPane.INFORMATION_MESSAGE );
                        }
                    }
                    else
                    {
                        title = null;
                        frame .setFile( null );
                        JOptionPane .showMessageDialog( frame,
                                "This document was created by an older version." + NL + 
                                "It is being opened as a new document, so you can" + NL +
                                "still open the original using the older version.",
                                "Outdated Format", JOptionPane.INFORMATION_MESSAGE );
                    }

                if ( title == null )
                    title = "untitled " + ++mLastUntitled;
                else
                    recordRecentDoc( title );
                
                documentFrameRenamed( null, title, frame );
            }

			@Override
            protected void showError( Exception e )
            {
                JOptionPane .showMessageDialog( frame,
                        e .getLocalizedMessage(),
                        "Error Loading Document", JOptionPane.ERROR_MESSAGE );
                documentFrameClosed( frame );
                frame .dispose();
            }
            
        } .actionPerformed( null );   
    }
    
	protected static void setInitialPosition(DocumentFrame frame) {
		// Find the screen with the largest area if this is a multi-monitor system.
		// Set the frame size to just a bit smaller than the screen
		//	so the frame will fit on the screen if the user un-maximizes it.
		// Default to opening the window as maximized on the selected (or default) monitor.
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		GraphicsDevice[] gs = ge.getScreenDevices();
		if(gs.length > 0) {
			int bestIndex = 0;
			GraphicsDevice bestDevice = gs[bestIndex];
			DisplayMode bestMode = bestDevice.getDisplayMode();
			int bestArea = bestMode.getHeight() * bestMode.getWidth();
			for (int i = bestIndex+1; i < gs.length; i++) {
				GraphicsDevice testDevice = gs[i];
				DisplayMode testMode = testDevice.getDisplayMode();
				int testArea = testMode.getHeight() * testMode.getWidth();
				if(bestArea < testArea) {
					bestArea = testArea;					
					bestMode = testMode;					
					bestDevice = testDevice;
				}
			}
			Rectangle bounds = bestDevice.getDefaultConfiguration().getBounds();
			frame.setLocation(bounds.x, bounds.y);
			int n = 15, d = n + 1; // set size to 15/16 of full screen size then maximize it
			frame.setSize(bestMode.getWidth() * n/d, bestMode.getHeight() * n/d);
		}
		frame.setExtendedState(java.awt.Frame.MAXIMIZED_BOTH);
	}
	
    public void documentFrameClosed( DocumentFrame frame )
    {
//      if ( doc .isChanged() ) {
//      // ask to save or abort
//      }
        mWindows .remove( frame );
        mDocuments .values() .remove( frame );
        if ( mDocuments .isEmpty() )
            System .exit( 0 );
    }
    
    private final static int MAX_RECENT = 2;

    private Properties userPreferences;

    private File prefsFile;

	private boolean reentrantQuit;
    
    public void recordRecentDoc( String title )
    {
        if ( mRecentDocs .contains( title ) )
            mRecentDocs .remove( title );
        mRecentDocs .addFirst( title );
        if ( mRecentDocs .size() > MAX_RECENT )
            mRecentDocs .removeLast();
        
        System .out .println( "recent:" );
        for ( Iterator it = mRecentDocs .iterator(); it .hasNext(); )
            System .out .println( it .next() );
    }
    
    public synchronized void documentFrameRenamed( String oldTitle, String newTitle, Frame frame )
    {
        Frame oldFrame = (Frame) mDocuments .get( newTitle );
        if ( oldFrame != null ) {
            // there's another window that was open on this title... changes might be lost!
            mWindows .remove( frame );
            mDocuments .remove( newTitle );
        }
        
        if ( oldTitle != null )
            mDocuments .values() .remove( frame );
        if ( newTitle != null ) {
            mDocuments .put( newTitle, frame );
            frame .setTitle( newTitle );
        }
        mWindows .add( frame );
    }

    public void openFile( File file, Properties props )
    {
        try {
            URL url = file .toURI() .toURL();
            openURL( url, props );
        } catch ( Exception e ) {
            logger .log( Level.SEVERE, "exception during openFile()", e );
            JOptionPane .showMessageDialog( null,
                    e .getMessage(),
                    "file open error", JOptionPane.ERROR_MESSAGE );
        }
    }

    public boolean quit()
    {
    	if ( this .reentrantQuit )
    		return false;
    	this .reentrantQuit = true;
        Collection<Frame> wins = new ArrayList<>( mDocuments .values() );
        for ( Iterator windows = wins .iterator(); windows .hasNext(); ) {
            DocumentFrame window = (DocumentFrame) windows .next();
            if ( ! window .closeWindow() ) {
            	this .reentrantQuit = false;
                return false;
            }
        }
    	this .reentrantQuit = false;
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
                + licensee + "\n\n"
                + "Acknowledgements:\n\n" + "Paul Hildebrandt\n" + "Marc Pelletier\n" + "David Hall\n"
                + "David Richter\n" + "Brian Hall\n" + "Dan Duddy\n" + "Fabien Vienne\n" + "George Hart\n"
                + "Edmund Harriss\n" + "Corrado Falcolini\n" + "Ezra Bradford\n" + "Chris Kling\n" + "Samuel Verbiese\n" + "Walt Venable\n"
                + "Will Ackel\n" + "Tom Darrow\n" + "Sam Vandervelde\n" + "Henri Picciotto\n" + "Florelia Braschi\n"
                + "\n" + "Dedicated to Everett Vorthmann,\n" + "who made me an engineer\n" + "\n"
                + "  Copyright 2015, Scott Vorthmann \n", "About vZome", JOptionPane.PLAIN_MESSAGE );
    }
} 
