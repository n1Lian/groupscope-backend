package org.groupscope.security;


import lombok.extern.slf4j.Slf4j;
import org.groupscope.security.entity.User;
import org.groupscope.security.services.auth.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;

import static org.springframework.util.StringUtils.hasText;

/*
 * This class is a JwtFilter responsible for authenticating and authorizing requests using JWT tokens.
 * It intercepts incoming requests, extracts JWT tokens, validates them, and sets the user authentication in the security context.
 */

@Slf4j
@Component
public class JwtFilter extends GenericFilterBean {

    public static final String AUTHORIZATION = "authorization";

    private final JwtProvider jwtProvider;

    private final UserDetailsServiceImpl userDetailsServiceImpl;

    @Autowired
    public JwtFilter(JwtProvider jwtProvider, UserDetailsServiceImpl userDetailsServiceImpl) {
        this.jwtProvider = jwtProvider;
        this.userDetailsServiceImpl = userDetailsServiceImpl;
    }

    /*
     * This method intercepts incoming requests, extracts the JWT token from the "Authorization" header,
     * validates the token, and sets the user authentication in the security context.
     */
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse,
                         FilterChain filterChain) throws IOException, ServletException {
        //RequestInfo.printRequest((HttpServletRequest) servletRequest);

        String token = getTokenFromRequest((HttpServletRequest) servletRequest);
        if(token != null && jwtProvider.validateToken(token)) {
            String userLogin = jwtProvider.getLoginFromToken(token);
            User user = userDetailsServiceImpl.loadUserByUsername(userLogin);
            UsernamePasswordAuthenticationToken auth =
                    new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(auth);
        }
        filterChain.doFilter(servletRequest, servletResponse);
    }

    /*
     * Extracts the JWT token from the "Authorization" header of the HTTP request.
     * Returns the extracted token or null if no token is found.
     */
    private String getTokenFromRequest(HttpServletRequest request) {
        String bearer = request.getHeader(AUTHORIZATION);
        if(hasText(bearer) && bearer.startsWith("Bearer ")) {
            return bearer.substring(7);
        }
        return null;
    }
}
