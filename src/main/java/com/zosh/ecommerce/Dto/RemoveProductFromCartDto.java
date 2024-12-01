package com.zosh.ecommerce.Dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RemoveProductFromCartDto {
    private Long cartId;
    private Long userId;
    private Long productId;
    private int remainingQuantity;
    private int totalQuantity;
    private double totalPrice;
}
