
//(c) Copyright 2013, Scott Vorthmann.

package org.vorthmann.zome.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.concurrent.ExecutionException;

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
import org.vorthmann.zome.app.impl.GitHubApi;

import com.github.scribejava.core.builder.ServiceBuilder;
import com.github.scribejava.core.model.DeviceAuthorization;
import com.github.scribejava.core.model.OAuth2AccessToken;
import com.github.scribejava.core.oauth.OAuth20Service;

public class ShareDialog extends EscapeDialog
{
    private final Controller controller;
    private final CardPanel cardPanel;
    private final HyperlinkPanel viewUrlPanel, gistUrlPanel, rawUrlPanel;
    private final JLabel codeLabel;
    private final JButton loginButton;

    private final static String VIEWER_PREFIX = "https://vzome.com/app/?url=";

    private transient String fileName, xml, deviceCode, loginUrl;

    public ShareDialog( Frame frame, final Controller controller )
    {
        super( frame, "Share your design", true );
        this.controller = controller;
        setLocationRelativeTo( frame );

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

            codeLabel = new JLabel();
            loginButton = new JButton( "Copy code and log in" );
            loginButton .setEnabled( false );
            loginButton .addActionListener( new ActionListener()
            {
                @Override
                public void actionPerformed( ActionEvent e )
                {
                    controller .setProperty( "clipboard", deviceCode );
                    try {
                        Desktop .getDesktop() .browse( new URI( loginUrl ) );
                    } catch (IOException | URISyntaxException e1) {
                        e1.printStackTrace();
                    }
                }
            });
            JPanel codePanel = new JPanel();
            codePanel .add( codeLabel );
            codePanel .add( loginButton );
            codePanel .setAlignmentX( Component.CENTER_ALIGNMENT );
            
            JButton cancel = new JButton( "Cancel" );
            cancel .addActionListener( new ActionListener()
            {
                @Override
                public void actionPerformed( ActionEvent e )
                {
                    ShareDialog.this .setVisible( false );
                }
            } );
            JPanel buttons = new JPanel();
            buttons .add( cancel );

            loginPanel .add( help, BorderLayout.NORTH );
            loginPanel .add( codePanel, BorderLayout.CENTER );
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

        this .cardPanel = new CardPanel();
        this .cardPanel .add( "login", loginPanel );
        this .cardPanel .add( "results", resultsPanel );
        this .cardPanel .showCard( "login" );
        content .add( this .cardPanel );
        
        this .setSize( new Dimension( 450, 250 ) );
        this .setResizable( false );
    }
    
    /**
     * Runs on the background thread started in setVisible, NOT the EDT.
     */
    private void getDeviceCode() throws InterruptedException, ExecutionException, IOException
    {
        String clientId = controller .getProperty( "githubClientId" );
        String clientSecret = controller .getProperty( "githubClientSecret" );
        String scope = "gist";
        
        final OAuth20Service service = new ServiceBuilder(clientId)
                .debug()
                .apiSecret( clientSecret )
                .responseType( "application/json" )
                .defaultScope( scope )
                .build( GitHubApi.instance() );
     
        final DeviceAuthorization deviceAuthorization = service .getDeviceAuthorizationCodes();
        
        deviceCode = deviceAuthorization.getUserCode();
        loginUrl = deviceAuthorization .getVerificationUriComplete();
        if ( loginUrl == null )
            loginUrl = deviceAuthorization.getVerificationUri();
        SwingUtilities .invokeLater( new Runnable()
        {
            @Override
            public void run()
            {
                codeLabel .setText( "<html><h1>" + deviceCode + "</h1><html>" );
                loginButton .setEnabled( true );
                cardPanel .showCard( "login" );
            }
        });

        // This will spin on the thread
        final OAuth2AccessToken accessToken = service.pollAccessTokenDeviceAuthorizationGrant(deviceAuthorization);
        controller .setProperty( "githubAccessToken", accessToken .getAccessToken() );
        doUpload();
    }
    
    /**
     * Runs on the background thread started in setVisible, NOT the EDT.
     */
    private void doUpload()
    {
        GistFile gistFile = new GistFile();
        gistFile .setContent( xml );
        Gist gist = new Gist();
        gist .setDescription( "Shared from vZome for vZome Online (https://vzome.com/app)" );
        gist .setFiles( Collections.singletonMap( fileName, gistFile ) );
        gist .setPublic( true );
        GistService service = new GistService();
        String token = controller .getProperty( "githubAccessToken" );
        service .getClient() .setOAuth2Token( token );
        try {
            Gist completedGist = service.createGist( gist );
            String gistUrl = completedGist .getHtmlUrl();
            String rawUrl = completedGist .getFiles() .get( fileName ) .getRawUrl();
            SwingUtilities .invokeLater( new Runnable()
            {
                @Override
                public void run()
                {
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
            e1.printStackTrace();
//            cardPanel .showCard( "error" );
        }
    }

    public void setFileData( String fileName, String xml )
    {
        this.fileName = fileName;
        this.xml = xml;
        String token = controller .getProperty( "githubAccessToken" );
        new Thread( new Runnable()
        {
            public void run()
            {
                if ( token != null ) {                
                    doUpload();
                } else {
                    try {
                        getDeviceCode();
                    } catch (InterruptedException | ExecutionException | IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }
        }).start();

        if ( token == null ) {
            this .loginButton .setEnabled( false );
            this .getRootPane() .setDefaultButton( this .loginButton );
            this .cardPanel .showCard( "login" );
        } else {
            this .cardPanel .showCard( "results" );
            getRootPane() .setDefaultButton( this .viewUrlPanel .getCopyButton() );
            this .viewUrlPanel .getCopyButton() .requestFocusInWindow();
        }
        this .setVisible( true );
    }
}
