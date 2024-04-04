
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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import org.eclipse.egit.github.core.Blob;
import org.eclipse.egit.github.core.Commit;
import org.eclipse.egit.github.core.CommitUser;
import org.eclipse.egit.github.core.Reference;
import org.eclipse.egit.github.core.Repository;
import org.eclipse.egit.github.core.RepositoryCommit;
import org.eclipse.egit.github.core.Tree;
import org.eclipse.egit.github.core.TreeEntry;
import org.eclipse.egit.github.core.TypedResource;
import org.eclipse.egit.github.core.client.GitHubClient;
import org.eclipse.egit.github.core.service.CommitService;
import org.eclipse.egit.github.core.service.DataService;
import org.eclipse.egit.github.core.service.RepositoryService;
import org.eclipse.egit.github.core.service.UserService;
import org.vorthmann.ui.CardPanel;

import com.github.scribejava.core.builder.ServiceBuilder;
import com.github.scribejava.core.model.DeviceAuthorization;
import com.github.scribejava.core.model.OAuth2AccessToken;
import com.github.scribejava.core.oauth.OAuth20Service;
import com.vzome.core.exporters.GitHubShare;
import com.vzome.desktop.api.Controller;

@SuppressWarnings("serial")
public class ShareDialog extends EscapeDialog
{
    private static final String DEFAULT_REPO_NAME = "vzome-sharing";
    private static final String DEFAULT_BRANCH_NAME = "main";

    private final Controller controller;
    private final CardPanel cardPanel;
    private final JLabel codeLabel;
    private final JButton authzButton;
    private final JLabel errorLabel;
    private final HyperlinkPanel githubUrlPanel;
       
    // Services
    private final OAuth20Service oAuthService;
    private final GitHubClient client;
    private final RepositoryService repositoryService;
    private final UserService userService;
    private final CommitService commitService;
    private final DataService dataService;

    // State markers
    private transient DeviceAuthorization deviceAuthorization;
    private transient String authToken, orgName, repoName, branchName;
    private transient Repository repo;
    private transient boolean configuring, configured;
    private transient String title, description;
    private transient String gitUrl, error; // marks success or failure state
    private transient Thread workerThread;
    
    // Inputs
    private JCheckBox generatePostCheckBox, publishCheckBox;
    private JTextField titleText;
    private JTextArea descriptionText;
    private JLabel publishLabel;

    static final Logger logger = Logger.getLogger( "org.vorthmann.zome.ui.githubsharing" );

    private transient GitHubShare shareData;

    /* %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
     * First, the code that runs on the Swing event dispatcher thread...
     *   we cannot do anything time-consuming here, such as contacting Github.
     * %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
     */
    
