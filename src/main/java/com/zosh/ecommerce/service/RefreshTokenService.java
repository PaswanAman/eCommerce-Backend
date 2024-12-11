package com.zosh.ecommerce.service;

import com.zosh.ecommerce.entities.RefreshToken;
import org.springframework.stereotype.Service;

@Service
public interface RefreshTokenService {
    RefreshToken createRefreshToken(String userName);
    RefreshToken verifyRefreshToken(String refreshToken);
}
