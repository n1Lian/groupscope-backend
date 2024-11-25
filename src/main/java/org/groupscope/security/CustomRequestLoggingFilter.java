package org.groupscope.security;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.CommonsRequestLoggingFilter;

import java.util.Arrays;
import java.util.List;

/**
 * @author Mykyta Liashko
 */
@Component
public class CustomRequestLoggingFilter extends CommonsRequestLoggingFilter {

    @Autowired
    private Environment environment;

    private final List<String> ignoredEndpoints;

    public CustomRequestLoggingFilter(List<String> ignoredEndpoints) {
        this.ignoredEndpoints = ignoredEndpoints;
    }

    @Override
    protected boolean shouldLog(HttpServletRequest request) {
        String requestURI = request.getRequestURI();

        if (ignoredEndpoints.contains(requestURI) ||
                ignoredEndpoints.stream().anyMatch(requestURI::startsWith)
        ) {
            return false;
        }

        return super.shouldLog(request);
    }

    @Override
    protected void beforeRequest(HttpServletRequest request, String message) {
        // Skip logging for GET requests in PROD env
        if (Arrays.stream(this.environment.getActiveProfiles())
                .map(String::toLowerCase)
                .toList()
                .contains(("prod")) && "GET".equalsIgnoreCase(request.getMethod())
        ) {
            return;
        }

        super.beforeRequest(request, message);
    }

    @Override
    protected void afterRequest(HttpServletRequest request, String message) {
        // disabled
    }

}