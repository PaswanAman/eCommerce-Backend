package com.zosh.ecommerce.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "cart_quantity")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CartQuantity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long cartQuantityId;

    @ManyToOne
    @JoinColumn(name = "cart_id",nullable = false)
    private Cart cart;


    @ManyToOne()
    @JoinColumn(name = "product_id",nullable = false)
    private Product product;


    private Integer quantity;


}
