
package org.vorthmann.zome.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
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
import org.vorthmann.ui.Controller;
import org.vorthmann.zome.app.impl.GitHubApi;

import com.github.scribejava.core.builder.ServiceBuilder;
import com.github.scribejava.core.model.DeviceAuthorization;
import com.github.scribejava.core.model.OAuth2AccessToken;
import com.github.scribejava.core.oauth.OAuth20Service;

public class ShareDialog extends EscapeDialog
{
    private static final String VIEWER_PREFIX = "https://vzome.com/app/embed.py?url=";
    private static final String REPO_NAME = "vzome-sharing";
    private static final String BRANCH_NAME = "main";

    private final Controller controller;
    private final CardPanel cardPanel;
    private final JLabel codeLabel;
    private final JButton authzButton;
    private final JLabel errorLabel;
   
    // Services
    private final OAuth20Service oAuthService;
    private final GitHubClient client;
    private final RepositoryService repositoryService;
    private final UserService userService;
    private final CommitService commitService;
    private final DataService dataService;

    // State markers
    private transient DeviceAuthorization deviceAuthorization;
    private transient String authToken;
    private transient Repository repo;
    private transient String gitUrl, error; // marks success or failure state
    
    // Inputs
    private transient String fileName, png, xml;
    
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

        setLocationRelativeTo( frame );

