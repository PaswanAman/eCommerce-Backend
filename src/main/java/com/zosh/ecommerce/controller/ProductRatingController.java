package com.zosh.ecommerce.controller;

import com.zosh.ecommerce.Dto.ProductRatingDto;
import com.zosh.ecommerce.exception.ResourceNotFoundException;
import com.zosh.ecommerce.service.ProductRatingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/rating")
public class ProductRatingController {
    @Autowired
    private ProductRatingService productRatingService;

    @PostMapping("/{productId}")
    public ResponseEntity<?> addRating(@RequestHeader("Authorization") String userToken,
                                                      @PathVariable Long productId,
                                                      @RequestBody ProductRatingDto productRatingDto) {
        try {
            productRatingDto.setProductId(productId);
            ProductRatingDto responseDto = productRatingService.addRating(userToken, productRatingDto);
            return ResponseEntity.ok(responseDto);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

}
