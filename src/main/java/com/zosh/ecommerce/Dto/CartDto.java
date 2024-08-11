package com.zosh.ecommerce.Dto;

import com.zosh.ecommerce.entities.Product;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CartDto {

    private String cartId;
    private String userId;
    private List<List<Product>> productId;
    private Integer quantity;
    private Double totalPrice;

}
