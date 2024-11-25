package org.groupscope.security.controller;

import jakarta.persistence.EntityExistsException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.groupscope.security.JwtProvider;
import org.groupscope.security.dto.AuthRequest;
import org.groupscope.security.dto.AuthResponse;
import org.groupscope.security.dto.OAuth2Request;
import org.groupscope.security.dto.RegistrationRequest;
import org.groupscope.security.entity.Provider;
import org.groupscope.security.entity.RefreshToken;
import org.groupscope.security.entity.User;
import org.groupscope.security.services.RefreshTokenService;
import org.groupscope.security.services.auth.UserService;
import org.groupscope.security.services.oauth2.OAuth2UserService;
import org.groupscope.security.util.CookieUtil;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Slf4j
@CrossOrigin
@RestController
@RequiredArgsConstructor
@Validated
public class AuthController {
    private final UserService userService;

    private final OAuth2UserService oAuth2UserService;

    private final JwtProvider jwtProvider;

    private final RefreshTokenService refreshTokenService;

    private final CookieUtil cookieUtil;

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.OK)
    public void registerUser(
            @RequestBody @Valid RegistrationRequest request
    ) {
        // Checking for user existing
        if (userService.findByLogin(request.getLogin()) != null) {
            throw new EntityExistsException("User with login " + request.getLogin() + " already exists");
        }

        // Save new user
        userService.saveUser(request.toUser(), request, Provider.LOCAL);
    }

    @PostMapping("/auth")
    public ResponseEntity<AuthResponse> auth(@RequestBody @Valid AuthRequest request) {
        User user = userService.findByLoginAndPassword(request.getLogin(), request.getPassword());
        if (user != null) {
            String refreshTokenStr = refreshTokenService.createOrUpdateRefreshToken(user, false).getToken();
            String jwtToken = jwtProvider.generateToken(user.getLogin());
            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.SET_COOKIE,
                    cookieUtil.createRefreshTokenCookie(refreshTokenStr, 2592000000L).toString());

            return ResponseEntity.ok().headers(headers).body(new AuthResponse(jwtToken, user.getLearner().getRole()));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @PostMapping("/oauth2")
    public ResponseEntity<AuthResponse> auth(@RequestBody @Valid OAuth2Request request) {
        User user = oAuth2UserService.loginOAuthGoogle(request);
        if (user != null) {
            String refreshTokenStr = refreshTokenService.createOrUpdateRefreshToken(user, false).getToken();
            String jwtToken = jwtProvider.generateToken(user.getLogin());
            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.SET_COOKIE,
                    cookieUtil.createRefreshTokenCookie(refreshTokenStr, 2592000000L).toString());

            return ResponseEntity.ok().headers(headers).body(new AuthResponse(jwtToken, user.getLearner().getRole()));
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @GetMapping("/refresh")
    public ResponseEntity<AuthResponse> refreshJwtToken(@CookieValue(name = "refreshToken", required = false) String refreshToken) {
        RefreshToken refreshTokenObj = refreshTokenService.findByToken(refreshToken);

        if (refreshTokenObj != null) {
            User user = refreshTokenObj.getUser();
            String refreshTokenStr = refreshTokenService.createOrUpdateRefreshToken(user, true).getToken();
            String jwtToken = jwtProvider.generateToken(user.getLogin());
            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.SET_COOKIE,
                    cookieUtil.createRefreshTokenCookie(refreshTokenStr, 2592000000L).toString());

            return ResponseEntity.ok().headers(headers).body(new AuthResponse(jwtToken, user.getLearner().getRole()));
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @GetMapping("/hi")
    public ResponseEntity<String> handleHeadRequest() {
        return ResponseEntity.ok("Hello!");
    }
}
