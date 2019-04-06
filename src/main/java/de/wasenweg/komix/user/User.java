package de.wasenweg.komix.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    private String email;
    private String name;
    private String picture;
}
