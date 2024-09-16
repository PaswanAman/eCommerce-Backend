package com.zosh.ecommerce.serviceImpl;

import com.zosh.ecommerce.Dto.CategoryDto;
import com.zosh.ecommerce.Dto.ProductDto;
import com.zosh.ecommerce.controller.UserController;
import com.zosh.ecommerce.entities.Category;
import com.zosh.ecommerce.entities.Product;
import com.zosh.ecommerce.entities.ProductImage;
import com.zosh.ecommerce.entities.User;
import com.zosh.ecommerce.exception.ResourceNotFoundException;
import com.zosh.ecommerce.repository.*;
import com.zosh.ecommerce.service.FileService;
import com.zosh.ecommerce.service.ProductService;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private ProductRepo productRepo;

    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private CartRepo cartRepo;

    @Autowired
    private ProductImageRepo productImageRepo;

    @Autowired
    private CategoryRepo categoryRepo;

    @Autowired
    private FileService fileService;

    @Value("${picture.base-url}")
    private String baseurl;

    private static final Logger logger = LoggerFactory.getLogger(ProductServiceImpl.class);

    @Override
    public ProductDto createProduct(ProductDto productDto, List<MultipartFile> images, Long sellerId) throws IOException {
        // Retrieve the seller from the UserRepository by sellerId
        User seller = userRepo.findById(sellerId)
                .orElseThrow(() -> new IllegalArgumentException("Seller not found with ID: " + sellerId));

        // Check if the user has the "SELLER" role
        if (!"ROLE_SELLER".equals(seller.getRole())) {
            throw new IllegalArgumentException("User is not a seller");
        }

        if (seller.getStore() == null) {
            throw new IllegalArgumentException("Seller does not have a store. Access denied.");
        }


        Product product = this.modelMapper.map(productDto, Product.class);
        product.setSold(false);
        product.setSeller(seller);

        Category category = categoryRepo.findByName(productDto.getCategoryName());
        product.setCategory(category);

        LocalDateTime currentDate = LocalDateTime.now();
        product.setAddedDate(currentDate);

        LocalDateTime expirationDate = currentDate.plus(30, ChronoUnit.DAYS);
        product.setExpirationDate(expirationDate);

        product.setHistoryStatus(false);
        product.setHiddenPost(false);


        product.setImages(new ArrayList<>());

        List<ProductImage> productImages = new ArrayList<>();
        List<String> imageUrls= new ArrayList<>();

        for (MultipartFile image : images){

            if(!isImage(image)){
                throw new IllegalArgumentException("Only image file are allowed");
            }

             String imageName = fileService.savePicture(image);
             String imageUrl = baseurl+"/api/v1/auth/picture/"+imageName;
             imageUrls.add(imageUrl);

            ProductImage productImage = new ProductImage();
            productImage.setProduct(product);
             productImage.setImage(imageName);
            productImages.add(productImage);


        }
        product.setImages(productImages);
        try{
            Product addProduct = productRepo.save(product);
            ProductDto productDtoResponse = modelMapper.map(addProduct, ProductDto.class);
            productDtoResponse.setImageUrls(imageUrls);
            System.out.println("Error creating productImage: " + imageUrls);

            return productDtoResponse;
        }catch (Exception e){
            System.out.println("Error creating productImage: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    private boolean isImage(MultipartFile images) {
        String contentType = images.getContentType();
        return contentType != null && (contentType.equals("image/jpeg") ||
                contentType.equals("image/png") ||
                contentType.equals("image/gif") ||
                contentType.equals("image/svg") ||
                contentType.equals("image/pjp") ||
                contentType.equals("image/pjpeg") ||
                contentType.equals("image/jfif") ||
                contentType.equals("image/webp") ||
                contentType.equals("image/jpg"));
    }

    @Override
    public ProductDto updateProduct(ProductDto productDto, Long productId) throws IOException {

         Product product = this.productRepo.findById(productId).orElseThrow();

         if (productDto.getTitle() != null){
             product.setTitle(productDto.getTitle());
         }
         if (productDto.getBrand() != null){
             product.setBrand(productDto.getBrand());
         }
         if (productDto.getPrice() != null){
             product.setPrice(productDto.getPrice());
         }

         Product updatedProduct = this.productRepo.save(product);
         return this.modelMapper.map(updatedProduct, ProductDto.class);
    }

    @Override
    public ProductDto getProductById(Long productId) {
        Product product = this.productRepo.findById(productId).orElseThrow(()-> new ResourceNotFoundException("Product", "Id", productId));
        ProductDto productDto = modelMapper.map(product, ProductDto.class);
        productDto.setSellerId(product.getSeller().getId());


        List<String> imageUrls= new ArrayList<>();
        for (ProductImage image : product.getImages()) {
            System.out.println("hello");
            imageUrls.add(image.getImage());
        }
        productDto.setImageUrls(imageUrls);

        return productDto;
    }

    @Override
    public void updateExpiredProduct() {
        LocalDateTime currentDate = LocalDateTime.now();
        List<Product> expiredProducts = productRepo.findByExpirationDateLessThanEqualAndHistoryStatusFalse(currentDate);
        expiredProducts.forEach(product -> {
            product.setHiddenPost(true);
            product.setHistoryStatus(true);
            productRepo.save(product);
        });

    }

    @Override
    public ProductDto updateProductAsSold(Long productId) {
        Product product = productRepo.findById(productId).orElseThrow(() -> new ResourceNotFoundException("Product","Product Id", productId ));
        product.setSold(true);
        Product updatedProduct = productRepo.save(product);

        return modelMapper.map(updatedProduct, ProductDto.class);
    }

    @Override
    public List<ProductDto> getAllNonExpiredProducts() {
        LocalDateTime currentDate = LocalDateTime.now();

        // Fetch all products
        List<Product> allProducts = productRepo.findAll();

        // Filter products that are not expired and handle null expiration dates
        List<ProductDto> nonExpiredProducts = allProducts.stream()
                .filter(product -> {
                    if (product.getExpirationDate() == null) {
                        logger.warn("Product with ID {} has a null expiration date.", product.getProductId());
                        return false; // Skip products with null expiration date
                    }
                    return product.getExpirationDate().isAfter(currentDate) ||
                            product.getExpirationDate().isEqual(currentDate);
                })
                .filter(product -> !product.getHistoryStatus())
                .map(product -> {
                    ProductDto productDto = modelMapper.map(product, ProductDto.class);

                    // Create image URLs map
                    List<String> imageUrls= new ArrayList<>();
                    for (ProductImage image : product.getImages()) {
                        imageUrls.add( baseurl+"/api/v1/auth/picture/" +image.getImage()); // Adjust key if needed
                    }
                    productDto.setImageUrls(imageUrls);

                    return productDto;
                })
                .collect(Collectors.toList());

        return nonExpiredProducts;
    }

    @Override
    public List<ProductDto> getProductsByCategoryName(String categoryName) {
        List<Product> products = productRepo.findByCategoryName(categoryName);
        return products.stream().map(product -> {
            ProductDto productDto = modelMapper.map(product, ProductDto.class);

            // Create image URLs map
            List<String> imageUrls= new ArrayList<>();
            for (ProductImage image : product.getImages()) {
                imageUrls.add( baseurl+"/api/v1/auth/picture/" +image.getImage()); // Adjust key if needed
            }
            productDto.setImageUrls(imageUrls);

            return productDto;
        }).collect(Collectors.toList());
    }

    @Override
    public List<ProductDto> searchProductsByTitle(String keyword) {
        List<Product> products = productRepo.findByTitleContainingIgnoreCase(keyword);
        return products.stream()
                .map(product -> modelMapper.map(product, ProductDto.class))
                .collect(Collectors.toList());
    }


    @Override
    public List<ProductDto> getProductsBySellerId(Long sellerId) {
        // Ensure only sellers' products are fetched
        List<Product> products = productRepo.findBySellerIdAndSellerRole(sellerId, "ROLE_SELLER");

        // Convert List<Product> to List<ProductDto>
        return products.stream()
                .map(product -> modelMapper.map(product, ProductDto.class))
                .toList();
    }


}
