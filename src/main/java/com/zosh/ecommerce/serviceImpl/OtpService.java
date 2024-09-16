package com.zosh.ecommerce.serviceImpl;

import com.zosh.ecommerce.entities.Otp;
import com.zosh.ecommerce.entities.User;
import com.zosh.ecommerce.exception.OtpNotFoundException;
import com.zosh.ecommerce.exception.UserNotFoundException;
import com.zosh.ecommerce.repository.OtpRepo;
import com.zosh.ecommerce.repository.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class OtpService {
    @Autowired
    private OtpRepo otpRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private UserRepo userRepo;

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

    public void verifyOtp(String otpCode) {
        Otp otp = otpRepository.findByOtpCode(otpCode)
                .orElseThrow(() -> new OtpNotFoundException("OTP not found or expired"));

        if (otp.getExpirationTime().isBefore(LocalDateTime.now())) {
            throw new OtpNotFoundException("OTP has expired");
        }

        // Fetch user directly from the OTP entity
        User user = otp.getUser();
        if (user == null) {
            throw new UserNotFoundException("Associated user not found in OTP");
        }

        user.setIsOtpVerified(true);
        userRepo.save(user);

        otpRepository.delete(otp);
    }


}
