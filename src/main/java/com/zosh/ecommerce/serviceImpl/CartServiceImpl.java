package com.zosh.ecommerce.serviceImpl;

import com.zosh.ecommerce.Dto.AddProductToCartDto;
import com.zosh.ecommerce.Dto.CartDto;
import com.zosh.ecommerce.Dto.ProductDto;
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
    public AddProductToCartDto addProductToCart(Long userId, Long productId, Integer productQuantity) {

        Product product = productRepo.findById(productId).orElseThrow(() ->
                new ResourceNotFoundException("product with", "Id", productId));

        Cart cart = cartRepo.findByUserId(userId).orElseThrow(() ->
                new ResourceNotFoundException("cart with user", "Id", userId));

        CartQuantity existingCartQuantity = null;
        for (CartQuantity cartQuantity : cart.getCartQuantity()) {
            if (cartQuantity.getProduct().getProductId().equals(productId)) {
                existingCartQuantity = cartQuantity;
                break;
            }
        }

        if (existingCartQuantity != null) {
            // Product already exists in the cart, update its quantity and total price
            int newQuantity = existingCartQuantity.getQuantity() + productQuantity;

            // Ensure the product quantity does not go below 1
            if (newQuantity < 1) {
                newQuantity = 1;
            }

            int quantityDifference = newQuantity - existingCartQuantity.getQuantity();

            existingCartQuantity.setQuantity(newQuantity);

            // Update cart's total quantity and total price
            cart.setTotalQuantity(cart.getTotalQuantity() + quantityDifference);
            cart.setTotalPrice(cart.getTotalPrice() + (product.getPrice() * quantityDifference));

            // Save updated cart and cartQuantity
            cartQuantityRepo.save(existingCartQuantity);
            cartRepo.save(cart);

            // Create and return AddProductToCartDto with the updated product
            AddProductToCartDto cartDto = new AddProductToCartDto();
            cartDto.setCartId(cart.getCartId());
            cartDto.setUserId(userId);
            cartDto.setProducts(modelMapper.map(product, ProductDto.class));
            cartDto.setQuantity(existingCartQuantity.getQuantity());
            cartDto.setTotalPrice(cart.getTotalPrice());

            return cartDto;
        }

        // If the product is not in the cart, add a new CartQuantity
        CartQuantity newCartQuantity = new CartQuantity();
        newCartQuantity.setCart(cart);
        newCartQuantity.setProduct(product);
        newCartQuantity.setQuantity(Math.max(productQuantity, 1)); // Ensure at least 1
        cart.getCartQuantity().add(newCartQuantity);

        // Update cart's total quantity and total price
        cart.setTotalQuantity(cart.getTotalQuantity() + Math.max(productQuantity, 1));
        cart.setTotalPrice(cart.getTotalPrice() + (product.getPrice() * Math.max(productQuantity, 1)));

        // Save new cartQuantity and updated cart
        cartQuantityRepo.save(newCartQuantity);
        Cart updatedCart = cartRepo.save(cart);

        // Create and return AddProductToCartDto with the newly added product
        AddProductToCartDto cartDto = new AddProductToCartDto();
        cartDto.setCartId(updatedCart.getCartId());
        cartDto.setUserId(userId);
        cartDto.setProducts(modelMapper.map(product, ProductDto.class));
        cartDto.setQuantity(newCartQuantity.getQuantity());
        cartDto.setTotalPrice(updatedCart.getTotalPrice());

        return cartDto;
    }


    @Override
    public CartDto getUserCart(Long userId) {
        Cart cart = cartRepo.findByUserId(userId).orElseThrow(() ->
                new ResourceNotFoundException("Cart not found","Id", userId));


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


//    @Override
//    public CartDto removeProductFromCart(Long cartId, Long productId, Integer productQuantity) {
//        Cart cart = cartRepo.findById(cartId).orElseThrow();
//        Product product = productRepo.findById(productId).orElseThrow();
//        CartQuantity cartQuantity = cartQuantityRepo.findByCartAndProduct(cart, product);
//
//        Integer existingQuantity = cartQuantity.getQuantity();
//        if (existingQuantity <= productQuantity) {
//            cart.getCartQuantity().remove(cartQuantity);
//            cart.getProducts().remove(product);
//            cartQuantityRepo.deleteCartQuantityById(cartQuantity.getCartQuantityId());
//            System.out.println("CartQuantity deleted and product removed from cart");
//        } else {
//
//            cartQuantity.setQuantity(existingQuantity - productQuantity);
//            cartQuantityRepo.save(cartQuantity);
//        }
//
//        Integer qnt = cart.getTotalQuantity();
//        if (qnt == 0) {
//            cartRepo.delete(cart);
//        } else {
//            cart.setTotalQuantity(qnt - productQuantity);
//        }
//        Double price = cart.getTotalPrice();
//        cart.setTotalPrice(price - (product.getPrice() * productQuantity));
//
//        Cart cartUser = this.cartRepo.save(cart);
//        return this.modelMapper.map(cartUser, CartDto.class);
//    }

    @Override
    public void deleteProductFromCart(Long userId, Long productId) {
        Cart cart = cartRepo.findByUserId(userId).orElseThrow(() ->
                new ResourceNotFoundException("Cart not found for user", "Id", userId));

        // Find the cartQuantity for the given product
        CartQuantity cartQuantity = cart.getCartQuantity().stream()
                .filter(q -> q.getProduct().getProductId().equals(productId))
                .findFirst()
                .orElseThrow(() ->
                        new ResourceNotFoundException("Product not found in cart", "Id", productId));

        // Update cart totals
        int quantityToRemove = cartQuantity.getQuantity();
        cart.setTotalQuantity(cart.getTotalQuantity() - quantityToRemove);
        cart.setTotalPrice(cart.getTotalPrice() - (cartQuantity.getProduct().getPrice() * quantityToRemove));

        // Remove the product from the cart's product list
        cart.getProducts().remove(cartQuantity.getProduct());

        // Remove the CartQuantity from the cart
        cart.getCartQuantity().remove(cartQuantity);

        // Delete the CartQuantity entry
        cartQuantityRepo.delete(cartQuantity);

        // Save the updated cart
        cartRepo.save(cart);
    }
}
