package de.wasenweg.comix;

import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.oauth2.client.OAuth2ClientContext;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.OAuth2Request;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class OAuth2Mock {

    public static final String MOCK_USER_ID = "oauth2-mock-user-id";

    public static Authentication getOauthTestAuthentication() {
        return new OAuth2Authentication(getOauth2Request(), getAuthentication());
    }

    public static OAuth2ClientContext getOauth2ClientContext() {
        final OAuth2ClientContext mockClient = mock(OAuth2ClientContext.class);
        when(mockClient.getAccessToken()).thenReturn(new DefaultOAuth2AccessToken("my-fun-token"));

        return mockClient;
    }

    private static OAuth2Request getOauth2Request() {
        final String clientId = "oauth-client-id";
        final Map<String, String> requestParameters = Collections.emptyMap();
        final boolean approved = true;
        final String redirectUrl = "http://my-redirect-url.com";
        final Set<String> responseTypes = Collections.emptySet();
        final Set<String> scopes = Collections.emptySet();
        final Set<String> resourceIds = Collections.emptySet();
        final Map<String, Serializable> extensionProperties = Collections.emptyMap();
        final List<GrantedAuthority> authorities = AuthorityUtils.createAuthorityList("Everything");

        final OAuth2Request oAuth2Request = new OAuth2Request(requestParameters, clientId, authorities,
            approved, scopes, resourceIds, redirectUrl, responseTypes, extensionProperties);

        return oAuth2Request;
    }

    private static Authentication getAuthentication() {
        final List<GrantedAuthority> authorities = AuthorityUtils.createAuthorityList("Everything");

        final User userPrincipal = new User(MOCK_USER_ID, "", true, true, true, true, authorities);

        final HashMap<String, String> details = new HashMap<String, String>();
        details.put("user_name", "Foo_Bar");
        details.put("email", "foo.b@r.com");
        details.put("name", "Foo Bar");

        final TestingAuthenticationToken token = new TestingAuthenticationToken(userPrincipal, null, authorities);
        token.setAuthenticated(true);
        token.setDetails(details);

        return token;
    }
}
