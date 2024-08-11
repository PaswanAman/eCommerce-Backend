package com.zosh.ecommerce.repository;

import com.zosh.ecommerce.entities.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ProductRepo extends JpaRepository<Product, Long> {
    List<Product> findByCategoryName(String categoryName);
    List<Product> findByTitleContainingIgnoreCase(String keyword);
    List<Product> findByExpirationDateLessThanEqualAndHistoryStatusFalse(LocalDateTime currentDate);
    List<Product> findByExpirationDateGreaterThanEqualAndHistoryStatusFalse(LocalDateTime currentDate);

}
