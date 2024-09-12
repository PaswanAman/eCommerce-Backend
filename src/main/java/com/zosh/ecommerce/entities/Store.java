package com.zosh.ecommerce.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "store")
public class Store {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "store_Id")
    private Long id;
    @Column(name = "store_name", unique = true,length = 255)
    private String storeName;
   @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "store_images", joinColumns = @JoinColumn(name = "store_id"))
    @Column(name = "image_name")
    private List<String> storeImages = new ArrayList<>();
    @Column(name = "store_panNumber", unique = true)
    private String panNumber;
    private String bankName;
    private String accountNumber;
    private String branchName;
    private String storeAddress;
    private String latitude;
    private String longitude;

    @OneToOne
    @JoinColumn(name = "seller_id",nullable = false)
    private User seller;
}
