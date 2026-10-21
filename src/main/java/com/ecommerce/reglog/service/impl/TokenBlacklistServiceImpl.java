package com.ecommerce.reglog.service.impl;

import com.ecommerce.reglog.entity.RevokedToken;
import com.ecommerce.reglog.repository.RevokedTokenRepository;
import com.ecommerce.reglog.service.TokenBlacklistService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.HexFormat;

@Service
public class TokenBlacklistServiceImpl implements TokenBlacklistService {

    private static final Logger logger = LoggerFactory.getLogger(TokenBlacklistServiceImpl.class);

    private final RevokedTokenRepository revokedTokenRepository;

    public TokenBlacklistServiceImpl(RevokedTokenRepository revokedTokenRepository) {
        this.revokedTokenRepository = revokedTokenRepository;
    }

    @Override
    @Transactional
    public void revokeToken(String token, Instant expiryDate) {
        String tokenHash = hashToken(token);
        if (!revokedTokenRepository.existsByTokenHash(tokenHash)) {
            RevokedToken revokedToken = new RevokedToken(tokenHash, expiryDate);
            revokedTokenRepository.save(revokedToken);
            logger.info("Token successfully blacklisted/revoked until {}", expiryDate);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isTokenRevoked(String token) {
        String tokenHash = hashToken(token);
        return revokedTokenRepository.existsByTokenHash(tokenHash);
    }

    @Override
    @Scheduled(cron = "0 0 * * * *") // Run hourly to purge expired tokens
    @Transactional
    public void purgeExpiredTokens() {
        revokedTokenRepository.deleteByExpiryDateBefore(Instant.now());
        logger.info("Purged expired revoked tokens from blacklist table");
    }

    private String hashToken(String token) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(token.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algorithm not available", e);
        }
    }
}
