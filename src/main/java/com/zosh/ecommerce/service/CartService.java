package com.zosh.ecommerce.service;

import com.zosh.ecommerce.Dto.CartDto;
import com.zosh.ecommerce.entities.Cart;
import org.springframework.stereotype.Service;

@Service
public interface CartService {
     CartDto addProductToCart(Long userId, Long productId, Integer totalQuantity);

     CartDto getUserCart(Long userId);
     CartDto removeProductFromCart(Long cartId, Long productId, Integer totalQuantity);

}
