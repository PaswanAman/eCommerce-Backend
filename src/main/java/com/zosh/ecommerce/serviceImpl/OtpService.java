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

    private static final int OTP_EXPIRATION_MINUTES = 5;
    public Otp createOtp(User user, String otp) {
        Otp token = new Otp();
        token.setUser(user);
        token.setOtpCode(otp);
        token.setCreatedDate(LocalDateTime.now());
        token.setExpiryDate(LocalDateTime.now().plusMinutes(OTP_EXPIRATION_MINUTES));
        return otpRepository.save(token);
    }
    public Otp getToken(String otp) {
        Otp token = otpRepository.findByOtpCode(otp);
        if (token != null && isTokenExpired(token)) {
            deleteToken(token);
            return null;
        }
        return token;
    }

    boolean isTokenExpired(Otp token) {
        return LocalDateTime.now().isAfter(token.getCreatedDate().plusMinutes(OTP_EXPIRATION_MINUTES));
    }
    public void deleteToken(Otp token) {
        otpRepository.delete(token);
    }

//    @Transactional
//    public String generateOtp(User user) {
//        String otpCode = String.valueOf((int) ((Math.random() * (999999 - 100000)) + 100000));
//        LocalDateTime expirationTime = LocalDateTime.now().plusMinutes(5);
//
//        if (user.getId() != null && !this.userRepo.existsById(user.getId())) {
//            user = this.userRepo.findById(user.getId())
//                    .orElseThrow(() -> new RuntimeException("User not found"));
//        }
//
//        Otp otp = new Otp();
//        otp.setUser(user);
//        otp.setOtpCode(otpCode);
//        otp.setExpirationTime(expirationTime);
//
//        otpRepository.save(otp);
//        emailService.sendOtp(user.getEmail(), otpCode);
//
//        return otpCode;
//    }
//
//    public void verifyOtp(String otpCode) {
//        Otp otp = otpRepository.findByOtpCode(otpCode)
//                .orElseThrow(() -> new OtpNotFoundException("OTP not found or expired"));
//
//        if (otp.isValid()) {
//
//            User user = otp.getUser();
//
//
//            user.setEnabled(true);
//            userRepo.save(user);
//
//            // Delete the OTP from the database, not the user
//            otpRepository.delete(otp);
//        } else {
//            throw new OtpNotFoundException("OTP verification failed");
//        }
//    }


}
