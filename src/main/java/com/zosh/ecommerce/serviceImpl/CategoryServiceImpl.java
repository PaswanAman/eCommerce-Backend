package com.zosh.ecommerce.serviceImpl;

import com.zosh.ecommerce.Dto.CategoryDto;
import com.zosh.ecommerce.Dto.ProductDto;
import com.zosh.ecommerce.entities.Category;
import com.zosh.ecommerce.entities.Product;
import com.zosh.ecommerce.exception.ResourceNotFoundException;
import com.zosh.ecommerce.repository.CategoryRepo;
import com.zosh.ecommerce.service.CategoryService;
import com.zosh.ecommerce.service.FileService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoryServiceImpl implements CategoryService {
    @Autowired
    private CategoryRepo categoryRepo;

    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private FileService fileService;


    @Value("${picture.base-url}")
    private String baseUrl;


    @Override
    public CategoryDto createCategory(CategoryDto categoryDto, MultipartFile imageFile) throws IOException {
        Category category = modelMapper.map(categoryDto, Category.class);

        Category existingCategory = categoryRepo.findByName(categoryDto.getName());


        // Check if the category already exists
        if (existingCategory != null) {
            throw new IllegalArgumentException("Category with the name '" + category.getName() + "' already exists.");
        }

        // Save the image and set the image name in the category
        if (imageFile != null && !imageFile.isEmpty()) {
            String imageName = fileService.savePicture(imageFile); // Method to save the image
            category.setImageName(imageName);
        }

        Category savedCategory = categoryRepo.save(category);

        // Prepare the response DTO
        CategoryDto responseDto = modelMapper.map(savedCategory, CategoryDto.class);
        responseDto.setImageName(savedCategory.getImageName());
        responseDto.setImageUrl(baseUrl + "/api/v1/auth/picture/" + savedCategory.getImageName());

        return responseDto;
    }


    @Override
    public CategoryDto findByName(String name) {
        Category category = categoryRepo.findByName(name);
        if (category == null) {
            throw new ResourceNotFoundException("Category", "name", name);
        }
        CategoryDto categoryDto = modelMapper.map(category, CategoryDto.class);
        categoryDto.setImageName(category.getImageName());
        categoryDto.setImageUrl(baseUrl + "/api/v1/auth/picture/" + category.getImageName());
        return categoryDto;

    }

    @Override
    public CategoryDto getCategoryById(Long id) {
        Category category = categoryRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", id));
        CategoryDto categoryDto = modelMapper.map(category, CategoryDto.class);
        categoryDto.setImageName(category.getImageName());
        categoryDto.setImageUrl(baseUrl + "/api/v1/auth/picture/" + category.getImageName());
        return categoryDto;
    }

    @Override
    public List<CategoryDto> getAllCategories() {
        List<Category> categories = categoryRepo.findAll();
        return categories.stream()
                .map(category -> {
                    CategoryDto categoryDto = modelMapper.map(category, CategoryDto.class);
                    categoryDto.setImageName(category.getImageName());
                    categoryDto.setImageUrl(baseUrl + "/api/v1/auth/picture/" + category.getImageName());
                    return categoryDto;
                })
                .collect(Collectors.toList());
    }

    @Override
    public void deleteCategoryById(Long id) {
        Category category = categoryRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", id));
        categoryRepo.delete(category);


    }
}
