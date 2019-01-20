package de.wasenweg.komix;

import java.util.Collections;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.boot.autoconfigure.web.servlet.error.ErrorViewResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.ModelAndView;

@Configuration
public class KomixApplicationConfiguration {

    @Bean
    ErrorViewResolver redirectViewUrls() {
        return new ErrorViewResolver() {
            @Override
            public ModelAndView resolveErrorView(final HttpServletRequest request, final HttpStatus status, final Map<String, Object> model) {
                return status == HttpStatus.NOT_FOUND
                        ? new ModelAndView("index.html", Collections.<String, Object>emptyMap(), HttpStatus.OK)
                        : null;
            }
        };
    }
}
