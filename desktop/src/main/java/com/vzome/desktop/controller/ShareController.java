package com.vzome.desktop.controller;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileTime;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;

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

import com.github.scribejava.core.builder.ServiceBuilder;
import com.github.scribejava.core.exceptions.OAuthException;
import com.github.scribejava.core.model.DeviceAuthorization;
import com.github.scribejava.core.model.OAuth2AccessToken;
import com.github.scribejava.core.oauth.OAuth20Service;
import com.vzome.core.exporters.GitHubShare;


public class ShareController extends DefaultController
{
    private static final String DEFAULT_REPO_NAME = "vzome-sharing";
    private static final String DEFAULT_BRANCH_NAME = "main";

    static final Logger logger = Logger.getLogger( "org.vorthmann.zome.ui.githubsharing" );

    private OAuth20Service oAuthService;
    private GitHubClient client;
    private RepositoryService repositoryService;
    private UserService userService;
    private CommitService commitService;
    private DataService dataService;
    private DeviceAuthorization deviceAuthorization;
    private URI loginUri;
    private String deviceCode;
    private String authToken;

    private String orgName;
    private String repoName;
    private String branchName;
    private Repository repo;
    
    private boolean configured;
    private String title;
    private String description;
    private String gitUrl;
    private String error;

    private Thread workerThread;
    private GitHubShare shareData;
        
    public void startShare()
    {
        this .authToken = super .getProperty( "githubAccessToken" );

        this .orgName = super .getProperty( "githubOrgName" );

        String repoNameOverride = super .getProperty( "githubRepoName" );
        if ( repoNameOverride == null || "".equals( repoNameOverride ) )
            repoNameOverride = DEFAULT_REPO_NAME;
        this .repoName = repoNameOverride;
        String branchNameOverride = super .getProperty( "githubBranchName" );
        if ( branchNameOverride == null || "".equals( branchNameOverride ) )
            branchNameOverride = DEFAULT_BRANCH_NAME;
        this .branchName = branchNameOverride;        

        if ( this .dataService == null )
            try {
                String clientId = super .getProperty( "githubClientId" );
                String clientSecret = super .getProperty( "githubClientSecret" );
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
            } catch (Exception e) {
                e .printStackTrace();
                fail( "Unable to connect to GitHub: " + e .getMessage() );
                return;
            }
        
        String filePathStr = super .getProperty( "original.path" );
        Path filePath = Paths.get( filePathStr );
        LocalDateTime lastMod = LocalDateTime.now();
        if ( super .propertyIsTrue( "share.last.mod.time" ) ) {
            try {
                FileTime fileTime = Files.getLastModifiedTime( Paths.get( filePathStr ) );
                lastMod = LocalDateTime.ofInstant( fileTime.toInstant(), ZoneId.systemDefault() );
            } catch (IOException e2) {
                logger.log( Level.INFO, "Unable to get last mod time for " + filePathStr );
            }
        }
        String time = DateTimeFormatter.ofPattern( "HH-mm-ss" ) .format( lastMod );
        String date = DateTimeFormatter.ofPattern( "yyyy-MM-dd" ) .format( lastMod );
        String xml = super .getProperty( "vZome-xml" );
        String pngEncoded = super .getProperty( "png-base64" );
        String shapesJson = super .getProperty( "shapes-json" );
        this.shareData = new GitHubShare( filePath .getFileName() .toString(), date, time, xml, pngEncoded, shapesJson );

        // Initialize the transient, file-specific state
        this.title = shareData .getDesignName() .replaceAll( "-", " " ) .trim();    // but we do want spaces in the title
        this.description = "A 3D design created in vZome.  Use your mouse or touch to interact.";

        this.error = null;
        this.gitUrl = null;
        this.configured = false;
        
        this.workerThread = new Thread( new Worker() );
        this.workerThread .start();
    }
    
    private void fail( String errorMsg )
    {
        this.error = errorMsg;
        logger .severe( errorMsg );
        firePropertyChange( "stage", null, "error" );
    }
    
