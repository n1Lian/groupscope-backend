package org.groupscope.security.controller;

import lombok.extern.slf4j.Slf4j;
import org.groupscope.security.entity.Provider;
import org.groupscope.security.JwtProvider;
import org.groupscope.security.dto.*;
import org.groupscope.security.entity.RefreshToken;
import org.groupscope.security.entity.User;
import org.groupscope.security.services.RefreshTokenService;
import org.groupscope.security.services.auth.UserService;
import org.groupscope.security.services.oauth2.OAuth2UserService;
import org.groupscope.security.util.CookieUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@Slf4j
@RestController
public class AuthController {
    private final UserService userService;

    private final OAuth2UserService OAuth2UserService;

    private final JwtProvider jwtProvider;

    private final RefreshTokenService refreshTokenService;

    private final CookieUtil cookieUtil;

    @Autowired
    public AuthController(UserService userService,
                          OAuth2UserService OAuth2UserService,
                          JwtProvider jwtProvider,
                          RefreshTokenService refreshTokenService,
                          CookieUtil cookieUtil) {
        this.userService = userService;
        this.OAuth2UserService = OAuth2UserService;
        this.jwtProvider = jwtProvider;
        this.refreshTokenService = refreshTokenService;
        this.cookieUtil = cookieUtil;
    }

    @PostMapping("/register")
    public ResponseEntity<HttpStatus> registerUser(@RequestBody @Valid RegistrationRequest request) {
        try {
            // Checking for user existing
            if (userService.findByLogin(request.getLogin()) != null) {
                return ResponseEntity.status(HttpStatus.CONFLICT).build();
            }

            if(!request.isValid()) {
                log.info(request + " not valid");
                return ResponseEntity.badRequest().build();
            }

            // Save new user
            User user = userService.saveUser(request.toUser(), request, Provider.LOCAL);
            if (user != null) {
                return ResponseEntity.ok().build();
            } else {
                return ResponseEntity.badRequest().build();
            }
        } catch (NullPointerException | IllegalArgumentException e) {
            log.error(e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } catch (Exception e) {
            log.error(e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build();
        }
    }

    @PostMapping("/auth")
    public ResponseEntity<AuthResponse> auth(@RequestBody @Valid AuthRequest request) {
        try {
            User user = userService.findByLoginAndPassword(request.getLogin(), request.getPassword());
            if (user != null) {
                String refreshTokenStr = refreshTokenService.createOrUpdateRefreshToken(user, false).getToken();
                String jwtToken = jwtProvider.generateToken(user.getLogin());
                HttpHeaders headers = new HttpHeaders();
                headers.add(HttpHeaders.SET_COOKIE,
                        cookieUtil.createRefreshTokenCookie(refreshTokenStr,  2592000000L).toString());

                return ResponseEntity.ok().headers(headers).body(new AuthResponse(jwtToken, user.getLearner().getRole()));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
        } catch (NullPointerException | IllegalArgumentException e) {
            log.error(e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            log.error(e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build();
        }
    }

    @PostMapping("/oauth2")
    public ResponseEntity<AuthResponse> auth(@RequestBody @Valid OAuth2Request request) {
        try {
            User user = OAuth2UserService.loginOAuthGoogle(request);
            if (user != null) {
                String refreshTokenStr = refreshTokenService.createOrUpdateRefreshToken(user, false).getToken();
                String jwtToken = jwtProvider.generateToken(user.getLogin());
                HttpHeaders headers = new HttpHeaders();
                headers.add(HttpHeaders.SET_COOKIE,
                        cookieUtil.createRefreshTokenCookie(refreshTokenStr,  2592000000L).toString());

                return ResponseEntity.ok().headers(headers).body(new AuthResponse(jwtToken, user.getLearner().getRole()));
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }
        } catch (NullPointerException | IllegalArgumentException e) {
            log.error(e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } catch (Exception e) {
            log.error(e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build();
        }
    }

    @GetMapping("/refresh")
    public ResponseEntity<AuthResponse> refreshJwtToken(@CookieValue(name = "refreshToken", required = false) String refreshToken) {
        try {
            RefreshToken refreshTokenObj = refreshTokenService.findByToken(refreshToken);

            if(refreshTokenObj != null) {
                User user = refreshTokenObj.getUser();
                String refreshTokenStr = refreshTokenService.createOrUpdateRefreshToken(user, true).getToken();
                String jwtToken = jwtProvider.generateToken(user.getLogin());
                HttpHeaders headers = new HttpHeaders();
                headers.add(HttpHeaders.SET_COOKIE,
                        cookieUtil.createRefreshTokenCookie(refreshTokenStr,  2592000000L).toString());

                return ResponseEntity.ok().headers(headers).body(new AuthResponse(jwtToken, user.getLearner().getRole()));
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }
        } catch (Exception e) {
            log.error(e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build();
        }
    }

    @GetMapping("/hi")
    public ResponseEntity<String> handleHeadRequest() {
        HttpHeaders headers = new HttpHeaders();
        return ResponseEntity.ok("Hello!");
    }
}
