package com.zosh.ecommerce.service;

import com.zosh.ecommerce.Dto.AddProductToCartDto;
import com.zosh.ecommerce.Dto.CartDto;
import com.zosh.ecommerce.Dto.OrderDto;
import com.zosh.ecommerce.Dto.RemoveProductFromCartDto;
import com.zosh.ecommerce.entities.Cart;
import org.springframework.stereotype.Service;

@Service
public interface CartService {
//     AddProductToCartDto addProductToCart(Long userId, Long productId, Integer totalQuantity);
     AddProductToCartDto addProductToCart(Long userId, Long productId, Integer quantity) throws Exception;

     CartDto getUserCart(Long userId);
//     CartDto removeProductFromCart(Long cartId, Long productId, Integer totalQuantity);
//     void deleteProductFromCart(Long userId, Long productId);
     OrderDto checkout(Long userId);

     RemoveProductFromCartDto removeProductFromCart(Long userId, Long productId, Integer removeQuantity);


}
