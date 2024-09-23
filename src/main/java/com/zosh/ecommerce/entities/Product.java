package com.zosh.ecommerce.entities;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "Product")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long productId;
    private String title;
    private String brand;
    private String description;
    private Double price;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime addedDate;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime expirationDate;
    private Boolean historyStatus;
    private Boolean hiddenPost;
    private boolean sold;

    @ManyToOne
    @JoinColumn(name = "seller_id",nullable = false)
    @JsonIgnore
    private User seller;


    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<CartQuantity> cartQuantity;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<ProductImage> images;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;


    @Override
    public String toString() {
        return "Product{" +
                "productId=" + productId +
                ", title='" + title + '\'' +
                ", price=" + price +
                // Avoid printing the full cart or other entities
                '}';
    }


}
