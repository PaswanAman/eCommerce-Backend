package com.zosh.ecommerce.Dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@ToString
@Getter
@Setter
public class SellerDto {
    private Long id;
    private String firstName;
    private String lastName;
    private String fullName;
    private String mobileNumber;
    private String email;
    private String password;
    private String picture;
    private String pictureUrl;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime createdDate;
//    private String categoryName;


    public String getFullName() {
        return getFirstName() + " " + getLastName();
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

}
