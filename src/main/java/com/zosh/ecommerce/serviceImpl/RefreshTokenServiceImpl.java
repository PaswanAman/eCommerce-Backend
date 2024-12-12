package com.zosh.ecommerce.serviceImpl;

import com.zosh.ecommerce.entities.RefreshToken;
import com.zosh.ecommerce.entities.User;
import com.zosh.ecommerce.repository.RefreshTokenRepo;
import com.zosh.ecommerce.repository.UserRepo;
import com.zosh.ecommerce.service.RefreshTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
public class RefreshTokenServiceImpl implements RefreshTokenService {


    @Autowired
    private UserRepo userRepo;
    @Autowired
    private RefreshTokenRepo refreshTokenRepo;
    @Override
    public RefreshToken createRefreshToken(String userName) {
        User user = userRepo.findByEmail(userName).get();
        RefreshToken refreshToken = user.getRefreshToken();

        Instant expiryTime = Instant.now().plusMillis(5 * 60 * 60 * 10000);

        if (refreshToken == null){
            refreshToken = RefreshToken.builder()
                    .refreshToken(UUID.randomUUID().toString())
                    .expiry(expiryTime)
                    .user(userRepo.findByEmail(userName).get())
                    .build();
        } else {
//            refreshToken.setExpiry(Instant.now().plusMillis(5*60*60*10000));
            refreshToken.setExpiry(expiryTime);
        }

        user.setRefreshToken(refreshToken);
        refreshTokenRepo.save(refreshToken);
        return refreshToken;
    }

    @Override
    public RefreshToken verifyRefreshToken(String refreshToken) {
        RefreshToken refreshTokenOb = refreshTokenRepo.findByRefreshToken(refreshToken).orElseThrow();
        if (refreshTokenOb.getExpiry().compareTo(Instant.now())<0){
            refreshTokenRepo.delete(refreshTokenOb);
            throw new RuntimeException("Refresh Token Expired");
        }
        return refreshTokenOb;
    }
}
