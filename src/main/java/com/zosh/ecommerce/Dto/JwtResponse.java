package com.zosh.ecommerce.Dto;

import jdk.jfr.Name;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class JwtResponse {

    private String token;
    private String email;

    private String message;
    private Long userId;
    private String firstName;
    private String lastName;
//    private String email;
}
