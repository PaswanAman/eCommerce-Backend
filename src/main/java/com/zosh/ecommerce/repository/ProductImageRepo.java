package com.zosh.ecommerce.repository;

import com.zosh.ecommerce.entities.ProductImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface ProductImageRepo extends JpaRepository<ProductImage, Long> {
}
