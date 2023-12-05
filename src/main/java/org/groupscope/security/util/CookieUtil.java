package org.groupscope.security.util;

import org.springframework.http.HttpCookie;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

@Component
public class CookieUtil {
    public HttpCookie createAccessTokenCookie(String token, Long duration) {
        return ResponseCookie.from("accessToken", token)
                .maxAge(duration)
                .httpOnly(true)
                .path("/")
                .domain("localhost")
                .sameSite("None")
                .secure(true)
                .build();
    }

    public HttpCookie createRefreshTokenCookie(String token, Long duration) {
        return ResponseCookie.from("refreshToken", token)
                .maxAge(duration)
                .httpOnly(true)
                .path("/")
                .domain("localhost")
                .sameSite("None")
                .secure(true)
                .build();
    }

}
