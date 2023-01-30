package org.vorthmann.zome.app.impl;


import java.io.IOException;
import java.io.OutputStream;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.scribejava.core.builder.ServiceBuilder;
import com.github.scribejava.core.builder.api.DefaultApi20;
import com.github.scribejava.core.extractors.OAuth2AccessTokenJsonExtractor;
import com.github.scribejava.core.extractors.TokenExtractor;
import com.github.scribejava.core.httpclient.HttpClient;
import com.github.scribejava.core.httpclient.HttpClientConfig;
import com.github.scribejava.core.model.DeviceAuthorization;
import com.github.scribejava.core.model.OAuth2AccessToken;
import com.github.scribejava.core.model.OAuthRequest;
import com.github.scribejava.core.model.Response;
import com.github.scribejava.core.model.Verb;
import com.github.scribejava.core.oauth.OAuth20Service;
import com.github.scribejava.core.utils.Preconditions;

public class Google20DeviceAuthorizationGrantExample {

    private static final String NETWORK_NAME = "GitHub";
    private static final String PROTECTED_RESOURCE_URL = "https://api.github.com/user";

    
    private static class GitHubApi extends DefaultApi20
    {
        protected GitHubApi() {
        }

        private static final OAuth2AccessTokenJsonExtractor EXTRACTOR = new OAuth2AccessTokenJsonExtractor()
        {
            @Override
            public OAuth2AccessToken extract( Response response ) throws IOException
            {
                final String body = response.getBody();
                Preconditions.checkEmptyString(body, "Response body is incorrect. Can't extract a token from an empty string");

                final JsonNode json = OBJECT_MAPPER.readTree(body);
                if ( response.getCode() != 200 || json .get( "error" ) != null ) {
                    generateError( response );
                }

                return super.extract(response);
            }
            
        };

        private static class InstanceHolder {
            private static final GitHubApi INSTANCE = new GitHubApi();
        }

        public static GitHubApi instance() {
            return InstanceHolder.INSTANCE;
        }

        @Override
        public Verb getAccessTokenVerb() {
            return Verb.POST;
        }

        @Override
        public String getAccessTokenEndpoint() {
            return "https://github.com/login/oauth/access_token";
        }

        @Override
        protected String getAuthorizationBaseUrl() {
            return "https://github.com/login/oauth/authorize";
        }

        @Override
        public TokenExtractor<OAuth2AccessToken> getAccessTokenExtractor() {
            return EXTRACTOR;
        }

        @Override
        public String getDeviceAuthorizationEndpoint() {
            return "https://github.com/login/device/code";
        }

        @Override
        public OAuth20Service createService( String apiKey, String apiSecret, String callback, String defaultScope,
                String responseType, OutputStream debugStream, String userAgent, HttpClientConfig httpClientConfig,
                HttpClient httpClient) {
            return new OAuth20Service(this, apiKey, apiSecret, callback, defaultScope, responseType, debugStream, userAgent,
                    httpClientConfig, httpClient)
            {
                @Override
                protected OAuthRequest createDeviceAuthorizationCodesRequest( String scope )
                {
                    OAuthRequest request = super .createDeviceAuthorizationCodesRequest( scope );
                    request .addHeader( "Accept", "application/json" );
                    return request;
                }
                
                @Override
                protected OAuthRequest createAccessTokenDeviceAuthorizationGrantRequest( DeviceAuthorization deviceAuthorization )
                {
                    OAuthRequest request = super .createAccessTokenDeviceAuthorizationGrantRequest( deviceAuthorization );
                    request .addHeader( "Accept", "application/json" );
                    return request;
                }
            };
        }
    }

    private Google20DeviceAuthorizationGrantExample() {
    }

    //@SuppressWarnings("PMD.SystemPrintln")
    public static void main(String... args) throws IOException, InterruptedException, ExecutionException
    {
        // Replace these with your client id and secret
        final String clientId = "your ID here";
        final String clientSecret = "your secret here";

        final OAuth20Service service = new ServiceBuilder(clientId)
                .debug()
                .apiSecret(clientSecret)
                .responseType( "application/json" )
                .defaultScope("gist") // replace with desired scope
                .build(GitHubApi.instance());
        try (Scanner in = new Scanner(System.in, "UTF-8")) {
			System.out.println("=== " + NETWORK_NAME + "'s OAuth Workflow ===");
			System.out.println();

			System.out.println("Requesting a set of verification codes...");

			final DeviceAuthorization deviceAuthorization = service.getDeviceAuthorizationCodes();
			System.out.println("Got the Device Authorization Codes!");
			System.out.println(deviceAuthorization);

			System.out.println("Now go and authorize ScribeJava. Visit: " + deviceAuthorization.getVerificationUri()
			        + " and enter the code: " + deviceAuthorization.getUserCode());
			if (deviceAuthorization.getVerificationUriComplete() != null) {
			    System.out.println("Or visit " + deviceAuthorization.getVerificationUriComplete());
			}

			System.out.println("Polling for an Access Token...");
			final OAuth2AccessToken accessToken = service.pollAccessTokenDeviceAuthorizationGrant(deviceAuthorization);

			System.out.println("Got the Access Token!");
			System.out.println("(The raw response looks like this: " + accessToken.getRawResponse() + "')");

			// Now let's go and ask for a protected resource!
			System.out.println("Now we're going to access a protected resource...");
			while (true) {
			    System.out.println("Paste fieldnames to fetch (leave empty to get profile, 'exit' to stop the example)");
			    System.out.print(">>");
			    final String query = in.nextLine();
			    System.out.println();
			    final String requestUrl;
			    if ("exit".equals(query)) {
			        break;
			    } else if (query == null || query.isEmpty()) {
			        requestUrl = PROTECTED_RESOURCE_URL;
			    } else {
			        requestUrl = PROTECTED_RESOURCE_URL + "?fields=" + query;
			    }
			    final OAuthRequest request = new OAuthRequest(Verb.GET, requestUrl);
			    service.signRequest(accessToken, request);
			    System.out.println();
			    try (Response response = service.execute(request)) {
			        System.out.println(response.getCode());
			        System.out.println(response.getBody());
			    }
			    System.out.println();
			}
		}
    }
}