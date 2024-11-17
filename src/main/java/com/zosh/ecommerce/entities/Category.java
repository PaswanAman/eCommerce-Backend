package com.zosh.ecommerce.entities;


import jakarta.persistence.*;
import lombok.*;

import java.util.Set;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
//@ToString
@Table(name = "category")
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    private String name;
    private String imageName;
    private String description;


    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL,orphanRemoval = true)
    private Set<Product> products;

}
