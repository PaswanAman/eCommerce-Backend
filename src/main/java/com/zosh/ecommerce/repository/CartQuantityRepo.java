package com.zosh.ecommerce.repository;

import com.zosh.ecommerce.entities.Cart;
import com.zosh.ecommerce.entities.CartQuantity;
import com.zosh.ecommerce.entities.Product;
import com.zosh.ecommerce.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface CartQuantityRepo extends JpaRepository<CartQuantity, Long> {
    Optional<CartQuantity>  findByCartAndProduct(Cart cart, Product product);
//    @Modifying
//    @Transactional
//    @Query("DELETE FROM CartQuantity c WHERE c.id = ?1")
//    void deleteCartQuantityById(Long id);


    @Query("SELECT cq FROM CartQuantity cq WHERE cq.cart.cartId = :cartId AND cq.product.productId = :productId")
    Optional<CartQuantity> findCartQuantity(@Param("cartId") Long cartId, @Param("productId") Long productId);

    @Modifying
    @Query("UPDATE CartQuantity cq SET cq.quantity = :quantity WHERE cq.cart.cartId = :cartId AND cq.product.productId = :productId")
    int updateCartQuantity(@Param("cartId") Long cartId, @Param("productId") Long productId, @Param("quantity") Integer quantity);

//    @Modifying
//    @Query("INSERT INTO CartQuantity (cart, product, quantity) VALUES (:cart, :product, :quantity)")
//    void insertCartQuantity(@Param("cart") Cart cart, @Param("product") Product product, @Param("quantity") Integer quantity);

//    @Query("SELECT cq FROM CartQuantity cq WHERE cq.cart.cartId = :cartId AND cq.product.id = :productId")
//    Optional<CartQuantity> findCartQuantity(@Param("cartId") Long cartId, @Param("productId") Long productId);

    @Modifying
    @Query("DELETE FROM CartQuantity cq WHERE cq.cart.cartId = :cartId AND cq.product.productId = :productId")
    void deleteCartQuantity(@Param("cartId") Long cartId, @Param("productId") Long productId);
}