        JPanel loginPanel = new JPanel();
        {
            loginPanel .setLayout( new BorderLayout() );
            
            JLabel help = new JLabel();
            help .setBorder( BorderFactory.createEmptyBorder( 15, 15, 15, 15 ) );
            help .setText( "<html>"
                            + "vZome can upload a file to Github repository named 'vzome-sharing', if you have a Github account."
                            + " (Accounts are free.)<br/><br/>Once you have created the 'vzome-sharing' repository,"
                            + " enable GitHub Pages in the settings for the repository,"
                            + " so your uploaded files can be shared through vZome Online."
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
                }
            } );
            JPanel buttons = new JPanel();
            buttons .add( cancel );

            loginPanel .add( help, BorderLayout.NORTH );
            loginPanel .add( codePanel, BorderLayout.CENTER );
            loginPanel .add( buttons, BorderLayout .SOUTH );
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

        JPanel errorPanel = new JPanel();
        {
            errorLabel = new JLabel();
            errorLabel .setBorder( BorderFactory.createEmptyBorder( 15, 15, 15, 15 ) );
            
            errorPanel .setLayout( new BorderLayout() );
            errorPanel .add( errorLabel, BorderLayout.CENTER );
        }

        Container content = this .getContentPane();

        this .cardPanel = new CardPanel();
        this .cardPanel .add( "authz",  loginPanel );
        this .cardPanel .add( "upload", uploadPanel );
        this .cardPanel .add( "error",  errorPanel );
        this .cardPanel .showCard( "authz" );
        content .add( this .cardPanel );
        
        this .setSize( new Dimension( 450, 250 ) );
        this .setResizable( false );
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
            try {
                setVisible( false );
                Desktop .getDesktop() .browse( new URI( this.gitUrl ) );
            } catch (IOException | URISyntaxException e1) {
                e1.printStackTrace();
            }
        }
        else if ( this.repo != null || this.authToken != null )
        {
            this .cardPanel .showCard( "upload" );
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

    public void startUpload( String fileName, String xml, String png )
    {
        this.fileName = fileName;
        this.xml = xml;
        this.png = png;
        
        // Initialize the transient, file-specific state
        this.error = null;
        this.gitUrl = null;
        
        this .updateDialog();
        
        new Thread( new Worker() ).start();  // TODO kill this thread if the user cancels

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
                if ( repo != null )
                {
                    doUpload();
                }
                else if ( authToken != null && authToken != "" )
                {
                    try {
                        List<Repository> repositories = repositoryService .getRepositories();
                        repo = repositories .stream() .filter( r -> r.getName() .equals( REPO_NAME ) ) .findFirst() .orElse( null );
                        if ( repo == null ) {
                            error = "Unable to find repository '" + REPO_NAME + "'";
                        }
                    } catch ( IOException e ) {
                        e .printStackTrace();
                        error = "Unable to fetch repositories.  Your authorization may have expired or been revoked.  Try again, to reauthorize.";
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
                        e .printStackTrace();
                        error = "Github authorization failed.";
                    }
                }
                else
                {
                    try {
                        deviceAuthorization = oAuthService .getDeviceAuthorizationCodes();
                    } catch (InterruptedException | ExecutionException | IOException e) {
                        e .printStackTrace();
                        error = "Github device code access failed.";
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
    
    private static final String MARKDOWN_BOILERPLATE =
        "Your vZome design has successfully uploaded to GitHub.  You may now share it using the links below.\n\n" +
        "[vZome Online (immediate)][1] - this link works as soon as vZome has pushed the files to Github; it can be shared immediately, but it will not show a preview image when auto-expanded in Twitter, Discord, Facebook, etc.\n" + 
        "\n" + 
        "[vZome Online (embeddable)][2] - this link may not work initially, since Github Pages may not be ready for a few minutes.  Once ready, this link is *great* for sharing on social media, since it will display the design title and a preview image.  Note that Github Pages only supports 20 updates in an hour, so you may have to wait up to an hour if you have shared more than 20 designs quickly.\n" + 
        "\n" + 
        "[Github Source][3] - view this content as source in Github.  Feel free to edit `README.md` (this content), if you want to modify and share the page.\n" + 
        "\n" + 
        "[Github Pages][4] - view this content as rendered in Github Pages.  Remember there may be a delay before changes appear here; see above.\n" + 
        "\n" + 
        "![Image]("
        ;

    private void doUpload()
    {
        String username = null;
        try {
            username = userService .getUser() .getLogin();
        } catch ( IOException e1 ) {
            e1 .printStackTrace();
            this .error = "Unable to get user";
        }
        try {            
            // set up for a non-root commit
            String baseCommitSha = repositoryService .getBranches( this .repo ) .get(0) .getCommit() .getSha();
            RepositoryCommit baseCommit = commitService .getCommit( this .repo, baseCommitSha );
            String baseTreeSha = baseCommit .getSha();

            Tree baseTree = dataService .getTree( this .repo, baseTreeSha );

            // prepare the timestamp path
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern( "yyyy/MM/dd/HH-mm-ss" );
            String path = formatter .format( LocalDateTime.now() );
            String vZomePath = path + "/" + URLEncoder.encode( fileName, StandardCharsets.UTF_8.toString() );

            Collection<TreeEntry> entries = new ArrayList<TreeEntry>();

            this .addFile( entries, vZomePath, this.xml, Blob.ENCODING_UTF8 );
            
            String designName = this .fileName;
            int index = designName .toLowerCase() .lastIndexOf( ".vZome" .toLowerCase() );
            if ( index > 0 )
                designName = designName .substring( 0, index );
            String imageFileName = designName + ".png";
            this .addFile( entries, path + "/" + imageFileName, png, Blob.ENCODING_BASE64 );

            String rawUrl = "https://raw.githubusercontent.com/" + username + "/" + REPO_NAME + "/" + BRANCH_NAME + "/" + vZomePath;
            String quickUrl = "https://vzome.com/app/?url=" + rawUrl;
            String slowUrl  = VIEWER_PREFIX + "https://" + username + ".github.io/" + REPO_NAME + "/" + vZomePath;
            String gitUrl   = "https://github.com/" + username + "/" + REPO_NAME + "/tree/" + BRANCH_NAME + "/" + path + "/";
            String pagesUrl = "https://" + username + ".github.io/" + REPO_NAME + "/" + path + "/";
            
            String markdown = "### " + designName + "\n\n" + MARKDOWN_BOILERPLATE + imageFileName + ")\n\n";
            markdown += "[1]: " + quickUrl + "\n";
            markdown += "[2]: " + slowUrl + "\n";
            markdown += "[3]: " + gitUrl + "\n";
            markdown += "[4]: " + pagesUrl + "\n";
            this .addFile( entries, path + "/README.md", markdown, Blob.ENCODING_UTF8 );                

            Tree newTree = dataService .createTree( this .repo, entries, (baseTree==null)? null : baseTree.getSha() );

            // create commit
            Commit commit = new Commit();
            commit.setMessage( "shared from vZome" );
            commit.setTree( newTree );
            
            // Due to an error with github api we have to do this
            Calendar now = Calendar.getInstance();
            String email = controller .getProperty( "githubEmail" );
            if ( email == null || "" .equals( email ) )
                email = "vZomeUser@example.com";
            CommitUser author = new CommitUser();
            author .setName( username );
            author .setEmail( email );
            author .setDate( now.getTime() );
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
            Reference reference = dataService .getReference( this .repo, "heads/" + BRANCH_NAME );
            reference .setObject( commitResource );
            dataService .editReference( this .repo, reference, true );
            this .gitUrl = gitUrl;
        }
        catch (Exception e) {
            e .printStackTrace();
            this .error = "Unable to upload files to the repository: " + e .getMessage();
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
