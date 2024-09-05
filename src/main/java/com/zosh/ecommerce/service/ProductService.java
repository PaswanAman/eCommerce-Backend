package com.zosh.ecommerce.service;

import com.zosh.ecommerce.Dto.ProductDto;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
public interface ProductService {

    ProductDto createProduct (ProductDto productDto, List<MultipartFile> image) throws IOException;
    ProductDto updateProduct (ProductDto productDto, Long productId) throws IOException;
    ProductDto getProductById(Long productId);
    void updateExpiredProduct();
    ProductDto updateProductAsSold(Long productId);
    List<ProductDto> getAllNonExpiredProducts();
    List<ProductDto> getProductsByCategoryName(String categoryName);
    List<ProductDto> searchProductsByTitle(String keyword);



}
