

package org.vorthmann.zome.ui;

import java.awt.EventQueue;
import java.awt.Frame;
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
import org.vorthmann.ui.LoggingErrorChannel;
import org.vorthmann.ui.SplashScreen;


public final class ApplicationUI extends DefaultController implements Platform.UI 
{
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

    public String getProperty( String string )
    {
        return mProperties .getProperty( string );
    }

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

    private int mLastUntitled = 0, mLastOffset = 0;
    
    private final List mWindows = new ArrayList();
    
    private final Map mDocuments = new HashMap(); // keyed by URL
    
    private final LinkedList mRecentDocs = new LinkedList();
    
//    private final JFrame mColorsFrame;
    
    private final Properties mProperties;
    
    private boolean initialAppLaunch = true;
        
//    private final ColorPanel mColorPanel;
    
    private ApplicationController mController;

    private Logger logger = Logger .getLogger( "org.vorthmann.zome.ui.ApplicationUI" );

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
        
        mController = controller;
        mController .setNextController( this );

        controller .initialize( mProperties );

//        final JFrame colorsFrame = new JFrame( "Colors" );
//        mColorPanel = new ColorPanel( mController .getSubController( "colors" ) );
//        colorsFrame .setContentPane( mColorPanel );
//        colorsFrame .pack();
//        mColorsFrame = colorsFrame;
    }

    public static void main( String[] args )
    {
        try {
            String prop = System .getProperty( "user.dir" );
            File workingDir = new File( prop );
            URL codebase = workingDir .toURI() .toURL();
            initialize( args, codebase );
        } catch ( Throwable e ) {
            Logger .getLogger( "org.vorthmann.zome.ui.ApplicationUI" )
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
        File logsFolder = Platform .logsFolder();
        logsFolder .mkdir();
        Handler fh = null;
        try {
            fh = new FileHandler( "%h/" + Platform .logsPath() + "/vZomeLog%g.log", 200000, 12 );
        } catch ( Exception e1 ) {
        	rootLogger .log( Level.WARNING, "unable to set up vZome file log handler", e1 );
            try {
                fh = new FileHandler();
            } catch ( Exception e2 ) {
            	rootLogger .log( Level.WARNING, "unable to set up default file log handler", e1 );
            }        
        }
        if ( fh != null )
        {
            fh .setFormatter( new SimpleFormatter() );
            rootLogger .addHandler( fh );
        }
        
        final Logger logger = Logger .getLogger( "org.vorthmann.zome.ui.ApplicationUI" );
        
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
        
        if ( props .containsKey( "checkAllFiles" ) )
        {
            URL url = null;
            if ( urlStr == null )
                throw new IllegalArgumentException( "no URL argument provided with fileChecker argument" );
            
            try {
                url = new URL( codebase, urlStr );
            } catch ( MalformedURLException e ) {
                logger .log( Level.SEVERE, "bad URL for fileChecker: " + urlStr, e );
                System .exit( 0 );
            }
            if ( url .getProtocol() != "file" ) {
                logger .severe( "non-file URL for fileChecker: " + urlStr );
                System .exit( 0 );
            }
            
            controller .setErrorChannel( new LoggingErrorChannel( logger ) );
            controller .initialize( props );

            File fileToCheck = new File( url .getFile() );
            try {
                controller .doFileAction( "checkAllFiles", fileToCheck );
            } catch ( Exception e ) {
                logger .log( Level.SEVERE, "exception from checkAllFiles action", e );
                System .exit( 0 );
            }
            return null;
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
                    errorCode = "internal error, see vZomeLog0.log in your home directory";
                } else {
                    logger.log( Level.WARNING, "reporting error: " + errorCode, arguments );
                    ; // TODO use resources
                }
                    ; // TODO use resources
                JOptionPane .showMessageDialog( null, errorCode,
                        "Application Error", JOptionPane.ERROR_MESSAGE );
            }

            public void clearError()
            {}
        };
        controller .setErrorChannel( appErrors );
        appUI .setErrorChannel( appErrors );

        // doing this after to make sure that openURL is ready to work...
        //   presumably the listener mechanisms buffer the events long enough
        logger .fine( "setting up Platform event listeners" );
        Platform .setupEventListener( appUI );

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
        
        frame .setLocation( 20*mLastOffset, 20*mLastOffset );
        mLastOffset = (++mLastOffset) % 10;
        
        final String finalTitle = title;
        final String NL = System .getProperty( "line.separator" );
        new ExclusiveAction( frame .getExcluder() )
        {
            protected void doAction( ActionEvent e ) throws Exception
            {
            	// exceptions here will end up in showError() below
                docController .doAction( "finish.load", e );
                
                String title = finalTitle;
                String edited = docController .getProperty( "edited" );

                if ( ! userHasEntitlement( "model.edit" ) )
                {
                    docController .doAction( "switchToArticle", e );
                    if ( url != null )
                        title = url .toExternalForm();
                    edited = "false";
                }

                if ( ! asTemplate && "true" .equals( edited ) ) // a migration
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

    /**
     * This is implementing Platform.UI, for Apple "open document" callback
     */
    public void openFile( File file )
    {
        logger .fine( "in openFile()" );
        openFile( file, new Properties() );
    }

    private void openFile( File file, Properties props )
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
        Collection wins = new ArrayList( mDocuments .values() );
        for ( Iterator windows = wins .iterator(); windows .hasNext(); ) {
            DocumentFrame window = (DocumentFrame) windows .next();
            if ( ! window .closeWindow() )
                return false;
        }
        return true;
    }

    /**
     * Single execution path for command-line argument interpretation.
     * May be called by Java Web Start SingleInstanceService.
     */
    public void newActivation( String[] args, URL codebase )
    {
        logger .fine( "in newActivation()" );
        Properties props = new Properties();
        try {
            URL url = null;
            for ( int i = 0; i < args.length; i++ ) {
                if ( args[i] .equals( "-field" ) )
                    ++i;
                else if ( args[i] .equals( "-open" ) )
                {
                    // I believe this is how Windows handles a double-click
                    String fileName = args[++i];
                    url = new File( fileName ) .toURI() .toURL();
                }
                else if ( args[i] .startsWith( "-" ) ) {
                    String propName = args[i++] .substring( 1 );
                    String propValue = args[i];
                    props .setProperty( propName, propValue );
                }
                else
                    url = new URL( codebase, args[i] );
            }
            if ( url != null )
                System .out .println( "opening " + url .toString() );
            openURL( url, props );
        } catch ( Exception e ) {
            logger .log( Level.SEVERE, "exception during newActivation()", e );
            JOptionPane .showMessageDialog( null,
                    e .getMessage(),
                    "file open error", JOptionPane.ERROR_MESSAGE );
        }
    }

    public void about()
    {
        String licensee = mController .getProperty( "licensed.user" );
        if ( licensee == null )
            licensee = "unlicensed";
        else
            licensee = "licensed to " + licensee;
        JOptionPane.showMessageDialog( null, mController.getProperty( "edition" ) + " " + mController.getProperty( "version" ) + "\n\n"
                + "by Scott Vorthmann\n\n"
                + licensee + "\n\n"
                + "Acknowledgements:\n\n" + "Paul Hildebrandt\n" + "Marc Pelletier\n"
                + "David Richter\n" + "Brian Hall\n" + "Dan Duddy\n" + "Fabien Vienne\n" + "George Hart\n"
                + "Edmund Harriss\n" + "Corrado Falcolini\n" + "Ezra Bradford\n" + "Chris Kling\n" + "Samuel Verbiese\n" + "Walt Venable\n"
                + "Will Ackel\n" + "Tom Darrow\n" + "Sam Vandervelde\n" + "Henri Picciotto\n" + "Florelia Braschi\n"
                + "\n" + "Dedicated to Everett Vorthmann,\n" + "who made me an engineer\n" + "\n"
                + "  Copyright 2011, Scott Vorthmann \n", "About vZome", JOptionPane.PLAIN_MESSAGE );
    }
    
//    public void showColorDialog( String colorName, ListSelectionListener listener )
//    {
//        mColorPanel .setup( colorName, listener );
//        mColorsFrame .setVisible( true );
//    }
//
//    public String pickCustomColor( String colorName )
//    {
////        java.awt.Color color = mColorPanel .toAwtColor( colorName );
////        color = JColorChooser .showDialog( mColorsFrame,
////            "Choose Color", color );
////        if ( color != null ) {
////            colorName = Colors.RGB_CUSTOM + " " + color.getRed() + " " + color.getGreen() + " " + color.getBlue();
////            mColorPanel .setColor( colorName, color );
////        }
//        return colorName;
//    }
} 




