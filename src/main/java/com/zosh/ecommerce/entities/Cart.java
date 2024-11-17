package com.zosh.ecommerce.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Entity
@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
//@ToString
@Table(name="cart")
public class Cart {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long cartId;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;


    @ManyToMany(fetch = FetchType.LAZY,cascade = CascadeType.ALL)
    @JoinTable(name = "cart_products", joinColumns = @JoinColumn(name = "cart_id"), inverseJoinColumns = @JoinColumn(name = "product_id"))
    @JsonIgnore
    private List<Product> products;


    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, fetch = FetchType.LAZY,orphanRemoval = true)
    private List<CartQuantity> cartQuantity;


    private Integer totalQuantity;
    private Double totalPrice;

//    @Override
//    public String toString() {
//        return "Cart{" +
//                "cartId=" + cartId +
//                ", totalQuantity=" + totalQuantity +
//                ", totalPrice=" + totalPrice +
//                // Avoid direct references to User or Product here
//                '}';
//    }




}
