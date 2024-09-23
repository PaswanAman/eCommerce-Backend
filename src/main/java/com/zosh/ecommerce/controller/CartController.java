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

    @PostMapping("/buyer/addProduct/{userId}/{productId}/{quantity}")
    public CartDto addProductToCart(@PathVariable Long userId, @PathVariable Long productId, @PathVariable Integer quantity) {
        logger.info("Add Product to Cart API called");
        return cartService.addProductToCart(userId, productId, quantity);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<CartDto> getUserCart(@PathVariable Long userId) {
        // Call the service method to fetch the cart for the user
        CartDto cartDto = cartService.getUserCart(userId);

        // Return the CartDto in the response
        return ResponseEntity.ok(cartDto);
    }



    @DeleteMapping("/buyer/{cartId}/deleteCart/{productId}/{quantity}")
    public CartDto removeProductFromCart(@PathVariable Long cartId, @PathVariable Long productId, @PathVariable Integer quantity){
        logger.info("Delete Cart API called");
        logger.info("Cart deleted successfully");
        return cartService.removeProductFromCart(cartId, productId, quantity);

    }

}
