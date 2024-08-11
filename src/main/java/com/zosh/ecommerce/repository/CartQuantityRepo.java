package com.zosh.ecommerce.repository;

import com.zosh.ecommerce.entities.Cart;
import com.zosh.ecommerce.entities.CartQuantity;
import com.zosh.ecommerce.entities.Product;
import com.zosh.ecommerce.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface CartQuantityRepo extends JpaRepository<CartQuantity, Long> {
    CartQuantity  findByCartAndProduct(Cart cart,Product product);
    @Modifying
    @Transactional
    @Query("DELETE FROM CartQuantity c WHERE c.id = ?1")
    void deleteCartQuantityById(Long id);

}
