package com.zosh.ecommerce.Dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AddProductToCartDto {

    private Long cartId;
    private Long userId;
    private ProductDto products;
    private Integer quantity;
    private Double totalPrice;
}
