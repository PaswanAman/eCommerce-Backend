package com.zosh.ecommerce.service;

import com.zosh.ecommerce.Dto.ProductRatingDto;
import org.springframework.stereotype.Service;

@Service
public interface ProductRatingService {
    ProductRatingDto addRating(String userToken, ProductRatingDto ratingDto);
}
