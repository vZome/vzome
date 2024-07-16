
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
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import org.vorthmann.ui.CardPanel;

import com.vzome.desktop.api.Controller;

@SuppressWarnings("serial")
public class ShareDialog extends EscapeDialog implements PropertyChangeListener
{
    private final Controller controller;
    private final CardPanel cardPanel;
    private final JLabel codeLabel;
    private final JButton authzButton;
    private final JLabel errorLabel;
    private final HyperlinkPanel githubUrlPanel;
       
    // Inputs
    private JCheckBox showScenesCheckBox, generatePostCheckBox, publishCheckBox;
    private JComboBox<String> stylesMenu;
    private JTextField titleText;
    private JTextArea descriptionText;
    private JLabel publishLabel, stylesLabel;
    private JButton uploadButton;
    
    public ShareDialog( Frame frame, final Controller controller )
    {
        super( frame, "Share your design", true );
        this.controller = controller;
        controller .addPropertyListener( this );

        JPanel loginPanel = new JPanel();
        {
            loginPanel .setLayout( new BorderLayout() );
            
            JLabel help = new JLabel();
            help .setBorder( BorderFactory.createEmptyBorder( 15, 15, 15, 15 ) );
            help .setText( "<html>"
                            + "vZome can upload a file to Github repository named 'vzome-sharing', if you have a Github account."
                            + " (Accounts are free.)<br/><br/>Once you have created the 'vzome-sharing' repository,"
                            + " enable GitHub Pages in the settings for the repository,"
                            + " so you can share the generated web pages for your uploaded designs."
                          +"</html>" );

            codeLabel = new JLabel();
            authzButton = new JButton( "Copy code and authorize" );
            authzButton .setEnabled( false );
            JPanel codePanel = new JPanel();
            codePanel .add( codeLabel );
            codePanel .add( authzButton );
            codePanel .setAlignmentX( Component.CENTER_ALIGNMENT );
            
            JButton cancel = new JButton( "Cancel" );
            cancel .addActionListener( new ActionListener()
            {
                @Override
                public void actionPerformed( ActionEvent e )
                {
                    setVisible( false );
                    controller .actionPerformed( null, "abort" );
                }
            } );
            JPanel buttons = new JPanel();
            buttons .add( cancel );

            loginPanel .add( help, BorderLayout.NORTH );
            loginPanel .add( codePanel, BorderLayout.CENTER );
            loginPanel .add( buttons, BorderLayout .SOUTH );
        }

        JPanel optionsPanel = new JPanel();
        {
            optionsPanel .setLayout( new BorderLayout() );
            optionsPanel .setBorder( BorderFactory.createEmptyBorder( 15, 15, 15, 15 ) );
            
            JPanel scenesPanel = new JPanel( new BorderLayout() );
            showScenesCheckBox = new JCheckBox();
            showScenesCheckBox .addActionListener( new ActionListener()
            {
                @Override
                public void actionPerformed( ActionEvent e )
                {
                    boolean showScenes = controller .propertyIsTrue( "sharing-showScenes" );
                    controller .setProperty( "sharing-showScenes", !showScenes );
                    enableConfigurations();
                }
            });
            scenesPanel .add( showScenesCheckBox, BorderLayout.WEST );
            scenesPanel .add( new JLabel( "Show scenes" ), BorderLayout.CENTER );
            {
                JPanel sceneStylesPanel = new JPanel();
                sceneStylesPanel .setToolTipText( "Select an interaction style for scenes." );
                stylesLabel = new JLabel( "Style" );
                sceneStylesPanel .add( stylesLabel );
                {
                    String[] styleNames= new String[] { "indexed", "indexed (load-camera)", "menu", "zometool", "javascript" };
                    String defaultStyle = controller .getProperty( "sharing-sceneStyle" );
                    stylesMenu = new JComboBox<String>( styleNames );
                    stylesMenu .setSelectedItem( defaultStyle );
                    stylesMenu .setMaximumRowCount( 5 );
                    stylesMenu .addActionListener( new ActionListener()
                    {
                        @Override
                        public void actionPerformed( ActionEvent e )
                        {
                            JComboBox<?> combo = (JComboBox<?>) e.getSource();
                            controller .setProperty ("sharing-sceneStyle", combo .getSelectedItem().toString() );
                        }
                    } );
                    sceneStylesPanel .add( stylesMenu );
                }
                scenesPanel .add( sceneStylesPanel, BorderLayout.EAST );
            }
            optionsPanel .add( scenesPanel, BorderLayout.NORTH );

            JPanel postPanel = new JPanel( new BorderLayout() );
            {
                JPanel generatePostPanel = new JPanel( new BorderLayout() );
                generatePostCheckBox = new JCheckBox();
                generatePostCheckBox .addActionListener( new ActionListener()
                {
                    @Override
                    public void actionPerformed( ActionEvent e )
                    {
                        boolean generatePost = controller .propertyIsTrue( "sharing-generatePost" );
                        controller .setProperty( "sharing-generatePost", !generatePost );
                        enableConfigurations();
                    }
                });
                generatePostPanel .add( generatePostCheckBox, BorderLayout.WEST );
                generatePostPanel .add( new JLabel( "Generate blog post" ), BorderLayout.CENTER );
                postPanel .add( generatePostPanel, BorderLayout.NORTH );

                publishCheckBox = new JCheckBox();
                publishCheckBox .addActionListener( new ActionListener()
                {
                    @Override
                    public void actionPerformed( ActionEvent e )
                    {
                        boolean publish = controller .propertyIsTrue( "sharing-publishImmediately" );
                        controller .setProperty( "sharing-publishImmediately", !publish );
                    }
                });
                JPanel cboxPanel = new JPanel( new BorderLayout() );
                cboxPanel .add( publishCheckBox, BorderLayout.WEST );
                publishLabel = new JLabel( "Publish immediately" );
                cboxPanel .add( publishLabel, BorderLayout.CENTER );

                titleText = new JTextField( 0 );
                titleText .setBorder(
                        BorderFactory.createCompoundBorder(
                                BorderFactory.createCompoundBorder(
                                        BorderFactory.createTitledBorder( "title" ),
                                        BorderFactory.createEmptyBorder( 5,5,5,5 ) ),
                                titleText .getBorder()));

                descriptionText = new JTextArea();
                descriptionText .setLineWrap( true );
                descriptionText .setWrapStyleWord( true );
                descriptionText .setBorder(
                        BorderFactory.createCompoundBorder(
                                BorderFactory.createCompoundBorder(
                                        BorderFactory.createTitledBorder( "description" ),
                                        BorderFactory.createEmptyBorder( 5,5,5,5 ) ),
                                descriptionText .getBorder()));

                //Put everything together.
                JPanel inputsPane = new JPanel( new BorderLayout() );
                inputsPane .add( titleText, BorderLayout.NORTH );
                inputsPane .add( descriptionText, BorderLayout.CENTER );
                inputsPane .add( cboxPanel, BorderLayout.SOUTH );
                inputsPane .setBorder( BorderFactory.createEmptyBorder( 5,5,5,5 ) );
                postPanel .add( inputsPane, BorderLayout.CENTER );
            }
            optionsPanel .add( postPanel, BorderLayout.CENTER );

            uploadButton = new JButton( "Upload to GitHub" );
            uploadButton .addActionListener( new ActionListener()
            {
                @Override
                public void actionPerformed( ActionEvent e )
                {
                    controller .setProperty( "title", titleText .getText() );
                    controller .setProperty( "description", descriptionText .getText() );
                    controller .actionPerformed( null, "doUpload" );
                    updateDialog( "upload" );
                }
            });
            JButton cancel = new JButton( "Cancel" );
            cancel .addActionListener( new ActionListener()
            {
                @Override
                public void actionPerformed( ActionEvent e )
                {
                    setVisible( false );
                    controller .actionPerformed( null, "abort" );
                }
            } );
            JPanel buttons = new JPanel();
            buttons .add( cancel );
            buttons .add( uploadButton );
            optionsPanel .add( buttons, BorderLayout.SOUTH );
            
            enableConfigurations();
        }

        JPanel uploadPanel = new JPanel();
        {
            JLabel help = new JLabel();
            help .setBorder( BorderFactory.createEmptyBorder( 15, 15, 15, 15 ) );
            help .setText( "<html>Your vZome design is uploading to GitHub...</html>" );
            
            uploadPanel .setLayout( new BorderLayout() );
            uploadPanel .add( help, BorderLayout.CENTER );
            
//            JProgressBar progressBar = new JProgressBar();
//            progressBar .setIndeterminate( true );
//            uploadPanel .add( progressBar, BorderLayout.CENTER );
        }
        
        JPanel resultsPanel = new JPanel();
        {
            JLabel help = new JLabel();
            help .setBorder( BorderFactory.createEmptyBorder( 15, 15, 15, 15 ) );
            help .setText( "<html>Your vZome file has uploaded successfully, and the URL below is copied to the clipboard.</html>" );
            this .githubUrlPanel = new HyperlinkPanel( "View GitHub Folder", controller, false );
            JPanel linksPanel = new JPanel();
            linksPanel .setLayout( new FlowLayout() );
            linksPanel .add( this .githubUrlPanel );
            resultsPanel .setLayout( new BorderLayout() );
            resultsPanel .add( help, BorderLayout.NORTH );
            resultsPanel .add( linksPanel, BorderLayout.CENTER );
            this .getRootPane() .setDefaultButton( this .githubUrlPanel .getCopyButton() );
        }

        JPanel errorPanel = new JPanel();
        {
            errorLabel = new JLabel();
            errorLabel .setBorder( BorderFactory.createEmptyBorder( 15, 15, 15, 15 ) );
            
            errorPanel .setLayout( new BorderLayout() );
            errorPanel .add( errorLabel, BorderLayout.CENTER );
        }

        Container content = this .getContentPane();

        this .cardPanel = new CardPanel();
        this .cardPanel .add( "authz",   loginPanel );
        this .cardPanel .add( "options", optionsPanel );
        this .cardPanel .add( "upload",  uploadPanel );
        this .cardPanel .add( "success", resultsPanel );
        this .cardPanel .add( "error",   errorPanel );
        this .cardPanel .showCard( "authz" );
        content .add( this .cardPanel );
        
        this .setSize( new Dimension( 500, 300 ) );
        this .setResizable( false );
        this .setLocationRelativeTo( frame );
    }
    
