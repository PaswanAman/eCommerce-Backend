package com.zosh.ecommerce.serviceImpl;

import com.zosh.ecommerce.entities.Otp;
import com.zosh.ecommerce.entities.User;
import com.zosh.ecommerce.exception.OtpNotFoundException;
import com.zosh.ecommerce.exception.UserNotFoundException;
import com.zosh.ecommerce.repository.OtpRepo;
import com.zosh.ecommerce.repository.UserRepo;
import jakarta.transaction.Transactional;
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

    @Transactional
    public String generateOtp(User user) {
        String otpCode = String.valueOf((int) ((Math.random() * (999999 - 100000)) + 100000));
        LocalDateTime expirationTime = LocalDateTime.now().plusMinutes(5);

        if (user.getId() != null && !this.userRepo.existsById(user.getId())) {
            user = this.userRepo.findById(user.getId())
                    .orElseThrow(() -> new RuntimeException("User not found"));
        }

        Otp otp = new Otp();
        otp.setUser(user);
        otp.setOtpCode(otpCode);
        otp.setExpirationTime(expirationTime);

        otpRepository.save(otp);
        emailService.sendOtp(user.getEmail(), otpCode);

        return otpCode;
    }

    @Transactional
    public void verifyOtp(String otpCode) {
        Otp otp = otpRepository.findByOtpCode(otpCode)
                .orElseThrow(() -> new OtpNotFoundException("OTP not found or expired"));

        User user = otp.getUser();

        if (otp.isValid()) {
            user.setEnabled(true);
            userRepo.save(user);

            otpRepository.delete(otp); // OTP deleted successfully
        } else {
            otpRepository.delete(otp);
            userRepo.delete(user);

            throw new OtpNotFoundException("OTP verification failed, user deleted.");

        }
    }


}
