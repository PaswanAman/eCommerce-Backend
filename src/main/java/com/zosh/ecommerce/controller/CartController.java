package com.zosh.ecommerce.controller;

import com.zosh.ecommerce.Dto.AddProductToCartDto;
import com.zosh.ecommerce.Dto.CartDto;
import com.zosh.ecommerce.entities.Cart;
import com.zosh.ecommerce.exception.ResourceNotFoundException;
import com.zosh.ecommerce.service.CartService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/user/cart")
public class CartController {

    @Autowired
    private CartService cartService;

    private Logger logger = LoggerFactory.getLogger(CartController.class);

    @PostMapping("/buyer/addProduct/{userId}/{productId}/{quantity}")
    public ResponseEntity<?> addProductToCart(@PathVariable Long userId, @PathVariable Long productId, @PathVariable Integer quantity) {
        logger.info("Add Product to Cart API called");
        try {
            AddProductToCartDto addProductToCartDto = cartService.addProductToCart(userId, productId, quantity);
            return ResponseEntity.status(HttpStatus.OK).body(Map.of("status","success","message","add product to cart successful","cart",addProductToCartDto));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("status","error","message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("status","error","message", "An unexpected error occurred"));
        }
    }

    @GetMapping("/{userId}")
    public ResponseEntity<?> getUserCart(@PathVariable Long userId) {
        logger.info("Get Cart API called");
        try {
            CartDto cartDto = cartService.getUserCart(userId);
            return ResponseEntity.status(HttpStatus.OK).body(Map.of("status","success","message","get cart by user successful","cart",cartDto));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("status","error","message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("status","error","message","An unexpected error occurred"));
        }
    }



    @DeleteMapping("/buyer/{cartId}/deleteCart/{productId}/{quantity}")
    public CartDto removeProductFromCart(@PathVariable Long cartId, @PathVariable Long productId, @PathVariable Integer quantity){
        logger.info("Delete Cart API called");
        return cartService.removeProductFromCart(cartId, productId, quantity);

    }

}
