package com.zosh.ecommerce.Dto;

import jakarta.persistence.Access;
import jakarta.persistence.Table;
import lombok.*;

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JwtAuthRequest {

    private String email;

    private String password;
}
