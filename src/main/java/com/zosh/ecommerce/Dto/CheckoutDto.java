package com.zosh.ecommerce.Dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CheckoutDto {
    private Long cartId;
    private Long userId;
    private List<CheckoutProductDto> products;
    private double totalPriceForCheckout;
//    private String message;
}
