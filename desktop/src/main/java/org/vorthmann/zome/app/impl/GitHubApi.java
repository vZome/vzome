package org.vorthmann.zome.app.impl;

import java.io.IOException;
import java.io.OutputStream;

import com.fasterxml.jackson.databind.JsonNode;
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

public class GitHubApi extends DefaultApi20
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

    public String getDeviceAuthorizationEndpoint() {
        return "https://github.com/login/device/code";
    }

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
            
            protected OAuthRequest createAccessTokenDeviceAuthorizationGrantRequest( DeviceAuthorization deviceAuthorization )
            {
                OAuthRequest request = super .createAccessTokenDeviceAuthorizationGrantRequest( deviceAuthorization );
                request .addHeader( "Accept", "application/json" );
                return request;
            }
        };
    }
}
