package de.wasenweg.alfred.config.dev;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.oauth2.client.authentication.OAuth2LoginAuthenticationToken;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationExchange;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationResponse;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.time.Instant;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;

public class DevOAuth2Mock {

    public static final String MOCK_USER_ID = "oauth2-mock-user-id";

    public static Authentication getOAuth2LoginAuthenticationToken() {
        return new OAuth2LoginAuthenticationToken(
                getRegistration(),
                getExchange(),
                getUser(),
                Collections.emptyList(),
                getAccessToken(),
                null);
    }

    private static ClientRegistration getRegistration() {
        return ClientRegistration
                .withRegistrationId(MOCK_USER_ID)
                .authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS)
                .clientId("dev-client")
                .tokenUri("http://foo.bar.com")
                .authorizationUri("http://foo.bar.com")
                .build();
    }

    private static OAuth2AuthorizationExchange getExchange() {
        final OAuth2AuthorizationRequest request = OAuth2AuthorizationRequest
                .implicit()
                .clientId("dev-client")
                .authorizationUri("http://foo.bar.com")
                .redirectUri("http://foo.bar.com")
                .build();
        final OAuth2AuthorizationResponse response = OAuth2AuthorizationResponse
                .success("success")
                .redirectUri("http://foo.bar.com")
                .build();
        return new OAuth2AuthorizationExchange(request, response);
    }

    private static OAuth2User getUser() {
        final List<GrantedAuthority> authorities = AuthorityUtils.createAuthorityList("Everything");
        final LinkedHashMap<String, Object> details = new LinkedHashMap<String, Object>();
        details.put("id", MOCK_USER_ID);
        details.put("user_name", "B.Wayne");
        details.put("email", "b.wayne@waynecorp.com");
        details.put("name", "Bruce Wayne");
        details.put("picture", "https://img.icons8.com/office/80/000000/batman-old.png");
        return new DefaultOAuth2User(authorities, details, "id");
    }

    private static OAuth2AccessToken getAccessToken() {
        return new OAuth2AccessToken(
                OAuth2AccessToken.TokenType.BEARER,
                "1234",
                Instant.now(),
                Instant.now().plusSeconds(3600));
    }
}
