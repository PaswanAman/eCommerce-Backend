package com.zosh.ecommerce.service;

import com.zosh.ecommerce.Dto.CategoryDto;
import com.zosh.ecommerce.entities.Category;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface CategoryService {

    CategoryDto createCategory(CategoryDto categoryDto);
    CategoryDto findByName(String name);
    CategoryDto getCategoryById(Long id);
    List<CategoryDto> getAllCategories();
    void deleteCategoryById(Long id);


}
