package com.ecommerce.reglog.service;

import java.time.Instant;

public interface TokenBlacklistService {

    void revokeToken(String token, Instant expiryDate);

    boolean isTokenRevoked(String token);

    void purgeExpiredTokens();
}