    public ShareDialog( Frame frame, final Controller controller )
    {
        super( frame, "Share your design", true );
        this.controller = controller;

        this .authToken = controller .getProperty( "githubAccessToken" );

        this .orgName = controller .getProperty( "githubOrgName" );

        String repoNameOverride = controller .getProperty( "githubRepoName" );
        if ( repoNameOverride == null || "".equals( repoNameOverride ) )
            repoNameOverride = DEFAULT_REPO_NAME;
        this .repoName = repoNameOverride;
        String branchNameOverride = controller .getProperty( "githubBranchName" );
        if ( branchNameOverride == null || "".equals( branchNameOverride ) )
            branchNameOverride = DEFAULT_BRANCH_NAME;
        this .branchName = branchNameOverride;
        String clientId = controller .getProperty( "githubClientId" );
        String clientSecret = controller .getProperty( "githubClientSecret" );
        String scope = "repo";
        
        // create needed services
        this .oAuthService = new ServiceBuilder( clientId )
                .debug()
                .apiSecret( clientSecret )
                .responseType( "application/json" )
                .defaultScope( scope )
                .build( GitHubApi.instance() );
        this .client = new GitHubClient();
        this .client .setOAuth2Token( this .authToken );
        this .repositoryService = new RepositoryService( client );
        this .userService = new UserService(client);
        this .commitService = new CommitService( client );
        this .dataService = new DataService( client );

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
                    workerThread .interrupt();
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

            JPanel topPanel = new JPanel( new BorderLayout() );
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
            topPanel .add( generatePostCheckBox, BorderLayout.WEST );
            topPanel .add( new JLabel( "Generate blog post" ), BorderLayout.CENTER );
            optionsPanel .add( topPanel, BorderLayout.NORTH );

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
                                    BorderFactory.createTitledBorder( "Title" ),
                                    BorderFactory.createEmptyBorder( 5,5,5,5 ) ),
                            titleText .getBorder()));

            descriptionText = new JTextArea();
            descriptionText .setLineWrap( true );
            descriptionText .setWrapStyleWord( true );
            descriptionText .setBorder(
                    BorderFactory.createCompoundBorder(
                            BorderFactory.createCompoundBorder(
                                    BorderFactory.createTitledBorder( "Description" ),
                                    BorderFactory.createEmptyBorder( 5,5,5,5 ) ),
                            descriptionText .getBorder()));

            //Put everything together.
            JPanel inputsPane = new JPanel( new BorderLayout() );
            inputsPane .add( titleText, BorderLayout.NORTH );
            inputsPane .add( descriptionText, BorderLayout.CENTER );
            inputsPane .add( cboxPanel, BorderLayout.SOUTH );
            inputsPane .setBorder( BorderFactory.createEmptyBorder( 5,5,5,5 ) );
            optionsPanel .add( inputsPane, BorderLayout.CENTER );

            JButton uploadButton = new JButton( "Upload to GitHub" );
            uploadButton .addActionListener( new ActionListener()
            {
                @Override
                public void actionPerformed( ActionEvent e )
                {
                    title = titleText .getText();
                    description = descriptionText .getText();
                    configured = true;
                    updateDialog();
                }
            });
            JButton cancel = new JButton( "Cancel" );
            cancel .addActionListener( new ActionListener()
            {
                @Override
                public void actionPerformed( ActionEvent e )
                {
                    setVisible( false );
                    workerThread .interrupt();
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
        boolean generatePost = this .controller .propertyIsTrue( "sharing-generatePost" );
        publishCheckBox .setEnabled( generatePost );
        publishLabel .setEnabled( generatePost );
        descriptionText .setEnabled( generatePost );
        titleText .setEnabled( generatePost );
    }

    private void updateDialog()
    {
        if ( this.error != null )
        {
            // the terminal failure state
            errorLabel .setText( "<html>" + this.error + "</html>" );
            this .cardPanel .showCard( "error" );
        }
        else if ( this.gitUrl != null )
        {
            // the terminal success state
            this .githubUrlPanel .setUrl( this .gitUrl );
            cardPanel .showCard( "success" );
            getRootPane() .setDefaultButton( this .githubUrlPanel .getCopyButton() );
            this .githubUrlPanel .getCopyButton() .requestFocusInWindow();
        }
        else if ( this.configured )
        {
            this .cardPanel .showCard( "upload" );
        }
        else if ( this.repo != null || this.authToken != null )
        {
            if ( ! this .configuring ) {
                this .titleText .setText( this .title );
                this .descriptionText .setText( this .description );
                this .cardPanel .showCard( "options" );
                this .generatePostCheckBox .setSelected( this .controller .propertyIsTrue( "sharing-generatePost" ) );
                this .publishCheckBox .setSelected( this .controller .propertyIsTrue( "sharing-publishImmediately" ) );
                this .configuring = true;
            }
        }
        else if ( this.deviceAuthorization != null )
        {
            String deviceCode = deviceAuthorization .getUserCode();
            String loginUrl = deviceAuthorization .getVerificationUriComplete();
            if ( loginUrl == null )
                loginUrl = deviceAuthorization .getVerificationUri();
            try {
                final URI loginUri = new URI( loginUrl );
                codeLabel .setText( "<html><h1>" + deviceCode + "</h1><html>" );
                authzButton .setEnabled( true );
                authzButton .addActionListener( new ActionListener()
                {
                    @Override
                    public void actionPerformed( ActionEvent e )
                    {
                        controller .setProperty( "clipboard", deviceCode );
                        try {
                            Desktop .getDesktop() .browse( loginUri );
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        }
                    }
                });
                cardPanel .showCard( "authz" );
            }
            catch ( URISyntaxException e2 )
            {
                e2 .printStackTrace();
                this .error = "Device authorization verification URI format incorrect: " + loginUrl;
                logger .severe( this.error );
                errorLabel .setText( "<html>" + this.error + "</html>" );
                this .cardPanel .showCard( "error" );
            }
        }
        else
        {
            this .authzButton .setEnabled( false );
            this .codeLabel .setText( "" );
            this .cardPanel .showCard( "authz" );
        }
    }

    public void startUpload( GitHubShare shareData )
    {        
        this.shareData = shareData;

        // Initialize the transient, file-specific state
        this.title = shareData .getDesignName() .replaceAll( "-", " " ) .trim();    // but we do want spaces in the title
        this.description = "A 3D design created in vZome.  Use your mouse or touch to interact.";

        this.error = null;
        this.gitUrl = null;
        this.configured = false;
        this.configuring = false;
        
        this .updateDialog();
        
        this.workerThread = new Thread( new Worker() );
        this.workerThread .start();

        this .setVisible( true );
    }

    
    
    /* %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
     *    Now, the code that runs on the worker thread...
     *      we cannot modify the UI state here.
     * %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
     */
    
    private class Worker implements Runnable
    {
        @Override
        public void run()
        {
            while ( error == null && gitUrl == null )
            {
                if ( Thread.currentThread().isInterrupted() )
                    return;
                
                if ( configured )
                {
                    doUpload();
                }
                else if ( repo != null )
                {
                    // just wait for the user
                    try {
                        Thread.sleep( 300 );
                        continue; // skip the updateDialog()
                    } catch (InterruptedException e) {
                        // no problem, will exit at the top of the loop
                    }
                }
                else if ( authToken != null && authToken != "" )
                {
                    try {
                        List<Repository> repositories = repositoryService .getRepositories();
                        if ( orgName == null )
                            repo = repositories .stream() .filter( r -> r.getName() .equals( repoName ) ) .findFirst() .orElse( null );
                        else
                            repo = repositories .stream() .filter( r -> r.getGitUrl() .contains( orgName ) )
                                .filter( r -> r.getName() .equals( repoName ) ) .findFirst() .orElse( null );
                        if ( repo == null ) {
                            error = "Unable to find repository '" + repoName + "'";
                        } else
                            logger .info( "found repo " + repo .getGitUrl() );
                    } catch ( IOException e ) {
                        error = "Unable to fetch repositories.  Your authorization may have expired or been revoked.  Try again, to reauthorize.";
                        logger .warning( error );
                        authToken = null;
                        controller .setProperty( "githubAccessToken", "" );
                    }
                }
                else if ( deviceAuthorization != null )
                {
                    try {
                        // This will spin on the thread
                        final OAuth2AccessToken accessToken = oAuthService .pollAccessTokenDeviceAuthorizationGrant( deviceAuthorization );
                        authToken = accessToken .getAccessToken();
                        controller .setProperty( "githubAccessToken", authToken );
                        client .setOAuth2Token( authToken );
                    }
                    catch ( InterruptedException | ExecutionException | IOException e ) {
                        error = "Github authorization failed.";
                        logger .severe( error );
                    }
                }
                else
                {
                    try {
                        deviceAuthorization = oAuthService .getDeviceAuthorizationCodes();
                    } catch (InterruptedException | ExecutionException | IOException e) {
                        error = "Github device code access failed.";
                        logger .severe( error );
                    }
                }
                
                // Regardless of how we got here, we must update the UI, on the EDT.
                SwingUtilities .invokeLater( new Runnable()
                {
                    @Override
                    public void run()
                    {
                        updateDialog();
                    }
                });
            }
        }
    }
    
    private void doUpload()
    {
        String username = null;
        try {
            username = userService .getUser() .getLogin();
        } catch ( IOException e1 ) {
            e1 .printStackTrace();
            this .error = "Unable to get user";
        }
        if ( this.orgName == null )
            this.orgName = username;
        
        try {            
            // set up for a non-root commit
            String baseCommitSha = repositoryService .getBranches( this .repo ) .get(0) .getCommit() .getSha();
            RepositoryCommit baseCommit = commitService .getCommit( this .repo, baseCommitSha );
            String baseTreeSha = baseCommit .getSha();

            Tree baseTree = dataService .getTree( this .repo, baseTreeSha );

            boolean blog = this .controller .propertyIsTrue( "sharing-generatePost" );
            boolean publish = this .controller .propertyIsTrue( "sharing-publishImmediately" );
            
            Collection<TreeEntry> entries = new ArrayList<TreeEntry>();
            this.shareData .setEntryHandler( (path, data, encoding) -> {
                addFile( entries, path, data, encoding );
            } );

            // Now generate the content
            this .gitUrl = this .shareData .generateContent( this.orgName, this.repoName, this.branchName, this.title, this.description, blog, publish );

            Tree newTree = dataService .createTree( this .repo, entries, (baseTree==null)? null : baseTree.getSha() );

            // create commit
            Commit commit = new Commit();
            commit.setMessage( this .title );
            commit.setTree( newTree );
            
            // Due to an error with github api we have to do this
            String email = controller .getProperty( "githubEmail" );
            if ( email == null || "" .equals( email ) )
                email = "vZomeUser@example.com";
            CommitUser author = new CommitUser();
            author .setName( username );
            author .setEmail( email );
            author .setDate( Calendar .getInstance() .getTime() );
            commit .setAuthor( author );
            commit .setCommitter( author );
            
            if ( baseCommitSha != null ) {
                List<Commit> listOfCommits = new ArrayList<Commit>();
                listOfCommits .add( new Commit() .setSha( baseCommitSha ) );
                commit .setParents( listOfCommits );
            }
            Commit newCommit = dataService .createCommit( this .repo, commit );
            
            // create resource
            TypedResource commitResource = new TypedResource();
            commitResource .setType( TypedResource.TYPE_COMMIT );
            commitResource .setSha( newCommit .getSha() );
            commitResource .setUrl( newCommit .getUrl() );

            // get main reference and update it
            Reference reference = dataService .getReference( this .repo, "heads/" + this.branchName );
            reference .setObject( commitResource );
            dataService .editReference( this .repo, reference, false );
            controller .setProperty( "clipboard", gitUrl );
        }
        catch (Exception e) {
            this .error = "Unable to upload files to the repository: " + e .getMessage();
            logger .severe( this.error );
        }
    }
    
    private void addFile( Collection<TreeEntry> entries, String path, String content, String encoding ) throws IOException
    {
        // create new blob with data
        Blob blob = new Blob();
        blob.setContent( content ) .setEncoding( encoding );
        String blobSha = this.dataService .createBlob( this.repo, blob );

        // create new tree entry
        TreeEntry treeEntry = new TreeEntry();
        treeEntry .setPath( path );
        treeEntry .setMode( TreeEntry.MODE_BLOB );
        treeEntry .setType( TreeEntry.TYPE_BLOB );
        treeEntry .setSha( blobSha );
        treeEntry .setSize( blob.getContent().length() );

        entries.add( treeEntry );
    }
}