    private class Worker implements Runnable
    {
        @Override
        public void run()
        {
            if ( authToken == null ) {
                if ( deviceAuthorization == null )
                    try {
                        deviceAuthorization = oAuthService .getDeviceAuthorizationCodes();
                        firePropertyChange( "stage", null, "authz" );
                    } catch (InterruptedException | ExecutionException | IOException | OAuthException e) {
                        e .printStackTrace();
                        fail( "Failed to get GitHub device authorization code.  You may be offline.  Check your connection and try again." );
                        return;
                    }

                if ( deviceCode == null )
                    try {
                        deviceCode = deviceAuthorization .getUserCode();
                        String loginUrl = deviceAuthorization .getVerificationUriComplete();
                        if ( loginUrl == null )
                            loginUrl = deviceAuthorization .getVerificationUri();
                        loginUri = new URI( loginUrl );
                        firePropertyChange( "stage", null, "deviceCode" );
                    } catch ( URISyntaxException e2 )
                    {
                        e2 .printStackTrace();
                        fail( "Device authorization verification URI format incorrect: " + e2.getMessage() );
                        return;
                    }

                try {
                    final OAuth2AccessToken accessToken = oAuthService .pollAccessTokenDeviceAuthorizationGrant( deviceAuthorization );
                    authToken = accessToken .getAccessToken();
                    setProperty( "githubAccessToken", authToken );
                    client .setOAuth2Token( authToken );
                    firePropertyChange( "stage", null, "authToken" );
                } catch ( InterruptedException | ExecutionException | IOException e ) {
                    e .printStackTrace();
                    fail( "GitHub authorization failed." );
                    return;
                }
            }

            String username = null;
            try {
                username = userService .getUser() .getLogin();
            } catch ( IOException e1 ) {
                e1 .printStackTrace();
                fail( "Unable to get user" );
                return;
            }
            if ( orgName == null )
                orgName = username;

            if ( repo == null )
                try {
                    List<Repository> repositories = repositoryService .getRepositories();
                    repo = repositories .stream()
                            .filter( r -> r.getName() .equals( repoName ) )
                            .filter( r -> r.getGitUrl() .contains( orgName ) )
                            .findFirst() .orElse( null );
                    if ( repo == null ) {
                        fail( "Unable to find repository '" + orgName + "/" + repoName + "'" );
                        return;
                    } else {
                        logger .info( "found repo " + repo .getGitUrl() );
                    }
                } catch ( IOException e ) {
                    fail( "Unable to fetch repositories.  You may be offline, or your authorization may have expired or been revoked.  Check your internet connection and try again, to reauthorize." );
                    authToken = null;
                    setProperty( "githubAccessToken", "" );
                    return;
                }

            firePropertyChange( "stage", null, "options" );

            // now wait for the user to trigger "doUpload"
            configured = false;
            while ( !configured )
            {
                if ( Thread.currentThread().isInterrupted() )
                    return;
                
                try {
                    Thread.sleep( 300 );
                    continue; // skip the firePropertyChange()
                } catch (InterruptedException e) {
                    // no problem, will exit at the top of the loop
                }
            }

            try {            
                // set up for a non-root commit
                String baseCommitSha = repositoryService .getBranches( repo ) .get(0) .getCommit() .getSha();
                RepositoryCommit baseCommit = commitService .getCommit( repo, baseCommitSha );
                String baseTreeSha = baseCommit .getSha();

                Tree baseTree = dataService .getTree( repo, baseTreeSha );

                boolean blog = propertyIsTrue( "sharing-generatePost" );
                boolean publish = propertyIsTrue( "sharing-publishImmediately" );
                String style = (propertyIsTrue( "hasScenes" ) && propertyIsTrue( "sharing-showScenes" ))?
                                getProperty( "sharing-sceneStyle" ) : "none";
                
                Collection<TreeEntry> entries = new ArrayList<TreeEntry>();
                shareData .setEntryHandler( (path, data, encoding) -> {
                    addFile( entries, path, data, encoding );
                } );

                // Now generate the content
                gitUrl = shareData .generateContent( orgName, repoName, branchName, title, description, blog, publish, style );

                Tree newTree = dataService .createTree( repo, entries, (baseTree==null)? null : baseTree.getSha() );

                // create commit
                Commit commit = new Commit();
                commit.setMessage( title );
                commit.setTree( newTree );
                
                // Due to an error with github api we have to do this
                String email = ShareController.super .getProperty( "githubEmail" );
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
                Commit newCommit = dataService .createCommit( repo, commit );
                
                // create resource
                TypedResource commitResource = new TypedResource();
                commitResource .setType( TypedResource.TYPE_COMMIT );
                commitResource .setSha( newCommit .getSha() );
                commitResource .setUrl( newCommit .getUrl() );

                // get main reference and update it
                Reference reference = dataService .getReference( repo, "heads/" + branchName );
                reference .setObject( commitResource );
                dataService .editReference( repo, reference, false );
                ShareController.super .setProperty( "clipboard", gitUrl );
            }
            catch (Exception e) {
                fail( "Unable to upload files to the repository: " + e .getMessage() );
                return;
            }
            
            firePropertyChange( "stage", null, "success" );
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

    @Override
    public String getProperty( String name )
    {
        switch ( name ) {

        case "error":
            return this.error;
            
        case "gitUrl":
            return this.gitUrl;

        case "title":
            return this.title;

        case "description":
            return this.description;

        case "deviceCode":
            return this.deviceCode;

        case "loginURI":
            return this.loginUri.toString();

        default:
            return super.getProperty( name );
        }
    }

    @Override
    public void setModelProperty( String name, Object value )
    {
        switch (name) {
        
        case "title":
            this.title = (String) value;
            break;

        case "description":
            this.description = (String) value;
            break;

        default:
            super.setModelProperty( name, value );
        }
    }

    @Override
    protected void doAction( String action ) throws Exception
    {
        if ( "startShare" .equals( action ) ) {
            this.startShare();
        } else if ( "abort" .equals( action ) ) {
            this.workerThread .interrupt();
        } else if ( "doUpload" .equals( action ) ) {
            this.configured = true; // will trigger doUpload, but off the EDT
        }
        else
            super.doAction( action );
    }
}