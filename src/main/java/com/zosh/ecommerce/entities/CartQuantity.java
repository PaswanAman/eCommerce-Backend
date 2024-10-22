package com.zosh.ecommerce.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "cart_quantity")
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Getter
@Setter
public class CartQuantity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long cartQuantityId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "cart_id")
    private Cart cart;


    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "product_id")
    private Product product;


    private Integer quantity;

    @Override
    public String toString() {
        return "CartQuantity{" +
                "cartQuantityId=" + cartQuantityId +
                ", quantity=" + quantity +
                // Avoid printing the cart or product directly
                '}';
    }

}
