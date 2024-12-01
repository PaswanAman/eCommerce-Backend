package com.zosh.ecommerce.Dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CheckoutProductDto {
    private String productName;
    private int quantity;
    private double unitPrice;
    private double totalPrice;
//    private String imageUrl;
}
