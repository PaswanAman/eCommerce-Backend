package com.zosh.ecommerce.repository;

import com.zosh.ecommerce.Dto.CategoryDto;
import com.zosh.ecommerce.entities.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepo extends JpaRepository<Category, Long> {
    Category findByName(String name);
}
