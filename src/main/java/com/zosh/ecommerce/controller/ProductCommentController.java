package com.zosh.ecommerce.controller;

import com.zosh.ecommerce.Dto.ProductCommentDto;
import com.zosh.ecommerce.Dto.ProductRatingDto;
import com.zosh.ecommerce.exception.ResourceNotFoundException;
import com.zosh.ecommerce.service.ProductCommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/comment")
public class ProductCommentController {
    @Autowired
    private ProductCommentService productCommentService;

    @PostMapping("/{productId}")
    public ResponseEntity<?> addComment(@RequestHeader("Authorization") String userToken,
                                       @PathVariable Long productId,
                                       @RequestBody ProductCommentDto productCommentDto) {
        try {
//            productCommentDto.s(productId);
            ProductCommentDto responseDto = productCommentService.addComment(userToken,productId, productCommentDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("status","success","message",responseDto));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Error: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred: " + e.getMessage());
        }
    }
}
