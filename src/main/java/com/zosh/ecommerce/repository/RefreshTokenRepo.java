package com.zosh.ecommerce.repository;

import com.zosh.ecommerce.entities.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RefreshTokenRepo extends JpaRepository<RefreshToken, String> {

    Optional<RefreshToken> findByRefreshToken(String token);
}
