package com.zosh.ecommerce.schedular;

import com.zosh.ecommerce.entities.Otp;
import com.zosh.ecommerce.entities.User;
import com.zosh.ecommerce.repository.OtpRepo;
import com.zosh.ecommerce.repository.UserRepo;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component

public class OtpSchedular {
    @Autowired
    private OtpRepo otpRepo;
    @Autowired
    private UserRepo userRepo;

    @Scheduled(cron = "0 */1 * * * ?")
    public void cleanupExpiredTokens() {
        LocalDateTime now = LocalDateTime.now();
        List<Otp> expiredTokens = otpRepo.findByExpiryDateBefore(now);

        for (Otp otp : expiredTokens) {
            otpRepo.delete(otp);
            User user = otp.getUser();
            userRepo.delete(user);
        }
    }
}
