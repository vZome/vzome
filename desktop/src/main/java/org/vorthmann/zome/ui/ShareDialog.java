
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
import org.eclipse.egit.github.core.User;
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
    private final Controller controller;
    private final CardPanel cardPanel;
    private final HyperlinkPanel viewUrlPanel, gistUrlPanel, rawUrlPanel;
    private final JLabel codeLabel;
    private final JButton loginButton;

    private final static String VIEWER_PREFIX = "https://vzome.com/app/embed.py?url=";
    private static final String REPO_NAME = "vzome-sharing";
    private static final String BRANCH_NAME = "master";

    private transient String fileName, png, xml, deviceCode, loginUrl;
    private Repository repo;
    private DataService dataService;

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
                            + "vZome can upload a file to Github repository named 'vzome-sharing', if you have a Github account."
                            + " (Accounts are free.)<br/><br/>Once vZome has created the 'vzome-sharing' repository,"
                            + " enable GitHub Pages in the Settings for the repository,"
                            + " so you uploaded files can be shared through vZome Online."
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
     * Runs on the background thread started in setFileData, NOT the EDT.
     */
    private void getDeviceCode() throws InterruptedException, ExecutionException, IOException
    {
        String clientId = controller .getProperty( "githubClientId" );
        String clientSecret = controller .getProperty( "githubClientSecret" );
        String scope = "repo";
        
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
    
    private static final String MARKDOWN_BOILERPLATE =
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
    
    /**
     * Runs on the background thread started in setVisible, NOT the EDT.
     */
    private void doUpload()
    {
        try {
            // initialize github client
            GitHubClient client = new GitHubClient();
            String token = controller .getProperty( "githubAccessToken" );
            client .setOAuth2Token( token );

            // create needed services
            RepositoryService repositoryService = new RepositoryService( client );
            CommitService commitService = new CommitService( client );
            dataService = new DataService( client );
            UserService userService = new UserService(client);
            User user = userService .getUser();
            String username = user .getLogin();

            List<Repository> repositories = repositoryService .getRepositories();
            Repository repository = repositories .stream() .filter( r -> r.getName() .equals( REPO_NAME ) ) .findFirst() .orElse( null );
            Tree baseTree = null;
            String baseCommitSha = null;
            if ( repository == null ) {
                // Create the repo the first time
                repository = new Repository() .setName( REPO_NAME ) .setDefaultBranch( BRANCH_NAME ) .setPrivate( false );
                repository = repositoryService .createRepository( repository );
            } else {
                this .repo = repository;
                // set up for a non-root commit
                baseCommitSha = repositoryService .getBranches(repository) .get(0) .getCommit() .getSha();
                RepositoryCommit baseCommit = commitService .getCommit( repository, baseCommitSha );
                String baseTreeSha = baseCommit .getSha();
                baseTree = dataService .getTree( repository, baseTreeSha );
            }

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

            String rawUrl = "https://raw.githubusercontent.com/" + username + "/" + REPO_NAME + "/master/" + vZomePath;
            String quickUrl = "https://vzome.com/app/?url=" + rawUrl;
            String slowUrl  = VIEWER_PREFIX + "https://" + username + ".github.io/" + REPO_NAME + "/" + vZomePath;
            String gitUrl   = "https://github.com/" + username + "/" + REPO_NAME + "/tree/master/" + path + "/";
            String pagesUrl = "https://" + username + ".github.io/" + REPO_NAME + "/" + path + "/";
            
            String markdown = "### " + designName + "\n\n" + MARKDOWN_BOILERPLATE + imageFileName + ")\n\n";
            markdown += "[1]: " + quickUrl + "\n";
            markdown += "[2]: " + slowUrl + "\n";
            markdown += "[3]: " + gitUrl + "\n";
            markdown += "[4]: " + pagesUrl + "\n";
            this .addFile( entries, path + "/README.md", markdown, Blob.ENCODING_UTF8 );                

            Tree newTree = dataService.createTree( repository, entries, (baseTree==null)? null : baseTree.getSha() );

            // create commit
            Commit commit = new Commit();
            commit.setMessage( "shared from vZome" );
            commit.setTree( newTree );
            
            //Due to an error with github api we have to do this
            Calendar now = Calendar.getInstance();
            String email = controller .getProperty( "githubEmail" );
            if ( email == null || "" .equals( email ) )
                email = "vZomeUser@example.com";
            CommitUser author = new CommitUser();
            author.setName( username );
            author.setEmail( email );
            author.setDate( now.getTime() );
            commit.setAuthor( author );
            commit.setCommitter( author );
            
            if ( baseCommitSha != null ) {
                List<Commit> listOfCommits = new ArrayList<Commit>();
                listOfCommits .add( new Commit() .setSha(baseCommitSha) );
                commit .setParents( listOfCommits );
            }
            Commit newCommit = dataService .createCommit( repository, commit );
            
            // create resource
            TypedResource commitResource = new TypedResource();
            commitResource .setType( TypedResource.TYPE_COMMIT );
            commitResource .setSha( newCommit .getSha() );
            commitResource .setUrl( newCommit .getUrl() );

            // get master reference and update it
            Reference reference = dataService.getReference( repository, "heads/" + BRANCH_NAME );
            reference .setObject( commitResource );
            dataService .editReference( repository, reference, true );
            
            SwingUtilities .invokeLater( new Runnable()
            {
                @Override
                public void run()
                {
                    viewUrlPanel .setUrl( VIEWER_PREFIX + rawUrl );
                    gistUrlPanel .setUrl( gitUrl );
                    rawUrlPanel .setUrl( rawUrl );
                    cardPanel .showCard( "results" );
                    getRootPane() .setDefaultButton( viewUrlPanel .getCopyButton() );
                    viewUrlPanel .getCopyButton() .requestFocusInWindow();
                }
            });
            
        } catch (Exception e) {
            // TODO: handle exception
            e .printStackTrace();
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

    public void setFileData( String fileName, String xml, String png )
    {
        this.fileName = fileName;
        this.xml = xml;
        this.png = png;
        
        if ( this.repo == null )
        {
            
        }
        else {
            
        }
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

//        if ( token == null ) {
            this .loginButton .setEnabled( false );
            this .getRootPane() .setDefaultButton( this .loginButton );
            this .cardPanel .showCard( "login" );
//        } else {
//            this .cardPanel .showCard( "results" );
//            getRootPane() .setDefaultButton( this .viewUrlPanel .getCopyButton() );
//            this .viewUrlPanel .getCopyButton() .requestFocusInWindow();
//        }
        this .setVisible( true );
    }
}
