package de.wasenweg.komix.user;

import lombok.RequiredArgsConstructor;

import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class UserController {

    @RequestMapping("/user")
    public User user(final OAuth2Authentication authentication) {
        final LinkedHashMap<String, String> properties =
                (LinkedHashMap<String, String>) authentication.getUserAuthentication().getDetails();

        return User.builder()
            .email(properties.get("email"))
            .name(properties.get("name"))
            .picture(properties.get("picture"))
            .build();
    }
}
