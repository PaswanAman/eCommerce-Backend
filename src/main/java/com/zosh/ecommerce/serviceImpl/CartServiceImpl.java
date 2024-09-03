package com.zosh.ecommerce.serviceImpl;

import com.zosh.ecommerce.Dto.CartDto;
import com.zosh.ecommerce.Dto.ProductDto;
import com.zosh.ecommerce.Dto.UserDto;
import com.zosh.ecommerce.entities.Cart;
import com.zosh.ecommerce.entities.CartQuantity;
import com.zosh.ecommerce.entities.Product;
import com.zosh.ecommerce.entities.User;
import com.zosh.ecommerce.exception.ResourceNotFoundException;
import com.zosh.ecommerce.repository.CartQuantityRepo;
import com.zosh.ecommerce.repository.CartRepo;
import com.zosh.ecommerce.repository.ProductRepo;
import com.zosh.ecommerce.repository.UserRepo;
import com.zosh.ecommerce.service.CartService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Transactional
@Service
public class CartServiceImpl implements CartService {


    @Autowired
    private UserRepo userRepo;

    @Autowired
    private CartRepo cartRepo;

    @Autowired
    private ProductRepo productRepo;

    @Autowired
    private CartQuantityRepo cartQuantityRepo;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public CartDto addProductToCart(Long userId, Long productId, Integer productQuantity) {
//
        Product product = productRepo.findById(productId).orElseThrow();
        Cart cart = cartRepo.findByUserId(userId).orElseThrow();
        CartQuantity cartQuantity = new CartQuantity();
        cartQuantity.setCart(cart);
        cartQuantity.setProduct(product);
        cartQuantity.setQuantity(productQuantity);
        cart.getCartQuantity().add(cartQuantity);
        productRepo.save(product);
        cartQuantityRepo.save(cartQuantity);

        Integer qnt = cart.getTotalQuantity();
        cart.setTotalQuantity(qnt + productQuantity);


        Double price = cart.getTotalPrice();
        cart.setTotalPrice(price + (product.getPrice() * productQuantity));

        cart.getProducts().add(product);

        Cart cartUser = this.cartRepo.save(cart);
        return this.modelMapper.map(cartUser, CartDto.class);
    }

    @Override
    public CartDto getCartByUserId(Long userId) {
        Cart cart = this.cartRepo.findById(userId).orElseThrow();
        List<List<Product>> products = cartRepo.findById(cart.getCartId()).stream()
                .map(Cart::getProducts)
                .toList();
        CartDto cartDto = modelMapper.map(cart, CartDto.class);
        cartDto.setProductId(products);
        return cartDto;

    }

    @Override
    public CartDto removeProductFromCart(Long cartId, Long productId, Integer productQuantity) {
        Cart cart = cartRepo.findById(cartId).orElseThrow();
        Product product = productRepo.findById(productId).orElseThrow();
        CartQuantity cartQuantity = cartQuantityRepo.findByCartAndProduct(cart, product);

        Integer existingQuantity = cartQuantity.getQuantity();
        if (existingQuantity <= productQuantity) {
            cart.getCartQuantity().remove(cartQuantity);
            cart.getProducts().remove(product);
            cartQuantityRepo.deleteCartQuantityById(cartQuantity.getCartQuantityId());
            System.out.println("CartQuantity deleted and product removed from cart");
        } else {

            cartQuantity.setQuantity(existingQuantity - productQuantity);
            cartQuantityRepo.save(cartQuantity);
        }

        Integer qnt = cart.getTotalQuantity();
        if (qnt == 0) {
            cartRepo.delete(cart);
        } else {
            cart.setTotalQuantity(qnt - productQuantity);
        }
        Double price = cart.getTotalPrice();
        cart.setTotalPrice(price - (product.getPrice() * productQuantity));

        Cart cartUser = this.cartRepo.save(cart);
        return this.modelMapper.map(cartUser, CartDto.class);
    }
}
