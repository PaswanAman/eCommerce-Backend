package com.zosh.ecommerce.Dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ChangePassword {
    private String oldPassword;

    @NotBlank(message = "Password is required")
    @Pattern(regexp = "^.{8,}$", message = "Password must be 8 character")
    private String newPassword;

    @NotBlank
    @Pattern(regexp = "^.{8,}$", message = "Password must be 8 character")
    private String confirmPassword;
}
