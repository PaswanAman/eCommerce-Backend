package com.zosh.ecommerce.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CheckoutItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "checkout_id")
    private CheckOut checkout;
    private Long productId;

    private String productName;
    private int quantity;
    private double unitPrice;
    private double totalPrice;
//    private String imageUrl;
}