    protected void enableConfigurations()
    {
        boolean showScenes = this .controller .propertyIsTrue( "sharing-showScenes" );
        stylesLabel .setEnabled( showScenes );
        stylesMenu .setEnabled( showScenes );

        boolean generatePost = this .controller .propertyIsTrue( "sharing-generatePost" );
        publishCheckBox .setEnabled( generatePost );
        publishLabel .setEnabled( generatePost );
        descriptionText .setEnabled( generatePost );
        titleText .setEnabled( generatePost );
    }

    @Override
    public void propertyChange( PropertyChangeEvent pce )
    {
        if ( "stage" .equals( pce .getPropertyName() ) ) {
            String stage = (String) pce .getNewValue();
            // This listener is invoked on the worker thread, and we need to get on the EDT
            SwingUtilities .invokeLater( new Runnable()
            {
                @Override
                public void run()
                {
                    updateDialog( stage );
                }
            });
        }
    }


    private void updateDialog( String stage )
    {
        switch (stage) {
        
        case "authz": {
            this .authzButton .setEnabled( false );
            this .codeLabel .setText( "" );
            break;
        }
        
        case "deviceCode": {
            String deviceCode = this.controller .getProperty( "deviceCode" );
            this.controller .setProperty( "clipboard", deviceCode );
            String loginUri = controller .getProperty( "loginURI" );
            codeLabel .setText( "<html><h1>" + deviceCode + "</h1><html>" );
            authzButton .setEnabled( true );
            authzButton .addActionListener( new ActionListener()
            {
                @Override
                public void actionPerformed( ActionEvent e )
                {
                    try {
                        Desktop .getDesktop() .browse( new URI( loginUri ) );
                    } catch (IOException e1) {
                        e1.printStackTrace();
                        errorLabel .setText( "<html>" + "IO exception launching browser with " + loginUri + "</html>" );
                        cardPanel .showCard( "error" );
                    } catch ( URISyntaxException e1 ) {
                        e1.printStackTrace();
                        errorLabel .setText( "<html>" + "Device authorization verification URI format incorrect: " + loginUri + "</html>" );
                        cardPanel .showCard( "error" );
                    }
                }
            });
            break;
        }

        case "repo": {
            break;
        }

        case "options": {
            this .titleText .setText( this .controller .getProperty( "title" ) );
            this .descriptionText .setText( this .controller .getProperty( "description" ) );
            this .generatePostCheckBox .setSelected( this .controller .propertyIsTrue( "sharing-generatePost" ) );
            this .showScenesCheckBox .setSelected( this .controller .propertyIsTrue( "sharing-showScenes" ) );
            this .publishCheckBox .setSelected( this .controller .propertyIsTrue( "sharing-publishImmediately" ) );
            break;
        }

        case "success": {
            // the terminal success state
            this .githubUrlPanel .setUrl( this.controller .getProperty( "gitUrl" ) );
            getRootPane() .setDefaultButton( this .githubUrlPanel .getCopyButton() );
            this .githubUrlPanel .getCopyButton() .requestFocusInWindow();
            break;
        }

        case "error": {
            // the terminal failure state
            errorLabel .setText( "<html>" + this.controller .getProperty( "error" ) + "</html>" );
            break;
        }

        default:
            System.out.println( "Unknown stage in ShareDialog: " + stage );
        };
        
        this .cardPanel .showCard( stage );
        this .setVisible( true );
    }
}
