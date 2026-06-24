package com.re.artworkgallery_security.repository;

import com.re.artworkgallery_security.model.TokenSession;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface TokenSessionRepository extends JpaRepository<TokenSession, Long> {
    Optional<TokenSession> findByRefreshTokenValue(String refreshTokenValue);
    List<TokenSession> findByAccountId(Long accountId);
}
