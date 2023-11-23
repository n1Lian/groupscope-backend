package org.groupscope.assignment_management.dao.repositories;

import org.groupscope.security.entity.RefreshToken;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends CrudRepository<RefreshToken, Long> {
    Optional<RefreshToken> getRefreshTokenByToken(String token);

    Optional<RefreshToken> getRefreshTokenByUserId(Long userId);
}
