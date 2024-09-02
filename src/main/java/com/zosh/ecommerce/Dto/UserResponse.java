package com.zosh.ecommerce.Dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString

public class UserResponse {
    private String token;
    private String email;
    private String message;
    private Long userId;
    private String firstName;
    private String lastName;
    private String pictureUrl;
    private String role;

}
