package de.wasenweg.komix.config.dev;

import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.OAuth2Request;

import java.io.Serializable;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class DevOAuth2Mock {

    public static final String MOCK_USER_ID = "oauth2-mock-user-id";

    public static Authentication getOauthTestAuthentication() {
        return new OAuth2Authentication(getOauth2Request(), getAuthentication());
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

        final LinkedHashMap<String, String> details = new LinkedHashMap<String, String>();
        details.put("user_name", "Foo_Bar");
        details.put("email", "foo.b@r.com");
        details.put("name", "Foo Bar");
        details.put("picture", "https://foo.bar.com/foo.bar.png");

        final TestingAuthenticationToken token = new TestingAuthenticationToken(userPrincipal, null, authorities);
        token.setAuthenticated(true);
        token.setDetails(details);

        return token;
    }
}
