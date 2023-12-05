package org.groupscope.security.services;

import lombok.extern.slf4j.Slf4j;
import org.groupscope.assignment_management.dao.repositories.RefreshTokenRepository;
import org.groupscope.security.entity.RefreshToken;
import org.groupscope.security.entity.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.security.SecureRandom;
import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

import static org.groupscope.util.FunctionInfo.getCurrentMethodName;
import static org.groupscope.util.ObjectUtil.isNull;

@Slf4j
@Service
public class RefreshTokenService {

    @Value("${jwt.lifetime.refreshToken}")
    private Long refreshTokenDurationMs;

    private final RefreshTokenRepository refreshTokenRepository;

    public RefreshTokenService(RefreshTokenRepository refreshTokenRepository) {
        this.refreshTokenRepository = refreshTokenRepository;
    }

    /**
     * Retrieve a refresh token by its token value.
     *
     * @param token The token to search for.
     * @return The RefreshToken object associated with the given token, or null if not found.
     */
    @Transactional
    public RefreshToken findByToken(String token) {
        if (isNull(token)) {
            log.error("Token string is null in " + getCurrentMethodName());
            return null;
        }

        return refreshTokenRepository.getRefreshTokenByToken(token).orElse(null);
    }

    /**
     * Retrieve a refresh token by the user's ID.
     *
     * @param id The ID of the user.
     * @return The RefreshToken object associated with the user, or null if not found.
     */
    @Transactional
    public RefreshToken findByUserId(Long id) {
        if (isNull(id)) {
            log.error("User id is null in " + getCurrentMethodName());
            return null;
        }

        return refreshTokenRepository.getRefreshTokenByUserId(id).orElse(null);
    }

    /**
     * Create a new refresh token for a user or update the existing one.
     *
     * @param user        The user for whom the refresh token is created or updated.
     * @param forceUpdate If true, force the update of the existing token even if it's not expired.
     * @return The created or updated RefreshToken object.
     */
    @Transactional
    public RefreshToken createOrUpdateRefreshToken(User user, boolean forceUpdate) {
        ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);
        buffer.putLong(user.getId() + LocalDate.now().toEpochDay());
        SecureRandom secureRandom = new SecureRandom(buffer.array());

        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        long refreshLifetime = Duration.ofMillis(refreshTokenDurationMs).toDays();
        String expiry = LocalDate.now().plusDays(refreshLifetime).format(dateFormatter);

        RefreshToken refreshToken = findByUserId(user.getId());

        if (refreshToken != null) {
            if (!isValidExpiration(refreshToken) || forceUpdate) {
                refreshToken.setExpiryDate(expiry);
                refreshToken.setToken(new BigInteger(256, secureRandom).toString(32));
            }
        } else {
            refreshToken = new RefreshToken();
            refreshToken.setUser(user);
            refreshToken.setExpiryDate(expiry);
            refreshToken.setToken(new BigInteger(256, secureRandom).toString(32));
        }

        RefreshToken token = refreshTokenRepository.save(refreshToken);
        Objects.requireNonNull(token, "Refresh token is null")

        return token;
    }

    /**
     * Check if a refresh token has a valid expiration date.
     *
     * @param token The RefreshToken object to check.
     * @return True if the token's expiration date is equal to or after the current date, false otherwise.
     */
    public boolean isValidExpiration(RefreshToken token) {
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        LocalDate now = LocalDate.now();
        LocalDate expiry = LocalDate.parse(token.getExpiryDate(), dateFormatter);

        return expiry.isEqual(now) || expiry.isAfter(now);
    }
}
