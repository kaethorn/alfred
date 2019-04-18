package de.wasenweg.komix.config.dev;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.IOException;

@Component
@RequiredArgsConstructor
@Profile({"dev", "test"})
public class DevPreuthenticationFilter extends GenericFilterBean {

    @Override
    public void doFilter(final ServletRequest request,
                         final ServletResponse response,
                         final FilterChain chain)
            throws IOException, ServletException {

        SecurityContextHolder.getContext().setAuthentication(
                DevOAuth2Mock.getOAuth2LoginAuthenticationToken());

        chain.doFilter(request, response);
    }
}
