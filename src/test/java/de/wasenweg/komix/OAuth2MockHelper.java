package de.wasenweg.komix;

import de.wasenweg.komix.config.dev.DevOAuth2Mock;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.OAuth2ClientContext;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class OAuth2MockHelper {

    public static OAuth2ClientContext getOauth2ClientContext() {
        final OAuth2ClientContext mockClient = mock(OAuth2ClientContext.class);
        when(mockClient.getAccessToken()).thenReturn(new DefaultOAuth2AccessToken("my-mock-token"));

        return mockClient;
    }

    public static Authentication getOauthTestAuthentication() {
        return DevOAuth2Mock.getOauthTestAuthentication();
    }
}
