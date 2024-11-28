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

    @Scheduled(cron = "0 * * * * *")
    @Transactional
    public void cleanExpiredOtps() {
        LocalDateTime now = LocalDateTime.now();

        // Fetch all expired OTPs
        List<Otp> expiredOtps = otpRepo.findAllByExpirationTimeBefore(now);

        for (Otp otp : expiredOtps) {
            User user = otp.getUser();

            // Delete the OTP entry
            otpRepo.delete(otp);

            // Delete the associated user if they are still disabled
            if (!user.isEnabled()) {
                userRepo.delete(user);
                System.out.println("Deleted user with ID: " + user.getId());
            }
        }

        System.out.println("OTP cleanup completed at " + now);
    }


}
