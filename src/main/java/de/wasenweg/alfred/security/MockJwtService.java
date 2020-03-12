package de.wasenweg.alfred.security;

import org.springframework.context.annotation.Profile;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
@Profile({"dev", "test"})
public class MockJwtService implements IJwtService {

  @Override
  public Boolean verifyToken(final String token, final String secret) {
    final ArrayList<SimpleGrantedAuthority> authorities = new ArrayList<>();
    authorities.add(new SimpleGrantedAuthority("iss"));
    authorities.add(new SimpleGrantedAuthority("sub"));
    authorities.add(new SimpleGrantedAuthority("API_ALLOWED"));
    authorities.add(new SimpleGrantedAuthority("exp"));

    final UsernamePasswordAuthenticationToken mockAuth =
        new UsernamePasswordAuthenticationToken("mock-user-1", "", authorities);

    SecurityContextHolder.getContext().setAuthentication(mockAuth);
    return true;
  }
}
