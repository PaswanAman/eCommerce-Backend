package com.zosh.ecommerce.schedular;

import com.zosh.ecommerce.repository.OtpRepo;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component

public class OtpSchedular {
    @Autowired
    private OtpRepo otpRepo;

   @Scheduled(cron = "0 0 * * * *")
   @Transactional// Runs every hour
    public void cleanExpiredOtps() {
        // Deletes expired OTPs and returns the count of deleted rows
        int deletedCount = otpRepo.deleteByExpirationTimeBefore(LocalDateTime.now());
        System.out.println("Deleted " + deletedCount + " expired OTP(s)");
    }



}
