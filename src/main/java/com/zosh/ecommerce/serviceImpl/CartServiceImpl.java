package com.zosh.ecommerce.serviceImpl;

import com.zosh.ecommerce.Dto.*;
import com.zosh.ecommerce.entities.*;
import com.zosh.ecommerce.exception.ResourceNotFoundException;
import com.zosh.ecommerce.repository.*;
import com.zosh.ecommerce.service.CartService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
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
    private OrderRepo orderRepo;

    @Autowired
    private CartQuantityRepo cartQuantityRepo;

    @Autowired
    private ModelMapper modelMapper;

    @Value("${picture.base-url}")
    private String baseurl;

    @Transactional
    public AddProductToCartDto addProductToCart(Long userId, Long productId, Integer productQuantity) {

        // Fetch Product
        Product product = productRepo.findById(productId).orElseThrow(() ->
                new ResourceNotFoundException("Product not found", "Id", productId));

        // Fetch Cart for the User
        Cart cart = cartRepo.findByUserId(userId).orElseThrow(() ->
                new ResourceNotFoundException("Cart not found", "UserId", userId));

        // Check if the product already exists in the cart
        Optional<CartQuantity> optionalCartQuantity = cartQuantityRepo.findCartQuantity(cart.getCartId(), productId);

        if (optionalCartQuantity.isPresent()) {
            // Product already in cart, update quantity
            CartQuantity existingCartQuantity = optionalCartQuantity.get();
            int currentQuantity = existingCartQuantity.getQuantity();
            int newQuantity = currentQuantity + productQuantity;

            if (newQuantity > 0) {
                // Update the quantity in the cart
                int rowsUpdated = cartQuantityRepo.updateCartQuantity(cart.getCartId(), productId, newQuantity);

                if (rowsUpdated > 0) {
                    cart.setTotalQuantity(cart.getTotalQuantity() + productQuantity);
                    cart.setTotalPrice(cart.getTotalPrice() + (product.getPrice() * productQuantity));
                    cartRepo.save(cart);  // Save the updated cart
                }
            } else {
                throw new IllegalArgumentException("Invalid quantity. Total quantity cannot be less than 1.");
            }
        } else {
            // If product doesn't exist, add new CartQuantity
            if (productQuantity > 0) {
                CartQuantity newCartQuantity = new CartQuantity();
                newCartQuantity.setCart(cart);
                newCartQuantity.setProduct(product);
                newCartQuantity.setQuantity(productQuantity);

                // Save new CartQuantity
                cartQuantityRepo.save(newCartQuantity);

                // Update cart total
                cart.setTotalQuantity(cart.getTotalQuantity() + productQuantity);
                cart.setTotalPrice(cart.getTotalPrice() + (product.getPrice() * productQuantity));
                cartRepo.save(cart);  // Save the updated cart
            } else {
                throw new IllegalArgumentException("Product quantity must be greater than zero.");
            }
        }

        // Create DTO for response
        AddProductToCartDto cartDto = new AddProductToCartDto();
        cartDto.setCartId(cart.getCartId());
        cartDto.setUserId(userId);
        cartDto.setProducts(modelMapper.map(product, ProductDto.class));  // Map Product to ProductDto
        cartDto.setQuantity(productQuantity);
        cartDto.setTotalPrice(cart.getTotalPrice());

        return cartDto;
    }

    @Transactional
    public RemoveProductFromCartDto removeProductFromCart(Long userId, Long productId, Integer removeQuantity) {

        // Fetch Cart for the User
        Cart cart = cartRepo.findByUserId(userId).orElseThrow(() ->
                new ResourceNotFoundException("Cart not found", "UserId", userId));

        // Find the CartQuantity entry for the specified product
        CartQuantity cartQuantity = cartQuantityRepo.findCartQuantity(cart.getCartId(), productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found in cart", "ProductId", productId));

        int currentQuantity = cartQuantity.getQuantity();
        double productPrice = cartQuantity.getProduct().getPrice();

        if (removeQuantity >= currentQuantity) {
            // Remove product from cart using custom query
            cartQuantityRepo.deleteCartQuantity(cart.getCartId(), productId);

            // Update cart total quantity and price
            cart.setTotalQuantity(cart.getTotalQuantity() - currentQuantity);
            cart.setTotalPrice(cart.getTotalPrice() - (productPrice * currentQuantity));
        } else {
            // Update the cart quantity if removal is less than current quantity
            int newQuantity = currentQuantity - removeQuantity;
            cartQuantity.setQuantity(newQuantity);
            cartQuantityRepo.save(cartQuantity);

            // Update cart total quantity and price
            cart.setTotalQuantity(cart.getTotalQuantity() - removeQuantity);
            cart.setTotalPrice(cart.getTotalPrice() - (productPrice * removeQuantity));
        }

        cartRepo.save(cart);  // Save the updated cart

        // Create DTO for response
        RemoveProductFromCartDto cartDto = new RemoveProductFromCartDto();
        cartDto.setCartId(cart.getCartId());
        cartDto.setUserId(userId);
        cartDto.setProductId(productId);
        cartDto.setRemainingQuantity(removeQuantity >= currentQuantity ? 0 : currentQuantity - removeQuantity);
        cartDto.setTotalQuantity(cart.getTotalQuantity());
        cartDto.setTotalPrice(cart.getTotalPrice());

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

//    @Transactional
//    @Override
//    public void deleteProductFromCart(Long userId, Long productId) {
//        // Debug input values
//        System.out.println("Deleting product with ID: " + productId + " from cart for user with ID: " + userId);
//
//        // Fetch the cart associated with the user
//        Cart cart = cartRepo.findByUserId(userId).orElseThrow(() -> {
//            System.out.println("Cart not found for user with ID: " + userId);
//            return new ResourceNotFoundException("Cart not found for user", "Id", userId);
//        });
//        System.out.println("Cart found: " + cart);
//
//        // Fetch the product by its ID
//        Product product = productRepo.findById(productId).orElseThrow(() -> {
//            System.out.println("Product not found with ID: " + productId);
//            return new ResourceNotFoundException("Product not found", "Id", productId);
//        });
//        System.out.println("Product found: " + product);
//
//        // Find the CartQuantity entry for the given product
//        CartQuantity cartQuantity = cartQuantityRepo.findByCartAndProduct(cart, product).orElseThrow(() -> {
//            System.out.println("Product with ID " + productId + " not found in cart for user " + userId);
//            return new ResourceNotFoundException("Product not found in cart", "Product ID", productId);
//        });
//        System.out.println("CartQuantity found: " + cartQuantity);
//
//        // Calculate the quantity and price to remove
//        int quantityToRemove = cartQuantity.getQuantity();
//        double priceToDeduct = product.getPrice() * quantityToRemove;
//        System.out.println("Quantity to remove: " + quantityToRemove);
//        System.out.println("Price to deduct: " + priceToDeduct);
//
//        // Update cart's total quantity and total price
//        cart.setTotalQuantity(cart.getTotalQuantity() - quantityToRemove);
//        cart.setTotalPrice(cart.getTotalPrice() - priceToDeduct);
//
//        // Ensure cart totals don't go below zero
//        cart.setTotalQuantity(Math.max(cart.getTotalQuantity(), 0));
//        cart.setTotalPrice(Math.max(cart.getTotalPrice(), 0));
//
//        System.out.println("Updated cart totals - Total Quantity: " + cart.getTotalQuantity() + ", Total Price: " + cart.getTotalPrice());
//
//        // Delete the CartQuantity entry
//        cartQuantityRepo.delete(cartQuantity);
//        System.out.println("Deleted CartQuantity entry for product ID: " + productId);
//
//        // Save the updated cart
//        cartRepo.save(cart);
//        System.out.println("Cart updated and saved successfully.");
//    }

    @Transactional
    public OrderDto checkout(Long userId) {
        Cart cart = cartRepo.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Cart not found for user: " + userId));

        List<CartQuantity> cartQuantities = cart.getCartQuantity();
        if (cartQuantities.isEmpty()) {
            throw new RuntimeException("Cart is empty. Cannot proceed with checkout.");
        }

        double totalAmount = 0.0;
        List<OrderItem> orderItems = new ArrayList<>();

        // Process each product in the cart
        for (CartQuantity cartQuantity : cartQuantities) {
            Product product = cartQuantity.getProduct();
            int quantity = cartQuantity.getQuantity();
            double itemTotal = product.getPrice() * quantity;
            totalAmount += itemTotal;

            OrderItem orderItem = new OrderItem();
            orderItem.setProductName(product.getTitle());
            orderItem.setPrice(product.getPrice());
            orderItem.setQuantity(quantity);
            orderItems.add(orderItem);
        }

        // Create the Order entity
        Order order = new Order();
//        order.setOrderId(null);
        order.setUser(cart.getUser());
        order.setTotalAmount(totalAmount);
        order.setOrderItems(orderItems);
        order.setOrderDate(LocalDateTime.now());

        // Save the order and clear the cart
        orderRepo.save(order);
        cartQuantities.forEach(cartQuantity -> cartQuantityRepo.delete(cartQuantity));
        cart.getProducts().clear();  // If using ManyToMany for products

        cart.setTotalQuantity(0);
        cart.setTotalPrice(0.0);
        cartRepo.save(cart);  // Persist the empty cart

        return modelMapper.map(order, OrderDto.class);
    }
}
