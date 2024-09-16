package com.zosh.ecommerce.repository;

import com.zosh.ecommerce.entities.Otp;
import com.zosh.ecommerce.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface OtpRepo extends JpaRepository<Otp,Long> {
    Optional<Otp> findByOtpCodeAndUser(String otpCode, User user);
    int deleteByExpirationTimeBefore(LocalDateTime expirationTime);
}
