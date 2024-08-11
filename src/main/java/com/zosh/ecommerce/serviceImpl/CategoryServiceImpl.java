package com.zosh.ecommerce.serviceImpl;

import com.zosh.ecommerce.Dto.CategoryDto;
import com.zosh.ecommerce.Dto.ProductDto;
import com.zosh.ecommerce.entities.Category;
import com.zosh.ecommerce.entities.Product;
import com.zosh.ecommerce.exception.ResourceNotFoundException;
import com.zosh.ecommerce.repository.CategoryRepo;
import com.zosh.ecommerce.service.CategoryService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoryServiceImpl implements CategoryService {
    @Autowired
    private CategoryRepo categoryRepo;

    @Autowired
    private ModelMapper modelMapper;


    @Override
    public CategoryDto createCategory(CategoryDto categoryDto) {
        Category category = this.modelMapper.map(categoryDto,Category.class );
        try{
            Category addCategory = categoryRepo.save(category);
            CategoryDto categoryDtoResponse = modelMapper.map(addCategory, CategoryDto.class);

            return categoryDtoResponse;
        }catch (Exception e){
            System.out.println("Error creating Category: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    @Override
    public CategoryDto findByName(String name) {
        Category category = categoryRepo.findByName(name);
        CategoryDto categoryDto = modelMapper.map(category, CategoryDto.class);
        return categoryDto;
    }

    @Override
    public CategoryDto getCategoryById(Long id) {
        Category category = categoryRepo.findById(id).orElseThrow(()-> new ResourceNotFoundException("Product", "Id", id));
        CategoryDto categoryDto = this.modelMapper.map(category, CategoryDto.class);
        return categoryDto;
    }

    @Override
    public List<CategoryDto> getAllCategories() {
        return categoryRepo.findAll().stream().map(category -> modelMapper.map(category, CategoryDto.class)).collect(Collectors.toList());
    }

    @Override
    public void deleteCategoryById(Long id) {
        Category category = categoryRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Category", "Category id", id));
        this.categoryRepo.delete(category);

    }

}
