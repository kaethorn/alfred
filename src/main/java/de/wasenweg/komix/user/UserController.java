package de.wasenweg.komix.user;

import lombok.RequiredArgsConstructor;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class UserController {

    @RequestMapping("/user")
    public User user(@AuthenticationPrincipal final OAuth2User oauth2User) {
        final Map<String, Object> properties = oauth2User.getAttributes();

        return User.builder()
            .id(oauth2User.getName())
            .email((String) properties.get("email"))
            .name((String) properties.get("name"))
            .picture((String) properties.get("picture"))
            .build();
    }
}
