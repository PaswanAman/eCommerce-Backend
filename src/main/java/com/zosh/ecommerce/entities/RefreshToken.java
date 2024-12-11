package com.zosh.ecommerce.entities;

import jakarta.annotation.security.DenyAll;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
public class RefreshToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long tokenId;

    private String refreshToken;

    private Instant expiry;

    @OneToOne
    private User user;
}
