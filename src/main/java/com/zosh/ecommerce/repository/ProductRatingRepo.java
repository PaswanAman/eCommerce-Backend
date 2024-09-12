package com.zosh.ecommerce.repository;

import com.zosh.ecommerce.entities.Product;
import com.zosh.ecommerce.entities.ProductRating;
import com.zosh.ecommerce.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRatingRepo extends JpaRepository<ProductRating,Long> {
    Optional<ProductRating> findByProductAndUser(Product product, User user);

    @Query("SELECT AVG(r.rating) FROM ProductRating r WHERE r.product.id = :productId")
    double calculateAverageRatingByProduct(@Param("productId") Long productId);
}
