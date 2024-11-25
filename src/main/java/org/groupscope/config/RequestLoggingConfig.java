package org.groupscope.config;

import org.groupscope.security.CustomRequestLoggingFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * @author Mykyta Liashko
 */
@Configuration
public class RequestLoggingConfig {

    private final List<String> ignoredEndpoints = List.of(
            "/api/v1/auth",
            "/api/v1/oauth",
            "/swagger-ui",
            "/swagger-resources",
            "/v3/api-docs"
    );

    @Bean
    public CustomRequestLoggingFilter requestLoggingFilter() {
        CustomRequestLoggingFilter loggingFilter = new CustomRequestLoggingFilter(ignoredEndpoints);
        loggingFilter.setIncludeClientInfo(true);
        loggingFilter.setIncludeQueryString(true);
        loggingFilter.setIncludePayload(true);
        loggingFilter.setMaxPayloadLength(64000);
        loggingFilter.setBeforeMessagePrefix("REQUEST DATA: [");
        return loggingFilter;
    }

}
