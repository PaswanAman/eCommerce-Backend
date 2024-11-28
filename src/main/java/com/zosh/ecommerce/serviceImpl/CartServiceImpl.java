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
        newCartQuantity.setQuantity(Math.max(productQuantity, 1));

        System.out.println("Before Adding: Cart ID: " + cart.getCartId());
        System.out.println("Current Cart Quantity List Size: " + cart.getCartQuantity().size());// Ensure at least 1
        cart.getCartQuantity().add(newCartQuantity);
        System.out.println("After Adding: Cart ID: " + cart.getCartId());
        System.out.println("Updated Cart Quantity List Size: " + cart.getCartQuantity().size());


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

    @Transactional
    @Override
    public void deleteProductFromCart(Long userId, Long productId) {
        // Debug input values
        System.out.println("Deleting product with ID: " + productId + " from cart for user with ID: " + userId);

        // Fetch the cart associated with the user
        Cart cart = cartRepo.findByUserId(userId).orElseThrow(() -> {
            System.out.println("Cart not found for user with ID: " + userId);
            return new ResourceNotFoundException("Cart not found for user", "Id", userId);
        });
        System.out.println("Cart found: " + cart);

        // Fetch the product by its ID
        Product product = productRepo.findById(productId).orElseThrow(() -> {
            System.out.println("Product not found with ID: " + productId);
            return new ResourceNotFoundException("Product not found", "Id", productId);
        });
        System.out.println("Product found: " + product);

        // Find the CartQuantity entry for the given product
        CartQuantity cartQuantity = cartQuantityRepo.findByCartAndProduct(cart, product).orElseThrow(() -> {
            System.out.println("Product with ID " + productId + " not found in cart for user " + userId);
            return new ResourceNotFoundException("Product not found in cart", "Product ID", productId);
        });
        System.out.println("CartQuantity found: " + cartQuantity);

        // Calculate the quantity and price to remove
        int quantityToRemove = cartQuantity.getQuantity();
        double priceToDeduct = product.getPrice() * quantityToRemove;
        System.out.println("Quantity to remove: " + quantityToRemove);
        System.out.println("Price to deduct: " + priceToDeduct);

        // Update cart's total quantity and total price
        cart.setTotalQuantity(cart.getTotalQuantity() - quantityToRemove);
        cart.setTotalPrice(cart.getTotalPrice() - priceToDeduct);

        // Ensure cart totals don't go below zero
        cart.setTotalQuantity(Math.max(cart.getTotalQuantity(), 0));
        cart.setTotalPrice(Math.max(cart.getTotalPrice(), 0));

        System.out.println("Updated cart totals - Total Quantity: " + cart.getTotalQuantity() + ", Total Price: " + cart.getTotalPrice());

        // Delete the CartQuantity entry
        cartQuantityRepo.delete(cartQuantity);
        System.out.println("Deleted CartQuantity entry for product ID: " + productId);

        // Save the updated cart
        cartRepo.save(cart);
        System.out.println("Cart updated and saved successfully.");
    }

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
