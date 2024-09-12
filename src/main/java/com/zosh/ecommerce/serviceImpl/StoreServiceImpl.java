package com.zosh.ecommerce.serviceImpl;

import com.zosh.ecommerce.Dto.ProductDto;
import com.zosh.ecommerce.Dto.StoreDto;
import com.zosh.ecommerce.config.JwtTokenHelper;
import com.zosh.ecommerce.entities.Product;
import com.zosh.ecommerce.entities.ProductImage;
import com.zosh.ecommerce.entities.Store;
import com.zosh.ecommerce.entities.User;
import com.zosh.ecommerce.exception.InvalidImageException;
import com.zosh.ecommerce.exception.ResourceNotFoundException;
import com.zosh.ecommerce.exception.StoreCreationException;
import com.zosh.ecommerce.repository.StoreRepo;
import com.zosh.ecommerce.repository.UserRepo;
import com.zosh.ecommerce.service.FileService;
import com.zosh.ecommerce.service.StoreService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Service
public class StoreServiceImpl implements StoreService {
    @Autowired
    private StoreRepo storeRepo;
    @Autowired
    private UserRepo userRepo;
    @Autowired
    private JwtTokenHelper jwtTokenHelper;
    @Autowired
    private FileService fileService;
    @Autowired
    private ModelMapper modelMapper;
    @Value("${picture.base-url}")
    private String baseurl;

    private static final List<String> ALLOWED_IMAGE_TYPES = Arrays.asList("image/png", "image/jpeg", "image/jpg");

    @Override
    public StoreDto createStore(StoreDto storeDto, String userToken, List<MultipartFile> images) throws IOException {
        Store store = this.modelMapper.map(storeDto, Store.class);
        String token = userToken.replaceAll("Bearer", " ").trim();
        String username = jwtTokenHelper.getUsernameFromToken(token);
        User seller = this.userRepo.findByEmail(username).orElseThrow(() -> new ResourceNotFoundException("Seller", "email", username));

        store.setSeller(seller);

        List<String> imageUrls= new ArrayList<>();
        List<String> imageNames = new ArrayList<>();
        for (MultipartFile image : images){

            if(!isImage(image)){
                throw new IllegalArgumentException("Only image file are allowed");
            }

            String imageName = fileService.savePicture(image);
            String imageUrl = baseurl+"/api/v1/auth/picture/"+imageName;
            imageUrls.add(imageUrl);
            imageNames.add(imageName);

        }
        store.setStoreImages(imageNames);



        try{
            Store addStore = storeRepo.save(store);
            StoreDto storeDto1 = modelMapper.map(addStore, StoreDto.class);
            storeDto1.setStoreImageUrls(imageUrls);
            storeDto1.setStoreImageName(imageNames);
            System.out.println("Error creating storeImage: " + imageUrls);

            return storeDto1;
        }catch (Exception e){
            System.out.println("Error creating storeImage: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }


    @Override
    public StoreDto storeGetById(Long storeId) {
        try {
            Store store = storeRepo.findById(storeId)
                    .orElseThrow(() -> new ResourceNotFoundException("Store", "id", storeId));

            store.getStoreImages().size();
            StoreDto storeDto = modelMapper.map(store, StoreDto.class);

            List<String> imageUrls = new ArrayList<>();
            List<String> imageNames = store.getStoreImages();

            for (String imageName : imageNames) {
                String imageUrl = baseurl + "/api/v1/auth/picture/" + imageName;
                imageUrls.add(imageUrl);
            }
            storeDto.setStoreImageUrls(imageUrls);
            storeDto.setStoreImageName(imageNames);

            return storeDto;
        } catch (ResourceNotFoundException ex) {
            // Log exception and rethrow it to be handled by the controller or global handler
            throw new ResourceNotFoundException("Store", "id", storeId);
        } catch (Exception ex) {
            // Log any other exceptions
            ex.printStackTrace();
            throw new RuntimeException("An error occurred while fetching the store details.");
        }

    }

    @Override
    public List<StoreDto> getAllStores() {
        try {
            List<Store> stores = storeRepo.findAll();
            List<StoreDto> storeDtos = new ArrayList<>();

            for (Store store : stores) {
                StoreDto storeDto = modelMapper.map(store, StoreDto.class);

                List<String> imageUrls = new ArrayList<>();
                List<String> imageNames = store.getStoreImages(); // Assuming it's a List<String>

                // Check if imageNames is not null and not empty
                if (imageNames != null && !imageNames.isEmpty()) {
                    for (String imageName : imageNames) {
                        String imageUrl = baseurl + "/api/v1/auth/picture/" + imageName;
                        imageUrls.add(imageUrl);
                    }
                }

                // Set image names and URLs in the DTO
                storeDto.setStoreImageUrls(imageUrls);
                storeDto.setStoreImageName(imageNames);

                storeDtos.add(storeDto);
            }

            return storeDtos;
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException("An error occurred while fetching all stores.");
        }
    }

    @Override
    public StoreDto getStoreBySellerId(Long sellerId) {
        try {
            User seller = userRepo.findById(sellerId)
                    .orElseThrow(() -> new ResourceNotFoundException("Seller", "id", sellerId));

            Store store = storeRepo.findBySeller(seller)
                    .orElseThrow(() -> new ResourceNotFoundException("Store", "sellerId", sellerId));

            StoreDto storeDto = modelMapper.map(store, StoreDto.class);
            List<String> imageUrls = new ArrayList<>();

            if (store.getStoreImages() != null) {
                for (String imageName : store.getStoreImages()) {
                    String imageUrl = baseurl + "/api/v1/auth/picture/" + imageName;
                    imageUrls.add(imageUrl);
                }
            }

            storeDto.setStoreImageUrls(imageUrls);
            storeDto.setStoreImageName(store.getStoreImages());
            return storeDto;

        } catch (Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException("An error occurred while fetching the store by seller ID.");
        }
    }


    private boolean isImage(MultipartFile images) {
        String contentType = images.getContentType();
        return contentType != null && (contentType.equals("image/jpeg") ||
                contentType.equals("image/png") ||
                contentType.equals("image/gif") ||
                contentType.equals("image/jpg"));
    }


}


