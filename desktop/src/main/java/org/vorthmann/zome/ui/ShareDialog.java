
//(c) Copyright 2013, Scott Vorthmann.

package org.vorthmann.zome.ui;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.Collections;
import java.util.Properties;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import org.eclipse.egit.github.core.Gist;
import org.eclipse.egit.github.core.GistFile;
import org.eclipse.egit.github.core.service.GistService;
import org.vorthmann.ui.CardPanel;
import org.vorthmann.ui.Controller;
import org.vorthmann.zome.app.impl.OAuthLogin;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;

public class ShareDialog extends EscapeDialog
{
    private final Controller controller;
    private final CardPanel cardPanel;
    private final HyperlinkPanel viewUrlPanel, gistUrlPanel, rawUrlPanel;
    private final JButton uploadButton;

    private final static String VIEWER_PREFIX = "https://vzome.com/app/?url=";

    private transient String fileName;
    private transient String xml;
    private transient String token;

    public ShareDialog( Frame frame, final Controller controller )
    {
        super( frame, "Share your design", true );
        this.controller = controller;
        setLocationRelativeTo( frame );

        new JFXPanel(); // This should initialize JavaFX

        JPanel loginPanel = new JPanel();
        {
            loginPanel .setLayout( new BorderLayout() );
            
            JLabel help = new JLabel();
            help .setBorder( BorderFactory.createEmptyBorder( 15, 15, 15, 15 ) );
            help .setText( "<html>"
                            + "vZome can upload a file to Github as a <i>gist</i>, if you have a Github account."
                            + " (Accounts are free.)<br/><br/>Gists are available through public URLs, and include a <i>raw</i> URL for download,"
                            + " so they can be used to share through vZome Online."
                          +"</html>" );

            
            JPanel buttons = new JPanel();

            JButton cancel = new JButton( "Cancel" );
            cancel .addActionListener( new ActionListener()
            {
                @Override
                public void actionPerformed( ActionEvent e )
                {
                    ShareDialog.this .setVisible( false );
                }
            } );
            buttons .add( cancel );

            uploadButton = new JButton( "Upload" );
            this .getRootPane() .setDefaultButton( uploadButton );
            uploadButton .requestFocusInWindow();
            uploadButton .setActionCommand( "upload" );
            uploadButton .addActionListener( new ActionListener()
            {
                @Override
                public void actionPerformed( ActionEvent e )
                {
                    doUpload();
                }
            } );
            buttons .add( uploadButton );

            loginPanel .add( help, BorderLayout.NORTH );
            loginPanel .add( buttons, BorderLayout .SOUTH );
        }

        viewUrlPanel = new HyperlinkPanel( "View design in vZome Online", controller );
        gistUrlPanel = new HyperlinkPanel( "View gist page", controller );
        rawUrlPanel = new HyperlinkPanel( "View gist raw content", controller );
        JPanel resultsPanel = new JPanel();
        {
            JLabel help = new JLabel();
            help .setBorder( BorderFactory.createEmptyBorder( 15, 15, 15, 15 ) );
            help .setText( "<html>Your vZome file has uploaded successfully.</html>" );

            JPanel urlsPanel = new JPanel();
            urlsPanel .setLayout( new FlowLayout() );
            urlsPanel .add( viewUrlPanel );
            urlsPanel .add( gistUrlPanel );
            urlsPanel .add( rawUrlPanel );
            
            resultsPanel .setLayout( new BorderLayout() );
            resultsPanel .add( help, BorderLayout.NORTH );
            resultsPanel .add( urlsPanel, BorderLayout.CENTER );
            this .getRootPane() .setDefaultButton( viewUrlPanel .getCopyButton() );
        }

        Container content = this .getContentPane();
        content .setLayout( new BorderLayout() );

        this .cardPanel = new CardPanel();
        this .cardPanel .add( "login", loginPanel );
        this .cardPanel .add( "results", resultsPanel );
        content .add( this .cardPanel, BorderLayout.CENTER );
        
        this .setSize( new Dimension( 450, 250 ) );
        this .setResizable( false );
    }

    private void doUpload()
    {
        Properties props = new Properties();
        props .setProperty( "resourceDomain", "github.com" );
        props .setProperty( "resourcePath", "/login/oauth/authorize" );
        props .setProperty( "authnPath", "/login/oauth/access_token" );
        props .setProperty( "clientId", controller.getProperty( "githubClientId" ) );
        props .setProperty( "clientSecret", controller.getProperty( "githubClientSecret" ) );
        props .setProperty( "scope", "gist" );

        GistFile gistFile = new GistFile();
        gistFile .setContent( xml );
        Gist gist = new Gist();
        gist .setDescription( "Shared from vZome for vZome Online (https://vzome.com/app)" );
        gist .setFiles( Collections.singletonMap( fileName, gistFile ) );
        gist .setPublic( true );

        Platform.runLater( new Runnable()
        { 
            @Override
            public void run()
            {
                if ( token == null )
                    token = new OAuthLogin() .getToken( props );
                GistService service = new GistService();
                service .getClient() .setOAuth2Token( token );
                try {
                    Gist completedGist = service.createGist( gist );
                    SwingUtilities.invokeLater( new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            String gistUrl = completedGist .getHtmlUrl();
                            String rawUrl = completedGist .getFiles() .get( fileName ) .getRawUrl();
                            viewUrlPanel .setUrl( VIEWER_PREFIX + rawUrl );
                            gistUrlPanel .setUrl( gistUrl );
                            rawUrlPanel .setUrl( rawUrl );
                            cardPanel .showCard( "results" );
                            getRootPane() .setDefaultButton( viewUrlPanel .getCopyButton() );
                            viewUrlPanel .getCopyButton() .requestFocusInWindow();
                        }
                    });
                }
                catch (IOException e1)
                {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                    SwingUtilities.invokeLater( new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            cardPanel .showCard( "error" );
                        }
                    });
                }
            }
        });
    }

    @Override
    public void setVisible( boolean visible )
    {
        if ( visible ) {
            if ( token != null ) {                
                doUpload();
            } else {
                this .getRootPane() .setDefaultButton( uploadButton );
                uploadButton .requestFocusInWindow();
                cardPanel .showCard( "login" );
            }
        }
        super.setVisible( visible );
    }

    public void setFileData( String fileName, String xml )
    {
        this.fileName = fileName;
        this.xml = xml;
    }
}
