package com.zosh.ecommerce.serviceImpl;

import com.zosh.ecommerce.entities.Otp;
import com.zosh.ecommerce.entities.User;
import com.zosh.ecommerce.repository.OtpRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class OtpService {
    @Autowired
    private OtpRepo otpRepository;

    @Autowired
    private EmailService emailService;

    public String generateOtp(User user) {
        String otpCode = String.valueOf((int) ((Math.random() * (999999 - 100000)) + 100000));
        LocalDateTime expirationTime = LocalDateTime.now().plusMinutes(5);

        Otp otp = new Otp();
        otp.setUser(user);
        otp.setOtpCode(otpCode);
        otp.setExpirationTime(expirationTime);

        otpRepository.save(otp);
        emailService.sendOtp(user.getEmail(), otpCode);

        return otpCode;
    }

    public boolean verifyOtp(String otpCode, User user) {
        return otpRepository.findByOtpCodeAndUser(otpCode, user)
                .filter(otp -> otp.getExpirationTime().isAfter(LocalDateTime.now()))
                .isPresent();
    }
}
