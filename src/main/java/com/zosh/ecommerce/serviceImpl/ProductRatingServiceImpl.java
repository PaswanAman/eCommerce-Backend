package com.zosh.ecommerce.serviceImpl;

import com.zosh.ecommerce.Dto.ProductRatingDto;
import com.zosh.ecommerce.config.JwtTokenHelper;
import com.zosh.ecommerce.entities.Product;
import com.zosh.ecommerce.entities.ProductRating;
import com.zosh.ecommerce.entities.User;
import com.zosh.ecommerce.exception.ResourceNotFoundException;
import com.zosh.ecommerce.repository.ProductRatingRepo;
import com.zosh.ecommerce.repository.ProductRepo;
import com.zosh.ecommerce.repository.UserRepo;
import com.zosh.ecommerce.service.ProductRatingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProductRatingServiceImpl implements ProductRatingService {
    @Autowired
    private UserRepo userRepo;
    @Autowired
    private ProductRepo productRepo;
    @Autowired
    private ProductRatingRepo productRatingRepo;
    @Autowired
    private JwtTokenHelper jwtTokenHelper;

    @Override
    public ProductRatingDto addRating(String userToken, ProductRatingDto ratingDto) {
        try {
            String token = userToken.replaceAll("Bearer", " ").trim();
            String username = jwtTokenHelper.getUsernameFromToken(token);
            User user = this.userRepo.findByEmail(username).orElseThrow(() -> new ResourceNotFoundException("User", "email", username));

            Product product = productRepo.findById(ratingDto.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException("Product", "Id", ratingDto.getProductId()));

            ProductRating existingRating = productRatingRepo.findByProductAndUser(product, user)
                    .orElse(new ProductRating());


            existingRating.setProduct(product);
            existingRating.setUser(user);
            existingRating.setRating(ratingDto.getRating());
            existingRating.setReview(ratingDto.getReview());


            productRatingRepo.save(existingRating);


            double averageRating = productRatingRepo.calculateAverageRatingByProduct(product.getProductId());


            ratingDto.setAverageRating(averageRating);


            return ratingDto;
        } catch (Exception e) {

            throw new RuntimeException("Failed to add rating", e);
        }
    }
}
