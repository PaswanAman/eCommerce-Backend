package com.zosh.ecommerce.Dto;

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

}
