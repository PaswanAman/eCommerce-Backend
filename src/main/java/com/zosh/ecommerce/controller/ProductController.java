package com.zosh.ecommerce.controller;

import com.zosh.ecommerce.Dto.CategoryDto;
import com.zosh.ecommerce.Dto.ProductDto;
import com.zosh.ecommerce.entities.Category;
import com.zosh.ecommerce.exception.ResourceNotFoundException;
import com.zosh.ecommerce.repository.CategoryRepo;
import com.zosh.ecommerce.service.CategoryService;
import com.zosh.ecommerce.service.ProductService;
import jakarta.validation.Valid;
import org.apache.coyote.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/user")
public class ProductController {
    @Autowired
    private ProductService productService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private CategoryRepo categoryRepo;

    private static final Logger logger = LoggerFactory.getLogger(ProductController.class);

    @PostMapping("/seller/{sellerId}/create-products")
    public ResponseEntity<?> createProduct(@Valid @ModelAttribute ProductDto productDto,@PathVariable Long sellerId, @RequestParam("images") List<MultipartFile> images){
        try{
            logger.info("Product DTO: {}", productDto);
            logger.info("Product Image: {}", images);
            logger.info("Product sellerId: {}", sellerId);
            ProductDto createProduct = this.productService.createProduct(productDto,images,sellerId);
            logger.info("Product created successfully");
            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("status","success","message","Product created successfully"));
        } catch (IllegalArgumentException e){
            logger.error("Image support only");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("status","error", "message", e.getMessage()));

        } catch (Exception e) {
            logger.error("Failed to create product");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("status","error","message","Failed to create product"));
        }
    }

    @PutMapping("/seller/product/{productId}")
    public ResponseEntity<?> updateProduct(@RequestBody ProductDto productDto, @PathVariable Long productId){
        logger.info("Update Product API called");
        try{
            ProductDto updateProduct = this.productService.updateProduct(productDto, productId);
            logger.info("Product Updated Successfully");
            return ResponseEntity.ok().body(Map.of("status","success","message","Product updated successfully"));
        } catch (Exception e){
            logger.info("Failed to Update Product");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("status","error","message","Failed to update product"));
        }
    }

    @GetMapping("/product/{productId}")
    public ResponseEntity<?> getProduct(@PathVariable Long productId){
        logger.info("Get Product By ProductID API called");
        try{
            ProductDto productDto = this.productService.getProductById(productId);
            if (productDto == null){
                logger.info("Product Not Found");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("status","error","message","Product not found"));
            }
            logger.info("Product Found");
            return ResponseEntity.ok().body(Map.of("status","success","Product",productDto));
        } catch (ResourceNotFoundException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("status", "error", "message", e.getMessage()));
        }
    }

    @GetMapping("/seller/product/{sellerId}")
    public ResponseEntity<?> getProductsBySellerId(@PathVariable Long sellerId) {
        List<ProductDto> products = productService.getProductsBySellerId(sellerId);
        return ResponseEntity.status(HttpStatus.OK).body(Map.of("status","success","message","Products fetched successfully","products",products));
    }

    @GetMapping("/product/category/{categoryName}")
    public ResponseEntity<?> getProductByCategoryName(@PathVariable String categoryName){
        logger.info("Get product by category name api called");
        List<ProductDto> productDtos = productService.getProductsByCategoryName(categoryName);
        return ResponseEntity.status(HttpStatus.OK).body(Map.of("status","success","message","Products Fetched Successfully","products", productDtos));
    }

    @GetMapping("/buyer/search")
    public ResponseEntity<List<ProductDto>> searchProductByTitle(@RequestParam String keyword){
        logger.info("Search Api called");
        List<ProductDto> productDtos = productService.searchProductsByTitle(keyword);
        return ResponseEntity.ok(productDtos);
    }

    @GetMapping("/non-expired")
    public ResponseEntity<?> getAllNonExpiredProducts() {
        return ResponseEntity.status(HttpStatus.OK).body(Map.of("status", "success", "message","Product fetched Successfully","products",productService.getAllNonExpiredProducts()));

    }


}
