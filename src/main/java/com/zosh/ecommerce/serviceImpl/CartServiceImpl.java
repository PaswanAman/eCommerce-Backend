package com.zosh.ecommerce.serviceImpl;

import com.zosh.ecommerce.Dto.CartDto;
import com.zosh.ecommerce.Dto.ProductQuantityDto;
import com.zosh.ecommerce.entities.Cart;
import com.zosh.ecommerce.entities.CartQuantity;
import com.zosh.ecommerce.entities.Product;
import com.zosh.ecommerce.entities.ProductImage;
import com.zosh.ecommerce.exception.ResourceNotFoundException;
import com.zosh.ecommerce.repository.CartQuantityRepo;
import com.zosh.ecommerce.repository.CartRepo;
import com.zosh.ecommerce.repository.ProductRepo;
import com.zosh.ecommerce.repository.UserRepo;
import com.zosh.ecommerce.service.CartService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
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

    @Value("${picture.base-url}")
    private String baseurl;

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
    public CartDto getUserCart(Long userId) {
        Cart cart = cartRepo.findByUserId(userId).orElseThrow(() -> new ResourceNotFoundException("Cart not found","Id", userId));


        CartDto cartDto = new CartDto();
        cartDto.setCartId(cart.getCartId());
        cartDto.setUserId(userId);

        Integer totalQuantity = 0;
        List<ProductQuantityDto> productQuantityDtos = new ArrayList<>();

        for (CartQuantity cartQuantity : cart.getCartQuantity()) {
            Product product = cartQuantity.getProduct();
            totalQuantity += cartQuantity.getQuantity();


            ProductQuantityDto productQuantityDto = new ProductQuantityDto();
            productQuantityDto.setProductId(product.getProductId());
            productQuantityDto.setTitle(product.getTitle());
            productQuantityDto.setBrand(product.getBrand());
            productQuantityDto.setDescription(product.getDescription());
            productQuantityDto.setPrice(product.getPrice());
            productQuantityDto.setQuantity(cartQuantity.getQuantity());

            List<String> imageUrls= new ArrayList<>();
            for (ProductImage image : product.getImages()) {
                imageUrls.add( baseurl+"/api/v1/auth/picture/" +image.getImage());
            }

            productQuantityDto.setImageUrls(imageUrls);

            productQuantityDtos.add(productQuantityDto);
        }

        cartDto.setQuantity(totalQuantity);
        cartDto.setProducts(productQuantityDtos);
        cartDto.setTotalPrice(cart.getTotalPrice());

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
