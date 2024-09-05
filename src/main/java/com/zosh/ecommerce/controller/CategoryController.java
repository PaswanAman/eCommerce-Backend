package com.zosh.ecommerce.controller;

import com.zosh.ecommerce.Dto.CategoryDto;
import com.zosh.ecommerce.Dto.ProductDto;
import com.zosh.ecommerce.entities.Category;
import com.zosh.ecommerce.entities.Product;
import com.zosh.ecommerce.exception.ResourceNotFoundException;
import com.zosh.ecommerce.service.CategoryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/user")
public class CategoryController {
    @Autowired
    private CategoryService categoryService;

    private static final Logger logger = LoggerFactory.getLogger(CategoryController.class);

    @PostMapping("/admin/create-category")
    public ResponseEntity<?> createCategory(@ModelAttribute CategoryDto categoryDto,
                                                      @RequestParam("imageFile") MultipartFile imageFile) throws IOException {
        CategoryDto createdCategory = categoryService.createCategory(categoryDto, imageFile);
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("status","success","message","Category created successfully","category", createdCategory));
    }

    @GetMapping("/category/{id}")
    public ResponseEntity<?> getCategoryById(@PathVariable Long id){
        logger.info("Get Category By CategoryId API called");
        try{
            CategoryDto categoryDto = this.categoryService.getCategoryById(id);
            if (categoryDto == null){
                logger.info("Category Not Found");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("status","error","message","Category not found"));
            }
            logger.info("Category Found");
            return ResponseEntity.ok().body(Map.of("status","success","Category",categoryDto));
        } catch (ResourceNotFoundException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("status", "error", "message", e.getMessage()));
        }

    }

    @GetMapping("/allcategories")
    public ResponseEntity<?> getAllCategories(){
        List<CategoryDto> categories = categoryService.getAllCategories();
        return ResponseEntity.status(HttpStatus.OK).body(Map.of("status","success","message","Category Fetched Successfully","categories", categories));

    }

    @DeleteMapping("/admin/delete-category/{id}")
    public ResponseEntity<?> deleteCategoryById(@PathVariable Long id){
        try {
            this.categoryService.deleteCategoryById(id);
            return ResponseEntity.status(HttpStatus.OK).body(Map.of("status", "success", "message","category deleted"));
        } catch (ResourceNotFoundException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("status", "error", "message", "category not found"));

        }

    }
}
