package com.zosh.ecommerce.service;

import com.zosh.ecommerce.Dto.CategoryDto;
import com.zosh.ecommerce.entities.Category;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
public interface CategoryService {

    CategoryDto createCategory(CategoryDto categoryDto, MultipartFile imageFile) throws IOException;
    CategoryDto findByName(String name);
    CategoryDto getCategoryById(Long id);
    List<CategoryDto> getAllCategories();
    void deleteCategoryById(Long id);


}
