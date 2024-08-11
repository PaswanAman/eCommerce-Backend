package com.zosh.ecommerce.controller;

import com.zosh.ecommerce.Dto.CartDto;
import com.zosh.ecommerce.entities.Cart;
import com.zosh.ecommerce.service.CartService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/user/cart")
public class CartController {

    @Autowired
    private CartService cartService;

    private Logger logger = LoggerFactory.getLogger(CartController.class);

    @PostMapping("/buyer/{cartId}/addProduct/{productId}/{quantity}")
    public CartDto addProductToCart(@PathVariable Long cartId, @PathVariable Long productId, @PathVariable Integer quantity) {
        logger.info("Add Product to Cart API called");
        return cartService.addProductToCart(cartId, productId, quantity);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<CartDto> getCartByUserId(@PathVariable Long userId) {
        logger.info("Get Cart By User Id API called");
        try {
            CartDto cartDto = cartService.getCartByUserId(userId);
            logger.info("Get Product By Cart Id");
            return ResponseEntity.ok(cartDto);
        } catch (RuntimeException e) {
            logger.info("Error to get Cart By Id");
            return ResponseEntity.status(404).body(null);
        }
    }

    @DeleteMapping("/buyer/{cartId}/deleteCart/{productId}/{quantity}")
    public CartDto removeProductFromCart(@PathVariable Long cartId, @PathVariable Long productId, @PathVariable Integer quantity){
        logger.info("Delete Cart API called");
        logger.info("Cart deleted successfully");
        return cartService.removeProductFromCart(cartId, productId, quantity);

    }

}
