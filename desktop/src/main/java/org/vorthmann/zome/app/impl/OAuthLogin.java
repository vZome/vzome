package org.vorthmann.zome.app.impl;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import com.microsoft.alm.oauth2.useragent.AuthorizationException;
import com.microsoft.alm.oauth2.useragent.AuthorizationResponse;
import com.microsoft.alm.oauth2.useragent.UserAgentImpl;

/**
 * From https://developer.okta.com/blog/2019/08/14/javafx-tutorial-oauth2-oidc
 */
public class OAuthLogin
{
    private final static String REDIRECT_URI = "http://localhost:8080/authorization-code/callback";

    String resourceDomain;
    String authnPath;
    String resourcePath;
    String clientId;
    String clientSecret;
    String scope;

    /**
     * Build the authorization request URL
     *
     * @return
     * @throws URISyntaxException
     * @throws MalformedURLException
     */
    public URI getAuthorizationEndpointUri() throws URISyntaxException, MalformedURLException {

        URIBuilder builder = new URIBuilder();

        builder.setScheme( "https" );
        builder.setHost( resourceDomain );
        builder.setPath( resourcePath );
        builder.addParameter( "client_id", clientId );
        builder.addParameter( "redirect_uri", REDIRECT_URI );
        builder.addParameter( "response_type", "code" );
        builder.addParameter( "state", "this is a state" );
        builder.addParameter( "scope", scope );

        URL url = builder.build().toURL();

        return url.toURI();

    }

    /**
     * Requests an authorization code from the auth server
     *
     * @return
     * @throws MalformedURLException
     * @throws URISyntaxException
     * @throws AuthorizationException
     */
    public String requestAuthCode() throws MalformedURLException, URISyntaxException, AuthorizationException {

        // Generate the auth endpoint URI to request the auth code

        URI authorizationEndpoint = getAuthorizationEndpointUri();

        System.out.print( "Authorization Endpoint URI: " );
        System.out.println( authorizationEndpoint.toString() );

        final URI redirectUri = new URI( REDIRECT_URI );

        // Create the user agent and make the call to the auth endpoint

        final UserAgentImpl userAgent = new UserAgentImpl();

        final AuthorizationResponse authorizationResponse = userAgent.requestAuthorizationCode(authorizationEndpoint, redirectUri);

        // We should have the code, which we can trade for the token

        final String code = authorizationResponse.getCode();

        System.out.print("Authorization Code: ");
        System.out.println(code);

        return code;

    }

    /**
     * Given an authorization code, calls the auth server to request a token
     *
     * @param code
     * @return
     * @throws URISyntaxException
     * @throws IOException
     */
    public String getTokenForCode(String code) throws URISyntaxException, IOException {

        // The token request URL

        final String tokenUrl = "https://"+ resourceDomain + authnPath;

        // Using HttpClient to make the POST to exchange the auth code for the token

        HttpClient client = HttpClientBuilder.create().build();
        HttpPost post = new HttpPost(tokenUrl);

        // Adding the POST params to the request

        List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
        urlParameters.add( new BasicNameValuePair( "grant_type", "authorization_code" ) );
        urlParameters.add( new BasicNameValuePair( "code", code ) );
        urlParameters.add( new BasicNameValuePair( "redirect_uri", REDIRECT_URI ) );
        urlParameters.add( new BasicNameValuePair( "client_id", clientId ) );
        urlParameters.add( new BasicNameValuePair( "client_secret", clientSecret ) );
        urlParameters.add( new BasicNameValuePair( "scope", scope ) );

        post.setEntity(new UrlEncodedFormEntity(urlParameters));

        // Execute the request

        HttpResponse response = client.execute(post);

        // Print the status code

        System.out.println("Response Code : " + response.getStatusLine().getStatusCode());

        // Get the content as a String

        String content = EntityUtils.toString(response.getEntity());

        System.out.println("Result : " + content.toString());

        return content.toString();
    }
    
    /**
     * Loads our config info from the app.properties file
     * @throws IOException
     */
    public void setProperties( Properties appProps )
    {
        resourceDomain = appProps.getProperty( "resourceDomain" );
        resourcePath =   appProps.getProperty( "resourcePath" );
        authnPath =      appProps.getProperty( "authnPath" );
        clientId =       appProps.getProperty( "clientId" );
        clientSecret =   appProps.getProperty( "clientSecret" );
        scope =          appProps.getProperty( "scope" );
    }

    /**
     * Entry point for the JavaFX application.
     * 1) Loads the system properties
     * 2) Requests the authorization code
     * 3) Exchanges the code for the token
     * 4) Displays the token
     * @param primaryStage
     */
    public String getToken( Properties appProps ) {

        try {
            setProperties( appProps );

            // Request the authorization code from the OAuth provider

            String code = requestAuthCode();

            // Exchange the auth code for the access token

            String token = getTokenForCode(code);
            String[] parts = token.split( "&" );
            return parts[ 0 ] .split( "=" )[ 1 ];
        }
        catch (IOException | AuthorizationException | URISyntaxException e) {
            e.printStackTrace();
            return null;
        }
    }
}
