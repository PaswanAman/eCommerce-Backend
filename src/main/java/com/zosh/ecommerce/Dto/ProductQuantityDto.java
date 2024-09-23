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
public class ProductQuantityDto {
    private Long productId;
    private String title;
    private String brand;
    private String description;
    private Double price;
    private Integer quantity; // Quantity in the cart
    private List<String> imageUrls;
}
