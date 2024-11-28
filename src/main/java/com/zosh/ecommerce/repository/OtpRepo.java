package com.zosh.ecommerce.repository;

import com.zosh.ecommerce.entities.Otp;
import com.zosh.ecommerce.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface OtpRepo extends JpaRepository<Otp,Long> {
//    Optional<Otp> findByOtpCodeAndUser(String otpCode, User user);
//    int deleteByExpirationTimeBefore(LocalDateTime expirationTime);
    List<Otp> findByExpiryDateBefore(LocalDateTime expiryDate);
    Otp findByOtpCode(String otpCode);
//    Optional<Otp> findByOtpCode(String otpCode);
}
